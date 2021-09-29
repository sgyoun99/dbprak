/**
 * entity-class for Actor
 * @version 21-09-23
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

