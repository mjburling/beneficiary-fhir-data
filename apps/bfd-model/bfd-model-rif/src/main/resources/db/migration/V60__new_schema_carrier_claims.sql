-- flyway migration for carreir_claims and carrier_claim_lines
-- HSQL differs from PSQL (postgres) in that the table defintion
-- is declared prior to loading data into the tabke. PSQL can
-- derive the table column structure based on the data input
-- (i.e., column name, data type). Thus, for HSQL we will
-- explicitly define the table structure prior to loading data.
--
${logic.hsql-only} create table public.bfd1485_carrier_claims (
${logic.hsql-only} 	clm_id                                   bigint not null,                          
${logic.hsql-only} 	bene_id                                  bigint not null,                          
${logic.hsql-only} 	clm_grp_id                               bigint not null,                          
${logic.hsql-only} 	last_updated                             timestamp with time zone,                 
${logic.hsql-only} 	clm_from_dt                              date not null,                            
${logic.hsql-only} 	clm_thru_dt                              date not null,                            
${logic.hsql-only} 	clm_clncl_tril_num                       varchar(8),                     
${logic.hsql-only} 	clm_disp_cd                              varchar(2) not null,            
${logic.hsql-only} 	clm_pmt_amt                              numeric(10,2) not null,                   
${logic.hsql-only} 	carr_clm_cntl_num                        varchar(23),                    
${logic.hsql-only} 	carr_clm_entry_cd                        char(1) not null,                    
${logic.hsql-only} 	carr_clm_hcpcs_yr_cd                     char(1),                             
${logic.hsql-only} 	carr_clm_pmt_dnl_cd                      varchar(2) not null,            
${logic.hsql-only} 	carr_clm_prvdr_asgnmt_ind_sw             char(1),                             
${logic.hsql-only} 	carr_clm_rfrng_pin_num                   varchar(14) not null, 
${logic.hsql-only} 	carr_clm_cash_ddctbl_apld_amt            numeric(10,2) not null,
${logic.hsql-only} 	carr_clm_prmry_pyr_pd_amt                numeric(10,2) not null, 
${logic.hsql-only} 	carr_num                                 varchar(5) not null,            
${logic.hsql-only} 	final_action                             char(1) not null,                    
${logic.hsql-only} 	nch_carr_clm_alowd_amt                   numeric(10,2) not null,                   
${logic.hsql-only} 	nch_carr_clm_sbmtd_chrg_amt              numeric(10,2) not null,                   
${logic.hsql-only} 	nch_clm_bene_pmt_amt                     numeric(10,2) not null,                   
${logic.hsql-only} 	nch_clm_prvdr_pmt_amt                    numeric(10,2) not null,                       
${logic.hsql-only} 	nch_clm_type_cd                          varchar(2) not null,            
${logic.hsql-only} 	nch_near_line_rec_ident_cd               char(1) not null,                                
${logic.hsql-only} 	nch_wkly_proc_dt                         date not null,                            
${logic.hsql-only} 	prncpal_dgns_cd                          varchar(7),                     
${logic.hsql-only} 	prncpal_dgns_vrsn_cd                     char(1),                             
${logic.hsql-only} 	rfr_physn_npi                            varchar(12),                    
${logic.hsql-only} 	rfr_physn_upin                           varchar(12),                    
${logic.hsql-only} 	icd_dgns_cd1                             varchar(7),                     
${logic.hsql-only} 	icd_dgns_cd2                             varchar(7),                     
${logic.hsql-only} 	icd_dgns_cd3                             varchar(7),                     
${logic.hsql-only} 	icd_dgns_cd4                             varchar(7),                     
${logic.hsql-only} 	icd_dgns_cd5                             varchar(7),                     
${logic.hsql-only} 	icd_dgns_cd6                             varchar(7),                     
${logic.hsql-only} 	icd_dgns_cd7                             varchar(7),                     
${logic.hsql-only} 	icd_dgns_cd8                             varchar(7),                     
${logic.hsql-only} 	icd_dgns_cd9                             varchar(7),                     
${logic.hsql-only} 	icd_dgns_cd10                            varchar(7),                     
${logic.hsql-only} 	icd_dgns_cd11                            varchar(7),                     
${logic.hsql-only} 	icd_dgns_cd12                            varchar(7),                     
${logic.hsql-only} 	icd_dgns_vrsn_cd1                        char(1),                             
${logic.hsql-only} 	icd_dgns_vrsn_cd2                        char(1),                             
${logic.hsql-only} 	icd_dgns_vrsn_cd3                        char(1),                             
${logic.hsql-only} 	icd_dgns_vrsn_cd4                        char(1),                             
${logic.hsql-only} 	icd_dgns_vrsn_cd5                        char(1),                             
${logic.hsql-only} 	icd_dgns_vrsn_cd6                        char(1),                             
${logic.hsql-only} 	icd_dgns_vrsn_cd7                        char(1),                             
${logic.hsql-only} 	icd_dgns_vrsn_cd8                        char(1),                             
${logic.hsql-only} 	icd_dgns_vrsn_cd9                        char(1),                             
${logic.hsql-only} 	icd_dgns_vrsn_cd10                       char(1),                             
${logic.hsql-only} 	icd_dgns_vrsn_cd11                       char(1),                             
${logic.hsql-only} 	icd_dgns_vrsn_cd12                       char(1), 
${logic.hsql-only} 	constraint public.bfd1485_carrier_claims_pkey
${logic.hsql-only} 	primary key (clm_id) );

