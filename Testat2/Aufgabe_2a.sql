--Aufgabe 1
	SELECT 
		sum(case when Productgroup='Book' then 1 else 0 end) AS book, 
		sum(case when Productgroup='Music_CD' then 1 else 0 end) AS CD, 
		sum(case when Productgroup='DVD' then 1 else 0 end) AS DVD 
	FROM item;



--Aufgabe 2
	WITH
	sorted_items AS (
		SELECT productgroup, item_id, rating, salesranking
		FROM item
		WHERE salesranking > 0
		ORDER BY rating DESC, salesranking ASC),
	best_book AS (
		SELECT productgroup, item_id, rating FROM sorted_items WHERE productgroup = 'Book' LIMIT 5),
	best_music_cd AS (
		SELECT productgroup, item_id, rating FROM sorted_items WHERE productgroup = 'Music_CD' LIMIT 5),
	best_dvd AS (
		SELECT productgroup, item_id, rating FROM sorted_items WHERE productgroup = 'DVD' LIMIT 5)

	SELECT * FROM best_book
	UNION
	SELECT * FROM best_music_cd
	UNION
	SELECT * FROM best_dvd
	ORDER BY productgroup ASC;



--Aufgabe 3
	WITH
	has_offer AS (SELECT DISTINCT(item_id) FROM item_shop WHERE availability='true')

	SELECT * FROM item
	WHERE item_id NOT IN (SELECT item_id FROM has_offer)
	ORDER BY item_id;



--Aufgabe 4
	SELECT item_id, MAX(price) as highest_price, MIN(price) as lowest_price 
	FROM item_shop
	WHERE availability='true'
	GROUP BY item_id
	HAVING MAX(price) > MIN(price)*2
	ORDER BY item_id;



--Aufgabe 5
	WITH
	rating_one  AS (SELECT DISTINCT(item_id), rating FROM review WHERE rating=1),
	rating_five AS (SELECT DISTINCT(item_id), rating FROM review WHERE rating=5)

	SELECT * from item
	WHERE item_id in (SELECT item_id FROM rating_one)
	AND item_id in (SELECT item_id FROM rating_five)
	ORDER BY item_id;



--Aufgabe 6
	--Variante1
		SELECT item_id, rating FROM item
		WHERE rating = 0.0
		ORDER BY item_id;

	--Variante2
		WITH
		has_review AS (SELECT DISTINCT item_id FROM review)

		SELECT * FROM item
		WHERE item_id NOT IN (SELECT * FROM has_review)
		ORDER BY item_id;



--Aufgabe 7
	SELECT customer, count(*) AS number_of_reviews FROM review
	GROUP BY customer
	HAVING count(*) >= 10
	ORDER BY count(*) DESC;



--Aufgabe 8
	WITH
	dvd_participants AS (
		SELECT actor AS participant FROM actor
		UNION
		SELECT creator AS participant FROM creator
		UNION
		SELECT director AS participant FROM director),
	music_cd_participants AS (
		SELECT artist AS participant FROM artist)

	SELECT DISTINCT author FROM author
	WHERE author IN (SELECT participant FROM dvd_participants)
	OR author IN (SELECT participant FROM music_cd_participants)
	ORDER BY author ASC;



--Aufgabe 9
	SELECT AVG(title_count)::numeric(5,2) AS avg_title_count
	FROM (SELECT COUNT(title) AS title_count FROM title GROUP BY item_id) title_count;



