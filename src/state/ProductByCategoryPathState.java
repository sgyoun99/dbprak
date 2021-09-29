package state;

import java.util.Scanner;

import main.App;
import main.Testtat;

/**
 * Class for the state to retrieve products under designated category path
 */
public class ProductByCategoryPathState implements State {

	//Scanner for user input
	Scanner sc = new Scanner(System.in);
	//it stores user input as string
	String inputString = "";
	
	//not used in this class
	@Override
	public void requestInput() {
		
	}

	//not used in this class
	@Override
	public boolean isValidInput() {
		return false;
	}

	/**
	 * Method to invoke the method getProductsByCategoryPath
	 */
	@Override
	public void executeCommand() {
		new Testtat().getProductsByCategoryPath(App.sessionFactory);
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
		System.out.println("[Products by category path]");
		
	}

	/**
	 * Method to run next state
	 */
	@Override
	public void runNextState() {
		new HomeState().runState();
	}

}
