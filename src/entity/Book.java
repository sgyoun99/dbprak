package entity;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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

	public void setPublication_date(String publication_date) throws XmlInvalidValueException{
		if(publication_date != null) {
			try {
				this.publication_date = Date.valueOf(publication_date);
			} catch (IllegalArgumentException e) {
				XmlInvalidValueException ex = new XmlInvalidValueException("date is not in the form yyyy-mm-dd");
				ex.setAttrName("publication_date");
				throw ex;
			}
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
	
	
	public static Predicate<Short> pred_pages = pages -> pages == null || (pages >= 0 && pages < Short.MAX_VALUE); // ?
	public static Predicate<String> pred_isbn = date -> date == null || date != null && (date.length()==10 || date.length() ==13); //null allowed
	public static Predicate<Date> pred_publicationdate = date -> true; //will be tested in the setter
	
	public void testBook(Book book) throws XmlValidationFailException, XmlNullNodeException {
		try {
			if(!Item.pred_item_id.test(book.getItem_id())) {
				XmlInvalidValueException e = new XmlInvalidValueException("item_id Error (id does not exist): "+book.getItem_id());
				e.setAttrName("item_id");
				throw e;
			}
			if(!pred_pages.test(book.getPages())) {
				XmlInvalidValueException e = new XmlInvalidValueException("regioncode Error "+ book.getPages()); 
				e.setAttrName("pages");
				throw e;
			}
			if(!pred_isbn.test(book.getIsbn())) {
				XmlInvalidValueException e = new XmlInvalidValueException("isbn Error "+ book.getIsbn()); 
				e.setAttrName("isbn");
				throw e;
			}
			if(!pred_publicationdate.test(book.getPublication_date())) {
				XmlInvalidValueException e = new XmlInvalidValueException("publication date Error "+ book.getPublication_date()); 
				e.setAttrName("publication_date)");
				throw e;
			}
		} catch (NullPointerException e) {
			throw new XmlNullNodeException();
		} catch (XmlInvalidValueException e) {
			throw new XmlValidationFailException(e);
		}
		
	}
	
	public static Predicate<String> pred_author = author -> true; //allow
	public static Predicate<String> pred_publisher = publisher -> publisher != null;
	public void testAuthor(Author author) throws XmlValidationFailException {
		try {
			if(!pred_author.test(getAuthor())) {
				XmlInvalidValueException e = new XmlInvalidValueException("author Error: \""+getAuthor()+"\""); 
				e.setAttrName("author");
				throw e;
			}
		} catch (XmlInvalidValueException e) {
			throw new XmlValidationFailException(e);
		}
	}
	public void testPublisher(Publisher publisher) throws XmlValidationFailException {
		try {
			if(!pred_publisher.test(publisher.getPublisher())) {
				XmlInvalidValueException e = new XmlInvalidValueException("publisher Error. publisher is null.");
				e.setAttrName("publisher");
				throw e;
			}
		} catch (XmlInvalidValueException e) {
			throw new XmlValidationFailException(e);
		}
	}
	

	public void book() {
		String location = "Book(" + this.location + ")";
		System.out.println(">> Book " + this.location + " ...");
		XmlTool xt = new XmlTool(this.xmlPath);
		xt.getNodesByNameDFS(xt.getDocumentNode(), "item").stream().filter(itemNode -> {
			try {
				if(xt.getAttributeValue(itemNode, "pgroup").equals("Book")) {
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
			Book book = new Book();
			try {
				book.setItem_id(xt.getAttributeValue(itemNode, "asin"));
			} catch (XmlDataException e) {
				ErrorLogger.write(location, ErrType.XML, e, xt.getNodeContentDFS(itemNode));
			}
			
			Node bookspec = xt.getNodeByNameDFS(itemNode, "bookspec");
			try {
				Node isbn = xt.getNodeByNameDFS(bookspec, "isbn");
				book.setIsbn(xt.getNodeContentForceNullable(isbn));
				Node pages = xt.getNodeByNameDFS(bookspec, "pages");
				book.setPages(xt.getNodeContentForceNullable(pages));
				Node publication = xt.getNodeByNameDFS(bookspec, "publication");
				book.setPublication_date(xt.getNodeContentForceNullable(publication));
				
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
							
				
			} catch (IllegalArgumentException e) {
				ErrorLogger.write(location, book.getItem_id(), ErrType.PROGRAM, "" ,e, xt.getNodeContentDFS(itemNode));
			} catch (XmlValidationFailException e) {
				e.setLocation(location);
				e.setItem_id(book.getItem_id());
				ErrorLogger.write(e, xt.getNodeContentDFS(itemNode));
			} catch (XmlDataException e) {
				e.setLocation(location);
				e.setItem_id(book.getItem_id());
				ErrorLogger.write(e, xt.getNodeContentDFS(itemNode));
			} catch (SQLException ex) {
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
			} catch (Exception e) {
				ErrorLogger.write(location, book.getItem_id(), ErrType.PROGRAM, "", e, xt.getNodeContentDFS(itemNode));
			}
			
		});
	}	
	
	
	
	public void author() {
		String location = "author(" + this.location + ")";
		String attrName = "author";
		System.out.println(">> author " + this.location + " ...");
		XmlTool xt = new XmlTool(this.xmlPath);
		xt.getNodesByNameDFS(xt.getDocumentNode(), "item").stream().filter(itemNode -> {
			try {
				if(xt.getAttributeValue(itemNode, "pgroup").equals("Book")) {
					return true;
				} else {
					return false;
				}
			} catch (XmlNoAttributeException e) {
				//do noting
			}
			return false;
		}).collect(Collectors.toList()).forEach(itemNode -> {
			Book book = new Book();
			try {
				try {
					book.setItem_id(xt.getAttributeValue(itemNode, "asin"));
				} catch (XmlNoAttributeException e) {
					e.setLocation(location);
					ErrorLogger.write(e, xt.getNodeContentDFS(itemNode));
				}
				xt.getNodesByNameDFS(itemNode, "author").forEach(node -> {
				Author author = new Author();
					try {
						author.setAuthor(xt.getNodeContentForceNotNull(node));
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
							ps.setString(1, book.getItem_id());
							ps.setString(2, author.getAuthor());
							ps.executeUpdate();
							ps.close();
						});
					} catch (IllegalArgumentException e) {
						ErrorLogger.write(location, book.getItem_id(), ErrType.PROGRAM, attrName ,e, xt.getNodeContentDFS(itemNode));
					} catch (XmlDataException e) {
						e.setLocation(location);
						e.setItem_id(book.getItem_id());
						e.setAttrName(attrName);
						ErrorLogger.write(e, xt.getNodeContentDFS(itemNode));
					} catch (SQLException ex) {
						if(ex.getMessage().contains("duplicate key value")) {
							SQLKeyDuplicatedException e = new SQLKeyDuplicatedException();
							e.setAttrName(attrName);
							e.setItem_id(book.getItem_id());
							e.setLocation(location);
							e.setMessage("duplicate key value");
							ErrorLogger.write(e, xt.getNodeContentDFS(itemNode));
						} else {
							ErrorLogger.write(location, book.getItem_id(), ErrType.SQL, attrName, ex, xt.getNodeContentDFS(itemNode));
						}
					}
				});
				}  catch (Exception e) {
					ErrorLogger.write(location, book.getItem_id(), ErrType.PROGRAM, attrName, e, xt.getNodeContentDFS(itemNode));
				}	
			}	
		);
	}	
	

	public void publisher() {
		String location = "publisher(" + this.location + ")";
		String attrName = "publisher";
		System.out.println(">> publisher " + this.location + " ...");
		XmlTool xt = new XmlTool(this.xmlPath);
		xt.getNodesByNameDFS(xt.getDocumentNode(), "item").stream().filter(itemNode -> {
			try {
				if(xt.getAttributeValue(itemNode, "pgroup").equals("Book")) {
					return true;
				} else {
					return false;
				}
			} catch (XmlNoAttributeException e) {
				//do noting
			}
			return false;
		}).collect(Collectors.toList()).forEach(itemNode -> {
			Book book = new Book();
			try {
				try {
					book.setItem_id(xt.getAttributeValue(itemNode, "asin"));
				} catch (XmlDataException e) {
					e.setLocation(location);
					ErrorLogger.write(e, xt.getNodeContentDFS(itemNode));
				}
				xt.getNodesByNameDFS(itemNode, "publisher").forEach(node -> {
				Publisher publisher = new Publisher();
					try {
						publisher.setPublisher(xt.getNodeContentForceNotNull(node));
						this.testPublisher(publisher);
						JDBCTool.executeUpdateAutoCommitOn((con, st) -> {
							String sql;
							PreparedStatement ps;
							sql = "INSERT INTO public.publisher("
									+ "	publisher)"
									+ "	VALUES (?);";
							ps = con.prepareStatement(sql);
							ps.setString(1, publisher.getPublisher());
							ps.executeUpdate();
							ps.close();
							
							sql = "INSERT INTO public.book_publisher("
									+ "	item_id, publisher)"
									+ "	VALUES (?, ?);";
							ps = con.prepareStatement(sql);
							ps.setString(1, book.getItem_id());
							ps.setString(2, publisher.getPublisher());
							ps.executeUpdate();
							ps.close();
						});
					} catch (IllegalArgumentException e) {
						ErrorLogger.write(location, book.getItem_id(), ErrType.PROGRAM, attrName ,e, xt.getNodeContentDFS(itemNode));
					} catch (XmlDataException e) {
						e.setLocation(location);
						e.setItem_id(book.getItem_id());
						e.setAttrName(attrName);
						ErrorLogger.write(e, xt.getNodeContentDFS(itemNode));
					} catch (SQLException ex) {
						if(ex.getMessage().contains("duplicate key value")) {
							SQLKeyDuplicatedException e = new SQLKeyDuplicatedException();
							e.setAttrName(attrName);
							e.setItem_id(book.getItem_id());
							e.setLocation(location);
							e.setMessage("duplicate key value");
							ErrorLogger.write(e, xt.getNodeContentDFS(itemNode));
						} else {
							ErrorLogger.write(location, book.getItem_id(), ErrType.SQL, attrName, ex, xt.getNodeContentDFS(itemNode));
						}
					}
				});
				}  catch (Exception e) {
					ErrorLogger.write(location, book.getItem_id(), ErrType.PROGRAM, attrName, e, xt.getNodeContentDFS(itemNode));
				}	
			}	
		);
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
