WITH
all_shop_id AS (
	SELECT DISTINCT shop_id FROM shop),
items_in_all_shop AS (
	SELECT * FROM item
	WHERE TRUE = ALL(SELECT is_item_in_shop(item.item_id, all_shop_id.shop_id) FROM all_shop_id)
	ORDER BY item_id),
cheap_items AS (	
	SELECT item_shop.item_id, MIN(item_shop.price) as min_price
	FROM items_in_all_shop iias INNER JOIN item_shop ON iias.item_id = item_shop.item_id 
								INNER JOIN shop ON item_shop.shop_id = shop.shop_id
	WHERE iias.item_id = item_shop.item_id
	GROUP BY item_shop.item_id),
cheap_items_in_leipzig AS (
	SELECT * 
	FROM cheap_items ci INNER JOIN item_shop ON (ci.item_id = item_shop.item_id AND ci.min_price = item_shop.price)
	WHERE item_shop.shop_id = (SELECT shop_id FROM shop WHERE shop.shop_name = 'Leipzig'))

SELECT ((SELECT COUNT(*) * 100 FROM cheap_items_in_leipzig) / (SELECT COUNT(*) FROM cheap_items)::FLOAT)::NUMERIC(10,2)
--SELECT COUNT(*) FROM cheap_items_in_leipzig --248
--SELECT COUNT(*) FROM cheap_items --307