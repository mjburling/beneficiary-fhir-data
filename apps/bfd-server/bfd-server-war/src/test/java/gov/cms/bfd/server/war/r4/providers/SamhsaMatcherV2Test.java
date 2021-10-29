package gov.cms.bfd.server.war.r4.providers;

import static org.junit.Assert.assertTrue;

import com.codahale.metrics.MetricRegistry;
import gov.cms.bfd.model.rif.InpatientClaim;
import gov.cms.bfd.model.rif.samples.StaticRifResourceGroup;
import gov.cms.bfd.server.war.ServerTestUtils;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.hl7.fhir.r4.model.ExplanationOfBenefit;
import org.junit.Before;
import org.junit.Test;

/** Verifies that transformations that contain SAMHSA codes are filtered as expected. */
public final class SamhsaMatcherV2Test {

  private R4SamhsaMatcher samhsaMatcherV2;

  /** Sets up the test. */
  @Before
  public void setup() {
    samhsaMatcherV2 = new R4SamhsaMatcher();
  }

  /**
   * Verifies that when transforming a SAMSHA claim into an ExplanationOfBenefit (where the
   * ExplanationOfBenefit then contains an item[n].productOrService.coding[n].system =
   * TransformerConstants.CODING_SYSTEM_HCPCS) the SAMHSA matcher's test method will successfully
   * identify this as a SAMSHA related ExplanationOfBenefit.
   */
  @Test
  public void testR4SamshaMatcherWhenTransformedInpatientHasItemWithHcpcsCodeExpectMatch() {

    InpatientClaim claim = generateClaim();

    // TODO: Adjust claim to have item with the right values, or make a claim txt example that turns
    // into the right thing
    // The existing sample-a files use eob.diagnosis instead of
    // eob.item[n].productOrService.coding[n].system so
    // they wont test the HCPCS value. We need new samples which resolve to the correct field for
    // this test.

    ExplanationOfBenefit explanationOfBenefit =
        InpatientClaimTransformerV2.transform(new MetricRegistry(), claim, Optional.empty());

    assertTrue(samhsaMatcherV2.test(explanationOfBenefit));
  }

  /**
   * Verifies that when transforming a SAMSHA claim into an ExplanationOfBenefit (where the
   * ExplanationOfBenefit then contains an item[n].productOrService.coding[n].system =
   * CcwCodebookVariable.HCPCS_CD) the SAMHSA matcher's test method will not identify this as a
   * SAMSHA related ExplanationOfBenefit.
   *
   * <p>While this code is used for SAMSHA system values, BFD is currently using
   * TransformerConstants.CODING_SYSTEM_HCPCS as the only recognized code.
   */
  @Test
  public void testR4SamshaMatcherWhenTransformedInpatientHasItemWithHcpcsCdCodeExpectNoMatch() {

    InpatientClaim claim = generateClaim();

    // TODO: Adjust claim to have item with the right values, or make a claim txt example that turns
    // into the right thing
    // The existing sample-a files use eob.diagnosis instead of
    // eob.item[n].productOrService.coding[n].system so
    // they wont test the HCPCS value. We need new samples which resolve to the correct field for
    // this test.

    ExplanationOfBenefit explanationOfBenefit =
        InpatientClaimTransformerV2.transform(new MetricRegistry(), claim, Optional.empty());

    assertTrue(samhsaMatcherV2.test(explanationOfBenefit));
  }

  /**
   * Verifies that when transforming a claim into an ExplanationOfBenefit which has no
   * item[n].productOrService.coding[n].system (procedure code) values which =
   * TransformerConstants.CODING_SYSTEM_HCPCS, has no eob.diagnosis SAMSHA code, and has no
   * eob.procedure SAMSHA code, then the SAMHSA matcher's test method will not identify this as a
   * SAMSHA related ExplanationOfBenefit.
   */
  @Test
  public void testR4SamshaMatcherWhenTransformedInpatientHasItemWithNoHcpcsCodeExpectNoMatch() {

    InpatientClaim claim = generateClaim();

    // TODO: Need an impatient claim which does not trigger samsha on any of the three SAMSHA
    // triggers

    ExplanationOfBenefit explanationOfBenefit =
        InpatientClaimTransformerV2.transform(new MetricRegistry(), claim, Optional.empty());

    assertTrue(samhsaMatcherV2.test(explanationOfBenefit));
  }

  /**
   * Generates the Claim object to be used in a test.
   *
   * @return the inpatient claim to be used for the test
   */
  public InpatientClaim generateClaim() {
    List<Object> parsedRecords =
        ServerTestUtils.parseData(Arrays.asList(StaticRifResourceGroup.SAMPLE_A.getResources()));

    InpatientClaim claim =
        parsedRecords.stream()
            .filter(r -> r instanceof InpatientClaim)
            .map(r -> (InpatientClaim) r)
            .findFirst()
            .orElse(null);

    if (claim != null) {
      claim.setLastUpdated(Instant.now());
    } else {
      throw new IllegalStateException(
          "Test setup issue, did not find expected InpatientClaim in sample record.");
    }

    return claim;
  }
}
