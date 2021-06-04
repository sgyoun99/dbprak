package entity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import exception.XmlInvalidValueException;
import main.Config;
import main.CreateTables;
import main.DropTables;
import main.ErrType;
import main.ErrorLogger;

public class Categories {

	
	public void insertMainCategory() {
		String location = "mainCategory";
		XmlTool xt = new XmlTool();
		xt.loadXML(Config.CATEGORY_ENCODED);

		Node categoriesNode = xt.getDocumentNode().getFirstChild().getNextSibling();
		List<Node> mainCategoryNodesList = xt.getDirectChildElementNodes(categoriesNode); //12

		mainCategoryNodesList.forEach(mainCategoryNode -> {
			Category category = new Category();
			
			String catName = xt.getFirstTextNodeValue(mainCategoryNode);
			
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
	
	
	private void insertSubCategoryDFS(Node startNode) {
		class CurrentNode {
			private Node currentNode;

			public Node getCurrentNode() {
				return currentNode;
			}

			public void setCurrentNode(Node currentNode) {
				this.currentNode = currentNode;
			}
			
		}
		String location = "subCategory";
		XmlTool xt = new XmlTool();

			
		CurrentNode currentNode = new CurrentNode();
		currentNode.setCurrentNode(startNode);
		
		
		//int level = 0;
	
		Stack<Node> dfsStack = new Stack<Node>();
		dfsStack.push(currentNode.getCurrentNode());
		dfs:while(currentNode.getCurrentNode() != null) {

			if(currentNode.getCurrentNode().getNodeType() == Node.ELEMENT_NODE  && !"item".equals(currentNode.getCurrentNode().getNodeName())) {
				//System.out.print(xt.getPrintOpeningNode(currentNode.getCurrentNode(), level));
				Node curNode = currentNode.getCurrentNode();
				String parentCategory = xt.getFirstTextNodeValue(curNode.getParentNode());
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
					//sub_category
					int parentCategoryId = this.getCategory_id(parentCategory);
					int currentCategoryId = this.getCategory_id(xt.getFirstTextNodeValue(curNode));
					JDBCTool.executeUpdateAutoCommitOn((con, st) -> {
						String sql;
						PreparedStatement ps;
						sql = "INSERT INTO public.sub_category("
								+ "	main_category_id, sub_category_id)"
								+ "	VALUES (?, ?);";
						ps = con.prepareStatement(sql);
						ps.setInt(1, parentCategoryId);
						ps.setInt(2, currentCategoryId);
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
			
			// item_category
			if(currentNode.getCurrentNode().getNodeType() == Node.ELEMENT_NODE  && "item".equals(currentNode.getCurrentNode().getNodeName())) {
				Node categoryNode = currentNode.getCurrentNode().getParentNode();
				String categoryName = xt.getFirstTextNodeValue(categoryNode);
				int categoryId = this.getCategory_id(categoryName);

				List<Node> itemNodeList = xt.getNodesByNameDFS(currentNode.getCurrentNode(), "item");
				itemNodeList.forEach(itemNode -> {
					String item_id = xt.getTextContentOfLeafNode(itemNode);
					
					try {
						JDBCTool.executeUpdateAutoCommitOn((con, st) -> {
							String sql;
							PreparedStatement ps;
							sql = "INSERT INTO public.item_category("
									+ "	item_id, category_id)"
									+ "	VALUES (?, ?);";
							ps = con.prepareStatement(sql);
							ps.setString(1, item_id);
							ps.setInt(2,categoryId);
							ps.executeUpdate();
							ps.close();	
							
						});
					} catch (IllegalArgumentException e) {
						ErrorLogger.write(location, item_id, ErrType.PROGRAM, "item_category" ,e, xt.getNodeContentDFS(currentNode.getCurrentNode()));
					} catch (SQLException ex) {
						if(ex.getMessage().contains("duplicate key value")) {
							SQLKeyDuplicatedException e = new SQLKeyDuplicatedException();
							e.setAttrName("item_id");
							e.setItem_id(item_id);
							e.setLocation(location);
							e.setMessage("duplicate key value");
							ErrorLogger.write(e, xt.getNodeContentDFS(currentNode.getCurrentNode()));
						} else {
							ErrorLogger.write(location, item_id, ErrType.SQL, "item_category", ex, xt.getNodeContentDFS(currentNode.getCurrentNode()));
						}
					} catch (Exception e) {
						ErrorLogger.write(location, item_id, ErrType.PROGRAM, "item_category", e, xt.getNodeContentDFS(currentNode.getCurrentNode()));
					}
					
				});
			}
			
			if(currentNode.getCurrentNode().hasChildNodes()) {
				dfsStack.push(currentNode.getCurrentNode());
				//level++;
				currentNode.setCurrentNode(currentNode.getCurrentNode().getFirstChild());
			} else if(currentNode.getCurrentNode().getNextSibling() != null) {
				currentNode.setCurrentNode(currentNode.getCurrentNode().getNextSibling());
			} else {
				while(currentNode.getCurrentNode().getNextSibling() == null) {
					try{
						currentNode.setCurrentNode(dfsStack.pop());
						//level--;
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
	
	
	@SuppressWarnings("finally")
	private Integer getCategory_id(String categoryName) {
		Category category = new Category();
		String location = "getCategory_id";
		try {
			JDBCTool.executeUpdateAutoCommitOn((con, st) -> {
				String sql;
				PreparedStatement ps;
				sql = "SELECT category_id FROM public.category\n"
						+ "where name = (?)";
				ps = con.prepareStatement(sql);
				ps.setString(1, categoryName);
				ResultSet rs = ps.executeQuery();
				
				while(rs.next()){
					category.setCategory_id(rs.getInt(1));
				}
				ps.close();	
			});
		} catch (SQLException ex) {
			ErrorLogger.write(location, categoryName, ErrType.SQL, "CategoryId", ex, categoryName+" is not in the table category.");
		} catch (Exception e) {
			ErrorLogger.write(location, categoryName, ErrType.PROGRAM, "CategoryId", e, categoryName+" is not in the table category.");
		} finally {
			return category.getCategory_id();
		}
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
		
		DropTables.dropTable("Errors");
		CreateTables.createTable("Errors");

		DropTables.dropTable(CreateTables.Item_Category);
		DropTables.dropTable(CreateTables.Sub_Category);
		DropTables.dropTable(CreateTables.Category);
		CreateTables.createTable(CreateTables.Category);
		CreateTables.createTable(CreateTables.Sub_Category);
		CreateTables.createTable(CreateTables.Item_Category);
		
		Categories categories = new Categories();
		categories.insertMainCategory();
		categories.insertSubCategory();

		System.out.println("finished");
	}
}
