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
import main.CreateTables;
import main.DropTables;
import main.ErrType;
import main.ErrorLogger;


class IDNode{
	public int id;
	public Node node;

	public IDNode(int id, Node node){
		this.id = id;
		this.node = node;
	}
}

class TheCategory{
	private int[] ids = new int[2];
	private String name = "";

	public TheCategory() {
		this.ids[0] = 0;
		this.ids[1] = 0;
		this.name = "root";
	}
	public TheCategory(int idp, int ido, String name){
		this.ids[0] = idp;
		this.ids[1] = ido;
		this.name = name;
	}

	public void setName(String name){
		this.name = name;
	}
	public void setParent(int parent){
		ids[0] = parent;
	}
	public void setOwnId(int id){
		ids[1] = id;
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


public class Cat {

	private ArrayList<TheCategory> categoryList = new ArrayList<TheCategory>();
	private ArrayList<String[]> itemList = new ArrayList<String[]>();
	private int idCounter = 0;
		

	public int getNewID(){
		this.idCounter ++;
		return (this.idCounter -1);
	}
	

	public void readCat() {
		String location = "Cat";
		System.out.println("\033[1;34m*\033[35m*\033[33m*\033[32m* \033[91m > Category ... \033[32m*\033[33m*\033[35m*\033[34m*\033[0m");

		Node root;

		try {
//			File inputFile = new File("../data/categories.xml");
			File inputFile = new File(Config.CATEGORY_ENCODED);
	
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document document = dBuilder.parse(inputFile);
			root = document.getDocumentElement();
			TheCategory rootCat = new TheCategory();
			categoryList.add(rootCat);
			IDNode rootNode = new IDNode(getNewID(), root);

			Stack<IDNode> catStack = new Stack<IDNode>();
			catStack.push(rootNode); 

			while(!catStack.empty()){
				IDNode workingNode = catStack.pop();

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
						TheCategory newCat = new TheCategory(workingNode.id, getNewID(), name);
						categoryList.add(newCat);
						catStack.push(new IDNode(newCat.getOwnId(), nodeList.item(i)));
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}


		categoryList.remove(0);
		for(TheCategory cat : categoryList){
			try{
				JDBCTool.executeUpdateAutoCommitOn((con, st) -> {
					String sql;
					PreparedStatement ps;
					sql = "INSERT INTO category(category_id, name)	VALUES (?, ?);";
					ps = con.prepareStatement(sql);
					ps.setInt(1, cat.getOwnId());
					ps.setString(2, cat.getName());
					ps.executeUpdate();
					ps.close();	
				});
			}catch (SQLException ex) {
				System.out.println("SQL in Category");
				
			} catch (Exception e) {
				System.out.println("other exception");
			} 
		}

		System.out.println("\033[1;34m*\033[35m*\033[33m*\033[32m* \033[91m > Sub_Category ... \033[32m*\033[33m*\033[35m*\033[34m*\033[0m");
		
		for(TheCategory cat : categoryList){
			if(cat.getParent() != 0){
				try{
					//System.out.println(itemList.size());
					JDBCTool.executeUpdateAutoCommitOn((con, st) -> {
						String sql;
						PreparedStatement ps;
						sql = "INSERT INTO sub_category(over_category_id, sub_category_id)	VALUES (?, ?);";
						ps = con.prepareStatement(sql);
						ps.setInt(1, cat.getParent());
						ps.setInt(2, cat.getOwnId());
						ps.executeUpdate();
						ps.close();	
					});
				}catch (SQLException ex) {
					System.out.println("SQL in Sub_Category");
					
				} catch (Exception e) {
					System.out.println("other exception");
				}
			}
		} 

		System.out.println("\033[1;34m*\033[35m*\033[33m*\033[32m* \033[91m > Item_Category ... \033[32m*\033[33m*\033[35m*\033[34m*\033[0m");

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
				//System.out.println("SQL");
				if(ex.getMessage().contains("constraint")){
					ErrorLogger.write("Category", item[1], ErrType.SQL_FK_ERROR, "item_id", ex, "");
				}
				
			} catch (Exception e) {
				System.out.println("other exception");
			}			
		} 

	} 


	public static void main(String[] args) throws Exception {
		
		DropTables.dropTables();
		CreateTables.createTables();

		Cat cat = new Cat();
		cat.readCat();

	}
	
	
}
