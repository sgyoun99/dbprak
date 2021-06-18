WITH 
book_count AS (SELECT COUNT(*) as book FROM item WHERE productgroup='Book'),
music_cd_count AS (SELECT COUNT(*) as music_cd FROM item WHERE productgroup='Music_CD'),
dvd_count AS (SELECT COUNT(*) as dvd FROM item WHERE productgroup='DVD')

SELECT * FROM book_count, music_cd_count, dvd_count;
