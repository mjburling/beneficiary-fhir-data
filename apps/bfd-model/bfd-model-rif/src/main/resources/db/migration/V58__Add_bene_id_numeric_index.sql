-- Ideally, this migration would be part of the V57__ migration that alters the
-- beneficiaries table and creates a trigger to maintain it during insert ops.
-- However, the alter and create triggers will run in a transaction, and this
-- migration fails since flyway does not support transactional and non-transactional
-- statements within the same migration script (unless you explicitly enable that).
-- This script falls under non-transactional since the create index is being done
-- concurrently.
CREATE UNIQUE INDEX ${logic.index-create-concurrently}
    IF NOT EXISTS beneficiaries_bene_id_numeric_idx ON public.beneficiaries (bene_id_numeric);