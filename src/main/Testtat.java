package main;

import entity.*;
import csv.*;


import java.sql.Date;

import java.util.List;
import java.util.Iterator;

import org.hibernate.HibernateException; 
import org.hibernate.Session; 
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.Query;



public class Testtat {


    /*TestDaten:
    Testtat test = new Testtat();

        test.getProduct(factory, "B000026NO5");
        test.getProduct(factory, "B000026N65");

        test.getProducts(factory, "Sherlock");
        test.getProducts(factory, "Sherlick");
        test.getProducts(factory, "g");

        test.addNewReview(factory, "B000026NO5", "baerchen76", "COOL", "WOW WAS THIS COOL!", 3);
        test.addNewReview(factory, "B000026NO5", "baerchen77", "COOL", "WOW WAS THIS COOL!", 2);
        test.addNewReview(factory, "B000026N65", "baerchen76", "COOL", "WOW WAS THIS COOL!", 5);

        test.getOffers(factory, "B00066KWNS");
        test.getOffers(factory, "3405156211");
        test.getOffers(factory, "B000026N65");

        test.getTopProducts(factory, 15);

        test.getSimilarCheaperProduct(factory, "6304498969");
        test.getSimilarCheaperProduct(factory, "3937825061");
        test.getSimilarCheaperProduct(factory, "B000026N65");

        test.getTrolls(factory, 2);
    
    */

    /**
     * get details to a given Item_Id
     */
    public void getProduct(SessionFactory factory, String item_id) {
        Session session = factory.openSession();
		Transaction tx = null;

        try{
            tx = session.beginTransaction();
            Item searchItem = (Item) session.get(Item.class, item_id);
            if(searchItem!=null) {
                String rating = (searchItem.getRating()==0.0) ? "not rated" : searchItem.getRating()+"";
                System.out.println( "\nItem: " + searchItem.getItem_id() + 
                                    "\nTitle: " + searchItem.getTitle() + 
                                    "\nRating: " + rating + 
                                    "\nSalesrank: " + searchItem.getSalesranking() + 
                                    "\nImage: " + searchItem.getImage() + 
                                    "\nProductgroup" + searchItem.getProductgroup() + "\n");
            } else {
                System.out.println("\nWe are sorry, but Item " + item_id + " does not exist in our Database. Please check your input and contact our Helpcenter.\n");
            }
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            System.out.println("Ooops! Something went wrong while getting the Product ... ^^' ");
        } finally {
            session.close();
        }
    }

    /**
     * get all Products that match the titlepattern
     */
    public void getProducts(SessionFactory factory, String pattern) {
        if(pattern.length() < 5){
            System.out.println("\nThe pattern has to include at least 5 characters.\n");

        } else {
            Session session = factory.openSession();
            Transaction tx = null;

            try{
                tx = session.beginTransaction();
                String queryString = "FROM Item I WHERE I.title LIKE '%" + pattern + "%'";
                List<?> productsList = session.createQuery(queryString).list();            
                if(!productsList.isEmpty()) {
                    System.out.println("\nThe following items include the pattern '" + pattern + "':");
                    for(int i=0; i<productsList.size(); i++) {
                        Item item = (Item) productsList.get(i);
                        System.out.println(item.getItem_id() + "\t" + item.getTitle());
                    }
                } else {
                    System.out.println("\nWe are sorry but it seems there are no products fitting this pattern: " + pattern);
                }     
                System.out.println();       
                tx.commit();
            }catch (HibernateException e) {
                if (tx!=null) tx.rollback();
                System.out.println("Ooops! Something went wrong while getting Products ... ^^' ");
            } finally {
                session.close();
            }
        }
    }

    /**
     * add new Reviews to the DB
     */
    public void addNewReview(SessionFactory factory, String item_id, String customer, String summary, String content, int rating) {
        Session session = factory.openSession();
		Transaction tx = null;

        try {
            tx = session.beginTransaction();

            //ensures that this is either a valid customer or an anonymous review
            Customer reviewCustomer = (Customer) session.get(Customer.class, customer);
            if(reviewCustomer==null) {  
                customer = "guest";
            } 

            Item reviewItem = (Item) session.get(Item.class, item_id);
            if(reviewItem==null) {
                System.out.println("We are sorry but we can not add your Review for item " + item_id + "\nIt seems that this item is not listet in our database. If you are sure that this is the correct item_id please contact our Helpcenter.\n");
                //Fehlermeldung weil kein existentes Item in DB
            }else{
                Date review_date = new Date(System.currentTimeMillis());
                Review review = new Review(item_id, customer, review_date, summary, content, rating);			
                session.save(review); 			
                System.out.println("New Review added\nfor " + item_id + " by " + customer + "\nrating: " + rating + "\n" + summary + "\n" + content + "\n");
            }
            tx.commit();
        } catch (Exception e) {
            if (tx!=null) {
                tx.rollback();
            }
            System.out.println("Ooops! Something went wrong while adding a new Review ... ^^' ");
        } finally {
            session.close(); 
        }
    }


