package gov.cms.bfd.model.rif;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.*;

/** JPA class for the LoadedBatches table */
@Entity
@Table(name = "loaded_batches")
public class LoadedBatch {
  public static final String SEPARATOR = ",";

  @Id
  @Column(name = "loaded_batchid", nullable = false)
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "loaded_batches_loaded_batchid_seq")
  @SequenceGenerator(
      name = "loaded_batches_loaded_batchid_seq",
      sequenceName = "loaded_batches_loaded_batchid_seq",
      allocationSize = 20)
  private long loadedBatchId;

  @Column(name = "loaded_fileid", nullable = false)
  private long loadedFileId;

  @Column(name = "beneficiaries", columnDefinition = "varchar", nullable = false)
  private String beneficiaries;

  @Column(name = "created", nullable = false)
  private Instant created;

  /** default constructor */
  public LoadedBatch() {}

  /**
   * Create with known values
   *
   * @param loadedBatchId unique sequence id
   * @param loadedFileId associated file
   * @param beneficiaries to associate
   * @param created batch creation date
   */
  public LoadedBatch(long loadedBatchId, long loadedFileId, String beneficiaries, Instant created) {
    this();
    this.loadedBatchId = loadedBatchId;
    this.loadedFileId = loadedFileId;
    this.beneficiaries = beneficiaries;
    this.created = created;
  }

  /**
   * Create with known values
   *
   * @param loadedBatchId unique sequence id
   * @param loadedFileId associated file
   * @param beneficiaries to associate
   * @param created batch creation date
   */
  public LoadedBatch(
      long loadedBatchId, long loadedFileId, List<Long> beneficiaries, Instant created) {
    this();
    this.loadedBatchId = loadedBatchId;
    this.loadedFileId = loadedFileId;
    this.beneficiaries = convertToString(beneficiaries);
    this.created = created;
  }

  /** @return the loadedBatchId */
  public long getLoadedBatchId() {
    return loadedBatchId;
  }

  /** @param loadedBatchId the identifier to set */
  public void setLoadedBatchId(long loadedBatchId) {
    this.loadedBatchId = loadedBatchId;
  }

  /** @return the loadedFileId */
  public long getLoadedFileId() {
    return loadedFileId;
  }

  /** @param loadedFileId the identifier to set */
  public void setLoadedFileId(long loadedFileId) {
    this.loadedFileId = loadedFileId;
  }

  /** @return the creation time stamp */
  public Instant getCreated() {
    return created;
  }

  /** @param created time stamp to set */
  public void setCreated(Instant created) {
    this.created = created;
  }

  /** @param beneficiaries the beneficiaryId to set */
  public void setBeneficiaries(long beneficiaryId) {
    this.beneficiaries = String.valueOf(beneficiaryId);
  }

  /** @param beneficiaries the beneficiaryId to set */
  public void setBeneficiaries(String beneficiaryId) {
    this.beneficiaries = beneficiaryId;
  }

  /**
   * Set the beneficiaries from a list
   *
   * @param beneficiaries list to convert
   */
  public void setBeneficiariesLong(List<Long> beneficiaries) {
    this.beneficiaries = convertToString(beneficiaries);
  }

  /**
   * Set the beneficiaries from a list
   *
   * @param beneficiaries list to convert
   */
  public void setBeneficiaries(List<String> beneficiaries) {
    this.beneficiaries =
        (beneficiaries == null || beneficiaries.isEmpty())
            ? ""
            : beneficiaries.stream().collect(Collectors.joining(SEPARATOR));
  }

  /**
   * Get the beneficiaries as a list
   *
   * @return beneficiaries as list
   */
  public List<Long> getBeneficiaries() {
    return convertToList(this.beneficiaries);
  }

  /**
   * Utility function to combine to batch into a larger batch. Useful for small number of batches.
   *
   * @param a batch
   * @param b batch
   * @return batch which has id of a, beneficiaries of both, and the latest created
   */
  public static LoadedBatch combine(LoadedBatch a, LoadedBatch b) {
    if (a == null) return b;
    if (b == null) return a;
    LoadedBatch sum = new LoadedBatch();
    sum.loadedBatchId = a.loadedBatchId;
    sum.loadedFileId = a.loadedFileId;
    sum.beneficiaries =
        a.beneficiaries.isEmpty()
            ? b.beneficiaries
            : b.beneficiaries.isEmpty()
                ? a.beneficiaries
                : a.beneficiaries + SEPARATOR + b.beneficiaries;
    sum.created = (a.created.isAfter(b.created)) ? a.created : b.created;
    return sum;
  }

  /*
   * Dev Note: A JPA AttributeConverter could be created instead of these static methods. This is
   * slightly simpler and, since conversion is done once, just as efficient.
   */
  private static String convertToString(List<Long> list) {
    return list == null || list.isEmpty()
        ? ""
        : list.stream().map(String::valueOf).collect(Collectors.joining(SEPARATOR));
  }

  private static List<Long> convertToList(String commaSeparated) {
    return commaSeparated == null || commaSeparated.isEmpty()
        ? new ArrayList<>()
        : Arrays.stream(commaSeparated.split(SEPARATOR, -1))
            .map(Long::parseLong)
            .collect(Collectors.toList());
  }
}
