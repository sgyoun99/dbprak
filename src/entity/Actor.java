/**
 * Classes needed to read the DVD-related data from file and
 * write it in the associated tables in DB
 * @version 03.06.2021
 */
package entity;


public class Actor{
	private String actor;

	public Actor() {}
	public Actor(String actor) {
		this.actor = actor;
	}

	public String getActor() {
		return actor;
	}

	public void setActor(String actor) {
		this.actor = actor;
	}

	@Override
	public String toString() {
		return this.actor;
	}
	
}

