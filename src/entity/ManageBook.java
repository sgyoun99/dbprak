/**
 * Classes needed to read for Books all data from file
 * and insert it into the associated tables
 * @version 21-09-23
 */

package entity;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.HashSet;
import java.util.Iterator;

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


public class ManageBook {

	/**
	 * Function that controls reading-in of books and associated classes
	 */
	public void manageBooks(SessionFactory factory) {
		book(Config.LEIPZIG, factory);
		book(Config.DRESDEN_ENCODED, factory);
		System.out.println("\033[1;34m *\033[35m*\033[33m*\033[32m* \033[91mBooks finished \033[32m*\033[33m*\033[35m*\033[34m*\033[0m");
	}

	/**
	 * function that adds books and associated classes to the DB
	 */
	private void addBook(Book book, SessionFactory factory) {
		Session session = factory.openSession();
		Transaction tx = null;
		
		try {
			tx = session.beginTransaction();
			session.save(book); 
			tx.commit();
		} catch (HibernateException e) {
			if (tx!=null) {
				tx.rollback();
			}
			System.out.println("HibernateException for " + book.getItem_id()); 
		} finally {
			session.close(); 
		}
	}
	
	
	
	/**
	 * check if read-in book-data is valid
	 */
	public static Predicate<Short> pred_pages = pages -> pages == null || (pages >= 0 && pages < Short.MAX_VALUE); // ?
	public static Predicate<String> pred_isbn = date -> date == null || date != null && (date.length()==10 || date.length() ==13); //null allowed
	public static Predicate<Date> pred_publicationdate = date -> true; //will be tested in the setter
	
	public void testBook(Book book) throws XmlValidationFailException, XmlNullNodeException {
		try {
			if(!ManageItem.pred_item_id.test(book.getItem_id())) {
				XmlInvalidValueException e = new XmlInvalidValueException("item_id Error (length not 10): \""+book.getItem_id()+"\""); 
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
	
	
	/**
	 * reads book-data from file and writes it to DB table "book"
	 */
	private void book(String xmlPath, SessionFactory factory) {
		String location = "Book(" + xmlPath + ")";
		XmlTool xt = new XmlTool(xmlPath);
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
			HashSet<Author> authorSet = new HashSet<Author>();
			HashSet<Publisher> publisherSet = new HashSet<Publisher>();
			try {
				book.setItem_id(xt.getAttributeValue(itemNode, "asin"));
			} catch (XmlDataException e) {
				ErrorLogger.write(location, ErrType.XML, e, xt.getNodeContentDFS(itemNode));
			}
			
			//get the authors from file, add to Set
			xt.getNodesByNameDFS(itemNode, "author").forEach(node -> {
				Author author = new Author();
				try {
					author.setAuthor(xt.getNodeContentForceNotNull(node));
					authorSet.add(author);
				} catch (IllegalArgumentException e) {
					ErrorLogger.write(location, book.getItem_id(), ErrType.PROGRAM, "author" ,e, xt.getNodeContentDFS(itemNode));
				} catch (XmlDataException e) {
					e.setLocation(location);
					e.setItem_id(book.getItem_id());
					e.setAttrName("author");
					ErrorLogger.write(e, xt.getNodeContentDFS(itemNode));
				}
			});

			//get the publishers from file, add to Set
			xt.getNodesByNameDFS(itemNode, "publisher").forEach(node -> {
				Publisher publisher = new Publisher();
				try {
					publisher.setPublisher(xt.getNodeContentForceNotNull(node));
					publisherSet.add(publisher);
				} catch (IllegalArgumentException e) {
					ErrorLogger.write(location, book.getItem_id(), ErrType.PROGRAM, "publisher" ,e, xt.getNodeContentDFS(itemNode));
				} catch (XmlDataException e) {
					e.setLocation(location);
					e.setItem_id(book.getItem_id());
					e.setAttrName("publisher");
					ErrorLogger.write(e, xt.getNodeContentDFS(itemNode));
				}
			});


			Node bookspec = xt.getNodeByNameDFS(itemNode, "bookspec");
			try {
				Node isbn = xt.getNodeByNameDFS(bookspec, "isbn");
				book.setIsbn(xt.getNodeContentForceNullable(isbn));
				Node pages = xt.getNodeByNameDFS(bookspec, "pages");
				book.setPages(xt.getNodeContentForceNullable(pages));
				Node publication = xt.getNodeByNameDFS(bookspec, "publication");
				book.setPublication_date(xt.getNodeContentForceNullable(publication));
				
				this.testBook(book);
				book.setAuthors(authorSet);
				book.setPublishers(publisherSet);
				this.addBook(book, factory);
							
				
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
			} catch (Exception e) {
				ErrorLogger.write(location, book.getItem_id(), ErrType.PROGRAM, "", e, xt.getNodeContentDFS(itemNode));
			}
			
		});
	}	
		
	
}
