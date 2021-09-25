package state;

import java.util.Scanner;

import main.App;
import main.Testtat;

public class ProductByCategoryPathState implements State {

	
	Scanner sc = new Scanner(System.in);
	String inputString = "";

	@Override
	public void requestInput() {
//		system.out.println("enter product id.");
//		system.out.print(">>");
//		inputString = sc.nextLine();
		
	}

	@Override
	public boolean isValidInput() {
		return false;
	}

	@Override
	public void executeCommand() {
		new Testtat().getProductsByCategoryPath(App.sessionFactory);
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
		runNextState();
	}

	@Override
	public void printStateMessage() {
		System.out.println("[Products by category path]");
		
	}

	@Override
	public void runNextState() {
		new HomeState().runState();
	}

}
