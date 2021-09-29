/**
 * entity-class for Director
 * @version 21-09-23
 */
package entity;


public class Director{
	private String director;

	public Director() {}
	public Director(String director) {
		this.director = director;
	}

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	@Override
	public String toString() {
		return this.director;
	}
	
}

