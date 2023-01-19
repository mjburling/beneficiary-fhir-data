package gov.cms.bfd.server.war.commons;

/** Collection of BlueButton related coding systems. */
public class BBCodingSystems {

  /** The base Blue Button url. */
  public static final String BB_BASE_URL = "https://bluebutton.cms.gov/resources";

  /** Private constructor to prevent instantiation. */
  private BBCodingSystems() {}

  /** FISS targeted BlueButton coding systems. */
  public static class FISS {

    /** The FISS base coding url. */
    private static final String FISS_BASE = BB_BASE_URL + "/variables/fiss";

    /** The full CURR_STATUS. */
    public static final String CURR_STATUS = FISS_BASE + "/curr-status";

    /** The full CURR_TRAN_DT_CYMD. */
    public static final String CURR_TRAN_DT_CYMD = FISS_BASE + "/curr-tran-dt-cymd";

    /** The full payer's name. */
    public static final String PAYERS_NAME = FISS_BASE + "/payers-name";

    /** The full RECD_DT_CYMD. */
    public static final String RECD_DT_CYMD = FISS_BASE + "/recd-dt-cymd";

    /** The full tax number. */
    public static final String TAX_NUM = FISS_BASE + "/fed-tax-nb";

    /** The full DCN. */
    public static final String DCN = FISS_BASE + "/dcn";

    /** The full SERV_TYP_CD. */
    public static final String SERV_TYP_CD = FISS_BASE + "/serv-typ-cd";

    /** The full FREQ_CD. */
    public static final String FREQ_CD = FISS_BASE + "/freq-cd";

    /** The full MEDA_PROV_6. */
    public static final String MEDA_PROV_6 = FISS_BASE + "/meda-prov-6";

    /** The full LOB_CD. */
    public static final String LOB_CD = FISS_BASE + "/lob-cd";

    /** The full DIAG_POA_IND. */
    public static final String DIAG_POA_IND = FISS_BASE + "/diag-poa-ind";

    /** Private constructor to prevent instantiation. */
    private FISS() {}
  }

  /** MCS targeted BlueButton coding systems. */
  public static class MCS {

    /** The MCS base coding url. */
    private static final String MCS_BASE = BB_BASE_URL + "/variables/mcs";

    /** The full bill provider EIN. */
    public static final String BILL_PROV_EIN = MCS_BASE + "/bill-prov-ein";

    /** The full bill provider spec. */
    public static final String BILL_PROV_SPEC = MCS_BASE + "/bill-prov-spec";

    /** The full bill provider type. */
    public static final String BILL_PROV_TYPE = MCS_BASE + "/bill-prov-type";

    /** The full claim receipt date. */
    public static final String CLAIM_RECEIPT_DATE = MCS_BASE + "/claim-receipt-date";

    /** The full claim type. */
    public static final String CLAIM_TYPE = MCS_BASE + "/claim-type";

    /** The full status code. */
    public static final String STATUS_CODE = MCS_BASE + "/status-code";

    /** The full status date. */
    public static final String STATUS_DATE = MCS_BASE + "/status-date";

    /** The full ICN. */
    public static final String ICN = MCS_BASE + "/icn";

    /** Private constructor to prevent instantiation. */
    private MCS() {}
  }
}
