/**
 * classed needed to read all music_cd related data from file
 * and write to DB in the associated tables
 * @version 21-09-23
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
	
	/**
	 * method managing reading cd-data from file and adding it to the DB
	 */
	public void manageCDs(SessionFactory factory) {
		readCD(Config.DRESDEN_ENCODED, factory);
		readCD(Config.LEIPZIG, factory);
		System.out.println("\033[1;34m*\033[35m*\033[33m*\033[32m*\033[91mCDs finished \033[32m*\033[33m*\033[35m*\033[34m*\033[0m");
	}

	/**
	 * method adding a cd to the DB
	 */
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
	public static Predicate<String> pred_title = title -> title != null && title.length() > 0; // Not Null
	public static Predicate<List<String>> pred_music_artist = artistList -> artistList != null || (artistList != null && artistList.size() > 0); //Not Null
	public static Predicate<Date> pred_release_date = date -> true; //will be tested in the setter

	public void testMusic_CD(Music_CD music_cd, List<String> artists) throws XmlValidationFailException, XmlNullNodeException {
		try {
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
	

	/**
	 * method reading cd-data from file and calling on addCD() to add the data to the DB
	 */
	private void readCD(String xmlPath, SessionFactory factory) {
		String location = "Music_CD(" + xmlPath + ")";
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
			List<String> artistList = new ArrayList<>();
			try {
				cd.setItem_id(xt.getAttributeValue(itemNode, "asin"));
			} catch (XmlDataException e) {
				ErrorLogger.write(location, ErrType.XML, e, xt.getNodeContentDFS(itemNode));
			}
			
			
			//get the artists
			xt.visitChildElementNodesDFS(itemNode, (node, level) -> {										
				if(node.getNodeName().equals("artists") && !xt.isLeafElementNode(node)) {
					xt.visitChildElementNodesDFS(node, (nd, l) -> {
						Artist artist = new Artist();									
						if(nd.getNodeName().equals("artist")) {
							try {
								artist.setArtist(xt.getAttributeValue(nd, "name"));
							}catch(XmlDataException e) {
								artist.setArtist(xt.getTextContentOfLeafNode(nd));												
							}finally{
								artistSet.add(artist);
								artistList.add(artist.getArtist());
							}
						}
					});
				}
			});
			//zT "creator" als artist => get artists
			xt.visitChildElementNodesDFS(itemNode, (node, level) -> {										
				if(node.getNodeName().equals("creators") && !xt.isLeafElementNode(node)) {
					xt.visitChildElementNodesDFS(node, (nd, l) -> {
						Artist artist = new Artist();									
						if(nd.getNodeName().equals("creator")) {
							try {
								artist.setArtist(xt.getAttributeValue(nd, "name"));
							}catch(XmlDataException e) {
								artist.setArtist(xt.getTextContentOfLeafNode(nd));									
							}finally{
								artistSet.add(artist);
								artistList.add(artist.getArtist());
							}
						}
					});
				}
			});
		
			//get the labels
			xt.visitChildElementNodesDFS(itemNode, (node, level) -> {										
				if(node.getNodeName().equals("labels") && !xt.isLeafElementNode(node)) {
					xt.visitChildElementNodesDFS(node, (nd, l) -> {
						Label label = new Label();									
						if(nd.getNodeName().equals("label")) {
							try {
								label.setLabel(xt.getAttributeValue(nd, "name"));
							}catch(XmlDataException e) {
								label.setLabel(xt.getTextContentOfLeafNode(nd));											
							}finally{
								labelSet.add(label);
							}
						}
					});
				}
			});

			//get the titles
			xt.visitChildElementNodesDFS(itemNode, (node, level) -> {										
				if(node.getNodeName().equals("tracks") && !xt.isLeafElementNode(node)) {
					xt.visitChildElementNodesDFS(node, (nd, l) -> {
						Title title = new Title();		
						title.setItem_id(cd.getItem_id());							
						if(nd.getNodeName().equals("title")) {
							try {
								title.setTitle(xt.getAttributeValue(nd, "name"));
							}catch(XmlDataException e) {
								title.setTitle(xt.getTextContentOfLeafNode(nd));											
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
				this.testMusic_CD(cd, artistList);
				
				//insert into DB				
				this.addCD(cd, factory);
							
				
			} catch (IllegalArgumentException e) {
				ErrorLogger.write(location, cd.getItem_id(), ErrType.PROGRAM, "" ,e, xt.getNodeContentDFS(itemNode));
			} catch (XmlDataException e) {
				e.setLocation(location);
				e.setItem_id(cd.getItem_id());
				ErrorLogger.write(e, xt.getNodeContentDFS(itemNode));
			} catch (Exception e) {
				ErrorLogger.write(location, cd.getItem_id(), ErrType.PROGRAM, "", e, xt.getNodeContentDFS(itemNode));
			}
			
		});
	}	

	
	
}
