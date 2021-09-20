/**
 * classed needed to read all music_cd related data from file
 * and write to DB in the associated tables
 * @version 03.06.2021
 */
package entity;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.HashSet;

import org.w3c.dom.Node;

import org.hibernate.HibernateException; 
import org.hibernate.Session; 
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

//import JDBCTools.JDBCTool;
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


public class ManageMusic_CD {
	
	public void manageCDs(SessionFactory factory) {
		readCD(Config.DRESDEN_ENCODED, factory);
		readCD(Config.LEIPZIG, factory);
		System.out.println("\033[1;34m    *\033[35m*\033[33m*\033[32m* \033[91mCDs finished \033[32m*\033[33m*\033[35m*\033[34m*\033[0m");
	}

	private void addCD(Music_CD cd, SessionFactory factory) {
		Session session = factory.openSession();
		Transaction tx = null;
		
		try {
			tx = session.beginTransaction();
			session.save(cd); 
			tx.commit();
		} catch (HibernateException e) {
			if (tx!=null) {
				tx.rollback();
			}
			System.out.println("HibernateException for " + cd.getItem_id()); 
		} finally {
			session.close(); 
		}
	}
	
	/**
	 * check whether read-in data is valid
	 */
/*	public static Predicate<String> pred_title = title -> title != null && title.length() > 0; // Not Null
	public static Predicate<List<String>> pred_music_artist = artistList -> artistList != null || (artistList != null && artistList.size() > 0); //Not Null
	public static Predicate<Date> pred_release_date = date -> true; //will be tested in the setter

	public void testMusic_CD(Music_CD music_cd, List<String> artists) throws XmlValidationFailException, XmlNullNodeException {
		try {
			if(!Item.pred_item_id.test(music_cd.getItem_id())) {
				XmlInvalidValueException e = new XmlInvalidValueException("item_id Error (length not 10): \""+music_cd.getItem_id()+"\""); 
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
		
	}*/
	

	//TODO: Artist setzen in CD/NOT NUL constraint über music_cd_artist???
	private void readCD(String xmlPath, SessionFactory factory) {
		String location = "Music_CD(" + xmlPath + ")";
		//System.out.println(">> cd " + this.location + " ...");
		XmlTool xt = new XmlTool(xmlPath);
		xt.getNodesByNameDFS(xt.getDocumentNode(), "item").stream().filter(itemNode -> {
			try {
				if(xt.getAttributeValue(itemNode, "pgroup").equals("Music")) {
					return true;
				} else {
					return false;
				}
			} catch (XmlNoAttributeException e) {
				//do noting
			}
			return false;
		}).collect(Collectors.toList()).forEach(itemNode -> {
			//xml data
			Music_CD cd = new Music_CD();
			HashSet<Label> labelSet = new HashSet<Label>();
			HashSet<Artist> artistSet = new HashSet<Artist>();
			HashSet<Title> titleSet = new HashSet<Title>();
			try {
				cd.setItem_id(xt.getAttributeValue(itemNode, "asin"));
			} catch (XmlDataException e) {
				ErrorLogger.write(location, ErrType.XML, e, xt.getNodeContentDFS(itemNode));
			}
			
			//artist
			xt.visitChildElementNodesDFS(itemNode, (node, level) -> {										
				if(node.getNodeName().equals("artists") && !xt.isLeafElementNode(node)) {
					xt.visitChildElementNodesDFS(node, (nd, l) -> {
						Artist artist = new Artist();									
						if(nd.getNodeName().equals("artist")) {
							try {
								artist.setArtist(xt.getAttributeValue(nd, "name"));
							}catch(XmlDataException e) {
								artist.setArtist(xt.getTextContentOfLeafNode(nd));		//try catch falls Inhalt fehlt?										
							}finally{
								artistSet.add(artist);
								
							}
						}
					});
				}
			});
			//zT artists als creator drin!
			xt.visitChildElementNodesDFS(itemNode, (node, level) -> {										
				if(node.getNodeName().equals("creators") && !xt.isLeafElementNode(node)) {
					xt.visitChildElementNodesDFS(node, (nd, l) -> {
						Artist artist = new Artist();									
						if(nd.getNodeName().equals("creator")) {
							try {
								artist.setArtist(xt.getAttributeValue(nd, "name"));
							}catch(XmlDataException e) {
								artist.setArtist(xt.getTextContentOfLeafNode(nd));		//try catch falls Inhalt fehlt!								
							}finally{
								artistSet.add(artist);
								
							}
						}
					});
				}
			});
		
			//Label
			xt.visitChildElementNodesDFS(itemNode, (node, level) -> {										
				if(node.getNodeName().equals("labels") && !xt.isLeafElementNode(node)) {
					xt.visitChildElementNodesDFS(node, (nd, l) -> {
						Label label = new Label();									
						if(nd.getNodeName().equals("label")) {
							try {
								label.setLabel(xt.getAttributeValue(nd, "name"));
							}catch(XmlDataException e) {
								label.setLabel(xt.getTextContentOfLeafNode(nd));	//try catch falls Inhalt fehlt!										
							}finally{
								labelSet.add(label);
							}
						}
					});
				}
			});

			//titles
			xt.visitChildElementNodesDFS(itemNode, (node, level) -> {										
				if(node.getNodeName().equals("tracks") && !xt.isLeafElementNode(node)) {
					xt.visitChildElementNodesDFS(node, (nd, l) -> {
						Title title = new Title();		
						title.setItem_id(cd.getItem_id());							
						if(nd.getNodeName().equals("title")) {
							try {
								title.setTitle(xt.getAttributeValue(nd, "name"));
							}catch(XmlDataException e) {
								title.setTitle(xt.getTextContentOfLeafNode(nd));		//try catch falls Inhalt fehlt!										
							}finally{
								titleSet.add(title);
							}
						}
					});
				}
			});


			Node musicspec = xt.getNodeByNameDFS(itemNode, "musicspec");
			try {
				Node releasedate = xt.getNodeByNameDFS(musicspec, "releasedate");
				cd.setRelease_date(xt.getNodeContentForceNullable(releasedate));
				
				
				cd.setArtists(artistSet);
				cd.setLabels(labelSet);
				cd.setTitles(titleSet);	
				

				//test
				
				//insert				
				this.addCD(cd, factory);
							
				
			} catch (IllegalArgumentException e) {
				ErrorLogger.write(location, cd.getItem_id(), ErrType.PROGRAM, "" ,e, xt.getNodeContentDFS(itemNode));
			}/* catch (XmlValidationFailException e) {
				e.setLocation(location);
				e.setItem_id(cd.getItem_id());
				ErrorLogger.write(e, xt.getNodeContentDFS(itemNode));
			} */catch (XmlDataException e) {
				e.setLocation(location);
				e.setItem_id(cd.getItem_id());
				ErrorLogger.write(e, xt.getNodeContentDFS(itemNode));
			} /*catch (SQLException ex) {
				if(ex.getMessage().contains("duplicate key value")) {

					ErrorLogger.checkDuplicate(book);
					
					SQLKeyDuplicatedException e = new SQLKeyDuplicatedException();
					e.setAttrName("item_id");
					e.setItem_id(book.getItem_id());
					e.setLocation(location);
					e.setMessage("duplicate key value");
					ErrorLogger.write(e, xt.getNodeContentDFS(itemNode));
				} else {
					ErrorLogger.write(location, book.getItem_id(), ErrType.SQL, "", ex, xt.getNodeContentDFS(itemNode));
				}
			}*/ catch (Exception e) {
				ErrorLogger.write(location, cd.getItem_id(), ErrType.PROGRAM, "", e, xt.getNodeContentDFS(itemNode));
			}
			
		});
	}	



