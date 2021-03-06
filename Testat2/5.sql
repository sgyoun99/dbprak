WITH
rating_one  AS (SELECT DISTINCT(item_id), rating FROM review WHERE rating=1),
rating_five AS (SELECT DISTINCT(item_id), rating FROM review WHERE rating=5)

SELECT * from item
WHERE item_id in (SELECT item_id FROM rating_one)
  AND item_id in (SELECT item_id FROM rating_five)