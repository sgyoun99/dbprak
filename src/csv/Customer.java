
package csv;

import java.io.Serializable;

public class Customer implements Serializable{
    //private int customer_id;
    private String name;
    private String street;
    private int nr;
    private int zip;
    private String city;
    private String account_number;
    

    public Customer() {}
    public Customer(String name, String street, int nr, int zip, String city, String account_number) {
        this.name = name;
        this.street = street;
        this.nr = nr;
        this.zip = zip;
        this.city = city;
        this.account_number = account_number;
    }

/*    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }
    public int getCustomer_id() {
        return this.customer_id;
    }*/
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
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





    @Override
    public String toString(){
        return this.name;
    }
}
