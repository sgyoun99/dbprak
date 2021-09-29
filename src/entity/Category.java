/**
 * entity-class for Category
 * @version 21-09-23
 */

package entity;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.ArrayList;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.Stack;
import java.util.Set;

import exception.SQLKeyDuplicatedException;
import exception.XmlDataException;
import exception.XmlInvalidValueException;
import exception.XmlNoAttributeException;
import exception.XmlNullNodeException;
import exception.XmlValidationFailException;

import main.Config;
import main.ErrType;
import main.ErrorLogger;


public class Category {

	private int category_id;
    private String name;

    private Set sub_categories;
    private Set over_categories;
    private Set items;


    public Category() {}
    public Category(int category_id, String name) {
        this.category_id = category_id;
        this.name = name;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }
    public int getCategory_id() {
        return this.category_id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }
		
    public void setSub_categories(Set sub_categories) {
        this.sub_categories = sub_categories;
    }
    public Set getSub_categories() {
        return this.sub_categories;
    }
    public void setOver_categories(Set over_categories) {
        this.over_categories = over_categories;
    }
    public Set getOver_categories() {
        return this.over_categories;
    }
    public void setItems(Set items) {
        this.items = items;
    }
    public Set getItems() {
        return this.items;
    }
	

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!this.getClass().equals(obj.getClass())) return false;
  
        Category obj2 = (Category)obj;
        if(this.category_id == obj2.getCategory_id() ) {
           return true;
        }
        return false;
     }
	
}
