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
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;


public class XmlTool {

	public Document doc;
	private String filePath;
	List<Node> allNodes = new ArrayList<Node>();
	public int allNodeCount = 0;
	
	public XmlTool() {
		
	}
	public XmlTool(String filePath) {
		loadXML(filePath);
	}
	
	public void loadXML(String filePath) {
		this.filePath = filePath;
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

	public Document getDocument() {
		if(this.doc == null) {
			System.out.println("Load XML first.");
		}
		return this.doc;
	}
	
	public int countDirectChiledElementNodes(Node node) {
		int res = 0;
		NodeList nl = node.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			if(nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
				res++;
			}
		}
		return res;
	}
	
	// encodes a xml file into UTF-8 and create a xml file name with "__to__UTF-8.xml" in the end.
	public void encodeFileToUTF_8() {
		String encodedFilePath = this.filePath+"__to__UTF-8.xml";
		try {
			//to prevent from endless extending to the existing encoded xml,
			//delete the existing encoded file which was created by this program.
			File oldFile= new File(encodedFilePath);
			oldFile.delete();
		} catch (NullPointerException e) {
			// ok. do nothing
		}

		System.out.println(">> Encoding start: " + this.filePath);

		try (
			FileOutputStream fos = new FileOutputStream(encodedFilePath, true);
			BufferedReader br = new BufferedReader(new InputStreamReader(
									new FileInputStream(this.filePath), StandardCharsets.UTF_8));) {
			
			

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
	
	public String getTextContent(Node node) {
		if(node.getFirstChild() != null) {
			if(node.getFirstChild().getNodeType() == Node.TEXT_NODE) {
				return "\n".compareTo(node.getFirstChild().getTextContent()) == 0 ? "" : node.getTextContent();
			}
		}
		return "";
	}
	
	public List<Node> findAllElementNodeDFS() {
		this.visitAllElementNodeDFS(null);
		return this.allNodes;

	}
	public int visitChildElementNodesDFS(Node startNode, XmlToolWorkable worker) {
		int visitedNodes = 0;
		List<Node> allNodes = new ArrayList<>();
		Node currentNode = this.getDocument().getDocumentElement();
		Stack<Node> dfsStack = new Stack<Node>();
		dfsStack.push(this.doc);
		int level = 1;
		while(currentNode != null) {
			if(currentNode.getNodeType() == Node.ELEMENT_NODE) {
				
				this.allNodeCount++;
				allNodes.add(currentNode);
				if(worker != null) {
					worker.work(currentNode, level, this);
				}
				System.out.print(String.format("%8d:",this.allNodeCount));
				System.out.print(" ".repeat(level*2));
				System.out.print("<");
				System.out.print(currentNode.getNodeName());
				System.out.print(">");
				System.out.print(this.getTextContent(currentNode));
				System.out.println();

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
						} catch (EmptyStackException e) {
							break;
						}

						System.out.print("         ");
						System.out.print(" ".repeat(level*2));
						System.out.print("</");
						System.out.print(currentNode.getNodeName());
						System.out.print(">");
						System.out.println();

					}

					currentNode = currentNode.getNextSibling();
					
				}
			}
		} 
		return visitedNodes;
	}

	public int visitAllElementNodeDFS(XmlToolWorkable worker) {
		return this.visitChildElementNodesDFS(this.doc, worker);
	}
	
	public List<Node> findAllElementNodeBFS() { 
		this.visitAllElementNodeBFS(null);
		return this.allNodes;
	}
	
	public void visitAllElementNodeBFS(XmlToolWorkable worker) {
		List<Node> allNodes = new ArrayList<Node>();
		this.allNodeCount = 0;
		Queue<Node> q = new LinkedList<>();
		NodeList nl = this.getDocument().getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			if(nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
				q.add(nl.item(i));
				allNodes.add(nl.item(i));
			}
		}
		
		while(!q.isEmpty()) {
			Node node = q.poll();
			if(worker != null) {
				worker.work(node, this);
			}
			this.allNodeCount++;
			System.out.println(node.getNodeName());
			if(node.getNodeType()==Node.TEXT_NODE) {
				System.out.println(node.getTextContent());
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
		this.allNodes = allNodes;
	}
	
	public static void main(String[] args) {
		XmlTool xt = new XmlTool();
		xt.loadXML("./data/dresden.xml__to__UTF-8.xml");
		xt.visitAllElementNodeBFS(null);
//		xt.visitAllElementNodeDFS(null);

		/*
		System.out.println(((Element)xt.getDocument().getElementsByTagName("listmania").item(2)).getChildNodes().item(19).getParentNode().getNextSibling().getNodeName());
		System.out.println(((Element)xt.getDocument().getElementsByTagName("listmania").item(2)).getChildNodes().item(19).getNodeName());
		System.out.println(((Element)xt.getDocument().getElementsByTagName("listmania").item(2)).getChildNodes().item(19).getTextContent());
		 */
		
		/*
		System.out.println(((Element)xt.getDocument().getElementsByTagName("listmania").item(2)).getChildNodes().item(17).getChildNodes().item(0).getNodeType());
		System.out.println(((Element)xt.getDocument().getElementsByTagName("listmania").item(2)).getChildNodes().item(17).getChildNodes().item(0).getTextContent());
		System.out.println(((Element)xt.getDocument().getElementsByTagName("listmania").item(2)).getChildNodes().item(17).getTextContent());

		System.out.println(xt.getDocument().getElementsByTagName("item").item(0).getChildNodes().item(4));
		System.out.println(xt.getDocument().getElementsByTagName("item").item(0).getChildNodes().item(4).getChildNodes().item(0));
		System.out.println(xt.getDocument().getElementsByTagName("item").item(0).getChildNodes().item(4).getChildNodes().item(1));
		 */

			}

}
