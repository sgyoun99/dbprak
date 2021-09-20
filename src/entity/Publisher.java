/**
 * Classes needed to read for Books all data from file
 * and write it in the associated tables
 * @version 03.07.2021
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