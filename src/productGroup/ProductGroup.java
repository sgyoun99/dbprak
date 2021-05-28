package productGroup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import JDBCTools.JDBCTool;
import XmlTools.XmlTool;
import dresden.Shop;

public class ProductGroup {
	Connection con;

	String shop_name;
	String street;
	String zip;
	
	public ProductGroup() {
		this.con = JDBCTool.getConnection();
	}
	
	public void getAllProductgroup() {
		XmlTool xt = new XmlTool();
		xt.loadXML("./data/dresden.xml__to__UTF-8.xml");
		
		Document doc = xt.getDocument();
		NodeList nl = doc.getElementsByTagName("shop");
		Element el = (Element) nl.item(0);
		shop_name = el.getAttribute("name");
		street = el.getAttribute("street");
		zip = el.getAttribute("zip");
		
		System.out.println(shop_name);
		System.out.println(street);
		System.out.println(zip);
	
}
	
	public void insertIntoShop() {
		try {
			con.setAutoCommit (false);
			Statement st = con.createStatement();
			{ //write code in this block

				PreparedStatement ps = con.prepareStatement("INSERT INTO shop (shop_name, street, zip) VALUES (?, ?, ?)");
				ps.setString(1, shop_name);
				ps.setString(2, street);
				ps.setString(3, zip);
				ps.executeUpdate();
				ps.close();
				
				
				ResultSet rs = st.executeQuery("SELECT * FROM SHOP");
				while(rs.next()) {
					System.out.print(rs.getString(1) + "\t");
					System.out.print(rs.getString(2) + "\t"); 
					System.out.println(rs.getString(3));
				
			}

			}
			st.close();
			con.commit ();
			} catch (SQLException e) {
				System.out.println(e);
				try { 
					con.rollback (); 
				} catch (SQLException e2) {
					System.out.println(e2);
				} 
			} finally {
				try { 
					con.setAutoCommit (true); 
					con.close();
				} catch (SQLException e3) {
					System.out.println(e3);
				} 
			}
	}

	public static void main(String[] args) {

		ProductGroup pgroup = new ProductGroup();
		pgroup.insertIntoShop();

	}

}

