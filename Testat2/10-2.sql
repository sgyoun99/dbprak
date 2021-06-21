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

--test
SELECT main_cat_id, category.name 
FROM select_all_main_cat_id('6304498977') 
INNER JOIN category ON main_cat_id = category.category_id