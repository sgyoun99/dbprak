/**
 * Class needed to read item-data from file
 * and write to DB table "item"
 * @version 03.06.2021
 */
package entity;

import java.util.function.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.w3c.dom.Node;

import JDBCTools.JDBCTool;
import XmlTools.XmlTool;
import exception.SQLKeyDuplicatedException;
import exception.XmlDataException;
import exception.XmlNoAttributeException;
import exception.XmlValidationFailException;
import exception.XmlInvalidValueException;
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

	/**
	 * check data from file for validity
	 */
	public static Predicate<String> pred_item_id = item_id -> item_id != null && item_id.length() > 0;
	public static Predicate<String> pred_title = title -> title != null && title.length() != 0;
	public static Predicate<Double> pred_rating = rating -> rating >= 0 && rating <= 5;
	public static Predicate<Integer> pred_salesranking = ranking -> ranking >= 0;
	public static Predicate<String> pred_image = img -> true; // null allowed
	public static Predicate<String> pred_pgroup = pgroup -> Pgroup.isValueOfPgroup(pgroup);	

	public void test(Item item) throws XmlValidationFailException {
		try {
			if(!pred_item_id.test(item.getItem_id())) {
				XmlInvalidValueException e = new XmlInvalidValueException("item_id Error (id does not exist): "+item.getItem_id());
				e.setAttrName("item_id");
				throw e;
			}
			if(!pred_title.test(item.getTitle())) {
				XmlInvalidValueException e =new XmlInvalidValueException("title Error (title empty)"); 
				e.setAttrName("title");
				throw e;
			}
			/*
			if(!pred_rating.test(item.getRating())) {
				XmlInvalidValueException e = new XmlInvalidValueException("rating Error (out of range");  // how??
				e.setAttrName("rating");
				throw e;
			}
			 */
			if(!pred_salesranking.test(item.getSalesranking())) {
				XmlInvalidValueException e = new XmlInvalidValueException("salesranking Error"); 
				e.setAttrName("salesranking");
				throw e;
			}
			if(!pred_image.test(item.getImage())) {
				XmlInvalidValueException e = new XmlInvalidValueException("img Error");
				e.setAttrName("image");
				throw e;
			}
	//		if(!pred_pgroup.test(item.getProductgroup().toString())) {throw new XmlInvalidValueException("pgroup Error"); } // not necessary
		} catch (XmlInvalidValueException e) {
			throw new XmlValidationFailException(e);
		}
	}
	
	/**
	 * method to read item-data from file and write to DB table "item"
	 * extra method needed because of different file structure
	 */
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
				String item_id = xt.getAttributeValue(itemNode, "asin");
				item.setItem_id(item_id);
				xt.getDirectChildElementNodes(itemNode).forEach(nd -> {
					if(nd.getNodeName().equals("title") && xt.isLeafElementNode(nd)) {
						item.setTitle(nd.getTextContent());
					}
					if(nd.getNodeName().equals("details")) {
						try {
							item.setImage(xt.getAttributeValue(nd, "img"));
						} catch (XmlNoAttributeException e) {
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
					ps.setDouble(3, 0); // temporary, later changed if there are any reviews to base an updated rating on
					ps.setInt(4, item.getSalesranking());
					ps.setString(5, item.getImage());
					ps.setString(6, item.getProductgroup().toString());
					ps.executeUpdate();
					ps.close();
					
				});
			} catch (IllegalArgumentException e) {
				ErrorLogger.write(location, item.getItem_id(), ErrType.PROGRAM, "" ,e, xt.getNodeContentDFS(itemNode));
			} catch (XmlValidationFailException e) {
				e.setLocation(location);
				e.setItem_id(item.getItem_id());
				ErrorLogger.write(e, xt.getNodeContentDFS(itemNode));
			} catch (XmlDataException e) {
				e.setLocation(location);
				e.setItem_id(item.getItem_id());
				ErrorLogger.write(e, xt.getNodeContentDFS(itemNode));
			} catch (SQLException ex) {
				if(ex.getMessage().contains("duplicate key value")) {
					SQLKeyDuplicatedException e = new SQLKeyDuplicatedException();
					e.setAttrName("item_id");
					e.setItem_id(item_id);
					e.setLocation(location);
					e.setMessage("List Empty");
					ErrorLogger.write(e, xt.getNodeContentDFS(itemNode));
				} else {
					ErrorLogger.write(location, item_id, ErrType.SQL, "", ex, xt.getNodeContentDFS(itemNode));
				}
			} catch (Exception e) {
				ErrorLogger.write(location, item.getItem_id(), ErrType.PROGRAM, "", e, xt.getNodeContentDFS(itemNode));
			}
		});
	}
	
	/**
	 * read item-data from file and write to DB table "item"
	 * extra method needed because of different file structure
	 */
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
					ps.setDouble(3, 0); // temporary, later changed if there are any reviews to base an updated rating on
					ps.setInt(4, item.getSalesranking());
					ps.setString(5, item.getImage());
					ps.setString(6, item.getProductgroup().toString());
					ps.executeUpdate();
					ps.close();
					
				});
			} catch (IllegalArgumentException e) {
				ErrorLogger.write(location, item.getItem_id(), ErrType.PROGRAM, "" ,e, xt.getNodeContentDFS(itemNode));
			} catch (XmlValidationFailException e) {
				e.setLocation(location);
				e.setItem_id(item.getItem_id());
				ErrorLogger.write(e, xt.getNodeContentDFS(itemNode));
			} catch (XmlDataException e) {
				e.setLocation(location);
				e.setItem_id(item.getItem_id());
				ErrorLogger.write(e, xt.getNodeContentDFS(itemNode));
			} catch (SQLException ex) {
				if(ex.getMessage().contains("duplicate key value")) {
					SQLKeyDuplicatedException e = new SQLKeyDuplicatedException();
					e.setAttrName("item_id");
					e.setItem_id(item_id);
					e.setLocation(location);
					e.setMessage("List Empty");
					ErrorLogger.write(e, xt.getNodeContentDFS(itemNode));
				} else {
					ErrorLogger.write(location, item_id, ErrType.SQL, "", ex, xt.getNodeContentDFS(itemNode));
				}
			} catch (Exception e) {
				ErrorLogger.write(location, item.getItem_id(), ErrType.PROGRAM, "", e, xt.getNodeContentDFS(itemNode));
			}
		});
	}
	
	public void handleDuplicatedPKDresden(Item item) {
		//to-do
	}

	public void handleDuplicatedPKLeipzig(Item item) {
		//to-do
	}

	/**
	 * not currently used for main-program
	 */
	public static void main(String[] args) throws Exception {
		
		DropTables.dropTables();
		CreateTables.createTables();
		
		Item item = new Item();
		item.dresden();
		item.leipzig();
		
	}
	/*
	 */
	
}
