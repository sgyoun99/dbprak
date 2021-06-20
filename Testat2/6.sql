WITH
has_review AS (SELECT DISTINCT item_id FROM review)

SELECT * FROM item
WHERE item_id NOT IN (SELECT * FROM has_review)
