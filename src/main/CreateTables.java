package main;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import JDBCTools.JDBCTool;

public class CreateTables {
	
	public static List<String> tableOrder = new ArrayList<>();
	public static Map<String, String> createTableMap = new HashMap<String, String>();

	static {
        
        createTableMap.put("Item", "CREATE TABLE item(item_id CHAR(10) PRIMARY KEY, title TEXT, rating numeric(2,1), salesranking INTEGER, image TEXT, productgroup pgroup NOT NULL);");
        createTableMap.put("Shop", "CREATE TABLE shop(shop_name TEXT NOT NULL, street TEXT NOT NULL, zip CHAR(5) NOT NULL, PRIMARY KEY(shop_name, street, zip));");
        createTableMap.put("Item_Shop", "CREATE TABLE item_shop(item_id CHAR(10) REFERENCES item(item_id) NOT NULL, shop_name TEXT NOT NULL, street TEXT NOT NULL, zip CHAR(5) NOT NULL, currency CHAR(3), price numeric(8,2), availability BOOLEAN, condition CHAR(11), FOREIGN KEY(shop_name, street, zip) REFERENCES shop(shop_name, street, zip), PRIMARY KEY(item_id, shop_name, street, zip));");
		createTableMap.put("Similar_Items", "CREATE TABLE similar_items(item_id CHAR(10) REFERENCES item(item_id), similar_item_id CHAR(10) REFERENCES item(item_id), PRIMARY KEY(item_id, similar_item_id));");
		
		createTableMap.put("Category", "CREATE TABLE category(category_id SERIAL PRIMARY KEY NOT NULL, name TEXT);");
        createTableMap.put("Sub_Category", "CREATE TABLE sub_category(main_category_id INTEGER REFERENCES category(category_id), sub_category_id INTEGER REFERENCES category(category_id), PRIMARY KEY(main_category_id, sub_category_id));");
        createTableMap.put("Item_Category", "CREATE TABLE item_category(item_id char(10) REFERENCES item(item_id), category_id INTEGER REFERENCES category(category_id), PRIMARY KEY(item_id, category_id));");
        
		createTableMap.put("Author", "CREATE TABLE author(author TEXT PRIMARY KEY);");
        createTableMap.put("Publisher", "CREATE TABLE publisher(publisher TEXT PRIMARY KEY);");
        createTableMap.put("Book", "CREATE TABLE book(item_id char(10) REFERENCES item(item_id) PRIMARY KEY, author TEXT REFERENCES author(author), publisher TEXT REFERENCES publisher(publisher), pages SMALLINT, publication_date DATE, isbn BIGINT);");
        createTableMap.put("Book_Author", "CREATE TABLE book_author(item_id char(10) REFERENCES book(item_id), author TEXT REFERENCES author(author), PRIMARY KEY(item_id, author));");
		createTableMap.put("Book_Publisher", "CREATE TABLE book_publisher(item_id char(10) REFERENCES book(item_id), publisher TEXT REFERENCES publisher(publisher), PRIMARY KEY(item_id, publisher));");
		
		createTableMap.put("DVD", "CREATE TABLE dvd(item_id char(10) REFERENCES item(item_id) PRIMARY KEY, format TEXT, runningtime SMALLINT, regioncode CHAR(5));");
		createTableMap.put("Actor", "CREATE TABLE actor(actor TEXT PRIMARY KEY);");
		createTableMap.put("Creator", "CREATE TABLE creator(creator TEXT PRIMARY KEY);");
		createTableMap.put("Director", "CREATE TABLE director(director TEXT PRIMARY KEY);");
		createTableMap.put("DVD_Actor", "CREATE TABLE dvd_actor(item_id char(10) REFERENCES dvd(item_id) NOT NULL, actor text NOT NULL REFERENCES actor(actor), PRIMARY KEY(item_id, actor));");
		createTableMap.put("DVD_Creator", "CREATE TABLE dvd_creator(item_id char(10) REFERENCES dvd(item_id) NOT NULL, creator text REFERENCES creator(creator), PRIMARY KEY (item_id, creator));");
		createTableMap.put("DVD_Director", "CREATE TABLE dvd_director(item_id char(10) REFERENCES dvd(item_id) NOT NULL, director TEXT NOT NULL REFERENCES director(director), PRIMARY KEY(item_id, director));");

		createTableMap.put("Artist", "CREATE TABLE artist(artist TEXT PRIMARY KEY);");          
		createTableMap.put("Label", "CREATE TABLE label(label TEXT PRIMARY KEY);");  
		createTableMap.put("Music_CD", "CREATE TABLE music_cd(item_id char(10) REFERENCES item(item_id) PRIMARY KEY, artist TEXT NOT NULL REFERENCES artist(artist), release_date DATE);");
		createTableMap.put("Titel", "CREATE TABLE titel(item_id char(10) REFERENCES music_cd(item_id), titel TEXT, PRIMARY KEY(item_id, titel));");
		createTableMap.put("Music_CD_Artist", "CREATE TABLE music_cd_artist(item_id char(10) REFERENCES music_cd(item_id), artist TEXT REFERENCES artist(artist), PRIMARY KEY(item_id, artist));");
		createTableMap.put("Music_CD_Label", "CREATE TABLE music_cd_label(item_id char(10) REFERENCES music_cd(item_id), label TEXT REFERENCES label(label), PRIMARY KEY(item_id, label));");
		
		createTableMap.put("Customer", "CREATE TABLE customer(customer_id TEXT PRIMARY KEY, street CHAR(5), nr SMALLINT, zip SMALLINT, city TEXT, account_number TEXT UNIQUE NOT NULL);");
		createTableMap.put("Purchase", "CREATE TABLE purchase(customer_id TEXT REFERENCES customer(customer_id), item_id CHAR(10) REFERENCES item(item_id), shop_name TEXT NOT NULL, street TEXT NOT NULL, zip CHAR(5) NOT NULL, order_date DATE NOT NULL, FOREIGN KEY(shop_name, street, zip) REFERENCES shop(shop_name, street, zip), PRIMARY KEY(item_id, shop_name, street, zip, order_date));");
		createTableMap.put("Review", "CREATE TABLE review(item_id CHAR(10) REFERENCES item(item_id), customer_id TEXT REFERENCES customer(customer_id), review_date DATE, summary TEXT, contect TEXT, rating SMALLINT, PRIMARY KEY(item_id, customer_id));");

		createTableMap.put("Errors", "CREATE TABLE errors(error_id SERIAL PRIMARY KEY, location TEXT, errtype ErrType, exception TEXT, error_message TEXT, contents TEXT);"); 
		
		

		tableOrder.add("Item");
        tableOrder.add("Shop");
        tableOrder.add("Item_Shop");
		tableOrder.add("Similar_Items");
		
		tableOrder.add("Category");
        tableOrder.add("Sub_Category");
        tableOrder.add("Item_Category");
        
		tableOrder.add("Author");
        tableOrder.add("Publisher");
        tableOrder.add("Book");
        tableOrder.add("Book_Author");
		tableOrder.add("Book_Publisher");
		
		tableOrder.add("DVD");
		tableOrder.add("Actor");
		tableOrder.add("Creator");
		tableOrder.add("Director");
		tableOrder.add("DVD_Actor");
		tableOrder.add("DVD_Creator");
		tableOrder.add("DVD_Director");

		tableOrder.add("Artist");
		tableOrder.add("Label");
		tableOrder.add("Music_CD");
		tableOrder.add("Titel");
		tableOrder.add("Music_CD_Artist");
		tableOrder.add("Music_CD_Label");
		
		tableOrder.add("Customer");
		tableOrder.add("Purchase");
		tableOrder.add("Review");

		tableOrder.add("Errors");
			
	}
	
