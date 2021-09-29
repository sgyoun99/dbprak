package frontend;

import org.hibernate.SessionFactory;

/**
 * Interface for Aufgabe of Testtat3
 * It is implemented through the front end
 */
public interface ExecutableCommand {

	//to initialize Hibernate
	public void init();

	//to finish the Hibernate
	public void finish();

	//to retrieve product with the given item_id
	public void getProduct(SessionFactory factory, String item_id);

	//to retrieve list of products with the matching title pattern
	public void getProducts(SessionFactory factory, String pattern);

	//to retrieve category tree of given category id
	public void getCategoryTree(SessionFactory factory, int startCat);
	
	//to retrieve products with the given category path
	public void getProductsByCategoryPath(SessionFactory factory);
	
	//to retrieve the highest rated top-k products(ordered by sales ranking)
	public void getTopProducts(SessionFactory factory, int limit);
	
	//to find cheaper product with similarity 
	public void getSimilarCheaperProduct(SessionFactory factory, String item_id);
	
	//to add Review
	public void addNewReview(SessionFactory factory, String item_id, String customer, String summary, String content, int rating);
	
	//to retrieve users with the low average rating under the given rating
	public void getTrolls(SessionFactory factory, int limit);
	
	//to retrieve available products
	public void getOffers(SessionFactory factory, String item_id);
	

}
