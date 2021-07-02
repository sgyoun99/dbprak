CREATE MATERIALIZED VIEW diff_main_cat
AS
	SELECT * FROM similar_items si
	WHERE is_main_cat_disjoint(si.item_id, si.similar_item_id) = TRUE
	ORDER BY item_id, similar_item_id
WITH NO DATA;
REFRESH MATERIALIZED VIEW diff_main_cat;
