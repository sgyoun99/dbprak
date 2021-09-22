package state;

import java.util.Scanner;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import entity.Item;
import main.App;

public class ProductState implements State {
	
	Scanner sc = new Scanner(System.in);
	String inputString = "";

	@Override
	public void requestInput() {
		System.out.println("Enter Product ID.");
		System.out.print(">>");
		inputString = sc.nextLine();
		
	}

	@Override
	public boolean isValidInput() {
		return this.inputString != null && this.inputString.length() > 0;
	}

	@Override
	public void executeCommand() {
		String item_id = "";
		if(isValidInput()) {
			item_id = this.inputString;
			System.out.println(item_id);
			Session session = App.sessionFactory.openSession();
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
		} else {
			System.out.println("Invalid input.");
			runState();
		}
	}

	@Override
	public void responseResult() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void runState() {
		printStateMessage();
		requestInput();
		executeCommand();
		runNextState();
	}

	@Override
	public void printStateMessage() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void runNextState() {
		new HomeState().runState();
		
	}
	
}
