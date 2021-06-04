package XmlTools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import entity.Shop;
import exception.XmlDataException;
import main.Config;
import main.ErrorLogger;

public class Test {
	
	public static void main(String[] args) throws Exception {
		XmlTool xt = new XmlTool();
		
//		xt.analyseDirectChildNodes(Config.DRESDEN_ENCODED,"publishers");
//		xt.analyseDirectChildNodes(Config.LEIPZIG,"publishers");
		
		xt.loadXML(Config.CATEGORY_ENCODED);
		Map<String, Integer> map = new HashMap<>();
		xt.visitChildElementNodesDFS(xt.getDocumentNode(), (node, level) -> {
		});
		
//		System.out.println(xt.getDocumentNode().getFirstChild().getNextSibling().getNodeName());

		Node categoriesNode = xt.getDocumentNode().getFirstChild().getNextSibling();
		List<Node> mainCategoryNodesList = xt.getDirectChildElementNodes(categoriesNode); //12

		mainCategoryNodesList.forEach(n->{
			System.out.println(xt.getFirstTextNodeValue(n));
		});

		
//		System.out.println(xt.getNodeContentDFS(xt.getDocumentNode()));
		
	}

}
