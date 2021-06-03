package exception;

import main.ErrType;



public class XmlNullNodeException extends XmlDataException {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6330471575592266975L;

	public XmlNullNodeException() {
		this.setErrType(ErrType.XML_NO_NODE);
	}

}
