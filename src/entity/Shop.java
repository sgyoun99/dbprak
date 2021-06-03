package entity;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import JDBCTools.JDBCTool;
import XmlTools.XmlTool;
import main.ErrType;
import main.ErrorLogger;

public class Shop {
	
	private String shop_name;
	private String street;
	private String zip;
	private String xmlPath;
	
	

	public String getShop_name() {
		return shop_name;
	}
	public String getStreet() {
		return street;
	}
	public String getZip() {
		return zip;
	}

	public Shop(String xmlPath) {
		this.xmlPath = xmlPath;
	}
	public void readShop() {

		XmlTool xt = new XmlTool();
		Node shopNode = null;
		try {
			
			xt.loadXML(this.xmlPath);
			shopNode = xt.getDocumentNode().getElementsByTagName("shop").item(0);
			Element el = (Element)shopNode;

			shop_name = el.getAttribute("name");
			street = el.getAttribute("street");
			zip = el.getAttribute("zip");
		} catch (Exception e) {
//			System.out.println(e);
			e.printStackTrace();
			if(shopNode != null) {
				ErrorLogger.write("Shop.read", "", ErrType.XML, "", e, xt.getAllAttributeContents(shopNode)+": at "+ xmlPath);
			}
		}
	}

	public void insertShop() {
		try {
			JDBCTool.executeUpdate((con, st) -> {

					PreparedStatement ps = con.prepareStatement("INSERT INTO shop (shop_name, street, zip) VALUES (?, ?, ?)");
					ps.setString(1, shop_name);
					ps.setString(2, street);
					ps.setString(3, zip);
					ps.executeUpdate();
					ps.close();
				}
			);
		} catch (Exception e) {
			ErrorLogger.write("Shop.insert", "", ErrType.SQL, "", e, "Shop Insert filed: "+xmlPath);
		}
		System.out.println("insert shop complete.");
	}
	
	public void selectShop() {
		System.out.println("=== Select shop result === ");
		try {
			JDBCTool.executeUpdate((con, st) -> {
					ResultSet rs = st.executeQuery("SELECT * FROM SHOP");
					while(rs.next()) {
						System.out.print(rs.getString(1) + "\t");
						System.out.print(rs.getString(2) + "\t"); 
						System.out.println(rs.getString(3));
					}
					
				}
			);
		} catch (Exception e) {
			//do nothing
		}
		System.out.println();
	}

}
