package gov.hhs.cms.bluebutton.datapipeline.fhir.transform;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hl7.fhir.dstu21.model.Address;
import org.hl7.fhir.dstu21.model.Bundle;
import org.hl7.fhir.dstu21.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu21.model.Bundle.BundleEntryRequestComponent;
import org.hl7.fhir.dstu21.model.Bundle.BundleType;
import org.hl7.fhir.dstu21.model.Bundle.HTTPVerb;
import org.hl7.fhir.dstu21.model.CodeableConcept;
import org.hl7.fhir.dstu21.model.Coding;
import org.hl7.fhir.dstu21.model.Coverage;
import org.hl7.fhir.dstu21.model.DateType;
import org.hl7.fhir.dstu21.model.Duration;
import org.hl7.fhir.dstu21.model.ExplanationOfBenefit;
import org.hl7.fhir.dstu21.model.ExplanationOfBenefit.DetailComponent;
import org.hl7.fhir.dstu21.model.ExplanationOfBenefit.DiagnosisComponent;
import org.hl7.fhir.dstu21.model.ExplanationOfBenefit.ItemsComponent;
import org.hl7.fhir.dstu21.model.ExplanationOfBenefit.SubDetailComponent;
import org.hl7.fhir.dstu21.model.Extension;
import org.hl7.fhir.dstu21.model.IdType;
import org.hl7.fhir.dstu21.model.Identifier;
import org.hl7.fhir.dstu21.model.IntegerType;
import org.hl7.fhir.dstu21.model.Medication;
import org.hl7.fhir.dstu21.model.MedicationOrder;
import org.hl7.fhir.dstu21.model.MedicationOrder.MedicationOrderDispenseRequestComponent;
import org.hl7.fhir.dstu21.model.Money;
import org.hl7.fhir.dstu21.model.Organization;
import org.hl7.fhir.dstu21.model.Patient;
import org.hl7.fhir.dstu21.model.Period;
import org.hl7.fhir.dstu21.model.Practitioner;
import org.hl7.fhir.dstu21.model.Reference;
import org.hl7.fhir.dstu21.model.ReferralRequest;
import org.hl7.fhir.dstu21.model.ReferralRequest.ReferralStatus;
import org.hl7.fhir.dstu21.model.Resource;
import org.hl7.fhir.dstu21.model.SimpleQuantity;
import org.hl7.fhir.dstu21.model.StringType;
import org.hl7.fhir.dstu21.model.TemporalPrecisionEnum;
import org.hl7.fhir.dstu21.model.valuesets.Adjudication;
import org.hl7.fhir.instance.model.api.IBaseResource;

import com.justdavis.karl.misc.exceptions.BadCodeMonkeyException;

import gov.hhs.cms.bluebutton.datapipeline.ccw.jdo.ClaimType;
import gov.hhs.cms.bluebutton.datapipeline.ccw.jdo.CurrentBeneficiary;
import gov.hhs.cms.bluebutton.datapipeline.ccw.jdo.PartAClaimFact;
import gov.hhs.cms.bluebutton.datapipeline.ccw.jdo.PartAClaimRevLineFact;
import gov.hhs.cms.bluebutton.datapipeline.ccw.jdo.PartBClaimFact;
import gov.hhs.cms.bluebutton.datapipeline.ccw.jdo.PartBClaimLineFact;
import gov.hhs.cms.bluebutton.datapipeline.ccw.jdo.PartDEventFact;
import gov.hhs.cms.bluebutton.datapipeline.rif.model.BeneficiaryRow;
import gov.hhs.cms.bluebutton.datapipeline.rif.model.CarrierClaimGroup;
import gov.hhs.cms.bluebutton.datapipeline.rif.model.CarrierClaimGroup.CarrierClaimLine;
import gov.hhs.cms.bluebutton.datapipeline.rif.model.IcdCode;
import gov.hhs.cms.bluebutton.datapipeline.rif.model.PartDEventRow;
import gov.hhs.cms.bluebutton.datapipeline.rif.model.RecordAction;
import gov.hhs.cms.bluebutton.datapipeline.rif.model.RifRecordEvent;

/**
 * Handles the translation from source/CCW {@link CurrentBeneficiary} data into
 * FHIR {@link BeneficiaryBundle}s.
 */
public final class DataTransformer {
	static final String EXTENSION_CMS_CLAIM_TYPE = "http://bluebutton.cms.hhs.gov/extensions#claimType";

	static final String EXTENSION_CMS_DIAGNOSIS_GROUP = "http://bluebutton.cms.hhs.gov/extensions#diagnosisRelatedGroupCode";

	static final String EXTENSION_CMS_ADMITTING_DIAGNOSIS = "http://bluebutton.cms.hhs.gov/extensions#admittingDiagnosis";

	static final String EXTENSION_CMS_OTHER_PHYSICIAN = "http://bluebutton.cms.hhs.gov/extensions#otherPhysician";

	static final String EXTENSION_CMS_OPERATING_PHYSICIAN = "http://bluebutton.cms.hhs.gov/extensions#operatingPhysician";

	static final String EXTENSION_CMS_ATTENDING_PHYSICIAN = "http://bluebutton.cms.hhs.gov/extensions#attendingPhysician";

	static final String EXTENSION_CMS_DIAGNOSIS_LINK_ID = "http://bluebutton.cms.hhs.gov/extensions#diagnosisLinkId";

	static final String COVERAGE_ISSUER = "Centers for Medicare and Medicaid Services";

	static final String COVERAGE_PLAN = "Medicare";

	/**
	 * The {@link Coverage#getPlan()} value for Part A.
	 */
	static final String COVERAGE_PLAN_PART_A = "Part A";

	/**
	 * The {@link Coverage#getPlan()} value for Part B.
	 */
	static final String COVERAGE_PLAN_PART_B = "Part B";

	/**
	 * The {@link Coverage#getPlan()} value for Part D.
	 */
	static final String COVERAGE_PLAN_PART_D = "Part D";

	/**
	 * A CMS-controlled standard. More info here: <a href=
	 * "https://en.wikipedia.org/wiki/Healthcare_Common_Procedure_Coding_System">
	 * Healthcare Common Procedure Coding System</a>.
	 */
	static final String CODING_SYSTEM_HCPCS = "https://www.ccwdata.org/cs/groups/public/documents/datadictionary/typsrvcb.txt";

	static final String CODING_SYSTEM_ICD9_DIAG = "http://hl7.org/fhir/sid/icd-9-cm/diagnosis";

	static final String CODING_SYSTEM_ICD9_PROC = "http://hl7.org/fhir/sid/icd-9-cm/procedure";

	/**
	 * The United States National Provider Identifier, as available at
	 * <a href="http://download.cms.gov/nppes/NPI_Files.html">NPI/NPPES File</a>
	 * .
	 */
	static final String CODING_SYSTEM_NPI_US = "http://hl7.org/fhir/sid/us-npi";

	static final String CODING_SYSTEM_ADJUDICATION_FHIR = "http://hl7.org/fhir/adjudication";

	static final String CODING_SYSTEM_CMS_CLAIM_TYPES = "http://bluebutton.cms.hhs.gov/coding#claimType";

	/**
	 * The CMS-custom {@link Coding#getSystem()} value for Medicare
	 * {@link Adjudication}s.
	 */
	static final String CODING_SYSTEM_ADJUDICATION_CMS = "CMS Adjudications";

	/**
	 * See <a href=
	 * "https://www.ccwdata.org/cs/groups/public/documents/datadictionary/bene_id.txt">
	 * CCW Data Dictionary: BENE_ID</a>.
	 */
	static final String CODING_SYSTEM_CCW_BENE_ID = "CCW.BENE_ID";

	static final String CODING_SYSTEM_CCW_CLAIM_ID = "https://www.ccwdata.org/cs/groups/public/documents/datadictionary/clm_id.txt";

	/**
	 * See <a href=
	 * "https://www.ccwdata.org/cs/groups/public/documents/datadictionary/pde_id.txt">
	 * CCW Data Dictionary: PDE_ID</a>.
	 */
	static final String CODING_SYSTEM_CCW_PDE_ID = "CCW.PDE_ID";

	/**
	 * See <a href=
	 * "https://www.ccwdata.org/cs/groups/public/documents/datadictionary/rx_srvc_rfrnc_num.txt">
	 * CCW Data Dictionary: RX_SRVC_RFRNC_NUM</a>.
	 */
	static final String CODING_SYSTEM_RX_SRVC_RFRNC_NUM = "CCW.RX_SRVC_RFRNC_NUM";

	static final String CODING_SYSTEM_CCW_PHRMCY_SRVC_TYPE_CD = "https://www.ccwdata.org/cs/groups/public/documents/datadictionary/phrmcy_srvc_type_cd.txt";

	static final String CODING_SYSTEM_PDE_PLAN_CONTRACT_ID = "https://www.ccwdata.org/cs/groups/public/documents/datadictionary/plan_cntrct_rec_id.txt";

	static final String CODING_SYSTEM_PDE_PLAN_BENEFIT_PACKAGE_ID = "https://www.ccwdata.org/cs/groups/public/documents/datadictionary/plan_pbp_rec_num.txt";

	static final String CODING_SYSTEM_FHIR_ACT = "http://hl7.org/fhir/v3/ActCode";

	static final String CODED_CMS_CLAIM_TYPE_RX_DRUG = "FIXME3"; // FIXME

	static final String CODED_ADJUDICATION_ALLOWED_CHARGE = "Line Allowed Charge Amount";

	static final String CODED_ADJUDICATION_DEDUCTIBLE = "Line Beneficiary Part B Deductible Amount";

	static final String CODED_ADJUDICATION_BENEFICIARY_PRIMARY_PAYER_PAID = "Line Beneficiary Primary Payer Paid Amount";

	static final String CODED_ADJUDICATION_LINE_COINSURANCE_AMOUNT = "Line Coinsurance Amount";

	static final String CODED_ADJUDICATION_PAYMENT = "Line NCH Payment Amount";

	/**
	 * See <a href=
	 * "https://www.ccwdata.org/cs/groups/public/documents/datadictionary/lprvpmt.txt">
	 * CCW Data Dictionary: LPRVPMT</a>.
	 */
	static final String CODED_ADJUDICATION_PAYMENT_B = "Line Provider Payment Amount";

