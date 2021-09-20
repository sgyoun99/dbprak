/**
 * Classes needed to read the DVD-related data from file and
 * write it in the associated tables in DB
 * @version 03.06.2021
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

