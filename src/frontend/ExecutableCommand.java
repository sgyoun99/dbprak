package frontend;

import org.hibernate.SessionFactory;

public interface ExecutableCommand {

	public void init();

	public void finish();

	public void getProduct(SessionFactory factory, String item_id);

	public void getProducts(SessionFactory factory, String pattern);

	public void getCategoryTree(SessionFactory factory, int startCat);
	
	public void getProductsByCategoryPath(SessionFactory factory);
	
	public void getTopProducts(SessionFactory factory, int limit);
	
	public void getSimilarCheaperProduct(SessionFactory factory, String item_id);
	
	public void addNewReview(SessionFactory factory, String item_id, String customer, String summary, String content, int rating);
	
	public void getTrolls(SessionFactory factory, int limit);
	
	public void getOffers(SessionFactory factory, String item_id);
	

}