	static final String CODED_ADJUDICATION_PASS_THROUGH_PER_DIEM_AMOUNT = "Claim Pass Thru Per Diem Amount";

	static final String CODED_ADJUDICATION_NCH_BENEFICIARY_INPATIENT_DEDUCTIBLE = "NCH Beneficiary Inpatient Deductible Amount";

	static final String CODED_ADJUDICATION_NCH_BENEFICIARY_PART_A_COINSURANCE_LIABILITY = "NCH Beneficiary Part A Coinsurance Liability Amount";

	/**
	 * See <a href=
	 * "https://www.ccwdata.org/cs/groups/public/documents/datadictionary/ptnt_pay_amt.txt">
	 * CCW Data Dictionary: PTNT_PAY_AMT</a>.
	 */
	static final String CODED_ADJUDICATION_PATIENT_PAY = "Patient Pay Amount";

	/**
	 * See <a href=
	 * "https://www.ccwdata.org/cs/groups/public/documents/datadictionary/tot_rx_cst_amt.txt">
	 * CCW Data Dictionary: TOT_RX_CST_AMT</a>.
	 */
	static final String CODED_ADJUDICATION_TOTAL_COST = "Total Prescription Cost";

	static final String CODED_ADJUDICATION_NCH_BENEFICIARY_BLOOD_DEDUCTIBLE_LIABILITY_AMOUNT = "NCH Beneficiary Blood Deductible Liability Amount";

	static final String CODED_ADJUDICATION_NCH_PRIMARY_PAYER_CLAIM_PAID_AMOUNT = "NCH Primary Payer Claim Paid Amount";

	static final String CODED_ADJUDICATION_NCH_BENEFICIARY_PART_B_DEDUCTIBLE = "NCH Beneficiary Part B Deductible Amount";

	static final String CODED_ADJUDICATION_NCH_BENEFICIARY_PART_B_COINSURANCE = "NCH Beneficiary Part B Coinsurance Amount";

	/**
	 * See <a href=
	 * "https://www.ccwdata.org/cs/groups/public/documents/datadictionary/drug_cvrg_stus_cd.txt">
	 * CCW Data Dictionary: DRUG_CVRG_STUS_CD</a>.
	 */
	static final String CODED_ADJUDICATION_PART_D_COVERED = "Part D Covered";

	/**
	 * See <a href=
	 * "https://www.ccwdata.org/cs/groups/public/documents/datadictionary/drug_cvrg_stus_cd.txt">
	 * CCW Data Dictionary: DRUG_CVRG_STUS_CD</a>.
	 */
	static final String CODED_ADJUDICATION_PART_D_NONCOVERED_SUPPLEMENT = "Part D Supplemental drugs (reported by plans that provide Enhanced Alternative coverage)";

	/**
	 * See <a href=
	 * "https://www.ccwdata.org/cs/groups/public/documents/datadictionary/drug_cvrg_stus_cd.txt">
	 * CCW Data Dictionary: DRUG_CVRG_STUS_CD</a>.
	 */
	static final String CODED_ADJUDICATION_PART_D_NONCOVERED_OTC = "Part D Over-the-counter drugs";

	/**
	 * See <a href=
	 * "https://www.ccwdata.org/cs/groups/public/documents/datadictionary/othr_troop_amt.txt">
	 * CCW Data Dictionary: OTHR_TROOP_AMT</a>.
	 */
	static final String CODED_ADJUDICATION_OTHER_TROOP_AMOUNT = "Other True Out-of-Pocket (TrOOP) Amount";

	/**
	 * See <a href=
	 * "https://www.ccwdata.org/cs/groups/public/documents/datadictionary/lics_amt.txt">
	 * CCW Data Dictionary: LICS_AMT</a>.
	 */
	static final String CODED_ADJUDICATION_LOW_INCOME_SUBSIDY_AMOUNT = "Part D Low Income Subsidy (LICS) Amount";

	/**
	 * See <a href=
	 * "https://www.ccwdata.org/cs/groups/public/documents/datadictionary/plro_amt.txt">
	 * CCW Data Dictionary: PLRO_AMT</a>.
	 */
	static final String CODED_ADJUDICATION_PATIENT_LIABILITY_REDUCED_AMOUNT = "Reduction in patient liability due to payments by other payers (PLRO) Amount";

	/**
	 * See <a href=
	 * "https://www.ccwdata.org/cs/groups/public/documents/datadictionary/rptd_gap_dscnt_num.txt">
	 * CCW Data Dictionary: RPTD_GAP_DSCNT_NUM</a>.
	 */
	static final String CODED_ADJUDICATION_GAP_DISCOUNT_AMOUNT = "Medicare Coverage Gap Discount Amount";

	static final String CODING_SYSTEM_FHIR_EOB_ITEM_TYPE = "http://hl7.org/fhir/ValueSet/v3-ActInvoiceGroupCode";

	static final String CODED_EOB_ITEM_TYPE_CLINICAL_SERVICES_AND_PRODUCTS = "CSPINV";

	static final String CODING_SYSTEM_MONEY = "urn:std:iso:4217";

	static final String CODING_SYSTEM_MONEY_US = "USD";

	static final String CODING_SYSTEM_NDC = "https://www.accessdata.fda.gov/scripts/cder/ndc";

	/**
	 * @param sourceBeneficiaries
	 *            the source/CCW {@link CurrentBeneficiary} records to be
	 *            transformed
	 * @return a {@link Stream} of FHIR {@link BeneficiaryBundle}s
	 */
	public Stream<BeneficiaryBundle> transformSourceData(Stream<CurrentBeneficiary> sourceBeneficiaries) {
		Stream<BeneficiaryBundle> transformedRecords = sourceBeneficiaries.map(b -> convertToFhir(b));
		return transformedRecords;
	}

