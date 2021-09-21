package state;

import org.hibernate.cfg.Configuration;

import frontend.HomeState;
import main.App;
import main.DataLoader;

public class InitState implements State {

	@Override
	public String[] validInputArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void executeCommand() {
		if(App.sessionFactory == null) {
			try {
				App.sessionFactory = new Configuration().configure().buildSessionFactory();
				new DataLoader(App.sessionFactory).load();
			} catch (Throwable ex) { 
				System.err.println("Failed to create sessionFactory object." + ex);
				throw new ExceptionInInitializerError(ex); 
			} finally {
			}
		} else {
			System.out.println("App is aleady initiallized!");
		}
		runNextState();
	}

	@Override
	public void responseResult() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void printStateMessage() {
		System.out.println("Initiallizing Database...");
		
	}

	@Override
	public void printInputRequestMessage() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void runNextState() {
		new HomeState().runState();
	}


}
