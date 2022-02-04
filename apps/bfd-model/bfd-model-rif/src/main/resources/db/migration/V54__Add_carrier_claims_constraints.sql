${logic.psql-only} alter table public.carrier_claims_bfd1485
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

${logic.psql-only} alter table public.carrier_claims_bfd1485
${logic.psql-only}     add CONSTRAINT carrier_claims_bfd1485_pkey PRIMARY KEY (clm_id);

CREATE INDEX IF NOT EXISTS carrier_claims_bfd1485_bene_id_idx
    ON public.carrier_claims_bfd1485 (bene_id);