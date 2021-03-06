package test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;

import JDBCTools.JDBCTool;
import XmlTools.XmlTool;
import main.Config;




/**
 * temporary Class for category test. 
 *
 */
public class TestCategory {
	
	class Item {
		public String item_id;
		public int cat_id;
		public int main_cat_id;
		public String main_cat_Name;
	}

	class Category {
		public int category_id;
		public String categoryName;
		public int level;
		List<Category> subCategoryList = new ArrayList<>();
		List<Item> itemList = new ArrayList<Item>();
	}

	class DiffItem {
		public String item_id;
		public List<Integer> diff_main_cats = new ArrayList<Integer>();
		
	}
	
	XmlTool xt = new XmlTool(Config.CATEGORY_ENCODED);
	Node categoriesNode = xt.getDocumentNode().getFirstChild().getNextSibling();
	List<Node> mainCategoryNodesList = xt.getDirectChildElementNodes(categoriesNode); //12
	List<String> db_itemList = new ArrayList<String>();
	List<Item> item_category_List = new ArrayList<Item>();
	List<Category> categoryList = new ArrayList<Category>();
	List<String> itemsNotInDB = Arrays.asList(new String[]{"B0006ULVPM", "B00007DXEC", "3551065020"});
	List<String> diffItemsInDB = new ArrayList<String>();
	
	Map<String, List<Integer>> item_MainCatList_Map = new HashMap<String, List<Integer>>();
	Map<Integer,String> mainCategoryMap = new HashMap<Integer, String>();
	Map<String,List<String>> simMap = new HashMap<String, List<String>>();
	Map<String,List<Integer>> diffMap = new HashMap<String, List<Integer>>();
	static int count = 0;
	static int diffCnt = 0;
	
