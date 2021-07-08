/**
 * Class needed to read similar_item-data from file 
 * and write to DB table "similar_items"
 * @version 03.06.2021
 */
package entity;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.w3c.dom.Node;

import JDBCTools.JDBCTool;
import XmlTools.XmlTool;
import exception.XmlInvalidValueException;
import exception.XmlNoAttributeException;
import exception.XmlValidationFailException;
import main.Config;
import main.CreateTables;
import main.DropTables;
import main.ErrType;
import main.ErrorLogger;


public class Similar_Items {
	String item_id;
	String sim_item_id;
	public String getItem_id() {
		return item_id;
	}
	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}
	public String getSim_item_id() {
		return sim_item_id;
	}
	public void setSim_item_id(String sim_item_id) {
		this.sim_item_id = sim_item_id;
	}
	
	//check for valid length of item_id; not currently in use
	public void test() throws XmlValidationFailException{
		try {
			if(!Item.pred_item_id.test(getItem_id())) {throw new XmlInvalidValueException("item_id Error (length not 10): "+getItem_id()); }
			if(!Item.pred_item_id.test(getSim_item_id())) {throw new XmlInvalidValueException("similar_item_id Error (length not 10): "+getSim_item_id()); }
		} catch (XmlInvalidValueException e) {
			throw new XmlValidationFailException(e);
		}
	}
	
	/**
	 * read data about similar_items from file dresden.xml and write to DB tabel "similar_items"
	 * extra method needed because of differences in file structure
	 */
	public void dresden() {
		String location = "Similar_Items(Dresden)";
		System.out.println(">> Similar_Items Dresden ...");
		XmlTool xt = new XmlTool(Config.DRESDEN_ENCODED);
		xt.filterElementNodesDFS(xt.getDocumentNode(), node -> {
			return node.getNodeName().equals("similars") && !xt.isLeafElementNode(node);
		}
		).forEach(similars -> xt.visitChildElementNodesDFS(similars, (node, level) -> {
			try {
				
			//xml data
				setItem_id("");
				setSim_item_id("");
				if(node.getNodeName().equals("item")) {
					Node parentItemNode = similars.getParentNode();
					String item_id = "";
					item_id = xt.getAttributeValue(parentItemNode, "asin");
					setItem_id(item_id);

					String sim_item_id = "";
					sim_item_id = xt.getAttributeValue(node, "asin");
					setSim_item_id(sim_item_id);
				}

				if(!getItem_id().equals("") && !getSim_item_id().equals("")) {
			//test
					//test();
					
			//insert into DB
					JDBCTool.executeUpdate((con, st) ->	{
						String sql = "INSERT INTO SIMILAR_ITEMS "
								+ "(item_id, similar_item_id) "
								+ "values (?,?)";
						PreparedStatement ps = con.prepareStatement(sql);
						ps.setString(1, getItem_id());
						ps.setString(2, getSim_item_id());
						ps.executeUpdate();
						ps.close();
						
					});
				}
				
			} catch (IllegalArgumentException e) {
				ErrorLogger.write(location, ErrType.PROGRAM , e, xt.getNodeContentDFS(node));
			} catch (XmlNoAttributeException e) {
				e.setLocation(location);
				ErrorLogger.write(e, xt.getNodeContentDFS(node));
			/*} catch (XmlValidationFailException e) {
				e.setLocation(location);
				ErrorLogger.write(e, xt.getNodeContentDFS(node));*/
			} catch (SQLException e) {
				if(!e.getMessage().contains("duplicate key value")) {
					ErrorLogger.write(location, ErrType.SQL, e, xt.getNodeContentDFS(node));
				}
			} catch (Exception e) {
				ErrorLogger.write(location, ErrType.PROGRAM, e, xt.getNodeContentDFS(node));
			}	

		}));
	}

	/**
	 * read data about similar_items from file leipzig.xml and write to DB tabel "similar_items"
	 * extra method needed because of differences in file structure
	 */
	public void leipzig() {
		String location = "Similar_Items(Leipzig)";
		System.out.println(">> Similar_Items Leipzig ...");
		XmlTool xt = new XmlTool(Config.LEIPZIG);
		xt.filterElementNodesDFS(xt.getDocumentNode(), node -> {
			return node.getNodeName().equals("similars") && !xt.isLeafElementNode(node);
		}
		).forEach(similars -> xt.visitChildElementNodesDFS(similars, (node, level) -> {
			try {
				
			//xml data
				setItem_id("");
				setSim_item_id("");
				if(node.getNodeName().equals("asin")) {
					Node parentItemNode = similars.getParentNode();
					String item_id = "";
					item_id = xt.getAttributeValue(parentItemNode, "asin");
					setItem_id(item_id);

					String sim_item_id = "";
					sim_item_id = xt.getTextContentOfLeafNode(node);
					setSim_item_id(sim_item_id);
				}

				if(!getItem_id().equals("") && !getSim_item_id().equals("")) {
			//test
					//test();
					
			//insert
					JDBCTool.executeUpdate((con, st) ->	{
						String sql = "INSERT INTO SIMILAR_ITEMS "
								+ "(item_id, similar_item_id) "
								+ "values (?,?)";
						PreparedStatement ps = con.prepareStatement(sql);
						ps.setString(1, getItem_id());
						ps.setString(2, getSim_item_id());
						ps.executeUpdate();
						ps.close();
						
					});
				}
				
			} catch (IllegalArgumentException e) {
				ErrorLogger.write(location, ErrType.PROGRAM , e, xt.getNodeContentDFS(node));
			} catch (XmlNoAttributeException e) {
				e.setLocation(location);
				ErrorLogger.write(e, xt.getNodeContentDFS(node));
			/*} catch (XmlValidationFailException e) {
				e.setLocation(location);
				ErrorLogger.write(e, xt.getNodeContentDFS(node));*/
			} catch (SQLException e) {
				if(!e.getMessage().contains("duplicate key value")) {
					ErrorLogger.write(location, ErrType.SQL, e, xt.getNodeContentDFS(node));
				}
			} catch (Exception e) {
				ErrorLogger.write(location, ErrType.PROGRAM, e, xt.getNodeContentDFS(node));
			}	

		}));
	}
	
	//not used for main-program
	public static void main(String[] args) throws Exception {
		DropTables.dropTable("Errors");
		CreateTables.createTable("Errors");
		DropTables.dropTable("Similar_Items");
		CreateTables.createTable("Similar_Items");

		Similar_Items si = new Similar_Items();
		si.dresden();
		si.leipzig();
	}
}
