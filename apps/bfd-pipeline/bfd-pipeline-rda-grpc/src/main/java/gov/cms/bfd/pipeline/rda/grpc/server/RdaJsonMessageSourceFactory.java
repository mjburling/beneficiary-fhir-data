package gov.cms.bfd.pipeline.rda.grpc.server;

import com.google.common.io.ByteSource;
import gov.cms.mpsm.rda.v1.FissClaimChange;
import gov.cms.mpsm.rda.v1.McsClaimChange;
import lombok.AllArgsConstructor;

/**
 * Implementation of {@link RdaMessageSourceFactory} that uses predefined value for version and
 * reads claims from predefined {@link ByteSource}s containing NDJSON data.
 */
@AllArgsConstructor
public class RdaJsonMessageSourceFactory implements RdaMessageSourceFactory {
  /** The version returned by {@link RdaService#getVersion}. */
  private final RdaService.Version version;
  /** JSON data containing FISS claims. */
  private final ByteSource fissJson;
  /** JSON data containing MCS claims. */
  private final ByteSource mcsJson;

  @Override
  public RdaService.Version getVersion() {
    return version;
  }

  @Override
  public MessageSource<FissClaimChange> createFissMessageSource(long startingSequenceNumber) {
    return new JsonMessageSource<>(fissJson, JsonMessageSource::parseFissClaimChange)
        .filter(change -> change.getSeq() >= startingSequenceNumber);
  }

  @Override
  public MessageSource<McsClaimChange> createMcsMessageSource(long startingSequenceNumber) {
    return new JsonMessageSource<>(mcsJson, JsonMessageSource::parseMcsClaimChange)
        .filter(change -> change.getSeq() >= startingSequenceNumber);
  }

  @Override
  public void close() throws Exception {}
}