${logic.hsql-only} CREATE TABLE IF NOT EXISTS public.bfd1485_carrier_claim_lines (
${logic.hsql-only} clm_id				 			bigint not null,
${logic.hsql-only} line_num				 			smallint not null,
${logic.hsql-only} line_nch_pmt_amt					numeric(10,2) not null,
${logic.hsql-only} line_1st_expns_dt 				date,
${logic.hsql-only} line_alowd_chrg_amt				numeric(10,2) not null,
${logic.hsql-only} line_bene_pmt_amt				numeric(10,2) not null,
${logic.hsql-only} line_bene_prmry_pyr_cd			character(1),
${logic.hsql-only} line_bene_prmry_pyr_pd_amt		numeric(10,2) not null,
${logic.hsql-only} line_bene_ptb_ddctbl_amt			numeric(10,2) not null,
${logic.hsql-only} line_cms_type_srvc_cd			character(1) not null,
${logic.hsql-only} line_coinsrnc_amt				numeric(10,2) not null,
${logic.hsql-only} line_hct_hgb_rslt_num			numeric(4,1) not null,
${logic.hsql-only} line_hct_hgb_type_cd				character varying(2),
${logic.hsql-only} line_icd_dgns_cd					character varying(7),
${logic.hsql-only} line_icd_dgns_vrsn_cd			character(1),
${logic.hsql-only} line_last_expns_dt 				date,
${logic.hsql-only} line_ndc_cd				  		character varying(11),
${logic.hsql-only} line_place_of_srvc_cd			character varying(2) not null,
${logic.hsql-only} line_pmt_80_100_cd				character(1),
${logic.hsql-only} line_prcsg_ind_cd				character varying(2),
${logic.hsql-only} line_sbmtd_chrg_amt				numeric(10,2) not null,
${logic.hsql-only} line_prvdr_pmt_amt				numeric(10,2) not null,
${logic.hsql-only} line_service_deductible			character(1),
${logic.hsql-only} line_srvc_cnt				 	smallint not null,
${logic.hsql-only} carr_line_mtus_cd				character(1),
${logic.hsql-only} carr_line_mtus_cnt				integer not null,
${logic.hsql-only} betos_cd				  			character varying(3),
${logic.hsql-only} carr_line_ansthsa_unit_cnt		smallint,
${logic.hsql-only} carr_line_clia_lab_num			character varying(10),
${logic.hsql-only} carr_line_prcng_lclty_cd			character varying(2) not null,
${logic.hsql-only} carr_line_prvdr_type_cd			character(1),
${logic.hsql-only} carr_line_rdcd_pmt_phys_astn_c	character(1),
${logic.hsql-only} carr_line_rx_num					character varying(30),
${logic.hsql-only} carr_prfrng_pin_num				character varying(15) not null,
${logic.hsql-only} hcpcs_1st_mdfr_cd				character varying(5),
${logic.hsql-only} hcpcs_2nd_mdfr_cd				character varying(5),
${logic.hsql-only} hcpcs_cd				  			character varying(5),
${logic.hsql-only} hpsa_scrcty_ind_cd				character(1),
${logic.hsql-only} org_npi_num				  		character varying(10),
${logic.hsql-only} prf_physn_npi				  	character varying(12),
${logic.hsql-only} prf_physn_upin				  	character varying(12),
${logic.hsql-only} prtcptng_ind_cd				  	character(1),
${logic.hsql-only} prvdr_spclty				  		character varying(3),
${logic.hsql-only} prvdr_state_cd				  	character varying(2),
${logic.hsql-only} prvdr_zip				  		character varying(9),
${logic.hsql-only} tax_num				  			character varying(10),
${logic.hsql-only} constraint public.bfd1485_carrier_claim_lines_pkey
${logic.hsql-only}   primary key (clm_id, line_num));

