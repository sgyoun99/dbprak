package main;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;

import JDBCTools.JDBCTool;

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
			
			int len = CreateTables.tableOrder.size();
			for (int i = 0; i < len; i++) {
				String tableName = CreateTables.tableOrder.get(len -i-1);
				dropTable(tableName, "DROP TABLE " + tableName + ";", st);
			}


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
	
	public static void dropTable(String tableName) throws Exception {
		JDBCTool.executeUpdate((con, st) -> dropTable(tableName, "DROP TABLE "+tableName+";", st));
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

	public static void dropTable(String tableName, String sqlStr, Statement st){
		try{
				st.executeUpdate(sqlStr);
				System.out.println("Table "+tableName+" is droped.");
			}
			catch (SQLException e){
				System.out.println(e);
			}
	}



}





