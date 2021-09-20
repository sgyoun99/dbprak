/**
 * Classes needed to read the DVD-related data from file and
 * write it in the associated tables in DB
 * @version 03.06.2021
 */
package entity;

import java.sql.PreparedStatement;
import java.sql.SQLException;
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
import exception.XmlValidationFailException;
import main.Config;
import main.CreateTables;
import main.DropTables;
import main.ErrType;
import main.ErrorLogger;


public class ManageDvd {

	public void manageDvds(SessionFactory factory) {
		dvd(factory, Config.DRESDEN_ENCODED);
		dvd(factory, Config.LEIPZIG);
		System.out.println("\033[1;34m    *\033[35m*\033[33m*\033[32m* \033[91mDVDs finished \033[32m*\033[33m*\033[35m*\033[34m*\033[0m");
	}

	private void addDvd(SessionFactory factory, Dvd dvd) {
		Session session = factory.openSession();
		Transaction tx = null;
		
		try {
			tx = session.beginTransaction();
			session.save(dvd); 
			tx.commit();
		} catch (HibernateException e) {
			if (tx!=null) {
				tx.rollback();
			}
			System.out.println("HibernateException for " + dvd.getItem_id()); 
		} finally {
			session.close(); 
		}
	}
	
	
	/**
	 * check that if runningtime exists it is of valid size
	 */
/*	public static Predicate<String> pred_format = format -> true; //allow null
	public static Predicate<Short> pred_runningtime = runningtime -> runningtime == null || (runningtime >= 0 && runningtime < Short.MAX_VALUE); //allow null
	public static Predicate<String> pred_regioncode = regioncode -> true; //allow null
	
	public void testDvd(Dvd dvd) throws XmlValidationFailException {
		try {
			if(!Item.pred_item_id.test(dvd.getItem_id())) {
				XmlInvalidValueException e = new XmlInvalidValueException("item_id Error (length not 10): \""+dvd.getItem_id()+"\""); 
				e.setAttrName("item_id");
				throw e;
			}
			if(!pred_format.test(dvd.getFormat())) {
				XmlInvalidValueException e = new XmlInvalidValueException("format Error: \""+dvd.getFormat()+"\""); 
				e.setAttrName("format");
				throw e;
			}
			if(!pred_runningtime.test(dvd.getRunningtime())) {
				XmlInvalidValueException e = new XmlInvalidValueException("runningtime Error: " +dvd.getRunningtime() ); 
				e.setAttrName("runningtime");
				throw e;
			}
			if(!pred_regioncode.test(dvd.getRegioncode())) {
				XmlInvalidValueException e = new XmlInvalidValueException("regioncode Error "+ dvd.getRegioncode()); 
				e.setAttrName("regioncode");
				throw e;
			}
		} catch (XmlInvalidValueException e) {
			throw new XmlValidationFailException(e);
		}
	}*/
	
	/**
	 * check that actors, creators and directors actually exist
	 */
/*	public static Predicate<String> pred_actor = actor -> actor != null;
	public static Predicate<String> pred_creator = creator -> creator != null;
	public static Predicate<String> pred_director = director -> director != null;
	public void testActor(Actor actor) throws XmlValidationFailException {
		try {
			if(!pred_actor.test(actor.getActor())) {throw new XmlInvalidValueException("actor Error. actor is null.");}
		} catch (XmlInvalidValueException e) {
			throw new XmlValidationFailException(e);
		}
	}
	public void testCreator(Creator creator) throws XmlValidationFailException {
		try {
			if(!pred_creator.test(creator.getCreator())) {throw new XmlInvalidValueException("creator Error. creator is null.");}
		} catch (XmlInvalidValueException e) {
			throw new XmlValidationFailException(e);
		}
	}
	public void testDirector(Director director) throws XmlValidationFailException {
		try {
			if(!pred_director.test(director.getDirector())) {throw new XmlInvalidValueException("director Error. director is null.");}
		} catch (XmlInvalidValueException e) {
			throw new XmlValidationFailException(e);
		}
	}*/
	
