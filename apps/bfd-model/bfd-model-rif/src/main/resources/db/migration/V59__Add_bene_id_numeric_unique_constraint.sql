-- unfortunately, this is another non-transcational migration
-- which depends on (at least in psql) the presence of an existing
-- unique index name (see V58__Add_bene_id_numeric_index).
ALTER TABLE public.beneficiaries
    ADD CONSTRAINT
${logic.hsql-only} beneficiaries_bene_id_numeric_fk UNIQUE (bene_id_numeric);
${logic.psql-only} beneficiaries_bene_id_numeric_idx UNIQUE USING INDEX beneficiaries_bene_id_numeric_idx;