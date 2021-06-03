package exception;

import main.ErrType;



public class XmlValidationFailException extends XmlDataException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6017918224034238517L;


	public XmlValidationFailException(XmlInvalidValueException e) {
		this.setErrType(ErrType.XML_DATA_INCOMPLETE);
		this.setMessage(e.getMessage());
		
	}

}
