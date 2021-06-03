package entity;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import org.w3c.dom.Node;

import JDBCTools.JDBCTool;
import XmlTools.XmlTool;
import exception.SQLKeyDuplicatedException;
import exception.XmlDataException;
import exception.XmlInvalidValueException;
import exception.XmlNoAttributeException;
import exception.XmlValidationFailException;
import main.Config;
import main.CreateTables;
import main.DropTables;
import main.ErrType;
import main.ErrorLogger;

public class Item_Shop {

	private String item_id;
	private String shop_name;
	private String street;
	private String zip;
	private String currency;
	private Double price;
	private Boolean availaility;
	private String condition;


	public String getItem_id() {
		return item_id;
	}
	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}
	public String getShop_name() {
		return shop_name;
	}
	public void setShop_name(String shop_name) {
		this.shop_name = shop_name;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public void setPrice(String price) {
		if("".equals(price)) {
			this.price = Double.valueOf(0);
		} else {
			this.price = Double.valueOf(price);
		}
	}
	public void setPrice(String price, String mult) {
			if("".equals(price)) {
			this.price = Double.valueOf(0);
		} else {
			this.price = Double.valueOf(price) * Double.valueOf(mult);
		}
	}
	public Boolean getAvailaility() {
		return availaility;
	}
	public void setAvailaility(Boolean availaility) {
		this.availaility = availaility;
	}
	public void setAvailaility(Double price) {
		if(price > 0) {
			this.availaility = true;
		} else {
			this.availaility = false;
		}
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	
	
	
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
			if(!Item.pred_item_id.test(item_shop.getItem_id())) {
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
				e.setAttrName("currentcy");
				throw e;
			}
			if(!pred_avaliablity.test(item_shop.getPrice(), item_shop.getAvailaility())) {
				XmlInvalidValueException e = new XmlInvalidValueException("availability Error");
				e.setAttrName("availability");
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
	
	//finished
	public void dresden() {
		String location = "Item_Shop(Dresden)";
		System.out.println(">> Item_Shop Dresden ...");
		XmlTool xt = new XmlTool(Config.DRESDEN_ENCODED);
		Shop shop = new Shop(Config.DRESDEN_ENCODED);
		shop.readShop();
		
		List<Node> itemList = xt.filterElementNodesDFS(xt.getDocumentNode(), 
				level -> level == 2, 
				node ->  node.getNodeName().equals("item")
		);
		itemList.forEach(itemNode -> {
			Item_Shop item_shop = new Item_Shop();
			try {
			//xml data
				item_shop.setItem_id(xt.getAttributeValue(itemNode, "asin"));
				item_shop.setShop_name(shop.getShop_name());
				item_shop.setStreet(shop.getStreet());
				item_shop.setZip(shop.getZip());
				xt.visitChildElementNodesDFS(itemNode, (nd, lv) -> {
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
							item_shop.setAvailaility(item_shop.getPrice());
							item_shop.setCondition(state);
						}
					}
				});
		
			//test
				this.test(item_shop);
			//insert
				JDBCTool.executeUpdate((con, st) ->	{
					String sql = "INSERT INTO ITEM_SHOP "
							+ "(item_id, shop_name, street, zip, currency, price, availability, condition) "
							+ "values (?,?,?,?,?,?,?,?)";
					PreparedStatement ps = con.prepareStatement(sql);
					ps.setString(1, item_shop.getItem_id());
					ps.setString(2, item_shop.getShop_name());
					ps.setString(3, item_shop.getStreet());
					ps.setString(4, item_shop.getZip());
					ps.setString(5, item_shop.getCurrency());
					ps.setDouble(6, item_shop.getPrice());
					ps.setBoolean(7, item_shop.getAvailaility());
					ps.setString(8, item_shop.getCondition());
					ps.executeUpdate();
					ps.close();
					
				});
			} catch (IllegalArgumentException e) {
				ErrorLogger.write(location, item_shop.getItem_id(), ErrType.PROGRAM, "" ,e, xt.getNodeContentDFS(itemNode));
			} catch (XmlValidationFailException e) {
				e.setLocation(location);
				e.setItem_id(item_shop.getItem_id());
				e.setNode(itemNode);
				ErrorLogger.write(e, xt.getNodeContentDFS(itemNode));
			} catch (XmlDataException e) {
				e.setLocation(location);
				e.setItem_id(item_shop.getItem_id());
				e.setNode(itemNode);
				ErrorLogger.write(e, xt.getNodeContentDFS(itemNode));
			} catch (SQLException e) {
				if(e.getMessage().contains(JDBCTool.KEY_DUPLICATED)) {
//					this.handleDuplicatedPK();
					SQLKeyDuplicatedException keyDupExc = new SQLKeyDuplicatedException(e.getMessage());
					ErrorLogger.write(location,item_shop.getItem_id(),  ErrType.SQL_DUPLICATE, "", keyDupExc, xt.getNodeContentDFS(itemNode));
				} else {
					ErrorLogger.write(location,item_shop.getItem_id(),  ErrType.SQL, "", e, xt.getNodeContentDFS(itemNode));
				}
			} catch (Exception e) {
				ErrorLogger.write(location, item_shop.getItem_id(), ErrType.PROGRAM, "", e, xt.getNodeContentDFS(itemNode));
			}			
		});
	}
	
	public void leipzig() {
		String location = "Item_Shop(Leipzig)";
		System.out.println(">> Item_Shop Leipzig ...");
		XmlTool xt = new XmlTool(Config.LEIPZIG);
		Shop shop = new Shop(Config.LEIPZIG);
		shop.readShop();
		
		List<Node> itemList = xt.filterElementNodesDFS(xt.getDocumentNode(), 
				level -> level == 2, 
				node ->  node.getNodeName().equals("item")
		);
		itemList.forEach(itemNode -> {
			Item_Shop item_shop = new Item_Shop();
			try {
			//xml data
				item_shop.setItem_id(xt.getAttributeValue(itemNode, "asin"));
				item_shop.setShop_name(shop.getShop_name());
				item_shop.setStreet(shop.getStreet());
				item_shop.setZip(shop.getZip());
				xt.visitChildElementNodesDFS(itemNode, (nd, lv) -> {
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
							item_shop.setAvailaility(item_shop.getPrice());
							item_shop.setCondition(state);
						}
					}
				});
		
			//test
				this.test(item_shop);
			//insert
				JDBCTool.executeUpdate((con, st) ->	{
					String sql = "INSERT INTO ITEM_SHOP "
							+ "(item_id, shop_name, street, zip, currency, price, availability, condition) "
							+ "values (?,?,?,?,?,?,?,?)";
					PreparedStatement ps = con.prepareStatement(sql);
					ps.setString(1, item_shop.getItem_id());
					ps.setString(2, item_shop.getShop_name());
					ps.setString(3, item_shop.getStreet());
					ps.setString(4, item_shop.getZip());
					ps.setString(5, item_shop.getCurrency());
					ps.setDouble(6, item_shop.getPrice());
					ps.setBoolean(7, item_shop.getAvailaility());
					ps.setString(8, item_shop.getCondition());
					ps.executeUpdate();
					ps.close();
					
				});
			} catch (IllegalArgumentException e) {
				ErrorLogger.write(location, item_shop.getItem_id(), ErrType.PROGRAM, "" ,e, xt.getNodeContentDFS(itemNode));
			} catch (XmlValidationFailException e) {
				e.setLocation(location);
				e.setItem_id(item_shop.getItem_id());
				e.setNode(itemNode);
				ErrorLogger.write(e, xt.getNodeContentDFS(itemNode));
			} catch (XmlDataException e) {
				e.setLocation(location);
				e.setItem_id(item_shop.getItem_id());
				e.setNode(itemNode);
				ErrorLogger.write(e, xt.getNodeContentDFS(itemNode));
			} catch (SQLException e) {
				if(e.getMessage().contains(JDBCTool.KEY_DUPLICATED)) {
//					this.handleDuplicatedPK();
					SQLKeyDuplicatedException keyDupExc = new SQLKeyDuplicatedException(e.getMessage());
					ErrorLogger.write(location,item_shop.getItem_id(),  ErrType.SQL_DUPLICATE, "", keyDupExc, xt.getNodeContentDFS(itemNode));
				} else {
					ErrorLogger.write(location,item_shop.getItem_id(),  ErrType.SQL, "", e, xt.getNodeContentDFS(itemNode));
				}
			} catch (Exception e) {
				ErrorLogger.write(location, item_shop.getItem_id(), ErrType.PROGRAM, "", e, xt.getNodeContentDFS(itemNode));
			}		
		});
	}
	
	
	
	public static void main(String[] args) throws Exception {
		DropTables.dropTable(CreateTables.Errors);
		CreateTables.createTable(CreateTables.Errors);
		DropTables.dropTable(CreateTables.Item_Shop);
		CreateTables.createTable(CreateTables.Item_Shop);
		

		Item_Shop is = new Item_Shop();
		is.dresden();
		is.leipzig();
		
	}
}
