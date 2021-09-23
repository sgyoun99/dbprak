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
import java.util.HashSet;

import org.hibernate.HibernateException; 
import org.hibernate.Session; 
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.Query;

//import JDBCTools.JDBCTool;
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
public class ManageCategory {

	private ArrayList<Category_relation> categoryList = new ArrayList<Category_relation>();
	private ArrayList<String[]> itemList = new ArrayList<String[]>();
	private int idCounter = 0;


	/**
	 * function that manages reading and inserting category, sub_category, item_category
	 */
	public void manageCategories(SessionFactory factory) {
		readCategory();
		insertCategory(factory, categoryList);
		addOverCategories(factory, categoryList);
		addItems(factory, itemList);
		System.out.println("\033[1;34m    *\033[35m*\033[33m*\033[32m* \033[91mCategories finished \033[32m*\033[33m*\033[35m*\033[34m*\033[0m");
	}
		
	/**
	 * generates the next id and returns the current new Id
	 */
	public int getNewID(){
		this.idCounter ++;
		return (this.idCounter -1);
	}
	
	/**
	 * insert Category into DB
	 * ACHTUNG: batches weil sehr viele Kategorien
	 */
	public void insertCategory(SessionFactory factory, ArrayList<Category_relation> categoryList) {
		Session session = factory.openSession();
		Transaction tx = null;
		
		try {
			tx = session.beginTransaction();
			for(int i=0; i<categoryList.size(); i++) {
				Category cat = new Category(categoryList.get(i).getOwnId(), categoryList.get(i).getName());			
				session.save(cat); 
				if( i % 50 == 0 ) {
					session.flush();
					session.clear();
				}
			}
			tx.commit();
		} catch (HibernateException e) {
			if (tx!=null) {
                tx.rollback();
            }
			System.out.println("HibernateException for adding Categories" /*+ cat.getCategory_id()*/); 
		} finally {
			session.close(); 
		}
	}

	/**
	 * insert into sub_category
	 * Keine Batch sondern for-Schleife außen weil sonst dieselbe Kategory evtl häufiger gleichzeitig geupdated werden soll -> funktioniert nicht
	 */
	public void addOverCategories(SessionFactory factory, ArrayList<Category_relation> categoryList){
		for(int i=0; i<categoryList.size(); i++) {
			if(categoryList.get(i).getParent()!=0){
				Session session = factory.openSession();
				Transaction tx = null;
			
				try {
					tx = session.beginTransaction();
						Category cat = new Category(categoryList.get(i).getOwnId(), categoryList.get(i).getName());
						HashSet<Category> catSet = new HashSet<Category>();
						Category parentCat = session.get(Category.class, categoryList.get(i).getParent());
						if(parentCat!=null){
							catSet.add(parentCat);
							cat.setOver_categories(catSet);			
							session.update(cat); 
						}			 
					tx.commit();
				} catch (HibernateException e) {
					if (tx!=null) tx.rollback();
					System.out.println("HibernateException for adding Sub_Categories o: " + categoryList.get(i).getParent() + " s: " + categoryList.get(i).getOwnId());
				} finally {
					session.close(); 
				}
			}
		}
	}


	/**
	 * insert into sub_category
	 * Keine Batch sondern for-Schleife außen weil sonst dieselbe Kategory evtl häufiger gleichzeitig geupdated werden soll -> funktioniert nicht
	 * evtl IdNode ändern und namen hinzufügen?
	 * ACHTUNG: Fehler, wenn das item nicht existiert! Abfangen
	 */
	public void addItems(SessionFactory factory, ArrayList<String[]> itemList){
		for(String[] item : itemList) {
			Session session = factory.openSession();
			Transaction tx = null;
		
			try {
				tx = session.beginTransaction();
				Category cat = session.get(Category.class, Integer.valueOf(item[0]));
				if(cat!=null){
					Item catItem = session.get(Item.class, item[1]);
					if(catItem!=null){
						cat.getItems().add(catItem); 			
						session.update(cat);
					}	
				}		 
				tx.commit();
			} catch (HibernateException e) {
				if (tx!=null) tx.rollback();
				System.out.println("HibernateException for adding Item_Categories " + item[0] + " " + item[1]);
			} finally {
				session.close(); 
			}
			
		}
	}




	/**
	 * read the Categories from file
	 * then add them to categoryList with their mainCategory
	 * if there are items attached to a category, add categoryID and itemID as String[] to itemList
	 */
	public void readCategory() {
		System.out.println(">> Category ...");

		Node root;
		
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
		
	} 

	/**
	 * not currently in use
	 */
	public static void main(String[] args) throws Exception {
		
//		DropTables.dropTables();
//		CreateTables.createTables();

		//Category category = new Category();
		//category.readCategory();

	}
	
	
}
