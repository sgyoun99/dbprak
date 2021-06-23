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

--test
SELECT get_main_cat_id(sub_category_id), over_category_id, sub_category_id FROM sub_category WHERE sub_category_id = 100