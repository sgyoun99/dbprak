package frontend;

import state.State;

public interface CommandLineInterface {
	
	public void printInputRequestMessage();

	public String[] validInputArray();

	public default boolean isValidRequestParameter(String inputParameter) {
		boolean res = false;
		for (int i = 0; i < validInputArray().length; i++) {
//			if(inputParameter.toUpperCase().equals(validInputArray()[i].toUpperCase())) {
			if(inputParameter.equals(validInputArray()[i])) {
				res = true;
				break;
			}
		}
		return res;
	};
	
	public default void printErrorMessage(String invalidInput) {
		System.out.println(invalidInput + " is not valid command.");
	}

	public void executeCommand();

	public void responseResult();
	
	
}
