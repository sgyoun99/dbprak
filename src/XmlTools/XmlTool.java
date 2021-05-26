package XmlTools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;


public class XmlTool {

	private Document doc;
	private String filePath;
	
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
		return this.doc;
	}
	
	private void encodeFileToUTF_8() {
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
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		System.out.println(">> Encoding Complete: " + encodedFilePath);
        
	}
	
	public void encodeDresdenXmlToUTF_8() {
		XmlTool xt = new XmlTool();

		//before encoding
		xt.loadXML("./data/dresden.xml");

		Document doc = xt.getDocument();
		Node node = doc.getElementsByTagName("shop").item(0);
		Element el = (Element)node;

		System.out.println(">> Before encoding");
		System.out.println(el.getAttribute("name"));
		System.out.println(el.getAttribute("street"));
		System.out.println(el.getAttribute("zip"));

		//after encoding
		xt.encodeFileToUTF_8();

		xt.loadXML("./data/dresden.xml__to__UTF-8.xml");
		doc = xt.getDocument();
		node = doc.getElementsByTagName("shop").item(0);
		el = (Element)node;

		System.out.println(">> After encoding");
		System.out.println(el.getAttribute("name"));
		System.out.println(el.getAttribute("street"));
		System.out.println(el.getAttribute("zip"));
	}
	
	public static void main(String[] args) {
		XmlTool xt = new XmlTool();

		xt.encodeDresdenXmlToUTF_8();
		/*
		 */
		
	}

}
