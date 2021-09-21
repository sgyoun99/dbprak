/**
 * Class needed to read item-data from file
 * and write to DB table "item"
 * @version 03.06.2021
 */
package entity;

import java.util.Set;

import main.Pgroup;

public class Item {

	private String item_id;
	private String title;
	private Double rating; 
	private Integer salesranking;
	private String image;
	private Pgroup productgroup;

	private Set shop_items;
	private Set sim_items;
	private Set items;

	private Set books;
	private Set cds;
	private Set dvds;

	private Set reviews;



	public Item() {}
	public Item(String item_id, String title, Double rating, int salesranking, String image, Pgroup productgroup) {
		this.item_id = item_id;
		this.title = title;
		this.rating = rating;
		this.salesranking = salesranking;
		this.image = image;
		this.productgroup = productgroup;
	}


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




	public Set getShop_items() {
		return this.shop_items;
	}
	public void setShop_items(Set shop_items) {
		this.shop_items = shop_items;
	}
	public Set getSim_items() {
		return this.sim_items;
	}
	public void setSim_items(Set sim_items) {
		this.sim_items = sim_items;
	}
	public Set getItems() {
		return this.items;
	}
	public void setItems(Set items) {
		this.items = items;
	}

	public Set getBooks() {
		return this.books;
	}
	public void setBooks(Set books) {
		this.books = books;
	}
	public Set getCds() {
		return this.cds;
	}
	public void setCds(Set cds) {
		this.cds = cds;
	}
	public Set getDvds() {
		return this.dvds;
	}
	public void setDvds(Set dvds) {
		this.dvds = dvds;
	}
	public Set getReviews() {
		return this.reviews;
	}
	public void setReviews(Set reviews) {
		this.reviews = reviews;
	}

	


	

	

	/**
	 * not currently used for main-program
	 */
/*	public static void main(String[] args) throws Exception {
		
		DropTables.dropTables();
		CreateTables.createTables();
		
		Item item = new Item();
		item.dresden();
		item.leipzig();
		
	}*/
	/*
	 */
	
}
