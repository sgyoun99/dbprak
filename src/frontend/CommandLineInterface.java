package frontend;


/**
 * Interface to define the least requirement of the CLI
 */
public interface CommandLineInterface {
	
	// to request input from user
	public void requestInput();

	// to validate user input
	// returns true when the input is valid according to the request
	public boolean isValidInput();

	// to execute command according to user input and run next State
	public void executeCommand();

	
}
