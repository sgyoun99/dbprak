/**
 * entity-class for Creator
 * @version 21-09-23
 */
package entity;

public class Creator{
	private String creator;

	public Creator() {}
	public Creator(String creator) {
		this.creator = creator;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	@Override
	public String toString() {
		return this.creator;
	}
	
}


