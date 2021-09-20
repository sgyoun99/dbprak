/**
 * Classes needed to read item-shop-data from file and write to DB
 * table item_shop
 * @version 03.06.2021
 */
package entity;

import java.io.Serializable;

public class Item_Shop implements Serializable {

	private String item_id;
	private int shop_id;
	private String currency;
	private Double price;
	private Boolean availabiliti;
	private String condition;

	public Item_Shop() {}
	public Item_Shop(String item_id, int shop_id, String currency, Double price, Boolean availabiliti, String condition) {
		this.item_id = item_id;
		this.shop_id = shop_id;
		this.currency = currency;
		this.price = price;
		this.availabiliti = availabiliti;
		this.condition = condition;
	}

	public String getItem_id() {
		return item_id;
	}
	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}
	public int getShop_id() {
		return shop_id;
	}
	public void setShop_id(int shop_id) {
		this.shop_id = shop_id;
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
	public Boolean getAvailabiliti() {
		return availabiliti;
	}
	public void setAvailabiliti(Boolean availabiliti) {
		this.availabiliti = availabiliti;
	}
	public void setAvailabiliti(Double price) {
		if(price > 0) {
			this.availabiliti = true;
		} else {
			this.availabiliti = false;
		}
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}

	public boolean equals(Object obj) {
		if (obj == null) return false;
      	if (!this.getClass().equals(obj.getClass())) return false;

		Item_Shop obj2 = (Item_Shop) obj;
		if(this.getItem_id().equals(obj2.getItem_id()) && this.getShop_id() == obj2.getShop_id() && this.getCondition().equals(obj2.getCondition())) {
			return true;
		}else{
			return false;
		}		
	 }

	 public int hashCode() {
		int tmp = 0;
		tmp = ( item_id).hashCode();
		return tmp;
	 }
	
	
	/**
	 * not in use for main-program
	 */
	public static void main(String[] args) throws Exception {
	/*	DropTables.dropTable(CreateTables.Errors);
		CreateTables.createTable(CreateTables.Errors);
		DropTables.dropTable(CreateTables.Item_Shop);
		CreateTables.createTable(CreateTables.Item_Shop);
		

		Item_Shop is = new Item_Shop();
		is.dresden();
		is.leipzig();*/
		
	}
}
