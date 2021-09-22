package frontend;


public interface CommandLineInterface {
	
	public void requestInput();

	public boolean isValidInput(String inputString);

	public void executeCommand();

	public void responseResult();
	
	
}
