package state;

import java.util.Scanner;

import main.App;
import main.Testtat;

/**
 * Class for the state to retrieve category tree
 */
public class CategoryTreeState implements State {

	//flag for returning to home
	boolean toHome = false;
	//Scanner for user input
	Scanner sc = new Scanner(System.in);
	//it stores user input as string
	String inputString = "";


	/**
	 * Method to request user input
	 */
	@Override
	public void requestInput() {
		System.out.println("Enter Category ID(a number). or 'home' to go back to [Home]");
		System.out.print(">>");
		inputString = sc.nextLine();

		if(!isValidInput()) {
			requestInput();
		}
	}

	/**
	 * Method to validate user input
	 */
	@Override
	public boolean isValidInput() {
		
		switch (inputString) {
		case "home":
			this.toHome = true;
			return true;
		default:
			try {
				Integer.parseInt(inputString);
			} catch (Exception e) {
				System.out.println(inputString + " is invalid.");
				return false;
			}
			return true;
		}
	}

	/**
	 * Method to invoke the method getCategoryTree
	 */
	@Override
	public void executeCommand() {
		int categoryId = Integer.parseInt(inputString);
		new Testtat().getCategoryTree(App.sessionFactory, categoryId);;
	}


	/**
	 * Method for entry point of the State
	 */
	@Override
	public void runState() {
		printStateMessage();
		requestInput();
		if(toHome) {
			new HomeState().runState();
		} else {
			executeCommand();
			runNextState();
		}
	}

	/**
	 * Method to show state message
	 */
	@Override
	public void printStateMessage() {
		System.out.println("[Category Tree]");
	}

	/**
	 * Method to run next state
	 */
	@Override
	public void runNextState() {
		new HomeState().runState();
	}

}
