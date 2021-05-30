package entity;

import java.util.function.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import JDBCTools.JDBCTool;
import XmlTools.XmlTool;
import XmlTools.XmlToolWorkable;
import main.Config;
import main.Pgroup;

public class Item {

	String item_id;
	String title;
	Double rating; // how?
	Integer salesranking;
	String image;
	public Pgroup productgroup;

	public String getItem_id() {
		return item_id;
	}

	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Double getRating() {
		return rating;
	}

	public void setRating(Double rating) {
		this.rating = rating;
	}

	public Integer getSalesranking() {
		return salesranking;
	}

	public void setSalesranking(int salesranking) {
		this.salesranking = salesranking;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Pgroup getProductgroup() {
		return productgroup;
	}

	public void setProductgroup(Pgroup productgroup) {
		this.productgroup = productgroup;
	}

	public void setProductgroup(String productgroup) {
		this.productgroup = Pgroup.valueOf(productgroup);
	}
	
	Predicate<Integer> predicate_item_id_predicate = length -> length == 10;
	Predicate<String> predicate_title = title -> title.length() != 0;
	Predicate<Double> predicate_rating = rating -> rating >= 0 && rating <= 5;
	Predicate<Integer> predicate_salesranking = ranking -> ranking >= 0;
	Predicate<String> predicate_image = img -> img.length() != 0;
	
	
	public void dresden() {
		XmlTool xt = new XmlTool(Config.DRESDEN_ENCODED);
		List<Node> items = xt.filterElementNodesDFS(xt.getDocumentNode(), 
			level -> level == 2, 
			node -> node.getNodeName().equals("item")
		);
		items.forEach(node -> {
			setItem_id(xt.getAttributeTextContent(node, "asin"));
			xt.getDirectChildElementNodes(node).forEach(nd -> {
				if(nd.getNodeName().equals("title")) {
					setTitle(node.getNodeName());
				}
				if(nd.getNodeName().equals("details")) {
					setImage(xt.getAttributeTextContent(nd, "img"));
				}
			});
			setSalesranking(Integer.valueOf(xt.getAttributeTextContent(node, "salesrank")));
			setProductgroup(xt.getAttributeTextContent(node, "pgroup"));

		});
		
	}
	
	public static void main(String[] args) {
		
		Item item = new Item();
		
	}
	
}
