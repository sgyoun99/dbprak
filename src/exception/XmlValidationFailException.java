package exception;

import main.ErrType;


/**
 * 
 * Exception for incomplete XML data in a node.
 * 
 */
public class XmlValidationFailException extends XmlDataException {

	private static final long serialVersionUID = -6017918224034238517L;


	/**
	 * 
	 * @param e XmlInvalidValueException
	 */
	public XmlValidationFailException(XmlInvalidValueException e) {
		this.setErrType(ErrType.XML_DATA_INCOMPLETE);
		this.setMessage(e.getMessage());
		
	}

}
