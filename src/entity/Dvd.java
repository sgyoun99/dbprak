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
	Integer regioncode;
	
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
		this.format = format;
	}

	public Integer getRunningtime() {
		return runningtime;
	}

	public void setRunningtime(String runningtime) {
		if(runningtime.length() == 0) {
			this.runningtime = 0;
		} else {
			this.runningtime = Integer.valueOf(runningtime);
		}
	}
	public void setRunningtime(Integer runningtime) {
		this.runningtime = runningtime;
	}

	public Integer getregioncode() {
		return regioncode;
	}

	public void setregioncode(String regioncode) {
		if(regioncode.length() == 0) {
			this.regioncode = 0;
		} else {
			this.regioncode = Integer.valueOf(regioncode);
		}
	}
	public void setregioncode(Integer regioncode) {
		this.regioncode = regioncode;
	}
	
	public static Predicate<String> pred_format = format -> format.length() != 0;
	public static Predicate<Integer> pred_runningtime = runningtime -> runningtime >= 0 && runningtime < 32767;
	public static Predicate<Integer> pred_regioncode = regioncode -> regioncode >= 0 && regioncode < 32767; // ?
	
	public boolean test() throws Exception {
		if(!Item.pred_item_id.test(getItem_id())) {throw new XmlDataException("item_id Error (length not 10): \""+getItem_id()+"\""); }
		if(!pred_format.test(getFormat())) {throw new XmlDataException("format Error: \""+getFormat()+"\""); }
		if(!pred_runningtime.test(getRunningtime())) {throw new XmlDataException("runningtime Error: " +getRunningtime() ); }
		if(!pred_regioncode.test(getregioncode())) {throw new XmlDataException("regioncode Error "+ getregioncode()); }
		
		return true;
	}
	
	public void dvdActor(Node node) {
		
	}

	public void leipzig() {
		String location = "Dvd(leipzig)";
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
			try {
			//xml data
				setItem_id(xt.getAttributeValue(dvdItemNode, "asin"));
				xt.visitChildElementNodesDFS(dvdItemNode, (node, level) -> {
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
								setregioncode(xt.getTextContent(nd));
							}
	
						});
					}
				});
		
			//insert
				this.test();
				JDBCTool.executeUpdate((con, st) ->	{
					String sql = "INSERT INTO public.dvd("
							+ "	item_id, format, runningtime, regioncode)"
							+ "	VALUES (?, ?, ?, ?);";
					PreparedStatement ps = con.prepareStatement(sql);
					ps.setString(1, getItem_id());
					ps.setString(2, getFormat());
					ps.setInt(3, getRunningtime());
					ps.setInt(4, getregioncode());
					ps.executeUpdate();
					ps.close();
					
				});
			} catch (IllegalArgumentException e) {
				ErrorLogger.write(location, ErrType.PROGRAM , e, xt.getNodeContentDFS(dvdItemNode));
			} catch (XmlDataException e) {
				ErrorLogger.write(location, ErrType.XML, e, xt.getNodeContentDFS(dvdItemNode));
			} catch (SQLException e) {
				if(!e.getMessage().contains("duplicate key value")) {
					ErrorLogger.write(location, ErrType.SQL, e, xt.getNodeContentDFS(dvdItemNode));
				}
			} catch (Exception e) {
				ErrorLogger.write(location, ErrType.PROGRAM, e, xt.getNodeContentDFS(dvdItemNode));
			}
		});
	}	
	
	public static void main(String[] args) throws Exception {
		DropTables.dropTable("Errors");
		CreateTables.createTable("Errors");
//		DropTables.dropTable("DVD");
//		CreateTables.createTable("DVD");
		
		Dvd dvd = new Dvd();
		dvd.leipzig();
	}
	
	
}
