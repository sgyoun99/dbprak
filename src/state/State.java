package state;

import frontend.CommandLineInterface;

/**
 * 
 * Interface for each state used in Command Line Interface
 *
 */
public interface State extends CommandLineInterface {

	// entry point of the State
	public void runState();
	
	// to print each state's initial message
	public void printStateMessage();

	// to start the next State
	public void runNextState();
	
}