${logic.psql-only} SET max_parallel_workers = 24;
${logic.psql-only} SET max_parallel_workers_per_gather = 20;
${logic.psql-only} SET parallel_leader_participation = off;
${logic.psql-only} SET parallel_tuple_cost = 0;
${logic.psql-only} SET parallel_setup_cost = 0;
${logic.psql-only} SET min_parallel_table_scan_size = 0;

${logic.hsql-only} insert into public.bfd1485_carrier_claims(
${logic.hsql-only} 	clm_id,
${logic.hsql-only} 	bene_id,
${logic.hsql-only} 	clm_grp_id,
${logic.hsql-only} 	last_updated,
${logic.hsql-only} 	clm_from_dt,
${logic.hsql-only} 	clm_thru_dt,
${logic.hsql-only} 	clm_clncl_tril_num,
${logic.hsql-only} 	clm_disp_cd,
${logic.hsql-only} 	clm_pmt_amt,
${logic.hsql-only} 	carr_clm_cntl_num,
${logic.hsql-only} 	carr_clm_entry_cd,
${logic.hsql-only} 	carr_clm_hcpcs_yr_cd,
${logic.hsql-only} 	carr_clm_pmt_dnl_cd,
${logic.hsql-only} 	carr_clm_prvdr_asgnmt_ind_sw,
${logic.hsql-only} 	carr_clm_rfrng_pin_num,
${logic.hsql-only} 	carr_clm_cash_ddctbl_apld_amt,
${logic.hsql-only} 	carr_clm_prmry_pyr_pd_amt,
${logic.hsql-only} 	carr_num,
${logic.hsql-only} 	final_action,
${logic.hsql-only} 	nch_carr_clm_alowd_amt,
${logic.hsql-only} 	nch_carr_clm_sbmtd_chrg_amt,
${logic.hsql-only} 	nch_clm_bene_pmt_amt,
${logic.hsql-only} 	nch_clm_prvdr_pmt_amt,
${logic.hsql-only} 	nch_clm_type_cd,
${logic.hsql-only} 	nch_near_line_rec_ident_cd,
${logic.hsql-only} 	nch_wkly_proc_dt,
${logic.hsql-only} 	prncpal_dgns_cd,
${logic.hsql-only} 	prncpal_dgns_vrsn_cd,
${logic.hsql-only} 	rfr_physn_npi,
${logic.hsql-only} 	rfr_physn_upin,
${logic.hsql-only} 	icd_dgns_cd1,
${logic.hsql-only} 	icd_dgns_cd2,
${logic.hsql-only} 	icd_dgns_cd3,
${logic.hsql-only} 	icd_dgns_cd4,
${logic.hsql-only} 	icd_dgns_cd5,
${logic.hsql-only} 	icd_dgns_cd6,
${logic.hsql-only} 	icd_dgns_cd7,
${logic.hsql-only} 	icd_dgns_cd8,
${logic.hsql-only} 	icd_dgns_cd9,
${logic.hsql-only} 	icd_dgns_cd10,
${logic.hsql-only} 	icd_dgns_cd11,
${logic.hsql-only} 	icd_dgns_cd12,
${logic.hsql-only} 	icd_dgns_vrsn_cd1,
${logic.hsql-only} 	icd_dgns_vrsn_cd2,
${logic.hsql-only} 	icd_dgns_vrsn_cd3,
${logic.hsql-only} 	icd_dgns_vrsn_cd4,
${logic.hsql-only} 	icd_dgns_vrsn_cd5,
${logic.hsql-only} 	icd_dgns_vrsn_cd6,
${logic.hsql-only} 	icd_dgns_vrsn_cd7,
${logic.hsql-only} 	icd_dgns_vrsn_cd8,
${logic.hsql-only} 	icd_dgns_vrsn_cd9,
${logic.hsql-only} 	icd_dgns_vrsn_cd10,
${logic.hsql-only} 	icd_dgns_vrsn_cd11,
${logic.hsql-only} 	icd_dgns_vrsn_cd12 )
${logic.psql-only} create table public.bfd1485_carrier_claims as
select
	${logic.psql-only} cast(clm_id as bigint),
	${logic.psql-only} cast(bene_id as bigint),
	${logic.psql-only} cast(clm_grp_id as bigint),  
    ${logic.hsql-only} convert(clm_id, SQL_BIGINT),
	${logic.hsql-only} convert(bene_id, SQL_BIGINT),
	${logic.hsql-only} convert(clm_grp_id, SQL_BIGINT),
	last_updated,
	clm_from_dt,
	clm_thru_dt,
	clm_clncl_tril_num,
	clm_disp_cd,
	${logic.hsql-only} clm_pmt_amt,
	${logic.psql-only} cast(clm_pmt_amt as numeric(10,2)),
	carr_clm_cntl_num,
	carr_clm_entry_cd,
	carr_clm_hcpcs_yr_cd,
	carr_clm_pmt_dnl_cd,
	carr_clm_prvdr_asgnmt_ind_sw,
	carr_clm_rfrng_pin_num,
	${logic.hsql-only} carr_clm_cash_ddctbl_apld_amt,
	${logic.psql-only} cast(carr_clm_cash_ddctbl_apld_amt as numeric(10,2)),
	${logic.hsql-only} carr_clm_prmry_pyr_pd_amt,
	${logic.psql-only} cast(carr_clm_prmry_pyr_pd_amt as numeric(10,2)),
	carr_num,
	final_action,
	${logic.hsql-only} nch_carr_clm_alowd_amt,
	${logic.psql-only} cast(nch_carr_clm_alowd_amt as numeric(10,2)),
	${logic.hsql-only} nch_carr_clm_sbmtd_chrg_amt,
	${logic.psql-only} cast(nch_carr_clm_sbmtd_chrg_amt as numeric(10,2)),
	${logic.hsql-only} nch_clm_bene_pmt_amt,
	${logic.psql-only} cast(nch_clm_bene_pmt_amt as numeric(10,2)),
	${logic.hsql-only} nch_clm_prvdr_pmt_amt,
	${logic.psql-only} cast(nch_clm_prvdr_pmt_amt as numeric(10,2)),
	nch_clm_type_cd,
	nch_near_line_rec_ident_cd,
	nch_wkly_proc_dt,
	prncpal_dgns_cd,
	prncpal_dgns_vrsn_cd,
	rfr_physn_npi,
	rfr_physn_upin,
	icd_dgns_cd1,
	icd_dgns_cd2,
	icd_dgns_cd3,
	icd_dgns_cd4,
	icd_dgns_cd5,
	icd_dgns_cd6,
	icd_dgns_cd7,
	icd_dgns_cd8,
	icd_dgns_cd9,
	icd_dgns_cd10,
	icd_dgns_cd11,
	icd_dgns_cd12,
	icd_dgns_vrsn_cd1,
	icd_dgns_vrsn_cd2,
	icd_dgns_vrsn_cd3,
	icd_dgns_vrsn_cd4,
	icd_dgns_vrsn_cd5,
	icd_dgns_vrsn_cd6,
	icd_dgns_vrsn_cd7,
	icd_dgns_vrsn_cd8,
	icd_dgns_vrsn_cd9,
	icd_dgns_vrsn_cd10,
	icd_dgns_vrsn_cd11,
	icd_dgns_vrsn_cd12
