package XmlTools;

import org.w3c.dom.Node;

public interface XmlToolWorkable {

//	public void work(Node node, XmlTool xmlTool);
	public void work(Node node, int level, XmlTool xmlTool);
}
