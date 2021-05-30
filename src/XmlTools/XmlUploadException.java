package XmlTools;

//public class XmlUploadException extends Exception {
public class XmlUploadException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4698365035507211597L;
	public XmlUploadException() {
	}
	public XmlUploadException(String message) {
		super(message);
	}
}