from
	public.carrier_claims;

${logic.psql-only} alter table public.bfd1485_carrier_claims
${logic.psql-only}     alter column clm_id SET NOT NULL,
${logic.psql-only}     alter column nch_carr_clm_alowd_amt SET NOT NULL,
${logic.psql-only}     alter column bene_id SET NOT NULL,
${logic.psql-only}     alter column carr_clm_cash_ddctbl_apld_amt SET NOT NULL,
${logic.psql-only}     alter column nch_clm_bene_pmt_amt SET NOT NULL,
${logic.psql-only}     alter column carr_num SET NOT NULL,
${logic.psql-only}     alter column clm_disp_cd SET NOT NULL,
${logic.psql-only}     alter column carr_clm_entry_cd SET NOT NULL,
${logic.psql-only}     alter column clm_grp_id SET NOT NULL,
${logic.psql-only}     alter column nch_clm_type_cd SET NOT NULL,
${logic.psql-only}     alter column clm_from_dt SET NOT NULL,
${logic.psql-only}     alter column clm_thru_dt SET NOT NULL,
${logic.psql-only}     alter column nch_near_line_rec_ident_cd SET NOT NULL,
${logic.psql-only}     alter column clm_pmt_amt SET NOT NULL,
${logic.psql-only}     alter column carr_clm_pmt_dnl_cd SET NOT NULL,
${logic.psql-only}     alter column carr_clm_prmry_pyr_pd_amt SET NOT NULL,
${logic.psql-only}     alter column nch_clm_prvdr_pmt_amt SET NOT NULL,
${logic.psql-only}     alter column carr_clm_rfrng_pin_num SET NOT NULL,
${logic.psql-only}     alter column nch_carr_clm_sbmtd_chrg_amt SET NOT NULL,
${logic.psql-only}     alter column nch_wkly_proc_dt SET NOT NULL,
${logic.psql-only}     alter column final_action SET NOT NULL;

