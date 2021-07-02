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

--test
SELECT * FROM select_main_cat('B00007BKGQ','B0000CFYEJ')
--SELECT * FROM select_main_cat('3895847011','3551551685')
