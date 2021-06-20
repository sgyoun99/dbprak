WITH
dvd_participants AS (
	SELECT actor AS participant FROM actor
	UNION
	SELECT creator AS participant FROM creator
	UNION
	SELECT director AS participant FROM director),
music_cd_participants AS (
	SELECT artist AS participant FROM artist)

SELECT DISTINCT author FROM author
WHERE author IN (SELECT participant FROM dvd_participants)
OR author IN (SELECT participant FROM music_cd_participants)
ORDER BY author ASC