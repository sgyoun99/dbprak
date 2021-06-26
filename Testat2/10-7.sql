CREATE MATERIALIZED VIEW diff_main_cat
AS
SELECT item_id, sidiff.*, name
FROM similar_items si,
	 select_sim_item_id_with_diff_main_cat(item_id, similar_item_id) sidiff
	 INNER JOIN category ON (diff_main_cat_id = category.category_id)
	 ORDER BY item_id
WITH NO DATA;
REFRESH MATERIALIZED VIEW diff_main_cat;


--select * from diff_main_cat --852
--select item_id, sim_item_id from diff_main_cat group by item_id, sim_item_id --650
--select count(distinct item_id) from diff_main_cat --457
