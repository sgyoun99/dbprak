package XmlTools;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Node;

import exception.XmlDataException;
import main.Config;
import main.ErrorLogger;

public class Test {
	
	public static void main(String[] args) throws Exception {
		XmlTool xt = new XmlTool();
		
//		xt.analyseDirectChildNodes(Config.DRESDEN_ENCODED,"publishers");
//		xt.analyseDirectChildNodes(Config.LEIPZIG,"publishers");
		
		xt.loadXML(Config.CATEGORY);
		Map<String, Integer> map = new HashMap<>();
		xt.visitChildElementNodesDFS(xt.getDocumentNode(), (node, level) -> {
		});
		
		xt.getDirectChildElementNodes(xt.getDocumentNode().getFirstChild().getNextSibling()).forEach(currentNode -> {
			try {
				System.out.println(xt.getNodeContentForceNotNull(currentNode));
			} catch (XmlDataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(currentNode.getNodeName());
		});
	}

}
