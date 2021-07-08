/**
 * Classes to read Categories from file and insert into the DB in 
 * category, sub_category and item_category
 * @version 03-07-2021
 */

package entity;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.ArrayList;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.Stack;

import JDBCTools.JDBCTool;
import exception.SQLKeyDuplicatedException;
import exception.XmlDataException;
import exception.XmlInvalidValueException;
import exception.XmlNoAttributeException;
import exception.XmlNullNodeException;
import exception.XmlValidationFailException;
import main.Config;
//import main.CreateTables;
//import main.DropTables;
import main.ErrType;
import main.ErrorLogger;

/**
 * Class that stores a CategoryNode from the file and
 * the Category's id
 */
class IDNode{
	public int id;
	public Node node;

	public IDNode(int id, Node node){
		this.id = id;
		this.node = node;
	}
}

/**
 * class that stores the relation of two categories,
 * generally the mainCategory id and
 * the subCategory id as well as the subCategory's name
 */
class Category_relation{
	private int[] ids = new int[2];
	private String name = "";


	public Category_relation(int idp, int ido, String name){
		this.ids[0] = idp;
		this.ids[1] = ido;
		this.name = name;
	}

	public int getOwnId(){
		return this.ids[1];
	}
	public int getParent(){
		return this.ids[0];
	}
	public String getName(){
		return this.name;
	}

}

/**
 * Class to read Categories from file and insert them into the DB
 */
public class Category {

	private ArrayList<Category_relation> categoryList = new ArrayList<Category_relation>();
	private ArrayList<String[]> itemList = new ArrayList<String[]>();
	private int idCounter = 0;
		
	/**
	 * generates the next id and returns the current new Id
	 */
	public int getNewID(){
		this.idCounter ++;
		return (this.idCounter -1);
	}
	
	/**
	 * Reads the categories from file and insert them into the DB
	 */
	public void readCategory() {
		System.out.println(">> Category ...");

		Node root;
		
		/**
		 * read the Categories from file
		 * then add them to categoryList with their mainCategory
		 * if there are items attached to a category, add categoryID and itemID as String[] to itemList
		 */
		try {
			File inputFile = new File(Config.CATEGORY_ENCODED);
	
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document document = dBuilder.parse(inputFile);
			root = document.getDocumentElement();
			Category_relation rootCategory = new Category_relation(0,0,"root");
			categoryList.add(rootCategory);
			IDNode rootNode = new IDNode(getNewID(), root);

			Stack<IDNode> categoryStack = new Stack<IDNode>();
			categoryStack.push(rootNode); 

			while(!categoryStack.empty()){
				IDNode workingNode = categoryStack.pop();

				NodeList nodeList = workingNode.node.getChildNodes();
				
				for(int i=0; i< nodeList.getLength(); i++){
					if(nodeList.item(i).getNodeName() == "item"){
						itemList.add( new String[] {workingNode.id + "", nodeList.item(i).getTextContent()} );
						continue;
					}
					if((nodeList.item(i).getNodeType() != Node.TEXT_NODE)){						
						String content = nodeList.item(i).getTextContent();
						int index = content.indexOf("\n");
						String name = content;
						if(index > 0){
							name = content.substring(0, index);
						}
						Category_relation newCat = new Category_relation(workingNode.id, getNewID(), name);
						categoryList.add(newCat);
						categoryStack.push(new IDNode(newCat.getOwnId(), nodeList.item(i)));
					}
				}
			}
		}catch (Exception e) {
			System.out.println("Cannot read Categorys from file");
			e.printStackTrace();
		}

		//root node is no category
		categoryList.remove(0);

		//insert categories into DB category
		for(Category_relation category : categoryList){
			try{
				JDBCTool.executeUpdateAutoCommitOn((con, st) -> {
					String sql;
					PreparedStatement ps;
					sql = "INSERT INTO category(category_id, name)	VALUES (?, ?);";
					ps = con.prepareStatement(sql);
					ps.setInt(1, category.getOwnId());
					ps.setString(2, category.getName());
					ps.executeUpdate();
					ps.close();	
				});
			}catch (SQLException ex) {
				ErrorLogger.write("Category", String.valueOf(category.getOwnId()), ErrType.SQL, "category", ex, "");
				
			} catch (Exception e) {
				System.out.println("other exception in Category");
			}
		}

		//insert into DB subCategorys
		System.out.println(">> Sub_Category ... ");
		for(Category_relation category : categoryList){
			if(category.getParent() != 0){	// if parentID == 0 => main_category
				try{
					JDBCTool.executeUpdateAutoCommitOn((con, st) -> {
						String sql;
						PreparedStatement ps;
						sql = "INSERT INTO sub_category(over_category_id, sub_category_id)	VALUES (?, ?);";
						ps = con.prepareStatement(sql);
						ps.setInt(1, category.getParent());
						ps.setInt(2, category.getOwnId());
						ps.executeUpdate();
						ps.close();	
					});
				}catch (SQLException ex) {
					ErrorLogger.write("sub_category", String.valueOf(category.getOwnId()), ErrType.SQL, "sub_category", ex, "");
					
				} catch (Exception e) {
					System.out.println("other exception in Category");
				}
			}
		} 

		//Insert into DB items and categoryId to item_category
		System.out.println(">> Item_Category ... ");
		for(String[] item : itemList){			
			try{
				
				JDBCTool.executeUpdateAutoCommitOn((con, st) -> {
					String sql;
					PreparedStatement ps;
					sql = "INSERT INTO item_category(item_id, category_id)	VALUES (?, ?);";
					ps = con.prepareStatement(sql);
					ps.setInt(2, Integer.valueOf(item[0]));
					ps.setString(1, item[1]);
					ps.executeUpdate();
					ps.close();	
				});
			}catch (SQLException ex) {
				if(ex.getMessage().contains("constraint")){
					ErrorLogger.write("Item_Category", item[1], ErrType.SQL_FK_ERROR, "item_id", ex, "");
				}
				
			} catch (Exception e) {
				System.out.println("other exception in Category");
			}			
		} 

	} 

	/**
	 * not currently in use
	 */
	public static void main(String[] args) throws Exception {
		
//		DropTables.dropTables();
//		CreateTables.createTables();

		Category category = new Category();
		category.readCategory();

	}
	
	
}
