package dresden;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import XmlTools.XmlTool;

public class Dresden {

	
	
	
	
	
	
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

		Dresden dresden = new Dresden();
		dresden.encodeDresdenXmlToUTF_8();
	}

}
