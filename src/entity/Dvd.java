package entity;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.w3c.dom.Node;

import JDBCTools.JDBCTool;
import XmlTools.XmlDataException;
import XmlTools.XmlTool;
import main.Config;
import main.CreateTables;
import main.DropTables;
import main.ErrType;
import main.ErrorLogger;
import main.Pgroup;

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

	String item_id;
	String format;
	Integer runningtime;
	Short regioncode;
	
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

	public Integer getRunningtime() {
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
			this.runningtime = Integer.valueOf(runningtime);
		}
	}
	public void setRunningtime(Integer runningtime) {
		this.runningtime = runningtime;
	}

	public Short getRegioncode() {
		if(regioncode == null) {
			return 0;
		} else {
			return regioncode;
		}
	}

	public void setRegioncode(String regioncode) {
		if(regioncode.length() == 0 || regioncode == null) {
			this.regioncode = 0;
		} else {
			this.regioncode = Short.valueOf(regioncode);
		}
	}
	public void setRegioncode(Short regioncode) {
		this.regioncode = regioncode;
	}
	
	
	public static Predicate<String> pred_format = format -> true; //allow
	public static Predicate<Integer> pred_runningtime = runningtime -> runningtime == null || (runningtime >= 0 && runningtime < Integer.MAX_VALUE);
	public static Predicate<Short> pred_regioncode = regioncode -> regioncode == null || (regioncode >= 0 && regioncode < Short.MAX_VALUE); // ?
	
	public void testDVD() throws Exception {
		if(!Item.pred_item_id.test(getItem_id())) {throw new XmlDataException("item_id Error (length not 10): \""+getItem_id()+"\""); }
		if(!pred_format.test(getFormat())) {throw new XmlDataException("format Error: \""+getFormat()+"\""); }
		if(!pred_runningtime.test(getRunningtime())) {throw new XmlDataException("runningtime Error: " +getRunningtime() ); }
		if(!pred_regioncode.test(getRegioncode())) {throw new XmlDataException("regioncode Error "+ getRegioncode()); }
		
	}
	
	public static Predicate<String> pred_actor = actor -> actor != null;
	public static Predicate<String> pred_creator = creator -> creator != null;
	public static Predicate<String> pred_director = director -> director != null;
	public void testActor(Actor actor) throws Exception {
		if(!pred_actor.test(actor.getActor())) {throw new XmlDataException("actor Error. actor is null.");}
	}
	public void testCreator(Creator creator) throws Exception {
		if(!pred_creator.test(creator.getCreator())) {throw new XmlDataException("creator Error. creator is null.");}
	}
	public void testDirector(Director director) throws Exception {
		if(!pred_director.test(director.getDirector())) {throw new XmlDataException("director Error. director is null.");}
	}
	

	public void dvdLeipzig() {
		String location = "DVD(leipzig)";
		System.out.println(">> DVD Leipzig ...");
		XmlTool xt = new XmlTool(Config.LEIPZIG);
		List<Node> items = xt.filterElementNodesDFS(xt.getDocumentNode(), 
				level -> level == 2, 
				node -> node.getNodeName().equals("item")
		);
		items.stream().filter(n -> {
			try {
				return xt.hasAttribute(n, "pgroup") 
//						&& Item.pred_pgroup.test(xt.getAttributeValue(n, "pgroup"))
						&& xt.getAttributeValue(n, "pgroup").equals("DVD");
			} catch (XmlDataException e) {
				//do nothing
			}
			return false;
		}).collect(Collectors.toList()).forEach(dvdItemNode -> {
			//xml data
			try {
				setItem_id(xt.getAttributeValue(dvdItemNode, "asin"));
			} catch (XmlDataException e) {
				ErrorLogger.write(location, ErrType.XML, e, xt.getNodeContentDFS(dvdItemNode));
			}
			xt.visitChildElementNodesDFS(dvdItemNode, (node, level) -> {
			try {
				if(node.getNodeName().equals("dvdspec") && !xt.isLeafElementNode(node)) {
					xt.visitChildElementNodesDFS(node, (nd, l) -> {
						if(nd.getNodeName().equals("format") && xt.hasTextContent(nd)) {
							if(xt.hasTextContent(nd)){
								setFormat(xt.getTextContent(nd));
							} else {
									try {
									setFormat(xt.getAttributeValue(nd, "value"));
								} catch (XmlDataException e) {
									//confirm here
									e.printStackTrace();
								}
							}
						}
						if(nd.getNodeName().equals("runningtime")) {
							setRunningtime(xt.getTextContent(nd));
						}
						if(nd.getNodeName().equals("regioncode")) {
							setRegioncode(xt.getTextContent(nd));
						}
						try {
							this.testDVD();
							JDBCTool.executeUpdateAutoCommit((con, st) -> {
								String sql;
								PreparedStatement ps;
								sql = "INSERT INTO public.dvd("
										+ "	item_id, format, runningtime, regioncode)"
										+ "	VALUES (?, ?, ?, ?);";
								ps = con.prepareStatement(sql);
								ps.setString(1, getItem_id());
								ps.setString(2, getFormat());
								ps.setInt(3, getRunningtime());
								ps.setShort(4, getRegioncode());
								ps.executeUpdate();
								ps.close();	
								
							});
						} catch (XmlDataException e) {
							ErrorLogger.write(location, ErrType.XML, e, xt.getNodeContentDFS(dvdItemNode));
						} catch (SQLException e) {
							if(!e.getMessage().contains("duplicate key value")) {
								ErrorLogger.write(location, ErrType.SQL, e, xt.getNodeContentDFS(dvdItemNode));
							}	
						} catch (Exception e) {
							ErrorLogger.write(location, ErrType.PROGRAM , e, xt.getNodeContentDFS(dvdItemNode));
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
	
	
	
	public void actorLeipzig() {
		String location = "acctor(leipzig)";
		XmlTool xt = new XmlTool(Config.LEIPZIG);
		List<Node> items = xt.filterElementNodesDFS(xt.getDocumentNode(), 
				level -> level == 2, 
				node -> node.getNodeName().equals("item")
		);
		items.stream().filter(n -> {
			try {
				return xt.hasAttribute(n, "pgroup") 
						&& xt.getAttributeValue(n, "pgroup").equals("DVD");
			} catch (XmlDataException e) {
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
							if(nd.getNodeName().equals("actor")) {
								try {
									actor.setActor(xt.getAttributeValue(nd, "name"));
								} catch (XmlDataException e) {
								}
								try {
									this.testActor(actor);
									JDBCTool.executeUpdateAutoCommit((con, st) -> {
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
								} catch (XmlDataException e) {
									ErrorLogger.write(location, ErrType.XML, e, xt.getNodeContentDFS(node));
								} catch (SQLException e) {
									if(!e.getMessage().contains("duplicate key value")) {
										ErrorLogger.write(location, ErrType.SQL, e, xt.getNodeContentDFS(dvdItemNode));
									}
								} catch (Exception e) {
									ErrorLogger.write(location, ErrType.PROGRAM , e, xt.getNodeContentDFS(dvdItemNode));
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
	

	public void creatorLeipzig() {
		String location = "creator(leipzig)";
		XmlTool xt = new XmlTool(Config.LEIPZIG);
		List<Node> items = xt.filterElementNodesDFS(xt.getDocumentNode(), 
				level -> level == 2, 
				node -> node.getNodeName().equals("item")
		);
		items.stream().filter(n -> {
			try {
				return xt.hasAttribute(n, "pgroup") 
						&& xt.getAttributeValue(n, "pgroup").equals("DVD");
			} catch (XmlDataException e) {
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
							if(nd.getNodeName().equals("creator")) {
								try {
									creator.setCreator(xt.getAttributeValue(nd, "name"));
								} catch (XmlDataException e) {
								}
								try {
									this.testCreator(creator);
									JDBCTool.executeUpdateAutoCommit((con, st) -> {
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
								} catch (XmlDataException e) {
									ErrorLogger.write(location, ErrType.XML, e, xt.getNodeContentDFS(node));
								} catch (SQLException e) {
									if(!e.getMessage().contains("duplicate key value")) {
										ErrorLogger.write(location, ErrType.SQL, e, xt.getNodeContentDFS(dvdItemNode));
									}
								} catch (Exception e) {
									ErrorLogger.write(location, ErrType.PROGRAM , e, xt.getNodeContentDFS(dvdItemNode));
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
	

	public void directorLeipzig() {
		String location = "director(leipzig)";
		XmlTool xt = new XmlTool(Config.LEIPZIG);
		List<Node> items = xt.filterElementNodesDFS(xt.getDocumentNode(), 
				level -> level == 2, 
				node -> node.getNodeName().equals("item")
		);
		items.stream().filter(n -> {
			try {
				return xt.hasAttribute(n, "pgroup") 
						&& xt.getAttributeValue(n, "pgroup").equals("DVD");
			} catch (XmlDataException e) {
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
							if(nd.getNodeName().equals("director")) {
								try {
									director.setDirector(xt.getAttributeValue(nd, "name"));
								} catch (XmlDataException e) {
								}
								try {
									this.testDirector(director);
									JDBCTool.executeUpdateAutoCommit((con, st) -> {
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
								} catch (XmlDataException e) {
									ErrorLogger.write(location, ErrType.XML, e, xt.getNodeContentDFS(node));
								} catch (SQLException e) {
									if(!e.getMessage().contains("duplicate key value")) {
										ErrorLogger.write(location, ErrType.SQL, e, xt.getNodeContentDFS(dvdItemNode));
									}
								} catch (Exception e) {
									ErrorLogger.write(location, ErrType.PROGRAM , e, xt.getNodeContentDFS(dvdItemNode));
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
		
		Dvd dvd = new Dvd();
		dvd.dvdLeipzig();
		dvd.actorLeipzig();
		dvd.creatorLeipzig();
		dvd.directorLeipzig();
	}
	
	
}
