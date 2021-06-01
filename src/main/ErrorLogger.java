package main;

import java.sql.PreparedStatement;

import JDBCTools.JDBCTool;

public class ErrorLogger {
	public static void write(String location, ErrType errType, Exception e, String errorContent) {

		//temp
		System.out.print(">> ErrorLogger::" + errType + "::");
		System.out.println(errorContent);

		try {
			JDBCTool.executeUpdate((conn, st) -> {
				PreparedStatement ps = conn.prepareStatement("INSERT INTO ERRORS (location, errtype, exception, error_message, contents) values (?,?::ErrType,?,?,?)");
				ps.setString(1, location);
				ps.setString(2, errType.toString());
				ps.setString(3, e.getClass().getSimpleName());
				ps.setString(4, e.getMessage());
				ps.setString(5, errorContent);
				ps.execute();
				ps.close();
			});
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}
