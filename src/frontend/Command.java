package frontend;

import state.*;

public class Command implements ExecutableCommand {
	

	public void init() {;
		new InitState().runState();
	}

	public void finish() {
		new FinishState().runState();
	}

	public void getProduct() {
		System.out.println("getProduct()");
		new ProductState().runState();
		
	}
	public void getProducts(String pattern) {
		
	}
	public void getCategoryTree() {
	
	}
	public void getProductsByCategoryPath() {
		
	}
	public void getTopProducts() {
		
	}
	public void getSimilarCheaperProduct() {
		
	}
	public void addNewReview() {
		
	}
	public void getTrolls() {
		
	}
	public void getOffers() {
		
	}
}