    /**
     * get Offers for a given Item
     */
    public void getOffers(SessionFactory factory, String item_id) {
		Session session = factory.openSession();
		Transaction tx = null;

        try{
            tx = session.beginTransaction();
            if( ((Item) session.get(Item.class, item_id))==null){
                System.out.println("\nWe are sory but it seems that " + item_id + " is not listet in our database. Please check your input and contact our Helpcenter\n");
            } else {
                String shopQueryString = "SELECT S.shop_id, S.shop_name FROM Shop S";
                List<?> shopList = session.createQuery(shopQueryString).list();

                String queryString = "FROM Item_Shop I WHERE I.item_id = :param_item_id";
                Query query = session.createQuery(queryString);
                query.setParameter("param_item_id", item_id);
                List offerList = query.list();

                String shopName = "";
                for(Iterator iterator = offerList.iterator(); iterator.hasNext();) {
                    Item_Shop is = (Item_Shop) iterator.next();
                    if(is.getAvailabiliti()==true){
                        for(int i=0; i<shopList.size(); i++) {
                            Object[] row = (Object[]) shopList.get(i);
                            if((Integer) row[0] == is.getShop_id()) {
                                shopName = (String) row[1];
                            }
                        }
                        System.out.println("You can find " + is.getItem_id() + " in our Shop in " + shopName + " for " + is.getPrice() + is.getCurrency() + " in " + is.getCondition() + " condition\n");
                    }
                }
                if(shopName.equals("")){
                    System.out.println("We are sorry but it seems there are currently no offers for " + item_id + " in our shops\n");
                }
            }
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            System.out.println("Ooops! Something went wrong while getting offers for this Product ... ^^' ");
        } finally {
            session.close();
        }
    }


    /**
     * returns the topProducts in the DB, ordered by rating and salesranking, limited to limit
     */
    public void getTopProducts(SessionFactory factory, int limit) {
		Session session = factory.openSession();
		Transaction tx = null;

        try{
            tx = session.beginTransaction();
			String topProducsQueryString = "SELECT I.item_id, I.title, I.rating, I.salesranking FROM Item I WHERE I.salesranking<>0 ORDER BY I.rating DESC, I.salesranking ASC";
			Query topProductQuery = session.createQuery(topProducsQueryString);
            topProductQuery.setMaxResults(limit);            
            List<?> topProductList = topProductQuery.list();

            System.out.println("Our TOP" + limit + " Products are:\nItemID\t\tTitle\t\t\t\t\t\tRating\tSalesrank");
			for(int i=0; i<topProductList.size(); i++) {
                Object[] row = (Object[]) topProductList.get(i);
                System.out.println((String) row[0] + "\t" + ((String) row[1] + "                                        ").substring(0,40) + "\t" + (Double) row[2] + "\t" +  (Integer) row[3]);
            }
            System.out.println();
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            System.out.println("Ooops! Something went wrong while getting our TopProducts ... ^^' ");
        } finally {
            session.close();
        }
    }

    /**
     * checks if any of the similar Products are cheaper than the one checked for
     */
    public void getSimilarCheaperProduct(SessionFactory factory, String item_id) {
        Session session = factory.openSession();
		Transaction tx = null;

        try{
            tx = session.beginTransaction();
            if(((Item) session.get(Item.class, item_id))==null) {
                System.out.println("\nWe are sorry, but Item " + item_id + " does not exist in our Database. Please check your input and contact our Helpcenter.\n");
            } else {
                Boolean cheaperExists = false;
                System.out.println(); 

                //get Price of original item
                String priceQuery = "SELECT MIN(O.price) FROM Item_Shop O WHERE O.item_id='" + item_id + "'";  
                Double priceOrigin = (Double) session.createQuery(priceQuery).uniqueResult();

                //get all similar items
                String simQuery = "SELECT I.sim_items FROM Item I WHERE I.item_id='" + item_id + "'";  
                List<?> simList = session.createQuery(simQuery).list();
                for(int i=0; i<simList.size(); i++) {    
                    Item simItem = (Item) simList.get(i);
                    String cheapPriceQuery = "SELECT B.price FROM Item_Shop B WHERE B.item_id = '" + simItem.getItem_id() + "'";
                    Double priceSim = (Double) session.createQuery(cheapPriceQuery).uniqueResult();   
                    if(priceSim<priceOrigin && priceSim!=0){ 
                        cheaperExists = true;
                        System.out.println("There is a cheaper option for " + item_id + ": " + simItem.getItem_id() + " for " + priceSim); 
                    }            
                }
                if(!cheaperExists) {
                    System.out.println("We are sorry, it seems that there is no cheaper option for: " + item_id);
                }   
                System.out.println();
            }
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            System.out.println("Ooops! Something went wrong while getting the Product ... ^^' ");
        } finally {
            session.close();
        }
    }


    /**
     * get all reviewers where average rating is below limit
     */
    public void getTrolls(SessionFactory factory, int limit) {
		Session session = factory.openSession();
		Transaction tx = null;

        try{
            tx = session.beginTransaction();
			String trollQueryString = "SELECT R.customer_name, AVG(R.rating) AS average FROM Review R GROUP BY R.customer_name ORDER BY average";        
            List<?> trollList = session.createQuery(trollQueryString).list();

            System.out.println("Troll\t\t\t\tAverage Rating");
			for(int i=0; i<trollList.size(); i++) {
                Object[] row = (Object[]) trollList.get(i);
                if( (((Double) row[1])<limit)  &&  (!((String) row[0]).equals("guest")) ){
                    System.out.println(((String) row[0] + "                              ").substring(0,30) + "\t" + (Double) row[1]);
                }
            }
            System.out.println();
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            System.out.println("Ooops! Something went wrong while getting trolls ... ^^' ");
        } finally {
            session.close();
        }
    }

}

