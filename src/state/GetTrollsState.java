package state;

import java.util.Scanner;

import main.App;
import main.Testtat;

/**
 * Class for the state to retrieve users with lowest average rating
 */
public class GetTrollsState implements State {

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
		System.out.println("Enter Rating. or 'home' to go back to [Home]");
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
		case "2":
		case "3":
		case "4":
		case "5":
			return true;
		default:
			System.out.println(inputString + " is invalid.");
			return false;
		}
	}

	/**
	 * Method to invoke the method getTrolls
	 */
	@Override
	public void executeCommand() {
		int rating = Integer.parseInt(inputString);
		new Testtat().getTrolls(App.sessionFactory, rating);;
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
		System.out.println("[List of Trolls]");
	}

	/**
	 * Method to run next state
	 */
	@Override
	public void runNextState() {
		new HomeState().runState();
	}

}
