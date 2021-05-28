package entity;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import JDBCTools.JDBCTool;
import JDBCTools.SQLExecutable;
import XmlTools.XmlTool;
import XmlTools.XmlToolWorkable;

public class Shop {
	
	private String shop_name;
	private String street;
	private String zip;
	private String xmlPath;

	public Shop(String xmlPath) {
		this.xmlPath = xmlPath;
	}
	public void readShop() {

		XmlTool xt = new XmlTool();
		xt.loadXML(this.xmlPath);
		xt.visitAllElementNodesDFS(new XmlToolWorkable() {
			
			@Override
			public void work(Node node, int level, XmlTool xmlTool) {
				Element el = (Element)node;
				if(el.getNodeName().equals("shop")) {
						shop_name = el.getAttribute("name");
						street = el.getAttribute("street");
						zip = el.getAttribute("zip");
				}
			}
		});
		System.out.println("read shop complete.");
	}

	public void insertShop() {
		try {
			JDBCTool.executeUpdate(new SQLExecutable() {
				
				@Override
				public void work(Connection con, Statement st) throws SQLException {

					PreparedStatement ps = con.prepareStatement("INSERT INTO shop (shop_name, street, zip) VALUES (?, ?, ?)");
					ps.setString(1, shop_name);
					ps.setString(2, street);
					ps.setString(3, zip);
					ps.executeUpdate();
					ps.close();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("insert shop complete.");
	}
	
	public void selectShop() {
		System.out.println("=== Select shop result === ");
		try {
			JDBCTool.executeUpdate(new SQLExecutable() {
				
				@Override
				public void work(Connection con, Statement st) throws SQLException {
					ResultSet rs = st.executeQuery("SELECT * FROM SHOP");
					while(rs.next()) {
						System.out.print(rs.getString(1) + "\t");
						System.out.print(rs.getString(2) + "\t"); 
						System.out.println(rs.getString(3));
					}
					
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}




}
