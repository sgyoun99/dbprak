package state;

import java.util.Scanner;

import frontend.Command;

public class HomeState implements State {
	
	private Scanner sc = new Scanner(System.in);
	private String inputString = "";
	private Command command = new Command();

	@Override
	public void printStateMessage() {
		System.out.println("[Home]");
	}
	
	
	
	@Override
	public void runState() {
		printStateMessage();
		requestInput();
		executeCommand();
	}


	@Override
	public void executeCommand() {

		switch (inputString) {
		case "1":
			command.init();
			break;
		case "2":
			command.finish();
			break;
		case "3":
			command.getProduct();
			break;
		case "4":
			command.getProducts(null);
			break;
		case "5":
			command.getCategoryTree();
			break;
		case "6":
			command.getProductsByCategoryPath();
			break;
		case "7":
			command.getTopProducts();
			break;
		case "8":
			command.getSimilarCheaperProduct();
			break;
		case "9":
			command.addNewReview();
			break;
		case "10":
			command.getTrolls();
			break;
		case "11":
			command.getOffers();
			break;
		case "q":
			//end program
			new FinishState().executeCommand();
			System.out.println("bye.");
			break;
		default:
			System.out.println("\"" + inputString + "\" is invalid input.");
			this.runState();
			break;
		}
	}


	@Override
	public void responseResult() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void requestInput() {
		System.out.println("*** Available commands ***");
		System.out.println(" 1: init");
		System.out.println(" 2: finish");
		System.out.println(" 3: getProduct");
		System.out.println(" 4: getProducts");
		System.out.println(" 5: getCategoryTree");
		System.out.println(" 6: getProductsByCategoryPath");
		System.out.println(" 7: getTopProducts");
		System.out.println(" 8: getSimilarCheaperProduct");
		System.out.println(" 9: addNewReview");
		System.out.println("10: getTrolls");
		System.out.println("11: getOffers");
		System.out.println(" q: quit Program");
		System.out.print(  ">>");

		this.inputString = sc.nextLine();
	}


	@Override
	public void runNextState() {
		this.runState();
	}



	@Override
	public boolean isValidInput(String inputString) {
		// TODO Auto-generated method stub
		return false;
	}

}