${logic.hsql-only} insert into public.bfd1485_carrier_claim_lines(
${logic.hsql-only} clm_id,
${logic.hsql-only} line_num,
${logic.hsql-only} line_nch_pmt_amt,
${logic.hsql-only} line_1st_expns_dt,
${logic.hsql-only} line_alowd_chrg_amt,
${logic.hsql-only} line_bene_pmt_amt,
${logic.hsql-only} line_bene_prmry_pyr_cd,
${logic.hsql-only} line_bene_prmry_pyr_pd_amt,
${logic.hsql-only} line_bene_ptb_ddctbl_amt,
${logic.hsql-only} line_cms_type_srvc_cd,
${logic.hsql-only} line_coinsrnc_amt,
${logic.hsql-only} line_hct_hgb_rslt_num,
${logic.hsql-only} line_hct_hgb_type_cd,
${logic.hsql-only} line_icd_dgns_cd,
${logic.hsql-only} line_icd_dgns_vrsn_cd,
${logic.hsql-only} line_last_expns_dt,
${logic.hsql-only} line_ndc_cd,
${logic.hsql-only} line_place_of_srvc_cd,
${logic.hsql-only} line_pmt_80_100_cd,
${logic.hsql-only} line_prcsg_ind_cd,
${logic.hsql-only} line_sbmtd_chrg_amt,
${logic.hsql-only} line_prvdr_pmt_amt,
${logic.hsql-only} line_service_deductible,
${logic.hsql-only} line_srvc_cnt,
${logic.hsql-only} carr_line_mtus_cd,
${logic.hsql-only} carr_line_mtus_cnt,
${logic.hsql-only} betos_cd,
${logic.hsql-only} carr_line_ansthsa_unit_cnt,
${logic.hsql-only} carr_line_clia_lab_num,
${logic.hsql-only} carr_line_prcng_lclty_cd,
${logic.hsql-only} carr_line_prvdr_type_cd,
${logic.hsql-only} carr_line_rdcd_pmt_phys_astn_c,
${logic.hsql-only} carr_line_rx_num,
${logic.hsql-only} carr_prfrng_pin_num,
${logic.hsql-only} hcpcs_1st_mdfr_cd,
${logic.hsql-only} hcpcs_2nd_mdfr_cd,
${logic.hsql-only} hcpcs_cd,
${logic.hsql-only} hpsa_scrcty_ind_cd,
${logic.hsql-only} org_npi_num,
${logic.hsql-only} prf_physn_npi,
${logic.hsql-only} prf_physn_upin,
${logic.hsql-only} prtcptng_ind_cd,
${logic.hsql-only} prvdr_spclty,
${logic.hsql-only} prvdr_state_cd,
${logic.hsql-only} prvdr_zip,
${logic.hsql-only} tax_num )
${logic.psql-only} create table public.bfd1485_carrier_claim_lines as
select
	${logic.psql-only} cast(clm_id as bigint),  
    ${logic.hsql-only} convert(clm_id, SQL_BIGINT),
	${logic.psql-only} cast(line_num as smallint),
	${logic.hsql-only} convert(line_num, SQL_SMALLINT),
	${logic.psql-only} cast(line_nch_pmt_amt as numeric(10,2)),
	${logic.hsql-only} line_nch_pmt_amt,
	line_1st_expns_dt,
	${logic.psql-only} cast(line_alowd_chrg_amt as numeric(10,2)),
	${logic.hsql-only} line_alowd_chrg_amt,
	${logic.psql-only} cast(line_bene_pmt_amt as numeric(10,2)),
	${logic.hsql-only} line_bene_pmt_amt,
	line_bene_prmry_pyr_cd,
	${logic.psql-only} cast(line_bene_prmry_pyr_pd_amt as numeric(10,2)),
	${logic.hsql-only} line_bene_prmry_pyr_pd_amt,
	${logic.psql-only} cast(line_bene_ptb_ddctbl_amt as numeric(10,2)),
	${logic.hsql-only} line_bene_ptb_ddctbl_amt,
	line_cms_type_srvc_cd,
	${logic.psql-only} cast(line_coinsrnc_amt as numeric(10,2)),
	${logic.hsql-only} line_coinsrnc_amt,
	${logic.psql-only} cast(line_hct_hgb_rslt_num as numeric(4,1)),
	${logic.hsql-only} line_hct_hgb_rslt_num,
	line_hct_hgb_type_cd,
	line_icd_dgns_cd,
	line_icd_dgns_vrsn_cd,
	line_last_expns_dt,
	line_ndc_cd,
	line_place_of_srvc_cd,
	line_pmt_80_100_cd,
	line_prcsg_ind_cd,
	${logic.psql-only} cast(line_sbmtd_chrg_amt as numeric(10,2)),
	${logic.hsql-only} line_sbmtd_chrg_amt,
	${logic.psql-only} cast(line_prvdr_pmt_amt as numeric(10,2)),
	${logic.hsql-only} line_prvdr_pmt_amt,
	line_service_deductible,
	${logic.psql-only} cast(line_srvc_cnt as smallint),
	${logic.hsql-only} convert(line_srvc_cnt, SQL_SMALLINT),
	carr_line_mtus_cd,
	${logic.psql-only} cast(carr_line_mtus_cnt as integer),
	${logic.hsql-only} carr_line_mtus_cnt,
	betos_cd,
	${logic.psql-only} cast(carr_line_ansthsa_unit_cnt as smallint),
	${logic.hsql-only} carr_line_ansthsa_unit_cnt,
	carr_line_clia_lab_num,
	carr_line_prcng_lclty_cd,
	carr_line_prvdr_type_cd ,
	carr_line_rdcd_pmt_phys_astn_c,
	carr_line_rx_num,
	carr_prfrng_pin_num,
	hcpcs_1st_mdfr_cd,
	hcpcs_2nd_mdfr_cd,
	hcpcs_cd,
	hpsa_scrcty_ind_cd,
	org_npi_num,
	prf_physn_npi,
	prf_physn_upin,
	prtcptng_ind_cd,
	prvdr_spclty,
	prvdr_state_cd,
	prvdr_zip,
	tax_num
