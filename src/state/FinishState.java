package state;

import org.hibernate.SessionFactory;

import frontend.HomeState;

public class FinishState implements State {
	
	private SessionFactory factory; 

	public FinishState(SessionFactory factory) {
		this.factory = factory;
	}

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
	public State executeCommand() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void responseResult() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void runState() {
		factory.close();
	}

	@Override
	public void printStateMessage() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public State nextState() {
		return new HomeState();
	}

	@Override
	public void checkSession() {
		// TODO Auto-generated method stub
		
	}

}
