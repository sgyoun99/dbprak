/**
 * Class needed to read similar_item-data from file 
 * and write to DB table "similar_items"
 * @version 03.06.2021
 */
package entity;

import java.io.Serializable;


public class Similar_Items implements Serializable {
	String item_id;
	String sim_item_id;

	public String getItem_id() {
		return item_id;
	}
	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}
	public String getSim_item_id() {
		return sim_item_id;
	}
	public void setSim_item_id(String sim_item_id) {
		this.sim_item_id = sim_item_id;
	}
	
	public Similar_Items() {}
	public Similar_Items(String item_id, String sim_item_id) {
		this.item_id = item_id;
		this.sim_item_id = sim_item_id;
	}
	
	
	//not used for main-program
	public static void main(String[] args) throws Exception {
		
	}
}
