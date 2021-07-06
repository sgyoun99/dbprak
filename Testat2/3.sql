WITH
has_offer AS (SELECT DISTINCT(item_id) FROM item_shop WHERE availability='true')

SELECT * FROM item
WHERE item_id NOT IN (SELECT item_id FROM has_offer)
