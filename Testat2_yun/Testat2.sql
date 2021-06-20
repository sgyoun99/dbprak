--Aufgabe 1
/*SELECT 
    sum(case when Productgroup='Book' then 1 else 0 end) AS book, 
    sum(case when Productgroup='Music_CD' then 1 else 0 end) AS CD, 
    sum(case when Productgroup='DVD' then 1 else 0 end) AS DVD 
FROM item; */


--Aufgabe 2
/*(SELECT item_id, productgroup, rating FROM item
    WHERE productgroup ='Book'
    ORDER BY rating DESC
    LIMIT 5)
UNION
(SELECT item_id, productgroup, rating FROM item
    WHERE productgroup ='DVD'
    ORDER BY rating DESC
    LIMIT 5)
UNION
(SELECT item_id, productgroup, rating FROM item
    WHERE productgroup ='Music_CD'
    ORDER BY rating DESC
    LIMIT 5)
ORDER BY rating, item_id;*/


--Aufgabe 3     Problem wegen item doppelt
/*SELECT * FROM item_shop
    WHERE availability = 'f'
    ORDER BY item_id;*/
    

--Aufgabe 4
/*SELECT a.item_id, a.maximum, b.minimum FROM 
    (SELECT item_id, max(price) AS maximum FROM item_shop
        GROUP BY item_id) AS a
    INNER JOIN
    (SELECT item_id, min(price) AS minimum FROM item_shop
        GROUP BY item_id) AS b
    ON a.item_id = b.item_id
    WHERE a.maximum > 2*b.minimum;*/    
  
    
--Aufgabe 5     140?
/*SELECT a.item_id, a.rating, b.rating FROM
    (SELECT item_id, rating FROM review
        WHERE rating = 1) AS a
    INNER JOIN
    (SELECT item_id, rating FROM review
        WHERE rating = 5) AS b
    ON a.item_id = b.item_id;*/


--Aufgabe 6
/*SELECT item_id, rating FROM item
    WHERE rating = 0.0;*/
    
    
--Aufgabe 7
/*SELECT customer, count(*) AS number_of_reviews FROM review
    GROUP BY customer
    HAVING count(*) >= 10
    ORDER BY count(*) DESC;*/
    
    
--Aufgabe 8
/*SELECT DISTINCT author FROM
    (
        SELECT author FROM
            ((SELECT author FROM author) AS a1
                INNER JOIN
                (SELECT artist FROM artist) as b
                ON a1.author = b.artist) 
        UNION
        (SELECT author FROM
            ((SELECT author FROM author) AS a2
                INNER JOIN
                (SELECT creator FROM creator) as c
                ON a2.author = c.creator))
        UNION
        (SELECT author FROM
            ((SELECT author FROM author) AS a3
                INNER JOIN
                (SELECT director FROM director) as d
                ON a3.author = d.director))
        UNION
        (SELECT author FROM
            ((SELECT author FROM author) AS a4
                INNER JOIN
                (SELECT actor FROM actor) AS aa
                ON a4.author = aa.actor))        
    ) as e
    ORDER BY author;*/


--Aufgabe 9
/*SELECT ROUND(AVG(titlenumber),0) AS average_number_of_titles FROM
    (SELECT Count(*) AS titlenumber FROM title
        GROUP BY item_id) as c;*/


--Aufgabe 10    Funktioniert noch nicht

/*CREATE OR REPLACE FUNCTION myFunc (cat_id integer)
RETURNS integer AS $main_cat$
DECLARE main_cat integer;
BEGIN
    WITH recursive cat_tree AS (
        SELECT main_category_id, sub_category_id
        FROM sub_category
        WHERE sub_category_id = cat_id     --start, going up from here
        
        UNION ALL
        
        SELECT main.main_category_id, main.sub_category_id
        FROM sub_category AS main
        
        JOIN cat_tree AS sub ON sub.main_category_id = main.sub_category_id
    )    
    SELECT main_category_id FROM cat_tree
    ORDER BY main_category_id ASC
    LIMIT 1;
    RETURN main_cat;
END;
$main_cat$
LANGUAGE plpgsql;*/


-- get main category of set category
/*WITH recursive cat_tree AS (
    SELECT main_category_id, sub_category_id
    FROM sub_category
    WHERE sub_category_id = 126     --start, going up from here
    
    UNION ALL
    
    SELECT main.main_category_id, main.sub_category_id
    FROM sub_category AS main
    
    JOIN cat_tree AS sub ON sub.main_category_id = main.sub_category_id
)    
SELECT main_category_id FROM cat_tree
ORDER BY main_category_id ASC
LIMIT 1;*/
    

    

    
--    SELECT one.item1, myFunc(one.cat_Item1), one.item2/*, item_category.category_id AS cat_item2*/ FROM item_category 
/*            INNER JOIN
                (SELECT similar_items.item_id AS item1, similar_item_id AS item2, it_cat.category_id AS cat_Item1 FROM similar_items
                INNER JOIN 
                    (SELECT * FROM item_category) as it_cat
                ON similar_items.item_id = it_cat.item_id
                ) as one
            ON one.item2 = item_category.item_id
            
            
         
        LIMIT 20;*/




/*WHERE (SELECT category_id from 
        (SELECT * FROM item_category 
            INNER JOIN
            (SELECT * FROM similar_items) as sim
            ON item_category.item_id = sim.item_id) AS a
        ) =
        (SELECT category_id from 
            (SELECT * FROM item_category 
                INNER JOIN
                (SELECT * FROM similar_items) as sim
                ON item_category.item_id = sim.similar_item_id) AS a
        )*/
            
            
            
            
       

    


--Aufgabe 11
/*SELECT item_id, count(Distinct shop_id) AS number_of_shops FROM item_shop
    GROUP BY item_id
    HAVING count(Distinct shop_id) = (SELECT count(*) FROM shop);*/


--Aufgabe 12
/*SELECT (count(*) * 100 / 
-- total number of items in both shops
    (SELECT count(*) FROM
        (SELECT item_id, count(DISTINCT shop_id) FROM item_shop
            GROUP BY item_id
            HAVING count(DISTINCT shop_id) = (SELECT count(*) FROM shop)) AS total)
) AS percentage FROM
-- number of items in both shops AND min(price) in Leipzig

-- number of items in both shops
    (SELECT item_id, count(DISTINCT shop_id) FROM item_shop
            GROUP BY item_id
            HAVING count(DISTINCT shop_id) = (SELECT count(*) FROM shop)) AS a
            
    INNER JOIN
    
-- number of items whose min(price) is in Leipzig
    (SELECT b.item_id, b.minimum FROM
    
-- minimum price in Leipzig
        (SELECT item_id, min(price) AS minimum FROM item_shop
            WHERE shop_id = (SELECT shop_id FROM shop WHERE shop_name = 'Leipzig')
            GROUP BY item_id
            ) AS b
        INNER JOIN
--minimum price overall
        (SELECT item_id, min(price) AS minimum FROM item_shop
            GROUP BY item_id
            ) AS c
        ON b.item_id = c.item_id
        WHERE b.minimum = c.minimum
    ) AS d
    
    ON a.item_id = d.item_id;*/
    
    
    
    
    
    

    
    
    
    
    
    
    
