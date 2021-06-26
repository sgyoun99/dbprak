-- find all items whose similar item has different main category.

WITH
diff_main_category AS (
	SELECT s.item_id, s.similar_item_id
	FROM (SELECT * FROM similar_items LIMIT 50) s,  -- because it takes too long...
		select_sim_item_id_with_diff_main_cat(s.item_id, s.similar_item_id) dmc
	GROUP BY s.item_id, s.similar_item_id)

SELECT diff.* FROM diff_main_category dmc, select_main_cat_diff(dmc.item_id, dmc.similar_item_id) diff
