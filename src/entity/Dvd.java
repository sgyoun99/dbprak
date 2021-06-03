package entity;

import java.sql.PreparedStatement;
import java.sql.SQLException;
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
	
	
	public static Predicate<String> pred_format = format -> true; //allow null
	public static Predicate<Short> pred_runningtime = runningtime -> runningtime == null || (runningtime >= 0 && runningtime < Short.MAX_VALUE); //allow null
	public static Predicate<String> pred_regioncode = regioncode -> true; //allow null
	
	public void testDvd(Dvd dvd) throws Exception {
		if(!Item.pred_item_id.test(dvd.getItem_id())) {throw new XmlDataException("item_id Error (length not 10): \""+dvd.getItem_id()+"\""); }
		if(!pred_format.test(dvd.getFormat())) {throw new XmlDataException("format Error: \""+dvd.getFormat()+"\""); }
		if(!pred_runningtime.test(dvd.getRunningtime())) {throw new XmlDataException("runningtime Error: " +dvd.getRunningtime() ); }
		if(!pred_regioncode.test(dvd.getRegioncode())) {throw new XmlDataException("regioncode Error "+ dvd.getRegioncode()); }
		
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
			} catch (XmlDataException e) {
				//do nothing
			}
			return false;
		}).collect(Collectors.toList()).forEach(dvdItemNode -> {
			try {
				//xml data
				Dvd dvd = new Dvd();
				dvd.setItem_id(xt.getAttributeValue(dvdItemNode, "asin"));
				Node dvdspec = xt.getNodebyNameDFS(dvdItemNode, "dvdspec");

				Node format = xt.getNodebyNameDFS(dvdspec, "format");
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
				Node runningtime = xt.getNodebyNameDFS(dvdspec, "runningtime");
				dvd.setRunningtime(xt.getTextContent(runningtime));
				Node regioncode = xt.getNodebyNameDFS(dvdspec, "regioncode");
				dvd.setRegioncode(xt.getTextContent(regioncode));
				
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
			} catch (XmlDataException e) {
				ErrorLogger.write(location, ErrType.XML, e, xt.getNodeContentDFS(dvdItemNode));
			} catch (SQLException e) {
				if(!e.getMessage().contains("duplicate key value")) {
					ErrorLogger.write(location, ErrType.SQL, e, xt.getNodeContentDFS(dvdItemNode));
				}	
			} catch (IllegalArgumentException e) {
				ErrorLogger.write(location, ErrType.PROGRAM , e, xt.getNodeContentDFS(dvdItemNode));
			} catch (Exception e) {
				ErrorLogger.write(location, ErrType.PROGRAM, e, xt.getNodeContentDFS(dvdItemNode));
			}
			
		});
	}	
	
	
	
	public void actor() {
		String location = "acctor(" + this.location + ")";
		System.out.println(">> actor " + this.location + " ...");
		XmlTool xt = new XmlTool(this.xmlPath);
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
									actor.setActor(xt.getTextContent(nd));
								}
								try {
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
	

	public void creator() {
		String location = "creator(" + this.location + ")";
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
									creator.setCreator(xt.getTextContent(nd));
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
	

	public void director() {
		String location = "director(" + this.location + ")";
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
									director.setDirector(xt.getTextContent(nd));
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
		
		Dvd dvd = new Dvd(Config.LEIPZIG, "Leipzig");
		dvd.dvd();
		dvd.actor();
		dvd.creator();
		dvd.director();
		dvd = new Dvd(Config.DRESDEN_ENCODED, "Dresden");
		dvd.dvd();
		dvd.actor();
		dvd.creator();
		dvd.director();
	}
	
	
}
