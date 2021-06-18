WITH
	has_offer AS (SELECT DISTINCT(item_id) FROM item_shop WHERE availability='true')

--SELECT * FROM item_shop
SELECT DISTINCT(item_shop.item_id), item.title, item.productgroup FROM item_shop, item
WHERE item_shop.item_id NOT IN (SELECT item_id FROM has_offer) 
and item_shop.item_id = item.item_id