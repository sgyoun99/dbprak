/**
 * Classes needed to read item-shop-data from file and write to DB
 * table item_shop
 * @version 03.06.2021
 */
package entity;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import org.w3c.dom.Node;

import org.hibernate.HibernateException; 
import org.hibernate.Session; 
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

//import JDBCTools.JDBCTool;
import XmlTools.XmlTool;
import exception.SQLKeyDuplicatedException;
import exception.XmlInvalidValueException;
import exception.XmlNoAttributeException;
import exception.XmlValidationFailException;
import main.Config;
import main.CreateTables;
import main.DropTables;
import main.ErrType;
import main.ErrorLogger;

public class ManageItem_Shop {

	public void manageShopItems(SessionFactory factory){
		dresden(factory);
		leipzig(factory);
		System.out.println("\033[1;34m    *\033[35m*\033[33m*\033[32m* \033[91mItem_Shop finished \033[32m*\033[33m*\033[35m*\033[34m*\033[0m");
		
	}
	
	/**
	 * add ShopItem
	 */
	private void addShopItem(Item_Shop shop_item, SessionFactory factory) {
		Session session = factory.openSession();
		Transaction tx = null;
		
		try {
			tx = session.beginTransaction();
			session.save(shop_item); 
			tx.commit();
		} catch (HibernateException e) {
			if (tx!=null) {
				tx.rollback();
			}
			System.out.println("HibernateException for " + shop_item.getItem_id() + " " + shop_item.getShop_id() + " " + shop_item.getCondition()); 
		} finally {
			session.close(); 
		}
	}
	
	
	/**
	 * check read-in data is valid
	 */
	public static Predicate<Double> pred_price = price -> price >= 0 && price<9999999 ;
	public static Predicate<String> pred_currency = curr -> Arrays.asList("EUR","").contains(curr);
	public static BiPredicate<Double,Boolean> pred_avaliablity = (price, avail) -> {
		if(price > 0 && avail) {
			return true;
		} else if(price == 0 && !avail) {
			return true;
		} else {
			return false;
		}

	}; 
	public Predicate<String> pred_condition = cond -> Arrays.asList("new","","second-hand").contains(cond);
	
	public void test(Item_Shop item_shop) throws XmlValidationFailException {
		try {
			if(!ManageItem.pred_item_id.test(item_shop.getItem_id())) {
				XmlInvalidValueException e = new XmlInvalidValueException("item_id Error (length not 10): ("+item_shop.getItem_id()+")"); 
				e.setAttrName("item_id");
				throw e;
			}
			if(!pred_price.test(item_shop.getPrice())) {
				XmlInvalidValueException e =new XmlInvalidValueException("price Error");
				e.setAttrName("price");
				throw e;
			}
			if(!pred_currency.test(item_shop.getCurrency())) {
				XmlInvalidValueException e = new XmlInvalidValueException("currency Error: "+ item_shop.getCurrency());
				e.setAttrName("currency");
				throw e;
			}
			if(!pred_avaliablity.test(item_shop.getPrice(), item_shop.getAvailabiliti())) {
				XmlInvalidValueException e = new XmlInvalidValueException("availability Error");
				e.setAttrName("availabiliti");
				throw e;
			}
			if(!pred_condition.test(item_shop.getCondition())) {
				XmlInvalidValueException e = new XmlInvalidValueException("condition Error: "+item_shop.getCondition());
				e.setAttrName("condition");
				throw e;
			}
		} catch (XmlInvalidValueException e) {
			throw new XmlValidationFailException(e);
		}
	}
	
	/**
	 * read data from shop dresden and write to DB table "item_shop"
	 * extra method required because of differences in file structure
	 */
	public void dresden(SessionFactory factory) {
		String location = "Item_Shop(Dresden)";
		System.out.println(">> Item_Shop Dresden ...");
		XmlTool xt = new XmlTool(Config.DRESDEN_ENCODED);
		ManageShop ms = new ManageShop();
		Shop shop = ms.readShop(Config.DRESDEN_ENCODED);
		int shop_id = ms.getShopId(factory, shop.getShop_name(), shop.getStreet(), shop.getZip());
		
		List<Node> itemList = xt.filterElementNodesDFS(xt.getDocumentNode(), 
				level -> level == 2, 
				node ->  node.getNodeName().equals("item")
		);
		itemList.forEach(itemNode -> {
			Item_Shop item_shop = new Item_Shop();
			item_shop.setShop_id(shop_id);
			try {
				item_shop.setItem_id(xt.getAttributeValue(itemNode, "asin"));
				xt.visitChildElementNodesDFS(itemNode, (nd, lv) -> {
					try {
					//xml data
						if(nd.getNodeName().equals("price")) {
							String mult = "";
							String state = "";
							String currency = "";
							try {
								mult = xt.getAttributeValue(nd, "mult");
								currency = xt.getAttributeValue(nd, "currency");
								state = xt.getAttributeValue(nd, "state");
							} catch (XmlNoAttributeException e) {
								e.setLocation(location);
								e.setItem_id(item_shop.getItem_id());
								ErrorLogger.write(e, xt.getNodeContentDFS(itemNode));
							} finally {
								item_shop.setCurrency(currency);
								item_shop.setPrice(xt.getTextContentOfLeafNode(nd),mult);
								item_shop.setAvailabiliti(item_shop.getPrice());
								item_shop.setCondition(state);
							}
			
						//test
							this.test(item_shop);
						//insert
							this.addShopItem(item_shop, factory);
						}
					} catch (IllegalArgumentException e) {
						ErrorLogger.write(location, item_shop.getItem_id(), ErrType.PROGRAM, "" ,e, xt.getNodeContentDFS(itemNode));
					} catch (XmlValidationFailException e) {
						e.setLocation(location);
						e.setItem_id(item_shop.getItem_id());
						ErrorLogger.write(e, xt.getNodeContentDFS(itemNode));
					} /*catch (SQLException ex) {
						if(ex.getMessage().contains("duplicate key value")) {
							SQLKeyDuplicatedException e = new SQLKeyDuplicatedException();
							e.setAttrName("");
							e.setItem_id(item_shop.getItem_id());
							e.setLocation(location);
							e.setMessage("duplicate key value");
							ErrorLogger.write(e, xt.getNodeContentDFS(itemNode));
						} else {
							ErrorLogger.write(location, item_shop.getItem_id(), ErrType.SQL, "", ex, xt.getNodeContentDFS(itemNode));
						}
					}*/ catch (Exception e) {
						ErrorLogger.write(location, item_shop.getItem_id(), ErrType.PROGRAM, "", e, xt.getNodeContentDFS(itemNode));
					}			
				});
			} catch (XmlNoAttributeException e1) {
				e1.setLocation(location);
				e1.setItem_id(item_shop.getItem_id());
				ErrorLogger.write(e1, xt.getNodeContentDFS(itemNode));
			}
		});
	}
	
