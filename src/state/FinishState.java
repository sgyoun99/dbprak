package state;

import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import main.App;

/**
 * Class for the state to finish Hibernate connection with the current property cfg file
 */
public class FinishState implements State {
	
	/**
	 * Method for entry point of the State
	 */
	@Override
	public void runState() {
		printStateMessage();
		executeCommand();
		runNextState();
	}

	//not used in this Class
	@Override
	public void requestInput() {
		
	}


	/**
	 * Method to free the Hibernate connection and configuration
	 */
	@Override
	public void executeCommand() {
		if(App.sessionFactory != null && App.sessionFactory.isOpen()) {
			App.sessionFactory.close();
			App.sessionFactory = null;
			App.isDbInitiallized = false;
			StandardServiceRegistryBuilder.destroy(App.registry);
			System.out.println("Database is now finished.");
		} else {
			System.out.println("Database is not in use.");
		}
	}


	/**
	 * Method to show state message
	 */
	@Override
	public void printStateMessage() {
		System.out.println("[Finishing]");
		
	}


	/**
	 * Method to run next state
	 */
	@Override
	public void runNextState() {
		new HomeState().runState();
	}

	//not used in this Class
	@Override
	public boolean isValidInput() {
		return false;
	}



}
