package exception;

import main.ErrType;

import org.w3c.dom.Node;


public class XmlGetAttributeException extends XmlDataException {


	/**
	 * 
	 */
	private static final long serialVersionUID = -3981935642546066162L;


	public XmlGetAttributeException(Node node, String attrName) {
		this.setErrType(ErrType.XML_NO_ATTRIBUTE);
		this.setAttrName(attrName);
		this.setMessage("<"+node.getNodeName()+" "+attrName+">");
	}

}
