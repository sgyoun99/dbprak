package entity;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.w3c.dom.Node;

import JDBCTools.JDBCTool;
import XmlTools.XmlTool;
import exception.XmlDataException;
import main.Config;
import main.CreateTables;
import main.DropTables;
import main.ErrType;
import main.ErrorLogger;

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
	
	public void setRelease_date(String release_date) throws IllegalArgumentException{
		if(release_date != null) {
			this.release_date = Date.valueOf(release_date);
		}
	}
	public void musicCdDresden() {
		String location = "Music_CD(Dresden)";
		XmlTool xt = new XmlTool();
		xt.loadXML(Config.DRESDEN_ENCODED);

		List<Node> musicItemNodeList = xt.getNodesByNameDFS(xt.getDocumentNode(), "item").stream().filter(itemNode -> {
			try {
				return xt.getAttributeValue(itemNode, "pgroup").equals("Music");
				//test
				/*
				return xt.getAttributeValue(itemNode, "pgroup").equals("Music") 
						&& xt.getAttributeValue(itemNode, "asin").equals("B000127Z4G");
				 */
			} catch (XmlDataException e) {
			}
			return false;
		}).collect(Collectors.toList());

		musicItemNodeList.forEach(musicItemNode -> {
			Music_CD music_cd = new Music_CD();
			/*
			Artist artist = new Artist();
			Label label = new Label();
			Title title = new Title();
			 */
			List<String> artistNameList = new ArrayList<>();
			List<String> labelNameList = new ArrayList<>();
			List<String> titleNameList = new ArrayList<>();

			try {
				//xml

				List<Node> artistNodes = xt.getNodesByNameDFS(musicItemNode, "artist");
				artistNodes.forEach(artistNode -> artistNameList.add(xt.getTextContentOfLeafNode(artistNode)));
				List<Node> creatorNodes = xt.getNodesByNameDFS(musicItemNode, "creator");
				creatorNodes.forEach(creatorNode -> artistNameList.add(xt.getTextContentOfLeafNode(creatorNode)));
				
				List<Node> labelNodes = xt.getNodesByNameDFS(musicItemNode, "label");
				labelNodes.forEach(labelNode -> labelNameList.add(xt.getTextContentOfLeafNode(labelNode)));
				
				List<Node> titleNodes = xt.getNodesByNameDFS(musicItemNode, "title");
				titleNodes.forEach(titleNode -> titleNameList.add(xt.getTextContentOfLeafNode(titleNode)));
				
				music_cd.setItem_id(xt.getAttributeValue(musicItemNode, "asin"));
				Node releasedate = xt.getNodebyNameDFS(musicItemNode, "releasedate");
				music_cd.setRelease_date(xt.getTextContentOfLeafNode(releasedate));
				music_cd.setArtist(artistNameList.get(0));//to set artist attribute NOT NULL
				
				//Insert
				this.insertArtist(location, artistNameList, musicItemNode);
				this.insertLabel(location, labelNameList, musicItemNode);
				this.insertMusic_cd(location, music_cd, musicItemNode);
				this.insertTitle(location, titleNameList, music_cd, musicItemNode);
				
			} catch (IllegalArgumentException e) {
				ErrorLogger.write(location, ErrType.PROGRAM , e, xt.getNodeContentDFS(musicItemNode));
			} catch (XmlDataException e) {
				ErrorLogger.write(location, ErrType.XML, e, xt.getNodeContentDFS(musicItemNode));
			} catch (Exception e) {
				ErrorLogger.write(location, ErrType.PROGRAM, e, xt.getNodeContentDFS(musicItemNode));
			}
		});
	}	
	
	public void musicCdLeipzig() {
		String location = "Music_CD(Leipzig)";
		XmlTool xt = new XmlTool();
		xt.loadXML(Config.LEIPZIG);

		List<Node> musicItemNodeList = xt.getNodesByNameDFS(xt.getDocumentNode(), "item").stream().filter(itemNode -> {
			try {
				return xt.getAttributeValue(itemNode, "pgroup").equals("Music");
				//test
				/*
				return xt.getAttributeValue(itemNode, "pgroup").equals("Music") 
						&& xt.getAttributeValue(itemNode, "asin").equals("B000127Z4G");
				 */
			} catch (XmlDataException e) {
			}
			return false;
		}).collect(Collectors.toList());

		musicItemNodeList.forEach(musicItemNode -> {
			Music_CD music_cd = new Music_CD();
			/*
			Artist artist = new Artist();
			Label label = new Label();
			Title title = new Title();
			 */
			List<String> artistNameList = new ArrayList<>();
			List<String> labelNameList = new ArrayList<>();
			List<String> titleNameList = new ArrayList<>();

			try {
				//xml

				List<Node> artistNodes = xt.getNodesByNameDFS(musicItemNode, "artist");
				artistNodes.forEach(artistNode -> {
					try {
						artistNameList.add(xt.getAttributeValue(artistNode, "name"));
					} catch (XmlDataException e) {
						//do nothing
					}
				});
				List<Node> creatorNodes = xt.getNodesByNameDFS(musicItemNode, "creator");
				creatorNodes.forEach(creatorNode -> {
					try {
						artistNameList.add(xt.getAttributeValue(creatorNode,"name"));
					} catch (XmlDataException e) {
						//do nothing
					}
				});
				
				List<Node> labelNodes = xt.getNodesByNameDFS(musicItemNode, "label");
				labelNodes.forEach(labelNode -> {
					try {
						labelNameList.add(xt.getAttributeValue(labelNode, "name"));
					} catch (XmlDataException e) {
						//do nothing
					}
				});
				
				List<Node> titleNodes = xt.getNodesByNameDFS(musicItemNode, "title");
				titleNodes.forEach(titleNode -> titleNameList.add(xt.getTextContentOfLeafNode(titleNode)));
				
				music_cd.setItem_id(xt.getAttributeValue(musicItemNode, "asin"));
				Node releasedate = xt.getNodebyNameDFS(musicItemNode, "releasedate");
				music_cd.setRelease_date(xt.getTextContentOfLeafNode(releasedate));
				music_cd.setArtist(artistNameList.get(0));//to set artist attribute NOT NULL
				
				//Insert
				this.insertArtist(location, artistNameList, musicItemNode);
				this.insertLabel(location, labelNameList, musicItemNode);
				this.insertMusic_cd(location, music_cd, musicItemNode);
				this.insertTitle(location, titleNameList, music_cd, musicItemNode);
				
			} catch (IllegalArgumentException e) {
				ErrorLogger.write(location, ErrType.PROGRAM , e, xt.getNodeContentDFS(musicItemNode));
			} catch (XmlDataException e) {
				ErrorLogger.write(location, ErrType.XML, e, xt.getNodeContentDFS(musicItemNode));
			} catch (Exception e) {
				ErrorLogger.write(location, ErrType.PROGRAM, e, xt.getNodeContentDFS(musicItemNode));
			}
		});
	}
	
	private void insertMusic_cd(String location, Music_CD music_cd, Node musicItemNode) {
		XmlTool xt = new XmlTool();
		try {
			
			JDBCTool.executeUpdateAutoCommitOn((con, st) -> {
				String sql;
				PreparedStatement ps;
				sql = "INSERT INTO public.music_cd("
						+ "	item_id, artist, release_date)"
						+ "	VALUES (?, ?, ?);";
				ps = con.prepareStatement(sql);
				ps.setString(1, music_cd.getItem_id());
				ps.setString(2, music_cd.getArtist());
				ps.setDate(3, music_cd.getRelease_date());
				ps.executeUpdate();
				ps.close();
			});
		} catch (SQLException e) {
			if(e.getMessage().contains("duplicate key value")) {
				this.handleDuplicatedPK();
			} else {
				ErrorLogger.write(location, ErrType.SQL, e, xt.getNodeContentDFS(musicItemNode));
			}
		} catch (Exception e) {
			ErrorLogger.write(location, ErrType.PROGRAM, e, xt.getNodeContentDFS(musicItemNode));	
		}
	}

	private void insertArtist(String location, List<String> artistNameList, Node musicItemNode) {
		String locationSurfix = ".artist";
		XmlTool xt = new XmlTool();
		//Artist
		try {
			JDBCTool.executeUpdateAutoCommitOn((con, st) -> {
				String sql;
				PreparedStatement ps;
				sql = "INSERT INTO public.artist("
						+ "	artist)"
						+ "	VALUES (?);";
				ps = con.prepareStatement(sql);	
				artistNameList.forEach(name -> {
					try {
						ps.setString(1, name);
						ps.executeUpdate();
					} catch (IndexOutOfBoundsException e) {
							ErrorLogger.write(location+locationSurfix, ErrType.XML, e, xt.getNodeContentDFS(musicItemNode));
					} catch (SQLException e) {
						if(!e.getMessage().contains("duplicate key value")) {
							ErrorLogger.write(location+locationSurfix, ErrType.SQL, e, xt.getNodeContentDFS(musicItemNode));
						}
					}
				});
				ps.close();
			});
		} catch (SQLException e) {
			ErrorLogger.write(location+locationSurfix, ErrType.SQL, e, xt.getNodeContentDFS(musicItemNode));
		}
	}
	
	private void insertLabel(String location, List<String> labelNameList, Node musicItemNode) {
		String locationSurfix = ".label";
		XmlTool xt = new XmlTool();
		//Label
		try {
			JDBCTool.executeUpdateAutoCommitOn((con, st) -> {
				String sql;
				PreparedStatement ps;
				sql = "INSERT INTO public.label("
						+ "	label)"
						+ "	VALUES (?);";
				ps = con.prepareStatement(sql);	
				labelNameList.forEach(name -> {
					try {
						ps.setString(1, name);
						ps.executeUpdate();
					} catch (IndexOutOfBoundsException e) {
							ErrorLogger.write(location+locationSurfix, ErrType.XML, e, xt.getNodeContentDFS(musicItemNode));
					} catch (SQLException e) {
						if(!e.getMessage().contains("duplicate key value")) {
							ErrorLogger.write(location+locationSurfix, ErrType.SQL, e, xt.getNodeContentDFS(musicItemNode));
						}
					}
				});
				ps.close();
			});
		} catch (IndexOutOfBoundsException e) {
		} catch (SQLException e) {
			ErrorLogger.write(location+locationSurfix, ErrType.SQL, e, xt.getNodeContentDFS(musicItemNode));
		}	
	}
	
	private void insertTitle(String location, List<String> titleNameList, Music_CD music_cd, Node musicItemNode) {
		String locationSurfix = ".title";
		XmlTool xt = new XmlTool();
		//Title
		try {
			JDBCTool.executeUpdateAutoCommitOn((con, st) -> {
				String sql;
				PreparedStatement ps;
				sql = "INSERT INTO public.title("
						+ "	item_id, title)"
						+ "	VALUES (?, ?);";
				ps = con.prepareStatement(sql);	
				titleNameList.forEach(name -> {
					try {
						ps.setString(1, music_cd.getItem_id());
						ps.setString(2, name);
						ps.executeUpdate();
					} catch (IndexOutOfBoundsException e) {
							ErrorLogger.write(location+locationSurfix, ErrType.XML, e, xt.getNodeContentDFS(musicItemNode));
					} catch (SQLException e) {
						if(!e.getMessage().contains("duplicate key value")) {
							ErrorLogger.write(location+locationSurfix, ErrType.SQL, e, xt.getNodeContentDFS(musicItemNode));
						}
					}
				});
				ps.close();
			});
		} catch (SQLException e) {
			ErrorLogger.write(location+".music_cd", ErrType.SQL, e, xt.getNodeContentDFS(musicItemNode));
		}	
	}
	
	
	

	
	
	public void handleDuplicatedPK() {
		System.out.println("need to make handleDuplicatedPK()");
	}
	
	
	
	
	
	public static void main(String[] args) throws Exception{
		DropTables.dropTable("Errors");
		CreateTables.createTable("Errors");

		DropTables.dropTable(CreateTables.Music_CD_Label);
		DropTables.dropTable(CreateTables.Music_CD_Artist);
		DropTables.dropTable(CreateTables.Title);
		DropTables.dropTable(CreateTables.Music_CD);
		DropTables.dropTable(CreateTables.Label);
		DropTables.dropTable(CreateTables.Artist);
		CreateTables.createTable(CreateTables.Artist);
		CreateTables.createTable(CreateTables.Label);
		CreateTables.createTable(CreateTables.Music_CD);
		CreateTables.createTable(CreateTables.Title);
		CreateTables.createTable(CreateTables.Music_CD_Artist);
		CreateTables.createTable(CreateTables.Music_CD_Label);
		
		Music_CD music = new Music_CD();
//		music.musicCdDresden();
		music.musicCdLeipzig();
		
				
		
	}
	
	
}
