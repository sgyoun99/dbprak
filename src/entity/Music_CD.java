package entity;

import java.sql.Date;
import java.sql.PreparedStatement;

import org.w3c.dom.Node;

import JDBCTools.JDBCTool;
import XmlTools.XmlDataException;
import XmlTools.XmlTool;
import main.CreateTables;
import main.DropTables;

class Artist {
	private String artist;

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}
}
	
class Label {
	private String label;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
}

class Title {
	private String title;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
}

public class Music_CD {
	private String item_id;
	private String artist;
	private Date release_date;
	public String getItem_id() {
		return item_id;
	}
	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public Date getRelease_date() {
		return release_date;
	}
	public void setRelease_date(Date release_date) {
		this.release_date = release_date;
	}
	
	public void setRelease_date(String publication_date) throws IllegalArgumentException{
		if(publication_date != null) {
			this.release_date = Date.valueOf(publication_date);
		}
	}
	
	public void cd(Music_CD music_cd, Node musicItemNode, PreparedStatement ps, XmlTool xt) {
		Node artist = xt.getNodebyNameDFS(musicItemNode, "artist");
		String artistName;
		try {
			artistName = xt.getAttributeValue(artist, "name");
		} catch (XmlDataException e) {
			artistName = artist.getTextContent();
		}
		music_cd.setArtist(artistName);
		
		
		
	}
	
	
	

	
	
	public void musicCD() {
		try {
			JDBCTool.executeUpdateAutoCommit((con, st) -> {
				String sql;
				PreparedStatement ps;
				sql = "INSERT INTO public.creator("
						+ "	creator)"
						+ "	VALUES (?);";
				ps = con.prepareStatement(sql);
//				ps.setString(1, creator.getPublisher());
				ps.executeUpdate();
				ps.close();
				
				sql = "INSERT INTO public.music_cd("
						+ "	item_id, artist, release_date)"
						+ "	VALUES (?, ?, ?);";
				ps = con.prepareStatement(sql);
//				ps.setString(1, getItem_id());
//				ps.setString(2, creator.getPublisher());
				ps.executeUpdate();
				ps.close();
				
				
				
			});
		} catch (Exception e) {

		}
			
	}
	
	
	
	
	
	public static void main(String[] args) throws Exception{
		DropTables.dropTable("Errors");
		CreateTables.createTable("Errors");

		DropTables.dropTable("Music_CD_Label");
		DropTables.dropTable("Music_CD_Artist");
		DropTables.dropTable("Titel");
		DropTables.dropTable("Music_CD");
		DropTables.dropTable("Label");
		DropTables.dropTable("Artist");
		CreateTables.createTable("Artist");
		CreateTables.createTable("Label");
		CreateTables.createTable("Music_CD");
		CreateTables.createTable("Titel");
		CreateTables.createTable("Music_CD_Artist");
		CreateTables.createTable("Music_CD_Label");
		
		Music_CD music = new Music_CD();
		
				
		
	}
	
	
}
