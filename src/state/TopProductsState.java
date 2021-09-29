package state;

import java.util.Scanner;

import main.App;
import main.Testtat;

/**
 * Class for the state to retrieve highst rated top-k products
 */
public class TopProductsState implements State {

	//Scanner for user input
	Scanner sc = new Scanner(System.in);
	//it stores user input as string
	String inputString = "";

	/**
	 * Method to request user input
	 */
	@Override
	public void requestInput() {
		System.out.println("Enter a limit(number).");
		System.out.print(">>");
		inputString = sc.nextLine();
		
	}

	/**
	 * Method to validate user input
	 * returns false to the null input
	 */
	@Override
	public boolean isValidInput() {
		int input;
		try {
			input = Integer.parseInt(inputString);
		} catch (Exception e) {
			return false;
		}
		return 0 < input && input < Integer.MAX_VALUE;
	}

	/**
	 * Method to invoke the method getTopProducts
	 */
	@Override
	public void executeCommand() {
		if(isValidInput()) {
			int limit = Integer.parseInt(this.inputString);
			new Testtat().getTopProducts(App.sessionFactory, limit);
			runNextState();
		} else {
			System.out.println("Invalid input.");
			runState();
		}
	}


	/**
	 * Method for entry point of the State
	 */
	@Override
	public void runState() {
		printStateMessage();
		requestInput();
		executeCommand();
	}

	/**
	 * Method to show state message
	 */
	@Override
	public void printStateMessage() {
		System.out.println("[Top Products]");
		
	}

	/**
	 * Method to run next state
	 */
	@Override
	public void runNextState() {
		new HomeState().runState();
	}

}
