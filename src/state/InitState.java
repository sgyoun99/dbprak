package state;

import org.hibernate.SessionFactory;

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
		// TODO Auto-generated method stub
	}

	@Override
	public void responseResult() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void runState() {
		printStateMessage();
		new DataLoader(App.sessionFactory).load();
		runNextState();
	}

	@Override
	public void printStateMessage() {
		System.out.println("Initiallizing Database...");
		
	}

	@Override
	public void printInputRequestMessage() {
		// TODO Auto-generated method stub
		
	}


}
