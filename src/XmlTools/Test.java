package XmlTools;

import org.w3c.dom.Node;

import exception.XmlDataException;
import main.Config;
import main.ErrorLogger;

public class Test {
	
	public static void main(String[] args) {
		XmlTool xt = new XmlTool();
		xt.loadXML(Config.DRESDEN_ENCODED);
		
		try {
			Node currentNode = xt.getNodebyNameDFS(xt.getDocumentNode(), "title");
			System.out.println(currentNode.getTextContent());
			String str = xt.getNodeContentForceNotNull(currentNode);
			System.out.println(str);
		} catch (XmlDataException e) {
			ErrorLogger.write(e, null);
		}
		
		
	}

}
