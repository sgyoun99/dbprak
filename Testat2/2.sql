WITH
sorted_items AS (
	SELECT productgroup, item_id, rating, salesranking
	FROM item
	WHERE salesranking > 0
	ORDER BY rating DESC, salesranking ASC),
best_book AS (
	SELECT * FROM sorted_items WHERE productgroup = 'Book' LIMIT 5),
best_music_cd AS (
	SELECT * FROM sorted_items WHERE productgroup = 'Music_CD' LIMIT 5),
best_dvd AS (
	SELECT * FROM sorted_items WHERE productgroup = 'DVD' LIMIT 5)

SELECT * FROM best_book
UNION
SELECT * FROM best_music_cd
UNION
SELECT * FROM best_dvd
ORDER BY productgroup ASC, rating DESC, salesranking ASC