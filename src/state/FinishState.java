package state;

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
		if(App.sessionFactory != null && App.sessionFactory.isOpen()) {
			App.sessionFactory.close();
			App.sessionFactory = null;
		} else {
			System.out.println("init first please!");
		}
		runNextState();
	}

	@Override
	public void responseResult() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void printStateMessage() {
		System.out.println("Closing Session...");
		
	}

	@Override
	public void runNextState() {
		new HomeState().runState();
	}


}
