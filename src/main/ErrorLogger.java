package main;

import java.sql.PreparedStatement;

import JDBCTools.JDBCTool;
import exception.SQLKeyDuplicatedException;
import exception.XmlDataException;

import entity.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;

/**
 * class for Error log in the DB.
 *
 */
public class ErrorLogger {

	//key duplication exception will be ignored as default.
	public static boolean willLogSQL_DUPLICATE = false;
	//insert SQL statement.
	private final static String sql = "INSERT INTO ERRORS ("
			+ "location, item_id, attribute, errtype, exception, error_message, contents) values "
			+ "(?,?,?,?::ErrType,?,?,?);" ;
	
	/**
	 * To write a log in the DB. Used when detailed information is included in the Exception.
	 * @param e XmlDataException
	 * @param errorContent error content.
	 * @throws RuntimeException
	 */
	public static void write(XmlDataException e, String errorContent) throws RuntimeException{

		if( !willLogSQL_DUPLICATE && e.getMessage().contains("duplicate key value")) {
			//not logging
		} else {
			
			//System standard out print
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
				throw new RuntimeException(ex);
			}	
		}
	}

	/**
	 * To write a log in the DB. Used when some information is not included in the Exception.
	 * @param location location where the log occurred.
	 * @param errType error type.
	 * @param e Exception.
	 * @param errorContent error content.
	 * @throws RuntimeException
	 */
	public static void write(String location, ErrType errType, Exception e, String errorContent)throws RuntimeException {
		write(location, "", errType, "", e, errorContent);
	}

	/**
	 * To write a log in the DB with full information.
	 * @param location location where the log occurred.
	 * @param item_id asin
	 * @param errType error type.
	 * @param attrName attribute name.
	 * @param e Exception.
	 * @param errorContent error content.
	 * @throws RuntimeException
	 */	
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
	
	 /**
	  * To write a log in the DB. Used when the Exception is key duplication exception.
	  * @param e SQLKeyDuplicatedException
	  * @param errorContent
	  */
	public static void write(SQLKeyDuplicatedException e, String errorContent) {
		if( !willLogSQL_DUPLICATE && e.getErrType().equals(ErrType.SQL_DUPLICATE)) {
			//not logging
		} else {
			
			//System standard out print
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

	public static void checkDuplicate(Book book){
		try{
            Connection con = JDBCTool.getConnection();
            con.setAutoCommit(false);
			PreparedStatement ps = con.prepareStatement("Select pages, publication_date, isbn FROM book where item_id = ?");
			ps.setString(1, book.getItem_id());
            ResultSet rs = ps.executeQuery();
         
            while(rs.next()){
                if(! (rs.getShort("pages") == book.getPages())){
					System.out.println("wrong page count on item " + book.getItem_id());
				}
				if (rs.getDate("publication_date").compareTo(book.getPublication_date()) != 0){
					System.out.println("wrong date on item " + book.getItem_id());
				}
				if (!(rs.getString("isbn").equals(book.getIsbn()))){
					System.out.println("wrong isbn on item " + book.getItem_id());
				}
            }
            con.close();
        
        }catch(SQLException e){
            System.out.println("Exception in ErrorLogger");
        }
	}



}



