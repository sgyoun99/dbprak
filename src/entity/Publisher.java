/**
 * entity-class for Publisher
 * @version 21-09-23
 */

package entity;


public class Publisher{
	private String publisher;

	public Publisher() {}
	public Publisher(String publisher) {
		this.publisher = publisher;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	@Override
	public String toString(){
		return this.publisher;
	}
	
}