	public void db_diff_main_cat() {
		try {
			JDBCTool.executeUpdate((con, st) -> {
				String sql = "select distinct(item_id) from diff_main_cat";
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery();
				ps.close();
				while(rs.next()) {
					diffItemsInDB.add(rs.getString("item_id"));
				}
			});
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}
	
	public void db_itemList(){
		try {
			JDBCTool.executeUpdate((con, st) -> {
				String sql = "select * from item";
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery();
				ps.close();
				while(rs.next()) {
					db_itemList.add(rs.getString("item_id"));
				}
			});
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public <T> T lastOf(List<T> list) {
		return list.get(list.size()-1);
	}
	
	public void addIfNotExists(List<String> list, String item) {
		boolean exists = false;
		for (int i = 0; i < list.size(); i++) {
			if(((String)item).equals(list.get(i))) {
				exists = true;
				break;
			}
		}
		if(!exists) {
			list.add(item);
		}
	}
	
	public void addIfNotExists(List<Integer> list, int item) {
		boolean exists = false;
		for (int i = 0; i < list.size(); i++) {
			if(list.get(i) == item) {
				exists = true;
				break;
			}
		}
		if(!exists) {
			list.add(item);
		}
	}
	
	public Category findMainCategory(Category subCategory) {
		for (int i = categoryList.size()-1; i >-1; i--) {
			if(categoryList.get(i).level == 1) {
				return categoryList.get(i);
			}
		}
		return null;
	}
	
	public String getCatName(int catId) {
		return categoryList.get(catId -1).categoryName;
	}
	
	public String mainCatNamesToString(String itemId) {
		String res = "(";
		List<Integer> mainCatList = this.item_MainCatList_Map.get(itemId);
		if(mainCatList == null) {
			return res + "-)";
		}
		for(int i = 0; i < mainCatList.size(); i++) {
			int mainCatId = mainCatList.get(i);
			res += this.getCatName(mainCatId);
			if(i < mainCatList.size()-1) {
				res += ", ";
			}
		}
		return res + ")";
	}
	
	public void setSimItemsFromDB() {
		try {
			JDBCTool.executeUpdate((con, st) -> {
				String sql = "select * from similar_items";
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery();
				ps.close();
				while(rs.next()) {
					simMap.compute(rs.getString(1), (k,simItemList) -> {
						if(simItemList!=null) {
							try {
								simItemList.add(rs.getString(2));
							} catch (SQLException e) {
								e.printStackTrace();
							}
							return simItemList;
						} else {
							simItemList = new ArrayList<String>();
							try {
								simItemList.add(rs.getString(2));
							} catch (SQLException e) {
								e.printStackTrace();
							}
							return simItemList;
						}
					});
				}
				
			});
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	
	public List<Integer> intersection(List<Integer> mainCatList1, List<Integer> mainCatList2){
		List<Integer> res = new ArrayList<Integer>();
		if(mainCatList1 == null || mainCatList2 == null) {
			return res;
		}
		mainCatList1.forEach(cat1 -> {
			if(mainCatList2.contains(cat1)) {
				res.add(cat1);
			}
		});
		return res;
	}
	
	public boolean hasNoIntersection(List<Integer> mainCatList1, List<Integer> mainCatList2) {
		return this.intersection(mainCatList1, mainCatList2).size() == 0 ? true : false;
	}
	

	public void init() throws Exception {
		db_itemList();
		setSimItemsFromDB();
		db_diff_main_cat();	
		
		mainCategoryNodesList.forEach(mainCatNode->{
			xt.visitChildElementNodesDFS(mainCatNode, (node, level) -> {
				level++;
				Category category;
				Item item;
				//add to categoryList
				if(node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("category")) {
					String catName = xt.getFirstTextNodeValue(node);
					TestCategory.count++;
					
					category = new Category();
					category.category_id = TestCategory.count;
					category.categoryName = catName;
					category.level = level;
					categoryList.add(category);	
					
				}
				
				//add to itemListFromCategories
				if(node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("item")) {
					category = lastOf(categoryList);
					item = new Item();
					item.item_id = node.getTextContent();
					item.cat_id = category.category_id;
					item.main_cat_id = findMainCategory(lastOf(categoryList)).category_id;
					item.main_cat_Name = findMainCategory(lastOf(categoryList)).categoryName;
					if(!itemsNotInDB.contains(item.item_id)) {
						item_category_List.add(item);
					}
				}
			});
		});

		int cnt = 0;
		for(Item item: item_category_List) {
//			System.out.println(++cnt + ": " + item.item_id + ": " + item.main_cat_id + ": " + item.main_cat_Name);
			item_MainCatList_Map.compute(item.item_id, (item_id,mainCatList) -> {
				if(mainCatList!= null) {
					addIfNotExists(mainCatList, item.main_cat_id);
					return mainCatList;
				} else {
					mainCatList = new ArrayList<>();
//					addIfNotExists(mainCatList, item.main_cat_id);
					mainCatList.add(item.main_cat_id);
					return mainCatList;
				}
			});
		}

		
		//gather 12 main catogories
		System.out.println(" = = = Main Categories = = = ");
		for(Category category: categoryList) {
			if(category.level == 1) {
				System.out.println(category.category_id + ": " + category.categoryName);
				mainCategoryMap.put(category.category_id, category.categoryName);
			}
		}
		
		/*
		item_MainCat_Map.forEach((item_id, mainCategories) -> {
			System.out.print(item_id + ": ");
			mainCategories.forEach(catId -> {
				System.out.print(catId+" ");
				System.out.print(getCatName(catId)+"/ ");
			});
			System.out.println();
		});
		
		
		simMap.forEach((item_id, sim_itemList) -> {
			sim_itemList.forEach(sim_item -> {
//				System.out.println(item + "::" + sim_item);
				diffMap.put(item_id, diff_mainCat(item_MainCat_Map.get(item_id), item_MainCat_Map.get(sim_item)));
			}); 
		});
		
		diffMap.forEach((item, catDiffList)-> {
			if(catDiffList.size() > 0) {
				diffCnt++;
			}
		});
		System.out.println(diffCnt);
		diffMap.forEach((item, catDiffList) -> {
			System.out.print(item + ": ");
			catDiffList.forEach(catID -> System.out.print(catID + " " + this.getCatName(catID) + "/ "));
			System.out.println();
		});
		 */

	}
	
	public static void main(String[] args) throws Exception {
		XmlTool xt = new XmlTool();
		xt.encodeCategoriesXMLToUTF8();

		TestCategory t = new TestCategory();
		t.init();
		
		Map<String,List<String>> resMap = new HashMap<String, List<String>>();
		t.db_itemList.forEach(item -> {
			List<String> simItemList = t.simMap.get(item);
			if(simItemList != null) {
				List<Integer> mainCatList1 = t.item_MainCatList_Map.get(item);
				simItemList.forEach(simItem ->{
					List<Integer> mainCatList2 = t.item_MainCatList_Map.get(simItem);
					if(t.hasNoIntersection(mainCatList1, mainCatList2)) {
						resMap.compute(item, (k, simList)->{
							if(simList == null) {
								simList = new ArrayList<String>();
								simList.add(simItem);
							} else {
								simList.add(simItem);
							}
							return simList;
						});
					}
				});
			}
		});
		
		System.out.println("================================");
		System.out.println(resMap.size());
		resMap.forEach((item, simItemList) -> {
			System.out.print(item + t.mainCatNamesToString(item) + ": ");
			simItemList.forEach(simItem -> {
				System.out.print(simItem + t.mainCatNamesToString(simItem) + "/ ");
			});
			System.out.println();
		});
		
	}
}
