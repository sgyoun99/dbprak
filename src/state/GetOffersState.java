package state;

import java.util.Scanner;

import main.App;
import main.Testtat;

/**
 * Class for the state to retrieve product offer
 *
 */
public class GetOffersState implements State {

	//Scanner for user input
	Scanner sc = new Scanner(System.in);
	//it stores user input as string
	String inputString = "";


	/**
	 * Method to request user input
	 */
	@Override
	public void requestInput() {
		System.out.println("Enter Product ID.");
		System.out.print(">>");
		inputString = sc.nextLine();
		
	}

	/**
	 * Method to validate user input
	 */
	@Override
	public boolean isValidInput() {
		return this.inputString != null && this.inputString.length() > 0;
	}

	/**
	 * Method to invoke the method getOffers
	 */
	@Override
	public void executeCommand() {
		String item_id = "";
		if(isValidInput()) {
			item_id = this.inputString;
			new Testtat().getOffers(App.sessionFactory, item_id);
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
		System.out.println("[Offers]");
		
	}

	/**
	 * Method to run next state
	 */
	@Override
	public void runNextState() {
		new HomeState().runState();
		
	}

}
