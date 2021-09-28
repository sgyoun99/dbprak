package state;

import java.util.Scanner;

import main.App;
import main.Testtat;

public class ProductState implements State {
	
	Scanner sc = new Scanner(System.in);
	String inputString = "";

	@Override
	public void requestInput() {
		System.out.println("Enter Product ID.");
		System.out.print(">>");
		inputString = sc.nextLine();
		
	}

	@Override
	public boolean isValidInput() {
		return this.inputString != null && this.inputString.length() > 0;
	}

	@Override
	public void executeCommand() {
		String item_id = "";
		if(isValidInput()) {
			item_id = this.inputString;
			new Testtat().getProduct(App.sessionFactory, item_id);
			runNextState();
		} else {
			System.out.println("Invalid input.");
			runState();
		}
	}


	@Override
	public void runState() {
		printStateMessage();
		requestInput();
		executeCommand();
	}

	@Override
	public void printStateMessage() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void runNextState() {
		new HomeState().runState();
		
	}
	
}
