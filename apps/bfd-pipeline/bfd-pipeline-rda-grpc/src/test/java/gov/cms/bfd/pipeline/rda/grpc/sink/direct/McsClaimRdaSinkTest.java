package gov.cms.bfd.pipeline.rda.grpc.sink.direct;

import static gov.cms.bfd.pipeline.rda.grpc.RdaPipelineTestUtils.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.ImmutableList;
import com.zaxxer.hikari.HikariDataSource;
import gov.cms.bfd.model.rda.PreAdjMcsClaim;
import gov.cms.bfd.pipeline.rda.grpc.ProcessingException;
import gov.cms.bfd.pipeline.rda.grpc.RdaChange;
import gov.cms.bfd.pipeline.rda.grpc.source.McsClaimTransformer;
import gov.cms.bfd.pipeline.sharedutils.IdHasher;
import gov.cms.bfd.pipeline.sharedutils.PipelineApplicationState;
import gov.cms.mpsm.rda.v1.McsClaimChange;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class McsClaimRdaSinkTest {
  private static final String VERSION = "version";

  private final Clock clock = Clock.fixed(Instant.ofEpochMilli(60_000L), ZoneOffset.UTC);
  private final IdHasher.Config hasherConfig = new IdHasher.Config(1, "notarealpepper");

  @Mock private HikariDataSource dataSource;
  @Mock private EntityManagerFactory entityManagerFactory;
  @Mock private EntityManager entityManager;
  @Mock private EntityTransaction transaction;
  @Mock private McsClaimTransformer transformer;
  private MetricRegistry appMetrics;
  private McsClaimRdaSink sink;
  private long nextSeq = 0L;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    appMetrics = new MetricRegistry();
    doReturn(entityManager).when(entityManagerFactory).createEntityManager();
    doReturn(transaction).when(entityManager).getTransaction();
    doReturn(MbiCache.computedCache(hasherConfig)).when(transformer).getMbiCache();
    doReturn(transformer).when(transformer).withMbiCache(any());
    doReturn(true).when(entityManager).isOpen();
    PipelineApplicationState appState =
        new PipelineApplicationState(appMetrics, dataSource, entityManagerFactory, clock);
    sink = new McsClaimRdaSink(appState, transformer, true);
    nextSeq = 0L;
  }

  @Test
  public void metricNames() {
    assertEquals(
        Arrays.asList(
            "McsClaimRdaSink.calls",
            "McsClaimRdaSink.change.latency.millis",
            "McsClaimRdaSink.failures",
            "McsClaimRdaSink.lastSeq",
            "McsClaimRdaSink.successes",
            "McsClaimRdaSink.writes.merged",
            "McsClaimRdaSink.writes.persisted",
            "McsClaimRdaSink.writes.total"),
        new ArrayList<>(appMetrics.getNames()));
  }

  @Test
  public void mergeSuccessful() throws Exception {
    final List<RdaChange<PreAdjMcsClaim>> batch =
        ImmutableList.of(createClaim("1"), createClaim("2"), createClaim("3"));

    final int count = sink.writeMessages(VERSION, messagesForBatch(batch));
    assertEquals(3, count);

    for (RdaChange<PreAdjMcsClaim> change : batch) {
      PreAdjMcsClaim claim = change.getClaim();
      verify(entityManager).merge(claim);
    }
    // the merge transaction will be committed
    verify(transaction).commit();

    final AbstractClaimRdaSink.Metrics metrics = sink.getMetrics();
    assertMeterReading(1, "calls", metrics.getCalls());
    assertMeterReading(0, "persists", metrics.getObjectsPersisted());
    assertMeterReading(3, "merges", metrics.getObjectsMerged());
    assertMeterReading(3, "writes", metrics.getObjectsWritten());
    assertMeterReading(1, "successes", metrics.getSuccesses());
    assertMeterReading(0, "failures", metrics.getFailures());
    assertGaugeReading(2, "lastSeq", metrics.getLatestSequenceNumber());
  }

  @Test
  public void mergeFatalError() {
    final List<RdaChange<PreAdjMcsClaim>> batch =
        ImmutableList.of(createClaim("1"), createClaim("2"), createClaim("3"));
    doThrow(new RuntimeException("oops")).when(entityManager).merge(batch.get(1).getClaim());

    try {
      sink.writeMessages(VERSION, messagesForBatch(batch));
      fail("should have thrown");
    } catch (ProcessingException error) {
      assertEquals(0, error.getProcessedCount());
      assertThat(error.getCause(), CoreMatchers.instanceOf(RuntimeException.class));
    }

    verify(entityManager).merge(batch.get(0).getClaim());
    verify(entityManager).merge(batch.get(1).getClaim());
    verify(entityManager, times(0)).merge(batch.get(2).getClaim()); // not called once a merge fails
    verify(transaction).rollback();

    final AbstractClaimRdaSink.Metrics metrics = sink.getMetrics();
    assertMeterReading(1, "calls", metrics.getCalls());
    assertMeterReading(0, "persists", metrics.getObjectsPersisted());
    assertMeterReading(0, "merges", metrics.getObjectsMerged());
    assertMeterReading(0, "writes", metrics.getObjectsWritten());
    assertMeterReading(0, "successes", metrics.getSuccesses());
    assertMeterReading(1, "failures", metrics.getFailures());
    assertGaugeReading(0, "lastSeq", metrics.getLatestSequenceNumber());
  }

  @Test
  public void closeMethodsAreCalled() throws Exception {
    sink.close();
    verify(entityManager).close();
  }

  private List<McsClaimChange> messagesForBatch(List<RdaChange<PreAdjMcsClaim>> batch) {
    final var messages = ImmutableList.<McsClaimChange>builder();
    for (RdaChange<PreAdjMcsClaim> change : batch) {
      var message =
          McsClaimChange.newBuilder()
              .setIcn(change.getClaim().getIdrClmHdIcn())
              .setSeq(change.getSequenceNumber())
              .build();
      doReturn(change).when(transformer).transformClaim(message);
      messages.add(message);
    }
    return messages.build();
  }

  private RdaChange<PreAdjMcsClaim> createClaim(String dcn) {
    PreAdjMcsClaim claim = new PreAdjMcsClaim();
    claim.setIdrClmHdIcn(dcn);
    claim.setApiSource(VERSION);
    return new RdaChange<>(
        nextSeq++, RdaChange.Type.INSERT, claim, clock.instant().minusMillis(12));
  }
}
