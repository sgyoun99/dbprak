WITH
item_id_count AS 
	(SELECT item_id, COUNT(*) as item_id_count FROM item_shop GROUP BY item_id),
many_offers AS 
	(SELECT item_id FROM item_id_count WHERE item_id_count >= 2),
price_table AS --for the items which has more than two offers.
	(SELECT * FROM item_shop
	WHERE availability='true' 
	and item_id in (SELECT item_id FROM many_offers)
	ORDER BY item_id ASC, shop_id ASC),
price_compare AS
	(SELECT item_id, MAX(price) as highest_price, MIN(price) as lowest_price 
	FROM price_table
	GROUP BY item_id)

SELECT * from price_compare
WHERE highest_price >= lowest_price*2

