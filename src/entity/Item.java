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
import XmlTools.XmlToolWorkable;
import XmlTools.XmlUploadException;
import main.Config;
import main.CreateTables;
import main.DropTables;
import main.ErrorLogger;
import main.Pgroup;

public class Item {

	private String item_id;
	private String title;
	private Double rating; // how?
	private Integer salesranking;
	private String image;
	private Pgroup productgroup;
//	private String productgroup;


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
		this.productgroup = productgroup;
	}

	public Predicate<String> predicate_item_id = id -> id.length() == 10;
	public Predicate<String> predicate_title = title -> title.length() != 0;
	public Predicate<Double> predicate_rating = rating -> rating >= 0 && rating <= 5;
	public Predicate<Integer> predicate_salesranking = ranking -> ranking >= 0;
	public Predicate<String> predicate_image = img -> true; // null allowed
	

	public boolean test() throws XmlDataException {
		if(!predicate_item_id.test(this.getItem_id())) {throw new XmlDataException("item_id Error (length not 10)"); }
		if(!predicate_title.test(this.getTitle())) {throw new XmlDataException("title Error (title empty)"); }
	//	if(!predicate_rating.test(this.getRating())) {throw new XmlDataException("rating Error (out of range"); } // how??
		if(!predicate_salesranking.test(this.getSalesranking())) {throw new XmlDataException("salesranking Error"); }
		if(!predicate_image.test(this.getImage())) {throw new XmlDataException("img Error"); }
		return true;
	}
	
	public void dresden() {
		XmlTool xt = new XmlTool(Config.DRESDEN_ENCODED);
		List<Node> items = xt.filterElementNodesDFS(xt.getDocumentNode(), 
			level -> level == 2, 
			node -> node.getNodeName().equals("item")
		);
		items.forEach(node -> {
			try {
			//xml data
				setItem_id(xt.getAttributeValue(node, "asin"));
				xt.getDirectChildElementNodes(node).forEach(nd -> {
					if(nd.getNodeName().equals("title") && xt.isLeafElementNode(nd)) {
						setTitle(nd.getTextContent());
					}
					if(nd.getNodeName().equals("details")) {
						try {
							setImage(xt.getAttributeValue(nd, "img"));
						} catch (XmlDataException e) {
							e.printStackTrace();
						}
					}
				});
				String salesRank = xt.getAttributeValue(node, "salesrank");
				if(salesRank.length() == 0) {
					setSalesranking(0);
				} else {
					setSalesranking(Integer.valueOf(salesRank));
				}
				String pgroup = xt.getAttributeValue(node, "pgroup");
				if(pgroup.equals("Music")) {
					pgroup = "Music_CD";
				}
				Pgroup pgr = Pgroup.valueOf(pgroup);
				setProductgroup(pgr);
		
			//insert
				this.test();
				JDBCTool.executeUpdate((con, st) ->	{
					String sql = "INSERT INTO ITEM (item_id, title, rating, salesranking, image, productgroup) values (?,?,?,?,?,?::Pgroup)";
					PreparedStatement ps = con.prepareStatement(sql);
					ps.setString(1, this.getItem_id());
					ps.setString(2, this.getTitle());
//					ps.setDouble(3, this.getRating());
					ps.setDouble(3, 0); // temporary
					ps.setInt(4, this.getSalesranking());
					ps.setString(5, this.getImage());
					ps.setString(6, this.getProductgroup().toString());
					ps.executeUpdate();
					ps.close();
					
				});
			} catch (IllegalArgumentException e) {
				ErrorLogger.write("Item(dresden)", "IllegalArgumentException", e.getMessage(), xt.getNodeContentDFS(node));
			} catch (XmlDataException e) {
				ErrorLogger.write("Item(dresden)", "XMLDataException", e.getMessage(), xt.getNodeContentDFS(node));
			} catch (SQLException e) {
				ErrorLogger.write("Item(dresden)", "SQLException", e.getMessage(), xt.getNodeContentDFS(node));
			} catch (Exception e) {
				ErrorLogger.write("Item(dresden)", "Exception", e.getMessage(), xt.getNodeContentDFS(node));
			}
		});
	}
	
	public void leipzig() {
		XmlTool xt = new XmlTool(Config.LEIPZIG);
		List<Node> items = xt.filterElementNodesDFS(xt.getDocumentNode(), 
				level -> level == 2, 
				node -> node.getNodeName().equals("item")
		);
		items.forEach(node -> {
			try {
			//xml data
				setItem_id(xt.getAttributeValue(node, "asin"));
				xt.getDirectChildElementNodes(node).forEach(nd -> {
					if(nd.getNodeName().equals("title") && xt.isLeafElementNode(nd)) {
						setTitle(nd.getTextContent());
					}
				});
				String salesRank = xt.getAttributeValue(node, "salesrank");
				if(salesRank.length() == 0) {
					setSalesranking(0);
				} else {
					setSalesranking(Integer.valueOf(salesRank));
				}
				setImage(xt.getAttributeValue(node, "picture"));
				String pgroup = xt.getAttributeValue(node, "pgroup");
				if(pgroup.equals("Music")) {
					pgroup = "Music_CD";
				}
				Pgroup pgr = Pgroup.valueOf(pgroup);
				setProductgroup(pgr);
		
			//insert
				this.test();
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
				ErrorLogger.write("Item(leipzig)", "IllegalArgumentException", e.getMessage(), xt.getNodeContentDFS(node));
			} catch (XmlDataException e) {
				ErrorLogger.write("Item(leipzig)", "XMLDataException", e.getMessage(), xt.getNodeContentDFS(node));
			} catch (SQLException e) {
				ErrorLogger.write("Item(leipzig)", "SQLException", e.getMessage(), xt.getNodeContentDFS(node));
			} catch (Exception e) {
				ErrorLogger.write("Item(leipzig)", "Exception", e.getMessage(), xt.getNodeContentDFS(node));
			}
		});
	}

	public static void main(String[] args) {
		
		DropTables.dropTables();
		CreateTables.createTables();
		
		Item item = new Item();
		item.dresden();
		item.leipzig();
		
	}
	/*
	 */
	
}