from
	public.carrier_claim_lines;

${logic.psql-only} alter table public.bfd1485_carrier_claim_lines
${logic.psql-only}    alter column clm_id SET NOT NULL,
${logic.psql-only}    alter column line_num	SET NOT NULL,
${logic.psql-only}    alter column line_nch_pmt_amt SET NOT NULL,
${logic.psql-only}    alter column line_alowd_chrg_amt SET NOT NULL,
${logic.psql-only}    alter column line_bene_pmt_amt SET NOT NULL,
${logic.psql-only}    alter column line_bene_prmry_pyr_pd_amt SET NOT NULL,
${logic.psql-only}    alter column line_bene_ptb_ddctbl_amt SET NOT NULL,
${logic.psql-only}    alter column line_cms_type_srvc_cd SET NOT NULL,
${logic.psql-only}    alter column line_coinsrnc_amt SET NOT NULL,
${logic.psql-only}    alter column line_hct_hgb_rslt_num SET NOT NULL,
${logic.psql-only}    alter column line_place_of_srvc_cd SET NOT NULL,
${logic.psql-only}    alter column line_sbmtd_chrg_amt SET NOT NULL,
${logic.psql-only}    alter column line_prvdr_pmt_amt SET NOT NULL,
${logic.psql-only}    alter column line_srvc_cnt SET NOT NULL,
${logic.psql-only}    alter column carr_line_mtus_cnt SET NOT NULL,
${logic.psql-only}    alter column carr_line_prcng_lclty_cd	SET NOT NULL,
${logic.psql-only}    alter column carr_prfrng_pin_num SET NOT NULL;

