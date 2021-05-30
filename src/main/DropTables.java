package main;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DropTables {

	public static void main(String[] args) {
		dropTables();
	}
	
	public static void dropTables() {
		
		try {
		
			Class.forName("org.postgresql.Driver");

			
			String url = "jdbc:postgresql://localhost/postgres?user=postgres&password=postgres&ssl=false";
			Connection conn = DriverManager.getConnection(url);
			
			
			Statement st = conn.createStatement();

			dropteable("Review", "DROP TABLE review;", st);
			dropteable("Purchase", "DROP TABLE purchase;", st);
			dropteable("Customer", "DROP TABLE customer;", st);
			
			dropteable("Music_CD_Label", "DROP TABLE music_cd_label;", st);
			dropteable("Music_CD_Artist", "DROP TABLE music_cd_artist;", st);
			dropteable("Titel", "DROP TABLE titel;", st);
			dropteable("Music_CD", "DROP TABLE music_cd;", st);
			dropteable("Label", "DROP TABLE label;", st);
			dropteable("Artist", "DROP TABLE artist;", st);

			dropteable("DVD_Director", "DROP TABLE dvd_director;", st);
			dropteable("DVD_Creator", "DROP TABLE dvd_creator;", st);
			dropteable("DVD_Actor", "DROP TABLE dvd_actor;", st);
			dropteable("Director", "DROP TABLE director;", st);
			dropteable("Creator", "DROP TABLE creator;", st);
			dropteable("Actor", "DROP TABLE actor;", st);
			dropteable("DVD", "DROP TABLE dvd;", st);

			dropteable("Book_Publisher", "DROP TABLE book_publisher;", st);
            dropteable("Book_Author", "DROP TABLE book_author;", st);
            dropteable("Book", "DROP TABLE book;", st);
            dropteable("Publisher", "DROP TABLE publisher;", st);
			dropteable("Author", "DROP TABLE author;", st);
			
            dropteable("Item_Category", "DROP TABLE item_category;", st);
            dropteable("Sub_Category", "DROP TABLE sub_category;", st);
			dropteable("Category", "DROP TABLE category;", st);
			
			dropteable("Similiar_Items", "DROP TABLE similiar_items;", st);
            dropteable("Item_Shop", "DROP TABLE item_shop;", st);
            dropteable("Shop", "DROP TABLE shop;", st);
			dropteable("Item", "DROP TABLE item;", st);

            dropEnum("pgroup", st);
			

            
			st.close();
			conn.close();
			

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

    public static void dropEnum(String enumName, Statement st){
        try{
				st.executeUpdate("DROP TYPE " + enumName+ ";");
				System.out.println("Enum "+enumName + " is droped.");
			}
			catch (SQLException e){
				System.out.println(e);
			}
    }

	public static void dropteable(String tableName, String sqlStr, Statement st){
		try{
				st.executeUpdate(sqlStr);
				System.out.println("Table "+tableName+" is droped.");
			}
			catch (SQLException e){
				System.out.println(e);
			}
	}



}