	/**
	 * read music_cd-data from file dresden.xml and write to DB tables
	 * extra method required because of differente file structures
	 */
/*	public void musicCdDresden() {
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
				//List<Node> creatorNodes = xt.getNodesByNameDFS(musicItemNode, "creator");
				//creatorNodes.forEach(creatorNode -> artistNameList.add(xt.getTextContentOfLeafNode(creatorNode)));
				
				List<Node> labelNodes = xt.getNodesByNameDFS(musicItemNode, "label");
				labelNodes.forEach(labelNode -> labelNameList.add(xt.getTextContentOfLeafNode(labelNode)));
				
				List<Node> titleNodes = xt.getNodesByNameDFS(musicItemNode, "title");
				titleNodes.forEach(titleNode -> titleNameList.add(xt.getTextContentOfLeafNode(titleNode)));
				
				music_cd.setItem_id(xt.getAttributeValue(musicItemNode, "asin"));
				Node releasedate = xt.getNodeByNameDFS(musicItemNode, "releasedate");
				music_cd.setRelease_date(xt.getTextContentOfLeafNode(releasedate));
				music_cd.setArtist(artistNameList.get(0));//to set artist attribute NOT NULL
				
				//Insert into DB (Order important)
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
	
	/**
	 * read music-cd-data from file leipzig.xml and write to DB
	 * extra method required because of different file structures
	 */
/*	public void musicCdLeipzig() {
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
				/*List<Node> creatorNodes = xt.getNodesByNameDFS(musicItemNode, "creator");
				creatorNodes.forEach(creatorNode -> {
					try {
						artistNameList.add(xt.getAttributeValue(creatorNode,"name"));
					} catch (XmlDataException e) {
						//do nothing
					}
				});*/
				
/*				List<Node> labelNodes = xt.getNodesByNameDFS(musicItemNode, "label");
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
				
				//Insert into DB (Order important)
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
	
	/**
	 * method to write artist-data to DB table "artist"
	 * @param location
	 * @param item_id
	 * @param artistNameList
	 * @param musicItemNode
	 */
/*	private void insertArtist(String location, String item_id, List<String> artistNameList, Node musicItemNode) {
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

	/**
	 * method to write label_data to DB table "label"
	 * @param location
	 * @param item_id
	 * @param labelNameList
	 * @param musicItemNode
	 */
/*	private void insertLabel(String location, String item_id, List<String> labelNameList, Node musicItemNode) {
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

	/**
	 * method to write music_cd-data to DB table "music_cd"
	 * @param location
	 * @param item_id
	 * @param music_cd
	 * @param musicItemNode
	 */
/*	private void insertMusic_CD(String location, String item_id, Music_CD music_cd, Node musicItemNode) {
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

	/**
	 * method to write artist/music_cd-data to DB table "music_cd_artist"
	 * @param location
	 * @param item_id
	 * @param artistNameList
	 * @param musicItemNode
	 */
/*	private void insertMusic_CD_Artist(String location, String item_id, List<String> artistNameList, Node musicItemNode) {
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
	
	/**
	 * method to write label/music_cd-data to DB table "music_cd_label"
	 * @param location
	 * @param item_id
	 * @param labelNameList
	 * @param musicItemNode
	 */
/*	private void insertMusic_CD_Label(String location, String item_id, List<String> labelNameList, Node musicItemNode) {
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
	
	/**
	 * method to write title-data to DB table "title"
	 * @param location
	 * @param item_id
	 * @param titleNameList
	 * @param music_cd
	 * @param musicItemNode
	 */
/*	private void insertTitle(String location, String item_id, List<String> titleNameList, Music_CD music_cd, Node musicItemNode) {
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
	
		
	/**
	 * not used for main_program
	 */
	public static void main(String[] args) {
			
				
		
	}
	
	
}
