package gov.hhs.cms.bluebutton.server.app.stu3.providers;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.hl7.fhir.dstu3.model.ExplanationOfBenefit;
import org.hl7.fhir.dstu3.model.ExplanationOfBenefit.CareTeamComponent;
import org.hl7.fhir.dstu3.model.ExplanationOfBenefit.ItemComponent;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.codesystems.ClaimCareteamrole;
import org.hl7.fhir.exceptions.FHIRException;
import org.junit.Assert;
import org.junit.Test;

import gov.hhs.cms.bluebutton.data.model.rif.DMEClaim;
import gov.hhs.cms.bluebutton.data.model.rif.DMEClaimLine;
import gov.hhs.cms.bluebutton.data.model.rif.samples.StaticRifResource;
import gov.hhs.cms.bluebutton.data.model.rif.samples.StaticRifResourceGroup;
import gov.hhs.cms.bluebutton.server.app.ServerTestUtils;

/**
 * Unit tests for {@link DMEClaimTransformer}.
 */
public final class DMEClaimTransformerTest {
	/**
	 * Verifies that {@link DMEClaimTransformer#transform(Object)} works as
	 * expected when run against the {@link StaticRifResource#SAMPLE_A_DME}
	 * {@link DMEClaim}.
	 * 
	 * @throws FHIRException
	 *             (indicates test failure)
	 */
	@Test
	public void transformSampleARecord() throws FHIRException {
		List<Object> parsedRecords = ServerTestUtils
				.parseData(Arrays.asList(StaticRifResourceGroup.SAMPLE_A.getResources()));
		DMEClaim claim = parsedRecords.stream().filter(r -> r instanceof DMEClaim).map(r -> (DMEClaim) r).findFirst()
				.get();

		ExplanationOfBenefit eob = DMEClaimTransformer.transform(claim);
		assertMatches(claim, eob);
	}

