SELECT AVG(title_count)::numeric(5,2) AS avg_title_count
FROM (SELECT COUNT(title) AS title_count FROM title GROUP BY item_id) title_counts