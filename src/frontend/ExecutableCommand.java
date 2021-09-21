package frontend;

import state.*;

public interface ExecutableCommand {

	public void init();

	public void finish();

	public void getProduct();

	public void getProducts(String pattern);

	public void getCategoryTree();
	
	public void getProductsByCategoryPath();
	
	public void getTopProducts();
	
	public void getSimilarCheaperProduct();
	
	public void addNewReview();
	
	public void getTrolls();
	
	public void getOffers();
	

}
