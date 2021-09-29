/**
 * Customer-class (entity, sorted under csv for convenience)
 * @version 21-09-23
 */

package csv;

import java.io.Serializable;
import java.util.Set;

public class Customer implements Serializable{
    private String customer_name;
    private String street;
    private int nr;
    private int zip;
    private String city;
    private String account_number;
    
    private Set reviews;
    private Set purchases;
    
	public Customer() {}
    public Customer(String customer_name, String street, int nr, int zip, String city, String account_number) {
        this.customer_name = customer_name;
        this.street = street;
        this.nr = nr;
        this.zip = zip;
        this.city = city;
        this.account_number = account_number;
    }


    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }
    public String getCustomer_name() {
        return this.customer_name;
    }
    public void setStreet(String street) {
        this.street = street;
    }
    public String getStreet() {
        return this.street;
    }
    public void setNr(int nr) {
        this.nr = nr;
    }
    public int getNr() {
        return this.nr;
    }
    public void setZip(int zip) {
        this.zip = zip;
    }
    public int getZip() {
        return this.zip;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getCity() {
        return this.city;
    }
    public void setAccount_number(String account_number) {
        this.account_number = account_number;
    }
    public String getAccount_number() {
        return this.account_number;
    }


    public Set getReviews() {
	 return reviews;
    }
    public void setReviews(Set reviews) {
	this.reviews = reviews;
    }
    public void setPurchases(Set purchases) {
        this.purchases = purchases;
    }
    public Set getPurchases() {
        return this.purchases;
    }



    @Override
    public String toString(){
        return this.customer_name;
    }
}
