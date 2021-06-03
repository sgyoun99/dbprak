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
		
		xt.analyseDirectChildNodes(Config.DRESDEN_ENCODED,"publishers");
		xt.analyseDirectChildNodes(Config.LEIPZIG,"publishers");
		
		xt.loadXML(Config.DRESDEN_ENCODED);
		Map<String, Integer> map = new HashMap<>();
		xt.visitChildElementNodesDFS(xt.getDocumentNode(), (node, level) -> {
			
		});
	}

}
