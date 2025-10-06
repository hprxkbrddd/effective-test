CREATE OR REPLACE FUNCTION transfer_funds(
    from_card_id BIGINT,
    to_card_id BIGINT,
    transfer_amount DECIMAL
) RETURNS BOOLEAN AS '
DECLARE
    success BOOLEAN;
BEGIN
    WITH withdrawal AS (
        UPDATE card
        SET balance = balance - transfer_amount
        WHERE id = from_card_id
        AND balance >= transfer_amount
        RETURNING 1
    ),
    deposit AS (
        UPDATE card
        SET balance = balance + transfer_amount
        WHERE id = to_card_id
        AND EXISTS (SELECT 1 FROM withdrawal)
    )
    SELECT EXISTS(SELECT 1 FROM withdrawal) INTO success;

    RETURN success;
END;
' LANGUAGE plpgsql;