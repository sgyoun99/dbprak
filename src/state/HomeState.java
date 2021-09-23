package state;

import java.util.Scanner;

import frontend.Command;
import main.App;
import main.DataLoader;

public class HomeState implements State {
	
	private Scanner sc = new Scanner(System.in);
	private String inputString = "";
	private Command command = new Command();

	@Override
	public void printStateMessage() {
		System.out.print("[Home]\t\t\t\t");
		System.out.print("\033[35m(DB: ");
		System.out.print(App.isDbInitiallized ? "on" : "off");
		System.out.print(" / Media: ");
		System.out.print(App.isMediaLoaded ? "loaded" : "not_loaded");
		System.out.println(")\033[0m");
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
			command.init();
			break;
		case "2":
			this.loadMedia();
			runState();
			break;
		case "3":
			command.finish();
			break;
		case "4":
			command.getProduct();
			break;
		case "5":
			command.getProducts(null);
			break;
		case "6":
			command.getCategoryTree();
			break;
		case "7":
			command.getProductsByCategoryPath();
			break;
		case "8":
			command.getTopProducts();
			break;
		case "9":
			command.getSimilarCheaperProduct();
			break;
		case "10":
			command.addNewReview();
			break;
		case "11":
			command.getTrolls();
			break;
		case "12":
			command.getOffers();
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
		System.out.println(" 1: init Database");
		System.out.println(" 2: load Media XML to DB");
		System.out.println(" 3: finish Database connection");
		System.out.println(" 4: getProduct");
		System.out.println(" 5: getProducts");
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
				new DataLoader(App.sessionFactory).load();
				App.isMediaLoaded = true;
			}			
		} else {
			System.out.println("Database is not initiallized.");
		}

	}

}
