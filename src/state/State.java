package state;

import frontend.CommandLineInterface;
import frontend.HomeState;

public interface State extends CommandLineInterface {

	public void runState();
	
	public void checkSession();

	public void printStateMessage();

	public State nextState();
	
	public default void restartState() {
		runState();
	};

	public default void moveToHomeState() {
		HomeState homeState = new HomeState();
		homeState.runState();
	}
}
