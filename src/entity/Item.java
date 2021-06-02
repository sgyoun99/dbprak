package entity;

import java.util.function.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.w3c.dom.Node;

import JDBCTools.JDBCTool;
import XmlTools.XmlDataException;
import XmlTools.XmlTool;
import main.Config;
import main.CreateTables;
import main.DropTables;
import main.ErrType;
import main.ErrorLogger;
import main.Pgroup;

public class Item {

	private String item_id;
	private String title;
	private Double rating; // how?
	private Integer salesranking;
	private String image;
	private Pgroup productgroup;


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
	
	public void setProductgroup(String pgroup) {
		if(pgroup.equals("Music")) {
			this.productgroup = Pgroup.valueOf("Music_CD");
		} else {
			this.productgroup = Pgroup.valueOf(pgroup);
		}
	}

	public static Predicate<String> pred_item_id = item_id -> item_id.length() == 10;
	public static Predicate<String> pred_title = title -> title != null && title.length() != 0;
	public static Predicate<Double> pred_rating = rating -> rating >= 0 && rating <= 5;
	public static Predicate<Integer> pred_salesranking = ranking -> ranking >= 0;
	public static Predicate<String> pred_image = img -> true; // null allowed
	public static Predicate<String> pred_pgroup = pgroup -> Pgroup.isValueOfPgroup(pgroup);
	

	public void test(Item item) throws XmlDataException {
		if(!pred_item_id.test(item.getItem_id())) {throw new XmlDataException("item_id Error (length not 10): "+item.getItem_id()); }
		if(!pred_title.test(item.getTitle())) {throw new XmlDataException("title Error (title empty)"); }
//		if(!predicate_rating.test(item.getRating())) {throw new XmlDataException("rating Error (out of range"); } // how??
		if(!pred_salesranking.test(item.getSalesranking())) {throw new XmlDataException("salesranking Error"); }
		if(!pred_image.test(item.getImage())) {throw new XmlDataException("img Error"); }
//		if(!pred_pgroup.test(item.getProductgroup().toString())) {throw new XmlDataException("pgroup Error"); } // not necessary
	}
	
	public void dresden() {
		String location = "Item(dresden)";
		System.out.println(">> Item Dresden ...");
		XmlTool xt = new XmlTool(Config.DRESDEN_ENCODED);
		List<Node> items = xt.filterElementNodesDFS(xt.getDocumentNode(), 
			level -> level == 2, 
			node -> node.getNodeName().equals("item")
		);
		items.forEach(itemNode -> {
			Item item = new Item();
			try {
			//xml data
				item.setItem_id(xt.getAttributeValue(itemNode, "asin"));
				xt.getDirectChildElementNodes(itemNode).forEach(nd -> {
					if(nd.getNodeName().equals("title") && xt.isLeafElementNode(nd)) {
						item.setTitle(nd.getTextContent());
					}
					if(nd.getNodeName().equals("details")) {
						try {
							item.setImage(xt.getAttributeValue(nd, "img"));
						} catch (XmlDataException e) {
							//do nothing. null allowed
						}
					}
				});
				String salesRank = xt.getAttributeValue(itemNode, "salesrank");
				if(salesRank.length() == 0) {
					item.setSalesranking(0);
				} else {
					item.setSalesranking(Integer.valueOf(salesRank));
				}
				String pgroup = xt.getAttributeValue(itemNode, "pgroup");
				item.setProductgroup(pgroup);
		
			//insert
				this.test(item);
				JDBCTool.executeUpdate((con, st) ->	{
					String sql = "INSERT INTO ITEM ("
							+ " item_id, title, rating, salesranking, image, productgroup)"
							+ " VALUES (?,?,?,?,?,?::Pgroup)";
					PreparedStatement ps = con.prepareStatement(sql);
					ps.setString(1, item.getItem_id());
					ps.setString(2, item.getTitle());
//					ps.setDouble(3, item.getRating());
					ps.setDouble(3, 0); // temporary
					ps.setInt(4, item.getSalesranking());
					ps.setString(5, item.getImage());
					ps.setString(6, item.getProductgroup().toString());
					ps.executeUpdate();
					ps.close();
					
				});
			} catch (IllegalArgumentException e) {
				ErrorLogger.write(location, ErrType.PROGRAM , e, xt.getNodeContentDFS(itemNode));
			} catch (XmlDataException e) {
				ErrorLogger.write(location, ErrType.XML, e, xt.getNodeContentDFS(itemNode));
			} catch (SQLException e) {
				if(e.getMessage().contains("duplicate key value")) {
					this.handleDuplicatedPKDresden(item);
				} else {
					ErrorLogger.write(location, ErrType.SQL, e, xt.getNodeContentDFS(itemNode));
				}
			} catch (Exception e) {
				ErrorLogger.write(location, ErrType.PROGRAM, e, xt.getNodeContentDFS(itemNode));
			}
		});
	}
	
