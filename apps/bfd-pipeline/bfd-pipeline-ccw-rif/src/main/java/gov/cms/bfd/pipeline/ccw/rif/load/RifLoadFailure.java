package gov.cms.bfd.pipeline.ccw.rif.load;

import gov.cms.bfd.model.rif.RifRecordEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * This unchecked {@link RuntimeException} is used to represent that one or more {@link
 * RifRecordEvent}s failed to load, when pushed to a FHIR server via {@link RifLoader}.
 */
public final class RifLoadFailure extends RuntimeException {
  private static final long serialVersionUID = 5268467019558996698L;

  /** Setting for whether to log the full record data when a record fails to load. */
  private static final boolean LOG_SOURCE_DATA = false;

  /** The failed record events. */
  private final List<RifRecordEvent<?>> failedRecordEvents;

  /**
   * Constructs a new {@link RifLoadFailure} instance, for a specific {@link RifRecordEvent}
   * failure.
   *
   * @param failedRecordEvent the value to use for {@link #getFailedRecordEvents()}
   * @param cause the {@link Throwable} that was encountered, when the {@link RifRecordEvent} failed
   *     to load
   */
  public RifLoadFailure(RifRecordEvent<?> failedRecordEvent, Throwable cause) {
    super(buildMessage(failedRecordEvent), cause);
    this.failedRecordEvents = Arrays.asList(failedRecordEvent);
  }

  /**
   * Constructs a new {@link RifLoadFailure} instance, for a more general failure to load one of
   * more {@link RifRecordEvent}s.
   *
   * @param cause the {@link Throwable} that was encountered, when the {@link RifRecordEvent}(s)
   *     failed to load
   */
  public RifLoadFailure(Throwable cause) {
    super(cause);
    this.failedRecordEvents = null;
  }

  /**
   * Constructs a new {@link RifLoadFailure} instance, for a failure in processing a group of {@link
   * RifRecordEvent}s.
   *
   * @param failedRecordEvents the value to use for {@link #getFailedRecordEvents()}
   * @param cause the {@link Throwable} that was encountered, when the {@link RifRecordEvent} failed
   *     to load
   */
  public RifLoadFailure(List<RifRecordEvent<?>> failedRecordEvents, Throwable cause) {
    super(buildMessage(failedRecordEvents), cause);
    this.failedRecordEvents = failedRecordEvents;
  }

  /**
   * Builds a message for the records that failed to load.
   *
   * @param failedRecordEvent the failed records
   * @return the value to use for {@link #getMessage()}
   */
  private static String buildMessage(RifRecordEvent<?> failedRecordEvent) {
    if (LOG_SOURCE_DATA)
      return String.format(
          "Failed to load a '%s' record: '%s'.",
          failedRecordEvent.getFileEvent().getFile().getFileType().name(),
          failedRecordEvent.toString());
    else
      return String.format(
          "Failed to load a '%s' record.",
          failedRecordEvent.getFileEvent().getFile().getFileType().name());
  }

  /**
   * Builds a message for the records that failed to load.
   *
   * @param failedRecordEvents the failed record events
   * @return the value to use for {@link #getMessage()}
   */
  private static String buildMessage(List<RifRecordEvent<?>> failedRecordEvents) {
    if (LOG_SOURCE_DATA)
      return String.format(
          "Failed to load '%s' records: '%s'.",
          failedRecordEvents.get(0).getFileEvent().getFile().getFileType().name(),
          failedRecordEvents.toString());
    else
      return String.format(
          "Failed to load '%s' records.",
          failedRecordEvents.get(0).getFileEvent().getFile().getFileType().name());
  }

  /**
   * Gets the failed record events, if any.
   *
   * @return the {@link RifRecordEvent} that failed to load, or an empty {@link Optional} if no
   *     records
   */
  public Optional<List<RifRecordEvent<?>>> getFailedRecordEvents() {
    return Optional.ofNullable(failedRecordEvents);
  }
}
