${logic.hsql-only}CREATE TABLE IF NOT EXISTS public.bfd1485_medicare_beneficiaryid_history (
${logic.hsql-only}	bene_mbi_id 			bigint NOT NULL,
${logic.hsql-only}	bene_id					bigint,
${logic.hsql-only}	last_updated			timestamp with time zone,
${logic.hsql-only}	bene_clm_acnt_num		varchar(9),
${logic.hsql-only}	bene_ident_cd			varchar(2),
${logic.hsql-only}	mbi_sqnc_num			integer,
${logic.hsql-only}	mbi_num					varchar(11),
${logic.hsql-only}	mbi_efctv_bgn_dt		date,
${logic.hsql-only}	mbi_efctv_end_dt		date,
${logic.hsql-only}	mbi_bgn_rsn_cd			varchar(5),
${logic.hsql-only}	mbi_end_rsn_cd			varchar(5),
${logic.hsql-only}	mbi_card_rqst_dt		date,
${logic.hsql-only}	creat_user_id			varchar(30) ,
${logic.hsql-only}	creat_ts				timestamp without time zone,
${logic.hsql-only}	updt_user_id			varchar(30) ,
${logic.hsql-only}	updt_ts					timestamp without time zone,
${logic.hsql-only}	bene_crnt_rec_ind_id	integer,
${logic.hsql-only}	CONSTRAINT bfd1485_medicare_beneficiaryid_history_pkey PRIMARY KEY (bene_mbi_id));

${logic.psql-only} SET max_parallel_workers = 24;
${logic.psql-only} SET max_parallel_workers_per_gather = 20;
${logic.psql-only} SET parallel_leader_participation = off;
${logic.psql-only} SET parallel_tuple_cost = 0;
${logic.psql-only} SET parallel_setup_cost = 0;
${logic.psql-only} SET min_parallel_table_scan_size = 0;

${logic.hsql-only} insert into public.bfd1485_medicare_beneficiaryid_history(
${logic.hsql-only}	bene_mbi_id,
${logic.hsql-only}	bene_id,
${logic.hsql-only}	last_updated,
${logic.hsql-only}	bene_clm_acnt_num,
${logic.hsql-only}	bene_ident_cd,
${logic.hsql-only}	mbi_sqnc_num,
${logic.hsql-only}	mbi_num,
${logic.hsql-only}	mbi_efctv_bgn_dt,
${logic.hsql-only}	mbi_efctv_end_dt,
${logic.hsql-only}	mbi_bgn_rsn_cd,
${logic.hsql-only}	mbi_end_rsn_cd,
${logic.hsql-only}	mbi_card_rqst_dt,
${logic.hsql-only}	creat_user_id,
${logic.hsql-only}	creat_ts,
${logic.hsql-only}	updt_user_id,
${logic.hsql-only}	updt_ts,
${logic.hsql-only}	bene_crnt_rec_ind_id )
${logic.psql-only}	create table public.bfd1485_medicare_beneficiaryid_history as
select
	${logic.psql-only} cast(bene_mbi_id as bigint),  
	${logic.hsql-only} convert(bene_mbi_id, SQL_BIGINT),
	${logic.psql-only} cast(bene_id as bigint),  
	${logic.hsql-only} convert(bene_id, SQL_BIGINT),
	last_updated,
	bene_clm_acnt_num,
	bene_ident_cd,
	mbi_sqnc_num,
	mbi_num,
	mbi_efctv_bgn_dt,
	mbi_efctv_end_dt,
	mbi_bgn_rsn_cd,
	mbi_end_rsn_cd,
	mbi_card_rqst_dt,
	creat_user_id,
	creat_ts,
	updt_user_id,
	updt_ts,
	bene_crnt_rec_ind_id
from
	public.medicare_beneficiaryid_history;
	
	
${logic.psql-only} alter table public.bfd1485_medicare_beneficiaryid_history
${logic.psql-only}	bene_mbi_id 	SET NOT NULL;


CREATE INDEX IF NOT EXISTS bfd1485_medicare_beneficiaryid_history_bene_id_idx
    ON public.bfd1485_medicare_beneficiaryid_history (bene_id);
