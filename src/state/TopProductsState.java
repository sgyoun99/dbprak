package state;

import java.util.Scanner;

import main.App;
import main.Testtat;

public class TopProductsState implements State {

	Scanner sc = new Scanner(System.in);
	String inputString = "";

	@Override
	public void requestInput() {
		System.out.println("Enter a limit(number).");
		System.out.print(">>");
		inputString = sc.nextLine();
		
	}

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

	@Override
	public void responseResult() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void runState() {
		printStateMessage();
		requestInput();
		executeCommand();
	}

	@Override
	public void printStateMessage() {
		System.out.println("[Top Products]");
		
	}

	@Override
	public void runNextState() {
		new HomeState().runState();
	}

}
