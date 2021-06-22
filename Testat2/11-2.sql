WITH
all_shop_id AS (
	SELECT DISTINCT shop_id FROM shop),
items_in_all_shop AS (
	SELECT * FROM item
	WHERE TRUE = ALL(SELECT is_item_in_shop(item.item_id, all_shop_id.shop_id) FROM all_shop_id)
	ORDER BY item_id)
	
SELECT * FROM items_in_all_shop