package JDBCTools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import main.Config;

public class JDBCTool {
	
	public static final String KEY_DUPLICATED = "org.postgresql.util.PSQLException: ERROR: duplicate key value violates unique constraint";
	public static Connection getConnection() {
		Connection con = null;
		try {
			Class.forName("org.postgresql.Driver");
			String url = Config.JDBC_POSTGRES_URL;
			con = DriverManager.getConnection(url);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return con;
	}
	
	public static void executeUpdate(SQLExecutable worker) throws SQLException {
		Connection con = getConnection();
		try {
			con.setAutoCommit (false);
			Statement st = con.createStatement();
			
			//Implemented class will do this job.
			worker.work(con, st);
			
			st.close();
			con.commit();
			} catch (SQLException e) {
				con.rollback (); 
				throw new SQLException(e);
			} finally {
				try { 
					con.setAutoCommit(true); 
					con.close();
				} catch (SQLException e3) {
					System.out.println(e3);
				} 
			}
	}
	
	public static void executeUpdateAutoCommitOn(SQLExecutable worker) throws SQLException {
		Connection con = getConnection();
		try {
			Statement st = con.createStatement();
			con.setAutoCommit (true); 
			
			//Implemented class will do this job.
			worker.work(con, st);
			
			st.close();
			} catch (SQLException e) {
				throw new SQLException(e);
			} finally {
				try { 
					con.setAutoCommit(true); 
					con.close();
				} catch (SQLException e3) {
					System.out.println(e3);
				} 
			}
	}
}
