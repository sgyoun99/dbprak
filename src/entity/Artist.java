

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
    
}