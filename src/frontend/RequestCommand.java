package frontend;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import state.FinishState;
import state.InitState;
import state.State;

public class RequestCommand implements ExecutableCommand {
	
	private static SessionFactory factory; 

	public void init() {;
		System.out.println("init()");
		try {
			factory = new Configuration().configure().buildSessionFactory();
		} catch (Throwable ex) { 
			System.err.println("Failed to create sessionFactory object." + ex);
			throw new ExceptionInInitializerError(ex); 
		}		
		State initState = new InitState(factory);
		initState.runState();
	}

	public void finish() {
		System.out.println("finish()");
		if(factory != null && factory.isOpen()) {
			State finishState = new FinishState(factory);
			finishState.runState();
		} else {
			System.out.println("init first please!");
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