--Aufgabe 10
	-- get the main_category of a sub_category
		CREATE OR REPLACE FUNCTION get_main_cat_id (sub_cat_id INTEGER)
		RETURNS INTEGER 
		language plpgsql
		AS 
		$$
		DECLARE main_cat_id INTEGER;
		BEGIN

		WITH
		RECURSIVE categories(sub_category_id, over_category_id, level) AS (
			SELECT sub_category_id, over_category_id, 1 FROM sub_category 
			WHERE sub_category_id = sub_cat_id
			UNION ALL
			SELECT rec_cat.sub_category_id, sub_cat.over_category_id, level+1 
			FROM categories rec_cat, sub_category sub_cat WHERE rec_cat.over_category_id = sub_cat.sub_category_id)

		SELECT over_category_id
		INTO main_cat_id
		FROM categories
		ORDER BY level DESC
		LIMIT 1;

		RETURN main_cat_id;
		END;
		$$;

	-- get all main_categories of an item
		CREATE OR REPLACE FUNCTION select_all_main_cat_id (param_item_id TEXT)
		RETURNS TABLE(main_cat_id INTEGER)
		language plpgsql
		AS 
		$$
		BEGIN

		RETURN QUERY 
			SELECT DISTINCT get_main_cat_id(ic.category_id) as main_cat_id 
			FROM item_category ic 
			WHERE ic.item_id = param_item_id;
			
		END;
		$$;

	-- shows how different the main categories between item und similar items are.
		CREATE OR REPLACE FUNCTION select_main_cat (param_item_id TEXT, param_sim_item_id TEXT)
		RETURNS TABLE(item TEXT, item_id TEXT, main_cat INTEGER, category_name TEXT)
		language plpgsql
		AS 
		$$
		BEGIN
		RETURN 	QUERY 

			WITH 
			main_categories AS (
			SELECT 	'item' AS item,
					param_item_id AS item_id, 
					select_all_main_cat_id(param_item_id) AS main_cat
			UNION
			SELECT	'similar_item' AS item,
					param_sim_item_id AS item_id, 
					select_all_main_cat_id(param_sim_item_id) AS main_cat
			)

			SELECT mc.item, mc.item_id, mc.main_cat, category.name
			FROM main_categories mc INNER JOIN category ON mc.main_cat = category.category_id
			UNION
			SELECT CONCAT('>>',param_item_id,'<<'), NULL, NULL, NULL
			ORDER BY item ASC, main_cat ASC;

		END;
		$$;

	-- check if intersection of main categories of the item and its similar_item is empty
		CREATE OR REPLACE FUNCTION is_main_cat_disjoint (param_item_id TEXT, param_sim_item_id TEXT)
		RETURNS BOOLEAN
		language plpgsql
		AS 
		$$
		DECLARE is_empty BOOLEAN;
		BEGIN

		WITH main_cat_intersection AS(
			SELECT * FROM select_all_main_cat_id(param_item_id)
			INTERSECT
			SELECT * FROM select_all_main_cat_id(param_sim_item_id)
		)
		SELECT CASE WHEN (SELECT COUNT(*) FROM main_cat_intersection) = 0 THEN TRUE 
					ELSE FALSE 
					END INTO is_empty;

		RETURN is_empty;
		END;
		$$;

	--create a materialized view for effectiveness
		CREATE MATERIALIZED VIEW diff_main_cat
		AS
			SELECT * FROM similar_items si
			WHERE is_main_cat_disjoint(si.item_id, si.similar_item_id) = TRUE
			ORDER BY item_id, similar_item_id
		WITH NO DATA;
		REFRESH MATERIALIZED VIEW diff_main_cat;

	--result1
		SELECT * FROM diff_main_cat;

	--result2
		SELECT diff.* FROM diff_main_cat dmc, select_main_cat(dmc.item_id, dmc.similar_item_id) diff;


--Aufgabe 11
	--Variante1
		SELECT item_id, count(Distinct shop_id) AS number_of_shops FROM item_shop
		GROUP BY item_id
		HAVING count(Distinct shop_id) = (SELECT count(*) FROM shop);

	--Variante2
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
								) THEN TRUE 
						ELSE FALSE 
						END INTO is_in_shop;

			RETURN is_in_shop;
			END;
			$$;
			
		--result
			WITH
			all_shop_id AS (
				SELECT DISTINCT shop_id FROM shop),
			items_in_all_shop AS (
				SELECT * FROM item
				WHERE TRUE = ALL(SELECT is_item_in_shop(item.item_id, all_shop_id.shop_id) FROM all_shop_id)
				ORDER BY item_id)
				
			SELECT * FROM items_in_all_shop;



--Aufgabe 12
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
		GROUP BY item_shop.item_id),
	cheap_items_in_leipzig AS (
		SELECT * 
		FROM cheap_items ci INNER JOIN item_shop ON (ci.item_id = item_shop.item_id AND ci.min_price = item_shop.price)
		WHERE item_shop.shop_id = (SELECT shop_id FROM shop WHERE shop.shop_name = 'Leipzig'))

	SELECT ((SELECT COUNT(*) * 100 FROM cheap_items_in_leipzig) / (SELECT COUNT(*) FROM items_in_all_shop)::FLOAT)::NUMERIC(10,2)