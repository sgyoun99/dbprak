package main;

import entity.*;
import frontend.ExecutableCommand;
import csv.*;


import java.sql.Date;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

import javax.management.InvalidAttributeValueException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import java.time.LocalDate;

import org.hibernate.HibernateException; 
import org.hibernate.Session; 
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.Query;

import main.App;

public class Testtat implements ExecutableCommand {


    /*TestDaten:
    Testtat test = new Testtat();

        test.getProduct(factory, "B000026NO5");
        test.getProduct(factory, "B000026N65");

        test.getProducts(factory, "Sherlock");
        test.getProducts(factory, "Sherlick");
        test.getProducts(factory, "g");

        test.getProductsByCategoryPath(factory, 3);
        test.getProductsByCategoryPath(factory, 0);
        test.getProductsByCategoryPath(factory, 17);

        test.addNewReview(factory, "B000026NO5", "baerchen76", "COOL", "WOW WAS THIS COOL!", 3);
        test.addNewReview(factory, "B000026NO5", "baerchen77", "COOL", "WOW WAS THIS COOL!", 2);
        test.addNewReview(factory, "B000026N65", "baerchen76", "COOL", "WOW WAS THIS COOL!", 5);

        test.getOffers(factory, "B00066KWNS");
        test.getOffers(factory, "3405156211");
        test.getOffers(factory, "B000026N65");
        test.getOffers(factory, "B00005AT2N"); // in 2 shops with different price

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
                String additionalDetails = "";
                switch(searchItem.getProductgroup()) {
                    case Book:
                        List<?> authorList = session.createQuery("SELECT B.author FROM Book A JOIN A.authors B WHERE A.item_id = '" + item_id + "'").list();
                        List<?> pubList = session.createQuery("SELECT B.publisher FROM Book A JOIN A.publishers B WHERE A.item_id = '" + item_id + "'").list();
                        Book book = (Book) session.get(Book.class, item_id);
                        for(int i=0; i<authorList.size(); i++) {
                            additionalDetails += "Author: " + authorList.get(i) + "\n";
                        }
                        for(int i=0; i<pubList.size(); i++) {
                            additionalDetails += "Publisher: " + pubList.get(i) + "\n";
                        }

                        if(book != null) {
							String pubDate;
							if(book.getPublication_date() == null) {
								pubDate = "-";
							} else {
								pubDate = book.getPublication_date().toString();
							}
							additionalDetails += "Pages: " + book.getPages() + "\nPublication Date: " + pubDate + "\nISBN: " + book.getIsbn() + "\n";
                        }
                        break;
                    case Music_CD:
                        List<?> artistList = session.createQuery("SELECT B.artist FROM Music_CD A JOIN A.artists B WHERE A.item_id = '" + item_id + "'").list();
                        String releaseDate = "-";
                        Date date =  ((Date) session.createQuery("SELECT A.release_date FROM Music_CD A WHERE A.item_id = '" + item_id + "'").uniqueResult());
                        if(date != null) {
                        	releaseDate = date.toString();
                        }
                        additionalDetails += "ReleaseDate: " + releaseDate + "\n"; 
                        List<?> titleList = session.createQuery("SELECT A.title FROM Title A WHERE A.item_id = '" + item_id + "'").list();                                               
                        for(int i=0; i<artistList.size(); i++) {
                            additionalDetails += "Artist: " + artistList.get(i) + "\n";
                        }
                        for(int i=0; i<titleList.size(); i++) {
                            additionalDetails += "Titel " + (i+1) + ": " + titleList.get(i) + "\n";
                        }                        
                        break;
                    case DVD:
                        Dvd dvd = (Dvd) session.get(Dvd.class, item_id);
                        List<?> creatorList = session.createQuery("SELECT B.creator FROM Dvd A JOIN A.creators B WHERE A.item_id = '" + item_id + "'").list();
                        List<?> actorsList = session.createQuery("SELECT B.actor FROM Dvd A JOIN A.actors B WHERE A.item_id = '" + item_id + "'").list();
                        List<?> directorList = session.createQuery("SELECT B.director FROM Dvd A JOIN A.directors B WHERE A.item_id = '" + item_id + "'").list();
                        for(int i=0; i<actorsList.size(); i++) {
                            additionalDetails += "Actor: " + actorsList.get(i) + "\n";
                        }
                        for(int i=0; i<directorList.size(); i++) {
                            additionalDetails += "Director: " + directorList.get(i) + "\n";
                        }
                        for(int i=0; i<creatorList.size(); i++) {
                            additionalDetails += "Creator: " + creatorList.get(i) + "\n";
                        }
                        additionalDetails += "Format: " + dvd.getFormat() + "\nRegiocode: " + dvd.getRegioncode() + "\nRunnigtime: " + dvd.getRunningtime() + "\n";
                        break;
                    default:
                        break;
                }
                String rating = (searchItem.getRating()==0.0) ? "not rated" : searchItem.getRating()+"";
                System.out.println( "\nItem: " + searchItem.getItem_id() + 
                                    "\nTitle: " + searchItem.getTitle() + 
                                    "\nRating: " + rating + 
                                    "\nSalesrank: " + searchItem.getSalesranking() + 
                                    "\nImage: " + searchItem.getImage() + 
                                    "\nProductgroup: " + searchItem.getProductgroup() + "\n" + additionalDetails);
            } else {
                System.out.println("\nWe are sorry, but Item " + item_id + " does not exist in our Database. Please check your input and contact our Helpcenter.\n");
            }
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            System.out.println("Ooops! Something went wrong while getting the Product ... ^^' ");
//        }catch (Exception e) {
        	e.printStackTrace();
        } finally {
            session.close();
        }
    }

    /**
     * get all Products that match the titlepattern
     */
    public void getProducts(SessionFactory factory, String pattern) {
//        if(pattern.length() < 5){
//        if(pattern.length() < 4){
//            System.out.println("\nThe pattern has to include at least 5 characters.\n");
//            System.out.println("\nThe pattern has to include at least 4 characters.\n");
    	if(false) {
    		//TODO what to check? nothing? because '' is also allowed.
        } else {
            Session session = factory.openSession();
            Transaction tx = null;
            try{
                tx = session.beginTransaction();
              //String queryString = "FROM Item I WHERE I.title LIKE '%" + pattern + "%'";
              //String queryString = "FROM Item I WHERE lower(I.title) LIKE '%" + pattern.toLowerCase() + "%'";
				String queryString = "";
                if(pattern.equals("")) {
					queryString = "FROM Item";
                } else {
					queryString = "FROM Item I WHERE lower(I.title) LIKE '" + pattern.toLowerCase() + "'";
                }
				

				//TODO update to hibernate version
                List<?> productsList = session.createQuery(queryString).list();            
                if(!productsList.isEmpty()) {
                    System.out.println("\nThe following items include the pattern '" + pattern + "':");
                    for(int i=0; i<productsList.size(); i++) {
                        Item item = (Item) productsList.get(i);
                        System.out.println(item.getItem_id() + "\t" + item.getTitle());
                    }
                    System.out.println("There are total " + productsList.size() + " result(s).");
                } else {
                    System.out.println("\nWe are sorry but it seems there are no products fitting this pattern: '" + pattern + "'.");
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
    public void addNewReview(SessionFactory factory, String item_id, String customer, String summary, String content, int rating)
		throws NoResultException {

        Session session = factory.openSession();
		Transaction tx = null;

        try {
            tx = session.beginTransaction();

            //ensures that this is either a valid customer or an anonymous review
            Customer reviewCustomer = (Customer) session.get(Customer.class, customer);
            if(reviewCustomer==null) {  
//                customer = "guest";
            	throw new NoResultException("Customer " + customer + " does not exists.");
            } 

            Item reviewItem = (Item) session.get(Item.class, item_id);
            if(reviewItem==null) {
                System.out.println("We are sorry but we can not add your Review for item " + item_id + "\nIt seems that this item is not listet in our database. If you are sure that this is the correct item_id please contact our Helpcenter.\n");
                //Fehlermeldung weil kein existentes Item in DB
            	throw new NoResultException("Product ID " + item_id + " does not exists.");
            }else{
                Date review_date = new Date(System.currentTimeMillis());
                Review review = new Review(item_id, customer, review_date, summary, content, rating);			
                session.save(review); 			
                System.out.println("New Review added\nfor " + item_id + " by " + customer + "\nrating: " + rating + "\n" + summary + "\n" + content + "\n");
            }
            tx.commit();
        } catch (NoResultException e) {
        	throw e;
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
                System.out.println("\nWe are sorry but it seems that " + item_id + " is not listet in our database. Please check your input and contact our Helpcenter\n");
            } else {
                String shopQueryString = "SELECT S.shop_id, S.shop_name FROM Shop S";
                List<?> shopList = session.createQuery(shopQueryString).list();

                String queryString = "FROM Item_Shop I WHERE I.item_id = :param_item_id AND I.availabiliti = true";
                Query query = session.createQuery(queryString);
                query.setParameter("param_item_id", item_id);
                List offerList = query.list();

                String shopName = "";
                for(Iterator iterator = offerList.iterator(); iterator.hasNext();) {
                    Item_Shop is = (Item_Shop) iterator.next();                   
                    for(int i=0; i<shopList.size(); i++) {
                        Object[] row = (Object[]) shopList.get(i);
                        if((Integer) row[0] == is.getShop_id()) {
                            shopName = (String) row[1];
                        }
                    }
                    System.out.println("You can find " + is.getItem_id() + " in our Shop in " + shopName + " for " + is.getPrice() + is.getCurrency() + " in " + is.getCondition() + " condition\n");                 
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

            System.out.println("Our TOP " + limit + " Products are:\nItemID\t\tTitle\t\t\t\t\t\tRating\tSalesrank");
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
                System.out.println(); 

                String s = "SELECT B.item_id FROM Item A JOIN A.sim_items B WHERE A.item_id = '" + item_id + "' AND " +
                            "(SELECT C.price FROM Item_Shop C WHERE C.item_id = B.item_id AND C.price <> 0.0) < " +
                            "(SELECT MIN(D.price) FROM Item_Shop D WHERE D.item_id = '" + item_id + "')";
                List<?> simList = session.createQuery(s).list();
                Boolean cheaperExists = (simList.size()>0) ? true : false;
                for(int i=0; i<simList.size(); i++) {  
                    System.out.print("There is a cheaper option for " + item_id + ": "); 
                    getOffers(factory, (String) simList.get(i));                               
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

    /**
     * get all products associated with the given category
     */
    public void getProductsByCategoryPath(SessionFactory factory) {
		Session session = factory.openSession();
		Transaction tx = null;

        try{
            tx = session.beginTransaction();
            Scanner scan = new Scanner(System.in);
            String inputString = "";
            int cat_id = 0;
            String catName = null;
            //main category
            String rootCatQuery = "SELECT DISTINCT B.category_id, B.name FROM Category A JOIN A.over_categories B WHERE B.category_id NOT IN (SELECT C.category_id FROM Category D JOIN D.sub_categories C) ORDER BY B.category_id";      
            List<?> rootCatList = session.createQuery(rootCatQuery).list();
            Map<Integer,String> catMap = new HashMap<>();

			System.out.println(" < ID >\t< Category >");
            for(int i=0; i<rootCatList.size(); i++) {
                Object[] row = (Object[]) rootCatList.get(i);
                catMap.put((Integer)row[0], (String)row[1]);
                System.out.print(String.format("-%6s\t", (Integer)row[0]));
                System.out.println((String)row[1]);
            }

            // receive input: main category id
            while(true) {
				System.out.println("Please enter category id(number).");
				System.out.print(">>");
				//cat_id = scan.nextInt();
				inputString = scan.nextLine();
				try {
					int tmpCatId = Integer.parseInt(inputString); //check int
					catName = catMap.get(tmpCatId); 
					if(catName == null) {
						//check existence
						throw new NullPointerException(" is not in the list.");
					} else {
						//success
						cat_id = tmpCatId;
						break; 
					}
				} catch (NullPointerException e) {
					System.out.println(inputString + e.getMessage());
					continue;
				} catch (Exception e) {
					System.out.println(inputString + " is invalid input.");
					continue;
				}
            }

            //sub-category
            Boolean fin = false;
            while(!fin){
                System.out.println();
                String catQueryString = "SELECT B.category_id, B.name FROM Category A Join A.sub_categories B WHERE A.category_id = " + cat_id + " ORDER BY B.category_id";
                List<?> catQList = session.createQuery(catQueryString).list();
                if(catQList.size() == 0) {
                	// reached at the lowest sub-category
                	fin = true;
                	break;
                }
                catMap = new HashMap<>();
				System.out.println(" < ID >\t< Sub Category >");
                for(int i=0; i<catQList.size(); i++) {
                    Object[] row = (Object[]) catQList.get(i);
					catMap.put((Integer)row[0], (String)row[1]);
					System.out.print(String.format("-%6s\t", (Integer)row[0]));
					System.out.println((String)row[1]);
                }
                while(true) {
					System.out.println("Press 'Y/y' to list all products under category " + cat_id + "(" + catName +")");
					System.out.println("Or enter category id for searching further categories.");
					System.out.print(">>");
					inputString = scan.nextLine();
					if("y".equals(inputString.toLowerCase())) {
						fin = true;
						break;
					} else {
						try {
							int tmpCatId = Integer.parseInt(inputString); //check int
							catName = catMap.get(tmpCatId); 
							if(catName == null) {
								//check existence
								throw new NullPointerException(" is not in the list.");
							} else {
								//success
								cat_id = tmpCatId;
								break; 
							}
						} catch (NullPointerException e) {
							System.out.println(inputString + e.getMessage());
							continue;
						} catch (Exception e) {
							System.out.println(inputString + " is invalid input.");
							continue;
						}
					}
                }
            }

            Stack<Integer> catStack = new Stack<Integer>();
            ArrayList<Integer> catList = new ArrayList<>(Arrays.asList(cat_id));
            catStack.push(cat_id);
            while(!catStack.empty()) {
                Integer catItemId = catStack.pop();
                String catQueryString = "SELECT B.category_id FROM Category A Join A.sub_categories B WHERE A.category_id = " + catItemId; 
                List<?> catQList = session.createQuery(catQueryString).list();
                for(int i=0; i<catQList.size(); i++) {
                        catList.add(((Integer) catQList.get(i)));
                        catStack.push(((Integer) catQList.get(i)));              
                }
            }            
            System.out.println("\nThe following items are associated with category: " + cat_id + "(" + catName +")");
            Boolean thereareItems = false;
            while(!catList.isEmpty()) {
                int categoryId = catList.get(0);
                catList.remove(0);
                String itemQuery = "SELECT B.name, A.item_id FROM Category B Join B.items A WHERE B.category_id = " + categoryId;
                List<?> itemList = session.createQuery(itemQuery).list();
                for(int i=0; i<itemList.size(); i++) {
                    thereareItems = true;
                    Object[] row = (Object[]) itemList.get(i);
                    //only item_id
//                    System.out.println((String) row[1]  + ": (" + categoryId + " - " + (String) row[0] + ")");
                    //Detail information
                    System.out.println("getProdcut::" + (String)row[1]);
                    this.getProduct(factory, (String)row[1]);
                }
            }
            
            String s = thereareItems ? "\n" : "We are sorry but there are no items associated with category: " + cat_id + "(" + catName +")\n";

            System.out.println(s);
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            System.out.println("Ooops! Something went wrong while getting products by category path ... ^^' ");
        } finally {
            session.close();
        }
    }


    private void rekCat(SessionFactory factory, int cat_id) {
        Session session = factory.openSession();
		Transaction tx = null;

        try{
            tx = session.beginTransaction();
            String subCatQuery = "SELECT A.category_id FROM Category B JOIN B.sub_categories A WHERE B.category_id = " + cat_id;
            List<Integer> subCatList = session.createQuery(subCatQuery).list();
            if(!subCatList.isEmpty()) {
                System.out.print(" " + cat_id + " [ ");
                for(Integer subCat : subCatList) {
                    rekCat(factory, subCat);
                }
                System.out.print(" ] ");
            } else {
                System.out.print(" " + cat_id + " ");
            }            
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            System.out.println("Ooops! Something went wrong while getting the CategoryTree ... ^^' ");
        } finally {
            session.close();
        }
    }

    public void getCategoryTree(SessionFactory factory, int catId) {
        Session session = factory.openSession();
        Transaction tx = null;

        try{
            tx = session.beginTransaction();
            String startCatQuery = "SELECT name FROM Category WHERE category_id = " + catId;
            String catName = (String) session.createQuery(startCatQuery).uniqueResult();

            if(catName!=null) {
                System.out.print(catId + "\t" + catName);
                Boolean fin = false;

                while(!fin) {
                    String catQuery = "SELECT B.category_id, B.name FROM Category A JOIN A.over_categories B WHERE A.category_id = " + catId;
                    Object[] answer = (Object[]) session.createQuery(catQuery).uniqueResult();
                    if(answer!=null) {
                        System.out.print("\n" + (int) answer[0] + "\t" + (String) answer[1]);
                        catId = (int) answer[0];
                    } else {
                        System.out.println("\t(root)\n");
                        fin = true;
                    }
                }
            } else {
                System.out.println("We are sorry but this category does not exist in our database\n");
            }    
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            System.out.println("Ooops! Something went wrong while getting the CategoryTree ... ^^' \n");
        } finally {
            session.close();
        }
    }

    /**
     * jetzt wird was eingekauft!
     */
    public void purchaseItem(SessionFactory factory, String item_id, String customer_name, int shop_id) {
        Session session = factory.openSession();
		Transaction tx = null;

        try{
            tx = session.beginTransaction();
            Item boughtItem = (Item) session.get(Item.class, item_id);
            Customer buyingCustomer = (Customer) session.get(Customer.class, customer_name);
            if(boughtItem!=null && buyingCustomer!=null) {
                Purchase newPurchase = new Purchase(customer_name, item_id, shop_id, (new Date(System.currentTimeMillis())));
                session.save(newPurchase);
                System.out.println("Congratulations, "+ customer_name + "! You successfully bought " + item_id + "!");
            } else {
                System.out.println("We are sorry, but you are either not one of our customers or this item does not exist.");
            }			
            System.out.println();
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            System.out.println("Ooops! Something went wrong while purchasing an item ... ^^' ");
        } finally {
            session.close();
        }
    }

	@Override
	public void init() {
		// TODO Auto-generated method stub
		// it is implemented in InitState.java
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		// it is implemented in FinishState.java
	}



}

    
