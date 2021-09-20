

SELECT 
    (SELECT count(*) FROM item) AS item, 
    (SELECT count(*) FROM shop) AS shop, 
    (SELECT count(*) FROM errors) AS errors, 
    (SELECT count(*) FROM item_shop) AS item_shop, 
    (SELECT count(*) FROM similar_items) AS similar_items, 
    (SELECT count(*) FROM book) AS book, 
    (SELECT count(*) FROM author) AS author, 
    (SELECT count(*) FROM publisher) AS publisher, 
    (SELECT count(*) FROM book_author) AS book_author, 
    (SELECT count(*) FROM book_publisher) AS book_publisher;

Select     
    (SELECT count(*) FROM actor) AS actor, 
    (SELECT count(*) FROM creator) AS creator, 
    (SELECT count(*) FROM director) AS director, 
    (SELECT count(*) FROM dvd) AS dvd, 
    (SELECT count(*) FROM dvd_actor) AS dvd_actor, 
    (SELECT count(*) FROM dvd_creator) AS dvd_creator, 
    (SELECT count(*) FROM dvd_director) AS dvd_director, 
    (SELECT count(*) FROM label) AS label, 
    (SELECT count(*) FROM artist) AS artist, 
    (SELECT count(*) FROM title) AS title,
    (SELECT count(*) FROM music_cd) AS music_cd, 
    (SELECT count(*) FROM music_cd_label) AS music_cd_label, 
    (SELECT count(*) FROM music_cd_artist) AS music_cd_artist    
    ;

SELECT 
    (SELECT count(*) FROM category) AS category, 
    (SELECT count(*) FROM sub_category) AS sub_category, 
    (SELECT count(*) FROM item_category) AS item_category, 
    (SELECT count(*) FROM review) AS review, 
    (SELECT count(*) FROM customer) AS customer
    ;



/*
, 
    (SELECT count(*) FROM shop) AS shop

*/