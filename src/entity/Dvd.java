/**
 * entity-class for DVD
 * @version 21-09-23
 */
package entity;

import java.util.Set;

public class Dvd {

	private String item_id;
	private String format;
	private Short runningtime;
	private String regioncode;
	private Set actors;
	private Set creators;
	private Set directors;
	
	public Dvd() {}
	public Dvd(String item_id, String format, Short runningtime, String regioncode) {
		this.item_id = item_id;
		this.format = format;
		this.runningtime = runningtime;
		this.regioncode = regioncode;
	}


	public String getItem_id() {
		return item_id;
	}
	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		if(format == null) {
			this.format = "";
		} else {
			this.format = format;
		}
	}
	public Short getRunningtime() {
		if(runningtime == null) {
			return 0;
		} else {
			return runningtime;
		}
	}
	public void setRunningtime(String runningtime) {
		if(runningtime.length() == 0 || runningtime == null) {
			this.runningtime = 0;
		} else {
			this.runningtime = Short.valueOf(runningtime);
		}
	}
	public void setRunningtime(Short runningtime) {
		this.runningtime = runningtime;
	}
	public String getRegioncode() {
		if(regioncode == null) {
			return null;
		} else {
			return regioncode;
		}
	}
	public void setRegioncode(String regioncode) {
			this.regioncode = regioncode;
	}
	

	public Set getActors() {
		return this.actors;
	}
	public void setActors(Set actors) {
		this.actors = actors;
	}
	public Set getCreators() {
		return this.creators;
	}
	public void setCreators(Set creators) {
		this.creators = creators;
	}
	public Set getDirectors() {
		return this.directors;
	}
	public void setDirectors(Set directors) {
		this.directors = directors;
	}
}
