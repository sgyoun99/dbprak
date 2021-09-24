package state;

import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import main.App;

public class FinishState implements State {
	
	@Override
	public void runState() {
		printStateMessage();
		executeCommand();
		runNextState();
	}

	@Override
	public void requestInput() {
		// TODO Auto-generated method stub
		
	}


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

	@Override
	public void responseResult() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void printStateMessage() {
		System.out.println("[Finishing]");
		
	}

	@Override
	public void runNextState() {
		new HomeState().runState();
	}

	@Override
	public boolean isValidInput() {
		// TODO Auto-generated method stub
		return false;
	}



}
