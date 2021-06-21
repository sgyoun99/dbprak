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
	
	static int count = 0;
	
	public static void main(String[] args) throws Exception {

		XmlTool xt = new XmlTool();
		xt.loadXML(Config.CATEGORY_ENCODED);
		
		Map<String, Integer> map = new HashMap<>();
		Node categoriesNode = xt.getDocumentNode().getFirstChild().getNextSibling();
		List<Node> mainCategoryNodesList = xt.getDirectChildElementNodes(categoriesNode); //12

		mainCategoryNodesList.forEach(n->{
			xt.visitChildElementNodesDFS(n, (node, level) -> {
				if(node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("category")) {
					System.out.print(level+":");
					System.out.print(++Test.count+":");
					String catName = xt.getFirstTextNodeValue(node);
					for (int i = 0; i < level; i++) {
						System.out.print(".");
					}
					System.out.println(catName);
				}
			});
		});
		
	}

}
