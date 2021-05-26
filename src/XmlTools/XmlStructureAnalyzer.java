package XmlTools;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

class NodeCount {
	
	Map<Integer, Integer> levelCountMap = new HashMap<>();
	Map<String, Integer> attributeCountMap = new HashMap<>();
	
	String nodeName;
	public NodeCount(String nodeName) {
		super();
		this.nodeName = nodeName;
	}
	void increaseLevelCount(int level) {
		if( !levelCountMap.containsKey(level)) {
			levelCountMap.put(level, 1);
		} else {
			levelCountMap.put(level, levelCountMap.get(level) + 1);
		}
	}
}

public class XmlStructureAnalyzer {
	Document doc;
	HashMap<String, NodeCount> counts = new HashMap<>();

	public  XmlStructureAnalyzer(String filePath) {
		try {
			File inputFile = new File(filePath);
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public NodeList getElementAtLevel(String elementName, int level) {
		doc.getDocumentElement();
		return null;
	}
	
	public void countNodes(Node node, int level) {
		String nodeName = node.getNodeName();
		if( !counts.containsKey(nodeName)) {
			if(node.getNodeType() == Node.ELEMENT_NODE) {
				NodeCount nc = new NodeCount(nodeName);
				nc.increaseLevelCount(level);
				counts.put(nodeName, nc);
				//
				if(node.hasAttributes()) {
					NamedNodeMap attributes =  node.getAttributes();
					for (int i = 0; i < attributes.getLength(); i++) {
						String attributeName = attributes.item(i).getNodeName();
						nc.attributeCountMap.put(attributeName, 1);
					}
				}
			} 
		} else {
			counts.get(nodeName).increaseLevelCount(level);
			//
			if(node.hasAttributes()) {
				NamedNodeMap attributes =  node.getAttributes();
				for (int i = 0; i < attributes.getLength(); i++) {
					String attributeName = attributes.item(i).getNodeName();
					counts.get(nodeName).attributeCountMap.compute(attributeName, (k,v) -> (v == null) ? 1 : v + 1);
				}
			}
		}
		if(node.hasChildNodes()) {
			NodeList nList = node.getChildNodes();
			for (int i = 0; i < nList.getLength(); i++) {
				countNodes(nList.item(i), level + 1);
			}
		}
	}
	
	public void printCounts() {
		System.out.println(">> Occurence counting...");
		for (Map.Entry<String, NodeCount> entry : counts.entrySet()) {
		    String nodeName = entry.getKey();
		    NodeCount nc = entry.getValue();
		    System.out.print("Number of Levels="+nc.levelCountMap.size() + " <" + nodeName +">");
		    for(Map.Entry<Integer, Integer> lc : nc.levelCountMap.entrySet()) {
				System.out.print("  #Lv." + lc.getKey()+"="+ lc.getValue() );
		    }
		    for(Map.Entry<String, Integer> ac : nc.attributeCountMap.entrySet()) {
				System.out.print("  &Attr:" + ac.getKey()+"="+ ac.getValue() );
		    }
		    System.out.println();
		}
	}
	
	
	
	public static void main(String[] args) {
		
		XmlStructureAnalyzer xsa = new XmlStructureAnalyzer("./data/dresden.xml__to__UTF-8.xml");
		//XmlStructureAnalyzer xsa = new XmlStructureAnalyzer("./data/leipzig_transformed.xml");

		xsa.countNodes(xsa.doc.getDocumentElement(), 0);
		xsa.printCounts();
		
		/*
		NodeList nl = xsa.doc.getElementsByTagName("item");
		for (int i = 0; i < nl.getLength(); i++) {
				if(nl.item(i).hasAttributes()) {
					//System.out.println(true);
					System.out.println(nl.item(i).getNodeName());
				}
		}
		 */


	}
}
