package JDBCTools;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 
 * Interface for SQL Handler
 *
 */
public interface SQLExecutable {
	
	/**
	 * 
	 * @param con Connection
	 * @param st Statement
	 * @throws SQLException
	 */
	public void handle(Connection con, Statement st) throws SQLException;
}
