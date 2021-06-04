package entity;

import java.util.ArrayList;
import java.util.List;

class ItemID {
	String item_id;

	public String getItem_id() {
		return item_id;
	}

	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}
	
}
public class Category {
	int category_id;
	String categoryName;
	List<Category> subCategory = new ArrayList<>();
	List<ItemID> item = new ArrayList<>();
	public int getCategory_id() {
		return category_id;
	}
	public void setCategory_id(int category_id) {
		this.category_id = category_id;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public List<Category> getSubCategory() {
		return subCategory;
	}
	public List<ItemID> getItem() {
		return item;
	}
	
	
	public boolean hasSubCategory() {
		return this.subCategory.size() > 0;
	}
	
	
	
	
	
}
