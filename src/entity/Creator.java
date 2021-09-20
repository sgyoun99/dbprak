/**
 * Classes needed to read the DVD-related data from file and
 * write it in the associated tables in DB
 * @version 03.06.2021
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


