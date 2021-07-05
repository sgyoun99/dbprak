package exception;

import main.ErrType;


/**
 * 
 * Exception for missing node
 * 
 */
public class XmlNullNodeException extends XmlDataException {

	private static final long serialVersionUID = 6330471575592266975L;

	//Error Type will be automatically set.
	public XmlNullNodeException() {
		this.setErrType(ErrType.XML_NO_NODE);
	}

}
