${logic.hsql-only} CREATE TABLE IF NOT EXISTS public.bfd1485_beneficiary_monthly (
${logic.hsql-only}	bene_id									bigint NOT NULL,
${logic.hsql-only}	year_month								date NOT NULL,
${logic.hsql-only}	partd_contract_number_id				varchar(5),
${logic.hsql-only}	fips_state_cnty_code					varchar(5),
${logic.hsql-only}	medicare_status_code					varchar(2),
${logic.hsql-only}	medicaid_dual_eligibility_code			varchar(2),
${logic.hsql-only}	entitlement_buy_in_ind					char(1),
${logic.hsql-only}	hmo_indicator_ind						char(1),
${logic.hsql-only}	partd_pbp_number_id						varchar(3),
${logic.hsql-only}	partd_segment_number_id					varchar(3),
${logic.hsql-only}	partd_retiree_drug_subsidy_ind			char(1),
${logic.hsql-only}	partd_low_income_cost_share_group_code	varchar(2),
${logic.hsql-only}	partc_contract_number_id 				varchar(5),
${logic.hsql-only}	partc_pbp_number_id						varchar(3),
${logic.hsql-only}	partc_plan_type_code					varchar(3),
${logic.hsql-only} 	CONSTRAINT bfd1485_beneficiary_monthly_pkey PRIMARY KEY (bene_id, year_month));

${logic.psql-only} SET max_parallel_workers = 24;
${logic.psql-only} SET max_parallel_workers_per_gather = 20;
${logic.psql-only} SET parallel_leader_participation = off;
${logic.psql-only} SET parallel_tuple_cost = 0;
${logic.psql-only} SET parallel_setup_cost = 0;
${logic.psql-only} SET min_parallel_table_scan_size = 0;

${logic.hsql-only} insert into public.bfd1485_beneficiary_monthly(
	${logic.hsql-only}	bene_id,
	${logic.hsql-only}	year_month,
	${logic.hsql-only}	partd_contract_number_id,
	${logic.hsql-only}	fips_state_cnty_code,
	${logic.hsql-only}	medicare_status_code,
	${logic.hsql-only}	medicaid_dual_eligibility_code,
	${logic.hsql-only}	entitlement_buy_in_ind,
	${logic.hsql-only}	hmo_indicator_ind,
	${logic.hsql-only}	partd_pbp_number_id,
	${logic.hsql-only}	partd_segment_number_id,
	${logic.hsql-only}	partd_retiree_drug_subsidy_ind,
	${logic.hsql-only}	partd_low_income_cost_share_group_code,
	${logic.hsql-only}	partc_contract_number_id,
	${logic.hsql-only}	partc_pbp_number_id,
	${logic.hsql-only}	partc_plan_type_code )
${logic.psql-only} create table public.bfd1485_beneficiary_monthly as
select
	${logic.psql-only} cast(bene_id as bigint),  
    ${logic.hsql-only} convert(bene_id, SQL_BIGINT),
	year_month,
	partd_contract_number_id,
	fips_state_cnty_code,
	medicare_status_code,
	medicaid_dual_eligibility_code,
	entitlement_buy_in_ind,
	hmo_indicator_ind,
	partd_pbp_number_id,
	partd_segment_number_id,
	partd_retiree_drug_subsidy_ind,
	partd_low_income_cost_share_group_code,
	partc_contract_number_id,
	partc_pbp_number_id,
	partc_plan_type_code
from
	public.beneficiary_monthly;
	
${logic.psql-only} alter table public.bfd1485_beneficiary_monthly
${logic.psql-only} 	alter column bene_id SET NOT NULL,
${logic.psql-only} 	alter column year_month SET NOT NULL;

CREATE INDEX IF NOT EXISTS bfd1485_beneficiary_monthly_partd_contract_year_month_bene_id_idx
ON public.bfd1485_beneficiary_monthly (partd_contract_number_id, year_month, bene_id );