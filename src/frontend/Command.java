package frontend;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import main.App;
import state.FinishState;
import state.InitState;
import state.State;

public class Command implements ExecutableCommand {
	

	public void init() {;
		System.out.println("init()");
		if(App.sessionFactory == null) {
			try {
				App.sessionFactory = new Configuration().configure().buildSessionFactory();
			} catch (Throwable ex) { 
				System.err.println("Failed to create sessionFactory object." + ex);
				throw new ExceptionInInitializerError(ex); 
			}		
			new InitState().runState();
		} else {
			System.out.println("App is aleady initiallized.");
			new HomeState().runState();
		}
	}

	public void finish() {
		System.out.println("finish()");
		if(App.sessionFactory != null && App.sessionFactory.isOpen()) {
			new FinishState().runState();
		} else {
			System.out.println("init first please!");
			new HomeState().runState();
		}
	};

	public void getProduct() {
		System.out.println("getProduct()");
		
	};
	public void getProducts(String pattern) {
		
	}
	public void getCategoryTree() {
	
	}
	public void getProductsByCategoryPath() {
		
	}
	public void getTopProducts() {
		
	}
	public void getSimilarCheaperProduct() {
		
	}
	public void addNewReview() {
		
	}
	public void getTrolls() {
		
	}
	public void getOffers() {
		
	}
}
