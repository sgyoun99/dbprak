package exception;

import main.ErrType;

import org.w3c.dom.Node;


/**
 * 
 * Exception for missing attribute content in the XML.
 */
public class XmlGetAttributeException extends XmlDataException {


	private static final long serialVersionUID = -3981935642546066162L;


	/**
	 * 
	 * @param node The Node where the Exception has occurred.
	 * @param attrName attribute name which is missing.
	 */
	public XmlGetAttributeException(Node node, String attrName) {
		this.setErrType(ErrType.XML_NO_ATTRIBUTE);
		this.setAttrName(attrName);
		this.setMessage("<"+node.getNodeName()+" "+attrName+">");
	}

}
