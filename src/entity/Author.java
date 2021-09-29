/**
 * entity-class for Author
 * @version 21-09-23
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

