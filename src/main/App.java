package main;


import java.util.NoSuchElementException;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;

import state.HomeState;

public class App {
	
	public static StandardServiceRegistry registry = null;
	public static SessionFactory sessionFactory = null;
	public static boolean isDbInitiallized = false;
	public static boolean isMediaLoaded = false;
	
	public static void main(String[] args) {
		try {
			HomeState homeState = new HomeState();
			homeState.runState();
		} catch (NoSuchElementException e) {
//			e.printStackTrace();
		}
	}

}
