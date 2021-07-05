package XmlTools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import exception.XmlDataException;
import exception.XmlGetNodeContentNullException;
import exception.XmlNoAttributeException;
import exception.XmlNullNodeException;
import main.Config;

import org.w3c.dom.Node;
import org.w3c.dom.Element;

/**
 * Class for XML parsing 
 *
 */
public class XmlTool {

	//Document node of XML file.
	private Document documentNode;
	//Option for printing xml contents
	public boolean printOption = false;
	
	public XmlTool() {
	}

	/**
	 * To set XML file path.
	 * @param filePath file path.
	 */
	public XmlTool(String filePath) {
		loadXML(filePath);
	}
	
	/**
	 * To load XML file with the path and set document node of the XML.
	 * @param filePath file path.
	 */
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

	/**
	 * To get document node of the XML.
	 * @return Document Node.
	 */
	public Document getDocumentNode() {
		if(this.documentNode == null) {
			System.err.println("Load XML first!!");
		}
		return this.documentNode;
	}
	
	/**
	 * To print contents of the given node with DFS.
	 * @param startNode start node.
	 */
	public void printNodeContentsDFS(Node startNode) {
		System.out.println(this.getNodeContentDFS(startNode));
	}
	
	/**
	 * To print contents of a node with the opening parenthesis
	 * including all attributes and their contents.
	 * @param node
	 * @param level
	 * @return hierarchy contents of a node
	 */
	public String getPrintOpeningNode(Node node, int level) {//
		StringBuilder sb = new StringBuilder();
			for(int i=0; i<level*2; i++){sb.append(" ");}
			sb.append("<");
			sb.append(node.getNodeName());
			sb.append(getAllAttributeContents(node));
			sb.append(">\n");

			String textValue = this.getFirstTextNodeValue(node).trim();
			if(textValue == null || textValue.length() == 0) {
			} else {
				for(int i=0; i<2; i++){sb.append(" ");}
				for(int i=0; i<level*2; i++){sb.append(" ");}
				sb.append("  ");
				sb.append(this.getFirstTextNodeValue(node).trim());
				sb.append("\n");
			}
	
		return sb.toString();
	}
	
	/**
	 * To print closing parenthesis of a node
	 * @param node Node
	 * @param level level of the Node
	 * @return closing parenthesis
	 */
	public String getPrintClosingNode(Node node, int level) {
		StringBuilder sb = new StringBuilder();
			for(int i=0; i<level*2; i++){sb.append(" ");}
			//sb.append(" ".repeat((level)*2));
			sb.append("</");
			sb.append(node.getNodeName());
			sb.append(">\n");
			
		return sb.toString();
	}

