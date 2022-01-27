package gov.cms.bfd.model.rif.samples;

import gov.cms.bfd.model.rif.RifFile;
import gov.cms.bfd.model.rif.RifFileType;
import gov.cms.bfd.model.rif.parse.RifParsingUtils;
import gov.cms.bfd.sharedutils.exceptions.BadCodeMonkeyException;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import org.apache.commons.csv.CSVParser;

/** Enumerates the sample RIF resources available on the classpath. */
public enum StaticRifResource {
  SAMPLE_A_BENES(
      resourceUrl("rif-static-samples/sample-a-beneficiaries.txt"),
      RifFileType.BENEFICIARY,
      Optional.of(1)),

  SAMPLE_A_BENES_WITH_BACKSLASH(
      resourceUrl("rif-static-samples/sample-a-beneficiaries-with-backslash.txt"),
      RifFileType.BENEFICIARY,
      Optional.of(1)),

  SAMPLE_A_BENEFICIARY_HISTORY(
      resourceUrl("rif-static-samples/sample-a-beneficiaryhistory.txt"),
      RifFileType.BENEFICIARY_HISTORY,
      Optional.of(3)),

  SAMPLE_A_MEDICARE_BENEFICIARY_ID_HISTORY(
      resourceUrl("rif-static-samples/sample-a-medicarebeneficiaryidhistory.txt"),
      RifFileType.MEDICARE_BENEFICIARY_ID_HISTORY,
      Optional.of(1)),

  SAMPLE_A_MEDICARE_BENEFICIARY_ID_HISTORY_EXTRA(
      resourceUrl("rif-static-samples/sample-a-medicarebeneficiaryidhistory-extra.txt"),
      RifFileType.MEDICARE_BENEFICIARY_ID_HISTORY,
      Optional.of(1)),

  SAMPLE_A_CARRIER(
      resourceUrl("rif-static-samples/sample-a-bcarrier.txt"), RifFileType.CARRIER, Optional.of(1)),

  SAMPLE_A_INPATIENT(
      resourceUrl("rif-static-samples/sample-a-inpatient.txt"),
      RifFileType.INPATIENT,
      Optional.of(1)),

  SAMPLE_A_OUTPATIENT(
      resourceUrl("rif-static-samples/sample-a-outpatient.txt"),
      RifFileType.OUTPATIENT,
      Optional.of(1)),

  SAMPLE_A_SNF(resourceUrl("rif-static-samples/sample-a-snf.txt"), RifFileType.SNF, Optional.of(1)),

  SAMPLE_A_HOSPICE(
      resourceUrl("rif-static-samples/sample-a-hospice.txt"), RifFileType.HOSPICE, Optional.of(1)),

  SAMPLE_A_HHA(resourceUrl("rif-static-samples/sample-a-hha.txt"), RifFileType.HHA, Optional.of(1)),

  SAMPLE_A_DME(resourceUrl("rif-static-samples/sample-a-dme.txt"), RifFileType.DME, Optional.of(1)),

  SAMPLE_A_PDE(resourceUrl("rif-static-samples/sample-a-pde.txt"), RifFileType.PDE, Optional.of(1)),

  SAMPLE_B_BENES(
      localCopyOfS3Data(TestDataSetLocation.SAMPLE_B_LOCATION, "beneficiary_test.rif"),
      RifFileType.BENEFICIARY,
      Optional.of(100)),

  SAMPLE_B_CARRIER(
      localCopyOfS3Data(TestDataSetLocation.SAMPLE_B_LOCATION, "carrier_test.rif"),
      RifFileType.CARRIER,
      Optional.of(4378)),

  SAMPLE_B_INPATIENT(
      localCopyOfS3Data(TestDataSetLocation.SAMPLE_B_LOCATION, "inpatient_test.rif"),
      RifFileType.INPATIENT,
      Optional.of(49)),

  SAMPLE_B_OUTPATIENT(
      localCopyOfS3Data(TestDataSetLocation.SAMPLE_B_LOCATION, "outpatient_test.rif"),
      RifFileType.OUTPATIENT,
      Optional.of(829)),

