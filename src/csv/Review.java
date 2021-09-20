/**
 * read the reviews from file and write in Table Reviews in DB
 * @version 21-06-02
 */
package csv;

import java.sql.Date;
import java.util.Set;

public class Review{
    private int review_id;
    private String item_id;
    private String customer_name;
    private Date review_date;
    private String summary;
    private String content;
    private int rating;

    private Set customers;

    public Review() {}
    public Review(String item_id, String customer_name, Date review_date, String summary, String content, int rating) {
        this.item_id = item_id;
        this.customer_name = customer_name;
        this.review_date = review_date;
        this.summary = summary;
        this.content = content;
        this.rating = rating;
    }

    public int getReview_id() {
		return review_id;
	}
	public void setReview_id(int review_id) {
		this.review_id = review_id;
	}
    public String getItem_id() {
		return item_id;
	}
	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}
    public String getCustomer_name() {
		return this.customer_name;
	}
	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}
    public Date getReview_date() {
		return review_date;
	}
	public void setReview_date(Date review_date) {
		this.review_date = review_date;
	}
    public String getSummary() {
		return this.summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
    public String getContent() {
		return this.content;
	}
	public void setContent(String content) {
		this.content = content;
	}
    public int getRating() {
		return this.rating;
	}
	public void setRating(int rating) {
		this.rating = rating;
	}

    public Set getCustomers() {
		return this.customers;
	}
	public void setCustomers(Set customers) {
		this.customers = customers;
	}

}