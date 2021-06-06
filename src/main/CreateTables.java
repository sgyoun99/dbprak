/**
 * Class to create tables in DB
 * write "CREATE TABLE" statements to HashMap to allow easy reorder while programming
 * also allows easy output for DropTable
 * @version 2021-06-03
 */

package main;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import JDBCTools.JDBCTool;

public class CreateTables {
	
	public final static List<String> tableOrder = new ArrayList<>();
	public final static Map<String, String> createTableSQLMap = new HashMap<String, String>();
	public final static String Item = "Item";
	public final static String Shop = "Shop";
	public final static String Item_Shop = "Item_Shop";
	public final static String Similar_Items = "Similar_Items";

	public final static String Category = "Category";
	public final static String Sub_Category = "Sub_Category";
	public final static String Item_Category = "Item_Category";

	public final static String Author = "Author";
	public final static String Publisher = "Publisher";
	public final static String Book = "Book";
	public final static String Book_Author = "Book_Author";
	public final static String Book_Publisher = "Book_Publisher";

	public final static String DVD = "DVD";
	public final static String Actor = "Actor";
	public final static String Creator = "Creator";
	public final static String Director = "Director";
	public final static String DVD_Actor = "DVD_Actor";
	public final static String DVD_Creator = "DVD_Creator";
	public final static String DVD_Director = "DVD_Director";

	public final static String Artist = "Artist";
	public final static String Label = "Label";
	public final static String Music_CD = "Music_CD";
	public final static String Title = "Title";
	public final static String Music_CD_Artist = "Music_CD_Artist";
	public final static String Music_CD_Label = "Music_CD_Label";

	public final static String Customer = "Customer";
	public final static String Purchase = "Purchase";
	public final static String Review = "Review";

	public final static String Errors = "Errors";