  SAMPLE_B_SNF(
      localCopyOfS3Data(TestDataSetLocation.SAMPLE_B_LOCATION, "snf_test.rif"),
      RifFileType.SNF,
      Optional.of(14)),

  SAMPLE_B_HOSPICE(
      localCopyOfS3Data(TestDataSetLocation.SAMPLE_B_LOCATION, "hospice_test.rif"),
      RifFileType.HOSPICE,
      Optional.of(10)),

  SAMPLE_B_HHA(
      localCopyOfS3Data(TestDataSetLocation.SAMPLE_B_LOCATION, "hha_test.rif"),
      RifFileType.HHA,
      Optional.of(51)),

  SAMPLE_B_DME(
      localCopyOfS3Data(TestDataSetLocation.SAMPLE_B_LOCATION, "dme_test.rif"),
      RifFileType.DME,
      Optional.of(248)),

  SAMPLE_B_PDE(
      localCopyOfS3Data(TestDataSetLocation.SAMPLE_B_LOCATION, "pde_test.rif"),
      RifFileType.PDE,
      Optional.of(5714)),

  SAMPLE_C_BENES(
      remoteS3Data(TestDataSetLocation.SAMPLE_C_LOCATION, "beneficiary_test.rif"),
      RifFileType.BENEFICIARY,
      Optional.of(1000000)),

  SAMPLE_C_CARRIER(
      remoteS3Data(TestDataSetLocation.SAMPLE_C_LOCATION, "carrier_test.rif"),
      RifFileType.CARRIER,
      Optional.of(32943217)),

  SAMPLE_C_DME(
      remoteS3Data(TestDataSetLocation.SAMPLE_C_LOCATION, "dme_test.rif"),
      RifFileType.DME,
      Optional.of(2320363)),

  SAMPLE_C_HHA(
      remoteS3Data(TestDataSetLocation.SAMPLE_C_LOCATION, "hha_test.rif"),
      RifFileType.HHA,
      Optional.of(228623)),

  SAMPLE_C_HOSPICE(
      remoteS3Data(TestDataSetLocation.SAMPLE_C_LOCATION, "hospice_test.rif"),
      RifFileType.HOSPICE,
      Optional.of(106462)),

  SAMPLE_C_INPATIENT(
      remoteS3Data(TestDataSetLocation.SAMPLE_C_LOCATION, "inpatient_test.rif"),
      RifFileType.INPATIENT,
      Optional.of(384616)),

  SAMPLE_C_OUTPATIENT(
      remoteS3Data(TestDataSetLocation.SAMPLE_C_LOCATION, "outpatient_test.rif"),
      RifFileType.OUTPATIENT,
      Optional.of(6195549)),

  SAMPLE_C_PDE(
      remoteS3Data(TestDataSetLocation.SAMPLE_C_LOCATION, "pde_test.rif"),
      RifFileType.PDE,
      Optional.of(67566673)),

  SAMPLE_C_SNF(
      remoteS3Data(TestDataSetLocation.SAMPLE_C_LOCATION, "snf_test.rif"),
      RifFileType.SNF,
      Optional.of(169175)),

  SAMPLE_U_BENES(
      resourceUrl("rif-static-samples/sample-u-beneficiaries.txt"),
      RifFileType.BENEFICIARY,
      Optional.of(1)),

  SAMPLE_U_BENES_CHANGED_WITH_8_MONTHS(
      resourceUrl("rif-static-samples/sample-u-with-8-months.txt"),
      RifFileType.BENEFICIARY,
      Optional.of(1)),

  SAMPLE_U_BENES_CHANGED_WITH_9_MONTHS(
      resourceUrl("rif-static-samples/sample-u-with-9-months.txt"),
      RifFileType.BENEFICIARY,
      Optional.of(1)),

  SAMPLE_U_BENES_UNCHANGED(
      resourceUrl("rif-static-samples/sample-u-unchanged-beneficiaries.txt"),
      RifFileType.BENEFICIARY,
      Optional.of(1)),

  SAMPLE_U_CARRIER(
      resourceUrl("rif-static-samples/sample-u-bcarrier.txt"), RifFileType.CARRIER, Optional.of(1)),

