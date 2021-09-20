/**
 * Class to read Shops with their attributes from file
 * and insert them into DB table shop
 * @version 30.06.2021
 */
package entity;

import java.util.Set;

public class Shop {
	
	private int shop_id;
	private String shop_name;
	private String street;
	private String zip;
	private Set shop_items;

	public Shop() {}
	public Shop(String shop_name, String street, String zip) {
		this.shop_name = shop_name;
		this.street = street;
		this.zip = zip;
	}
	
	public int getShop_id() {
		return shop_id;
	}
	public void setShop_id( int shop_id ) {
		this.shop_id = shop_id;
	}
	public String getShop_name() {
		return shop_name;
	}
	public void setShop_name( String shop_name ) {
		this.shop_name = shop_name;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet( String street ) {
		this.street = street;
	}
	public String getZip() {
		return zip;
	}
	public void setZip( String zip ) {
		this.zip = zip;
	}

	public Set getShop_items() {
		return this.shop_items;
	}

	public void setShop_items(Set shop_items) {
		this.shop_items = shop_items;
	}

}
