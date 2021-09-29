/**
 * Class to read Shops with their attributes from file
 * and insert them into DB table shop
 * @version 21-09-23
 */
package entity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Iterator;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.hibernate.HibernateException; 
import org.hibernate.Session; 
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.Query;

//import JDBCTools.JDBCTool;
import XmlTools.XmlTool;
import main.Config;
import main.ErrType;
import main.ErrorLogger;


public class ManageShop {

	/**
	 * method managing insertion of shops into DB
	 */
    public void manageShops(SessionFactory factory) {
       insertShop(readShop(Config.DRESDEN_ENCODED), factory);
       insertShop(readShop(Config.LEIPZIG), factory);
       System.out.println("\033[1;34m*\033[35m*\033[33m*\033[32m*\033[91mShops finished \033[32m*\033[33m*\033[35m*\033[34m*\033[0m");
    }
	
		
	/**
	 * reads shop names from given file
	 * and adds them as attributes to the class
	 */
	public Shop readShop(String xmlPath) {

		XmlTool xt = new XmlTool();
		Node shopNode = null;
        Shop shop = new Shop();
		try {			
			xt.loadXML(xmlPath);
			shopNode = xt.getDocumentNode().getElementsByTagName("shop").item(0);
			Element el = (Element)shopNode;
            shop = new Shop(el.getAttribute("name"), el.getAttribute("street"), el.getAttribute("zip"));
		} catch (Exception e) {
			System.out.println("Exception for " + shop.getShop_name());
			if(shopNode != null) {
				ErrorLogger.write("Shop.read", "", ErrType.XML, "", e, xt.getAllAttributeContents(shopNode)+": at "+ xmlPath);
			}
		}finally{
            return shop;
        }
	}
	

	/**
	 * writes shops with their attributes to the DB table shop
	 */
	public void insertShop(Shop shop, SessionFactory factory) {
		Session session = factory.openSession();
		Transaction tx = null;
		
		try {
			tx = session.beginTransaction();
			session.save(shop); 
			tx.commit();
		} catch (HibernateException e) {
			if (tx!=null) {
                tx.rollback();
            }
			System.out.println("HibernateException for " + shop.getShop_name()); 
		} finally {
			session.close(); 
		}
	}
	
	/**
	 * writes all shops with their attributes from DB into the terminal
	 */
	/*public void selectShop() {
		System.out.println("=== Select shop result === ");
		try {
			JDBCTool.executeUpdate((con, st) -> {
				ResultSet rs = st.executeQuery("SELECT * FROM SHOP");
				while(rs.next()) {
					System.out.print(rs.getInt(1) + "\t");
					System.out.print(rs.getString(2) + "\t");
					System.out.print(rs.getString(3) + "\t"); 
					System.out.println(rs.getString(4));
				}
				
			});
		} catch (Exception e) {
			//do nothing
		}
		System.out.println();
	}*/
	


	
	/**
	 * get shopId from DB with name and address
	 * @param shop_name
	 * @param street
	 * @param zip
	 * @return shopId as int
	 */
	public int getShopId(SessionFactory factory, String shop_name, String street, String zip) {
		int shopId = 0;
		Session session = factory.openSession();
		Transaction tx = null;		
		
		try {
			tx = session.beginTransaction();
			Query q = session.createQuery("FROM Shop S WHERE S.zip = :zip AND S.street = :street AND S.shop_name = :shop_name"); 
			q.setParameter("zip", zip);
			q.setParameter("street", street);
			q.setParameter("shop_name", shop_name);
			List shops = q.list();
			shopId = ((Shop) shops.get(0)).getShop_id();
			tx.commit();
		} catch (HibernateException e) {
			if (tx!=null) tx.rollback();
			//e.printStackTrace(); 
		} catch (Exception e) {
		} finally {
			session.close(); 
			return shopId;
		}
	}

}
