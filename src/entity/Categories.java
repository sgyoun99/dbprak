package entity;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.Predicate;

import org.w3c.dom.Node;

import JDBCTools.JDBCTool;
import XmlTools.XmlTool;
import exception.SQLKeyDuplicatedException;
import exception.XmlDataException;
import exception.XmlInvalidValueException;
import exception.XmlValidationFailException;
import main.Config;
import main.CreateTables;
import main.DropTables;
import main.ErrType;
import main.ErrorLogger;

public class Categories {

/*
		Node categoriesNode = xt.getDocumentNode().getFirstChild().getNextSibling();
		List<Node> mainCategoryNodesList = xt.getDirectChildElementNodes(categoriesNode); //12

		mainCategoryNodesList.forEach(n->{
			System.out.println(xt.getFirstTextNodeValue(n));
		});		
 */
	
	
	public void insertMainCategory() {
		String location = "mainCategory";
		XmlTool xt = new XmlTool();
		xt.loadXML(Config.CATEGORY_ENCODED);

		Node categoriesNode = xt.getDocumentNode().getFirstChild().getNextSibling();
		List<Node> mainCategoryNodesList = xt.getDirectChildElementNodes(categoriesNode); //12

		mainCategoryNodesList.forEach(mainCategoryNode -> {
			Category category = new Category();
			
			String catName = xt.getFirstTextNodeValue(mainCategoryNode);
//			category.setCategoryName(catName);
//			this.mainCategoryMap.put(catName, category);
			
			try {
				JDBCTool.executeUpdateAutoCommitOn((con, st) -> {
					String sql;
					PreparedStatement ps;
					sql = "INSERT INTO public.category("
							+ "	name)"
							+ "	VALUES (?);";
					ps = con.prepareStatement(sql);
					ps.setString(1, catName);
					ps.executeUpdate();
					ps.close();	
					
				});
			} catch (IllegalArgumentException e) {
				ErrorLogger.write(location, category.getCategoryName(), ErrType.PROGRAM, "" ,e, xt.getNodeContentDFS(mainCategoryNode));
			} catch (SQLException ex) {
				if(ex.getMessage().contains("duplicate key value")) {
					SQLKeyDuplicatedException e = new SQLKeyDuplicatedException();
					e.setAttrName("item_id");
					e.setItem_id(category.getCategoryName());
					e.setLocation(location);
					e.setMessage("duplicate key value");
					ErrorLogger.write(e, xt.getNodeContentDFS(mainCategoryNode));
				} else {
					ErrorLogger.write(location, category.getCategoryName(), ErrType.SQL, "mainCategoryName", ex, xt.getNodeContentDFS(mainCategoryNode));
				}
			} catch (Exception e) {
				ErrorLogger.write(location, category.getCategoryName(), ErrType.PROGRAM, "mainCategoryName", e, xt.getNodeContentDFS(mainCategoryNode));
			}
		});
	}
	
	private Map<String,Category> mainCategoryMap = new HashMap<>();
	
	public void insertSubCategory() {
		XmlTool xt = new XmlTool();
		xt.loadXML(Config.CATEGORY_ENCODED);
		Node categoriesNode = xt.getDocumentNode().getFirstChild().getNextSibling();
		List<Node> mainCategoryNodesList = xt.getDirectChildElementNodes(categoriesNode); //12

		mainCategoryNodesList.forEach(mainCategoryNode -> {
			Category category = new Category();	
			String catName = xt.getFirstTextNodeValue(mainCategoryNode);
			category.setCategoryName(catName);
			this.mainCategoryMap.put(catName, category);
			
			this.insertSubCategoryDFS(mainCategoryNode);
			
			
		});
	}
	
	class CurrentNode {
		Node currentNode;

		public Node getCurrentNode() {
			return currentNode;
		}