${logic.psql-only} alter table public.bfd1485_carrier_claims
${logic.psql-only}     add CONSTRAINT bfd1485_carrier_claims_pkey PRIMARY KEY (clm_id);

${logic.psql-only} alter table public.bfd1485_carrier_claim_lines
${logic.psql-only}     add CONSTRAINT bfd1485_carrier_claim_lines_pkey PRIMARY KEY (clm_id, line_num);

ALTER TABLE IF EXISTS public.bfd1485_carrier_claim_lines
    ADD CONSTRAINT bfd1485_carrier_claim_lines_clm_id_to_carrier_claims FOREIGN KEY (clm_id)
    	REFERENCES public.bfd1485_carrier_claims (clm_id);

CREATE INDEX IF NOT EXISTS bfd1485_carrier_claims_bene_id_idx
	ON public.bfd1485_carrier_claims (bene_id);

ALTER TABLE IF EXISTS public.bfd1485_carrier_claims
    ADD CONSTRAINT bfd1485_carrier_claims_bene_id_to_beneficiaries FOREIGN KEY (bene_id)
        REFERENCES public.beneficiaries (bene_id_numeric);

${logic.hsql-only} CREATE VIEW
${logic.psql-only} CREATE OR REPLACE VIEW
	public.carrier_claims_v as
    	select * from public.bfd1485_carrier_claims;

${logic.hsql-only} CREATE VIEW
${logic.psql-only} CREATE OR REPLACE VIEW
	public.carrier_claim_lines_v as
    	select * from public.bfd1485_carrier_claim_lines;