	/**
	 * read data from file and insert into DB table "item_shop"
	 * extra method required because of differences in file structure
	 */
	public void leipzig(SessionFactory factory) {
		String location = "Item_Shop(Leipzig)";
		System.out.println(">> Item_Shop Leipzig ...");
		XmlTool xt = new XmlTool(Config.LEIPZIG);
		ManageShop ms = new ManageShop();
		Shop shop = ms.readShop(Config.LEIPZIG);
		int shop_id = ms.getShopId(factory, shop.getShop_name(), shop.getStreet(), shop.getZip());

		List<Node> itemList = xt.filterElementNodesDFS(xt.getDocumentNode(), 
				level -> level == 2, 
				node ->  node.getNodeName().equals("item")
		);
		itemList.forEach(itemNode -> {
			Item_Shop item_shop = new Item_Shop();
			item_shop.setShop_id(shop_id);
			try {
				item_shop.setItem_id(xt.getAttributeValue(itemNode, "asin"));
				xt.visitChildElementNodesDFS(itemNode, (nd, lv) -> {
					try {
					//xml data
						if(nd.getNodeName().equals("price")) {
							String mult = "";
							String state = "";
							String currency = "";
							try {
								mult = xt.getAttributeValue(nd, "mult");
								currency = xt.getAttributeValue(nd, "currency");
								state = xt.getAttributeValue(nd, "state");
							} catch (XmlNoAttributeException e) {
								e.setLocation(location);
								e.setItem_id(item_shop.getItem_id());
								ErrorLogger.write(e, xt.getNodeContentDFS(itemNode));
							} finally {
								item_shop.setCurrency(currency);
								item_shop.setPrice(xt.getTextContentOfLeafNode(nd),mult);
								item_shop.setAvailabiliti(item_shop.getPrice());
								item_shop.setCondition(state);
							}
				
						//test
							this.test(item_shop);
							//insert
							this.addShopItem(item_shop, factory);
						}
					} catch (IllegalArgumentException e) {
						ErrorLogger.write(location, item_shop.getItem_id(), ErrType.PROGRAM, "" ,e, xt.getNodeContentDFS(itemNode));
					} catch (XmlValidationFailException e) {
						e.setLocation(location);
						e.setItem_id(item_shop.getItem_id());
						ErrorLogger.write(e, xt.getNodeContentDFS(itemNode));
					}/* catch (SQLException ex) {
						if(ex.getMessage().contains("duplicate key value")) {
							SQLKeyDuplicatedException e = new SQLKeyDuplicatedException();
							e.setAttrName("");
							e.setItem_id(item_shop.getItem_id());
							e.setLocation(location);
							e.setMessage("duplicate key value");
							ErrorLogger.write(e, xt.getNodeContentDFS(itemNode));
						} else {
							ErrorLogger.write(location, item_shop.getItem_id(), ErrType.SQL, "", ex, xt.getNodeContentDFS(itemNode));
						}
					}*/ catch (Exception e) {
						ErrorLogger.write(location, item_shop.getItem_id(), ErrType.PROGRAM, "", e, xt.getNodeContentDFS(itemNode));
					}		
				});
			} catch (XmlNoAttributeException e1) {
				e1.setLocation(location);
				e1.setItem_id(item_shop.getItem_id());
				ErrorLogger.write(e1, xt.getNodeContentDFS(itemNode));
			}
		});
	}
	
	
	/**
	 * not in use for main-program
	 */
	public static void main(String[] args) throws Exception {
		/*DropTables.dropTable(CreateTables.Errors);
		CreateTables.createTable(CreateTables.Errors);
		DropTables.dropTable(CreateTables.Item_Shop);
		CreateTables.createTable(CreateTables.Item_Shop);
		

		Item_Shop is = new Item_Shop();
		is.dresden();
		is.leipzig();*/
		
	}
}
