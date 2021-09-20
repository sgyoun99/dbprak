/**
 * Classes needed to read for Books all data from file
 * and write it in the associated tables
 * @version 03.07.2021
 */

package entity;


public class Author{
	private String author;

	public Author() {}
	public Author(String author) {
		this.author = author;
	}

	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	
	@Override
	public String toString() {
		return this.author;
	}

}

