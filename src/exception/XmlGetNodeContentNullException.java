package exception;

import main.ErrType;

import org.w3c.dom.Node;


public class XmlGetNodeContentNullException extends XmlDataException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7202521394562128449L;


	
	public XmlGetNodeContentNullException(Node node) {
		this.setErrType(ErrType.XML_NO_VALUE);
		this.setNode(node);
		this.setMessage("<"+node.getNodeName()+">" +" returns null.");
	}

}
