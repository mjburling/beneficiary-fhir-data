package gov.cms.bfd.pipeline.rda.grpc;

import com.codahale.metrics.MetricRegistry;
import gov.cms.bfd.model.rda.PreAdjMcsClaim;
import java.util.concurrent.Callable;

/**
 * PipelineJob requires that the class of the job be used to define the PipelineJobType. This class
 * is a simple wrapper to ensure that PreAdjMcsClaim processing has a unique PipelineJobType value
 * based on its class.
 */
public class RdaMcsClaimLoadJob extends AbstractRdaLoadJob<RdaChange<PreAdjMcsClaim>> {
  public RdaMcsClaimLoadJob(
      Config config,
      Callable<RdaSource<RdaChange<PreAdjMcsClaim>>> sourceFactory,
      Callable<RdaSink<RdaChange<PreAdjMcsClaim>>> sinkFactory,
      MetricRegistry appMetrics) {
    super(config, sourceFactory, sinkFactory, appMetrics);
  }
}