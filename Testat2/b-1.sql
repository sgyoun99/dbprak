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