	/**
	 * Verifies that the {@link ExplanationOfBenefit} "looks like" it should, if
	 * it were produced from the specified {@link DMEClaim}.
	 * 
	 * @param claim
	 *            the {@link DMEClaim} that the {@link ExplanationOfBenefit} was
	 *            generated from
	 * @param eob
	 *            the {@link ExplanationOfBenefit} that was generated from the
	 *            specified {@link DMEClaim}
	 * @throws FHIRException
	 *             (indicates test failure)
	 */
	static void assertMatches(DMEClaim claim, ExplanationOfBenefit eob) throws FHIRException {
		TransformerTestUtils.assertNoEncodedOptionals(eob);

		Assert.assertEquals(TransformerUtils.buildEobId(ClaimType.DME, claim.getClaimId()),
				eob.getIdElement().getIdPart());

		TransformerTestUtils.assertIdentifierExists(TransformerConstants.CODING_CCW_CLAIM_ID, claim.getClaimId(),
				eob.getIdentifier());
		TransformerTestUtils.assertIdentifierExists(TransformerConstants.CODING_CCW_CLAIM_GROUP_ID,
				claim.getClaimGroupId().toPlainString(), eob.getIdentifier());
		Assert.assertEquals(TransformerUtils.referencePatient(claim.getBeneficiaryId()).getReference(),
				eob.getPatient().getReference());
		TransformerTestUtils.assertHasCoding(TransformerConstants.CODING_CCW_RECORD_ID_CODE,
				"" + claim.getNearLineRecordIdCode(), eob.getType());
		TransformerTestUtils.assertHasCoding(TransformerConstants.CODING_CCW_CLAIM_TYPE,
				claim.getClaimTypeCode(), eob.getType());
		Assert.assertEquals(
				TransformerUtils.referenceCoverage(claim.getBeneficiaryId(), MedicareSegment.PART_B).getReference(),
				eob.getInsurance().getCoverage().getReference());
		Assert.assertEquals("active", eob.getStatus().toCode());

		TransformerTestUtils.assertDateEquals(claim.getDateFrom(), eob.getBillablePeriod().getStartElement());
		TransformerTestUtils.assertDateEquals(claim.getDateThrough(), eob.getBillablePeriod().getEndElement());

		Assert.assertEquals(TransformerConstants.CODED_EOB_DISPOSITION, eob.getDisposition());
		TransformerTestUtils.assertExtensionCodingEquals(eob,
				TransformerConstants.EXTENSION_IDENTIFIER_CARRIER_NUMBER,
				TransformerConstants.EXTENSION_IDENTIFIER_CARRIER_NUMBER, claim.getCarrierNumber());
		TransformerTestUtils.assertExtensionCodingEquals(eob,
				TransformerConstants.EXTENSION_CODING_CCW_CARR_PAYMENT_DENIAL,
				TransformerConstants.EXTENSION_CODING_CCW_CARR_PAYMENT_DENIAL, claim.getPaymentDenialCode());
		Assert.assertEquals(claim.getPaymentAmount(), eob.getPayment().getAmount().getValue());
		TransformerTestUtils.assertHasCoding(TransformerConstants.CODING_CCW_CLAIM_TYPE,
				claim.getClaimTypeCode(), eob.getType());
		Assert.assertEquals("active", eob.getStatus().toCode());

		TransformerTestUtils.assertBenefitBalanceEquals(TransformerConstants.CODING_BBAPI_BENEFIT_BALANCE_TYPE,
				TransformerConstants.CODED_ADJUDICATION_PRIMARY_PAYER_PAID_AMOUNT, claim.getPrimaryPayerPaidAmount(),
				eob.getBenefitBalanceFirstRep().getFinancial());

		TransformerTestUtils.assertExtensionCodingEquals(eob,
				TransformerConstants.EXTENSION_IDENTIFIER_CLINICAL_TRIAL_NUMBER,
				TransformerConstants.EXTENSION_IDENTIFIER_CLINICAL_TRIAL_NUMBER,
				claim.getClinicalTrialNumber().get());

		ReferralRequest referral = (ReferralRequest) eob.getReferral().getResource();
		Assert.assertEquals(TransformerUtils.referencePatient(claim.getBeneficiaryId()).getReference(),
				referral.getSubject().getReference());
		TransformerTestUtils.assertReferenceIdentifierEquals(TransformerConstants.CODING_NPI_US,
				claim.getReferringPhysicianNpi().get(), referral.getRequester().getAgent());

		TransformerTestUtils.assertExtensionCodingEquals(eob,
				TransformerConstants.CODING_CCW_PROVIDER_ASSIGNMENT,
				TransformerConstants.CODING_CCW_PROVIDER_ASSIGNMENT, "A");

		TransformerTestUtils.assertBenefitBalanceEquals(TransformerConstants.CODING_BBAPI_BENEFIT_BALANCE_TYPE,
				TransformerConstants.CODED_ADJUDICATION_PROVIDER_PAYMENT_AMOUNT, claim.getProviderPaymentAmount(),
				eob.getBenefitBalanceFirstRep().getFinancial());
		TransformerTestUtils.assertBenefitBalanceEquals(TransformerConstants.CODING_BBAPI_BENEFIT_BALANCE_TYPE,
				TransformerConstants.CODED_ADJUDICATION_SUBMITTED_CHARGE_AMOUNT, claim.getSubmittedChargeAmount(),
				eob.getBenefitBalanceFirstRep().getFinancial());
		TransformerTestUtils.assertBenefitBalanceEquals(TransformerConstants.CODING_BBAPI_BENEFIT_BALANCE_TYPE,
				TransformerConstants.CODED_ADJUDICATION_ALLOWED_CHARGE, claim.getAllowedChargeAmount(),
				eob.getBenefitBalanceFirstRep().getFinancial());

		Assert.assertEquals(3, eob.getDiagnosis().size());
		Assert.assertEquals(1, eob.getItem().size());
		ItemComponent eobItem0 = eob.getItem().get(0);
		DMEClaimLine claimLine1 = claim.getLines().get(0);
		Assert.assertEquals(claimLine1.getLineNumber(), new BigDecimal(eobItem0.getSequence()));

		TransformerTestUtils.assertCareTeamEquals(claimLine1.getProviderNPI().get(),
				ClaimCareteamrole.PRIMARY.toCode(), eob);
		CareTeamComponent performingCareTeamEntry = TransformerTestUtils.findCareTeamEntryForProviderIdentifier(
				claimLine1.getProviderNPI().get(), eob.getCareTeam());
		TransformerTestUtils.assertHasCoding(TransformerConstants.CODING_CCW_PROVIDER_SPECIALTY,
				claimLine1.getProviderSpecialityCode().get(), performingCareTeamEntry.getQualification());
		TransformerTestUtils.assertExtensionCodingEquals(performingCareTeamEntry,
				TransformerConstants.EXTENSION_CODING_CCW_PROVIDER_PARTICIPATING,
				TransformerConstants.EXTENSION_CODING_CCW_PROVIDER_PARTICIPATING,
				"" + claimLine1.getProviderParticipatingIndCode().get());

		TransformerTestUtils.assertExtensionCodingEquals(eobItem0,
				TransformerConstants.CODING_FHIR_ACT_INVOICE_GROUP,
				TransformerConstants.CODING_FHIR_ACT_INVOICE_GROUP,
				(TransformerConstants.CODED_ACT_INVOICE_GROUP_CLINICAL_SERVICES_AND_PRODUCTS));

		TransformerTestUtils.assertExtensionCodingEquals(eobItem0.getLocation(),
				TransformerConstants.EXTENSION_CODING_CCW_PROVIDER_STATE,
				TransformerConstants.EXTENSION_CODING_CCW_PROVIDER_STATE, claimLine1.getProviderStateCode());

		TransformerTestUtils.assertHasCoding(TransformerConstants.CODING_CCW_TYPE_SERVICE,
				String.valueOf(claimLine1.getCmsServiceTypeCode()), eobItem0.getCategory());

		TransformerTestUtils.assertHasCoding(TransformerConstants.CODING_CCW_PLACE_OF_SERVICE,
				claimLine1.getPlaceOfServiceCode(), eobItem0.getLocationCodeableConcept());

		TransformerTestUtils.assertDateEquals(claimLine1.getFirstExpenseDate().get(),
				eobItem0.getServicedPeriod().getStartElement());
		TransformerTestUtils.assertDateEquals(claimLine1.getLastExpenseDate().get(),
				eobItem0.getServicedPeriod().getEndElement());

		TransformerTestUtils.assertHasCoding(TransformerConstants.CODING_HCPCS,
				claimLine1.getHcpcsInitialModifierCode().get(),
				eobItem0.getModifier().get(0));
		Assert.assertFalse(claimLine1.getHcpcsSecondModifierCode().isPresent());

		TransformerTestUtils.assertHasCoding(TransformerConstants.CODING_HCPCS, "" + claim.getHcpcsYearCode().get(),
				claimLine1.getHcpcsCode().get(), eobItem0.getService());
		TransformerTestUtils.assertExtensionCodingEquals(eobItem0, TransformerConstants.CODING_BETOS,
				TransformerConstants.CODING_BETOS, claimLine1.getBetosCode().get());

		TransformerTestUtils.assertAdjudicationEquals(TransformerConstants.CODED_ADJUDICATION_PAYMENT,
				claimLine1.getPaymentAmount(), eobItem0.getAdjudication());
		TransformerTestUtils.assertAdjudicationEquals(
				TransformerConstants.CODED_ADJUDICATION_BENEFICIARY_PAYMENT_AMOUNT,
				claimLine1.getBeneficiaryPaymentAmount(), eobItem0.getAdjudication());
		TransformerTestUtils.assertAdjudicationEquals(TransformerConstants.CODED_ADJUDICATION_PROVIDER_PAYMENT_AMOUNT,
				claimLine1.getProviderPaymentAmount(), eobItem0.getAdjudication());
		TransformerTestUtils.assertAdjudicationEquals(TransformerConstants.CODED_ADJUDICATION_DEDUCTIBLE,
				claimLine1.getBeneficiaryPartBDeductAmount(), eobItem0.getAdjudication());
		TransformerTestUtils.assertAdjudicationEquals(TransformerConstants.CODED_ADJUDICATION_PRIMARY_PAYER_PAID_AMOUNT,
				claimLine1.getPrimaryPayerPaidAmount(), eobItem0.getAdjudication());
		TransformerTestUtils.assertAdjudicationEquals(TransformerConstants.CODED_ADJUDICATION_LINE_COINSURANCE_AMOUNT,
				claimLine1.getCoinsuranceAmount(), eobItem0.getAdjudication());
		TransformerTestUtils.assertAdjudicationEquals(
				TransformerConstants.CODED_ADJUDICATION_LINE_PRIMARY_PAYER_ALLOWED_CHARGE,
				claimLine1.getPrimaryPayerAllowedChargeAmount(), eobItem0.getAdjudication());
		TransformerTestUtils.assertAdjudicationEquals(TransformerConstants.CODED_ADJUDICATION_SUBMITTED_CHARGE_AMOUNT,
				claimLine1.getSubmittedChargeAmount(), eobItem0.getAdjudication());
		TransformerTestUtils.assertAdjudicationEquals(TransformerConstants.CODED_ADJUDICATION_ALLOWED_CHARGE,
				claimLine1.getAllowedChargeAmount(), eobItem0.getAdjudication());

		TransformerTestUtils.assertExtensionCodingEquals(eobItem0,
				TransformerConstants.CODING_CCW_PROCESSING_INDICATOR,
				TransformerConstants.CODING_CCW_PROCESSING_INDICATOR,
				claimLine1.getProcessingIndicatorCode().get());

		TransformerTestUtils.assertDiagnosisLinkPresent(
				Diagnosis.from(claimLine1.getDiagnosisCode(), claimLine1.getDiagnosisCodeVersion()), eob, eobItem0);

		TransformerTestUtils.assertAdjudicationEquals(
				TransformerConstants.CODED_ADJUDICATION_LINE_PURCHASE_PRICE_AMOUNT, claimLine1.getPurchasePriceAmount(),
				eobItem0.getAdjudication());

		TransformerTestUtils.assertExtensionCodingEquals(eobItem0.getLocation(),
				TransformerConstants.EXTENSION_CODING_CCW_PRICING_STATE_CD,
				TransformerConstants.EXTENSION_CODING_CCW_PRICING_STATE_CD, claimLine1.getPricingStateCode().get());

		TransformerTestUtils.assertExtensionCodingEquals(eobItem0.getLocation(),
				TransformerConstants.EXTENSION_CODING_CMS_SUPPLIER_TYPE,
				TransformerConstants.EXTENSION_CODING_CMS_SUPPLIER_TYPE,
				String.valueOf(claimLine1.getSupplierTypeCode().get()));

		TransformerTestUtils.assertExtensionCodingEquals(eobItem0, TransformerConstants.EXTENSION_CODING_MTUS,
				TransformerConstants.EXTENSION_CODING_MTUS, String.valueOf(claimLine1.getMtusCode().get()));

		TransformerTestUtils.assertExtensionCodingEquals(eobItem0, TransformerConstants.EXTENSION_MTUS_COUNT,
				TransformerConstants.EXTENSION_MTUS_COUNT, String.valueOf(claimLine1.getMtusCount()));

		TransformerTestUtils.assertExtensionCodingEquals(eobItem0, TransformerConstants.CODING_NDC,
				TransformerConstants.CODING_NDC, claimLine1.getNationalDrugCode().get());

	}
}
