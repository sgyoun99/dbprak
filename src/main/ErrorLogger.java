package main;

import java.sql.PreparedStatement;

import JDBCTools.JDBCTool;
import exception.SQLKeyDuplicatedException;
import exception.XmlDataException;

import entity.*;
import entity.Error;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

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
			
			Error error = new Error();
			error.setLocation(e.getLocation());
			error.setItem_id(e.getItem_id());
			error.setAttribute(e.getAttrName());
			error.setErrtype(e.getErrType());
			error.setException(e.getClass().getSimpleName());
			error.setError_message(e.getMessage());
			error.setContents(errorContent.split("\n")[0]);
			addError(error);

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

			Error error = new Error();
			error.setLocation(location);
			error.setItem_id(item_id);
			error.setAttribute(attrName);
			error.setErrtype(errType);
			error.setException(e.getClass().getSimpleName());
			error.setError_message(e.getMessage());
			error.setContents(errorContent.split("\n")[0]);
			addError(error);

		}
	}
	

	private static void addError(Error error) {
		Session session = App.sessionFactory.openSession();
		Transaction tx = null;
		
		try {
			tx = session.beginTransaction();
			session.save(error); 
			tx.commit();
		} catch (HibernateException e) {
			if (tx!=null) {
				tx.rollback();
			}
			System.out.println("HibernateException for ErrorLogger" + error.getException()); 
		} finally {
			session.close(); 
		}
	}

}



