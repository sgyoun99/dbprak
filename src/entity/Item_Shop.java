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
import main.Pgroup;

public class Item_Shop {

	private String item_id;
	private String shop_name;
	private String street;
	private String zip;
	private String currency;
	private Double price;
	private Boolean availaility;
	private String condition;

	private Node currentNode;
	public int errorCount = 0;

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
	
	
	
	Predicate<Double> pred_price = price -> price >= 0 && price<9999999 ;
	Predicate<String> pred_currency = curr -> Arrays.asList("EUR","").contains(curr);
	BiPredicate<Double,Boolean> pred_avaliablity = (price, avail) -> {
		if(price > 0 && avail) {
			return true;
		} else if(price == 0 && !avail) {
			return true;
		} else {
			return false;
		}

	}; 
	Predicate<String> pred_condition = cond -> Arrays.asList("new","","second-hand").contains(cond);
	
	

	public boolean test() throws XmlDataException {
		if(!pred_price.test(getPrice())) {throw new XmlDataException("price Error");}
		if(!pred_currency.test(getCurrency())) {throw new XmlDataException("currency Error");}
		if(!pred_avaliablity.test(getPrice(), getAvailaility())) {throw new XmlDataException("availability Error");}
		if(!pred_condition.test(getCondition())) {throw new XmlDataException("condition Error");}
		return true;
	}
	
	public void dresden() {
		XmlTool xt = new XmlTool(Config.DRESDEN_ENCODED);
		Shop shopDresden = new Shop(Config.DRESDEN_ENCODED);
		shopDresden.readShop();
		
		List<Node> itemList = xt.filterElementNodesDFS(xt.getDocumentNode(), 
				level -> level == 2, 
				node ->  node.getNodeName().equals("item")
		);
		itemList.forEach(node -> {
			this.currentNode = node;
			try {
			//xml data
				setItem_id(xt.getAttributeTextContent(node, "asin"));
				setShop_name(shopDresden.getShop_name());
				setStreet(shopDresden.getStreet());
				setZip(shopDresden.getZip());
				xt.visitChildElementNodesDFS(node, (nd, lv) -> {
					if(nd.getNodeName().equals("price")) {
						String mult = "";
						String state = "";
						String currency = "";
						try {
							mult = xt.getAttributeTextContent(nd, "mult");
							currency = xt.getAttributeTextContent(nd, "currency");
							state = xt.getAttributeTextContent(nd, "state");
						} catch (XmlDataException e) {
							//get information
							xt.printNodeContentsDFS(node.getParentNode());
							e.printStackTrace();
						} finally {
							setCurrency(currency);
							setPrice(xt.getTextContent(nd),mult);
							setAvailaility(getPrice());
							setCondition(state);
						}
					}
				});
		
			//insert
				this.test();
				JDBCTool.executeUpdate((con, st) ->	{
					String sql = "INSERT INTO ITEM_SHOP "
							+ "(item_id, shop_name, street, zip, currency, price, availability, condition) "
							+ "values (?,?,?,?,?,?,?,?)";
					PreparedStatement ps = con.prepareStatement(sql);
					ps.setString(1, getItem_id());
					ps.setString(2, getShop_name());
					ps.setString(3, getStreet());
					ps.setString(4, getZip());
					ps.setString(5, getCurrency());
					ps.setDouble(6, getPrice());
					ps.setBoolean(7, getAvailaility());
					ps.setString(8, getCondition());
					ps.executeUpdate();
					ps.close();
					
				});
			} catch (IllegalArgumentException e) {
				this.errorCount++;
				System.out.println();
				xt.printNodeContentsDFS(this.currentNode);
				e.printStackTrace();
//				System.out.println("Error in the item: asin=" + this.getItem_id() + " | title=" + this.getTitle());
			} catch (XmlDataException e) {
				this.errorCount++;
				System.out.println();
				xt.printNodeContentsDFS(this.currentNode);
				e.printStackTrace();
//				System.out.println("Error in the item: asin=" + this.getItem_id() + " | title=" + this.getTitle());
			} catch (SQLException e) {
				this.errorCount++;
				// to-do : Logging
				System.out.println();
//				System.out.println("Error in the item: asin=" + this.getItem_id() + " | title=" + this.getTitle());
				xt.printNodeContentsDFS(this.currentNode);
				e.printStackTrace();
			} catch (Exception e) {
				this.errorCount++;
				System.out.println();
//				System.out.println("Error in the item: asin=" + this.getItem_id() + " | title=" + this.getTitle());
				xt.printNodeContentsDFS(this.currentNode);
				e.printStackTrace();
			}			
		});
	}
	
	public static void main(String[] args) {
	
		Item_Shop is = new Item_Shop();
		is.dresden();
	}
}
