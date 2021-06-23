-- checks if the item is offered in the shop
CREATE OR REPLACE FUNCTION is_item_in_shop (param_item_id TEXT, param_shop_id INTEGER)
RETURNS BOOLEAN
language plpgsql
AS 
$$
DECLARE is_in_shop BOOLEAN;
BEGIN

SELECT CASE WHEN param_item_id 
				IN (SELECT item_id FROM item_shop 
					WHERE item_id = param_item_id 
					AND shop_id = param_shop_id
				    AND availability = TRUE) THEN TRUE 
			ELSE FALSE 
			END INTO is_in_shop;

RETURN is_in_shop;
END;
$$;

--test
SELECT is_item_in_shop('B000002ONW',1) --true
UNION ALL
SELECT is_item_in_shop('B000002ONW',2) --true
UNION ALL
SELECT is_item_in_shop('3000147012',1) --true
UNION ALL
SELECT is_item_in_shop('3000147012',2) --false
