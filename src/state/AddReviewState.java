package state;

import java.util.Scanner;

import javax.persistence.NoResultException;

import main.App;
import main.Testtat;

/**
 * Class for the state to add review
 */
public class AddReviewState implements State {

	//flag for returning to home
	boolean toHome = false;
	//Scanner for user input
	Scanner sc = new Scanner(System.in);
	//it stores user input as string
	String inputString = "";
	String item_id = "";
	String customer = "guest";
	String summary = "";
	String content = "";
	int rating = 5;
	
	
	/**
	 * Method to request user input
	 */
	@Override
	public void requestInput() {
		System.out.println("Please fill the information below and then enter add.");
		System.out.println("1: Product ID = " + this.item_id);
		System.out.println("2: Rating     = " + this.rating);
		System.out.println("3: Customer   = " + this.customer);
		System.out.println("4: Summary    = " + this.summary);
		System.out.println("5: Content    = " + this.content);
		System.out.println("add: Add review");
		System.out.println("home: To Home");
		System.out.print(">>");
		
		this.inputString = sc.nextLine();
		
		switch (this.inputString) {
		case "1":
			this.setItemId();
			this.requestInput();
			break;
		case "2":
			this.setRating();
			this.requestInput();
			break;
		case "3":
			this.setCustomer();
			this.requestInput();
			break;
		case "4":
			this.setSummary();
			this.requestInput();
			break;
		case "5":
			this.setContent();
			this.requestInput();
			break;
		case "add":
			//end requestInput
			break;
		case "home":
			this.toHome = true;
			break;
		default:
			System.out.println(this.inputString + " is invalid input.");
			this.requestInput();
			break;
		}

	}

	/**
	 * Method to validate user input
	 */
	@Override
	public boolean isValidInput() {
		//test rating 1/2/3/4/5
		switch (this.inputString) {
		case "1":
		case "2":
		case "3":
		case "4":
		case "5":
			return true;
		default:
			System.out.println(this.inputString + " is invalid Rating.");
			return false;
		}
	}

	/**
	 * Method to invoke the method addNewReview
	 */
	@Override
	public void executeCommand() {
		new Testtat().addNewReview(App.sessionFactory, item_id, customer, summary, content, rating);
	}


	/**
	 * Method for entry point of the State
	 */
	@Override
	public void runState() {
		printStateMessage();
		requestInput();
		if(this.toHome) {
			new HomeState().runState();
		} else {
			try {
				System.out.println("now adding...");
				executeCommand();
				runNextState();
			} catch (NoResultException e) {
				System.out.println(e.getMessage());
				runState();
			}			
		}
	}

	/**
	 * Method to show state message
	 */
	@Override
	public void printStateMessage() {
		System.out.println("[Add new review]");

	}

	/**
	 * Method to run next state
	 */
	@Override
	public void runNextState() {
		new HomeState().runState();
	}
	
	
	private void setItemId() {
		System.out.println("Enter Product ID");
		System.out.print(">>");
		this.item_id = sc.nextLine();
	}
	
	private void setCustomer() {
		System.out.println("Enter Customer ID");
		System.out.print(">>");
		this.customer = sc.nextLine();
	}
	
	private void setSummary() {
		System.out.println("Write review summary and press Enter");
		System.out.print(">>");
		this.summary = sc.nextLine();
	
	}
	
	private void setContent() {
		System.out.println("Write review and press Enter");
		System.out.print(">>");
		this.content = sc.nextLine();
	}
	
	private void setRating() {
		System.out.println("Enter Rating 1 ~ 5");
		System.out.print(">>");
		this.inputString = sc.nextLine();
		if(!isValidInput()) {
			setRating();
		} else {
			this.rating = Integer.parseInt(this.inputString);
		}
	}

}
