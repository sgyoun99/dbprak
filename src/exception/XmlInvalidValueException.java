package exception;

import main.ErrType;



public class XmlInvalidValueException extends XmlDataException {



	/**
	 * 
	 */
	private static final long serialVersionUID = -6242695051810088006L;

	public XmlInvalidValueException(String message) {
		this.setErrType(ErrType.XML_INVALID_VALUE);
		this.setMessage(message);
	}

}
