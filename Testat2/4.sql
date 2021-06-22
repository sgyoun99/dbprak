SELECT item_id, MAX(price) as highest_price, MIN(price) as lowest_price 
	FROM item_shop
	WHERE availability='true'
	GROUP BY item_id
	HAVING MAX(price) > MIN(price)*2