	public void leipzig() {
		String location = "Item(leipzig)";
		System.out.println(">> Item Leipzig ...");
		XmlTool xt = new XmlTool(Config.LEIPZIG);
		List<Node> items = xt.filterElementNodesDFS(xt.getDocumentNode(), 
				level -> level == 2, 
				node -> node.getNodeName().equals("item")
		);
		items.forEach(itemNode -> {
			Item item = new Item();
			try {
			//xml data
				item.setItem_id(xt.getAttributeValue(itemNode, "asin"));
				xt.getDirectChildElementNodes(itemNode).forEach(nd -> {
					if(nd.getNodeName().equals("title") && xt.isLeafElementNode(nd)) {
						item.setTitle(nd.getTextContent());
					}
				});
				String salesRank = xt.getAttributeValue(itemNode, "salesrank");
				if(salesRank.length() == 0) {
					item.setSalesranking(0);
				} else {
					item.setSalesranking(Integer.valueOf(salesRank));
				}
				item.setImage(xt.getAttributeValue(itemNode, "picture"));
				String pgroup = xt.getAttributeValue(itemNode, "pgroup");
				item.setProductgroup(pgroup);
		
			//insert
				this.test(item);
				JDBCTool.executeUpdate((con, st) ->	{
					String sql = "INSERT INTO ITEM ("
							+ " item_id, title, rating, salesranking, image, productgroup)"
							+ " VALUES (?,?,?,?,?,?::Pgroup)";
					PreparedStatement ps = con.prepareStatement(sql);
					ps.setString(1, item.getItem_id());
					ps.setString(2, item.getTitle());
					//ps.setDouble(3, item.getRating());
					ps.setDouble(3, 0); // temporary
					ps.setInt(4, item.getSalesranking());
					ps.setString(5, item.getImage());
					ps.setString(6, item.getProductgroup().toString());
					ps.executeUpdate();
					ps.close();
					
				});
			} catch (IllegalArgumentException e) {
				ErrorLogger.write(location, ErrType.PROGRAM , e, xt.getNodeContentDFS(itemNode));
			} catch (XmlDataException e) {
				ErrorLogger.write(location, ErrType.XML, e, xt.getNodeContentDFS(itemNode));
			} catch (SQLException e) {
				if(e.getMessage().contains("duplicate key value")) {
					this.handleDuplicatedPKLeipzig(item);
				} else {
					ErrorLogger.write(location, ErrType.SQL, e, xt.getNodeContentDFS(itemNode));
				}
			} catch (Exception e) {
				ErrorLogger.write(location, ErrType.PROGRAM, e, xt.getNodeContentDFS(itemNode));
			}
		});
	}
	
	public void handleDuplicatedPKDresden(Item item) {
		//to-do
	}

	public void handleDuplicatedPKLeipzig(Item item) {
		//to-do
	}

	public static void main(String[] args) throws Exception {
		
		DropTables.dropTables();
		CreateTables.createTables();
		
		Item item = new Item();
		item.leipzig();
		item.dresden();
		
	}
	/*
	 */
	
}