  SAMPLE_SYNTHEA_BENE(
      resourceUrl("rif-synthea/beneficiary.csv"), RifFileType.BENEFICIARY, Optional.empty()),
  SAMPLE_SYNTHEA_BENEINT(
      resourceUrl("rif-synthea/beneficiary_interim.csv"),
      RifFileType.BENEFICIARY,
      Optional.empty()),
  SAMPLE_SYNTHEA_BENEFINAL(
      resourceUrl("rif-synthea/beneficiary_final.csv"), RifFileType.BENEFICIARY, Optional.empty()),
  SAMPLE_SYNTHEA_CARRIER(
      resourceUrl("rif-synthea/carrier.csv"), RifFileType.CARRIER, Optional.empty()),
  SAMPLE_SYNTHEA_INPATIENT(
      resourceUrl("rif-synthea/inpatient.csv"), RifFileType.INPATIENT, Optional.empty()),
  SAMPLE_SYNTHEA_OUTPATIENT(
      resourceUrl("rif-synthea/outpatient.csv"), RifFileType.OUTPATIENT, Optional.empty()),
  SAMPLE_SYNTHEA_SNF(resourceUrl("rif-synthea/snf.csv"), RifFileType.SNF, Optional.empty()),
  SAMPLE_SYNTHEA_HOSPICE(
      resourceUrl("rif-synthea/hospice.csv"), RifFileType.HOSPICE, Optional.empty()),
  SAMPLE_SYNTHEA_HHA(resourceUrl("rif-synthea/hha.csv"), RifFileType.HHA, Optional.empty()),
  SAMPLE_SYNTHEA_DME(resourceUrl("rif-synthea/dme.csv"), RifFileType.DME, Optional.empty()),
  SAMPLE_SYNTHEA_PDE(resourceUrl("rif-synthea/pde.csv"), RifFileType.PDE, Optional.empty()),
  SAMPLE_SYNTHEA_BENEHISTORY(
      resourceUrl("rif-synthea/beneficiary_history.csv"),
      RifFileType.BENEFICIARY_HISTORY,
      Optional.empty()),

  SYNTHETIC_BENEFICIARY_1999(
      remoteS3Data(TestDataSetLocation.SYNTHETIC_DATA, "synthetic-beneficiary-1999.rif"),
      RifFileType.BENEFICIARY,
      Optional.of(10000)),

  SYNTHETIC_BENEFICIARY_2000(
      remoteS3Data(TestDataSetLocation.SYNTHETIC_DATA, "synthetic-beneficiary-2000.rif"),
      RifFileType.BENEFICIARY,
      Optional.of(10000)),

  SYNTHETIC_BENEFICIARY_2014(
      remoteS3Data(TestDataSetLocation.SYNTHETIC_DATA, "synthetic-beneficiary-2014.rif"),
      RifFileType.BENEFICIARY,
      Optional.of(10000)),

  SYNTHETIC_CARRIER_1999_1999(
      remoteS3Data(TestDataSetLocation.SYNTHETIC_DATA, "synthetic-carrier-1999-1999.rif"),
      RifFileType.CARRIER,
      Optional.of(102617)),

  SYNTHETIC_CARRIER_1999_2000(
      remoteS3Data(TestDataSetLocation.SYNTHETIC_DATA, "synthetic-carrier-1999-2000.rif"),
      RifFileType.CARRIER,
      Optional.of(107665)),

  SYNTHETIC_CARRIER_1999_2001(
      remoteS3Data(TestDataSetLocation.SYNTHETIC_DATA, "synthetic-carrier-1999-2001.rif"),
      RifFileType.CARRIER,
      Optional.of(113604)),

  SYNTHETIC_CARRIER_2000_2000(
      remoteS3Data(TestDataSetLocation.SYNTHETIC_DATA, "synthetic-carrier-2000-2000.rif"),
      RifFileType.CARRIER,
      Optional.of(102178)),

  SYNTHETIC_CARRIER_2000_2001(
      remoteS3Data(TestDataSetLocation.SYNTHETIC_DATA, "synthetic-carrier-2000-2001.rif"),
      RifFileType.CARRIER,
      Optional.of(108801)),

