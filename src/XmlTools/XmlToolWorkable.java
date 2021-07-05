package XmlTools;

import org.w3c.dom.Node;

/**
 * Interface for XML Handler
 */
public interface XmlToolWorkable {

	/**
	 * handle XML with the given Node and level
	 * @param node Node
	 * @param level level of Node
	 */
	public void handle(Node node, int level);
}