	/**
	 * to read dvd-data from file and write in DB table "dvd"
	 */
	public void dvd(SessionFactory factory, String xmlPath) {
		String location = "DVD(" + xmlPath + ")";
		XmlTool xt = new XmlTool(xmlPath);
		List<Node> items = xt.filterElementNodesDFS(xt.getDocumentNode(), 
				level -> level == 2, 
				node -> node.getNodeName().equals("item")
		);
		items.stream().filter(n -> {
			try {
				return xt.hasAttribute(n, "pgroup") 
						&& xt.getAttributeValue(n, "pgroup").equals("DVD");
			} catch (XmlNoAttributeException e) {
				//do nothing
			}
			return false;
		}).collect(Collectors.toList()).forEach(itemNode -> {
			Dvd dvd = new Dvd();
			HashSet<Actor> actorSet = new HashSet<>();
			HashSet<Creator> creatorSet = new HashSet<>();
			HashSet<Director> directorSet = new HashSet<>();
			try {
				//xml data
				dvd.setItem_id(xt.getAttributeValue(itemNode, "asin"));
				Node dvdspec = xt.getNodeByNameDFS(itemNode, "dvdspec");

				Node format = xt.getNodeByNameDFS(dvdspec, "format");
				// TODO confirm above method
				String formatValue = xt.getNodeContentForceNullable(format);
				dvd.setFormat(formatValue);
				
				Node runningtime = xt.getNodeByNameDFS(dvdspec, "runningtime");
				dvd.setRunningtime(xt.getTextContentOfLeafNode(runningtime));
				Node regioncode = xt.getNodeByNameDFS(dvdspec, "regioncode");
				dvd.setRegioncode(xt.getTextContentOfLeafNode(regioncode));
				
				//get Actors
				xt.visitChildElementNodesDFS(itemNode, (node, level) -> {
					//try {						
						if(node.getNodeName().equals("actors") && !xt.isLeafElementNode(node)) {
							xt.visitChildElementNodesDFS(node, (nd, l) -> {
								Actor actor = new Actor();									
								if(nd.getNodeName().equals("actor")) {
									try {
										actor.setActor(xt.getAttributeValue(nd, "name"));
									}catch(XmlDataException e) {
										actor.setActor(xt.getTextContentOfLeafNode(nd));										
									}finally{
										actorSet.add(actor);
									}

								}
							});
						}
				});

				//get Creators
				xt.visitChildElementNodesDFS(itemNode, (node, level) -> {
				//try {
					if(node.getNodeName().equals("creators") && !xt.isLeafElementNode(node)) {
						xt.visitChildElementNodesDFS(node, (nd, l) -> {
							Creator creator = new Creator();
							if(nd.getNodeName().equals("creator")) {
								try {
									creator.setCreator(xt.getAttributeValue(nd, "name"));
								} catch (XmlDataException e) {
									creator.setCreator(xt.getTextContentOfLeafNode(nd));
								}finally{
									creatorSet.add(creator);
								}
							}
						});
					}
				});

				//get Directors
				xt.visitChildElementNodesDFS(itemNode, (node, level) -> {
				//try {
					if(node.getNodeName().equals("directors") && !xt.isLeafElementNode(node)) {
						xt.visitChildElementNodesDFS(node, (nd, l) -> {
							Director director = new Director();
							if(nd.getNodeName().equals("director")) {
								try {
									director.setDirector(xt.getAttributeValue(nd, "name"));
								} catch (XmlDataException e) {
									director.setDirector(xt.getTextContentOfLeafNode(nd));
								}finally{
									directorSet.add(director);
								}
							}
						});
					}
				});
									
										



				//test
				//this.testDvd(dvd);
				//insert
				dvd.setActors(actorSet);
				dvd.setCreators(creatorSet);
				dvd.setDirectors(directorSet);
				this.addDvd(factory, dvd);

				
			} catch (IllegalArgumentException e) {
				ErrorLogger.write(location, dvd.getItem_id(), ErrType.PROGRAM, "" ,e, xt.getNodeContentDFS(itemNode));
			}/* catch (XmlValidationFailException e) {
				e.setLocation(location);
				e.setItem_id(dvd.getItem_id());
				ErrorLogger.write(e, xt.getNodeContentDFS(itemNode));
			}*/ catch (XmlDataException e) {
				e.setLocation(location);
				e.setItem_id(dvd.getItem_id());
				ErrorLogger.write(e, xt.getNodeContentDFS(itemNode));
			}/* catch (SQLException ex) {
				if(ex.getMessage().contains("duplicate key value")) {
					SQLKeyDuplicatedException e = new SQLKeyDuplicatedException();
					e.setAttrName("item_id");
					e.setItem_id(dvd.getItem_id());
					e.setLocation(location);
					e.setMessage("duplicate key value");
					ErrorLogger.write(e, xt.getNodeContentDFS(itemNode));
				} else {
					ErrorLogger.write(location, dvd.getItem_id(), ErrType.SQL, "", ex, xt.getNodeContentDFS(itemNode));
				}
			}*/ catch (Exception e) {
				ErrorLogger.write(location, dvd.getItem_id(), ErrType.PROGRAM, "", e, xt.getNodeContentDFS(itemNode));
			}
			
		});
	}	
	
	
	
	
}
