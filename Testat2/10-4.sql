-- shows how different the main categories between item und similar items are.
CREATE OR REPLACE FUNCTION select_main_cat_diff (param_item_id TEXT, param_sim_item_id TEXT)
RETURNS TABLE(item TEXT, item_id TEXT, main_cat TEXT, category_name TEXT, diff TEXT)
language plpgsql
AS 
$$
BEGIN
RETURN 	QUERY 

	WITH 
	main_categories AS (
	SELECT 	'item' as item,
			param_item_id as item_id, 
			CAST(select_all_main_cat_id(param_item_id) AS TEXT) as main_cat,
			'-' as diff
	UNION
	SELECT	'similar_item' as item,
			similar_item_id as item_id, 
			CAST(main_cat.main_cat_id AS TEXT),
			CASE WHEN main_cat.main_cat_id in (SELECT * FROM select_all_main_cat_id(param_item_id)) THEN 'SAME' ELSE 'DIFFERENT' END AS diff
	FROM similar_items, select_all_main_cat_id(similar_item_id) main_cat
	WHERE similar_item_id = param_sim_item_id
	)

	SELECT mc.item, mc.item_id, mc.main_cat, category.name, mc.diff
	FROM main_categories mc INNER JOIN category ON mc.main_cat = CAST(category.category_id AS TEXT)
	UNION
	SELECT '_diff_item', param_item_id, '======', '======', '======'
	ORDER BY item ASC, main_cat ASC;

END;
$$;

--test
SELECT * FROM select_main_cat_diff('3899406788','3551551677')
