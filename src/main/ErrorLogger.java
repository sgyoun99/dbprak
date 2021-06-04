package main;

import java.sql.PreparedStatement;

import JDBCTools.JDBCTool;
import exception.SQLKeyDuplicatedException;
import exception.XmlDataException;

public class ErrorLogger {

	public static boolean willLogSQL_DUPLICATE = false;
	private final static String sql = "INSERT INTO ERRORS ("
			+ "location, item_id, attribute, errtype, exception, error_message, contents) values "
			+ "(?,?,?,?::ErrType,?,?,?);" ;
	
	public static void write(XmlDataException e, String errorContent) throws RuntimeException{

		if( !willLogSQL_DUPLICATE && e.getMessage().contains("duplicate key value")) {
			//not logging
		} else {
			
			//temp
			System.out.print(">> ErrorLogger::" + e.getErrType() + "::");
			System.out.println(errorContent.split("\n")[0]);

			try {
				JDBCTool.executeUpdate((conn, st) -> {
					PreparedStatement ps = conn.prepareStatement(ErrorLogger.sql);
					ps.setString(1, e.getLocation());
					ps.setString(2, e.getItem_id());
					ps.setString(3, e.getAttrName());
					ps.setString(4, e.getErrType().toString());
					ps.setString(5, e.getClass().getSimpleName());
					ps.setString(6, e.getMessage());
					ps.setString(7, errorContent);
					ps.execute();
					ps.close();
				});
			} catch (Exception ex) {
				ex.printStackTrace();
				//TODO
				throw new RuntimeException("test");
			}	
		}
	}

	public static void write(String location, ErrType errType, Exception e, String errorContent)throws RuntimeException {
		write(location, "", errType, "", e, errorContent);
	}

	public static void write(String location, String item_id, ErrType errType, String attrName, Exception e, String errorContent)throws RuntimeException {

		if( !willLogSQL_DUPLICATE && e.getMessage().contains("duplicate key value")) {
			//not logging
		} else {
			//temp
			System.out.print(">> ErrorLogger::" + errType + "::");
			System.out.println(errorContent.split("\n")[0]);

			try {
				JDBCTool.executeUpdate((conn, st) -> {
					PreparedStatement ps = conn.prepareStatement(ErrorLogger.sql);
					ps.setString(1, location);
					ps.setString(2, item_id);
					ps.setString(3, attrName);
					ps.setString(4, errType.toString());
					ps.setString(5, e.getClass().getSimpleName());
					ps.setString(6, e.getMessage());
					ps.setString(7, errorContent);
					ps.execute();
					ps.close();
				});
			} catch (Exception ex) {
				ex.printStackTrace();
				//TODO
				throw new RuntimeException("test");
			}
		}
	}

	public static void write(SQLKeyDuplicatedException e, String errorContent) {
		if( !willLogSQL_DUPLICATE && e.getErrType().equals(ErrType.SQL_DUPLICATE)) {
			//not logging
		} else {
			
			//temp
			System.out.print(">> ErrorLogger::" + e.getErrType() + "::");
			System.out.println(errorContent.split("\n")[0]);

			try {
				JDBCTool.executeUpdate((conn, st) -> {
					PreparedStatement ps = conn.prepareStatement(ErrorLogger.sql);
					ps.setString(1, e.getLocation());
					ps.setString(2, e.getItem_id());
					ps.setString(3, e.getAttrName());
					ps.setString(4, e.getErrType().toString());
					ps.setString(5, e.getClass().getSimpleName());
					ps.setString(6, e.getMessage());
					ps.setString(7, errorContent);
					ps.execute();
					ps.close();
				});
			} catch (Exception ex) {
				ex.printStackTrace();
			}	
		}
		
	}
}