  SYNTHETIC_CARRIER_2000_2002(
      remoteS3Data(TestDataSetLocation.SYNTHETIC_DATA, "synthetic-carrier-2000-2002.rif"),
      RifFileType.CARRIER,
      Optional.of(113806)),

  SYNTHETIC_CARRIER_2014_2014(
      remoteS3Data(TestDataSetLocation.SYNTHETIC_DATA, "synthetic-carrier-2014-2014.rif"),
      RifFileType.CARRIER,
      Optional.of(108172)),

  SYNTHETIC_CARRIER_2014_2015(
      remoteS3Data(TestDataSetLocation.SYNTHETIC_DATA, "synthetic-carrier-2014-2015.rif"),
      RifFileType.CARRIER,
      Optional.of(106577)),

  SYNTHETIC_CARRIER_2014_2016(
      remoteS3Data(TestDataSetLocation.SYNTHETIC_DATA, "synthetic-carrier-2014-2016.rif"),
      RifFileType.CARRIER,
      Optional.of(86736)),

  SYNTHETIC_INPATIENT_1999_1999(
      remoteS3Data(TestDataSetLocation.SYNTHETIC_DATA, "synthetic-inpatient-1999-1999.rif"),
      RifFileType.INPATIENT,
      Optional.of(650)),

  SYNTHETIC_INPATIENT_1999_2000(
      remoteS3Data(TestDataSetLocation.SYNTHETIC_DATA, "synthetic-inpatient-1999-2000.rif"),
      RifFileType.INPATIENT,
      Optional.of(646)),

  SYNTHETIC_INPATIENT_1999_2001(
      remoteS3Data(TestDataSetLocation.SYNTHETIC_DATA, "synthetic-inpatient-1999-2001.rif"),
      RifFileType.INPATIENT,
      Optional.of(700)),

  SYNTHETIC_INPATIENT_2000_2000(
      remoteS3Data(TestDataSetLocation.SYNTHETIC_DATA, "synthetic-inpatient-2000-2000.rif"),
      RifFileType.INPATIENT,
      Optional.of(706)),

  SYNTHETIC_INPATIENT_2000_2001(
      remoteS3Data(TestDataSetLocation.SYNTHETIC_DATA, "synthetic-inpatient-2000-2001.rif"),
      RifFileType.INPATIENT,
      Optional.of(641)),

  SYNTHETIC_INPATIENT_2000_2002(
      remoteS3Data(TestDataSetLocation.SYNTHETIC_DATA, "synthetic-inpatient-2000-2002.rif"),
      RifFileType.INPATIENT,
      Optional.of(680)),

  SYNTHETIC_INPATIENT_2014_2014(
      remoteS3Data(TestDataSetLocation.SYNTHETIC_DATA, "synthetic-inpatient-2014-2014.rif"),
      RifFileType.INPATIENT,
      Optional.of(352)),

  SYNTHETIC_INPATIENT_2014_2015(
      remoteS3Data(TestDataSetLocation.SYNTHETIC_DATA, "synthetic-inpatient-2014-2015.rif"),
      RifFileType.INPATIENT,
      Optional.of(309)),

  SYNTHETIC_INPATIENT_2014_2016(
      remoteS3Data(TestDataSetLocation.SYNTHETIC_DATA, "synthetic-inpatient-2014-2016.rif"),
      RifFileType.INPATIENT,
      Optional.of(387)),

  SYNTHETIC_PDE_2014(
      remoteS3Data(TestDataSetLocation.SYNTHETIC_DATA, "synthetic-pde-2014.rif"),
      RifFileType.PDE,
      Optional.of(127643)),

  SYNTHETIC_PDE_2015(
      remoteS3Data(TestDataSetLocation.SYNTHETIC_DATA, "synthetic-pde-2015.rif"),
      RifFileType.PDE,
      Optional.of(140176)),

  SYNTHETIC_PDE_2016(
      remoteS3Data(TestDataSetLocation.SYNTHETIC_DATA, "synthetic-pde-2016.rif"),
      RifFileType.PDE,
      Optional.of(145526)),

