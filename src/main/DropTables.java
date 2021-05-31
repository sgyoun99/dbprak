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

			droptable("Errors", "DROP TABLE errors;", st);
			
			droptable("Review", "DROP TABLE review;", st);
			droptable("Purchase", "DROP TABLE purchase;", st);
			droptable("Customer", "DROP TABLE customer;", st);
			
			droptable("Music_CD_Label", "DROP TABLE music_cd_label;", st);
			droptable("Music_CD_Artist", "DROP TABLE music_cd_artist;", st);
			droptable("Titel", "DROP TABLE titel;", st);
			droptable("Music_CD", "DROP TABLE music_cd;", st);
			droptable("Label", "DROP TABLE label;", st);
			droptable("Artist", "DROP TABLE artist;", st);

			droptable("DVD_Director", "DROP TABLE dvd_director;", st);
			droptable("DVD_Creator", "DROP TABLE dvd_creator;", st);
			droptable("DVD_Actor", "DROP TABLE dvd_actor;", st);
			droptable("Director", "DROP TABLE director;", st);
			droptable("Creator", "DROP TABLE creator;", st);
			droptable("Actor", "DROP TABLE actor;", st);
			droptable("DVD", "DROP TABLE dvd;", st);

			droptable("Book_Publisher", "DROP TABLE book_publisher;", st);
            droptable("Book_Author", "DROP TABLE book_author;", st);
            droptable("Book", "DROP TABLE book;", st);
            droptable("Publisher", "DROP TABLE publisher;", st);
			droptable("Author", "DROP TABLE author;", st);
			
            droptable("Item_Category", "DROP TABLE item_category;", st);
            droptable("Sub_Category", "DROP TABLE sub_category;", st);
			droptable("Category", "DROP TABLE category;", st);
			
			droptable("Similiar_Items", "DROP TABLE similiar_items;", st);
            droptable("Item_Shop", "DROP TABLE item_shop;", st);
            droptable("Shop", "DROP TABLE shop;", st);
			droptable("Item", "DROP TABLE item;", st);

            dropEnum("ErrType", st);
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

	public static void droptable(String tableName, String sqlStr, Statement st){
		try{
				st.executeUpdate(sqlStr);
				System.out.println("Table "+tableName+" is droped.");
			}
			catch (SQLException e){
				System.out.println(e);
			}
	}



}





