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

import org.w3c.dom.Node;

import JDBCTools.JDBCTool;
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

class Actor{
	private String actor;

	public String getActor() {
		return actor;
	}

	public void setActor(String actor) {
		this.actor = actor;
	}
	
}

class Creator{
	private String creator;

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}
	
}

class Director{
	private String director;

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}
	
}

public class Dvd {

	private String item_id;
	private String format;
	private Short runningtime;
	private String regioncode;
	
	private String xmlPath;
	private String location;
	
	public Dvd(String xmlPath, String location) {
		super();
		this.xmlPath = xmlPath;
		this.location = location;
	}

	public Dvd() {		
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
//			return 0;
			//test
			return null;
		} else {
			return regioncode;
		}
	}
	public void setRegioncode(String regioncode) {
			this.regioncode = regioncode;
	}
	
	/**
	 * check that if runningtime exists it is of valid size
	 */
	public static Predicate<String> pred_format = format -> true; //allow null
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
	}
	
	/**
	 * check that actors, creators and directors actually exist
	 */
	public static Predicate<String> pred_actor = actor -> actor != null;
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
	}
	
	/**
	 * to read dvd-data from file and write in DB table "dvd"
	 */
	public void dvd() {
		String location = "DVD(" + this.location + ")";
		System.out.println(">> DVD " + this.location + " ...");
		XmlTool xt = new XmlTool(this.xmlPath);
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
			try {
				//xml data
				dvd.setItem_id(xt.getAttributeValue(itemNode, "asin"));
				Node dvdspec = xt.getNodeByNameDFS(itemNode, "dvdspec");

				Node format = xt.getNodeByNameDFS(dvdspec, "format");
				// TODO confirm above method
				String formatValue = xt.getNodeContentForceNullable(format);
				dvd.setFormat(formatValue);
				/*
				if(format != null) {
					if(xt.hasTextContent(format)){
						dvd.setFormat(xt.getTextContent(format));
					} else {
							try {
							dvd.setFormat(xt.getAttributeValue(format, "value"));
						} catch (XmlDataException e) {
							e.printStackTrace();
						}
					}
				}
				 */
				Node runningtime = xt.getNodeByNameDFS(dvdspec, "runningtime");
				dvd.setRunningtime(xt.getTextContentOfLeafNode(runningtime));
				Node regioncode = xt.getNodeByNameDFS(dvdspec, "regioncode");
				dvd.setRegioncode(xt.getTextContentOfLeafNode(regioncode));
				
				//test
				this.testDvd(dvd);

				//insert
				JDBCTool.executeUpdateAutoCommitOn((con, st) -> {
					String sql;
					PreparedStatement ps;
					sql = "INSERT INTO public.dvd("
							+ "	item_id, format, runningtime, regioncode)"
							+ "	VALUES (?, ?, ?, ?);";
					ps = con.prepareStatement(sql);
					ps.setString(1, dvd.getItem_id());
					ps.setString(2, dvd.getFormat());
					ps.setShort(3, dvd.getRunningtime());
					ps.setString(4, dvd.getRegioncode());
					ps.executeUpdate();
					ps.close();	
					
				});
			} catch (IllegalArgumentException e) {
				ErrorLogger.write(location, dvd.getItem_id(), ErrType.PROGRAM, "" ,e, xt.getNodeContentDFS(itemNode));
			} catch (XmlValidationFailException e) {
				e.setLocation(location);
				e.setItem_id(dvd.getItem_id());
				ErrorLogger.write(e, xt.getNodeContentDFS(itemNode));
			} catch (XmlDataException e) {
				e.setLocation(location);
				e.setItem_id(dvd.getItem_id());
				ErrorLogger.write(e, xt.getNodeContentDFS(itemNode));
			} catch (SQLException ex) {
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
			} catch (Exception e) {
				ErrorLogger.write(location, dvd.getItem_id(), ErrType.PROGRAM, "", e, xt.getNodeContentDFS(itemNode));
			}
			
		});
	}	
	
	
	/**
	 * read actor-data from file and write to DB tables "actor", "dvd_actor"
	 */
	public void actor() {
		String location = this.location + ".actor";
		String attrName = "actor";
		System.out.println(">> actor " + this.location + " ...");
		XmlTool xt = new XmlTool(this.xmlPath);
		List<Node> items = xt.filterElementNodesDFS(xt.getDocumentNode(), 
				level -> level == 2, 
				node -> node.getNodeName().equals("item")
		);
		items.stream().filter(itemNode -> {
			try {
				return xt.hasAttribute(itemNode, "pgroup") 
						&& xt.getAttributeValue(itemNode, "pgroup").equals("DVD");
			} catch (XmlNoAttributeException e) {
				//do nothing
			}
			return false;
		}).collect(Collectors.toList()).forEach(dvdItemNode -> {
			try {
				setItem_id(xt.getAttributeValue(dvdItemNode, "asin"));
			} catch (XmlDataException e) {
				ErrorLogger.write(location, ErrType.XML, e, xt.getNodeContentDFS(dvdItemNode));
			}
			xt.visitChildElementNodesDFS(dvdItemNode, (node, level) -> {
				try {
					
					if(node.getNodeName().equals("actors") && !xt.isLeafElementNode(node)) {
						xt.visitChildElementNodesDFS(node, (nd, l) -> {
							Actor actor = new Actor();
							String item_id = null;
							try {
								item_id = xt.getAttributeValue(dvdItemNode, "asin");
							} catch (XmlNoAttributeException e) {
								// do nothing
							}	
							if(nd.getNodeName().equals("actor")) {
								try {
									actor.setActor(xt.getNodeContentForceNotNull(nd));
									this.testActor(actor);
									JDBCTool.executeUpdateAutoCommitOn((con, st) -> {
										String sql;
										PreparedStatement ps;
										sql = "INSERT INTO public.actor("
												+ "	actor)"
												+ "	VALUES (?);";
										ps = con.prepareStatement(sql);
										ps.setString(1, actor.getActor());
										ps.executeUpdate();
										ps.close();
										
										sql = "INSERT INTO public.dvd_actor("
												+ "	item_id, actor)"
												+ "	VALUES (?, ?);";
										ps = con.prepareStatement(sql);
										ps.setString(1, getItem_id());
										ps.setString(2, actor.getActor());
										ps.executeUpdate();
										ps.close();
									});
								} catch (IllegalArgumentException e) {
									ErrorLogger.write(location, null, ErrType.PROGRAM, "" ,e, xt.getNodeContentDFS(nd));
								} catch (XmlDataException e) {
									e.setLocation(location);
									e.setItem_id(item_id);
									e.setAttrName(attrName);
									ErrorLogger.write(e, xt.getNodeContentDFS(nd));
								} catch (SQLException ex) {
									if(ex.getMessage().contains("duplicate key value")) {
										SQLKeyDuplicatedException e = new SQLKeyDuplicatedException();
										e.setAttrName(attrName);
										e.setItem_id(item_id);
										e.setLocation(location);
										e.setMessage("duplicate key value");
										ErrorLogger.write(e, xt.getNodeContentDFS(nd));
									} else {
										ErrorLogger.write(location, item_id, ErrType.SQL, attrName, ex, xt.getNodeContentDFS(nd));
									}
								} catch (Exception e) {
									ErrorLogger.write(location, item_id, ErrType.PROGRAM, attrName, e, xt.getNodeContentDFS(nd));
								}
							}
						});
					}
					
				} catch (IllegalArgumentException e) {
					ErrorLogger.write(location, ErrType.PROGRAM , e, xt.getNodeContentDFS(dvdItemNode));
				} catch (Exception e) {
					ErrorLogger.write(location, ErrType.PROGRAM, e, xt.getNodeContentDFS(dvdItemNode));
				}
			});
			
		});
	}	
	
	/**
	 * read creator-data from file and write to DB tables "creator", "dvd_creator"
	 */
	public void creator() {
		String location = this.location + ".creator";
		String attrName = "creator";
		System.out.println(">> creator " + this.location + " ...");
		XmlTool xt = new XmlTool(this.xmlPath);
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
		}).collect(Collectors.toList()).forEach(dvdItemNode -> {
			try {
				setItem_id(xt.getAttributeValue(dvdItemNode, "asin"));
			} catch (XmlDataException e) {
				ErrorLogger.write(location, ErrType.XML, e, xt.getNodeContentDFS(dvdItemNode));
			}
			xt.visitChildElementNodesDFS(dvdItemNode, (node, level) -> {
				try {
					if(node.getNodeName().equals("creators") && !xt.isLeafElementNode(node)) {
						xt.visitChildElementNodesDFS(node, (nd, l) -> {
							Creator creator = new Creator();
							String item_id = null;
							try {
								item_id = xt.getAttributeValue(dvdItemNode, "asin");
							} catch (XmlNoAttributeException e) {
								// do nothing
							}
							if(nd.getNodeName().equals("creator")) {
								try {
									creator.setCreator(xt.getAttributeValue(nd, "name"));
								} catch (XmlDataException e) {
									creator.setCreator(xt.getTextContentOfLeafNode(nd));
								}
								try {
									this.testCreator(creator);
									JDBCTool.executeUpdateAutoCommitOn((con, st) -> {
										String sql;
										PreparedStatement ps;
										sql = "INSERT INTO public.creator("
												+ "	creator)"
												+ "	VALUES (?);";
										ps = con.prepareStatement(sql);
										ps.setString(1, creator.getCreator());
										ps.executeUpdate();
										ps.close();
										
										sql = "INSERT INTO public.dvd_creator("
												+ "	item_id, creator)"
												+ "	VALUES (?, ?);";
										ps = con.prepareStatement(sql);
										ps.setString(1, getItem_id());
										ps.setString(2, creator.getCreator());
										ps.executeUpdate();
										ps.close();
									});
								} catch (IllegalArgumentException e) {
									ErrorLogger.write(location, null, ErrType.PROGRAM, attrName ,e, xt.getNodeContentDFS(nd));
								} catch (XmlDataException e) {
									e.setLocation(location);
									e.setItem_id(item_id);
									e.setAttrName(attrName);
									ErrorLogger.write(e, xt.getNodeContentDFS(nd));
								} catch (SQLException ex) {
									if(ex.getMessage().contains("duplicate key value")) {
										SQLKeyDuplicatedException e = new SQLKeyDuplicatedException();
										e.setAttrName(attrName);
										e.setItem_id(item_id);
										e.setLocation(location);
										e.setMessage("duplicate key value");
										ErrorLogger.write(e, xt.getNodeContentDFS(nd));
									} else {
										ErrorLogger.write(location, item_id, ErrType.SQL, attrName, ex, xt.getNodeContentDFS(nd));
									}
								} catch (Exception e) {
									ErrorLogger.write(location, item_id, ErrType.PROGRAM, attrName, e, xt.getNodeContentDFS(nd));
								}
							}
						});
					}
					
				} catch (IllegalArgumentException e) {
					ErrorLogger.write(location, ErrType.PROGRAM , e, xt.getNodeContentDFS(dvdItemNode));
				} catch (Exception e) {
					ErrorLogger.write(location, ErrType.PROGRAM, e, xt.getNodeContentDFS(dvdItemNode));
				}
			});
			
		});
	}	
	
	/**
	 * read director-data from file and write to tables "director", "dvd_director"
	 */
	public void director() {
		String location = this.location + ".director";
		String attrName = "director";
		System.out.println(">> director " + this.location + " ...");
		XmlTool xt = new XmlTool(this.xmlPath);
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
		}).collect(Collectors.toList()).forEach(dvdItemNode -> {
			try {
				setItem_id(xt.getAttributeValue(dvdItemNode, "asin"));
			} catch (XmlDataException e) {
				ErrorLogger.write(location, ErrType.XML, e, xt.getNodeContentDFS(dvdItemNode));
			}
			xt.visitChildElementNodesDFS(dvdItemNode, (node, level) -> {
				try {
					if(node.getNodeName().equals("directors") && !xt.isLeafElementNode(node)) {
						xt.visitChildElementNodesDFS(node, (nd, l) -> {
							Director director = new Director();
							String item_id = null;
							try {
								item_id = xt.getAttributeValue(dvdItemNode, "asin");
							} catch (XmlNoAttributeException e) {
								// do nothing
							}
							if(nd.getNodeName().equals("director")) {
								try {
									director.setDirector(xt.getAttributeValue(nd, "name"));
								} catch (XmlDataException e) {
									director.setDirector(xt.getTextContentOfLeafNode(nd));
								}
								try {
									this.testDirector(director);
									JDBCTool.executeUpdateAutoCommitOn((con, st) -> {
										String sql;
										PreparedStatement ps;
										sql = "INSERT INTO public.director("
												+ "	director)"
												+ "	VALUES (?);";
										ps = con.prepareStatement(sql);
										ps.setString(1, director.getDirector());
										ps.executeUpdate();
										ps.close();
										
										sql = "INSERT INTO public.dvd_director("
												+ "	item_id, director)"
												+ "	VALUES (?, ?);";
										ps = con.prepareStatement(sql);
										ps.setString(1, getItem_id());
										ps.setString(2, director.getDirector());
										ps.executeUpdate();
										ps.close();
									});
								} catch (IllegalArgumentException e) {
									ErrorLogger.write(location, null, ErrType.PROGRAM, attrName ,e, xt.getNodeContentDFS(nd));
								} catch (XmlDataException e) {
									e.setLocation(location);
									e.setItem_id(item_id);
									e.setAttrName(attrName);
									ErrorLogger.write(e, xt.getNodeContentDFS(nd));
								} catch (SQLException ex) {
									if(ex.getMessage().contains("duplicate key value")) {
										SQLKeyDuplicatedException e = new SQLKeyDuplicatedException();
										e.setAttrName(attrName);
										e.setItem_id(item_id);
										e.setLocation(location);
										e.setMessage("duplicate key value");
										ErrorLogger.write(e, xt.getNodeContentDFS(nd));
									} else {
										ErrorLogger.write(location, item_id, ErrType.SQL, attrName, ex, xt.getNodeContentDFS(nd));
									}
								} catch (Exception e) {
									ErrorLogger.write(location, item_id, ErrType.PROGRAM, attrName, e, xt.getNodeContentDFS(nd));
								}
							}
						});
					}
					
				} catch (IllegalArgumentException e) {
					ErrorLogger.write(location, ErrType.PROGRAM , e, xt.getNodeContentDFS(dvdItemNode));
				} catch (Exception e) {
					ErrorLogger.write(location, ErrType.PROGRAM, e, xt.getNodeContentDFS(dvdItemNode));
				}
			});
			
		});
	}	
	
	/**
	 * not used for main-programm
	 */
	public static void main(String[] args) throws Exception {
		DropTables.dropTable("Errors");
		CreateTables.createTable("Errors");

		DropTables.dropTable("DVD_Director");
		DropTables.dropTable("DVD_Creator");
		DropTables.dropTable("DVD_Actor");
		DropTables.dropTable("Director");
		DropTables.dropTable("Creator");
		DropTables.dropTable("Actor");
		DropTables.dropTable("DVD");
		CreateTables.createTable("DVD");
		CreateTables.createTable("Actor");
		CreateTables.createTable("Creator");
		CreateTables.createTable("Director");
		CreateTables.createTable("DVD_Actor");
		CreateTables.createTable("DVD_Creator");
		CreateTables.createTable("DVD_Director");
		
		Dvd dvd;
		dvd = new Dvd(Config.DRESDEN_ENCODED, "Dresden");
		dvd.dvd();
		dvd.actor();
		dvd.creator();
		dvd.director();
		dvd = new Dvd(Config.LEIPZIG, "Leipzig");
		dvd.dvd();
		dvd.actor();
		dvd.creator();
		dvd.director();
	}
	
	
}
