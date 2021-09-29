/**
 * entity-class for Artist
 * @version 21-09-23
 */

package entity;

public class Artist{
    private String artist;

    public Artist() {}
    public Artist(String artist) {
        this.artist = artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
    public String getArtist() {
        return this.artist;
    }

    @Override
    public String toString(){
        return this.artist;
    }
    
    @Override
    public boolean equals(Object obj) {
    	return this.artist.equals(((Artist)obj).getArtist());
    }
    
    @Override
    public int hashCode() {
    	return this.artist.hashCode();
    }
}