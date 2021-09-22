package state;

import frontend.CommandLineInterface;

public interface State extends CommandLineInterface {

	public void runState();
	
	public void printStateMessage();

	public void runNextState();
	
}
