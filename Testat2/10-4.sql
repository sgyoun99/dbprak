-- shows how different the main categories between item und similar items are.
CREATE OR REPLACE FUNCTION select_main_cat_diff (param_item_id TEXT, param_sim_item_id TEXT)
RETURNS TABLE(item TEXT, item_id TEXT, main_cat INTEGER, category_name TEXT, diff TEXT)
language plpgsql
AS 
$$
BEGIN
RETURN 	QUERY 

	WITH 
	main_categories AS (
	SELECT 	'item' as item,
			param_item_id as item_id, 
			select_all_main_cat_id(param_item_id) as main_cat,
			'-' as diff
	UNION
	SELECT	'similar_item' as item,
			similar_item_id as item_id, 
			main_cat.main_cat_id,
			CASE WHEN main_cat.main_cat_id in (SELECT * FROM select_all_main_cat_id(param_item_id)) THEN 'SAME' ELSE 'DIFFERENT' END AS diff
	FROM similar_items, select_all_main_cat_id(similar_item_id) main_cat
	WHERE similar_item_id = param_sim_item_id
	)

	SELECT mc.item, mc.item_id, mc.main_cat, category.name, mc.diff
	FROM main_categories mc INNER JOIN category ON mc.main_cat = category.category_id
	UNION
	SELECT ' = = = = = ', ' = = = = = ', '0', ' = = = = = ', ' = = = = = '
	ORDER BY item ASC, main_cat ASC;

END;
$$;

--test
SELECT * FROM select_main_cat_diff('B0007UARJW','3473344273')
