package main;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import JDBCTools.JDBCTool;

/**
 * Class for drop all tables in the table list.
 *
 */
public class DropTables {

	/**
	 * To drop all tables and enums
	 */
	public static void dropTables() {
		
		try {
		
			Connection con = JDBCTool.getConnection();
			Statement st = con.createStatement();
			
			dropTable("Mat View diff_main_cat", "DROP MATERIALIZED VIEW IF EXISTS diff_main_cat;", st);

			int len = CreateTables.tableOrder.size();
			//drop table in reverse order.
			for (int i = 0; i < len; i++) {
				String tableName = CreateTables.tableOrder.get(len -i-1);
				dropTable(tableName, "DROP TABLE " + tableName + ";", st);
			}

            dropEnum("ErrType", st);
            dropEnum("pgroup", st);
            
			st.close();
			con.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * To drop single table
	 * @param tableName table name
	 * @throws Exception
	 */
	public static void dropTable(String tableName) throws Exception {
		JDBCTool.executeUpdate((con, st) -> dropTable(tableName, "DROP TABLE "+tableName+";", st));
	}

	/**
	 * To drop single enum.
	 * @param enumName
	 * @param st
	 */
    public static void dropEnum(String enumName, Statement st){
        try{
				st.executeUpdate("DROP TYPE " + enumName+ ";");
				System.out.println("Enum "+enumName + " is dropped.");
			}
			catch (SQLException e){
				System.out.println(e);
			}
    }

    /**
     * To drop single table
     * @param tableName table name
     * @param sqlStr Drop SQL to execute.
     * @param st Statement
     */
	public static void dropTable(String tableName, String sqlStr, Statement st){
		try{
				st.executeUpdate(sqlStr);
				System.out.println("Table "+tableName+" is dropped.");
			}
			catch (SQLException e){
				System.out.println(e);
			}
	}


}





