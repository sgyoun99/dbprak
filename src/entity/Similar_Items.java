package entity;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.w3c.dom.Node;

import JDBCTools.JDBCTool;
import XmlTools.XmlDataException;
import XmlTools.XmlTool;
import main.Config;
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
	
	public boolean test() throws Exception{
		if(!Item.pred_item_id.test(getItem_id())) {throw new XmlDataException("item_id Error (length not 10): "+getItem_id()); }
		if(!Item.pred_item_id.test(getSim_item_id())) {throw new XmlDataException("similar_item_id Error (length not 10): "+getSim_item_id()); }
		return true;
	}
	
	
	public void dresden() {
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
					test();
					
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
				ErrorLogger.write("Similar_Items(dresden)", ErrType.PROGRAM , e, xt.getNodeContentDFS(node));
			} catch (XmlDataException e) {
				ErrorLogger.write("Similar_Items(dresden)", ErrType.XML, e, xt.getNodeContentDFS(node.getParentNode()));
			} catch (SQLException e) {
				ErrorLogger.write("Similar_Items(dresden))", ErrType.SQL, e, xt.getNodeContentDFS(node));
			} catch (Exception e) {
				ErrorLogger.write("Similar_Items(dresden))", ErrType.PROGRAM, e, xt.getNodeContentDFS(node));
			}	

		}));
	}

	public void leipzig() {
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
					Node parentItemNode = similars.getParentNode().getParentNode().getParentNode();
					String item_id = "";
					item_id = xt.getAttributeValue(parentItemNode, "asin");
					setItem_id(item_id);

					String sim_item_id = "";
					String asinNodeValue = xt.getTextContent(node);
					String asinAttrValue = xt.getAllAttributeContents(node);
					if(asinAttrValue.length() != 0) {
						System.out.println(xt.getNodeContentDFS(parentItemNode));
					}
					sim_item_id = xt.getAttributeValue(node, "asin");
					setSim_item_id(sim_item_id);
				}

				if(!getItem_id().equals("") && !getSim_item_id().equals("")) {
			//test
					test();
					
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
				ErrorLogger.write("Similar_Items(dresden)", ErrType.PROGRAM , e, xt.getNodeContentDFS(node));
			} catch (XmlDataException e) {
				ErrorLogger.write("Similar_Items(dresden)", ErrType.XML, e, xt.getNodeContentDFS(node.getParentNode()));
			} catch (SQLException e) {
				ErrorLogger.write("Similar_Items(dresden))", ErrType.SQL, e, xt.getNodeContentDFS(node));
			} catch (Exception e) {
				ErrorLogger.write("Similar_Items(dresden))", ErrType.PROGRAM, e, xt.getNodeContentDFS(node));
			}	

		}));
	}
	
	public static void main(String[] args) {
		Similar_Items si = new Similar_Items();
		si.dresden();
	}
}
