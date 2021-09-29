/**
 * entity-class for Book
 * @version 21-09-23
 */

package entity;

import java.sql.Date;
import java.util.Set;

import exception.XmlDataException;
import exception.XmlInvalidValueException;
import exception.XmlNoAttributeException;
import exception.XmlNullNodeException;
import exception.XmlValidationFailException;



public class Book {

	private String item_id;
	private Short pages;
	private Date publication_date;
	private String isbn;
	
	private Set authors;
	private Set publishers;
	
	
	public Book(String item_id, Short pages, Date publication_date, String isbn) {
		this.item_id = item_id;
		this.pages = pages;
		this.publication_date = publication_date;
		this.isbn = isbn;
	}

	public Book() {
	}

	public String getItem_id() {
		return item_id;
	}
	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}
	public Date getPublication_date() {
		return publication_date;
	}
	public void setPublication_date(String publication_date) throws XmlInvalidValueException{
		if(publication_date.equals("")) {
			this.publication_date = null;
		} else {
			
			if(publication_date != null) {
				try {
					this.publication_date = Date.valueOf(publication_date);
				} catch (IllegalArgumentException e) {
					XmlInvalidValueException ex = new XmlInvalidValueException("date is not in the form yyyy-mm-dd");
					ex.setAttrName("publication_date");
					throw ex;
				}
			}

		}
	}
	public void setPublication_date(Date publication_date) {
		this.publication_date = publication_date;
	}
	public String getIsbn() {
		return isbn;
	}
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}
	public void setPages(String pages) {
		if(pages.length() == 0 || pages == null) {
			this.pages = 0;
		} else {
			this.pages = Short.valueOf(pages);
		}
	}
	public void setPages(Short pages) {
		this.pages = pages;
	}
	public Short getPages() {
		return this.pages;
	}
	
	public Set getAuthors() {
		return this.authors;
	}
	public void setAuthors(Set authors) {
		this.authors = authors;
	}
	public Set getPublishers() {
		return this.publishers;
	}
	public void setPublishers(Set publishers) {
		this.publishers = publishers;
	}
	
	
}
