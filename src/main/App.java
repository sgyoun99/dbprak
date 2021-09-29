package main;


import java.util.NoSuchElementException;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;

import state.HomeState;

/**
 * Startup Class of the program
 *
 */
public class App {
	
	/**
	 * ServiceRegistry for Hibernate used for the Programm
	 */
	public static StandardServiceRegistry registry = null;
	
	/**
	 * SessionFactory for Hibernate used for the Programm
	 */
	public static SessionFactory sessionFactory = null;
	
	/**
	 * it is true when the hibernate service has started with a cfg file
	 */
	public static boolean isDbInitiallized = false;
	
	/**
	 * it is true when the Media data(xml) are successfully parsed and loaded in DB.
	 */
	public static boolean isMediaLoaded = false;
	
	
	/**
	 * Main method of the Programm
	 * @param args not used
	 */
	public static void main(String[] args) {
		try {
			HomeState homeState = new HomeState();
			homeState.runState();
		} catch (NoSuchElementException e) {
//			e.printStackTrace();
		}
	}

}
