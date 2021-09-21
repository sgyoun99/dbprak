package state;

import org.hibernate.SessionFactory;

import frontend.HomeState;
import main.App;

public class FinishState implements State {
	
	@Override
	public void printInputRequestMessage() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String[] validInputArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void executeCommand() {
		App.sessionFactory.close();
		App.sessionFactory = null;
	}

	@Override
	public void responseResult() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void runState() {
		printStateMessage();
		executeCommand();
		runNextState();
	}

	@Override
	public void printStateMessage() {
		System.out.println("Closing Session...");
		
	}


}
