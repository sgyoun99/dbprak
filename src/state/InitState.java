package state;

import org.hibernate.SessionFactory;

import main.DataLoader;

public class InitState implements State {

	private SessionFactory factory; 

	public InitState(SessionFactory factory) {
		this.factory = factory;
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
		DataLoader dataLoader = new DataLoader(factory);
		dataLoader.load();
		
	}

	@Override
	public void printStateMessage() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void printInputRequestMessage() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public State nextState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void checkSession() {
		// TODO Auto-generated method stub
		
	}

}