	/**
	 * @param sourceBeneficiary
	 *            a source {@link CurrentBeneficiary} record, along with its
	 *            associated claims data
	 * @return a {@link BeneficiaryBundle} that represents the specified
	 *         beneficiary and its associated claims data
	 */
	static BeneficiaryBundle convertToFhir(CurrentBeneficiary sourceBeneficiary) {
		List<IBaseResource> resources = new ArrayList<>();

		Patient patient = new Patient();
		resources.add(patient);
		patient.setId(IdType.newRandomUuid());
		patient.addIdentifier().setSystem("CCW_BENE_CRNT_VW.BENE_ID").setValue("" + sourceBeneficiary.getId());
		if (sourceBeneficiary.getBirthDate() != null)
			patient.setBirthDate(Date.valueOf(sourceBeneficiary.getBirthDate()));
		patient.addName().addFamily(sourceBeneficiary.getSurname()).addGiven(sourceBeneficiary.getGivenName());

		List<String> addressComponents = Arrays.asList(sourceBeneficiary.getContactAddress(),
				sourceBeneficiary.getContactAddressZip());
		addressComponents = addressComponents.stream().filter(c -> (c != null && c.trim().length() > 0))
				.collect(Collectors.toList());
		if (!addressComponents.isEmpty()) {
			Address address = patient.addAddress();
			for (String addressComponent : addressComponents)
				address.addLine(addressComponent);
		}

		Organization cms = new Organization();
		resources.add(cms);
		cms.setId(IdType.newRandomUuid());
		cms.setName("CMS");

		Coverage partACoverage = new Coverage();
		resources.add(partACoverage);
		partACoverage.setId(IdType.newRandomUuid());
		partACoverage.setIssuer(new Reference(cms.getId()));
		partACoverage.setPlan(COVERAGE_PLAN_PART_A);

		// Transform all Part A Inpatient claims.
		for (PartAClaimFact sourceClaim : sourceBeneficiary.getPartAClaimFacts()) {
			// Filter to only inpatient claims.
			if (sourceClaim.getClaimProfile() == null
					|| sourceClaim.getClaimProfile().getClaimType() != ClaimType.INPATIENT_CLAIM)
				continue;

			ExplanationOfBenefit eob = new ExplanationOfBenefit();
			resources.add(eob);
			eob.setId(IdType.newRandomUuid());
			eob.getCoverage().setCoverage(new Reference(partACoverage.getId()));
			eob.addExtension().setUrl(EXTENSION_CMS_CLAIM_TYPE)
					.setValue(new Coding().setSystem(CODING_SYSTEM_CMS_CLAIM_TYPES)
							.setCode(sourceClaim.getClaimProfile().getClaimType().getCode())
							.setDisplay(sourceClaim.getClaimProfile().getClaimType().getDescription()));
			eob.setPatient(new Reference().setReference(patient.getId()));
			eob.addIdentifier().setSystem("CCW_PTA_FACT.CLM_ID").setValue("" + sourceClaim.getId());

			if (sourceClaim.getDiagnosisGroup() != null) {
				eob.addExtension().setUrl(EXTENSION_CMS_DIAGNOSIS_GROUP)
						.setValue(new StringType(sourceClaim.getDiagnosisGroup().getCode()));
			}

			if (sourceClaim.getDateFrom() != null || sourceClaim.getDateThrough() != null) {
				eob.setBillablePeriod(new Period().setStart(Date.valueOf(sourceClaim.getDateFrom()))
						.setEnd(Date.valueOf(sourceClaim.getDateThrough())));
			}

			if (sourceClaim.getProviderAtTimeOfClaimNpi() != null) {
				Practitioner provider = new Practitioner();
				resources.add(provider);
				provider.setId(IdType.newRandomUuid());
				provider.addIdentifier().setSystem(CODING_SYSTEM_NPI_US)
						.setValue(sourceClaim.getProviderAtTimeOfClaimNpi().toString());
				eob.setProvider(new Reference().setReference(provider.getId()));
			}

			if (sourceClaim.getAttendingPhysicianNpi() != null) {
				Practitioner attendingPhysician = new Practitioner();
				resources.add(attendingPhysician);
				attendingPhysician.setId(IdType.newRandomUuid());
				attendingPhysician.addIdentifier().setSystem(CODING_SYSTEM_NPI_US)
						.setValue(sourceClaim.getAttendingPhysicianNpi().toString());

				Extension attendingPhysicianExtension = eob.addExtension();
				attendingPhysicianExtension.setUrl(EXTENSION_CMS_ATTENDING_PHYSICIAN);
				attendingPhysicianExtension.setValue(new Reference().setReference(attendingPhysician.getId()));
			}

			if (sourceClaim.getOperatingPhysicianNpi() != null) {
				Practitioner operatingPhysician = new Practitioner();
				resources.add(operatingPhysician);
				operatingPhysician.setId(IdType.newRandomUuid());
				operatingPhysician.addIdentifier().setSystem(CODING_SYSTEM_NPI_US)
						.setValue(sourceClaim.getOperatingPhysicianNpi().toString());

				Extension operatingPhysicianExtension = eob.addExtension();
				operatingPhysicianExtension.setUrl(EXTENSION_CMS_OPERATING_PHYSICIAN);
				operatingPhysicianExtension.setValue(new Reference().setReference(operatingPhysician.getId()));
			}

			if (sourceClaim.getOtherPhysicianNpi() != null) {
				Practitioner otherPhysician = new Practitioner();
				resources.add(otherPhysician);
				otherPhysician.setId(IdType.newRandomUuid());
				otherPhysician.addIdentifier().setSystem(CODING_SYSTEM_NPI_US)
						.setValue(sourceClaim.getOtherPhysicianNpi().toString());

				Extension otherPhysicianExtension = eob.addExtension();
				otherPhysicianExtension.setUrl(EXTENSION_CMS_OTHER_PHYSICIAN);
				otherPhysicianExtension.setValue(new Reference().setReference(otherPhysician.getId()));
			}

			if (sourceClaim.getAdmittingDiagnosisCode() != null) {
				Extension operatingPhysicianExtension = eob.addExtension();
				operatingPhysicianExtension.setUrl(EXTENSION_CMS_ADMITTING_DIAGNOSIS);
				operatingPhysicianExtension.setValue(new Coding().setSystem(CODING_SYSTEM_ICD9_DIAG)
						.setCode(sourceClaim.getAdmittingDiagnosisCode()));
			}

			ItemsComponent eobSoleItem = eob.addItem();
			eobSoleItem.setType(new Coding().setSystem(CODING_SYSTEM_FHIR_EOB_ITEM_TYPE)
					.setCode(CODED_EOB_ITEM_TYPE_CLINICAL_SERVICES_AND_PRODUCTS));

			if (sourceClaim.getDateAdmission() != null || sourceClaim.getDateDischarge() != null) {
				eobSoleItem.setServiced(new Period().setStart(Date.valueOf(sourceClaim.getDateAdmission()))
						.setEnd(Date.valueOf(sourceClaim.getDateDischarge())));
			}

			if (sourceClaim.getUtilizationDayCount() != null) {
				SimpleQuantity utilizationDayCount = (SimpleQuantity) new SimpleQuantity().setUnit("days")
						.setValue(sourceClaim.getUtilizationDayCount());
				eobSoleItem.setQuantity(utilizationDayCount);
			}

			if (sourceClaim.getPayment() != null)
				eobSoleItem.addAdjudication()
						.setCategory(new Coding().setSystem(CODING_SYSTEM_ADJUDICATION_CMS)
								.setCode(CODED_ADJUDICATION_PAYMENT))
						.getAmount().setSystem(CODING_SYSTEM_MONEY).setCode(CODING_SYSTEM_MONEY_US)
						.setValue(sourceClaim.getPayment());

			if (sourceClaim.getPassThroughPerDiemAmount() != null)
				eobSoleItem.addAdjudication()
						.setCategory(new Coding().setSystem(CODING_SYSTEM_ADJUDICATION_CMS)
								.setCode(CODED_ADJUDICATION_PASS_THROUGH_PER_DIEM_AMOUNT))
						.getAmount().setSystem(CODING_SYSTEM_MONEY).setCode(CODING_SYSTEM_MONEY_US)
						.setValue(sourceClaim.getPassThroughPerDiemAmount());

			if (sourceClaim.getNchBeneficiaryBloodDeductibleLiability() != null)
				eobSoleItem.addAdjudication()
						.setCategory(new Coding().setSystem(CODING_SYSTEM_ADJUDICATION_CMS)
								.setCode(CODED_ADJUDICATION_NCH_BENEFICIARY_BLOOD_DEDUCTIBLE_LIABILITY_AMOUNT))
						.getAmount().setSystem(CODING_SYSTEM_MONEY).setCode(CODING_SYSTEM_MONEY_US)
						.setValue(sourceClaim.getNchBeneficiaryBloodDeductibleLiability());

			if (sourceClaim.getNchBeneficiaryInpatientDeductible() != null)
				eobSoleItem.addAdjudication()
						.setCategory(new Coding().setSystem(CODING_SYSTEM_ADJUDICATION_CMS)
								.setCode(CODED_ADJUDICATION_NCH_BENEFICIARY_INPATIENT_DEDUCTIBLE))
						.getAmount().setSystem(CODING_SYSTEM_MONEY).setCode(CODING_SYSTEM_MONEY_US)
						.setValue(sourceClaim.getNchBeneficiaryInpatientDeductible());

			if (sourceClaim.getNchBeneficiaryPartACoinsuranceLiability() != null)
				eobSoleItem.addAdjudication()
						.setCategory(new Coding().setSystem(CODING_SYSTEM_ADJUDICATION_CMS)
								.setCode(CODED_ADJUDICATION_NCH_BENEFICIARY_PART_A_COINSURANCE_LIABILITY))
						.getAmount().setSystem(CODING_SYSTEM_MONEY).setCode(CODING_SYSTEM_MONEY_US)
						.setValue(sourceClaim.getNchBeneficiaryPartACoinsuranceLiability());

			if (sourceClaim.getNchPrimaryPayerPaid() != null)
				eobSoleItem.addAdjudication()
						.setCategory(new Coding().setSystem(CODING_SYSTEM_ADJUDICATION_CMS)
								.setCode(CODED_ADJUDICATION_NCH_PRIMARY_PAYER_CLAIM_PAID_AMOUNT))
						.getAmount().setSystem(CODING_SYSTEM_MONEY).setCode(CODING_SYSTEM_MONEY_US)
						.setValue(sourceClaim.getNchPrimaryPayerPaid());

			for (PartAClaimRevLineFact sourceLine : sourceClaim.getClaimLines()) {
				DetailComponent eobDetail = eobSoleItem.addDetail();
				eobDetail.setSequence((int) sourceLine.getLineNumber());
				eobDetail.setType(new Coding().setSystem(CODING_SYSTEM_FHIR_EOB_ITEM_TYPE)
						.setCode(CODED_EOB_ITEM_TYPE_CLINICAL_SERVICES_AND_PRODUCTS));

				addDiagnosisCodeAndLink(eob, eobDetail, sourceLine.getDiagnosisCode1());
				addDiagnosisCodeAndLink(eob, eobDetail, sourceLine.getDiagnosisCode2());
				addDiagnosisCodeAndLink(eob, eobDetail, sourceLine.getDiagnosisCode3());
				addDiagnosisCodeAndLink(eob, eobDetail, sourceLine.getDiagnosisCode4());
				addDiagnosisCodeAndLink(eob, eobDetail, sourceLine.getDiagnosisCode5());
				addDiagnosisCodeAndLink(eob, eobDetail, sourceLine.getDiagnosisCode6());
				addDiagnosisCodeAndLink(eob, eobDetail, sourceLine.getDiagnosisCode7());
				addDiagnosisCodeAndLink(eob, eobDetail, sourceLine.getDiagnosisCode8());
				addDiagnosisCodeAndLink(eob, eobDetail, sourceLine.getDiagnosisCode9());
				addDiagnosisCodeAndLink(eob, eobDetail, sourceLine.getDiagnosisCode10());

				List<String> procedureCodes = Arrays
						.stream(new String[] { sourceLine.getProcedureCode1(), sourceLine.getProcedureCode2(),
								sourceLine.getProcedureCode3(), sourceLine.getProcedureCode4(),
								sourceLine.getProcedureCode5(), sourceLine.getProcedureCode6() })
						.filter(c -> c != null && !c.trim().isEmpty()).collect(Collectors.toList());
				for (String procedureCode : procedureCodes) {
					SubDetailComponent eobSubDetail = new SubDetailComponent();
					eobDetail.addSubDetail(eobSubDetail);
					eobSubDetail.setType(new Coding().setSystem(CODING_SYSTEM_FHIR_EOB_ITEM_TYPE)
							.setCode(CODED_EOB_ITEM_TYPE_CLINICAL_SERVICES_AND_PRODUCTS));
					eobSubDetail.setService(new Coding().setSystem(CODING_SYSTEM_ICD9_PROC).setCode(procedureCode));
				}
			}
		}

		Coverage partBCoverage = new Coverage();
		resources.add(partBCoverage);
		partBCoverage.setId(IdType.newRandomUuid());
		partBCoverage.setIssuer(new Reference(cms.getId()));
		partBCoverage.setPlan(COVERAGE_PLAN_PART_B);

		/*
		 * Transform all Part B Outpatient claims. Confusingly, these are stored
		 * in the CCW's Part A tables because Part B hospital outpatient claims
		 * happen to be structured very similarly to Part A hospital inpatient
		 * claims. The claim type for each record can be used to determine which
		 * is actually which.
		 */
		for (PartAClaimFact sourceClaim : sourceBeneficiary.getPartAClaimFacts()) {
			// Filter to only outpatient claims.
			if (sourceClaim.getClaimProfile() == null
					|| sourceClaim.getClaimProfile().getClaimType() != ClaimType.OUTPATIENT_CLAIM)
				continue;

			ExplanationOfBenefit eob = new ExplanationOfBenefit();
			resources.add(eob);
			eob.setId(IdType.newRandomUuid());
			eob.getCoverage().setCoverage(new Reference(partBCoverage.getId()));
			eob.addExtension().setUrl(EXTENSION_CMS_CLAIM_TYPE)
					.setValue(new Coding().setSystem(CODING_SYSTEM_CMS_CLAIM_TYPES)
							.setCode(sourceClaim.getClaimProfile().getClaimType().getCode())
							.setDisplay(sourceClaim.getClaimProfile().getClaimType().getDescription()));
			eob.setPatient(new Reference().setReference(patient.getId()));
			eob.addIdentifier().setSystem("CCW_PTA_FACT.CLM_ID").setValue("" + sourceClaim.getId());

			if (sourceClaim.getDateFrom() != null || sourceClaim.getDateThrough() != null) {
				eob.setBillablePeriod(new Period().setStart(Date.valueOf(sourceClaim.getDateFrom()))
						.setEnd(Date.valueOf(sourceClaim.getDateThrough())));
			}

			if (sourceClaim.getProviderAtTimeOfClaimNpi() != null) {
				Practitioner provider = new Practitioner();
				resources.add(provider);
				provider.setId(IdType.newRandomUuid());
				provider.addIdentifier().setSystem(CODING_SYSTEM_NPI_US)
						.setValue(sourceClaim.getProviderAtTimeOfClaimNpi().toString());
				eob.setProvider(new Reference().setReference(provider.getId()));
			}

			if (sourceClaim.getAttendingPhysicianNpi() != null) {
				Practitioner attendingPhysician = new Practitioner();
				resources.add(attendingPhysician);
				attendingPhysician.setId(IdType.newRandomUuid());
				attendingPhysician.addIdentifier().setSystem(CODING_SYSTEM_NPI_US)
						.setValue(sourceClaim.getAttendingPhysicianNpi().toString());

				Extension attendingPhysicianExtension = eob.addExtension();
				attendingPhysicianExtension.setUrl(EXTENSION_CMS_ATTENDING_PHYSICIAN);
				attendingPhysicianExtension.setValue(new Reference().setReference(attendingPhysician.getId()));
			}

			if (sourceClaim.getOperatingPhysicianNpi() != null) {
				Practitioner operatingPhysician = new Practitioner();
				resources.add(operatingPhysician);
				operatingPhysician.setId(IdType.newRandomUuid());
				operatingPhysician.addIdentifier().setSystem(CODING_SYSTEM_NPI_US)
						.setValue(sourceClaim.getOperatingPhysicianNpi().toString());

				Extension operatingPhysicianExtension = eob.addExtension();
				operatingPhysicianExtension.setUrl(EXTENSION_CMS_OPERATING_PHYSICIAN);
				operatingPhysicianExtension.setValue(new Reference().setReference(operatingPhysician.getId()));
			}

			if (sourceClaim.getOtherPhysicianNpi() != null) {
				Practitioner otherPhysician = new Practitioner();
				resources.add(otherPhysician);
				otherPhysician.setId(IdType.newRandomUuid());
				otherPhysician.addIdentifier().setSystem(CODING_SYSTEM_NPI_US)
						.setValue(sourceClaim.getOtherPhysicianNpi().toString());

				Extension otherPhysicianExtension = eob.addExtension();
				otherPhysicianExtension.setUrl(EXTENSION_CMS_OTHER_PHYSICIAN);
				otherPhysicianExtension.setValue(new Reference().setReference(otherPhysician.getId()));
			}

			if (sourceClaim.getAdmittingDiagnosisCode() != null) {
				Extension operatingPhysicianExtension = eob.addExtension();
				operatingPhysicianExtension.setUrl(EXTENSION_CMS_ADMITTING_DIAGNOSIS);
				operatingPhysicianExtension.setValue(new Coding().setSystem(CODING_SYSTEM_ICD9_DIAG)
						.setCode(sourceClaim.getAdmittingDiagnosisCode()));
			}

			ItemsComponent eobSoleItem = eob.addItem();
			eobSoleItem.setType(new Coding().setSystem(CODING_SYSTEM_FHIR_EOB_ITEM_TYPE)
					.setCode(CODED_EOB_ITEM_TYPE_CLINICAL_SERVICES_AND_PRODUCTS));

			if (sourceClaim.getPayment() != null)
				eobSoleItem.addAdjudication()
						.setCategory(new Coding().setSystem(CODING_SYSTEM_ADJUDICATION_CMS)
								.setCode(CODED_ADJUDICATION_PAYMENT))
						.getAmount().setSystem(CODING_SYSTEM_MONEY).setCode(CODING_SYSTEM_MONEY_US)
						.setValue(sourceClaim.getPayment());

			if (sourceClaim.getNchPrimaryPayerPaid() != null)
				eobSoleItem.addAdjudication()
						.setCategory(new Coding().setSystem(CODING_SYSTEM_ADJUDICATION_CMS)
								.setCode(CODED_ADJUDICATION_NCH_PRIMARY_PAYER_CLAIM_PAID_AMOUNT))
						.getAmount().setSystem(CODING_SYSTEM_MONEY).setCode(CODING_SYSTEM_MONEY_US)
						.setValue(sourceClaim.getNchPrimaryPayerPaid());

			if (sourceClaim.getNchBeneficiaryBloodDeductibleLiability() != null)
				eobSoleItem.addAdjudication()
						.setCategory(new Coding().setSystem(CODING_SYSTEM_ADJUDICATION_CMS)
								.setCode(CODED_ADJUDICATION_NCH_BENEFICIARY_BLOOD_DEDUCTIBLE_LIABILITY_AMOUNT))
						.getAmount().setSystem(CODING_SYSTEM_MONEY).setCode(CODING_SYSTEM_MONEY_US)
						.setValue(sourceClaim.getNchBeneficiaryBloodDeductibleLiability());

			if (sourceClaim.getNchBeneficiaryPartBDeductible() != null)
				eobSoleItem.addAdjudication()
						.setCategory(new Coding().setSystem(CODING_SYSTEM_ADJUDICATION_CMS)
								.setCode(CODED_ADJUDICATION_NCH_BENEFICIARY_PART_B_DEDUCTIBLE))
						.getAmount().setSystem(CODING_SYSTEM_MONEY).setCode(CODING_SYSTEM_MONEY_US)
						.setValue(sourceClaim.getNchBeneficiaryPartBDeductible());

			if (sourceClaim.getNchBeneficiaryPartBCoinsurance() != null)
				eobSoleItem.addAdjudication()
						.setCategory(new Coding().setSystem(CODING_SYSTEM_ADJUDICATION_CMS)
								.setCode(CODED_ADJUDICATION_NCH_BENEFICIARY_PART_B_COINSURANCE))
						.getAmount().setSystem(CODING_SYSTEM_MONEY).setCode(CODING_SYSTEM_MONEY_US)
						.setValue(sourceClaim.getNchBeneficiaryPartBCoinsurance());

			for (PartAClaimRevLineFact sourceLine : sourceClaim.getClaimLines()) {
				DetailComponent eobDetail = eobSoleItem.addDetail();
				eobDetail.setSequence((int) sourceLine.getLineNumber());
				eobDetail.setType(new Coding().setSystem(CODING_SYSTEM_FHIR_EOB_ITEM_TYPE)
						.setCode(CODED_EOB_ITEM_TYPE_CLINICAL_SERVICES_AND_PRODUCTS));

				if (sourceLine.getRevenueCenter() != null)
					eobDetail.setService(new Coding().setSystem(CODING_SYSTEM_HCPCS)
							.setCode(sourceLine.getRevenueCenter().getCode()));

				addDiagnosisCodeAndLink(eob, eobDetail, sourceLine.getDiagnosisCode1());
				addDiagnosisCodeAndLink(eob, eobDetail, sourceLine.getDiagnosisCode2());
				addDiagnosisCodeAndLink(eob, eobDetail, sourceLine.getDiagnosisCode3());
				addDiagnosisCodeAndLink(eob, eobDetail, sourceLine.getDiagnosisCode4());
				addDiagnosisCodeAndLink(eob, eobDetail, sourceLine.getDiagnosisCode5());
				addDiagnosisCodeAndLink(eob, eobDetail, sourceLine.getDiagnosisCode6());
				addDiagnosisCodeAndLink(eob, eobDetail, sourceLine.getDiagnosisCode7());
				addDiagnosisCodeAndLink(eob, eobDetail, sourceLine.getDiagnosisCode8());
				addDiagnosisCodeAndLink(eob, eobDetail, sourceLine.getDiagnosisCode9());
				addDiagnosisCodeAndLink(eob, eobDetail, sourceLine.getDiagnosisCode10());

				List<String> procedureCodes = Arrays
						.stream(new String[] { sourceLine.getProcedureCode1(), sourceLine.getProcedureCode2(),
								sourceLine.getProcedureCode3(), sourceLine.getProcedureCode4(),
								sourceLine.getProcedureCode5(), sourceLine.getProcedureCode6() })
						.filter(c -> c != null && !c.trim().isEmpty()).collect(Collectors.toList());
				for (String procedureCode : procedureCodes) {
					SubDetailComponent eobSubDetail = new SubDetailComponent();
					eobDetail.addSubDetail(eobSubDetail);
					eobSubDetail.setType(new Coding().setSystem(CODING_SYSTEM_FHIR_EOB_ITEM_TYPE)
							.setCode(CODED_EOB_ITEM_TYPE_CLINICAL_SERVICES_AND_PRODUCTS));
					eobSubDetail.setService(new Coding().setSystem(CODING_SYSTEM_ICD9_PROC).setCode(procedureCode));
				}
			}
		}

		// Transform all Part B Carrier claims.
		for (PartBClaimFact sourceClaim : sourceBeneficiary.getPartBClaimFacts()) {
			// Filter to only carrier claims.
			if (sourceClaim.getClaimProfile() == null
					|| sourceClaim.getClaimProfile().getClaimType() != ClaimType.CARRIER_NON_DME_CLAIM)
				continue;

			ExplanationOfBenefit eob = new ExplanationOfBenefit();
			resources.add(eob);
			eob.setId(IdType.newRandomUuid());
			eob.getCoverage().setCoverage(new Reference(partBCoverage.getId()));
			eob.addExtension().setUrl(EXTENSION_CMS_CLAIM_TYPE)
					.setValue(new Coding().setSystem(CODING_SYSTEM_CMS_CLAIM_TYPES)
							.setCode(sourceClaim.getClaimProfile().getClaimType().getCode())
							.setDisplay(sourceClaim.getClaimProfile().getClaimType().getDescription()));
			eob.setPatient(new Reference().setReference(patient.getId()));
			eob.addIdentifier().setSystem("CCW_PTB_FACT.CARR_CLM_CNTL_NUM")
					.setValue("" + sourceClaim.getCarrierControlNumber());

			if (!isBlank(sourceClaim.getDiagnosisCode1()))
				eob.addDiagnosis().getDiagnosis().setSystem(CODING_SYSTEM_ICD9_DIAG)
						.setCode(sourceClaim.getDiagnosisCode1());
			if (!isBlank(sourceClaim.getDiagnosisCode2()))
				eob.addDiagnosis().getDiagnosis().setSystem(CODING_SYSTEM_ICD9_DIAG)
						.setCode(sourceClaim.getDiagnosisCode2());
			if (!isBlank(sourceClaim.getDiagnosisCode3()))
				eob.addDiagnosis().getDiagnosis().setSystem(CODING_SYSTEM_ICD9_DIAG)
						.setCode(sourceClaim.getDiagnosisCode3());
			if (!isBlank(sourceClaim.getDiagnosisCode4()))
				eob.addDiagnosis().getDiagnosis().setSystem(CODING_SYSTEM_ICD9_DIAG)
						.setCode(sourceClaim.getDiagnosisCode4());
			if (!isBlank(sourceClaim.getDiagnosisCode5()))
				eob.addDiagnosis().getDiagnosis().setSystem(CODING_SYSTEM_ICD9_DIAG)
						.setCode(sourceClaim.getDiagnosisCode5());
			if (!isBlank(sourceClaim.getDiagnosisCode6()))
				eob.addDiagnosis().getDiagnosis().setSystem(CODING_SYSTEM_ICD9_DIAG)
						.setCode(sourceClaim.getDiagnosisCode6());
			if (!isBlank(sourceClaim.getDiagnosisCode7()))
				eob.addDiagnosis().getDiagnosis().setSystem(CODING_SYSTEM_ICD9_DIAG)
						.setCode(sourceClaim.getDiagnosisCode7());
			if (!isBlank(sourceClaim.getDiagnosisCode8()))
				eob.addDiagnosis().getDiagnosis().setSystem(CODING_SYSTEM_ICD9_DIAG)
						.setCode(sourceClaim.getDiagnosisCode8());

			if (sourceClaim.getProviderNpi() != null) {
				Practitioner providingPhysician = new Practitioner();
				resources.add(providingPhysician);
				providingPhysician.setId(IdType.newRandomUuid());
				providingPhysician.addIdentifier().setSystem(CODING_SYSTEM_NPI_US)
						.setValue(sourceClaim.getProviderNpi().toString());
				eob.setProvider(new Reference().setReference(providingPhysician.getId()));
			}

			for (PartBClaimLineFact sourceClaimLine : sourceClaim.getClaimLines()) {
				ItemsComponent item = eob.addItem();
				item.setSequence((int) sourceClaimLine.getLineNumber());

				if (sourceClaimLine.getProcedure() != null)
					item.setService(new Coding().setSystem(CODING_SYSTEM_HCPCS)
							.setCode(sourceClaimLine.getProcedure().getCode()));

				if (sourceClaimLine.getDateFrom() != null || sourceClaimLine.getDateThrough() != null) {
					item.setServiced(new Period().setStart(Date.valueOf(sourceClaimLine.getDateFrom()))
							.setEnd(Date.valueOf(sourceClaimLine.getDateThrough())));
				}

				if (sourceClaimLine.getAllowedAmount() != null)
					item.addAdjudication()
							.setCategory(new Coding().setSystem(CODING_SYSTEM_ADJUDICATION_CMS)
									.setCode(CODED_ADJUDICATION_ALLOWED_CHARGE))
							.getAmount().setSystem(CODING_SYSTEM_MONEY).setCode(CODING_SYSTEM_MONEY_US)
							.setValue(sourceClaimLine.getAllowedAmount());
				if (sourceClaimLine.getDeductibleAmount() != null)
					item.addAdjudication()
							.setCategory(new Coding().setSystem(CODING_SYSTEM_ADJUDICATION_CMS)
									.setCode(CODED_ADJUDICATION_DEDUCTIBLE))
							.getAmount().setSystem(CODING_SYSTEM_MONEY).setCode(CODING_SYSTEM_MONEY_US)
							.setValue(sourceClaimLine.getDeductibleAmount());
				if (sourceClaimLine.getBeneficiaryPrimaryPayerPaidAmount() != null)
					item.addAdjudication()
							.setCategory(new Coding().setSystem(CODING_SYSTEM_ADJUDICATION_CMS)
									.setCode(CODED_ADJUDICATION_BENEFICIARY_PRIMARY_PAYER_PAID))
							.getAmount().setSystem(CODING_SYSTEM_MONEY).setCode(CODING_SYSTEM_MONEY_US)
							.setValue(sourceClaimLine.getBeneficiaryPrimaryPayerPaidAmount());
				if (sourceClaimLine.getCoinsuranceAmount() != null)
					item.addAdjudication()
							.setCategory(new Coding().setSystem(CODING_SYSTEM_ADJUDICATION_CMS)
									.setCode(CODED_ADJUDICATION_LINE_COINSURANCE_AMOUNT))
							.getAmount().setSystem(CODING_SYSTEM_MONEY).setCode(CODING_SYSTEM_MONEY_US)
							.setValue(sourceClaimLine.getCoinsuranceAmount());
				if (sourceClaimLine.getNchPaymentAmount() != null)
					item.addAdjudication()
							.setCategory(new Coding().setSystem(CODING_SYSTEM_ADJUDICATION_CMS)
									.setCode(CODED_ADJUDICATION_PAYMENT))
							.getAmount().setSystem(CODING_SYSTEM_MONEY).setCode(CODING_SYSTEM_MONEY_US)
							.setValue(sourceClaimLine.getNchPaymentAmount());

				if (!isBlank(sourceClaimLine.getLineDiagnosisCode())) {
					addDiagnosisCode(eob.getDiagnosis(), sourceClaimLine.getLineDiagnosisCode());
					int diagnosisCodeIndex = getDiagnosisCodeIndex(eob.getDiagnosis(),
							sourceClaimLine.getLineDiagnosisCode());
					item.addDiagnosisLinkId(diagnosisCodeIndex);
				}

				// TODO map source MiscCd

				/*
				 * TODO: where to stick LINE_PRCSG_IND_CD?
				 * "The code on a noninstitutional claim indicating to whom payment was made or if the claim was denied."
				 */
			}
		}

		Coverage partDCoverage = new Coverage();
		resources.add(partDCoverage);
		partDCoverage.setId(IdType.newRandomUuid());
		partDCoverage.setIssuer(new Reference(cms.getId()));
		partDCoverage.setPlan(COVERAGE_PLAN_PART_D);

		for (PartDEventFact sourceEvent : sourceBeneficiary.getPartDEventFacts()) {
			ExplanationOfBenefit eob = new ExplanationOfBenefit();
			resources.add(eob);
			eob.setId(IdType.newRandomUuid());
			eob.getCoverage().setCoverage(new Reference(partDCoverage.getId()));
			eob.addExtension().setUrl(EXTENSION_CMS_CLAIM_TYPE)
					.setValue(new Coding().setSystem(CODING_SYSTEM_CMS_CLAIM_TYPES)
							.setCode(CODED_CMS_CLAIM_TYPE_RX_DRUG).setDisplay("Part D"));
			eob.setPatient(new Reference().setReference(patient.getId()));
			eob.addIdentifier().setSystem("CCW_PDE_FACT.PDE_ID").setValue("" + sourceEvent.getId());

			MedicationOrder prescription = new MedicationOrder();
			resources.add(prescription);
			prescription.setId(IdType.newRandomUuid());
			eob.setPrescription(new Reference().setReference(prescription.getId()));

			if (sourceEvent.getPrescriberNpi() != null) {
				Practitioner prescriber = new Practitioner();
				resources.add(prescriber);
				prescriber.setId(IdType.newRandomUuid());
				prescriber.addIdentifier().setSystem(CODING_SYSTEM_NPI_US)
						.setValue(sourceEvent.getPrescriberNpi().toString());

				prescription.setPrescriber(new Reference().setReference(prescriber.getId()));
			}

			if (sourceEvent.getServiceProviderNpi() != null) {
				Practitioner servicingProvider = new Practitioner();
				resources.add(servicingProvider);
				servicingProvider.setId(IdType.newRandomUuid());
				servicingProvider.addIdentifier().setSystem(CODING_SYSTEM_NPI_US)
						.setValue(sourceEvent.getServiceProviderNpi().toString());
				eob.setProvider(new Reference().setReference(servicingProvider.getId()));
			}

			if (sourceEvent.getProductNdc() != null) {
				// TODO I think the system here is incorrect
				CodeableConcept medicationCoding = new CodeableConcept();
				medicationCoding.addCoding().setSystem("https://www.accessdata.fda.gov/scripts/cder/ndc/")
						.setCode("" + sourceEvent.getProductNdc());
				prescription.setMedication(medicationCoding);
			}

			ItemsComponent eventItem = eob.addItem();
			eventItem.setSequence(1);
			eventItem.setType(new Coding().setSystem("http://hl7.org/fhir/v3/ActCode").setCode("RXDINV"));
			eventItem.setServiced(new DateType().setValue(Date.valueOf(sourceEvent.getServiceDate())));

			SimpleQuantity quantity = new SimpleQuantity();
			quantity.setValue(sourceEvent.getQuantityDispensed());
			Duration numberDaysSupply = new Duration();
			numberDaysSupply.setUnit("days");
			numberDaysSupply.setValue(sourceEvent.getNumberDaysSupply());
			prescription.setDispenseRequest(new MedicationOrderDispenseRequestComponent().setQuantity(quantity)
					.setExpectedSupplyDuration(numberDaysSupply));

			Money patientPayment = (Money) new Money().setSystem(CODING_SYSTEM_MONEY_US)
					.setValue(sourceEvent.getPatientPayAmount());
			eventItem.addAdjudication().setCategory(
					new Coding().setSystem(CODING_SYSTEM_ADJUDICATION_CMS).setCode(CODED_ADJUDICATION_PATIENT_PAY))
					.setAmount(patientPayment);

			Money totalPrescriptionCost = (Money) new Money().setSystem(CODING_SYSTEM_MONEY_US)
					.setValue(sourceEvent.getTotalPrescriptionCost());
			eventItem.addAdjudication().setCategory(
					new Coding().setSystem(CODING_SYSTEM_ADJUDICATION_CMS).setCode(CODED_ADJUDICATION_TOTAL_COST))
					.setAmount(totalPrescriptionCost);
		}

		return new BeneficiaryBundle(resources);
	}

