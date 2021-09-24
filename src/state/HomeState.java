package state;

import java.util.Scanner;

import main.App;
import main.DataLoader;

public class HomeState implements State {
	
	private Scanner sc = new Scanner(System.in);
	private String inputString = "";

	@Override
	public void printStateMessage() {
		System.out.print("[Home]\t\t\t\t");
		System.out.print("\033[35m");
		System.out.print("(DB: ");
		System.out.print(App.isDbInitiallized ? "on" : "off");
		System.out.print(" / Media: ");
		System.out.print(App.isMediaLoaded ? "loaded)" : "not_loaded)");
		System.out.print("\033[0m");
		System.out.println();
	}
	
	
	
	@Override
	public void runState() {
		printStateMessage();
		requestInput();
		if( !App.isDbInitiallized || !App.isMediaLoaded ) {
			switch (inputString) {
			case "1":
			case "2":
			case "3":
			case "q":
				executeCommand();
				break;
			default:
				System.out.println("Please init and load first.");
				new HomeState().runState();
				break;
			}
		} else {
			// already done init and load
			executeCommand();
		}
	}


	@Override
	public void executeCommand() {

		switch (inputString) {
		case "1":
			new InitState().runState();
			break;
		case "2":
			this.loadMedia();
			runState();
			break;
		case "3":
			new FinishState().runState();
			break;
		case "4":
			new ProductState().runState();
			break;
		case "5":
			new ProductsState().runState();
			break;
		case "6":
//			command.getCategoryTree();
			break;
		case "7":
//			command.getProductsByCategoryPath();
			break;
		case "8":
//			command.getTopProducts();
			break;
		case "9":
//			command.getSimilarCheaperProduct();
			break;
		case "10":
			new AddReviewState().runState();
			break;
		case "11":
			new GetTrollsState().runState();
			break;
		case "12":
//			command.getOffers();
			break;
		case "q":
			//end program
			new FinishState().executeCommand();
			System.out.println("bye.");
			break;
		default:
			System.out.println("\"" + inputString + "\" is invalid input.");
			new HomeState().runState();
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
		System.out.println(" 1: init. Start Database");
		System.out.println(" 2: load Media. Load XML to DB");
		System.out.println(" 3: finish. Close Database connection");
		System.out.println(" 4: getProduct. Show information by Product ID");
		System.out.println(" 5: getProducts. Search Products by title");
		System.out.println(" 6: getCategoryTree");
		System.out.println(" 7: getProductsByCategoryPath");
		System.out.println(" 8: getTopProducts");
		System.out.println(" 9: getSimilarCheaperProduct");
		System.out.println("10: addNewReview");
		System.out.println("11: getTrolls");
		System.out.println("12: getOffers");
		System.out.println(" q: quit Program");
		System.out.print(  ">>");

		this.inputString = sc.nextLine();
		

	}


	@Override
	public void runNextState() {
		new HomeState().runState();
	}



	@Override
	public boolean isValidInput() {
		// TODO Auto-generated method stub
		return false;
	}
	
	private void loadMedia() {
		if(App.isDbInitiallized) {
			if(App.isMediaLoaded) {
				System.out.println("Media data is already loaded.");
			} else {
				try {
					new DataLoader(App.sessionFactory).load();
					App.isMediaLoaded = true;
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Loading Media data has failed.");
				}
			}			
		} else {
			System.out.println("Database is not initiallized.");
		}

	}

}
