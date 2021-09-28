package state;

import java.util.Scanner;

import javax.persistence.NoResultException;

import main.App;
import main.Testtat;

public class PurchaseState implements State {

	boolean toHome = false;
	Scanner sc = new Scanner(System.in);
	String inputString = "";
	String item_id = "";
	String customer = "guest";
	int shop_id = 0;
	
	
	@Override
	public void requestInput() {
		System.out.println("Please fill the information below and then enter buy.");
		System.out.println("1: Product ID = " + this.item_id);
		System.out.println("2: Shop ID    = " + (this.shop_id == 0 ? "" : this.shop_id));
		System.out.println("3: Customer   = " + this.customer);
		System.out.println("buy: To purchase selected product.");
		System.out.println("home: To Home");
		System.out.print(">>");
		
		this.inputString = sc.nextLine();
		
		switch (this.inputString) {
		case "1":
			this.setItemId();
			this.requestInput();
			break;
		case "2":
			this.setShopId();
			this.requestInput();
			break;
		case "3":
			this.setCustomer();
			this.requestInput();
			break;
		case "buy":
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

	@Override
	public boolean isValidInput() {
		try {
			Integer.parseInt(inputString);
			return true;
		} catch (Exception e) {
			System.out.println(this.inputString + " is invalid Shop ID.");
			return false;
		}
	}

	@Override
	public void executeCommand() {
		new Testtat().purchaseItem(App.sessionFactory, item_id, customer, shop_id);
	}


	@Override
	public void runState() {
		printStateMessage();
		requestInput();
		if(this.toHome) {
			new HomeState().runState();
		} else {
			try {
				System.out.println("now purchasing...");
				executeCommand();
				runNextState();
			} catch (NoResultException e) {
				System.out.println(e.getMessage());
				runState();
			}			
		}
	}

	@Override
	public void printStateMessage() {
		System.out.println("[Purchase Item]");

	}

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
	
	
	private void setShopId() {
		System.out.println("Enter Shop ID(a number)");
		System.out.print(">>");
		this.inputString = sc.nextLine();
		if(!isValidInput()) {
			setShopId();
		} else {
			this.shop_id = Integer.parseInt(this.inputString);
		}
	}

}
