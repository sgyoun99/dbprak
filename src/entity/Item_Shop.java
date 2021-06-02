package entity;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import org.w3c.dom.Node;

import JDBCTools.JDBCTool;
import XmlTools.XmlDataException;
import XmlTools.XmlTool;
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
	
	

	public boolean test(Item_Shop item_shop) throws XmlDataException {
		if(!Item.pred_item_id.test(item_shop.getItem_id())) {throw new XmlDataException("item_id Error (length not 10): ("+item_shop.getItem_id()+")"); }
		if(!pred_price.test(item_shop.getPrice())) {throw new XmlDataException("price Error");}
		if(!pred_currency.test(item_shop.getCurrency())) {throw new XmlDataException("currency Error: "+ item_shop.getCurrency());}
		if(!pred_avaliablity.test(item_shop.getPrice(), item_shop.getAvailaility())) {throw new XmlDataException("availability Error");}
		if(!pred_condition.test(item_shop.getCondition())) {throw new XmlDataException("condition Error: "+item_shop.getCondition());}
		return true;
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
			try {
			//xml data
				Item_Shop item_shop = new Item_Shop();
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
						} catch (XmlDataException e) {
							//get information
//							xt.printNodeContentsDFS(node.getParentNode());
//							e.printStackTrace();
						} finally {
							item_shop.setCurrency(currency);
							item_shop.setPrice(xt.getTextContent(nd),mult);
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
				ErrorLogger.write(location, ErrType.PROGRAM , e, xt.getNodeContentDFS(itemNode));
			} catch (XmlDataException e) {
				ErrorLogger.write(location, ErrType.XML, e, xt.getNodeContentDFS(itemNode));
			} catch (SQLException e) {
				ErrorLogger.write(location, ErrType.SQL, e, xt.getNodeContentDFS(itemNode));
			} catch (Exception e) {
				ErrorLogger.write(location, ErrType.PROGRAM, e, xt.getNodeContentDFS(itemNode));
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
		itemList.forEach(node -> {
			try {
			//xml data
				Item_Shop item_shop = new Item_Shop();
				item_shop.setItem_id(xt.getAttributeValue(node, "asin"));
				item_shop.setShop_name(shop.getShop_name());
				item_shop.setStreet(shop.getStreet());
				item_shop.setZip(shop.getZip());
				xt.visitChildElementNodesDFS(node, (nd, lv) -> {
					if(nd.getNodeName().equals("price")) {
						String mult = "";
						String state = "";
						String currency = "";
						try {
							mult = xt.getAttributeValue(nd, "mult");
							currency = xt.getAttributeValue(nd, "currency");
							state = xt.getAttributeValue(nd, "state");
						} catch (XmlDataException e) {
							//do nothing. It will be tested in "this.test(item_shop)";
						} finally {
							item_shop.setCurrency(currency);
							item_shop.setPrice(xt.getTextContent(nd),mult);
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
				ErrorLogger.write(location, ErrType.PROGRAM , e, xt.getNodeContentDFS(node));
			} catch (XmlDataException e) {
				ErrorLogger.write(location, ErrType.XML, e, xt.getNodeContentDFS(node));
			} catch (SQLException e) {
				ErrorLogger.write(location, ErrType.SQL, e, xt.getNodeContentDFS(node));
			} catch (Exception e) {
				ErrorLogger.write(location, ErrType.PROGRAM, e, xt.getNodeContentDFS(node));
			}			
		});
	}
	
	
	
	public static void main(String[] args) throws Exception {
		DropTables.dropTable(CreateTables.Item_Shop);
		CreateTables.createTable(CreateTables.Item_Shop);
		

		Item_Shop is = new Item_Shop();
		is.dresden();
		is.leipzig();
		
	}
}