	static {
		
		//create Table statements
        createTableSQLMap.put(CreateTables.Item, "CREATE TABLE item(item_id text PRIMARY KEY, title TEXT, rating numeric(2,1), salesranking INTEGER, image TEXT, productgroup pgroup NOT NULL);");
        createTableSQLMap.put(CreateTables.Shop, "CREATE TABLE shop(shop_name TEXT NOT NULL, street TEXT NOT NULL, zip CHAR(5) NOT NULL, PRIMARY KEY(shop_name, street, zip));");
        createTableSQLMap.put(CreateTables.Item_Shop, "CREATE TABLE item_shop(item_id text NOT NULL, shop_name TEXT NOT NULL, street TEXT NOT NULL, zip CHAR(5) NOT NULL, currency CHAR(3), price numeric(8,2), availability BOOLEAN, condition CHAR(11), FOREIGN KEY(item_id) REFERENCES item(item_id) ON DELETE CASCADE, FOREIGN KEY(shop_name, street, zip) REFERENCES shop(shop_name, street, zip) ON DELETE CASCADE, PRIMARY KEY(item_id, shop_name, street, zip));");
		createTableSQLMap.put(CreateTables.Similar_Items, "CREATE TABLE similar_items(item_id text, similar_item_id text, PRIMARY KEY(item_id, similar_item_id), FOREIGN KEY(item_id) REFERENCES item(item_id) ON DELETE CASCADE, FOREIGN KEY (similar_item_id) REFERENCES item(item_id) ON DELETE CASCADE);");
		
		//createTableSQLMap.put(CreateTables.Category, "CREATE TABLE category(category_id SERIAL PRIMARY KEY NOT NULL, name TEXT);");
		createTableSQLMap.put(CreateTables.Category, "CREATE TABLE category(category_id Integer PRIMARY KEY NOT NULL, name TEXT);");
        createTableSQLMap.put(CreateTables.Sub_Category, "CREATE TABLE sub_category(main_category_id INTEGER REFERENCES category(category_id), sub_category_id INTEGER REFERENCES category(category_id), PRIMARY KEY(main_category_id, sub_category_id));");
        createTableSQLMap.put(CreateTables.Item_Category, "CREATE TABLE item_category(item_id text REFERENCES item(item_id), category_id INTEGER REFERENCES category(category_id), PRIMARY KEY(item_id, category_id));");
        
		createTableSQLMap.put(CreateTables.Author, "CREATE TABLE author(author TEXT PRIMARY KEY);");
        createTableSQLMap.put(CreateTables.Publisher, "CREATE TABLE publisher(publisher TEXT PRIMARY KEY);");
        createTableSQLMap.put(CreateTables.Book, "CREATE TABLE book(item_id text PRIMARY KEY, pages SMALLINT, publication_date DATE, isbn TEXT, FOREIGN KEY(item_id) REFERENCES item(item_id) ON DELETE CASCADE);");
        createTableSQLMap.put(CreateTables.Book_Author, "CREATE TABLE book_author(item_id text, author TEXT REFERENCES author(author), PRIMARY KEY(item_id, author), FOREIGN KEY(item_id) REFERENCES book(item_id) ON DELETE CASCADE, FOREIGN KEY (author) REFERENCES author(author) ON DELETE CASCADE);");
		createTableSQLMap.put(CreateTables.Book_Publisher, "CREATE TABLE book_publisher(item_id text, publisher TEXT, PRIMARY KEY(item_id, publisher), FOREIGN KEY (item_id) REFERENCES book(item_id) ON DELETE CASCADE, FOREIGN KEY (publisher) REFERENCES publisher(publisher) ON DELETE CASCADE);");
		
		createTableSQLMap.put(CreateTables.DVD, "CREATE TABLE dvd(item_id text PRIMARY KEY, format TEXT, runningtime SMALLINT, regioncode CHAR(5), FOREIGN KEY (item_id) REFERENCES item(item_id) ON DELETE CASCADE);");
		createTableSQLMap.put(CreateTables.Actor, "CREATE TABLE actor(actor TEXT PRIMARY KEY);");
		createTableSQLMap.put(CreateTables.Creator, "CREATE TABLE creator(creator TEXT PRIMARY KEY);");
		createTableSQLMap.put(CreateTables.Director, "CREATE TABLE director(director TEXT PRIMARY KEY);");
		createTableSQLMap.put(CreateTables.DVD_Actor, "CREATE TABLE dvd_actor(item_id text NOT NULL, actor text NOT NULL, PRIMARY KEY(item_id, actor), FOREIGN KEY(item_id) REFERENCES dvd(item_id) ON DELETE CASCADE, FOREIGN KEY(actor) REFERENCES actor(actor) ON DELETE CASCADE);");
		createTableSQLMap.put(CreateTables.DVD_Creator, "CREATE TABLE dvd_creator(item_id text NOT NULL, creator text NOT NULL, PRIMARY KEY (item_id, creator), FOREIGN KEY(item_id) REFERENCES dvd(item_id) ON DELETE CASCADE, FOREIGN KEY(creator) REFERENCES creator(creator) ON DELETE CASCADE);");
		createTableSQLMap.put(CreateTables.DVD_Director, "CREATE TABLE dvd_director(item_id text NOT NULL, director TEXT NOT NULL, PRIMARY KEY(item_id, director), FOREIGN KEY(item_id) REFERENCES dvd(item_id) ON DELETE CASCADE, FOREIGN KEY(director) REFERENCES director(director) ON DELETE CASCADE);");

		createTableSQLMap.put(CreateTables.Artist, "CREATE TABLE artist(artist TEXT PRIMARY KEY);");          
		createTableSQLMap.put(CreateTables.Label, "CREATE TABLE label(label TEXT PRIMARY KEY);");  
		createTableSQLMap.put(CreateTables.Music_CD, "CREATE TABLE music_cd(item_id text PRIMARY KEY, artist TEXT NOT NULL, release_date DATE, FOREIGN KEY (item_id) REFERENCES item(item_id) ON DELETE CASCADE, FOREIGN KEY (artist) REFERENCES artist(artist) ON DELETE NO ACTION);");
		createTableSQLMap.put(CreateTables.Title, "CREATE TABLE title(item_id text, title TEXT, PRIMARY KEY(item_id, title), FOREIGN KEY (item_id) REFERENCES music_cd(item_id) ON DELETE CASCADE);");
		createTableSQLMap.put(CreateTables.Music_CD_Artist, "CREATE TABLE music_cd_artist(item_id text, artist TEXT REFERENCES artist(artist), PRIMARY KEY(item_id, artist), FOREIGN KEY (item_id) REFERENCES music_cd(item_id) ON DELETE CASCADE);"); //ON DELETE CASCADE for artist unneccesary -> cannot be deleted before cd is deleted
		createTableSQLMap.put(CreateTables.Music_CD_Label, "CREATE TABLE music_cd_label(item_id text NOT NULL, label TEXT NOT NULL, PRIMARY KEY(item_id, label), FOREIGN KEY (item_id) REFERENCES music_cd(item_id) ON DELETE CASCADE, FOREIGN KEY (label) REFERENCES label(label) ON DELETE CASCADE);");
		
		createTableSQLMap.put(CreateTables.Customer, "CREATE TABLE customer(customer_id SERIAL PRIMARY KEY, name TEXT NOT NULL, street CHAR(5) NOT NULL, nr SMALLINT NOT NULL, zip SMALLINT NOT NULL, city TEXT NOT NULL, account_number TEXT NOT NULL UNIQUE);");
		createTableSQLMap.put(CreateTables.Purchase, "CREATE TABLE purchase(customer_id INTEGER, item_id TEXT REFERENCES item(item_id), shop_name TEXT NOT NULL, street TEXT NOT NULL, zip CHAR(5) NOT NULL, order_date DATE NOT NULL, FOREIGN KEY (customer_id) REFERENCES customer(customer_id) ON DELETE NO ACTION, FOREIGN KEY(shop_name, street, zip) REFERENCES shop(shop_name, street, zip) ON DELETE CASCADE, PRIMARY KEY(item_id, shop_name, street, zip, order_date));"); // No ON DELETE CASCADE for customer_id -> allows keeping of record even if all other data from customer is deleted
		createTableSQLMap.put(CreateTables.Review, "CREATE TABLE review(review_id SERIAL PRIMARY KEY, item_id TEXT, customer TEXT"/* REFERENCES customer(customer_id)*/+", review_date DATE, summary TEXT, content TEXT, rating SMALLINT, FOREIGN KEY (item_id) REFERENCES item(item_id) ON DELETE CASCADE);");

		createTableSQLMap.put(CreateTables.Errors, "CREATE TABLE errors(error_id SERIAL PRIMARY KEY, location TEXT, errtype ErrType, exception TEXT, error_message TEXT, contents TEXT);"); 
		createTableSQLMap.put(CreateTables.Errors, "CREATE TABLE errors(error_id SERIAL PRIMARY KEY, location TEXT, item_id TEXT, attribute TEXT, errtype ErrType, exception TEXT, error_message TEXT, contents TEXT);"); 
		
		
		//order
		tableOrder.add(CreateTables.Item);
        tableOrder.add(CreateTables.Shop);
        tableOrder.add(CreateTables.Item_Shop);
		tableOrder.add(CreateTables.Similar_Items);
		
		tableOrder.add(CreateTables.Category);
        tableOrder.add(CreateTables.Sub_Category);
        tableOrder.add(CreateTables.Item_Category);
        
		tableOrder.add(CreateTables.Author);
        tableOrder.add(CreateTables.Publisher);
        tableOrder.add(CreateTables.Book);
        tableOrder.add(CreateTables.Book_Author);
		tableOrder.add(CreateTables.Book_Publisher);
		
		tableOrder.add(CreateTables.DVD);
		tableOrder.add(CreateTables.Actor);
		tableOrder.add(CreateTables.Creator);
		tableOrder.add(CreateTables.Director);
		tableOrder.add(CreateTables.DVD_Actor);
		tableOrder.add(CreateTables.DVD_Creator);
		tableOrder.add(CreateTables.DVD_Director);

		tableOrder.add(CreateTables.Artist);
		tableOrder.add(CreateTables.Label);
		tableOrder.add(CreateTables.Music_CD);
		tableOrder.add(CreateTables.Title);
		tableOrder.add(CreateTables.Music_CD_Artist);
		tableOrder.add(CreateTables.Music_CD_Label);
		
		tableOrder.add(CreateTables.Customer);
		tableOrder.add(CreateTables.Purchase);
		tableOrder.add(CreateTables.Review);

		tableOrder.add(CreateTables.Errors);
			
	}
	
