package state;

import java.util.Scanner;

import main.App;
import main.Testtat;

public class ProductsState implements State {

	Scanner sc = new Scanner(System.in);
	String inputString = "";

	@Override
	public void requestInput() {
		System.out.println("Enter starting pattern of Product Title.");
		System.out.println("Wildcards '%', '_' are available.");
		System.out.println("(Try _ello% for example)");
		System.out.print(">>");
		inputString = sc.nextLine();

	}

	@Override
	public boolean isValidInput() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void executeCommand() {
		new Testtat().getProducts(App.sessionFactory, inputString);
	}


	@Override
	public void runState() {
		printStateMessage();
		requestInput();
		executeCommand();
		runNextState();
	}

	@Override
	public void printStateMessage() {
		System.out.println("[Search Products with title]");

	}

	@Override
	public void runNextState() {
		new HomeState().runState();
	}

}
