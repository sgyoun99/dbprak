drop table if exists errors;
drop table if exists review, customer;
drop table if exists music_cd_artist, music_cd_label, title, music_cd, artist, label;
drop table if exists dvd_actor, dvd_creator, dvd_director, dvd, actor, creator, director;
drop table if exists book_publisher, book_author, book, publisher, author;
drop table if exists item_category, sub_category, category;
drop table if exists similar_items, item_shop, shop, item;
drop type if exists errtype;

/*CREATE TYPE pgroup AS ENUM('Book', 'Music_CD', 'DVD');*/
CREATE TYPE ErrType AS ENUM('XML', 'XML_NO_VALUE', 'XML_INVALID_VALUE', 'XML_DATA_INCOMPLETE', 'XML_NO_ATTRIBUTE', 'XML_NO_NODE', 'SQL', 'SQL_FK_ERROR', 'SQL_DUPLICATE', 'PROGRAM');


CREATE TABLE item(item_id text PRIMARY KEY, title TEXT, rating float(8), salesranking INTEGER, image TEXT, productgroup int NOT NULL);
CREATE TABLE shop(shop_id SERIAL, shop_name TEXT NOT NULL, street TEXT NOT NULL, zip VARCHAR(5) NOT NULL, PRIMARY KEY(shop_id), UNIQUE(shop_name, street, zip));
CREATE TABLE item_shop(item_id text NOT NULL, shop_id Integer NOT NULL, currency VARCHAR(3), price float(8), availabiliti BOOLEAN, condition text NOT NULL, PRIMARY KEY(item_id, shop_id, condition), FOREIGN KEY(item_id) REFERENCES item(item_id) ON DELETE CASCADE, FOREIGN KEY(shop_id) REFERENCES shop(shop_id) ON DELETE CASCADE);
CREATE TABLE similar_items(item_id text, sim_item_id text, PRIMARY KEY(item_id, sim_item_id), FOREIGN KEY(item_id) REFERENCES item(item_id) ON DELETE CASCADE, FOREIGN KEY (sim_item_id) REFERENCES item(item_id) ON DELETE CASCADE);

		
CREATE TABLE category(category_id Integer PRIMARY KEY NOT NULL, name TEXT);
CREATE TABLE sub_category(over_category_id INTEGER REFERENCES category(category_id), sub_category_id INTEGER REFERENCES category(category_id), PRIMARY KEY(over_category_id, sub_category_id));
CREATE TABLE item_category(item_id text REFERENCES item(item_id), category_id INTEGER REFERENCES category(category_id), PRIMARY KEY(item_id, category_id));


CREATE TABLE author(author TEXT PRIMARY KEY);
CREATE TABLE publisher(publisher TEXT PRIMARY KEY);
CREATE TABLE book(item_id text PRIMARY KEY, pages SMALLINT, publication_date DATE, isbn TEXT, FOREIGN KEY(item_id) REFERENCES item(item_id) ON DELETE CASCADE);
CREATE TABLE book_author(item_id text, author TEXT, PRIMARY KEY(item_id, author), FOREIGN KEY(item_id) REFERENCES book(item_id) ON DELETE CASCADE, FOREIGN KEY (author) REFERENCES author(author) ON DELETE CASCADE);
CREATE TABLE book_publisher(item_id text, publisher TEXT, PRIMARY KEY(item_id, publisher), FOREIGN KEY (item_id) REFERENCES book(item_id) ON DELETE CASCADE, FOREIGN KEY (publisher) REFERENCES publisher(publisher) ON DELETE CASCADE);

