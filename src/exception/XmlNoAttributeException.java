package exception;

import org.w3c.dom.Node;

import main.ErrType;

/**
 * 
 * Exception for missing attribute in a node
 * 
 */
public class XmlNoAttributeException extends XmlDataException {

	private static final long serialVersionUID = 5061580245017191605L;

	/**
	 * 
	 * @param node The Node where the Exception has occurred.
	 * @param attrName attribute name which is missing in the node.
	 */
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
