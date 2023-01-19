package gov.cms.bfd.server.war.stu3.providers;

import gov.cms.bfd.model.rif.Beneficiary;
import gov.cms.bfd.model.rif.CarrierClaim;
import gov.cms.bfd.model.rif.CarrierClaim_;
import gov.cms.bfd.model.rif.DMEClaim;
import gov.cms.bfd.model.rif.DMEClaim_;
import gov.cms.bfd.model.rif.HHAClaim;
import gov.cms.bfd.model.rif.HHAClaim_;
import gov.cms.bfd.model.rif.HospiceClaim;
import gov.cms.bfd.model.rif.HospiceClaim_;
import gov.cms.bfd.model.rif.InpatientClaim;
import gov.cms.bfd.model.rif.InpatientClaim_;
import gov.cms.bfd.model.rif.OutpatientClaim;
import gov.cms.bfd.model.rif.OutpatientClaim_;
import gov.cms.bfd.model.rif.PartDEvent;
import gov.cms.bfd.model.rif.PartDEvent_;
import gov.cms.bfd.model.rif.SNFClaim;
import gov.cms.bfd.model.rif.SNFClaim_;
import gov.cms.bfd.server.war.commons.ClaimTypeTransformer;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;
import org.hl7.fhir.dstu3.model.ExplanationOfBenefit;

/**
 * Enumerates the various Blue Button claim types that are supported by {@link
 * ExplanationOfBenefitResourceProvider}.
 */
public enum ClaimType {
  /** Represents the carrier claim type. */
  CARRIER(
      CarrierClaim.class,
      CarrierClaim_.claimId,
      CarrierClaim_.beneficiaryId,
      (entity) -> ((CarrierClaim) entity).getDateThrough(),
      CarrierClaimTransformer::transform,
      CarrierClaim_.lines),
  /** Represents the DME claim type. */
  DME(
      DMEClaim.class,
      DMEClaim_.claimId,
      DMEClaim_.beneficiaryId,
      (entity) -> ((DMEClaim) entity).getDateThrough(),
      DMEClaimTransformer::transform,
      DMEClaim_.lines),
  /** Represents the hha claim type. */
  HHA(
      HHAClaim.class,
      HHAClaim_.claimId,
      HHAClaim_.beneficiaryId,
      (entity) -> ((HHAClaim) entity).getDateThrough(),
      HHAClaimTransformer::transform,
      HHAClaim_.lines),
  /** Represents the hospice claim type. */
  HOSPICE(
      HospiceClaim.class,
      HospiceClaim_.claimId,
      HospiceClaim_.beneficiaryId,
      (entity) -> ((HospiceClaim) entity).getDateThrough(),
      HospiceClaimTransformer::transform,
      HospiceClaim_.lines),
  /** Represents the inpatient claim type. */
  INPATIENT(
      InpatientClaim.class,
      InpatientClaim_.claimId,
      InpatientClaim_.beneficiaryId,
      (entity) -> ((InpatientClaim) entity).getDateThrough(),
      InpatientClaimTransformer::transform,
      InpatientClaim_.lines),
  /** Represents the outpatient claim type. */
  OUTPATIENT(
      OutpatientClaim.class,
      OutpatientClaim_.claimId,
      OutpatientClaim_.beneficiaryId,
      (entity) -> ((OutpatientClaim) entity).getDateThrough(),
      OutpatientClaimTransformer::transform,
      OutpatientClaim_.lines),
  /** Represents the PDE claim type. */
  PDE(
      PartDEvent.class,
      PartDEvent_.eventId,
      PartDEvent_.beneficiaryId,
      (entity) -> ((PartDEvent) entity).getPrescriptionFillDate(),
      PartDEventTransformer::transform),
  /** Represents the SNF claim type. */
  SNF(
      SNFClaim.class,
      SNFClaim_.claimId,
      SNFClaim_.beneficiaryId,
      (entity) -> ((SNFClaim) entity).getDateThrough(),
      SNFClaimTransformer::transform,
      SNFClaim_.lines);

