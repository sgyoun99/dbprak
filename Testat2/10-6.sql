WITH
diff_main_category AS (
	SELECT s.item_id, s.similar_item_id, dmc.diff_main_cat_id
	FROM similar_items s,
		select_sim_item_id_with_diff_main_cat(s.item_id, s.similar_item_id) dmc)

SELECT COUNT(DISTINCT item_id) FROM diff_main_category --457
--SELECT * FROM diff_main_category --852