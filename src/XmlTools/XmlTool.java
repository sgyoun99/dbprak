package XmlTools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import main.Config;

import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;


public class XmlTool {

	private Document documentNode;
	private boolean printOption = false;
	
	public XmlTool() {
		
	}
	public XmlTool(String filePath) {
		loadXML(filePath);
	}
	
	public void loadXML(String filePath) {
		try {
			File inputFile = new File(filePath);
	
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			documentNode = dBuilder.parse(inputFile);
			documentNode.getDocumentElement().normalize();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Document getDocumentNode() {
		if(this.documentNode == null) {
			System.out.println("Load XML first.");
		}
		return this.documentNode;
	}
	
	public void printOptionOn() {
		this.printOption = true;
	}
	
	public void printOptionOff() {
		this.printOption = false;
	}
	
	public void printlnWithOption(String message) {
		if(this.printOption)
			System.out.println(message);
	}
	public void printlnWithOption() {
		if(this.printOption)
			System.out.println();
	}
	
	public void printWithOption(String message) {
		if(this.printOption)
			System.out.print(message);
	}
	
	public void printNodeContentsDFS(Node startNode) {
		System.out.println(this.getNodeContentDFS(startNode));
	}
	public String getPrintOpeningNode(Node node, int level) {
		StringBuilder sb = new StringBuilder();
			sb.append(" ".repeat((level)*2));
			sb.append("<");
			sb.append(node.getNodeName());
			sb.append(getAllAttributeContents(node));
			sb.append(">\n");
			sb.append(" ".repeat(2));
			sb.append(" ".repeat((level)*2));
			sb.append(this.getTextContent(node));
			sb.append("\n");
	
		return sb.toString();
	}
	public String getPrintClosingNode(Node node, int level) {
		StringBuilder sb = new StringBuilder();
			sb.append(" ".repeat((level)*2));
			sb.append("</");
			sb.append(node.getNodeName());
			sb.append(">\n");
			
		return sb.toString();
	}
	// encodes a xml file into UTF-8 and create a xml file name with "__to__UTF-8.xml" in the end.
	public void encodeFileToUTF_8(String xmlFilePath) {
		String encodedFilePath = xmlFilePath + Config.UTF8_SURFIX;
		try {
			//to prevent from endless extending to the existing encoded xml,
			//delete the existing encoded file which was created by this program.
			File oldFile= new File(encodedFilePath);
			oldFile.delete();
		} catch (Exception e) {
			// ok. do nothing
		}
		
		System.out.println(">> Encoding start: " + xmlFilePath);

		try (
			FileOutputStream fos = new FileOutputStream(encodedFilePath, true);
			BufferedReader br = new BufferedReader(new InputStreamReader(
									new FileInputStream(xmlFilePath), StandardCharsets.UTF_8));) {
			
			

			br.readLine(); // skip the first line and write <?xml version=\"1.0\" encoding=\"UTF-8\"?>
			fos.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n".getBytes()); //specify encoding
			String line;
			while ((line = br.readLine()) != null) {
				//System.out.println(line);
				fos.write(line.getBytes());
				fos.write("\n".getBytes());

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println(">> Encoding Complete: " + encodedFilePath);
        
	}
	
	// error
	// does not working with the leipzig.xml
	public boolean isLeafElementNode2(Node currentNode) {
		Node node = currentNode.getFirstChild();
		// only when the node is a leaf Element node(= has only one Text child node.)
		if(node != null 
				&& node.getNodeType() == Node.TEXT_NODE
				&& node.getTextContent().trim().length() == 0) {
			// \n between Nodes will be ignored.
			return true;
		} else if(currentNode.getFirstChild() == null) {
			return true;
		} else {
			return false;
		}
	}
	
	//test
	public boolean isLeafElementNode(Node currentNode) {
		NodeList nodeList = currentNode.getChildNodes();
		if(nodeList.getLength() == 0) {
			return true;
		}
		for (int i = 0; i < nodeList.getLength(); i++) {
			if(nodeList.item(i).getNodeType() != Node.TEXT_NODE) {
				return false;
			}
		}
		return true;
//		return false;
	}
	
	//only for the leaf node
	public boolean hasTextContent(Node node) {
		if(isLeafElementNode(node)) {
			return getTextContent(node).length() > 0 ? true : false;
		} else {
			return false;
		}
	}
	
	public String getTextContent(Node node) {
		if(isLeafElementNode(node)) {
			return node.getTextContent().trim();
		} else {
//			return "";
//			hmm null is but risky...
			return null;
		}
	}
	
	public int getLevel(Node node) {
		int level = 0;
		while(node.getParentNode() != null) {
			node = node.getParentNode();
			
			level++;
		}
		return level;
	}
	
	public List<Node> getDirectChildElementNodes(Node node) {
		List<Node> res = new ArrayList<Node>();
		NodeList nl = node.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			if(nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
				res.add(nl.item(i));
			}
		}
		return res;
	}
	public boolean hasAttribute(Node node, String attributeName) {
		Element el = (Element)node;
		if(el.hasAttribute(attributeName)) {
			return true;
		}
		return false;
	}
	
	public String getAllAttributeContents(Node node) {
		String res = "";
		if(node.hasAttributes()) {
			for (int i = 0; i < node.getAttributes().getLength(); i++) {
				String attrName = node.getAttributes().item(i).getNodeName();
				res += String.format(" %s=", attrName);
				res += String.format("\"%s\"",((Element)node).getAttribute(attrName));
			}
		}
		return res;
	}
	
	//test if attribute name exists and return text content
	public String getAttributeValue(Node node, String attributeName) throws XmlDataException{
		if(hasAttribute(node, attributeName)) {
			Element el = (Element)node;
			return el.getAttribute(attributeName);
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append("<");
			sb.append(node.getNodeName());
			sb.append("> does not have Attribute: ");
			sb.append(attributeName);
			sb.append("\n");
//			sb.append(this.getNodeContentDFS(node));
			throw new XmlDataException(sb.toString());
		}
	}
	
	public List<Node> getAllElementNodesDFS() {
		List<Node> res = new ArrayList<Node>();
		this.visitChildElementNodesDFS(this.documentNode, (node, level) -> res.add(node));
		return res;
	}
	
	//will returns null, when not exists
	public List<Node> getNodesbyNameDFS(Node startNode, String nodeName) {
		List<Node> res = new ArrayList<Node>();
		this.visitChildElementNodesDFS(startNode, (node, l) -> {
			if(node.getNodeName().equals(nodeName)) {
				res.add(node);
			}
		});
		return res;
	}
	
	//will returns null, when not exists
	public Node getNodebyNameDFS(Node startNode, String nodeName) {
		return getNodesbyNameDFS(startNode, nodeName).get(0) ;
	}
	
	public void visitAllElementNodesDFS(XmlToolWorkable worker) {
		this.visitChildElementNodesDFS(this.documentNode, worker);
	}
	
	//DFS
	public void visitChildElementNodesDFS(Node startNode, XmlToolWorkable worker) {
		Node currentNode = startNode;
		if(this.isLeafElementNode(currentNode)) {
			int level = this.getLevel(currentNode);
			if(this.printOption) {
				System.out.println(this.getPrintOpeningNode(currentNode, level));
				System.out.println(this.getPrintClosingNode(currentNode, level));
			}
			if(worker != null) {
				worker.work(currentNode, level);
			}
		} else {
			Stack<Node> dfsStack = new Stack<Node>();
			dfsStack.push(currentNode);
			int level = 0;
			dfs:while(currentNode != null) {
				if(currentNode.getNodeType() == Node.ELEMENT_NODE) {
					if(worker != null) {
						worker.work(currentNode, level);
					}
					if(this.printOption) {
						System.out.println(this.getPrintOpeningNode(currentNode, level));
					}
				}
				
				if(currentNode.hasChildNodes()) {
					dfsStack.push(currentNode);
					level++;
					currentNode = currentNode.getFirstChild();
				} else if(currentNode.getNextSibling() != null) {
					currentNode = currentNode.getNextSibling();
				} else {
					while(currentNode.getNextSibling() == null) {
						try{
							currentNode = dfsStack.pop();
							level--;
							if(this.printOption) {
								System.out.println(this.getPrintClosingNode(currentNode, level));
							}
							if(currentNode.isSameNode(startNode)) { break dfs; }
								
						} catch (EmptyStackException e) {
							e.printStackTrace();
							break;
						}
					}
					currentNode = currentNode.getNextSibling();
					
				}
			} 
		}
	}

	public String getNodeContentDFS(Node startNode) {
		StringBuilder sb = new StringBuilder();
		Node currentNode = startNode;
		int level = 0;
	
		if(this.isLeafElementNode(currentNode)) {
//			level = this.getLevel(currentNode);
			sb.append(this.getPrintOpeningNode(currentNode, level));
			sb.append(this.getPrintClosingNode(currentNode, level));
		} else {
			Stack<Node> dfsStack = new Stack<Node>();
			dfsStack.push(currentNode);
			dfs:while(currentNode != null) {
				if(currentNode.getNodeType() == Node.ELEMENT_NODE) {
					if(this.isLeafElementNode(currentNode)) {
						sb.append(this.getPrintOpeningNode(currentNode, level));
						sb.append(this.getPrintClosingNode(currentNode, level));
					} else {
						sb.append(this.getPrintOpeningNode(currentNode, level));
					}
				}
				
				if(currentNode.hasChildNodes()) {
					dfsStack.push(currentNode);
					level++;
					currentNode = currentNode.getFirstChild();
				} else if(currentNode.getNextSibling() != null) {
					currentNode = currentNode.getNextSibling();
				} else {
					while(currentNode.getNextSibling() == null) {
						try{
							currentNode = dfsStack.pop();
							level--;
							sb.append(this.getPrintClosingNode(currentNode, level));
							if(currentNode.isSameNode(startNode)) { break dfs; }
								
						} catch (EmptyStackException e) {
							e.printStackTrace();
							break;
						}
					}
					currentNode = currentNode.getNextSibling();
					
				}
			} 
		}
		return sb.toString();
	}
	public List<Node> filterElementNodesDFS(Node startNode, IntPredicate levelPredicate, Predicate<Node> predicate) {
		List<Node> res = new ArrayList<Node>();
		this.visitChildElementNodesDFS(startNode, (node, level) -> {
			if(levelPredicate.test(level) && predicate.test(node)) {
				res.add(node);
			}
		});
		return res;
	}
	/*
	public List<Node> filterElementNodesDFS(Node startNode, IntPredicate levelPredicate) {
		return this.filterElementNodesDFS(startNode, levelPredicate, node -> true);
	}
	 */
	public List<Node> filterElementNodesDFS(Node startNode, Predicate<Node> predicate) {
		return this.filterElementNodesDFS(startNode, level -> true, predicate);
	}

	/*
	public void visitAllElementNodesBFS(XmlToolWorkable worker) {
		List<Node> allNodes = new ArrayList<Node>();
		Queue<Node> q = new LinkedList<>();
		NodeList nl = this.getDocumentNode().getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			if(nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
				q.add(nl.item(i));
				allNodes.add(nl.item(i));
			}
		}
		
		while(!q.isEmpty()) {
			Node node = q.poll();
			if(worker != null) {
				worker.work(node, 0);
			}
			printWithOption("<");
			printWithOption(node.getNodeName());
			printWithOption("> ");
			if(this.hasTextContent(node)) {
				printlnWithOption(node.getTextContent());
			}
			if(node.hasChildNodes()) {
				nl = node.getChildNodes();
				for (int i = 0; i < nl.getLength(); i++) {
					if(nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
						q.add(nl.item(i));
					}
				}
			} else {
				continue;
			}
		}
	}
	 */
	
	//find all attribute within the Element Node with the given name
	public void analyseAttributesInNode(String nodeName) {
		Map<Integer, Map<String, Integer>> level_AttrName_Occurence = new HashMap<Integer, Map<String,Integer>>();

		this.visitAllElementNodesDFS((node, level) -> {
				if (node.getNodeName().equals(nodeName)) {
					Element el = (Element) node;
					for (int i = 0; i < el.getAttributes().getLength(); i++) {
						String attrName = el.getAttributes().item(i).getNodeName();
						level_AttrName_Occurence.compute(level, (lv, attrNameOccurMap) -> {
							if (attrNameOccurMap == null) {
								HashMap<String, Integer> attNameOccMap = new HashMap<String, Integer>();
								attNameOccMap.put(attrName, 1);
								return attNameOccMap;
							} else {
								attrNameOccurMap.compute( attrName, (att, count) -> count == null ? 1 : count + 1);
								return attrNameOccurMap;
							}
						});
					}
				}
			}
		);
		
		
		System.out.println("<" + nodeName + ">'s attributes:");
		for(Map.Entry<Integer,Map<String,Integer>> att : level_AttrName_Occurence.entrySet()) {
			System.out.println("Level "+att.getKey());
			att.getValue().forEach((attrName, count) -> 
				{ System.out.println(String.format(" -%10s :", attrName) + String.format("%6d ", count) +"times"); }
			);
		}	
	}
	
	
	
	public void testDresdenEncodeToUTF8() {
		XmlTool xt = new XmlTool();
		xt.loadXML(Config.DRESDEN_ORIGINAL);

		//before encoding

		Document doc = xt.getDocumentNode();
		Node node = doc.getElementsByTagName("shop").item(0);
		Element el = (Element)node;

		System.out.println("=== Before encoding ===");
		System.out.println(el.getAttribute("name"));
		System.out.println(el.getAttribute("street"));
		System.out.println(el.getAttribute("zip"));

		//after encoding
		xt.encodeFileToUTF_8(Config.DRESDEN_ORIGINAL);

		xt.loadXML(Config.DRESDEN_ENCODED);
		doc = xt.getDocumentNode();
		node = doc.getElementsByTagName("shop").item(0);
		el = (Element)node;

		System.out.println("=== After encoding ===");
		System.out.println(el.getAttribute("name"));
		System.out.println(el.getAttribute("street"));
		System.out.println(el.getAttribute("zip"));	
	}
	
	/* not yet
	public Map<String,String> getNodeContentMap(Node node){
		Map<String,String> res = new HashMap<String, String>();
		this.visitChildElementNodesDFS(node, null);
		return res;
	}
	 */
	
	public void analyseDirectChildNodes(String xmlPath, String parentNodeName) {
		XmlTool xt = new XmlTool();
		xt.loadXML(xmlPath);
		Map<String,Integer> nodeCount = new HashMap<String, Integer>();
		Map<String,Integer> textContentCount = new HashMap<String, Integer>();
		xt.filterElementNodesDFS(xt.getDocumentNode(), (Node n) -> {
			return n.getNodeName().equals(parentNodeName);
		}).forEach( node -> {
			xt.getDirectChildElementNodes(node).forEach(nd -> {

				nodeCount.compute(nd.getNodeName(), (k,v) -> v==null? 1 : v + 1);

				if(xt.hasTextContent(nd)) {
					textContentCount.compute(nd.getNodeName(), (k,v) -> v==null? 1 : v + 1);
				}
				
			});
		});
		System.out.println("= = = analyse <"+parentNodeName+"> = = =");
		System.out.println(">> Node count");
		nodeCount.forEach((k,v)->{
			System.out.println("<"+k + ">: " +v+" times");
			xt.analyseAttributesInNode(k);
			System.out.println();
		});
		System.out.println();
		System.out.println(">> Node Text contents count");
		textContentCount.forEach((k,v)->System.out.println("<"+k + ">:" +v+" times"));
		
		
	}
	
	public static void main(String[] args) {
		
		XmlTool xt = new XmlTool();
//		xt.loadXML(Config.LEIPZIG);
//		xt.analyseAttributesInNode("dvdspec");
		
		xt.encodeFileToUTF_8(Config.DRESDEN_ORIGINAL);

//		xt.loadXML(Config.DRESDEN_ENCODED);
//		xt.analyseAttributesInNode("format");
		
		
		
		xt.analyseDirectChildNodes(Config.DRESDEN_ENCODED, "artists"); // <artist>...</artist>
//		xt.analyseDirectChildNodes(Config.DRESDEN_ENCODED, "authors");

//		xt.analyseDirectChildNodes(Config.LEIPZIG, "artists"); // <artist name="..."/>
//		xt.analyseDirectChildNodes(Config.LEIPZIG, "authors");
		



		
	}
}
