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

/**
 * 
 * Class for creating all tables
 *
 */
public class CreateTables2 {
	
	
	

	
	
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

			//public static void createTable(String tableName) throws Exception {
			//	String sql = createTableSQLMap.get(tableName);
			st.executeUpdate("CREATE TABLE errors(error_id SERIAL PRIMARY KEY, location TEXT, item_id TEXT, attribute TEXT, errtype ErrType, exception TEXT, error_message TEXT, contents TEXT);");
			st.executeUpdate("CREATE TABLE item(item_id text PRIMARY KEY, title TEXT, rating numeric(2,1), salesranking INTEGER, image TEXT, productgroup pgroup NOT NULL);");
			
		
			/*public static void createTable(String tableName, String sqlStr, Statement st){
				try{
						st.executeUpdate(sqlStr);
						System.out.println("table " + tableName + " is created.");
					}
					catch (SQLException e){
						System.out.println("table " + tableName + " can not be created.");
						System.out.println(e);
					}
			}*/
			
			

           
			st.close();
			conn.close();
			

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * To create enum
	 * @param enumName enum name
	 * @param enumValues enum values as a single String
	 * @param st Statement
	 */
    public static void createEnum(String enumName, String enumValues, Statement st){
        try{
				st.executeUpdate("CREATE TYPE " + enumName + " AS ENUM (" + enumValues + ");");
				System.out.println("Enum "+enumName+"is created.");
			}
			catch (SQLException e){
				System.out.println(e);
			}
    }
    
    /**
     * To create table
     * @param tableName table name
     * @throws Exception
     */
  /*  public static void createTable(String tableName) throws Exception {
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
	
	/**
	 * To create all tables according to the table list.
	 */
	/*public static void countAllTables() {
		CreateTables.tableOrder.forEach(tableName -> {
			try {
				JDBCTool.executeUpdateAutoCommitOn((con, st) -> {
					ResultSet rs = st.executeQuery("select count(*) from " +tableName);
					if(rs.next()) {
						System.out.print(String.format(" -%20s: ", tableName));
						System.out.println(String.format("%8d rows.", rs.getInt(1)));
					}
					rs.close();
				});
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
	}*/


}
