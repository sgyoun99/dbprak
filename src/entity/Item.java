package entity;

import java.util.function.*;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import JDBCTools.JDBCTool;
import XmlTools.XmlTool;
import main.Config;
import main.Pgroup;

public class Item {

	private String item_id;
	private String title;
	private Double rating; // how?
	private Integer salesranking;
	private String image;
	private Pgroup productgroup;
//	private String productgroup;

	public int insertCount = 0;

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
//	public String getProductgroup() {
		return productgroup;
	}

	public void setProductgroup(Pgroup productgroup) {
//	public void setProductgroup(String productgroup) {
		this.productgroup = productgroup;
	}

	/*
	public void setProductgroup(String productgroup) {
//		this.productgroup = Pgroup.valueOf(productgroup);
		this.productgroup = productgroup;
	}
	 */

	Predicate<String> predicate_item_id = id -> id.length() == 10;
	Predicate<String> predicate_title = title -> title.length() != 0;
	Predicate<Double> predicate_rating = rating -> rating >= 0 && rating <= 5;
	Predicate<Integer> predicate_salesranking = ranking -> ranking >= 0;
//	Predicate<String> predicate_image = img -> img.length() != 0;
	Predicate<String> predicate_image = img -> true;
	
	public <T> boolean test(T value, Predicate<T> predicate) {
		return predicate.test(value);
	}

	public boolean test() {
		return  predicate_item_id.test(this.getItem_id()) &&
				predicate_title.test(this.getTitle()) &&
//				predicate_rating.test(this.getRating()) && // how??
				predicate_salesranking.test(this.getSalesranking()) &&
				predicate_image.test(this.getImage());
	}
	
	public int dresden() {
		XmlTool xt = new XmlTool(Config.DRESDEN_ENCODED);
		List<Node> items = xt.filterElementNodesDFS(xt.getDocumentNode(), 
			level -> level == 3, 
			node -> node.getNodeName().equals("item")
		);
		items.forEach(node -> {
			try {
				setItem_id(xt.getAttributeTextContent(node, "asin"));
//				System.out.println(this.getItem_id());
				xt.getDirectChildElementNodes(node).forEach(nd -> {
					if(nd.getNodeName().equals("title") && xt.isLeafElementNode(nd)) {
						setTitle(nd.getTextContent());
					}
					if(nd.getNodeName().equals("details")) {
						setImage(xt.getAttributeTextContent(nd, "img"));
					}
				});
				String salesRank = xt.getAttributeTextContent(node, "salesrank");
				if(salesRank.length() == 0) {
					setSalesranking(0);
				} else {
					setSalesranking(Integer.valueOf(xt.getAttributeTextContent(node, "salesrank")));
				}
				String pgroup = xt.getAttributeTextContent(node, "pgroup");
				if(pgroup.equals("Music")) {
					pgroup = "Music_CD";
				}
				Pgroup pgr = Pgroup.valueOf(pgroup);
				setProductgroup(pgr);
			} catch (Exception e) {
				System.out.print("Error handling needed: " + this.getItem_id());
				System.out.println(" : " + this.getTitle());
				System.out.println(e);
			}
		
			//insert
			if(this.test()) {
				try {
					JDBCTool.executeUpdate((con, st) ->	{
						String sql = "INSERT INTO ITEM (item_id, title, rating, salesranking, image, productgroup) values (?,?,?,?,?,?::Pgroup)";
						PreparedStatement ps = con.prepareStatement(sql);
						ps.setString(1, this.getItem_id());
						ps.setString(2, this.getTitle());
//						ps.setDouble(3, this.getRating());
						ps.setDouble(3, 0); // temporary
						ps.setInt(4, this.getSalesranking());
						ps.setString(5, this.getImage());
						ps.setString(6, this.getProductgroup().toString());
						ps.executeUpdate();
						ps.close();
						
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
				this.insertCount++;
			} else {
				System.out.print("Test failed : " + this.getItem_id());
				System.out.println(" : " + this.getTitle());
			}
		});
		return this.insertCount;
	}
	
	public static void main(String[] args) {
		
		Item item = new Item();
		try {
			item.dresden();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		System.out.println(item.insertCount);
		
	}
	
}
