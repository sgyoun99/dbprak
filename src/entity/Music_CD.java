/**
 * entity-class for Music_CD
 * @version 21-09-28
 */

package entity;

import java.util.Set;
import java.sql.Date;
import exception.XmlInvalidValueException;
import java.util.Iterator;

public class Music_CD {
	private String item_id;
	private Date release_date;
	
	private Set artists;
	private Set labels;
	private Set titles;

	public Music_CD() {}
	public Music_CD(String item_id, Date release_date) {
		this.item_id = item_id;
		this.release_date = release_date;
	}

	public String getItem_id() {
		return item_id;
	}
	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}
	public Date getRelease_date() {
		return release_date;
	}
	public void setRelease_date(Date release_date) {
		this.release_date = release_date;
	}	
	public void setRelease_date(String release_date) throws XmlInvalidValueException{
		if(release_date != null) {
			try {
				this.release_date = Date.valueOf(release_date);
			} catch (IllegalArgumentException e) {
				XmlInvalidValueException ex = new XmlInvalidValueException("date is not in the form yyyy-mm-dd");
				ex.setAttrName("releasedate");
				throw ex;
			}
		}
	}

	public Set getArtists() {
		return this.artists;
	}
	public void setArtists(Set artists) {
		this.artists = artists;
	}
	public Set getLabels() {
		return this.labels;
	}
	public void setLabels(Set labels) {
		this.labels = labels;
	}
	public Set getTitles() {
		return this.titles;
	}
	public void setTitles(Set titles) {
		this.titles = titles;
	}
	
	@Override
	public String toString() {
		String answer = this.item_id + " " + this.release_date + " ";
		Iterator<Set> i = this.artists.iterator();
		while (i.hasNext()) {answer += (i.next() + " ");}
		i = this.labels.iterator();
		while (i.hasNext()) {answer += (i.next() + " ");}
		answer += this.titles.size();
		return answer;
	}
	
	
}
