package entity;

import java.sql.Date;
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

class Author{
	private String author;

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
	
}

class Publisher{
	private String publisher;

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	
}


public class Book {

	private String item_id;
	private String author;
	private String publisher;
	private Short pages;
	private Date publication_date;
	private String isbn;
	
	private String xmlPath;
	private String location;
	
	public Book(String xmlPath, String location) {
		super();
		this.xmlPath = xmlPath;
		this.location = location;
	}



	public Book() {
	}



	public String getItem_id() {
		return item_id;
	}


	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}


	public String getAuthor() {
		return author;
	}


	public void setAuthor(String author) {
		this.author = author;
	}


	public String getPublisher() {
		return publisher;
	}


	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}


	public Date getPublication_date() {
		return publication_date;
	}

	public void setPublication_date(String publication_date) throws IllegalArgumentException{
		if(publication_date != null) {
			this.publication_date = Date.valueOf(publication_date);
		}
	}

	public String getIsbn() {
		return isbn;
	}


	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}
	public void setPages(String pages) {
		if(pages.length() == 0 || pages == null) {
			this.pages = 0;
		} else {
			this.pages = Short.valueOf(pages);
		}
	}
	public void setPages(Short pages) {
		this.pages = pages;
	}
	public Short getPages() {
		return this.pages;
	}
	
	
	public static Predicate<Date> pred_publicationdate = date -> true; //allow. let DB check.
	public static Predicate<String> pred_isbn = date -> true; //allow
	public static Predicate<Short> pred_pages = pages -> pages == null || (pages >= 0 && pages < Short.MAX_VALUE); // ?
	
	public void testBook(Book book) throws Exception {
		if(!Item.pred_item_id.test(book.getItem_id())) {throw new XmlDataException("item_id Error (length not 10): \""+book.getItem_id()+"\""); }
		if(!pred_pages.test(book.getPages())) {throw new XmlDataException("regioncode Error "+ book.getPages()); }
		if(!pred_isbn.test(book.getIsbn())) {throw new XmlDataException("isbn Error "+ book.getIsbn()); }
		if(!pred_publicationdate.test(book.getPublication_date())) {throw new XmlDataException("publication date Error "+ book.getPublication_date()); }
		
	}
	
	public static Predicate<String> pred_author = author -> true; //allow
	public static Predicate<String> pred_publisher = publisher -> publisher != null;
	public static Predicate<String> pred_director = director -> director != null;
	public void testAuthor(Author autor) throws Exception {
		if(!pred_author.test(getAuthor())) {throw new XmlDataException("author Error: \""+getAuthor()+"\""); }
	}
	public void testPublisher(Publisher publisher) throws Exception {
		if(!pred_publisher.test(publisher.getPublisher())) {throw new XmlDataException("publisher Error. publisher is null.");}
	}
	

	public void book() {
		String location = "Book(" + this.location + ")";
		System.out.println(">> Book " + this.location + " ...");
		XmlTool xt = new XmlTool(this.xmlPath);
		List<Node> items = xt.filterElementNodesDFS(xt.getDocumentNode(), 
				level -> level == 2, 
				node -> node.getNodeName().equals("item")
		);
		items.stream().filter(n -> {
			try {
				return xt.hasAttribute(n, "pgroup") 
//						&& Item.pred_pgroup.test(xt.getAttributeValue(n, "pgroup"))
						&& xt.getAttributeValue(n, "pgroup").equals("Book");
			} catch (XmlDataException e) {
				//do nothing
			}
			return false;
		}).collect(Collectors.toList()).forEach(bookItemNode -> {
			//xml data
			Book book = new Book();
			try {
				book.setItem_id(xt.getAttributeValue(bookItemNode, "asin"));
			} catch (XmlDataException e) {
				ErrorLogger.write(location, ErrType.XML, e, xt.getNodeContentDFS(bookItemNode));
			}
			Node bookspec = xt.getNodebyNameDFS(bookItemNode, "bookspec");
			try {
				if(bookspec.getNodeName().equals("bookspec") && !xt.isLeafElementNode(bookspec)) {
					Node isbn = xt.getNodebyNameDFS(bookspec, "isbn");
					if(isbn != null) {
						if(xt.hasTextContent(isbn)){
							book.setIsbn(xt.getTextContent(isbn));
						} else {
								try {
								book.setIsbn(xt.getAttributeValue(isbn, "val"));
							} catch (XmlDataException e) {
								e.printStackTrace();
							}
						}
					}
					Node pages = xt.getNodebyNameDFS(bookspec, "pages");
					book.setPages(xt.getTextContent(pages));
					Node publication = xt.getNodebyNameDFS(bookspec, "publication");
					try {
						book.setPublication_date(xt.getAttributeValue(publication,"date"));
					} catch ( IllegalArgumentException e) {
						ErrorLogger.write(location+".date", ErrType.XML, e, xt.getNodeContentDFS(bookItemNode));	
					} catch ( StringIndexOutOfBoundsException e) {
						ErrorLogger.write(location+".date", ErrType.XML, e, xt.getNodeContentDFS(bookItemNode));	
					} catch ( XmlDataException e) {
						ErrorLogger.write(location+".date", ErrType.XML, e, xt.getNodeContentDFS(bookItemNode));	
					}
					try {
						this.testBook(book);
						JDBCTool.executeUpdateAutoCommitOn((con, st) -> {
							String sql;
							PreparedStatement ps;
							sql = "INSERT INTO public.book("
									+ "	item_id, pages, publication_date, isbn)"
									+ "	VALUES (?, ?, ?, ?);";
							ps = con.prepareStatement(sql);
							ps.setString(1, book.getItem_id());
							ps.setShort(2, book.getPages());
							ps.setDate(3, book.getPublication_date());
							ps.setString(4, book.getIsbn());
							ps.executeUpdate();
							ps.close();	
							
						});
					} catch (XmlDataException e) {
						ErrorLogger.write(location, ErrType.XML, e, xt.getNodeContentDFS(bookItemNode));
					} catch (SQLException e) {
						if(!e.getMessage().contains("duplicate key value")) {
							ErrorLogger.write(location, ErrType.SQL, e, xt.getNodeContentDFS(bookItemNode));
						}	
					} catch (Exception e) {
						ErrorLogger.write(location, ErrType.PROGRAM , e, xt.getNodeContentDFS(bookItemNode));
					}
				}
				
			} catch (IllegalArgumentException e) {
				ErrorLogger.write(location, ErrType.PROGRAM , e, xt.getNodeContentDFS(bookItemNode));
			} catch (Exception e) {
				ErrorLogger.write(location, ErrType.PROGRAM, e, xt.getNodeContentDFS(bookItemNode));
			}
			
		});
	}	
	
	
	
	public void author() {
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
						&& xt.getAttributeValue(n, "pgroup").equals("Book");
			} catch (XmlDataException e) {
				//do nothing
			}
			return false;
		}).collect(Collectors.toList()).forEach(bookItemNode -> {
			try {
				setItem_id(xt.getAttributeValue(bookItemNode, "asin"));
			} catch (XmlDataException e) {
				ErrorLogger.write(location, ErrType.XML, e, xt.getNodeContentDFS(bookItemNode));
			}
			xt.visitChildElementNodesDFS(bookItemNode, (node, level) -> {
				try {
					if(node.getNodeName().equals("authors") && !xt.isLeafElementNode(node)) {
						xt.visitChildElementNodesDFS(node, (nd, l) -> {
							Author author = new Author();
							if(nd.getNodeName().equals("author")) {
								try {
									author.setAuthor(xt.getAttributeValue(nd, "name"));
								} catch (XmlDataException e) {
									author.setAuthor(xt.getTextContent(nd));
								}
								try {
									this.testAuthor(author);
									JDBCTool.executeUpdateAutoCommitOn((con, st) -> {
										String sql;
										PreparedStatement ps;
										sql = "INSERT INTO public.author("
												+ "	author)"
												+ "	VALUES (?);";
										ps = con.prepareStatement(sql);
										ps.setString(1, author.getAuthor());
										ps.executeUpdate();
										ps.close();
										
										sql = "INSERT INTO public.book_author("
												+ "	item_id, author)"
												+ "	VALUES (?, ?);";
										ps = con.prepareStatement(sql);
										ps.setString(1, getItem_id());
										ps.setString(2, author.getAuthor());
										ps.executeUpdate();
										ps.close();
									});
								} catch (XmlDataException e) {
									ErrorLogger.write(location, ErrType.XML, e, xt.getNodeContentDFS(node));
								} catch (SQLException e) {
									if(!e.getMessage().contains("duplicate key value")) {
										ErrorLogger.write(location, ErrType.SQL, e, xt.getNodeContentDFS(bookItemNode));
									}
								} catch (Exception e) {
									ErrorLogger.write(location, ErrType.PROGRAM , e, xt.getNodeContentDFS(bookItemNode));
								}
							}
						});
					}
					
				} catch (IllegalArgumentException e) {
					ErrorLogger.write(location, ErrType.PROGRAM , e, xt.getNodeContentDFS(bookItemNode));
				} catch (Exception e) {
					ErrorLogger.write(location, ErrType.PROGRAM, e, xt.getNodeContentDFS(bookItemNode));
				}
			});
			
		});
	}	
	

	public void publisher() {
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
						&& xt.getAttributeValue(n, "pgroup").equals("Book");
			} catch (XmlDataException e) {
				//do nothing
			}
			return false;
		}).collect(Collectors.toList()).forEach(bookItemNode -> {
			try {
				setItem_id(xt.getAttributeValue(bookItemNode, "asin"));
			} catch (XmlDataException e) {
				ErrorLogger.write(location, ErrType.XML, e, xt.getNodeContentDFS(bookItemNode));
			}
			xt.visitChildElementNodesDFS(bookItemNode, (node, level) -> {
				try {
					if(node.getNodeName().equals("creators") && !xt.isLeafElementNode(node)) {
						xt.visitChildElementNodesDFS(node, (nd, l) -> {
							Publisher creator = new Publisher();
							if(nd.getNodeName().equals("creator")) {
								try {
									creator.setPublisher(xt.getAttributeValue(nd, "name"));
								} catch (XmlDataException e) {
									creator.setPublisher(xt.getTextContent(nd));
								}
								try {
									this.testPublisher(creator);
									JDBCTool.executeUpdateAutoCommitOn((con, st) -> {
										String sql;
										PreparedStatement ps;
										sql = "INSERT INTO public.publisher("
												+ "	publisher)"
												+ "	VALUES (?);";
										ps = con.prepareStatement(sql);
										ps.setString(1, creator.getPublisher());
										ps.executeUpdate();
										ps.close();
										
										sql = "INSERT INTO public.book_publisher("
												+ "	item_id, publisher)"
												+ "	VALUES (?, ?);";
										ps = con.prepareStatement(sql);
										ps.setString(1, getItem_id());
										ps.setString(2, creator.getPublisher());
										ps.executeUpdate();
										ps.close();
									});
								} catch (XmlDataException e) {
									ErrorLogger.write(location, ErrType.XML, e, xt.getNodeContentDFS(node));
								} catch (SQLException e) {
									if(!e.getMessage().contains("duplicate key value")) {
										ErrorLogger.write(location, ErrType.SQL, e, xt.getNodeContentDFS(bookItemNode));
									}
								} catch (Exception e) {
									ErrorLogger.write(location, ErrType.PROGRAM , e, xt.getNodeContentDFS(bookItemNode));
								}
							}
						});
					}
					
				} catch (IllegalArgumentException e) {
					ErrorLogger.write(location, ErrType.PROGRAM , e, xt.getNodeContentDFS(bookItemNode));
				} catch (Exception e) {
					ErrorLogger.write(location, ErrType.PROGRAM, e, xt.getNodeContentDFS(bookItemNode));
				}
			});
			
		});
	}	
	

	public static void main(String[] args) throws Exception {
		DropTables.dropTable("Errors");
		CreateTables.createTable("Errors");

		DropTables.dropTable("Book_Publisher");
		DropTables.dropTable("Book_Author");
		DropTables.dropTable("Book");
		DropTables.dropTable("Publisher");
		DropTables.dropTable("Author");
		CreateTables.createTable("Author");
		CreateTables.createTable("Publisher");
		CreateTables.createTable("Book");
		CreateTables.createTable("Book_Author");
		CreateTables.createTable("Book_Publisher");
		
		Book book = new Book(Config.LEIPZIG, "Leipzig");
		book.book();
		book.author();
		book.publisher();
		book = new Book(Config.DRESDEN_ENCODED, "Dresden");
		book.book();
		book.author();
		book.publisher();
		System.out.println("=== done ===");
	}
	
	
}
