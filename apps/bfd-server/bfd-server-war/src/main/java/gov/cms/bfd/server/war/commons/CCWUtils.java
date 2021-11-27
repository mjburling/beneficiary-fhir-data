package gov.cms.bfd.server.war.commons;

import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import gov.cms.bfd.model.codebook.data.CcwCodebookVariable;
import gov.cms.bfd.model.codebook.model.CcwCodebookInterface;
import java.util.HashMap;
import java.util.Map;

/** A set of methods to work with {@link CcwCodebookInterface} instances. */
public class CCWUtils {
  private static final Map<CcwCodebookVariable, String> PART_D_MONTH_MAP =
      new HashMap<CcwCodebookVariable, String>() {
        {
          put(CcwCodebookVariable.PTDCNTRCT01, "01");
          put(CcwCodebookVariable.PTDCNTRCT02, "02");
          put(CcwCodebookVariable.PTDCNTRCT03, "03");
          put(CcwCodebookVariable.PTDCNTRCT04, "04");
          put(CcwCodebookVariable.PTDCNTRCT05, "05");
          put(CcwCodebookVariable.PTDCNTRCT06, "06");
          put(CcwCodebookVariable.PTDCNTRCT07, "07");
          put(CcwCodebookVariable.PTDCNTRCT08, "08");
          put(CcwCodebookVariable.PTDCNTRCT09, "09");
          put(CcwCodebookVariable.PTDCNTRCT10, "10");
          put(CcwCodebookVariable.PTDCNTRCT11, "11");
          put(CcwCodebookVariable.PTDCNTRCT12, "12");
        }
      };

  /**
   * @param ccwVariable the {@link CcwCodebookInterface} being mapped
   * @return the public URL at which documentation for the specified {@link CcwCodebookInterface} is
   *     published
   */
  public static String calculateVariableReferenceUrl(CcwCodebookInterface ccwVariable) {
    return String.format(
        "%s/%s",
        TransformerConstants.BASE_URL_CCW_VARIABLES,
        ccwVariable.getVariable().getId().toLowerCase());
  }

  public static String partDFieldByMonth(CcwCodebookVariable month) throws InvalidRequestException {
    if (!PART_D_MONTH_MAP.containsKey(month)) {
      throw new InvalidRequestException(
          "Unsupported extension system: " + month.getVariable().getId().toLowerCase());
    }
    return PART_D_MONTH_MAP.get(month);
  }

  public static CcwCodebookVariable codebookForSystem(String system)
      throws IllegalArgumentException {
    try {
      return CcwCodebookVariable.valueOf(system.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new InvalidRequestException("Unsupported extension system: " + system);
    }
  }
}