  SYNTHETIC_OUTPATIENT_1999_1999(
      remoteS3Data(TestDataSetLocation.SYNTHETIC_DATA, "synthetic-outpatient-1999-1999.rif"),
      RifFileType.OUTPATIENT,
      Optional.of(20744)),

  SYNTHETIC_OUTPATIENT_2000_1999(
      remoteS3Data(TestDataSetLocation.SYNTHETIC_DATA, "synthetic-outpatient-2000-1999.rif"),
      RifFileType.OUTPATIENT,
      Optional.of(22439)),

  SYNTHETIC_OUTPATIENT_2001_1999(
      remoteS3Data(TestDataSetLocation.SYNTHETIC_DATA, "synthetic-outpatient-2001-1999.rif"),
      RifFileType.OUTPATIENT,
      Optional.of(23241)),

  SYNTHETIC_OUTPATIENT_2002_2000(
      remoteS3Data(TestDataSetLocation.SYNTHETIC_DATA, "synthetic-outpatient-2002-2000.rif"),
      RifFileType.OUTPATIENT,
      Optional.of(24575)),

  SYNTHETIC_OUTPATIENT_2014_2014(
      remoteS3Data(TestDataSetLocation.SYNTHETIC_DATA, "synthetic-outpatient-2014-2014.rif"),
      RifFileType.OUTPATIENT,
      Optional.of(25194)),

  SYNTHETIC_OUTPATIENT_2015_2014(
      remoteS3Data(TestDataSetLocation.SYNTHETIC_DATA, "synthetic-outpatient-2015-2014.rif"),
      RifFileType.OUTPATIENT,
      Optional.of(26996)),

  SYNTHETIC_OUTPATIENT_2016_2014(
      remoteS3Data(TestDataSetLocation.SYNTHETIC_DATA, "synthetic-outpatient-2016-2014.rif"),
      RifFileType.OUTPATIENT,
      Optional.of(27955)),

  SAMPLE_MCT_BENES(
      resourceUrl("rif-static-samples/sample-mct-beneficiaries.txt"),
      RifFileType.BENEFICIARY,
      Optional.of(8)),

  SAMPLE_MCT_PDE(
      resourceUrl("rif-static-samples/sample-mct-pde.txt"), RifFileType.PDE, Optional.of(40)),

  SAMPLE_MCT_UPDATE_1_BENES(
      resourceUrl("rif-static-samples/sample-mct-update-1-beneficiaries.txt"),
      RifFileType.BENEFICIARY,
      Optional.of(8)),

  SAMPLE_MCT_UPDATE_2_PDE(
      resourceUrl("rif-static-samples/sample-mct-update-2-pde.txt"),
      RifFileType.PDE,
      Optional.of(40)),

  SAMPLE_MCT_UPDATE_3_BENES(
      resourceUrl("rif-static-samples/sample-mct-update-3-beneficiaries.txt"),
      RifFileType.BENEFICIARY,
      Optional.of(2)),

  SAMPLE_MCT_UPDATE_3_PDE(
      resourceUrl("rif-static-samples/sample-mct-update-3-pde.txt"),
      RifFileType.PDE,
      Optional.of(7)),

  SAMPLE_HICN_MULT_BENES(
      resourceUrl("rif-static-samples/sample-hicn-mult-bene-beneficiaries.txt"),
      RifFileType.BENEFICIARY,
      Optional.of(10)),

  SAMPLE_HICN_MULT_BENES_BENEFICIARY_HISTORY(
      resourceUrl("rif-static-samples/sample-hicn-mult-bene-beneficiaryhistory.txt"),
      RifFileType.BENEFICIARY_HISTORY,
      Optional.of(7));

  private final Supplier<URL> resourceUrlSupplier;
  private final RifFileType rifFileType;
  private Optional<Integer> recordCount;

  private URL resourceUrl;

  /**
   * Enum constant constructor.
   *
   * @param resourceUrlSupplier the value to use for {@link #getResourceSupplier()}
   * @param rifFileType the value to use for {@link #getRifFileType()}
   * @param recordCount the value to use for {@link #getRecordCount()}, or {@link Optional#empty()}
   *     if that count is not known in advance
   */
  private StaticRifResource(
      Supplier<URL> resourceUrlSupplier, RifFileType rifFileType, Optional<Integer> recordCount) {
    this.resourceUrlSupplier = resourceUrlSupplier;
    this.rifFileType = rifFileType;
    this.recordCount = recordCount;
  }

