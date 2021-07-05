package exception;

import main.ErrType;


/**
 * 
 * Exception for validation failure of XML data.
 * 
 */
public class XmlInvalidValueException extends XmlDataException {

	private static final long serialVersionUID = -6242695051810088006L;

	/**
	 * 
	 * @param message a detailed failure message.
	 */
	public XmlInvalidValueException(String message) {
		this.setErrType(ErrType.XML_INVALID_VALUE);
		this.setMessage(message);
	}

}