	public static void main(String[] args) {
		createTables();
	}
	
	// we need CASCADE, NO ACTION 
	public static void createTables() {
		try {
			
			Class.forName("org.postgresql.Driver");

			
			String url = "jdbc:postgresql://localhost/postgres?user=postgres&password=postgres&ssl=false";
			Connection conn = DriverManager.getConnection(url);
			
			
			Statement st = conn.createStatement();
			
			//create Enum
            createEnum("pgroup", "'Book', 'Music_CD', 'DVD'", st);
            createEnum("ErrType", "'XML', 'SQL', 'PROGRAM'", st);

			//create Tables
			tableOrder.forEach(tableName -> {
				createTable(tableName, createTableMap.get(tableName), st);
			});

			/*

			createTable("Item", "CREATE TABLE item(item_id CHAR(10) PRIMARY KEY, title TEXT, rating numeric(2,1), salesranking INTEGER, image TEXT, productgroup pgroup NOT NULL);", st);
            createTable("Shop", "CREATE TABLE shop(shop_name TEXT NOT NULL, street TEXT NOT NULL, zip CHAR(5) NOT NULL, PRIMARY KEY(shop_name, street, zip));", st);
            createTable("Item_Shop", "CREATE TABLE item_shop(item_id CHAR(10) REFERENCES item(item_id) NOT NULL, shop_name TEXT NOT NULL, street TEXT NOT NULL, zip CHAR(5) NOT NULL, currency CHAR(3), price numeric(8,2), availability BOOLEAN, condition CHAR(11), FOREIGN KEY(shop_name, street, zip) REFERENCES shop(shop_name, street, zip), PRIMARY KEY(item_id, shop_name, street, zip));", st);
			createTable("Similar_Items", "CREATE TABLE similar_items(item_id CHAR(10) REFERENCES item(item_id), similar_item_id CHAR(10) REFERENCES item(item_id), PRIMARY KEY(item_id, similar_item_id));", st);
			
			createTable("Category", "CREATE TABLE category(category_id SERIAL PRIMARY KEY NOT NULL, name TEXT);", st);
            createTable("Sub_Category", "CREATE TABLE sub_category(main_category_id INTEGER REFERENCES category(category_id), sub_category_id INTEGER REFERENCES category(category_id), PRIMARY KEY(main_category_id, sub_category_id));", st);
            createTable("Item_Category", "CREATE TABLE item_category(item_id char(10) REFERENCES item(item_id), category_id INTEGER REFERENCES category(category_id), PRIMARY KEY(item_id, category_id));", st);
            
			createTable("Author", "CREATE TABLE author(author TEXT PRIMARY KEY);", st);
            createTable("Publisher", "CREATE TABLE publisher(publisher TEXT PRIMARY KEY);", st);
            createTable("Book", "CREATE TABLE book(item_id char(10) REFERENCES item(item_id) PRIMARY KEY, author TEXT REFERENCES author(author), publisher TEXT REFERENCES publisher(publisher), pages SMALLINT, publication_date DATE, isbn BIGINT);", st);
            createTable("Book_Author", "CREATE TABLE book_author(item_id char(10) REFERENCES book(item_id), author TEXT REFERENCES author(author), PRIMARY KEY(item_id, author));", st);
			createTable("Book_Publisher", "CREATE TABLE book_publisher(item_id char(10) REFERENCES book(item_id), publisher TEXT REFERENCES publisher(publisher), PRIMARY KEY(item_id, publisher));", st);
			
			createTable("DVD", "CREATE TABLE dvd(item_id char(10) REFERENCES item(item_id) PRIMARY KEY, format TEXT, runningtime SMALLINT, regiocode SMALLINT);", st);
			createTable("Actor", "CREATE TABLE actor(actor TEXT PRIMARY KEY);", st);
			createTable("Creator", "CREATE TABLE creator(creator TEXT PRIMARY KEY);", st);
			createTable("Director", "CREATE TABLE director(director TEXT PRIMARY KEY);", st);
			createTable("DVD_Actor", "CREATE TABLE dvd_actor(item_id char(10) REFERENCES dvd(item_id) NOT NULL, actor text NOT NULL REFERENCES actor(actor), PRIMARY KEY(item_id, actor));", st);
			createTable("DVD_Creator", "CREATE TABLE dvd_creator(item_id char(10) REFERENCES dvd(item_id) NOT NULL, creator text REFERENCES creator(creator), PRIMARY KEY (item_id, creator));", st);
			createTable("DVD_Director", "CREATE TABLE dvd_director(item_id char(10) REFERENCES dvd(item_id) NOT NULL, director TEXT NOT NULL REFERENCES director(director), PRIMARY KEY(item_id, director));", st);

			createTable("Artist", "CREATE TABLE artist(artist TEXT PRIMARY KEY);", st);          
			createTable("Label", "CREATE TABLE label(label TEXT PRIMARY KEY);", st);  
			createTable("Music_CD", "CREATE TABLE music_cd(item_id char(10) REFERENCES item(item_id) PRIMARY KEY, artist TEXT NOT NULL REFERENCES artist(artist), release_date DATE);", st);
			createTable("Titel", "CREATE TABLE titel(item_id char(10) REFERENCES music_cd(item_id), titel TEXT, PRIMARY KEY(item_id, titel));", st);
			createTable("Music_CD_Artist", "CREATE TABLE music_cd_artist(item_id char(10) REFERENCES music_cd(item_id), artist TEXT REFERENCES artist(artist), PRIMARY KEY(item_id, artist));", st);
			createTable("Music_CD_Label", "CREATE TABLE music_cd_label(item_id char(10) REFERENCES music_cd(item_id), label TEXT REFERENCES label(label), PRIMARY KEY(item_id, label));", st);
			
			createTable("Customer", "CREATE TABLE customer(customer_id TEXT PRIMARY KEY, street TEXT, nr SMALLINT, zip SMALLINT, city TEXT, account_number BIGINT UNIQUE NOT NULL);", st);
			createTable("Purchase", "CREATE TABLE purchase(customer_id TEXT REFERENCES customer(customer_id), item_id CHAR(10) REFERENCES item(item_id), shop_name TEXT NOT NULL, street TEXT NOT NULL, zip CHAR(5) NOT NULL, order_date DATE NOT NULL, FOREIGN KEY(shop_name, street, zip) REFERENCES shop(shop_name, street, zip), PRIMARY KEY(item_id, shop_name, street, zip, order_date));", st);
			createTable("Review", "CREATE TABLE review(item_id CHAR(10) REFERENCES item(item_id), customer_id TEXT REFERENCES customer(customer_id), review_date DATE, summary TEXT, contect TEXT, rating SMALLINT, PRIMARY KEY(item_id, customer_id));", st);

			createTable("Errors", "CREATE TABLE errors(error_id SERIAL PRIMARY KEY, location TEXT, errtype ErrType, exception TEXT, error_message TEXT, contents TEXT);", st);
			 */
            
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
    	String sql = createTableMap.get(tableName);
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





