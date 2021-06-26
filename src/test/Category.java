package test;

import java.util.List;
import java.util.ArrayList;

class Item {
	public String item_id;
	public int cat_id;
	public int main_cat_id;
	public String main_cat_Name;
}

public class Category {
	public int category_id;
	public String categoryName;
	public int level;
	List<Category> subCategoryList = new ArrayList<>();
	List<Item> itemList = new ArrayList<Item>();
	
	
}
