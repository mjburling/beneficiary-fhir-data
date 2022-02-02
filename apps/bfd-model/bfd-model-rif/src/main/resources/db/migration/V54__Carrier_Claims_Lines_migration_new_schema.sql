create table if not exists carrier_claim_lines_bfd1485 (
    clm_id                                   bigint not null,                          -- parentClaim
    line_num                                 smallint not null,                        -- lineNumber
    line_nch_pmt_amt                         numeric(10,2) not null,                   -- paymentAmount
    line_1st_expns_dt                        date,                                     -- firstExpenseDate
    line_alowd_chrg_amt                      numeric(10,2) not null,                   -- allowedChargeAmount
    line_bene_pmt_amt                        numeric(10,2) not null,                   -- beneficiaryPaymentAmount
    line_bene_prmry_pyr_cd                   character(1),                             -- primaryPayerCode
    line_bene_prmry_pyr_pd_amt               numeric(10,2) not null,                   -- primaryPayerPaidAmount
    line_bene_ptb_ddctbl_amt                 numeric(10,2) not null,                   -- beneficiaryPartBDeductAmount
    line_cms_type_srvc_cd                    character(1) not null,                    -- cmsServiceTypeCode
    line_coinsrnc_amt                        numeric(10,2) not null,                   -- coinsuranceAmount
    line_hct_hgb_rslt_num                    numeric(4,1) not null,                    -- hctHgbTestResult
    line_hct_hgb_type_cd                     character varying(2),                     -- hctHgbTestTypeCode
    line_icd_dgns_cd                         character varying(7),                     -- diagnosisCode
    line_icd_dgns_vrsn_cd                    character(1),                             -- diagnosisCodeVersion
    line_last_expns_dt                       date,                                     -- lastExpenseDate
    line_ndc_cd                              character varying(11),                    -- nationalDrugCode
    line_place_of_srvc_cd                    character varying(2) not null,            -- placeOfServiceCode
    line_pmt_80_100_cd                       character(1),                             -- paymentCode
    line_prvdr_pmt_amt                       numeric(10,2) not null,                   -- providerPaymentAmount
    line_prcsg_ind_cd                        character varying(2),                     -- processingIndicatorCode
    line_sbmtd_chrg_amt                      numeric(10,2) not null,                   -- submittedChargeAmount
    line_service_deductible                  character(1),                             -- serviceDeductibleCode
    line_srvc_cnt                            smallint not null,                        -- serviceCount
    carr_line_mtus_cd                        character(1),                             -- mtusCode
    carr_line_mtus_cnt                       integer not null,                         -- mtusCount
    betos_cd                                 character varying(3),                     -- betosCode
    carr_line_ansthsa_unit_cnt               smallint not null,                        -- anesthesiaUnitCount
    carr_line_clia_lab_num                   character varying(10),                    -- cliaLabNumber
    carr_line_prcng_lclty_cd                 character varying(2) not null,            -- linePricingLocalityCode
    carr_line_prvdr_type_cd                  character(1) not null,                    -- providerTypeCode
    carr_line_rdcd_pmt_phys_astn_c           character(1) not null,                    -- reducedPaymentPhysicianAsstCode
    carr_line_rx_num                         character varying(30),                    -- rxNumber
    carr_prfrng_pin_num                      character varying(15) not null,           -- performingProviderIdNumber
    hcpcs_1st_mdfr_cd                        character varying(5),                     -- hcpcsInitialModifierCode
    hcpcs_2nd_mdfr_cd                        character varying(5),                     -- hcpcsSecondModifierCode
    hcpcs_cd                                 character varying(5),                     -- hcpcsCode
    hpsa_scrcty_ind_cd                       character(1),                             -- hpsaScarcityCode
    org_npi_num                              character varying(10),                    -- organizationNpi
    prf_physn_npi                            character varying(12),                    -- performingPhysicianNpi
    prf_physn_upin                           character varying(12),                    -- performingPhysicianUpin
    prtcptng_ind_cd                          character(1),                             -- providerParticipatingIndCode
    prvdr_spclty                             character varying(3),                     -- providerSpecialityCode
    prvdr_state_cd                           character varying(2),                     -- providerStateCode
    prvdr_zip                                character varying(9),                     -- providerZipCode
    tax_num                                  character varying(10) not null,           -- providerTaxNumber  
/*
    constraint carrier_claim_lines_bfd1485_pkey
        primary key (clm_id, line_num),

    constraint carrier_claim_lines_clm_id_to_carrier_claims_bfd1485
        foreign key (clm_id) references carrier_claims_bfd1485(clm_id)
*/
);