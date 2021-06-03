package exception;

import org.w3c.dom.Node;

import main.ErrType;

public class XmlNoAttributeException extends XmlDataException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5061580245017191605L;

	public XmlNoAttributeException(Node node, String attrName) {
		this.setErrType(ErrType.XML_NO_ATTRIBUTE);
		StringBuilder sb = new StringBuilder();
		sb.append("<");
		sb.append(node.getNodeName());
		sb.append("> does not have Attribute: ");
		sb.append(attrName);
		sb.append("\n");

		this.setAttrName(attrName);
		this.setMessage(sb.toString());
	}

	
}
