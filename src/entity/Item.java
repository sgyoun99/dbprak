package entity;

import java.util.function.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import JDBCTools.JDBCTool;
import XmlTools.XmlDataException;
import XmlTools.XmlTool;
import XmlTools.XmlUploadException;
import main.Config;
import main.CreateTables;
import main.DropTables;
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
	

	public boolean testDresdenItem() throws XmlDataException {
		if(!predicate_item_id.test(this.getItem_id())) {throw new XmlDataException("item_id Error"); }
		if(!predicate_title.test(this.getTitle())) {throw new XmlDataException("title Error"); }
	//	if(!predicate_rating.test(this.getRating())) {throw new XmlDataException("item Error"); } // how??
		if(!predicate_salesranking.test(this.getSalesranking())) {throw new XmlDataException("salesranking Error"); }
		if(!predicate_image.test(this.getImage())) {throw new XmlDataException("img Error"); }
		return true;
	}
	
	public int dresden() {
		XmlTool xt = new XmlTool(Config.DRESDEN_ENCODED);
		List<Node> items = xt.filterElementNodesDFS(xt.getDocumentNode(), 
			level -> level == 3, 
			node -> node.getNodeName().equals("item")
		);
		items.forEach(node -> {
			try {
			//xml data
				setItem_id(xt.getAttributeTextContent(node, "asin"));
//				System.out.println(this.getItem_id());
				xt.getDirectChildElementNodes(node).forEach(nd -> {
					if(nd.getNodeName().equals("title") && xt.isLeafElementNode(nd)) {
						setTitle(nd.getTextContent());
					}
					if(nd.getNodeName().equals("details")) {
						try {
							setImage(xt.getAttributeTextContent(nd, "img"));
						} catch (XmlDataException e) {
							e.printStackTrace();
						}
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
		
			//insert
				this.testDresdenItem();
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
			} catch (IllegalArgumentException e) {
				System.out.println();
				e.printStackTrace();
				System.out.println("Error in the item: " + this.getItem_id() + "/ title: " + this.getTitle());
			} catch (XmlDataException e) {
				System.out.println();
				e.printStackTrace();
				System.out.println("Error in the item: " + this.getItem_id() + "/ title: " + this.getTitle());
			} catch (SQLException e) {
				// to-do : Logging
				System.out.println();
				e.printStackTrace();
				System.out.println("SQL Error in the item: " + this.getItem_id() + "/ title: " + this.getTitle());
			} catch (Exception e) {
				System.out.println();
				e.printStackTrace();
				System.out.println("Error in the item: " + this.getItem_id() + "/ title: " + this.getTitle());
			}
			this.insertCount++;
		});
		return this.insertCount;
	}
	
	/*
	public static void main(String[] args) {
		
		DropTables.dropTables();
		CreateTables.createTables();
		
		Item item = new Item();
		item.dresden();
		System.out.println(item.insertCount);
		
	}
	 */
	
}
