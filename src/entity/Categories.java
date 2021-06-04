package entity;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Predicate;

import org.w3c.dom.Node;

import JDBCTools.JDBCTool;
import XmlTools.XmlTool;
import exception.SQLKeyDuplicatedException;
import exception.XmlDataException;
import exception.XmlInvalidValueException;
import exception.XmlValidationFailException;
import main.Config;
import main.ErrType;
import main.ErrorLogger;

public class Categories {

	
	
	public static Predicate<String> pred_categoryName = name -> name != null && name.length() > 0; //not null
	public void test(String categoryName) throws XmlInvalidValueException{
		if(!pred_categoryName.test(categoryName)) {
			XmlInvalidValueException e = new XmlInvalidValueException("category name is null or length is 0"); 
			e.setAttrName("catogoryName");
			throw e;
		}
	}
	
	
	public void insertMainCategories() {
		String location = "mainCategorys";
		XmlTool xt = new XmlTool();
		xt.loadXML(Config.CATEGORY);
		
		List<Node> mainCategories = xt.getDirectChildElementNodes(xt.getDocumentNode());
		mainCategories.forEach(mainCategory -> {
			Category category = new Category();
			try {
				//XML
				
				
				
				
				//test
				this.test(category.getCategoryName());

				//insert
				JDBCTool.executeUpdateAutoCommitOn((con, st) -> {
					String sql;
					PreparedStatement ps;
					sql = "INSERT INTO public.category("
							+ "	name)"
							+ "	VALUES (?);";
					ps = con.prepareStatement(sql);
					ps.setString(1, category.getCategoryName());
					ps.executeUpdate();
					ps.close();	
					
				});
			} catch (IllegalArgumentException e) {
				ErrorLogger.write(location, category.getCategoryName(), ErrType.PROGRAM, "" ,e, xt.getNodeContentDFS(mainCategory));
				/*
			} catch (XmlValidationFailException e) {
				e.setLocation(location);
				e.setItem_id(category.getCategoryName());
				ErrorLogger.write(e, xt.getNodeContentDFS(mainCategory));
			} catch (XmlDataException e) {
				e.setLocation(location);
				e.setItem_id(category.getCategoryName());
				ErrorLogger.write(e, xt.getNodeContentDFS(mainCategory));
				 */
			} catch (SQLException ex) {
				if(ex.getMessage().contains("duplicate key value")) {
					SQLKeyDuplicatedException e = new SQLKeyDuplicatedException();
					e.setAttrName("item_id");
					e.setItem_id(category.getCategoryName());
					e.setLocation(location);
					e.setMessage("duplicate key value");
					ErrorLogger.write(e, xt.getNodeContentDFS(mainCategory));
				} else {
					ErrorLogger.write(location, category.getCategoryName(), ErrType.SQL, "mainCategoryName", ex, xt.getNodeContentDFS(mainCategory));
				}
			} catch (Exception e) {
				ErrorLogger.write(location, category.getCategoryName(), ErrType.PROGRAM, "mainCategoryName", e, xt.getNodeContentDFS(mainCategory));
			}
		});
	}
}
