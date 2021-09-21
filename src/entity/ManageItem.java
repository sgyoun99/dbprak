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
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.hibernate.HibernateException; 
import org.hibernate.Session; 
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

//import JDBCTools.JDBCTool;
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

public class ManageItem {

	public void readIn(SessionFactory factory){   

      	dresden(factory);
		leipzig(factory);
		getSimItem(factory, Config.DRESDEN_ENCODED, "Dresden");
		getSimItem(factory, Config.LEIPZIG, "Leipzig");
		System.out.println("\033[1;34m    *\033[35m*\033[33m*\033[32m* \033[91mItems finished \033[32m*\033[33m*\033[35m*\033[34m*\033[0m");

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

	private void test(Item item) throws XmlValidationFailException {
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
		} catch (XmlInvalidValueException e) {
			throw new XmlValidationFailException(e);
		}
	}

	private void addItem(Item item, SessionFactory factory) {
		Session session = factory.openSession();
		Transaction tx = null;
		
		try {
			tx = session.beginTransaction();
			item.setRating(0.0);
			session.save(item); 
			tx.commit();
		} catch (HibernateException e) {
			if (tx!=null) {
				tx.rollback();
			}
			System.out.println("HibernateException for " + item.getItem_id()); 
		} finally {
			session.close(); 
		}
	}
	
	/**
	 * method to read item-data from file and write to DB table "item"
	 * extra method needed because of different file structure
	 */
	private void dresden(SessionFactory factory) {

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
				this.addItem(item, factory);

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
			} /*catch (SQLException ex) {
				if(ex.getMessage().contains("duplicate key value")) {
					SQLKeyDuplicatedException e = new SQLKeyDuplicatedException();
					e.setAttrName("item_id");
					e.setItem_id(item.getItem_id());
					e.setLocation(location);
					e.setMessage("List Empty");
					ErrorLogger.write(e, xt.getNodeContentDFS(itemNode));
				} else {
					ErrorLogger.write(location, item.getItem_id(), ErrType.SQL, "", ex, xt.getNodeContentDFS(itemNode));
				}
			}*/ catch (Exception e) {
				ErrorLogger.write(location, item.getItem_id(), ErrType.PROGRAM, "", e, xt.getNodeContentDFS(itemNode));
			}
		});
	}
	
	/**
	 * read item-data from file and write to DB table "item"
	 * extra method needed because of different file structure
	 */
	private void leipzig(SessionFactory factory) {
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
				this.addItem(item, factory);
				
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
			} /*catch (SQLException ex) {
				if(ex.getMessage().contains("duplicate key value")) {
					SQLKeyDuplicatedException e = new SQLKeyDuplicatedException();
					e.setAttrName("item_id");
					e.setItem_id(item.getItem_id());
					e.setLocation(location);
					e.setMessage("List Empty");
					ErrorLogger.write(e, xt.getNodeContentDFS(itemNode));
				} else {
					ErrorLogger.write(location, item.getItem_id(), ErrType.SQL, "", ex, xt.getNodeContentDFS(itemNode));
				}
			} */catch (Exception e) {
				ErrorLogger.write(location, item.getItem_id(), ErrType.PROGRAM, "", e, xt.getNodeContentDFS(itemNode));
			}
		});
	}
	
	/**
	 * add Sim_items to DB
	 * nicht in batches sonst Abbruch bei Hiberate Fehler
	 */
	private void addSimItems(SessionFactory factory, HashMap<String,ArrayList<String>> simItemMap) {
		for(Map.Entry<String, ArrayList<String>> set : simItemMap.entrySet()) {
			Session session = factory.openSession();
			Transaction tx = null;
			
			try {
				tx = session.beginTransaction();
				Item item = (Item)session.get(Item.class, set.getKey()); 
				if(item != null) {
					HashSet<Item> simItems = new HashSet<Item>();
					for(String simItemId : set.getValue()) {					
						Item simItem = (Item)session.get(Item.class, simItemId);
						if(simItem != null) {
							simItems.add(simItem);
						}
					}
					if(simItems.size()!= 0){
						item.setSim_items(simItems);
						session.update(item); 
					}
				}
				tx.commit();
			} catch (HibernateException e) {
				if (tx!=null) {
					tx.rollback();
				}
				System.out.println("HibernateException for " + set.getKey()); 
			} finally {
				session.close(); 
			}
		}
	}


	private void getSimItem(SessionFactory factory, String xmlPath, String location) {	
		HashMap<String,ArrayList<String>> item_simItem_map = new HashMap<String,ArrayList<String>>();
		System.out.println(">> Similar_Items " + location + " ...");
		XmlTool xt = new XmlTool(xmlPath);
		xt.filterElementNodesDFS(xt.getDocumentNode(), node -> {
			return node.getNodeName().equals("similars") && !xt.isLeafElementNode(node);
		}
		).forEach(similars -> xt.visitChildElementNodesDFS(similars, (node, level) -> {
			try {
				
				//xml data
				if(node.getNodeName().equals("item")) {
					Node parentItemNode = similars.getParentNode();

					String item_id = "";
					String sim_item_id = "";
					item_id = xt.getAttributeValue(parentItemNode, "asin");
					sim_item_id = xt.getAttributeValue(node, "asin");

					if(!item_id.equals("") && !sim_item_id.equals("")) {
						item_simItem_map.computeIfAbsent(item_id,itemVar->new ArrayList<String>()).add(sim_item_id);
					}
				}else {
					String item_id = "";
					String sim_item_id = "";
					item_id = xt.getAttributeValue(similars.getParentNode(), "asin");
					NodeList nodeList = node.getChildNodes();
					for(int i=0; i<nodeList.getLength(); i++) {
						if(nodeList.item(i).getNodeName().equals("asin")){
							sim_item_id = nodeList.item(i).getTextContent().trim();
						}
						if(!item_id.equals("") && !sim_item_id.equals("")) {
							item_simItem_map.computeIfAbsent(item_id,itemVar->new ArrayList<String>()).add(sim_item_id);
						}
					}
				}
				
			} catch (IllegalArgumentException e) {
				ErrorLogger.write(location, ErrType.PROGRAM , e, xt.getNodeContentDFS(node));
			} catch (XmlNoAttributeException e) {
				e.setLocation(location);
				ErrorLogger.write(e, xt.getNodeContentDFS(node));
			}/* catch (XmlValidationFailException e) {
				e.setLocation(location);
				ErrorLogger.write(e, xt.getNodeContentDFS(node));
			} catch (SQLException e) {
				if(!e.getMessage().contains("duplicate key value")) {
					ErrorLogger.write(location, ErrType.SQL, e, xt.getNodeContentDFS(node));
				}
			}*/ catch (Exception e) {
				ErrorLogger.write(location, ErrType.PROGRAM, e, xt.getNodeContentDFS(node));
			}	

		}));

		//test?
		
		//insert into DB
		addSimItems(factory, item_simItem_map);
	
	}
	
	
}
