package main;


import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;

import state.HomeState;

public class App {
	
	public static StandardServiceRegistry registry = null;
	public static SessionFactory sessionFactory = null;
	
	public static void main(String[] args) throws Exception {
		HomeState homeState = new HomeState();
		homeState.runState();
	}

}
