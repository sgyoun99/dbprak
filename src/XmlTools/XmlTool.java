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
	
	public boolean isLeafElementNode(Node node) {
		// only when the node is a leaf Element node(= has only one Text child node.)
		if(node.getFirstChild() != null && node.getFirstChild().getNodeType() == Node.TEXT_NODE) {
			// \n between Nodes will be ignored.
			return "\n".compareTo(node.getFirstChild().getTextContent()) == 0 ? false : true;
		} else {
			return false;
		}
	}
	
	public boolean hasTextContent(Node node) {
		if(isLeafElementNode(node)) {
			return getTextContent(node).length() > 0 ? true : false;
		} else {
			return false;
		}
	}
	
	public String getTextContent(Node node) {
		if(isLeafElementNode(node)) {
			return node.getTextContent();
		} else {
			return "";
		}
	}
	public boolean hasAttribute(Node node, String attributeName) {
		Element el = (Element)node;
		if(el.hasAttribute(attributeName)) {
			return true;
		}
		return false;
	}
	
	public String getAttributeTextContent(Node node, String attributeName) throws XmlDataException{
		if(hasAttribute(node, attributeName)) {
			Element el = (Element)node;
			return el.getAttribute(attributeName);
		} else {
			throw new XmlDataException("<"+node.getNodeName()+"> does not have Attribute: " + attributeName);
		}
	}
	

	public List<Node> getAllElementNodesDFS() {
		List<Node> res = new ArrayList<Node>();
		this.visitChildElementNodesDFS(this.documentNode, (node, level, xmlTool) -> res.add(node));
		return res;
	}
	
	public void visitAllElementNodesDFS(XmlToolWorkable worker) {
		this.visitChildElementNodesDFS(this.documentNode, worker);
	}
	
	public void visitChildElementNodesDFS(Node startNode, XmlToolWorkable worker) {
		this.visitChildElementNodesDFS(startNode, 1, worker);
	}
	
	public List<Node> filterElementNodesDFS(Node startNode, IntPredicate levelPredicate) {
		return this.filterElementNodesDFS(startNode, levelPredicate, _x -> true);
	}
	
	public List<Node> filterElementNodesDFS(Node startNode, Predicate<Node> predicate) {
		return this.filterElementNodesDFS(startNode, _x -> true, predicate);
	}
	public List<Node> filterElementNodesDFS(Node startNode, IntPredicate levelPredicate, Predicate<Node> predicate) {
		List<Node> res = new ArrayList<Node>();
		this.visitChildElementNodesDFS(startNode, (node, level, xt) -> {
			if(levelPredicate.test(level) && predicate.test(node)) {
				res.add(node);
			}
		});
		return res;
	}
	
	
	private void visitChildElementNodesDFS(Node startNode, int relativeLevel, XmlToolWorkable worker) {
		int visitedNodes = 0;
		Node currentNode = startNode;
		Stack<Node> dfsStack = new Stack<Node>();
		dfsStack.push(startNode);
		int startLevel = relativeLevel;
		int level = relativeLevel;
		dfs:while(currentNode != null) {
			if(currentNode.getNodeType() == Node.ELEMENT_NODE) {
				visitedNodes++;
				if(worker != null) {
					worker.work(currentNode, level, this);
				}
				this.printWithOption(String.format("%8d:", visitedNodes));
				this.printWithOption(" ".repeat(level*2));
				this.printWithOption("<");
				this.printWithOption(currentNode.getNodeName());
				this.printWithOption(">");
				this.printWithOption(this.getTextContent(currentNode));
				this.printlnWithOption();;
			}
			if(currentNode.hasChildNodes()) {
				dfsStack.push(currentNode);
				level++;
				currentNode = currentNode.getFirstChild();
			} else {
				if(currentNode.getNextSibling() != null) {
					currentNode = currentNode.getNextSibling();
				} else {
					while(currentNode.getNextSibling() == null) {
						try{
							currentNode = dfsStack.pop();
							level--;
							this.printWithOption(" ".repeat(8+1));
							this.printWithOption(" ".repeat(level*2));
							this.printWithOption("</");
							this.printWithOption(currentNode.getNodeName());
							this.printWithOption(">");
							this.printlnWithOption();
							if(level == startLevel) {
								break dfs;
							}
							//if(currentNode == startNode) { break dfs; }
								
						} catch (EmptyStackException e) {
							System.out.println(e);
							break;
						}

						

					}

					currentNode = currentNode.getNextSibling();
					
				}
			}
		} 
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
				worker.work(node, 0, this);
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
	
	//find all attribute names within the Element Node
	public void analyseAttributesInItem(String nodeName) {
		Map<Integer, Map<String, Integer>> level_AttrName_Occurence = new HashMap<Integer, Map<String,Integer>>();

		this.visitAllElementNodesDFS((node, level, xmlTool) -> {
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
		
		
		System.out.println("<" + nodeName + ">");
		for(Map.Entry<Integer,Map<String,Integer>> att : level_AttrName_Occurence.entrySet()) {
			System.out.println("Level "+att.getKey());
			att.getValue().forEach((attrName, count) -> 
				{ System.out.println(String.format(" -%10s :", attrName) + String.format("%6d ", count) +"times"); }
			);
		}	
	}
	
	/*
	public void analyseElementNode(String nodeName) {
		this.visitAllElementNodesDFS((node, level, xmlToo) -> {
			
		});
	}
	 */
	
	
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
	
	
	public static void main(String[] args) {
		
		XmlTool xt = new XmlTool();
		xt.loadXML(Config.LEIPZIG);
//		xt.analyseAttributesInItem("item");

		xt.loadXML(Config.DRESDEN_ENCODED);
//		xt.analyseAttributesInItem("item");
		
		/*
		Map<String,Integer> pgroup = new HashMap<String, Integer>();
		xt.visitAllElementNodesDFS((n,l,x)->{
			if(xt.hasAttribute(n, "pgroup")) {
				String pgr;
				try {
					pgr = xt.getAttributeTextContent(n, "pgroup");
					if(pgr.equals("Buch")) {
						System.out.println("Buch" + xt.getAttributeTextContent(n, "asin"));
					}
					if(pgr.equals("Musical")) {
						System.out.println("Musical" + xt.getAttributeTextContent(n, "asin"));
					}
					pgroup.compute(pgr, (k,v)->
						{
							if(v == null) {
								return 1;
							} else {
								return v+1;
							}
						}
					);
				} catch (XmlDataException e) {
					e.printStackTrace();
				}
			}
		});
		
		pgroup.forEach((k,v) -> System.out.println(k + ": "+ v));
		 */
		
//		xt.filterElementNodesDFS(xt.getDocumentNode(), l -> l == 3, node -> !xt.hasAttribute(node, "asin")).forEach(n -> System.out.println(xt.getTextContent(n)));
//		xt.filterElementNodesDFS(xt.getDocumentNode(), l -> l == 3, node -> !xt.hasAttribute(node, "asin")).forEach(n -> System.out.println(n.getTextContent()));
		
		Node node = xt.filterElementNodesDFS(xt.getDocumentNode(), l -> l == 3, n -> {
			try {
				return xt.getAttributeTextContent(n, "asin").equals("3405156211");
			} catch (XmlDataException e) {
			}
			return false;
		}).get(0);
		
		xt.printOptionOn();
		xt.visitChildElementNodesDFS(node, null);

	}
	/*
	 */
}
