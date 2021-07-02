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

--test
SELECT * FROM is_main_cat_disjoint('B00007BKGQ','B0000CFYEJ')
--SELECT * FROM is_main_cat_disjoint('3895847011','3551551685')
