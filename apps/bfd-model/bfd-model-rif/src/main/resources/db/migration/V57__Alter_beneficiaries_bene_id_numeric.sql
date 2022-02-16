/*
    Flyway migration to add a column, bene_id_numeric bigint, to the
    beneficairies table, populate that column for all current records,
    and then add a trigger which will fire/populate that numeric column
    whenever a new beneficairy record is intered tinto the table.
*/
ALTER TABLE public.beneficiaries ADD bene_id_numeric bigint;

UPDATE public.beneficiaries
${logic.hsql-only} SET bene_id_numeric = convert(bene_id, SQL_BIGINT)
${logic.psql-only} SET bene_id_numeric = cast(bene_id as bigint)
WHERE bene_id is not null;

ALTER TABLE public.beneficiaries ALTER COLUMN bene_id_numeric SET NOT NULL;

${logic.psql-only} CREATE OR REPLACE FUNCTION public.beneficiaries_pre_insert()
${logic.psql-only} RETURNS trigger AS $BODY$
${logic.psql-only} BEGIN
${logic.psql-only}   IF NEW.bene_id is not null THEN
${logic.psql-only}     NEW.bene_id_numeric = NEW.bene_id::bigint;
${logic.psql-only}   END IF;
${logic.psql-only}   RETURN NEW;
${logic.psql-only} END;
${logic.psql-only} $BODY$
${logic.psql-only} LANGUAGE plpgsql;

${logic.psql-only} DROP TRIGGER IF EXISTS beneficiaries_insert_trigger ON public.beneficiaries;
${logic.psql-only} CREATE TRIGGER beneficiaries_insert_trigger
${logic.psql-only}   BEFORE INSERT ON public.beneficiaries
${logic.psql-only}   FOR EACH ROW
${logic.psql-only}     EXECUTE FUNCTION beneficiaries_pre_insert();

${logic.hsql-only} CREATE TRIGGER beneficiaries_insert_trigger BEFORE INSERT ON beneficiaries
${logic.hsql-only}    REFERENCING NEW as newrow FOR EACH ROW
${logic.hsql-only}       BEGIN ATOMIC
${logic.hsql-only}          IF newrow.bene_id is not null THEN
${logic.hsql-only}            SET newrow.bene_id_numeric = convert(newrow.bene_id, SQL_BIGINT);
${logic.hsql-only}          END IF;
${logic.hsql-only}       END;