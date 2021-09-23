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
		new ProductState().runState();
		
	}
	public void getProducts(String pattern) {
		System.out.println("not yet");
	}
	public void getCategoryTree() {
		System.out.println("not yet");
	}
	public void getProductsByCategoryPath() {
		System.out.println("not yet");
	}
	public void getTopProducts() {
		System.out.println("not yet");
	}
	public void getSimilarCheaperProduct() {
		System.out.println("not yet");
	}
	public void addNewReview() {
		System.out.println("not yet");
	}
	public void getTrolls() {
		System.out.println("not yet");
	}
	public void getOffers() {
		System.out.println("not yet");
	}
}
