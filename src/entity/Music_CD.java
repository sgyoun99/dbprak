package entity;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.w3c.dom.Node;

import JDBCTools.JDBCTool;
import XmlTools.XmlTool;
import exception.SQLKeyDuplicatedException;
import exception.XmlDataException;
import exception.XmlInvalidValueException;
import exception.XmlNoAttributeException;
import exception.XmlNullNodeException;
import exception.XmlValidationFailException;
import main.Config;
import main.CreateTables;
import main.DropTables;
import main.ErrType;
import main.ErrorLogger;


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
	
	public static Predicate<String> pred_title = title -> title != null && title.length() > 0; // Not Null
	public static Predicate<List<String>> pred_music_artist = artistList -> artistList != null || (artistList != null && artistList.size() > 0); //Not Null
	public static Predicate<Date> pred_release_date = date -> true; //will be tested in the setter

	public void testMusic_CD(Music_CD music_cd, List<String> artists) throws XmlValidationFailException, XmlNullNodeException {
		try {
			if(!Item.pred_item_id.test(music_cd.getItem_id())) {
				XmlInvalidValueException e = new XmlInvalidValueException("item_id Error (id does not exist): "+music_cd.getItem_id());
				e.setAttrName("item_id");
				throw e;
			}
			if(!pred_music_artist.test(artists)) {
				XmlInvalidValueException e = new XmlInvalidValueException("No artist found: "+ music_cd.getItem_id()); 
				e.setAttrName("artist");
				throw e;
			}
			if(!pred_release_date.test(music_cd.getRelease_date())) {
				XmlInvalidValueException e = new XmlInvalidValueException("publication date Error "+ music_cd.getRelease_date()); 
				e.setAttrName("publication_date)");
				throw e;
			}
		} catch (NullPointerException e) {
			throw new XmlNullNodeException();
		} catch (XmlInvalidValueException e) {
			throw new XmlValidationFailException(e);
		}
		
	}
	

	public void musicCdDresden() {
		String location = "Music_CD(Dresden)";
		XmlTool xt = new XmlTool();
		xt.loadXML(Config.DRESDEN_ENCODED);

		List<Node> musicItemNodeList = xt.getNodesByNameDFS(xt.getDocumentNode(), "item").stream().filter(itemNode -> {
			try {
				return xt.getAttributeValue(itemNode, "pgroup").equals("Music");
			} catch (XmlDataException e) {
			}
			return false;
		}).collect(Collectors.toList());

		musicItemNodeList.forEach(musicItemNode -> {
			String item_id = null;
			try {
				item_id = xt.getAttributeValue(musicItemNode, "asin");
			} catch (XmlNoAttributeException e1) {
				//unreachable
			}
			
			Music_CD music_cd = new Music_CD();
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
				Node releasedate = xt.getNodeByNameDFS(musicItemNode, "releasedate");
				music_cd.setRelease_date(xt.getTextContentOfLeafNode(releasedate));
				music_cd.setArtist(artistNameList.get(0));//to set artist attribute NOT NULL
				
				//Insert(Order important)
				this.insertArtist(location, item_id, artistNameList, musicItemNode);
				this.insertLabel(location, item_id, labelNameList, musicItemNode);
				this.insertMusic_CD(location, item_id, music_cd, musicItemNode);
				this.insertMusic_CD_Artist(location, item_id, artistNameList, musicItemNode);
				this.insertMusic_CD_Label(location, item_id, labelNameList, musicItemNode);
				this.insertTitle(location, item_id, titleNameList, music_cd, musicItemNode);
				
			} catch (XmlInvalidValueException e) {
				e.setLocation(location);
				e.setItem_id(item_id);
				ErrorLogger.write(e, xt.getNodeContentDFS(musicItemNode));
			} catch (XmlNoAttributeException e) {
				e.setLocation(location);
				e.setItem_id(item_id);
				ErrorLogger.write(e, xt.getNodeContentDFS(musicItemNode));
			} catch (IndexOutOfBoundsException ex) {
				XmlInvalidValueException e = new XmlInvalidValueException(location);
				e.setAttrName("music_cd");
				e.setItem_id(item_id);
				e.setLocation(location);
				e.setMessage("List Empty");
				ErrorLogger.write(e, xt.getNodeContentDFS(musicItemNode));
			} catch (Exception e) {
				ErrorLogger.write(location, item_id, ErrType.PROGRAM, "", e, xt.getNodeContentDFS(musicItemNode));
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
			} catch (XmlDataException e) {
			}
			return false;
		}).collect(Collectors.toList());

		musicItemNodeList.forEach(musicItemNode -> {
			String item_id = null;
			try {
				item_id = xt.getAttributeValue(musicItemNode, "asin");
			} catch (XmlNoAttributeException e1) {
				//unreachable
			}
			Music_CD music_cd = new Music_CD();
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
				Node releasedate = xt.getNodeByNameDFS(musicItemNode, "releasedate");
				music_cd.setRelease_date(xt.getTextContentOfLeafNode(releasedate));
				music_cd.setArtist(artistNameList.get(0));//to set artist attribute NOT NULL
				
				//Insert(Order important)
				this.insertArtist(location, item_id, artistNameList, musicItemNode);
				this.insertLabel(location, item_id, labelNameList, musicItemNode);
				this.insertMusic_CD(location, item_id, music_cd, musicItemNode);
				this.insertMusic_CD_Artist(location, item_id, artistNameList, musicItemNode);
				this.insertMusic_CD_Label(location, item_id, labelNameList, musicItemNode);
				this.insertTitle(location, item_id, titleNameList, music_cd, musicItemNode);
				
			} catch (XmlInvalidValueException e) {
				e.setLocation(location);
				e.setItem_id(item_id);
				ErrorLogger.write(e, xt.getNodeContentDFS(musicItemNode));
			} catch (XmlNoAttributeException e) {
				e.setLocation(location);
				e.setItem_id(item_id);
				ErrorLogger.write(e, xt.getNodeContentDFS(musicItemNode));
			} catch (IndexOutOfBoundsException ex) {
				XmlInvalidValueException e = new XmlInvalidValueException(location);
				e.setAttrName("music_cd");
				e.setItem_id(item_id);
				e.setLocation(location);
				e.setMessage("List Empty");
				ErrorLogger.write(e, xt.getNodeContentDFS(musicItemNode));
			} catch (Exception e) {
				ErrorLogger.write(location, item_id, ErrType.PROGRAM, "", e, xt.getNodeContentDFS(musicItemNode));
			}
		});
	}
	
	private void insertArtist(String location, String item_id, List<String> artistNameList, Node musicItemNode) {
		String locationSurfix = ".artist";
		String attrName = "artist";
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
						ErrorLogger.write(location+locationSurfix, item_id, ErrType.XML, attrName, e, xt.getNodeContentDFS(musicItemNode));
					} catch (SQLException ex) {
						if(ex.getMessage().contains("duplicate key value")) {
							SQLKeyDuplicatedException e = new SQLKeyDuplicatedException();
							e.setAttrName(attrName);
							e.setItem_id(item_id);
							e.setLocation(location+locationSurfix);
							e.setMessage("duplicate key value");
							ErrorLogger.write(e, xt.getNodeContentDFS(musicItemNode));
						} else {
							ErrorLogger.write(location+locationSurfix, item_id, ErrType.SQL, attrName, ex, xt.getNodeContentDFS(musicItemNode));
						}
						ErrorLogger.write(location+locationSurfix, item_id, ErrType.SQL, attrName, ex, xt.getNodeContentDFS(musicItemNode));
					}
				});
				ps.close();
			});
			
		} catch (IndexOutOfBoundsException ex) {
			XmlInvalidValueException e = new XmlInvalidValueException(location+locationSurfix);
			e.setAttrName("artist");
			e.setItem_id(item_id);
			e.setLocation(location+locationSurfix);
			e.setMessage("List Empty");
		} catch (SQLException ex) {
			if(ex.getMessage().contains("duplicate key value")) {
				SQLKeyDuplicatedException e = new SQLKeyDuplicatedException();
				e.setAttrName(attrName);
				e.setItem_id(item_id);
				e.setLocation(location+locationSurfix);
				e.setMessage("duplicate key value");
				ErrorLogger.write(e, xt.getNodeContentDFS(musicItemNode));
			} else {
				ErrorLogger.write(location+locationSurfix, item_id, ErrType.SQL, attrName, ex, xt.getNodeContentDFS(musicItemNode));
			}
			ErrorLogger.write(location+locationSurfix, item_id, ErrType.SQL, attrName, ex, xt.getNodeContentDFS(musicItemNode));
		}
	}
	private void insertLabel(String location, String item_id, List<String> labelNameList, Node musicItemNode) {
		String locationSurfix = ".label";
		String attrName = "label";
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
							ErrorLogger.write(location+locationSurfix, item_id, ErrType.XML, attrName, e, xt.getNodeContentDFS(musicItemNode));
					} catch (SQLException ex) {
						if(ex.getMessage().contains("duplicate key value")) {
							SQLKeyDuplicatedException e = new SQLKeyDuplicatedException();
							e.setAttrName(attrName);
							e.setItem_id(item_id);
							e.setLocation(location+locationSurfix);
							e.setMessage("duplicate key value");
							ErrorLogger.write(e, xt.getNodeContentDFS(musicItemNode));
						} else {
							ErrorLogger.write(location+locationSurfix, item_id, ErrType.SQL, attrName, ex, xt.getNodeContentDFS(musicItemNode));
						}
						ErrorLogger.write(location+locationSurfix, item_id, ErrType.SQL, attrName, ex, xt.getNodeContentDFS(musicItemNode));
					}
				});
				ps.close();
			});
			
			
		} catch (IndexOutOfBoundsException ex) {
			XmlInvalidValueException e = new XmlInvalidValueException(location);
			e.setAttrName(attrName);
			e.setItem_id(item_id);
			e.setLocation(location);
			e.setMessage("List Empty");
		} catch (SQLException e) {
			ErrorLogger.write(location+locationSurfix, item_id, ErrType.SQL, attrName, e, xt.getNodeContentDFS(musicItemNode));
		}	
	}
	private void insertMusic_CD(String location, String item_id, Music_CD music_cd, Node musicItemNode) {
		XmlTool xt = new XmlTool();
		try {
			
			JDBCTool.executeUpdate((con, st) -> {
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
		} catch (IndexOutOfBoundsException ex) {
			XmlInvalidValueException e = new XmlInvalidValueException(location);
			e.setAttrName("music_cd");
			e.setItem_id(item_id);
			e.setLocation(location);
			e.setMessage("List Empty");
			ErrorLogger.write(e, xt.getNodeContentDFS(musicItemNode));
		} catch (SQLException ex) {
			if(ex.getMessage().contains("duplicate key value")) {
				SQLKeyDuplicatedException e = new SQLKeyDuplicatedException();
				e.setAttrName("item_id");
				e.setItem_id(item_id);
				e.setLocation(location);
				e.setMessage("duplicate key value");
				ErrorLogger.write(e, xt.getNodeContentDFS(musicItemNode));
			} else {
				ErrorLogger.write(location, item_id, ErrType.SQL, "item_id", ex, xt.getNodeContentDFS(musicItemNode));
			}
		} catch (Exception e) {
			ErrorLogger.write(location, item_id, ErrType.PROGRAM, "", e, xt.getNodeContentDFS(musicItemNode));	
		}
	}

	private void insertMusic_CD_Artist(String location, String item_id, List<String> artistNameList, Node musicItemNode) {
		String locationSurfix = ".artist";
		String attrName = "artist";
		XmlTool xt = new XmlTool();
		//Artist
		try {
			
			JDBCTool.executeUpdateAutoCommitOn((con, st) -> {
				String sql;
				PreparedStatement ps;
				sql = "INSERT INTO public.music_cd_artist("
						+ "	item_id, artist)"
						+ "	VALUES (?, ?);";
				ps = con.prepareStatement(sql);	
				artistNameList.forEach(name -> {
					try {
						ps.setString(1, item_id);
						ps.setString(2, name);
						ps.executeUpdate();
					} catch (IndexOutOfBoundsException e) {
						ErrorLogger.write(location+locationSurfix, item_id, ErrType.XML, attrName, e, xt.getNodeContentDFS(musicItemNode));
					} catch (SQLException ex) {
						if(ex.getMessage().contains("duplicate key value")) {
							SQLKeyDuplicatedException e = new SQLKeyDuplicatedException();
							e.setAttrName(attrName);
							e.setItem_id(item_id);
							e.setLocation(location+locationSurfix);
							e.setMessage("duplicate key value");
							ErrorLogger.write(e, xt.getNodeContentDFS(musicItemNode));
						} else {
							ErrorLogger.write(location+locationSurfix, item_id, ErrType.SQL, attrName, ex, xt.getNodeContentDFS(musicItemNode));
						}
						ErrorLogger.write(location+locationSurfix, item_id, ErrType.SQL, attrName, ex, xt.getNodeContentDFS(musicItemNode));
					}
				});
				ps.close();
			});
			
		} catch (IndexOutOfBoundsException ex) {
			XmlInvalidValueException e = new XmlInvalidValueException(location+locationSurfix);
			e.setAttrName("artist");
			e.setItem_id(item_id);
			e.setLocation(location+locationSurfix);
			e.setMessage("List Empty");
		} catch (SQLException ex) {
			if(ex.getMessage().contains("duplicate key value")) {
				SQLKeyDuplicatedException e = new SQLKeyDuplicatedException();
				e.setAttrName(attrName);
				e.setItem_id(item_id);
				e.setLocation(location+locationSurfix);
				e.setMessage("duplicate key value");
				ErrorLogger.write(e, xt.getNodeContentDFS(musicItemNode));
			} else {
				ErrorLogger.write(location+locationSurfix+"5", item_id, ErrType.SQL, attrName, ex, xt.getNodeContentDFS(musicItemNode));
			}
			ErrorLogger.write(location+locationSurfix+"6", item_id, ErrType.SQL, attrName, ex, xt.getNodeContentDFS(musicItemNode));
		}
	}	
	
	private void insertMusic_CD_Label(String location, String item_id, List<String> labelNameList, Node musicItemNode) {
		String locationSurfix = ".label";
		String attrName = "label";
		XmlTool xt = new XmlTool();
		//Label
		try {

			JDBCTool.executeUpdateAutoCommitOn((con, st) -> {
				String sql;
				PreparedStatement ps;
				sql = "INSERT INTO public.music_cd_label("
						+ "	item_id, label)"
						+ "	VALUES (?, ?);";
				ps = con.prepareStatement(sql);	
				labelNameList.forEach(name -> {
					try {
						ps.setString(1, item_id);
						ps.setString(2, name);
						ps.executeUpdate();
					} catch (IndexOutOfBoundsException e) {
							ErrorLogger.write(location+locationSurfix, item_id, ErrType.XML, attrName, e, xt.getNodeContentDFS(musicItemNode));
					} catch (SQLException ex) {
						if(ex.getMessage().contains("duplicate key value")) {
							SQLKeyDuplicatedException e = new SQLKeyDuplicatedException();
							e.setAttrName(attrName);
							e.setItem_id(item_id);
							e.setLocation(location+locationSurfix);
							e.setMessage("duplicate key value");
							ErrorLogger.write(e, xt.getNodeContentDFS(musicItemNode));
						} else {
							ErrorLogger.write(location+locationSurfix, item_id, ErrType.SQL, attrName, ex, xt.getNodeContentDFS(musicItemNode));
						}
						ErrorLogger.write(location+locationSurfix, item_id, ErrType.SQL, attrName, ex, xt.getNodeContentDFS(musicItemNode));
					}
				});
				ps.close();
			});
			
		} catch (IndexOutOfBoundsException ex) {
			XmlInvalidValueException e = new XmlInvalidValueException(location+locationSurfix);
			e.setAttrName(attrName);
			e.setItem_id(item_id);
			e.setLocation(location+locationSurfix);
			e.setMessage("List Empty");
		} catch (SQLException e) {
			ErrorLogger.write(location+locationSurfix, item_id, ErrType.SQL, attrName, e, xt.getNodeContentDFS(musicItemNode));
		}	
	}
	
	private void insertTitle(String location, String item_id, List<String> titleNameList, Music_CD music_cd, Node musicItemNode) {
		String locationSurfix = ".title";
		String attrName = "title";
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
						ErrorLogger.write(location+locationSurfix, item_id, ErrType.XML, attrName, e, xt.getNodeContentDFS(musicItemNode));
					} catch (SQLException ex) {
						if(ex.getMessage().contains("duplicate key value")) {
							SQLKeyDuplicatedException e = new SQLKeyDuplicatedException();
							e.setAttrName(attrName);
							e.setItem_id(item_id);
							e.setLocation(location+locationSurfix);
							e.setMessage("duplicate key value");
							ErrorLogger.write(e, xt.getNodeContentDFS(musicItemNode));
						} else {
							ErrorLogger.write(location+locationSurfix, item_id, ErrType.SQL, attrName, ex, xt.getNodeContentDFS(musicItemNode));
						}
						ErrorLogger.write(location+locationSurfix, item_id, ErrType.SQL, attrName, ex, xt.getNodeContentDFS(musicItemNode));
					}
				});
				ps.close();
			});
			
		} catch (IndexOutOfBoundsException ex) {
			XmlInvalidValueException e = new XmlInvalidValueException(location+locationSurfix);
			e.setAttrName("title");
			e.setItem_id(item_id);
			e.setLocation(location+locationSurfix);
			e.setMessage("List Empty");
		} catch (SQLException e) {
			ErrorLogger.write(location+locationSurfix, item_id, ErrType.SQL, "", e, xt.getNodeContentDFS(musicItemNode));
		}	
	}
	
	
	

	
	
	public void handleDuplicatedPK() {
		System.out.println("need to make handleDuplicatedPK()");
	}
	
	
	
	
	
	public static void main(String[] args) {
		try {
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
		music.musicCdDresden();
		music.musicCdLeipzig();
		} catch (RuntimeException e) {
			System.out.println(e);
		} catch (Exception e) {
			System.out.println(e);
		}
		
				
		
	}
	
	
}
