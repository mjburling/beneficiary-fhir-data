package gov.cms.bfd.pipeline.rda.grpc.sink;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.zaxxer.hikari.HikariDataSource;
import gov.cms.bfd.model.rda.PreAdjFissClaim;
import gov.cms.bfd.pipeline.rda.grpc.ProcessingException;
import gov.cms.bfd.pipeline.rda.grpc.RdaSink;
import gov.cms.bfd.pipeline.sharedutils.DatabaseOptions;
import gov.cms.bfd.pipeline.sharedutils.DatabaseUtils;
import java.util.Collection;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** RdaSink implementation that writes PreAdjFissClaim objects to the database in batches. */
public class FissClaimRdaSink implements RdaSink<PreAdjFissClaim> {
  private static final Logger LOGGER = LoggerFactory.getLogger(FissClaimRdaSink.class);
  public static final String CALLS_METER_NAME =
      MetricRegistry.name(FissClaimRdaSink.class.getSimpleName(), "calls");
  public static final String FAILURES_METER_NAME =
      MetricRegistry.name(FissClaimRdaSink.class.getSimpleName(), "failures");
  public static final String PERSISTS_METER_NAME =
      MetricRegistry.name(FissClaimRdaSink.class.getSimpleName(), "persists");
  public static final String MERGES_METER_NAME =
      MetricRegistry.name(FissClaimRdaSink.class.getSimpleName(), "merges");

  private final HikariDataSource dataSource;
  private final EntityManagerFactory entityManagerFactory;
  private final EntityManager entityManager;
  private final Meter callsMeter;
  private final Meter failuresMeter;
  private final Meter persistsMeter;
  private final Meter mergesMeter;

  public FissClaimRdaSink(DatabaseOptions databaseOptions, MetricRegistry metricRegistry) {
    dataSource = DatabaseUtils.createDataSource(databaseOptions, metricRegistry, 10);
    entityManagerFactory = DatabaseUtils.createEntityManagerFactory(dataSource);
    entityManager = entityManagerFactory.createEntityManager();
    callsMeter = metricRegistry.meter(CALLS_METER_NAME);
    failuresMeter = metricRegistry.meter(FAILURES_METER_NAME);
    persistsMeter = metricRegistry.meter(PERSISTS_METER_NAME);
    mergesMeter = metricRegistry.meter(MERGES_METER_NAME);
  }

  @Override
  public void close() throws Exception {
    entityManager.close();
    entityManagerFactory.close();
    dataSource.close();
  }

  /**
   * Writes the entire batch to the database in a single transaction.
   *
   * @param claims batch of claims to be written to the database
   * @return the number of claims successfully written to the database
   * @throws ProcessingException wrapped exception if an error takes place
   */
  @Override
  public int writeBatch(Collection<PreAdjFissClaim> claims) throws ProcessingException {
    try {
      try {
        persistBatch(claims);
        persistsMeter.mark(claims.size());
        LOGGER.info("wrote batch of {} claims using persist()", claims.size());
      } catch (Throwable error) {
        if (isDuplicateKeyException(error)) {
          LOGGER.info(
              "caught duplicate key exception: switching to merge for batch of {} claims",
              claims.size());
          mergeBatch(claims);
          mergesMeter.mark(claims.size());
          LOGGER.info("wrote batch of {} claims using merge()", claims.size());
        } else {
          throw error;
        }
        callsMeter.mark();
      }
    } catch (Exception error) {
      LOGGER.error("writeBatch failure: error={}", error.getMessage(), error);
      failuresMeter.mark();
      throw new ProcessingException(error, 0);
    }
    return claims.size();
  }

  private void persistBatch(Iterable<PreAdjFissClaim> claims) {
    boolean commit = false;
    try {
      entityManager.getTransaction().begin();
      for (PreAdjFissClaim claim : claims) {
        entityManager.persist(claim);
      }
      commit = true;
    } finally {
      if (commit) {
        entityManager.getTransaction().commit();
      } else {
        entityManager.getTransaction().rollback();
      }
    }
  }

  private void mergeBatch(Iterable<PreAdjFissClaim> claims) {
    entityManager.getTransaction().begin();
    for (PreAdjFissClaim claim : claims) {
      entityManager.merge(claim);
    }
    entityManager.getTransaction().commit();
  }

  private static boolean isDuplicateKeyException(Throwable error) {
    while (error != null) {
      if (error instanceof EntityExistsException) {
        return true;
      }
      if (error.getMessage().contains("already exists")
          || error.getMessage().contains("duplicate key")) {
        return true;
      }
      error = error.getCause() == error ? null : error.getCause();
    }
    return false;
  }
}
