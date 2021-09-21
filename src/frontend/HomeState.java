package frontend;

import java.util.Scanner;

import state.State;

public class HomeState implements State {
	
	Scanner sc = new Scanner(System.in);
	RequestCommand command = new RequestCommand();

	@Override
	public void printStateMessage() {
		System.out.println("***********************************");
		System.out.println("* List of available commands      *");
		System.out.println("***********************************");
		System.out.println(" 1. init");
		System.out.println(" 2. finish");
		System.out.println(" 3. getProduct");
		System.out.println(" 4. getProducts");
		System.out.println(" 5. getCategoryTree");
		System.out.println(" 6. getProductsByCategoryPath");
		System.out.println(" 7. getTopProducts");
		System.out.println(" 8. getSimilarCheaperProduct");
		System.out.println(" 9. addNewReview");
		System.out.println("10. getTrolls");
		System.out.println("11. getOffers");
	}
	
	
	@Override
	public String[] validInputArray() {
		return new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"};
	}
	
	@Override
	public void runState() {
		while(true) {
			printStateMessage();
			printInputRequestMessage();
			String inputString = "";
			inputString = sc.nextLine();

			switch (inputString) {
			// TODO
			// terminal clear command?
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
				command.getProducts("");
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
			default:
				printErrorMessage(inputString);
				break;
			}


		}
	}


	@Override
	public State executeCommand() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void responseResult() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void printInputRequestMessage() {
//		sc.nextLine(); // clear input stream?
		System.out.println("***********************************");
		System.out.println("* Please enter only one number.   *");
		System.out.println("***********************************");
		System.out.print(  ">>");
	}


	@Override
	public State nextState() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void checkSession() {
		// TODO Auto-generated method stub
		
	}

}
