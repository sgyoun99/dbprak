package state;

import java.util.Scanner;

import main.App;
import main.Testtat;

public class CategoryTreeState implements State {

	Scanner sc = new Scanner(System.in);
	String inputString = "";
	boolean toHome = false;


	@Override
	public void requestInput() {
		System.out.println("Enter Category ID(a number). or 'home' to go back to [Home]");
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

	@Override
	public void executeCommand() {
		int categoryId = Integer.parseInt(inputString);
		new Testtat().getCategoryTree(App.sessionFactory, categoryId);;
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
		System.out.println("[Category Tree]");
	}

	@Override
	public void runNextState() {
		new HomeState().runState();
	}

}