  /** The entity class. */
  private final Class<?> entityClass;
  /** The entity id attribute. */
  private final SingularAttribute<?, Long> entityIdAttribute;
  /** The entity beneficiary id attribute. */
  private final SingularAttribute<?, Long> entityBeneficiaryIdAttribute;
  /** The service end attribute function. */
  private final Function<Object, LocalDate> serviceEndAttributeFunction;
  /** The claim transformer. */
  private final ClaimTypeTransformer transformer;
  /** The entity lazy attributes. */
  private final Collection<PluralAttribute<?, ?, ?>> entityLazyAttributes;

  /**
   * Enum constant constructor.
   *
   * @param entityClass the value to use for {@link #getEntityClass()}
   * @param entityIdAttribute the value to use for {@link #getEntityIdAttribute()}
   * @param entityBeneficiaryIdAttribute the value to use for {@link
   *     #getEntityBeneficiaryIdAttribute()}
   * @param serviceEndAttributeFunction the service end attribute function
   * @param transformer the value to use for {@link #getTransformer()}
   * @param entityLazyAttributes the value to use for {@link #getEntityLazyAttributes()}
   */
  private ClaimType(
      Class<?> entityClass,
      SingularAttribute<?, Long> entityIdAttribute,
      SingularAttribute<?, Long> entityBeneficiaryIdAttribute,
      Function<Object, LocalDate> serviceEndAttributeFunction,
      ClaimTypeTransformer transformer,
      PluralAttribute<?, ?, ?>... entityLazyAttributes) {
    this.entityClass = entityClass;
    this.entityIdAttribute = entityIdAttribute;
    this.entityBeneficiaryIdAttribute = entityBeneficiaryIdAttribute;
    this.serviceEndAttributeFunction = serviceEndAttributeFunction;
    this.transformer = transformer;
    this.entityLazyAttributes =
        entityLazyAttributes != null
            ? Collections.unmodifiableCollection(Arrays.asList(entityLazyAttributes))
            : Collections.emptyList();
  }

  /**
   * Gets the {@link #entityClass}.
   *
   * @return the JPA {@link Entity} {@link Class} used to store instances of this {@link ClaimType}
   *     in the database
   */
  public Class<?> getEntityClass() {
    return entityClass;
  }

  /**
   * Gets the {@link #entityIdAttribute}.
   *
   * @return the JPA {@link Entity} field used as the entity's {@link Id}
   */
  public SingularAttribute<?, Long> getEntityIdAttribute() {
    return entityIdAttribute;
  }

  /**
   * Gets the {@link #entityBeneficiaryIdAttribute}.
   *
   * @return the JPA {@link Entity} field that is a (foreign keyed) reference to {@link
   *     Beneficiary#getBeneficiaryId()}
   */
  public SingularAttribute<?, Long> getEntityBeneficiaryIdAttribute() {
    return entityBeneficiaryIdAttribute;
  }

  /**
   * Gets the {@link #serviceEndAttributeFunction}.
   *
   * @return the {@link Function} to use to retrieve the {@link LocalDate} to use for service date
   *     filter
   */
  public Function<Object, LocalDate> getServiceEndAttributeFunction() {
    return serviceEndAttributeFunction;
  }

  /**
   * Gets the {@link #transformer}.
   *
   * @return the {@link ClaimTypeTransformer} to use to transform the JPA {@link Entity} instances
   *     into FHIR {@link ExplanationOfBenefit} instances
   */
  public ClaimTypeTransformer getTransformer() {
    return transformer;
  }

  /**
   * Gets the {@link #entityLazyAttributes}.
   *
   * @return the {@link PluralAttribute}s in the JPA {@link Entity} that are {@link FetchType#LAZY}
   */
  public Collection<PluralAttribute<?, ?, ?>> getEntityLazyAttributes() {
    return entityLazyAttributes;
  }

  /**
   * Gets the claim type that matches the specified claim type text, if any.
   *
   * @param claimTypeText the lower-cased {@link ClaimType#name()} value to parse back into a {@link
   *     ClaimType}
   * @return the {@link ClaimType} represented by the specified {@link String}
   */
  public static Optional<ClaimType> parse(String claimTypeText) {
    for (ClaimType claimType : ClaimType.values()) {
      if (claimType.name().toLowerCase().equals(claimTypeText)) {
        return Optional.of(claimType);
      }
    }
    return Optional.empty();
  }
}
