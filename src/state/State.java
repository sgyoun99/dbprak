package state;

import frontend.CommandLineInterface;
import frontend.HomeState;

public interface State extends CommandLineInterface {

	public default void runState() {
		printStateMessage();
		executeCommand();
	};
	
	public void printStateMessage();

	public void runNextState();
	
}
