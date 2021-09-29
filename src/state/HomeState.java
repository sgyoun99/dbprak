package state;

import java.util.Scanner;

import main.App;
import main.DataLoader;

/**
 * Home State for Front-end CLI
 *
 */
public class HomeState implements State {
	
	//Scanner for user input
	private Scanner sc = new Scanner(System.in);
	//it stores user input as string
	private String inputString = "";

	// to show message with the state of Hibernate and DB
	@Override
	public void printStateMessage() {
		System.out.print("\n[Home]\t\t\t\t");
		System.out.print("\033[35m");
		System.out.print("(DB: ");
		System.out.print(App.isDbInitiallized ? "on" : "off");
		System.out.print(" / Media: ");
		System.out.print(App.isMediaLoaded ? "loaded)" : "not_loaded)");
		System.out.print("\033[0m");
		System.out.println();
	}
	
	
	
	// entry point of the State
	@Override
	public void runState() {
		printStateMessage();
		requestInput();
		if( !App.isDbInitiallized || !App.isMediaLoaded ) {
			switch (inputString) {
			case "1":
				//to enable hibernate and DB
			case "2":
				//to load DB 
			case "3":
				//to finish hibernate
			case "q":
				// to quit program
				executeCommand();
				break;
			default:
				// before hibernate and DB are loaded, other menu can not be selected.
				System.out.println("Please init and load first.");
				new HomeState().runState();
				break;
			}
		} else {
			// when init and load are done
			executeCommand();
		}
	}


	// to enter the state according to user input and run next State
	@Override
	public void executeCommand() {

		switch (inputString) {
		case "1":
			//to enable hibernate and DB
			new InitState().runState();
			break;
		case "2":
			//to load DB 
			this.loadMedia();
			runState();
			break;
		case "3":
			//to finish hibernate
			new FinishState().runState();
			break;
		case "4":
			//to retrieve product with the given item_id 
			new ProductState().runState();
			break;
		case "5":
			//to retrieve list of products with the matching title pattern
			new ProductsState().runState();
			break;
		case "6":
			//to retrieve category tree of given category id
			new CategoryTreeState().runState();
			break;
		case "7":
			//to retrieve products with the given category path
			new ProductByCategoryPathState().runState();
			break;
		case "8":
			//to retrieve the highest rated top-k products(ordered by sales ranking)
			new TopProductsState().runState();
			break;
		case "9":
			//to find cheaper product with similarity 
			new SimilarCheaperProductState().runState();
			break;
		case "10":
			//to add Review
			new AddReviewState().runState();
			break;
		case "11":
			//to retrieve users with the low average rating under the given rating
			new GetTrollsState().runState();
			break;
		case "12":
			//to retrieve available products
			new GetOffersState().runState();
			break;
		case "13":
			//to purchase available products
			new PurchaseState().runState();
			break;
		case "q":
			// to quit program
			new FinishState().executeCommand();
			System.out.println("bye.");
			break;
		default:
			System.out.println("\"" + inputString + "\" is invalid input.");
			new HomeState().runState();
			break;
		}
	}


	// to request user input
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
		System.out.println("13: purchase");
		System.out.println(" q: quit Program");
		System.out.print(  ">>");

		this.inputString = sc.nextLine();
		

	}

	// to start the next State(Home)
	@Override
	public void runNextState() {
		new HomeState().runState();
	}


	// not used in this Class
	@Override
	public boolean isValidInput() {
		return false;
	}
	
	// to load Media XML when it is not loaded yet
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
