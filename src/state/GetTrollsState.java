package state;

import java.util.Scanner;

import main.App;
import main.Testtat;

public class GetTrollsState implements State {

	Scanner sc = new Scanner(System.in);
	String inputString = "";
	boolean toHome = false;


	@Override
	public void requestInput() {
		System.out.println("Enter Rating.");
		System.out.print(">>");
		inputString = sc.nextLine();

		if(!isValidInput()) {
			requestInput();
		}
	}

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

	@Override
	public void executeCommand() {
		int rating = Integer.parseInt(inputString);
		new Testtat().getTrolls(App.sessionFactory, rating);;
	}

	@Override
	public void responseResult() {
		// TODO Auto-generated method stub

	}

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

	@Override
	public void printStateMessage() {
		System.out.println("[List of Trolls]");
	}

	@Override
	public void runNextState() {
		new HomeState().runState();
	}

}
