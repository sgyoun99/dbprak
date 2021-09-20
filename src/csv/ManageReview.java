/**
 * read the reviews from file and write in Table Reviews in DB
 * @version 21-06-02
 */
package csv;

import entity.Item;


import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Date;

import java.util.HashMap;
import java.util.List;

import org.hibernate.HibernateException; 
import org.hibernate.Session; 
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.Query;

//import JDBCTools.JDBCTool;
import main.ErrorLogger;
import main.ErrType;



public class ManageReview{

    private CSV csvFile;
    private HashMap<String, Double> ratingHM = new HashMap<>();;

    /**
     * function managing reading in csv file and writing reviews to DB
     */
    public void manageReviews(SessionFactory factory) {
        this.csvFile = new CSV();
        this.csvFile.readFile();
        insertCustomers(factory);
        System.out.println("\033[1;34m*\033[35m*\033[33m*\033[32m* \033[91m Customers finished \033[32m*\033[33m*\033[35m*\033[34m*\033[0m");
        insertReviews(factory);
        System.out.println("\033[1;34m*\033[35m*\033[33m*\033[32m* \033[91m reviews fully written \033[32m*\033[33m*\033[35m*\033[34m*\033[0m");
        addRatings(factory);
        System.out.println("\033[1;34m*\033[35m*\033[33m*\033[32m* \033[91m ratings added \033[32m*\033[33m*\033[35m*\033[34m*\033[0m");
    }


    /**
     * add all customers in reviews
     * dummy Addresses/Accounts
     */
    public void insertCustomers(SessionFactory factory) {
        for(int i=0; i<this.csvFile.getFile().size(); i++) {
            Session session = factory.openSession();
            Transaction tx = null;
            
            try {
                tx = session.beginTransaction();
                Customer customer = new Customer(csvFile.getFile().get(i)[4], "Street"+i, i, 11111, i+"city", "Account"+i);			
//              session.save(customer); 			
                session.saveOrUpdate(customer); 			
                
                tx.commit();
            } catch (HibernateException e) {
                if (tx!=null) {
                    tx.rollback();
                }
                System.out.println("HibernateException for adding Customer " + i); 
            } finally {
                session.close(); 
            }
        }

    }




    /**
	 * insert reviews into DB
	 * ACHTUNG: nicht in batches weil sonst kompletter batch rejected wenn einer FKconstraint failure
	 */
	public void insertReviews(SessionFactory factory) {
        for(int i=0; i<this.csvFile.getFile().size(); i++) {
            Session session = factory.openSession();
            Transaction tx = null;
            
            try {
                tx = session.beginTransaction();
                //Customer reviewCustomer = (Customer) session.get(Customer.class, csvFile.getFile().get(i)[4]);
                Review review = new Review(csvFile.getFile().get(i)[0], csvFile.getFile().get(i)[4], Date.valueOf(csvFile.getFile().get(i)[3]), csvFile.getFile().get(i)[5], csvFile.getFile().get(i)[6], Integer.valueOf(csvFile.getFile().get(i)[1]));			
                session.save(review); 			
                tx.commit();
//            } catch (HibernateException e) {
                //TODO change HibernateExceptions to ???
            } catch (Exception e) {
                if (tx!=null) {
                    tx.rollback();
                }
                System.out.println("HibernateException for adding Review " + i); 
            } finally {
                session.close(); 
            }
        }
	}

    
    /**
     * Superfunction for getting ratings from the DB table review and adding them to items
     */
    public void addRatings(SessionFactory factory){
        getRating(factory);
        setRating(factory);
    }

    /**
     * Function to get teh averages of the ratings from the DB (review) and add them to the Review HashMap
     */
    private void getRating(SessionFactory factory) {
        Session session = factory.openSession();
		Transaction tx = null;

        try{
            tx = session.beginTransaction();
            String queryString = "Select R.item_id, AVG(R.rating) FROM Review R GROUP BY R.item_id";
            List<?> ratingList = session.createQuery(queryString).list();
            for(int i=0; i<ratingList.size(); i++) {
                Object[] row = (Object[]) ratingList.get(i);
                ratingHM.put((String) row[0], (Double) row[1]);
            }
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            System.out.println("HibernateException for getting Ratings");
            e.printStackTrace();
        } finally {
            session.close();
        }
    }


    
    /**
     * add Ratings from Review HashMap to DB item
     */
    private void setRating(SessionFactory factory){
        for(HashMap.Entry<String,Double> set : ratingHM.entrySet()){
            Session session = factory.openSession();
            Transaction tx = null;
            
                try {
                    tx = session.beginTransaction();
                    Item item = (Item)session.get(Item.class, set.getKey()); 
                    item.setRating(set.getValue());
                    session.update(item); 
                    tx.commit();
                } catch (HibernateException e) {
                    if (tx!=null) tx.rollback();
                    e.printStackTrace(); 
                } finally {
                    session.close(); 
                }
        }
    }  


}