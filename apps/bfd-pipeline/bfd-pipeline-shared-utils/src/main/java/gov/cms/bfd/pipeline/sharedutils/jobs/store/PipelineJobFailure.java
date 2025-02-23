package gov.cms.bfd.pipeline.sharedutils.jobs.store;

import gov.cms.bfd.pipeline.sharedutils.PipelineJob;

/** Represents the outcome of a failed {@link PipelineJob} execution. */
public final class PipelineJobFailure {
  /** Holds the exception that caused the failure. */
  private final Throwable exception;

  /**
   * Constructs a new {@link PipelineJobFailure} instance.
   *
   * @param exception the {@link Throwable} that the {@link PipelineJob} execution produced, which
   *     will be used for {@link #getType()} and {@link #getMessage()}
   */
  public PipelineJobFailure(Throwable exception) {
    this.exception = exception;
  }

  /**
   * Gets the {@link #exception} class.
   *
   * @return the {@link Throwable#getClass()} of the exception that the {@link PipelineJob}
   *     execution produced
   */
  public Class<? extends Throwable> getType() {
    return exception.getClass();
  }

  /**
   * Gets the {@link #exception} message.
   *
   * @return the {@link Throwable#getMessage()} of the exception that the {@link PipelineJob}
   *     execution produced
   */
  public String getMessage() {
    return exception.getMessage();
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("PipelineJobFailure [getType()=");
    builder.append(getType());
    builder.append(", getMessage()=");
    builder.append(getMessage());
    builder.append("]");
    return builder.toString();
  }
}