	public static void main(String[] args) {
		createTables();
	}
	
	
	/**
	 * Function to create enums and tables for the DB
	 * gets connection
	 * create tables for all key-value pairs in local map  + enums
	 */
	public static void createTables() {
		try {
			
			Class.forName("org.postgresql.Driver");

			
			String url = "jdbc:postgresql://localhost/postgres?user=postgres&password=postgres&ssl=false";
			Connection conn = DriverManager.getConnection(url);
			
			
			Statement st = conn.createStatement();
			
			//create Enum
            createEnum("pgroup", "'Book', 'Music_CD', 'DVD'", st);
            createEnum("ErrType", "'XML', 'XML_NO_VALUE', 'XML_INVALID_VALUE', 'XML_DATA_INCOMPLETE', 'XML_NO_ATTRIBUTE', 'XML_NO_NODE', 'SQL', 'SQL_FK_ERROR', 'SQL_DUPLICATE', 'PROGRAM'", st);

			//create Tables
			tableOrder.forEach(tableName -> {
				createTable(tableName, createTableSQLMap.get(tableName), st);
			});

           
			st.close();
			conn.close();
			

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

    public static void createEnum(String enumName, String enumValues, Statement st){
        try{
				st.executeUpdate("CREATE TYPE " + enumName + " AS ENUM (" + enumValues + ");");
				System.out.println("Enum "+enumName+"is created.");
			}
			catch (SQLException e){
				System.out.println(e);
			}
    }
    
    public static void createTable(String tableName) throws Exception {
    	String sql = createTableSQLMap.get(tableName);
    	JDBCTool.executeUpdate((con, st) -> createTable(tableName, sql, st));
    }

	public static void createTable(String tableName, String sqlStr, Statement st){
		try{
				st.executeUpdate(sqlStr);
				System.out.println("table " + tableName + " is created.");
			}
			catch (SQLException e){
				System.out.println("table " + tableName + " can not be created.");
				System.out.println(e);
			}
	}



}