CREATE TABLE dvd(item_id text PRIMARY KEY, format TEXT, runningtime SMALLINT, regioncode VARCHAR(255), FOREIGN KEY (item_id) REFERENCES item(item_id) ON DELETE CASCADE);
CREATE TABLE actor(actor TEXT PRIMARY KEY);
CREATE TABLE creator(creator TEXT PRIMARY KEY);
CREATE TABLE director(director TEXT PRIMARY KEY);
CREATE TABLE dvd_actor(item_id text NOT NULL, actor text NOT NULL, PRIMARY KEY(item_id, actor), FOREIGN KEY(item_id) REFERENCES dvd(item_id) ON DELETE CASCADE, FOREIGN KEY(actor) REFERENCES actor(actor) ON DELETE CASCADE);
CREATE TABLE dvd_creator(item_id text NOT NULL, creator text NOT NULL, PRIMARY KEY (item_id, creator), FOREIGN KEY(item_id) REFERENCES dvd(item_id) ON DELETE CASCADE, FOREIGN KEY(creator) REFERENCES creator(creator) ON DELETE CASCADE);
CREATE TABLE dvd_director(item_id text NOT NULL, director TEXT NOT NULL, PRIMARY KEY(item_id, director), FOREIGN KEY(item_id) REFERENCES dvd(item_id) ON DELETE CASCADE, FOREIGN KEY(director) REFERENCES director(director) ON DELETE CASCADE);

CREATE TABLE artist(artist TEXT PRIMARY KEY);          
CREATE TABLE label(label TEXT PRIMARY KEY);  
CREATE TABLE music_cd(item_id text PRIMARY KEY, artist TEXT /*NOT NULL*/, release_date DATE, FOREIGN KEY (item_id) REFERENCES item(item_id) ON DELETE CASCADE, FOREIGN KEY (artist) REFERENCES artist(artist) ON DELETE NO ACTION);
CREATE TABLE title(item_id text NOT NULL, title TEXT NOT NULL, PRIMARY KEY(item_id,title), FOREIGN KEY (item_id) REFERENCES music_cd(item_id) ON DELETE CASCADE);
CREATE TABLE music_cd_artist(item_id text, artist TEXT, PRIMARY KEY(item_id, artist), FOREIGN KEY (item_id) REFERENCES music_cd(item_id) ON DELETE CASCADE);
CREATE TABLE music_cd_label(item_id text NOT NULL, label TEXT NOT NULL, PRIMARY KEY(item_id, label), FOREIGN KEY (item_id) REFERENCES music_cd(item_id) ON DELETE CASCADE, FOREIGN KEY (label) REFERENCES label(label) ON DELETE CASCADE);
		
CREATE TABLE customer(customer_name TEXT NOT NULL UNIQUE, street text NOT NULL, nr Integer NOT NULL, zip Integer NOT NULL, city TEXT NOT NULL, account_number TEXT NOT NULL UNIQUE, PRIMARY KEY(customer_name));
/*CREATE TABLE purchase(customer_id INTEGER, item_id TEXT REFERENCES item(item_id), shop_id Integer NOT NULL, order_date DATE NOT NULL, FOREIGN KEY (customer_id) REFERENCES customer(customer_id) ON DELETE NO ACTION, FOREIGN KEY(shop_id) REFERENCES shop(shop_id) ON DELETE CASCADE, PRIMARY KEY(item_id, shop_id, order_date));
*/CREATE TABLE review(review_id SERIAL PRIMARY KEY, item_id TEXT NOT NULL, customer_name text NOT NULL, review_date DATE, summary TEXT, content TEXT, rating INTEGER, FOREIGN KEY (item_id) REFERENCES item(item_id) ON DELETE CASCADE, FOREIGN KEY (customer_name) REFERENCES customer(customer_name) ON DELETE CASCADE);

/*CREATE TABLE errors(error_id SERIAL PRIMARY KEY, location TEXT, errtype ErrType, exception TEXT, error_message TEXT, contents TEXT);*/
CREATE TABLE errors(error_id SERIAL PRIMARY KEY, location TEXT, item_id TEXT, attribute TEXT, errtype ErrType, exception TEXT, error_message TEXT, contents TEXT);
		 
