package main;

import java.sql.PreparedStatement;

import JDBCTools.JDBCTool;

public class ErrorLogger {
	public static void write(String location, String exception, String errorMessage, String errorContent) {
		try {
			JDBCTool.executeUpdate((conn, st) -> {
				PreparedStatement ps = conn.prepareStatement("INSERT INTO ERRORS (location, exception, error_message, contents) values (?,?,?,?)");
				ps.setString(1, location);
				ps.setString(2, exception);
				ps.setString(3, errorMessage);
				ps.setString(4, errorContent);
				ps.execute();
				ps.close();
			});
		} catch (Exception e) {
			
		}
	}
}
