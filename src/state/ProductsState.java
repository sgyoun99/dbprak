package state;

import java.util.Scanner;

import main.App;
import main.Testtat;

/**
 * Class for the state to retrieve products with titles matching pattern
 */
public class ProductsState implements State {

	//Scanner for user input
	Scanner sc = new Scanner(System.in);
	//it stores user input as string
	String inputString = "";

	/**
	 * Method to request user input
	 */
	@Override
	public void requestInput() {
		System.out.println("Enter starting pattern of Product Title.");
		System.out.println("Wildcards '%', '_' are available.");
		System.out.println("(Try _ello% for example)");
		System.out.print(">>");
		inputString = sc.nextLine();

	}

	//not used in this class
	@Override
	public boolean isValidInput() {
		return false;
	}

	/**
	 * Method to invoke the method getProducts
	 */
	@Override
	public void executeCommand() {
		new Testtat().getProducts(App.sessionFactory, inputString);
	}


	/**
	 * Method for entry point of the State
	 */
	@Override
	public void runState() {
		printStateMessage();
		requestInput();
		executeCommand();
		runNextState();
	}

	/**
	 * Method to show state message
	 */
	@Override
	public void printStateMessage() {
		System.out.println("[Search Products with title]");

	}

	/**
	 * Method to run next state
	 */
	@Override
	public void runNextState() {
		new HomeState().runState();
	}

}