		public void setCurrentNode(Node currentNode) {
			this.currentNode = currentNode;
		}
		
	}
	private void insertSubCategoryDFS(Node startNode) {
		String location = "subCategory";
		XmlTool xt = new XmlTool();

			
		CurrentNode currentNode = new CurrentNode();
		currentNode.setCurrentNode(startNode);
		
		
		int level = 0;
	
		Stack<Node> dfsStack = new Stack<Node>();
		dfsStack.push(currentNode.getCurrentNode());
		dfs:while(currentNode.getCurrentNode() != null) {
			if(currentNode.getCurrentNode().getNodeType() == Node.ELEMENT_NODE  && !"item".equals(currentNode.getCurrentNode().getNodeName())) {
				//System.out.print(xt.getPrintOpeningNode(currentNode.getCurrentNode(), level));
				Category category = new Category();	
				String catName = xt.getFirstTextNodeValue(currentNode.getCurrentNode());
				category.setCategoryName(catName);
				try {
					JDBCTool.executeUpdateAutoCommitOn((con, st) -> {
						String sql;
						PreparedStatement ps;
						sql = "INSERT INTO public.category("
								+ "	name)"
								+ "	VALUES (?);";
						ps = con.prepareStatement(sql);
						ps.setString(1, catName);
						ps.executeUpdate();
						ps.close();	
						
					});
				} catch (IllegalArgumentException e) {
					ErrorLogger.write(location, category.getCategoryName(), ErrType.PROGRAM, "" ,e, xt.getNodeContentDFS(currentNode.getCurrentNode()));
				} catch (SQLException ex) {
					if(ex.getMessage().contains("duplicate key value")) {
						SQLKeyDuplicatedException e = new SQLKeyDuplicatedException();
						e.setAttrName("item_id");
						e.setItem_id(category.getCategoryName());
						e.setLocation(location);
						e.setMessage("duplicate key value");
						ErrorLogger.write(e, xt.getNodeContentDFS(currentNode.getCurrentNode()));
					} else {
						ErrorLogger.write(location, category.getCategoryName(), ErrType.SQL, "mainCategoryName", ex, xt.getNodeContentDFS(currentNode.getCurrentNode()));
					}
				} catch (Exception e) {
					ErrorLogger.write(location, category.getCategoryName(), ErrType.PROGRAM, "mainCategoryName", e, xt.getNodeContentDFS(currentNode.getCurrentNode()));
				}
			}
			
			if(currentNode.getCurrentNode().hasChildNodes()) {
				dfsStack.push(currentNode.getCurrentNode());
				level++;
				currentNode.setCurrentNode(currentNode.getCurrentNode().getFirstChild());
			} else if(currentNode.getCurrentNode().getNextSibling() != null) {
				currentNode.setCurrentNode(currentNode.getCurrentNode().getNextSibling());
			} else {
				while(currentNode.getCurrentNode().getNextSibling() == null) {
					try{
						currentNode.setCurrentNode(dfsStack.pop());
						level--;
						if(!xt.isLeafElementNode(currentNode.getCurrentNode())) {
							//System.out.print(xt.getPrintClosingNode(currentNode.getCurrentNode(), level));
						}
						if(currentNode.getCurrentNode().isSameNode(startNode)) { break dfs; }
							
					} catch (EmptyStackException e) {
						e.printStackTrace();
						break;
					}
				}
				currentNode.setCurrentNode(currentNode.getCurrentNode().getNextSibling());
				
			}
		} 
	}
	
	public void insertCategory_SubCategory() {
		
	}
	
	
	public static Predicate<String> pred_categoryName = name -> name != null && name.length() > 0; //not null
	public void test(String categoryName) throws XmlInvalidValueException{
		if(!pred_categoryName.test(categoryName)) {
			XmlInvalidValueException e = new XmlInvalidValueException("category name is null or length is 0"); 
			e.setAttrName("catogoryName");
			throw e;
		}
	}
	
	
	
	public static void main(String[] args) throws Exception {
		
		DropTables.dropTable(CreateTables.Item_Category);
		DropTables.dropTable(CreateTables.Sub_Category);
		DropTables.dropTable(CreateTables.Category);
		CreateTables.createTable(CreateTables.Category);
		CreateTables.createTable(CreateTables.Sub_Category);
		CreateTables.createTable(CreateTables.Item_Category);
		
		Categories categories = new Categories();
		categories.insertMainCategory();
		categories.insertSubCategory();

	}
}
