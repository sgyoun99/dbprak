package XmlTools;

public class XmlDataException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1006124100678587748L;
	
	String message;

	public XmlDataException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public XmlDataException(String message) {
		super(message);
		this.message = message;
	}

	public void errorLogger() {
		// write in a file or in the db
	}
	
}
