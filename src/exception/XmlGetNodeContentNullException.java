package exception;

import main.ErrType;

import org.w3c.dom.Node;


/**
 * 
 * Exception for missing Node Content in the XML.
 * 
 */
public class XmlGetNodeContentNullException extends XmlDataException {

	private static final long serialVersionUID = -7202521394562128449L;
	
	/**
	 * 
	 * @param node The Node where the Exception has occurred.
	 */
	public XmlGetNodeContentNullException(Node node) {
		this.setErrType(ErrType.XML_NO_VALUE);
		this.setMessage("<"+node.getNodeName()+">" +" returns null.");
	}

}
