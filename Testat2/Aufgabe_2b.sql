--function for update average rating
    CREATE OR REPLACE FUNCTION update_avg()
    RETURNS TRIGGER 
    LANGUAGE PLPGSQL
    AS
    $$
    BEGIN
        UPDATE item
        SET rating = (SELECT AVG(review.rating)::NUMERIC(2,1) FROM review 
                    WHERE review.item_id = NEW.item_id 
                        OR review.item_id = OLD.item_id)
        WHERE item.item_id = NEW.item_id 
        OR item.item_id = OLD.item_id;
        RETURN NEW;
    END;
    $$;


--trigger for table review
    CREATE TRIGGER review_changes
    AFTER INSERT or DELETE or UPDATE 
    ON review
    FOR EACH ROW
    EXECUTE PROCEDURE update_avg();


--test data
    SELECT * FROM review WHERE item_id = 'B00006GEJS';
    SELECT * FROM item WHERE item_id = 'B00006GEJS';


--test statements for insert, update and delete
    INSERT INTO review VALUES (999999,	'B00006GEJS', 'guest', '2021-06-30'::DATE, 'foo', 'hello world', 1);
    UPDATE review SET rating = 3 WHERE review_id = 999999;
    DELETE FROM review WHERE review_id = 999999;

