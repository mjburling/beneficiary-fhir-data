SELECT bene_id
FROM beneficiary_monthly
WHERE year_month = $1::date
AND partd_contract_number_id = $2
ORDER BY bene_id ASC
LIMIT $3