  /** @return the {@link URL} to the resource's contents */
  public synchronized URL getResourceUrl() {
    if (resourceUrl == null) resourceUrl = resourceUrlSupplier.get();

    return resourceUrl;
  }

  /** @return the {@link RifFileType} of the RIF file */
  public RifFileType getRifFileType() {
    return rifFileType;
  }

  /** @return the number of beneficiaries/claims/drug events in the RIF file */
  public synchronized int getRecordCount() {
    if (!recordCount.isPresent()) recordCount = Optional.of(countRecords());

    return recordCount.get();
  }

  /** @return the number of records in this {@link StaticRifResource} */
  private Integer countRecords() {
    RifFile rifFile = new StaticRifFile(this);
    try (CSVParser csvParser = RifParsingUtils.createCsvParser(rifFile)) {
      Optional<Enum<?>> idColumn = rifFile.getFileType().getIdColumn();
      Set<String> uniqueIds = new HashSet<>();
      csvParser.forEach(
          csvRecord -> {
            if (idColumn.isPresent()) {
              // We can't just count the number of lines, as that won't account for there being
              // multiple claim lines per claim.
              if (csvRecord.get(0).equals("INSERT")) {
                String id = csvRecord.get(idColumn.get());
                uniqueIds.add(id);
              }
            } else {
              // Just count the number of lines
              uniqueIds.add("" + csvRecord.getRecordNumber());
            }
          });
      return uniqueIds.size();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /** @return a {@link RifFile} based on this {@link StaticRifResource} */
  public RifFile toRifFile() {
    return new StaticRifFile(this);
  }

  /**
   * @param resourceName the name of the resource on the classpath (as might be passed to {@link
   *     ClassLoader#getResource(String)})
   * @return a {@link Supplier} for the {@link URL} to the resource's contents
   */
  private static Supplier<URL> resourceUrl(String resourceName) {
    return () -> {
      URL resource = Thread.currentThread().getContextClassLoader().getResource(resourceName);
      if (resource == null)
        throw new IllegalArgumentException("Unable to find resource: " + resourceName);

      return resource;
    };
  }

  /**
   * @param dataSetLocation the {@link TestDataSetLocation} of the file to get a local copy of
   * @param fileName the name of the specific file in the specified {@link TestDataSetLocation} to
   *     get a local copy of, e.g. "beneficiaries.rif"
   * @return a {@link URL} to a local copy of the specified test data file from S3
   */
  private static Supplier<URL> localCopyOfS3Data(
      TestDataSetLocation dataSetLocation, String fileName) {
    return () -> {
      // Find the build output `target` directory.
      Path targetDir = Paths.get(".", "bfd-model-rif-samples", "target");
      if (!Files.exists(targetDir)) targetDir = Paths.get("..", "bfd-model-rif-samples", "target");
      if (!Files.exists(targetDir)) targetDir = Paths.get(".", "target");
      if (!Files.exists(targetDir))
        throw new IllegalStateException(
            "Unable to find directory: " + targetDir.toAbsolutePath().toString());

      // Build the path that the resources will be downloaded to.
      Path resourceDir =
          targetDir
              .resolve("test-data-from-s3")
              .resolve(dataSetLocation.getS3BucketName())
              .resolve(dataSetLocation.getS3KeyPrefix().replaceAll(":", "-"));
      Path resourceLocalCopy = resourceDir.resolve(fileName);

      /*
       * Implementation note: we have to carefully leverage
       * synchronization to ensure that we don't end up with multiple
       * copies of the same file. To avoid pegging dev systems, it's also
       * best to ensure that we're only grabbing one file at a time.
       * Locking on the static class object accomplishes these goals.
       */
      synchronized (StaticRifResource.class) {
        // Ensure the directory exists.
        if (!Files.exists(resourceDir)) {
          try {
            Files.createDirectories(resourceDir);
          } catch (IOException e) {
            throw new UncheckedIOException(e);
          }
        }

        // Download the file, if needed.
        if (!Files.exists(resourceLocalCopy)) {
          downloadFromS3(dataSetLocation, fileName, resourceLocalCopy);
        }
      }

      // We now know the file exists, so return it.
      try {
        return resourceLocalCopy.toUri().toURL();
      } catch (MalformedURLException e) {
        throw new BadCodeMonkeyException(e);
      }
    };
  }

  /**
   * Downloads the specified S3 object to the specified local path.
   *
   * @param dataSetLocation the {@link TestDataSetLocation} of the S3 object to download
   * @param fileName the name of the specific object/file to be downloaded
   * @param downloadPath the {@link Path} to download the S3 object to
   */
  private static void downloadFromS3(
      TestDataSetLocation dataSetLocation, String fileName, Path downloadPath) {
    /*
     * To avoid dragging in the S3 client libraries, we require here that
     * the test data files be available publicly via HTTP.
     */

    URL s3DownloadUrl = remoteS3Data(dataSetLocation, fileName).get();
    download(s3DownloadUrl, downloadPath);
  }

  /**
   * Copies the contents of the specified {@link URL} to the specified local {@link Path}.
   *
   * @param url the {@link URL} to download the contents of
   * @param localPath the local {@link Path} to write to
   */
  private static void download(URL url, Path localPath) {
    FileOutputStream outputStream = null;
    try {
      ReadableByteChannel channel = Channels.newChannel(url.openStream());
      outputStream = new FileOutputStream(localPath.toFile());
      outputStream.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
    } catch (IOException e) {
      throw new UncheckedIOException("Unable to download URL: " + url, e);
    } finally {
      if (outputStream != null) {
        try {
          outputStream.close();
        } catch (IOException e) {
          throw new UncheckedIOException(e);
        }
      }
    }
  }

  /**
   * @param dataSetLocation the {@link TestDataSetLocation} of the file to get a local copy of
   * @param fileName the name of the specific file in the specified {@link TestDataSetLocation} to
   *     get a local copy of, e.g. "beneficiaries.rif"
   * @return a {@link URL} that can be used to download/stream the specified test data file from S3
   */
  private static Supplier<URL> remoteS3Data(TestDataSetLocation dataSetLocation, String fileName) {
    return () -> {
      try {
        return new URL(
            String.format(
                "http://%s.s3.amazonaws.com/%s/%s",
                dataSetLocation.getS3BucketName(), dataSetLocation.getS3KeyPrefix(), fileName));
      } catch (MalformedURLException e) {
        throw new BadCodeMonkeyException(e);
      }
    };
  }

  /**
   * A simple app driver that can be run to verify the record counts for each {@link
   * StaticRifResource}.
   *
   * @param args (not used)
   * @throws Exception Any {@link Exception}s encountered will cause this mini-app to terminate.
   */
  public static void main(String[] args) throws Exception {
    /*
     * Note: Because of the SAMPLE_C files' large size, this will take HOURS
     * to run.
     */

    for (StaticRifResource resource : StaticRifResource.values()) {
      Set<String> uniqueIds = new HashSet<>();
      Path tempDownloadPath = null;
      InputStream tempDownloadStream = null;
      try {
        tempDownloadPath = Files.createTempFile("bfd-test-data-", ".rif");
        download(resource.getResourceUrl(), tempDownloadPath);

        tempDownloadStream =
            new BufferedInputStream(new FileInputStream(tempDownloadPath.toFile()));
        CSVParser parser =
            RifParsingUtils.createCsvParser(
                RifParsingUtils.CSV_FORMAT, tempDownloadStream, StandardCharsets.UTF_8);
        parser.forEach(
            r -> {
              if (resource.getRifFileType().getIdColumn() != null)
                uniqueIds.add(r.get(resource.getRifFileType().getIdColumn().get()));
              else uniqueIds.add("" + r.getRecordNumber());
            });
      } finally {
        if (tempDownloadPath != null) Files.deleteIfExists(tempDownloadPath);
        if (tempDownloadStream != null) tempDownloadStream.close();
      }
      System.out.println(String.format("%s: %d", resource.name(), uniqueIds.size()));
    }
  }
}
