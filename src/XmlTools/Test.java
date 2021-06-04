package XmlTools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
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
		
//		System.out.println(xt.getDocumentNode().getFirstChild().getNextSibling().getNodeName());

		Node categoriesNode = xt.getDocumentNode().getFirstChild().getNextSibling();
		List<Node> categoryNodesList = xt.getDirectChildElementNodes(categoriesNode);
		Node featuresNode = categoryNodesList.get(0);
//		List<Node> categoryNodesList = xt.getDirectChildElementNodes(categoriesNode);
		System.out.println(categoryNodesList.size()); // 12
		for (int i = 0; i < categoryNodesList.size(); i++) {
			Node node = categoryNodesList.get(i);
			Element element = ((Element)node);
			System.out.println(xt.getFirstTextNodeValue(node));
		}
		
		categoryNodesList.forEach(categoryNode -> {
//			System.out.println(categoryNode.getTextContent());
		});
		
		xt.getDirectChildElementNodes(xt.getDocumentNode().getFirstChild().getNextSibling()).forEach(currentNode -> {
//			System.out.println(xt.getFirstTextNodeValue(currentNode));
		});
	}

}
