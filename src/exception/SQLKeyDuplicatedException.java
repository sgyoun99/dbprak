package exception;

import java.sql.SQLException;

public class SQLKeyDuplicatedException extends SQLException {


	/**
	 * 
	 */
	private static final long serialVersionUID = 5722794895129876540L;
	
	
	public SQLKeyDuplicatedException(String message) {
		super(message);
	}

}
