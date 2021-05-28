package JDBCTools;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public interface SQLExecutable {
	public void work(Connection con, Statement st) throws SQLException;
}
