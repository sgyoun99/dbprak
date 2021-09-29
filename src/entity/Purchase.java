/**
 * entity-class for Purchase
 * not currently used
 * @version 21-09-23
 */

package entity;

import java.sql.Date;


public class Purchase {
    private int purchase_id;
    private String customer_name;
    private String item_id;
    private int shop_id;
    private Date order_date;
    //private Double price;

    public Purchase() {}
    public Purchase(String customer_name, String item_id, int shop_id, Date order_date/*, Double price*/) {
        this.customer_name = customer_name;
        this.item_id = item_id;
        this.shop_id = shop_id;
        this.order_date = order_date;
        //this.price = price;
    }

    public void setPurchase_id(int purchase_id) {
        this.purchase_id = purchase_id;
    }
    public int getPurchase_id() {
        return this.purchase_id;
    }
    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }
    public String getCustomer_name() {
        return this.customer_name;
    }
    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }
    public String getItem_id() {
        return this.item_id;
    }
    public void setShop_id(int shop_id) {
        this.shop_id = shop_id;
    }
    public int getShop_id() {
        return this.shop_id;
    }
    public void setOrder_date(Date order_date) {
        this.order_date = order_date;
    }
    public Date getOrder_date() {
        return this.order_date;
    }
    /*public void setPrice(Double price) {
        this.price = price;
    }
    public Double getPrice() {
        return this.price;
    }*/
    
}
