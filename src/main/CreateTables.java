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
	public static String Item = "Item";
	public static String Shop = "Shop";
	public static String Item_Shop = "Item_Shop";
	public static String Similar_Items = "Similar_Items";

	public static String Category = "Category";
	public static String Sub_Category = "Sub_Category";
	public static String Item_Category = "Item_Category";

	public static String Author = "Author";
	public static String Publisher = "Publisher";
	public static String Book = "Book";
	public static String Book_Author = "Book_Author";
	public static String Book_Publisher = "Book_Publisher";

	public static String DVD = "DVD";
	public static String Actor = "Actor";
	public static String Creator = "Creator";
	public static String Director = "Director";
	public static String DVD_Actor = "DVD_Actor";
	public static String DVD_Creator = "DVD_Creator";
	public static String DVD_Director = "DVD_Director";

	public static String Artist = "Artist";
	public static String Label = "Label";
	public static String Music_CD = "Music_CD";
	public static String Title = "Title";
	public static String Music_CD_Artist = "Music_CD_Artist";
	public static String Music_CD_Label = "Music_CD_Label";

	public static String Customer = "Customer";
	public static String Purchase = "Purchase";
	public static String Review = "Review";

	public static String Errors = "Errors";

	static {
        
        createTableMap.put(CreateTables.Item, "CREATE TABLE item(item_id CHAR(10) PRIMARY KEY, title TEXT, rating numeric(2,1), salesranking INTEGER, image TEXT, productgroup pgroup NOT NULL);");
        createTableMap.put(CreateTables.Shop, "CREATE TABLE shop(shop_name TEXT NOT NULL, street TEXT NOT NULL, zip CHAR(5) NOT NULL, PRIMARY KEY(shop_name, street, zip));");
        createTableMap.put(CreateTables.Item_Shop, "CREATE TABLE item_shop(item_id CHAR(10) REFERENCES item(item_id) NOT NULL, shop_name TEXT NOT NULL, street TEXT NOT NULL, zip CHAR(5) NOT NULL, currency CHAR(3), price numeric(8,2), availability BOOLEAN, condition CHAR(11), FOREIGN KEY(shop_name, street, zip) REFERENCES shop(shop_name, street, zip), PRIMARY KEY(item_id, shop_name, street, zip));");
		createTableMap.put(CreateTables.Similar_Items, "CREATE TABLE similar_items(item_id CHAR(10) REFERENCES item(item_id), similar_item_id CHAR(10) REFERENCES item(item_id), PRIMARY KEY(item_id, similar_item_id));");
		
		createTableMap.put(CreateTables.Category, "CREATE TABLE category(category_id SERIAL PRIMARY KEY NOT NULL, name TEXT);");
        createTableMap.put(CreateTables.Sub_Category, "CREATE TABLE sub_category(main_category_id INTEGER REFERENCES category(category_id), sub_category_id INTEGER REFERENCES category(category_id), PRIMARY KEY(main_category_id, sub_category_id));");
        createTableMap.put(CreateTables.Item_Category, "CREATE TABLE item_category(item_id char(10) REFERENCES item(item_id), category_id INTEGER REFERENCES category(category_id), PRIMARY KEY(item_id, category_id));");
        
		createTableMap.put(CreateTables.Author, "CREATE TABLE author(author TEXT PRIMARY KEY);");
        createTableMap.put(CreateTables.Publisher, "CREATE TABLE publisher(publisher TEXT PRIMARY KEY);");
        createTableMap.put(CreateTables.Book, "CREATE TABLE book(item_id char(10) REFERENCES item(item_id) PRIMARY KEY, pages SMALLINT, publication_date DATE, isbn TEXT);");
        createTableMap.put(CreateTables.Book_Author, "CREATE TABLE book_author(item_id char(10) REFERENCES book(item_id), author TEXT REFERENCES author(author), PRIMARY KEY(item_id, author));");
		createTableMap.put(CreateTables.Book_Publisher, "CREATE TABLE book_publisher(item_id char(10) REFERENCES book(item_id), publisher TEXT REFERENCES publisher(publisher), PRIMARY KEY(item_id, publisher));");
		
		createTableMap.put(CreateTables.DVD, "CREATE TABLE dvd(item_id char(10) REFERENCES item(item_id) PRIMARY KEY, format TEXT, runningtime SMALLINT, regioncode CHAR(5));");
		createTableMap.put(CreateTables.Actor, "CREATE TABLE actor(actor TEXT PRIMARY KEY);");
		createTableMap.put(CreateTables.Creator, "CREATE TABLE creator(creator TEXT PRIMARY KEY);");
		createTableMap.put(CreateTables.Director, "CREATE TABLE director(director TEXT PRIMARY KEY);");
		createTableMap.put(CreateTables.DVD_Actor, "CREATE TABLE dvd_actor(item_id char(10) REFERENCES dvd(item_id) NOT NULL, actor text NOT NULL REFERENCES actor(actor), PRIMARY KEY(item_id, actor));");
		createTableMap.put(CreateTables.DVD_Creator, "CREATE TABLE dvd_creator(item_id char(10) REFERENCES dvd(item_id) NOT NULL, creator text REFERENCES creator(creator), PRIMARY KEY (item_id, creator));");
		createTableMap.put(CreateTables.DVD_Director, "CREATE TABLE dvd_director(item_id char(10) REFERENCES dvd(item_id) NOT NULL, director TEXT NOT NULL REFERENCES director(director), PRIMARY KEY(item_id, director));");

		createTableMap.put(CreateTables.Artist, "CREATE TABLE artist(artist TEXT PRIMARY KEY);");          
		createTableMap.put(CreateTables.Label, "CREATE TABLE label(label TEXT PRIMARY KEY);");  
		createTableMap.put(CreateTables.Music_CD, "CREATE TABLE music_cd(item_id char(10) REFERENCES item(item_id) PRIMARY KEY, artist TEXT NOT NULL REFERENCES artist(artist), release_date DATE);");
		createTableMap.put(CreateTables.Title, "CREATE TABLE title(item_id char(10) REFERENCES music_cd(item_id), title TEXT, PRIMARY KEY(item_id, title));");
		createTableMap.put(CreateTables.Music_CD_Artist, "CREATE TABLE music_cd_artist(item_id char(10) REFERENCES music_cd(item_id), artist TEXT REFERENCES artist(artist), PRIMARY KEY(item_id, artist));");
		createTableMap.put(CreateTables.Music_CD_Label, "CREATE TABLE music_cd_label(item_id char(10) REFERENCES music_cd(item_id), label TEXT REFERENCES label(label), PRIMARY KEY(item_id, label));");
		
		createTableMap.put(CreateTables.Customer, "CREATE TABLE customer(customer_id TEXT PRIMARY KEY, street CHAR(5), nr SMALLINT, zip SMALLINT, city TEXT, account_number TEXT UNIQUE NOT NULL);");
		createTableMap.put(CreateTables.Purchase, "CREATE TABLE purchase(customer_id TEXT REFERENCES customer(customer_id), item_id CHAR(10) REFERENCES item(item_id), shop_name TEXT NOT NULL, street TEXT NOT NULL, zip CHAR(5) NOT NULL, order_date DATE NOT NULL, FOREIGN KEY(shop_name, street, zip) REFERENCES shop(shop_name, street, zip), PRIMARY KEY(item_id, shop_name, street, zip, order_date));");
		createTableMap.put(CreateTables.Review, "CREATE TABLE review(item_id CHAR(10) REFERENCES item(item_id), customer_id TEXT REFERENCES customer(customer_id), review_date DATE, summary TEXT, contect TEXT, rating SMALLINT, PRIMARY KEY(item_id, customer_id));");

		createTableMap.put(CreateTables.Errors, "CREATE TABLE errors(error_id SERIAL PRIMARY KEY, location TEXT, errtype ErrType, exception TEXT, error_message TEXT, contents TEXT);"); 
		
		

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
