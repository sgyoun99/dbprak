-- check if a similar item has different main category as the given item.
-- if so, returns the similar item and its main category which differs from the given item.
CREATE OR REPLACE FUNCTION select_sim_item_id_with_diff_main_cat (param_item_id TEXT, param_sim_item_id TEXT)
RETURNS TABLE(sim_item_id TEXT, diff_main_cat_id INTEGER)
language plpgsql
AS 
$$
BEGIN
RETURN 	QUERY 

	SELECT param_sim_item_id, sim.main_cat_id 
	FROM (SELECT * FROM select_all_main_cat_id(param_sim_item_id)) sim
	WHERE sim.main_cat_id NOT IN (SELECT * FROM select_all_main_cat_id(param_item_id));

END;
$$;

--test
SELECT sim_item_id, diff_main_cat_id
FROM select_sim_item_id_with_diff_main_cat('B0002H24N2','B0002JZ32E')