	/**
	 * To encode a xml file into UTF-8 and create a xml file name with "__to__UTF-8.xml" in the end.
	 */
	public void encodeDresdenXMLToUTF8() {
		String xmlFilePath = Config.DRESDEN_ORIGINAL;
		String encodedFilePath = Config.DRESDEN_ENCODED;
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
			fos.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n".getBytes("UTF-8")); //specify encoding
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
	
	
	/**
	 * To encode a xml file into UTF-8 and create a xml file name with "__to__UTF-8.xml" in the end.
	 */
	public void encodeCategoriesXMLToUTF8() {
		String xmlFilePath = Config.CATEGORY_ORIGINAL;
		String encodedFilePath = Config.CATEGORY_ENCODED;
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
			InputStream is = new FileInputStream(new File(xmlFilePath));
			OutputStream os = new FileOutputStream(encodedFilePath, true);
		    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(xmlFilePath), "ISO-8859-1"));
			){

			br.readLine(); // skip the first line and write <?xml version=\"1.0\" encoding=\"UTF-8\"?>
			os.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n".getBytes("UTF-8")); //specify encoding
		    String str;
			while((str = br.readLine()) != null) {
				String strInUTF8 = new String(str.getBytes(), "UTF-8");
//				System.out.println(strInUTF8);
				os.write(strInUTF8.getBytes("UTF-8"));
				os.write("\n".getBytes());
			}
			os.flush();
			os.close();

			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println(">> Encoding Complete: " + encodedFilePath);
        
	}	
	
	/**
	 * To determine if the node is leaf node.
	 * @param currentNode Node
	 * @return true if it is leaf node.
	 */
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
	}
	
	/**
	 * To get Text content of a leaf node
	 * @param node Node
	 * @return Text content of a leaf node
	 */
	public String getTextContentOfLeafNode(Node node) {
		if(isLeafElementNode(node)) {
			return node.getTextContent().trim();
		} else {
			return null;
		}
	}
	
	/**
	 * To calculate level of a node from the document node.
	 * @param node Node
	 * @return level of a node
	 */
	public int getLevel(Node node) {
		int level = 0;
		while(node.getParentNode() != null) {
			node = node.getParentNode();
			
			level++;
		}
		return level;
	}
	
	/**
	 * To get direct child element nodes of the node
	 * @param node Node
	 * @return direct child element nodes
	 */
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

	/**
	 * To determine if the node has attribute with the name.
	 * @param node Node
	 * @param attributeName attribute name
	 * @return true if the node has attribute with the name.
	 */
	public boolean hasAttribute(Node node, String attributeName) {
		Element el = (Element)node;
		if(el.hasAttribute(attributeName)) {
			return true;
		}
		return false;
	}
	
	/**
	 * To get all attribute name and its contents.
	 * @param node Node
	 * @return all attribute name and its contents
	 */
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
	
	/**
	 * To get attribute content from the node with the attribute name
	 * @param node Node
	 * @param attributeName attribute name
	 * @return attribute value
	 * @throws XmlNoAttributeException when attribute does not exists.
	 */
	public String getAttributeValue(Node node, String attributeName) throws XmlNoAttributeException{
		if(hasAttribute(node, attributeName)) {
			Element el = (Element)node;
			return el.getAttribute(attributeName);
		} else {
			throw new XmlNoAttributeException(node, attributeName);
		}
	}
	
	
	//will return null, when not exists
	/**
	 * To get nodes with the name by DFS
	 * @param startNode start Node
	 * @param nodeName node name
	 * @return nodes with the name
	 */
	public List<Node> getNodesByNameDFS(Node startNode, String nodeName) {
		List<Node> res = new ArrayList<Node>();
		this.visitChildElementNodesDFS(startNode, (node, l) -> {
			if(node.getNodeName().equals(nodeName)) {
				res.add(node);
			}
		});
		return res;
	}
	
	/**
	 * To get node with the name by DFS.
	 * will returns null, when not exists
	 * @param startNode start Node
	 * @param nodeName node name
	 * @return node with the name
	 */
	public Node getNodeByNameDFS(Node startNode, String nodeName) {
		return getNodesByNameDFS(startNode, nodeName).get(0) ;
	}
	
	/**
	 * Visit all Element Nodes by DFS starting from Document Node
	 * @param handler XML handler
	 */
	public void visitAllElementNodesDFS(XmlToolWorkable handler) {
		this.visitChildElementNodesDFS(this.documentNode, handler);
	}
	
	/**
	 * Visit Element Nodes by DFS starting from given Node
	 * @param startNode start Node
	 * @param handler XML handler
	 */
	public void visitChildElementNodesDFS(Node startNode, XmlToolWorkable handler) {
		Node currentNode = startNode;
		if(this.isLeafElementNode(currentNode)) {
			int level = this.getLevel(currentNode);
			if(this.printOption) {
				System.out.println(this.getPrintOpeningNode(currentNode, level));
				System.out.println(this.getPrintClosingNode(currentNode, level));
			}
			if(handler != null) {
				handler.handle(currentNode, level);
			}
		} else {
			Stack<Node> dfsStack = new Stack<Node>();
			dfsStack.push(currentNode);
			int level = 0;
			dfs:while(currentNode != null) {
				if(currentNode.getNodeType() == Node.ELEMENT_NODE) {
					if(handler != null) {
						handler.handle(currentNode, level);
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
	
	/**
	 * To take either textContent or attribute value when there is only one attribute in the node
	 * textContent of node has higher priority
	 * the null return is not allowed
	 * @param node
	 * @return node content
	 * @throws XmlDataException When node is null or both text content and attribute value are null 
	 */
	public String getNodeContentForceNotNull(Node node) throws XmlDataException {
		String res = null;
		String textContent = null;
		String attrValue = null;
		if(node == null) {
			throw new XmlNullNodeException();
		}
		if(node.getAttributes().getLength() == 1) {
			String attrName = node.getAttributes().item(0).getNodeName();	
			try {
				attrValue = this.getAttributeValue(node, attrName).trim();
			} catch (XmlDataException e) {
				e.setAttrName(attrName);
				throw e;
			}
		} else {
			textContent = this.getTextContentOfLeafNode(node).trim();
		}
		
			
		if( textContent == null && attrValue == null) {
			throw new XmlGetNodeContentNullException(node);
		} else if (attrValue != null){
			res = attrValue;
		} else if (textContent != null){
			// textContent of node has higher priority
			res = textContent;
		}

		return res;
	}
	
	/**
	 * To take either textContent or attribute value when there is only one attribute in the node
	 * textContent of node has higher priority
	 * the null return is allowed
	 * @param node
	 * @return node content
	 */
	public String getNodeContentForceNullable(Node node) {
		String res = null;
		if(node == null) {
			return null;
		}
		if(node.getAttributes().getLength() == 1) {
			String attrName = node.getAttributes().item(0).getNodeName();	
			try {
				res = this.getAttributeValue(node, attrName);
			} catch (XmlNoAttributeException e) {
				//do nothing
			}
		} else {
			res = this.getTextContentOfLeafNode(node);
		}
		
		return res;
	}
	
	
	/**
	 * To retrieve all node Content
	 * @param startNode start Node
	 * @return all node Content
	 */
	public String getNodeContentDFS(Node startNode) {
		Node currentNode = startNode;
		StringBuilder sb = new StringBuilder();
		int level = 0;
	
		if(this.isLeafElementNode(currentNode)) {
			sb.append(this.getPrintOpeningNode(currentNode, level));
			//System.out.print(this.getPrintOpeningNode(currentNode, level));
			sb.append(this.getPrintClosingNode(currentNode, level));
			//System.out.print(this.getPrintClosingNode(currentNode, level));
		} else {
			Stack<Node> dfsStack = new Stack<Node>();
			dfsStack.push(currentNode);
			dfs:while(currentNode != null) {
				if(currentNode.getNodeType() == Node.ELEMENT_NODE) {
					if(this.isLeafElementNode(currentNode)) {
						sb.append(this.getPrintOpeningNode(currentNode, level));
						//System.out.print(this.getPrintOpeningNode(currentNode, level));
						sb.append(this.getPrintClosingNode(currentNode, level));
						//System.out.print(this.getPrintClosingNode(currentNode, level));
					} else {
						sb.append(this.getPrintOpeningNode(currentNode, level));
						//System.out.print(this.getPrintOpeningNode(currentNode, level));
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
							if(!this.isLeafElementNode(currentNode)) {
								sb.append(this.getPrintClosingNode(currentNode, level));
								//System.out.print(this.getPrintClosingNode(currentNode, level));
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
		return sb.toString();
	}
		

	/**
	 * To filter nodes which fulfill the given level predicate and node predicate.
	 * @param startNode start Node DFS
	 * @param levelPredicate level predicate
	 * @param predicate node predicate
	 * @return filtered nodes which fulfill the given level predicate and node predicate.
	 */
	public List<Node> filterElementNodesDFS(Node startNode, IntPredicate levelPredicate, Predicate<Node> predicate) {
		List<Node> res = new ArrayList<Node>();
		this.visitChildElementNodesDFS(startNode, (node, level) -> {
			if(levelPredicate.test(level) && predicate.test(node)) {
				res.add(node);
			}
		});
		return res;
	}
	
	/**
	 * To filter nodes which fulfill the given node predicate.
	 * @param startNode start Node DFS
	 * @param predicate node predicate
	 * @return filtered nodes which fulfill the given node predicate.
	 */
	public List<Node> filterElementNodesDFS(Node startNode, Predicate<Node> predicate) {
		return this.filterElementNodesDFS(startNode, level -> true, predicate);
	}

				
	/**
	 * To analyze all attributes under the node with the given name from Document Node by DFS.
	 * @param nodeName node name
	 */
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
	
	
	/**
	 * To get the first child TextNode from the node
	 * if not exists, null will be returned.
	 * @param node node
	 * @return first TextNode
	 */
	public Node getFirstTextNodeOf(Node node) {
		NodeList nl = node.getChildNodes();
		Node res = null;
		for (int i = 0; i < nl.getLength(); i++) {
			if(nl.item(i).getNodeType() == Node.TEXT_NODE) {
				String textValue = nl.item(i).getTextContent();
				if(textValue != null && textValue.length() > 0) {
					return res = nl.item(i);
				}
			}
		}
		return res;
	}
	
	/**
	 * To get the first text value of the node.
	 * @param node node
	 * @return the first text value of the node.
	 */
	public String getFirstTextNodeValue(Node node) {
		Node textNode = this.getFirstTextNodeOf(node);
		if(textNode == null) {
			return "";
		} else {
			return textNode.getTextContent().trim();
		}
	}
	

}