	/**
	 * @param diagnoses
	 *            the {@link DiagnosisComponent}s to add this code to, if it
	 *            isn't already present
	 * @param diagnosisCode
	 *            the diagnosis code to add, if it isn't already present
	 */
	private static void addDiagnosisCode(List<DiagnosisComponent> diagnoses, String diagnosisCode) {
		for (DiagnosisComponent diagnosis : diagnoses) {
			if (diagnosisCode.equals(diagnosis.getDiagnosis().getCode()))
				return;
		}

		DiagnosisComponent diagnosisComponent = new DiagnosisComponent().setSequence(diagnoses.size());
		diagnosisComponent.getDiagnosis().setSystem(CODING_SYSTEM_ICD9_DIAG).setCode(diagnosisCode);
		diagnoses.add(diagnosisComponent);
	}

	/**
	 * @param diagnoses
	 *            the {@link DiagnosisComponent}s to search
	 * @param diagnosisCode
	 *            the diagnosis code to find a match for
	 * @return the index of the specified diagnosis code in the specified list
	 */
	private static int getDiagnosisCodeIndex(List<DiagnosisComponent> diagnoses, String diagnosisCode) {
		for (DiagnosisComponent diagnosis : diagnoses) {
			if (diagnosisCode.equals(diagnosis.getDiagnosis().getCode()))
				return diagnosis.getSequence();
		}

		throw new IllegalArgumentException();
	}

