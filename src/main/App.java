package main;

import org.hibernate.SessionFactory;

import frontend.HomeState;

public class App {
	
	public static SessionFactory sessionFactory = null;

	public static void main(String[] args) {
		HomeState homeState = new HomeState();
		homeState.runState();
	}

}