	/**
	 * @param eob
	 *            the {@link ExplanationOfBenefit} to add the diagnosis code to,
	 *            if it isn't already present
	 * @param eobDetail
	 *            the {@link DetailComponent} to link the diagnosis code to
	 * @param diagnosisCode
	 *            the diagnosis code to be added and linked
	 */
	private static void addDiagnosisCodeAndLink(ExplanationOfBenefit eob, DetailComponent eobDetail,
			String diagnosisCode) {
		if (!isBlank(diagnosisCode)) {
			addDiagnosisCode(eob.getDiagnosis(), diagnosisCode);
			int diagnosisCodeIndex = getDiagnosisCodeIndex(eob.getDiagnosis(), diagnosisCode);
			eobDetail.addExtension().setUrl(EXTENSION_CMS_DIAGNOSIS_LINK_ID)
					.setValue(new IntegerType(diagnosisCodeIndex));
		}
	}

	/**
	 * @param value
	 *            the value to check
	 * @return <code>true</code> if the specified value is <code>null</code> or
	 *         only contains whitespace
	 */
	private static boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}

	/**
	 * @param rifStream
	 *            the stream of source {@link RifRecordEvent}s to be transformed
	 * @return the stream of FHIR resource {@link Bundle}s that is the result of
	 *         transforming the specified {@link RifRecordEvent}s
	 */
	@SuppressWarnings("unchecked")
	public Stream<TransformedBundle> transform(Stream<RifRecordEvent<?>> rifStream) {
		return rifStream.map(rifRecordEvent -> {
			if (rifRecordEvent.getRecord() instanceof BeneficiaryRow)
				return transformBeneficiary((RifRecordEvent<BeneficiaryRow>) rifRecordEvent);
			else if (rifRecordEvent.getRecord() instanceof PartDEventRow)
				return transformPartDEvent((RifRecordEvent<PartDEventRow>) rifRecordEvent);
			else if (rifRecordEvent.getRecord() instanceof CarrierClaimGroup)
				return transformCarrierClaim((RifRecordEvent<CarrierClaimGroup>) rifRecordEvent);

			throw new BadCodeMonkeyException("Unhandled record type: " + rifRecordEvent.getRecord());
		});
	}

	/**
	 * @param rifRecordEvent
	 *            the source {@link RifRecordEvent} to be transformed
	 * @return the {@link TransformedBundle} that is the result of transforming
	 *         the specified {@link RifRecordEvent}
	 */
	private TransformedBundle transformBeneficiary(RifRecordEvent<BeneficiaryRow> rifRecordEvent) {
		if (rifRecordEvent == null)
			throw new IllegalArgumentException();
		BeneficiaryRow record = rifRecordEvent.getRecord();
		if (1 != record.version)
			throw new IllegalArgumentException("Unsupported record version: " + record.version);
		if (record.recordAction != RecordAction.INSERT)
			// Will need refactoring to support other ops.
			throw new BadCodeMonkeyException();

		Bundle bundle = new Bundle();
		bundle.setType(BundleType.TRANSACTION);

		Patient beneficiary = new Patient();
		beneficiary.setId("Patient/bene-" + record.beneficiaryId);
		beneficiary.addIdentifier().setSystem(CODING_SYSTEM_CCW_BENE_ID).setValue(record.beneficiaryId);
		beneficiary.addAddress().setState(record.stateCode).setDistrict(record.countyCode)
				.setPostalCode(record.postalCode);
		// TODO rest of mapping
		insert(bundle, beneficiary);

		/*
		 * To prevent an extra round-trip to the FHIR server (which would
		 * greatly complicate this class' code, since it doesn't even know about
		 * the FHIR server), we always just upsert this org, whenever it's used.
		 */
		Organization cms = new Organization();
		cms.setName(COVERAGE_ISSUER);
		Reference cmsOrgRef = upsert(bundle, cms, referenceOrganizationByName(COVERAGE_ISSUER).getReference());

		/*
		 * We don't have detailed enough data on this right now, so we'll just
		 * assume that everyone has Part A, B, and D.
		 */

		Coverage partA = new Coverage();
		partA.setPlan(COVERAGE_PLAN);
		partA.setSubPlan(COVERAGE_PLAN_PART_A);
		partA.setIssuer(cmsOrgRef);
		partA.setSubscriber(referencePatient(record.beneficiaryId));
		insert(bundle, partA);

		Coverage partB = new Coverage();
		partB.setPlan(COVERAGE_PLAN);
		partB.setSubPlan(COVERAGE_PLAN_PART_B);
		partB.setIssuer(cmsOrgRef);
		partB.setSubscriber(referencePatient(record.beneficiaryId));
		insert(bundle, partB);

		Coverage partD = new Coverage();
		partD.setPlan(COVERAGE_PLAN);
		partD.setSubPlan(COVERAGE_PLAN_PART_D);
		partD.setIssuer(cmsOrgRef);
		partD.setSubscriber(referencePatient(record.beneficiaryId));
		insert(bundle, partD);

		return new TransformedBundle(rifRecordEvent, bundle);
	}

	/**
	 * @param rifRecordEvent
	 *            the source {@link RifRecordEvent} to be transformed
	 * @return the {@link TransformedBundle} that is the result of transforming
	 *         the specified {@link RifRecordEvent}
	 */
	private TransformedBundle transformPartDEvent(RifRecordEvent<PartDEventRow> rifRecordEvent) {
		if (rifRecordEvent == null)
			throw new IllegalArgumentException();
		PartDEventRow record = rifRecordEvent.getRecord();
		if (1 != record.version)
			throw new IllegalArgumentException("Unsupported record version: " + record.version);
		if (record.recordAction != RecordAction.INSERT)
			// Will need refactoring to support other ops.
			throw new BadCodeMonkeyException();

		Bundle bundle = new Bundle();
		bundle.setType(BundleType.TRANSACTION);

		ExplanationOfBenefit eob = new ExplanationOfBenefit();
		eob.setId("ExplanationOfBenefit/" + record.partDEventId);
		eob.addIdentifier().setSystem(CODING_SYSTEM_CCW_PDE_ID).setValue(record.partDEventId);
		// TODO Specify eob.type once STU3 is available (pharmacy)
		Reference patientRef = referencePatient(record.beneficiaryId);
		eob.setPatient(patientRef);
		if (record.paymentDate.isPresent()) {
			eob.setPaymentDate(Date.valueOf(record.paymentDate.get()));
		}

		ItemsComponent rxItem = eob.addItem();
		rxItem.setSequence(1);
		switch (record.compoundCode) {
		case COMPOUNDED:
			/* Pharmacy dispense invoice for a compound */
			rxItem.setType(new Coding().setSystem(CODING_SYSTEM_FHIR_ACT).setCode("RXCINV"));
			break;
		case NOT_COMPOUNDED:
			/*
			 * Pharmacy dispense invoice not involving a compound
			 */
			rxItem.setType(new Coding().setSystem(CODING_SYSTEM_FHIR_ACT).setCode("RXDINV"));
			break;
		default:
			/*
			 * Unexpected value encountered - compound code should be either
			 * compounded or not compounded.
			 */
			throw new BadCodeMonkeyException();
		}

		rxItem.setServiced(new DateType().setValue(Date.valueOf(record.prescriptionFillDate)));

		switch (record.drugCoverageStatusCode) {
		/*
		 * If covered by Part D, use value from partDPlanCoveredPaidAmount
		 */
		case COVERED:
			rxItem.addAdjudication()
					.setCategory(new Coding().setSystem(CODING_SYSTEM_ADJUDICATION_CMS)
							.setCode(CODED_ADJUDICATION_PART_D_COVERED))
					.getAmount().setSystem(CODING_SYSTEM_MONEY).setCode(CODING_SYSTEM_MONEY_US)
					.setValue(record.partDPlanCoveredPaidAmount);
			break;
		/*
		 * If not covered by Part D, use value from
		 * partDPlanNonCoveredPaidAmount. There are 2 categories of non-covered
		 * payment amounts: supplemental drugs covered by enhanced plans, and
		 * over the counter drugs that are covered only under specific
		 * circumstances.
		 */
		case SUPPLEMENTAL:
			rxItem.addAdjudication()
					.setCategory(new Coding().setSystem(CODING_SYSTEM_ADJUDICATION_CMS)
							.setCode(CODED_ADJUDICATION_PART_D_NONCOVERED_SUPPLEMENT))
					.getAmount().setSystem(CODING_SYSTEM_MONEY).setCode(CODING_SYSTEM_MONEY_US)
					.setValue(record.partDPlanNonCoveredPaidAmount);
			break;
		case OVER_THE_COUNTER:
			rxItem.addAdjudication()
					.setCategory(new Coding().setSystem(CODING_SYSTEM_ADJUDICATION_CMS)
							.setCode(CODED_ADJUDICATION_PART_D_NONCOVERED_OTC))
					.getAmount().setSystem(CODING_SYSTEM_MONEY).setCode(CODING_SYSTEM_MONEY_US)
					.setValue(record.partDPlanNonCoveredPaidAmount);
			break;
		default:
			/*
			 * Unexpected value encountered - drug coverage status code should
			 * be one of the three above.
			 */
			throw new BadCodeMonkeyException();
		}

		rxItem.addAdjudication()
				.setCategory(
						new Coding().setSystem(CODING_SYSTEM_ADJUDICATION_CMS).setCode(CODED_ADJUDICATION_PATIENT_PAY))
				.getAmount().setSystem(CODING_SYSTEM_MONEY).setCode(CODING_SYSTEM_MONEY_US)
				.setValue(record.patientPaidAmount);

		rxItem.addAdjudication()
				.setCategory(new Coding().setSystem(CODING_SYSTEM_ADJUDICATION_CMS)
						.setCode(CODED_ADJUDICATION_OTHER_TROOP_AMOUNT))
				.getAmount().setSystem(CODING_SYSTEM_MONEY).setCode(CODING_SYSTEM_MONEY_US)
				.setValue(record.otherTrueOutOfPocketPaidAmount);

		rxItem.addAdjudication()
				.setCategory(new Coding().setSystem(CODING_SYSTEM_ADJUDICATION_CMS)
						.setCode(CODED_ADJUDICATION_LOW_INCOME_SUBSIDY_AMOUNT))
				.getAmount().setSystem(CODING_SYSTEM_MONEY).setCode(CODING_SYSTEM_MONEY_US)
				.setValue(record.lowIncomeSubsidyPaidAmount);

		rxItem.addAdjudication()
				.setCategory(new Coding().setSystem(CODING_SYSTEM_ADJUDICATION_CMS)
						.setCode(CODED_ADJUDICATION_PATIENT_LIABILITY_REDUCED_AMOUNT))
				.getAmount().setSystem(CODING_SYSTEM_MONEY).setCode(CODING_SYSTEM_MONEY_US)
				.setValue(record.patientLiabilityReductionOtherPaidAmount);

		rxItem.addAdjudication()
				.setCategory(
						new Coding().setSystem(CODING_SYSTEM_ADJUDICATION_CMS).setCode(CODED_ADJUDICATION_TOTAL_COST))
				.getAmount().setSystem(CODING_SYSTEM_MONEY).setCode(CODING_SYSTEM_MONEY_US)
				.setValue(record.totalPrescriptionCost);

		rxItem.addAdjudication()
				.setCategory(new Coding().setSystem(CODING_SYSTEM_ADJUDICATION_CMS)
						.setCode(CODED_ADJUDICATION_GAP_DISCOUNT_AMOUNT))
				.getAmount().setSystem(CODING_SYSTEM_MONEY).setCode(CODING_SYSTEM_MONEY_US)
				.setValue(record.gapDiscountAmount);

		Practitioner prescriber = new Practitioner();
		/*
		 * Set the coding system for the practitioner if the qualifier code is
		 * NPI, otherwise it is unknown. NPI was used starting in April 2013, so
		 * no other code types should be found here.
		 */
		prescriber.addIdentifier().setSystem(CODING_SYSTEM_NPI_US).setValue(record.prescriberId);
		Reference prescriberRef = referencePractitioner(record.prescriberId);
		upsert(bundle, prescriber, prescriberRef.getReference());

		/*
		 * Upsert Medication using NDC as the ID.
		 */
		Medication medication = new Medication();
		Reference medicationRef = new Reference("Medication/" + record.nationalDrugCode);
		CodeableConcept ndcConcept = new CodeableConcept();
		ndcConcept.addCoding().setSystem(CODING_SYSTEM_NDC).setCode(record.nationalDrugCode);
		medication.setCode(ndcConcept);
		upsert(bundle, medication, medicationRef.getReference());

		/*
		 * MedicationOrders are represented as contained resources inside the
		 * EOB, as the lifetime of this resource should match with the EOB.
		 */
		MedicationOrder medicationOrder = new MedicationOrder();
		medicationOrder.addIdentifier().setSystem(CODING_SYSTEM_RX_SRVC_RFRNC_NUM)
				.setValue(String.valueOf(record.prescriptionReferenceNumber));
		medicationOrder.setPatient(patientRef);
		medicationOrder.setPrescriber(prescriberRef);
		medicationOrder.setMedication(medicationRef);
		SimpleQuantity quantity = new SimpleQuantity();
		quantity.setValue(record.quantityDispensed);
		Duration daysSupply = new Duration();
		daysSupply.setUnit("days");
		daysSupply.setValue(record.daysSupply);
		medicationOrder.setDispenseRequest(new MedicationOrderDispenseRequestComponent().setQuantity(quantity)
				.setExpectedSupplyDuration(daysSupply));
		/*
		 * TODO Populate substitution.allowed and substitution.reason once STU3
		 * structures are available.
		 */
		eob.setPrescription(new Reference(medicationOrder));

		Organization serviceProviderOrg = new Organization();
		serviceProviderOrg.addIdentifier().setSystem(CODING_SYSTEM_NPI_US).setValue(record.serviceProviderId);
		Reference serviceProviderOrgReference = referenceOrganizationByNpi(record.serviceProviderId);
		serviceProviderOrg.setType(new CodeableConcept().addCoding(
				new Coding().setSystem(CODING_SYSTEM_CCW_PHRMCY_SRVC_TYPE_CD).setCode(record.pharmacyTypeCode)));
		upsert(bundle, serviceProviderOrg, serviceProviderOrgReference.getReference());
		eob.setOrganization(serviceProviderOrgReference);

		/*
		 * TODO PDE coverage includes unique identifiers where other coverage
		 * objects do not yet. Coverage identifiers probably need to be
		 * standardized across the board?
		 */
		Coverage coverage = new Coverage();
		Reference coverageRef = new Reference(
				String.format("Coverage?identifier=%s|%s", CODING_SYSTEM_PDE_PLAN_CONTRACT_ID, record.planContractId));
		coverage.addIdentifier().setSystem(CODING_SYSTEM_PDE_PLAN_CONTRACT_ID).setValue(record.planContractId);
		coverage.addIdentifier().setSystem(CODING_SYSTEM_PDE_PLAN_BENEFIT_PACKAGE_ID)
				.setValue(record.planBenefitPackageId);
		coverage.setPlan(COVERAGE_PLAN);
		coverage.setSubPlan(COVERAGE_PLAN_PART_D);
		upsert(bundle, coverage, coverageRef.getReference());
		eob.getCoverage().setCoverage(coverageRef);

		/*
		 * TODO When updated to STU3, use eob.information to store values of
		 * PRCNG_EXCPTN_CD, CTSTRPHC_CVRG_CD
		 */

		insert(bundle, eob);
		return new TransformedBundle(rifRecordEvent, bundle);
	}

	/**
	 * @param rifRecordEvent
	 *            the source {@link RifRecordEvent} to be transformed
	 * @return the {@link TransformedBundle} that is the result of transforming
	 *         the specified {@link RifRecordEvent}
	 */
	private TransformedBundle transformCarrierClaim(RifRecordEvent<CarrierClaimGroup> rifRecordEvent) {
		if (rifRecordEvent == null)
			throw new IllegalArgumentException();
		CarrierClaimGroup claimGroup = rifRecordEvent.getRecord();
		if (1 != claimGroup.version)
			throw new IllegalArgumentException("Unsupported record version: " + claimGroup.version);
		if (claimGroup.recordAction != RecordAction.INSERT)
			// Will need refactoring to support other ops.
			throw new BadCodeMonkeyException();

		Bundle bundle = new Bundle();
		bundle.setType(BundleType.TRANSACTION);

		ExplanationOfBenefit eob = new ExplanationOfBenefit();
		eob.addIdentifier().setSystem(CODING_SYSTEM_CCW_CLAIM_ID).setValue(claimGroup.claimId);
		eob.getCoverage().setCoverage(referenceCoverage(claimGroup.beneficiaryId, COVERAGE_PLAN_PART_B));
		eob.setPatient(referencePatient(claimGroup.beneficiaryId));

		setPeriodStart(eob.getBillablePeriod(), claimGroup.dateFrom);
		setPeriodEnd(eob.getBillablePeriod(), claimGroup.dateThrough);

		/*
		 * Referrals are represented as contained resources, because otherwise
		 * updating them would require an extra roundtrip to the server (can't
		 * think of an intelligent client-specified ID for them).
		 */
		Practitioner referrer = new Practitioner();
		referrer.addIdentifier().setSystem(CODING_SYSTEM_NPI_US).setValue(claimGroup.referringPhysicianNpi);
		Reference referrerReference = upsert(bundle, referrer,
				referencePractitioner(claimGroup.referringPhysicianNpi).getReference());
		ReferralRequest referral = new ReferralRequest();
		referral.setStatus(ReferralStatus.COMPLETED);
		referral.setPatient(referencePatient(claimGroup.beneficiaryId));
		referral.addRecipient(referrerReference);
		// Set the ReferralRequest as a contained resource in the EOB:
		eob.setReferral(new Reference(referral));

		/*
		 * TODO once STU3 is available, transform financial/payment amounts into
		 * eob.information entries
		 */

		addDiagnosisCode(eob, claimGroup.diagnosisPrincipal);
		for (IcdCode diagnosis : claimGroup.diagnosesAdditional)
			addDiagnosisCode(eob, diagnosis);

		for (CarrierClaimLine claimLine : claimGroup.lines) {
			ItemsComponent item = eob.addItem();
			item.setSequence(claimLine.number);

			/*
			 * TODO once STU3 is available, transform these fields into
			 * eob.item.careTeam entries: organizationNpi.
			 */

			/*
			 * TODO once STU3 available, transform cmsServiceTypeCode into
			 * eob.item.category.
			 */

			item.setService(new Coding().setSystem(CODING_SYSTEM_HCPCS).setCode(claimLine.hcpcsCode));

			item.addAdjudication()
					.setCategory(new Coding().setSystem(CODING_SYSTEM_ADJUDICATION_CMS)
							.setCode(CODED_ADJUDICATION_PAYMENT_B))
					.getAmount().setSystem(CODING_SYSTEM_MONEY).setCode(CODING_SYSTEM_MONEY_US)
					.setValue(claimLine.providerPaymentAmount);

			addDiagnosisLink(eob, item, claimLine.diagnosis);
		}

		insert(bundle, eob);
		return new TransformedBundle(rifRecordEvent, bundle);
	}

	/**
	 * @param urlText
	 *            the URL or URL portion to be encoded
	 * @return a URL-encoded version of the specified text
	 */
	private static String urlEncode(String urlText) {
		try {
			return URLEncoder.encode(urlText, StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			throw new BadCodeMonkeyException(e);
		}
	}

	/**
	 * Adds the specified {@link Resource}s to the specified {@link Bundle},
	 * setting it as a <a href="http://hl7-fhir.github.io/http.html#insert">FHIR
	 * "insert" operation</a> if {@link Resource#getId()} is <code>null</code>,
	 * or a <a href="http://hl7-fhir.github.io/http.html#update">FHIR "update"
	 * operation</a> if it is not.
	 * 
	 * @param bundle
	 *            the {@link Bundle} to include the resource in
	 * @param resource
	 *            the FHIR {@link Resource} to upsert into the specified
	 *            {@link Bundle}
	 * @return a {@link Reference} instance, which must be used for any
	 *         references to the {@link Resource} within the same {@link Bundle}
	 */
	private static Reference insert(Bundle bundle, Resource resource) {
		if (bundle == null)
			throw new IllegalArgumentException();
		if (resource == null)
			throw new IllegalArgumentException();

		BundleEntryComponent bundleEntry = bundle.addEntry();
		if (resource.getId() == null)
			bundleEntry.setResource(resource).getRequest().setMethod(HTTPVerb.POST);
		else
			bundleEntry.setResource(resource).getRequest().setMethod(HTTPVerb.PUT).setUrl(resource.getId());

		Reference bundleEntryReference = setFullUrl(bundleEntry);
		return bundleEntryReference;
	}

	/**
	 * Adds the specified {@link Resource}s to the specified {@link Bundle},
	 * setting it as a
	 * <a href="http://hl7-fhir.github.io/http.html#2.1.0.10.2">FHIR
	 * "conditional update" operation</a>.
	 * 
	 * @param bundle
	 *            the {@link Bundle} to include the resource in
	 * @param resource
	 *            the FHIR {@link Resource} to upsert into the specified
	 *            {@link Bundle}
	 * @param resourceUrl
	 *            the value to use for
	 *            {@link BundleEntryRequestComponent#setUrl(String)}, which will
	 *            typically be a search query of the form
	 *            "<code>Patient/?field=value</code>" (there are good examples
	 *            of this here: <a href=
	 *            "http://hl7-fhir.github.io/bundle-transaction.xml.html">
	 *            Bundle- transaction.xml</a>)
	 * @return a {@link Reference} instance, which must be used for any
	 *         references to the {@link Resource} within the same {@link Bundle}
	 */
	private static Reference upsert(Bundle bundle, Resource resource, String resourceUrl) {
		if (bundle == null)
			throw new IllegalArgumentException();
		if (resource == null)
			throw new IllegalArgumentException();

		if (resource.getId() != null)
			throw new IllegalArgumentException("FHIR conditional updates don't allow IDs to be specified client-side");

		BundleEntryComponent bundleEntry = bundle.addEntry();
		bundleEntry.setResource(resource).getRequest().setMethod(HTTPVerb.PUT).setUrl(resourceUrl);

		Reference bundleEntryReference = setFullUrl(bundleEntry);
		return bundleEntryReference;
	}

	/**
	 * <p>
	 * Sets the {@link BundleEntryComponent#getFullUrl()} field of the specified
	 * {@link BundleEntryComponent}. This is a required field, and a bit tricky:
	 * </p>
	 * <ol>
	 * <li>Each entry may use a UUID (i.e. "<code>urn:uuid:...</code>") as its
	 * URL. If so, other resources within the same {@link Bundle} may reference
	 * that UUID when defining relationships with that resource.</li>
	 * <li>Optionally: If the resource is known to <strong>already</strong>
	 * exist on the server, the entry URL may be set to the absolute URL of that
	 * resource. Again: this is optional; existing resources included in the
	 * bundle may also be assigned and referred to via a bundle-specific UUID.
	 * </li>
	 * </ol>
	 * 
	 * 
	 * @param bundleEntry
	 *            the {@link BundleEntryComponent} to modify
	 * @return a {@link Reference} to the {@link Resource} in the specified
	 *         {@link BundleEntryComponent}'s that can be used by other
	 *         resources in the same {@link Bundle}
	 */
	private static Reference setFullUrl(BundleEntryComponent bundleEntry) {
		/*
		 * For sanity, only this method should be used to set this field. And
		 * only once per bundle entry.
		 */
		if (bundleEntry.getFullUrl() != null)
			throw new BadCodeMonkeyException();

		/*
		 * The logic here is super simple. ... So why is this a method, you
		 * might ask? Mostly just so there's a single place for the explanation
		 * in this method's JavaDoc, which took quite a while to figure out.
		 */

		IdType entryId = IdType.newRandomUuid();
		bundleEntry.setFullUrl(entryId.getValue());

		return new Reference(entryId);
	}

	/**
	 * @param orgName
	 *            the {@link Organization#getName()} value to match
	 * @return a {@link Reference} to the {@link Organization} resource that
	 *         matches the specified parameters
	 */
	private Reference referenceOrganizationByName(String orgName) {
		return new Reference(String.format("Organization?name=" + urlEncode(orgName)));
	}

	/**
	 * @param organizationNpi
	 *            the {@link Organization#getIdentifier()} value to match (where
	 *            {@link Identifier#getSystem()} is
	 *            {@value #CODING_SYSTEM_NPI_US})
	 * @return a {@link Reference} to the {@link Organization} resource that
	 *         matches the specified parameters
	 */
	static Reference referenceOrganizationByNpi(String organizationNpi) {
		return new Reference(String.format("Organization?identifier=%s|%s", CODING_SYSTEM_NPI_US, organizationNpi));
	}

	/**
	 * @param subPlan
	 *            the {@link Coverage#getSubPlan()} value to match
	 * @param subscriberPatientId
	 *            the {@link Patient#getId()} for the
	 *            {@link Coverage#getSubscriber()} value to match
	 * @return a {@link Reference} to the {@link Coverage} resource where
	 *         {@link Coverage#getPlan()} matches {@link #COVERAGE_PLAN} and the
	 *         other parameters specified also match
	 */
	static Reference referenceCoverage(String subscriberPatientId, String subPlan) {
		return new Reference(String.format("Coverage?subscriber=Patient/bene-%s&plan=%s&subplan=%s",
				urlEncode(subscriberPatientId), urlEncode(COVERAGE_PLAN), urlEncode(subPlan)));
	}

	/**
	 * @param patientId
	 *            the {@link Patient#getId()} value to match
	 * @return a {@link Reference} to the {@link Patient} resource that matches
	 *         the specified parameters
	 */
	static Reference referencePatient(String patientId) {
		return new Reference(String.format("Patient/bene-%s", urlEncode(patientId)));
	}

	/**
	 * @param practitionerNpi
	 *            the {@link Practitioner#getIdentifier()} value to match (where
	 *            {@link Identifier#getSystem()} is
	 *            {@value #CODING_SYSTEM_NPI_US})
	 * @return a {@link Reference} to the {@link Practitioner} resource that
	 *         matches the specified parameters
	 */
	static Reference referencePractitioner(String practitionerNpi) {
		return new Reference(String.format("Practitioner?identifier=%s|%s", urlEncode(CODING_SYSTEM_NPI_US),
				urlEncode(practitionerNpi)));
	}

	/**
	 * @param period
	 *            the {@link Period} to adjust
	 * @param date
	 *            the {@link LocalDate} to set the {@link Period#getStart()}
	 *            value with/to
	 */
	private static void setPeriodStart(Period period, LocalDate date) {
		period.setStart(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()), TemporalPrecisionEnum.DAY);
	}

	/**
	 * @param period
	 *            the {@link Period} to adjust
	 * @param date
	 *            the {@link LocalDate} to set the {@link Period#getEnd()} value
	 *            with/to
	 */
	private static void setPeriodEnd(Period period, LocalDate date) {
		period.setEnd(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()), TemporalPrecisionEnum.DAY);
	}

	/**
	 * @param eob
	 *            the {@link ExplanationOfBenefit} to (possibly) modify
	 * @param diagnosis
	 *            the {@link IcdCode} to add, if it's not already present
	 * @return the {@link DiagnosisComponent#getSequence()} of the existing or
	 *         newly-added entry
	 */
	private static int addDiagnosisCode(ExplanationOfBenefit eob, IcdCode diagnosis) {
		Optional<DiagnosisComponent> existingDiagnosis = eob.getDiagnosis().stream()
				.filter(d -> d.getDiagnosis().getSystem().equals(diagnosis.getVersion().getFhirSystem()))
				.filter(d -> d.getDiagnosis().getCode().equals(diagnosis.getCode())).findAny();
		if (existingDiagnosis.isPresent())
			return existingDiagnosis.get().getSequence();

		DiagnosisComponent diagnosisComponent = new DiagnosisComponent().setSequence(eob.getDiagnosis().size());
		diagnosisComponent.getDiagnosis().setSystem(diagnosis.getVersion().getFhirSystem())
				.setCode(diagnosis.getCode());
		eob.getDiagnosis().add(diagnosisComponent);
		return diagnosisComponent.getSequence();
	}

	/**
	 * @param eob
	 *            the {@link ExplanationOfBenefit} that the specified
	 *            {@link ItemsComponent} is a child of
	 * @param item
	 *            the {@link ItemsComponent} to add an
	 *            {@link ItemsComponent#getDiagnosisLinkId()} entry to
	 * @param diagnosis
	 *            the diagnosis code to add a link for
	 */
	private static void addDiagnosisLink(ExplanationOfBenefit eob, ItemsComponent item, IcdCode diagnosis) {
		int diagnosisSequence = addDiagnosisCode(eob, diagnosis);
		item.addDiagnosisLinkId(diagnosisSequence);
	}
}
