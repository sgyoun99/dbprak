package main;


import XmlTools.XmlTool;
import entity.*;
import csv.*;

import org.hibernate.HibernateException; 
import org.hibernate.Session; 
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Date;

/**
 * Main class for project.
 *
 */
public class Main {

	private static SessionFactory factory; 

	/**
	 * main method for project.
	 * @param args args are not used.
	 */
	public static void main(String[] args) {
 

		Date dateAnf = new Date();
		//long secAnf = dateAnf.getTime();

		try {
			factory = new Configuration().configure().buildSessionFactory();
		} catch (Throwable ex) { 
			System.err.println("Failed to create sessionFactory object." + ex);
			throw new ExceptionInInitializerError(ex); 
		}
		
		try {
			
			ErrorLogger.willLogSQL_DUPLICATE = false;

			//drop and create tables
			//DropTables.dropTables();

			// die sind momentan hier drin, damit sie compiliert werden
			Item i = new Item();
			Shop s = new Shop();
			Item_Shop is = new Item_Shop();
			Similar_Items si = new Similar_Items();
			Book b = new Book();
			Author a = new Author();
			Publisher p = new Publisher();
			Actor ac = new Actor();
			Creator c = new Creator();
			Director d = new Director();
			Dvd dvd = new Dvd();
			Label l = new Label();
			Artist ar = new Artist();
			Title t = new Title();
			Music_CD cd = new Music_CD();
			Category cat = new Category();
			Review r = new Review();
			Customer cus = new Customer();

			
			//Encoding to UTF-8
			XmlTool xt = new XmlTool();
			xt.encodeDresdenXMLToUTF8();
			xt.encodeCategoriesXMLToUTF8();
			 
			//Shop
			ManageShop ms = new ManageShop();
			ms.manageShops(factory);
			
			//Item									//TODO: similiar Items
			ManageItem mi = new ManageItem();
			mi.readIn(factory);

			ManageItem_Shop mis = new ManageItem_Shop();
			mis.manageShopItems(factory);
			//ManageAuthor ma = new ManageAuthor();
			//ma.manageAuthor(factory);

			
			//DVD
			ManageDvd md = new ManageDvd();
			md.manageDvds(factory);
/*			Dvd dvd = new Dvd(Config.LEIPZIG, "Leipzig");
			dvd.dvd();
			dvd.actor();
			dvd.creator();
			dvd.director();
			dvd = new Dvd(Config.DRESDEN_ENCODED, "Dresden");
			dvd.dvd();
			dvd.actor();
			dvd.creator();
			dvd.director();*/
			
			//Book
			ManageBook mb = new ManageBook();
			mb.manageBooks(factory);
			/*Book book = new Book(Config.LEIPZIG, "Leipzig");
			book.book();
			book.author();
			book.publisher();
			book = new Book(Config.DRESDEN_ENCODED, "Dresden");
			book.book();
			book.author();
			book.publisher();*/

			//Music_CD
			ManageMusic_CD mc = new ManageMusic_CD();
			mc.manageCDs(factory);
/*			Music_CD music_cd = new Music_CD();
			music_cd.musicCdLeipzig();
			music_cd.musicCdDresden();*/
			
			//Review
			ManageReview mr = new ManageReview();
			mr.manageReviews(factory);
/*			Review review = new Review();
			review.writeReviewInDB();
			review.addRatings();*/
			
			//Category
			ManageCategory mcat = new ManageCategory();
			mcat.manageCategories(factory);
			/*Category cat = new Category();
			cat.readCategory();*/
 
		
		} catch (RuntimeException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("= = = Main.main() failed = = =");

			Date dateEnde = new Date();
			System.out.println("Dauer: " + (dateEnde.getTime()-dateAnf.getTime()) + " ms");
			System.out.println("Start:\t" + dateAnf.toString());
			System.out.println("Ende:\t" + dateEnde.toString());
		}
		
		System.out.println("= = = Main.main() complete = = =");
		
		Date dateEnde = new Date();
		System.out.println("Dauer:\t" + ((dateEnde.getTime()-dateAnf.getTime()) / 60000) + " min" + ( ( (dateEnde.getTime()-dateAnf.getTime() ) % 60000) / 1000) + " s");
		System.out.println("Start:\t" + dateAnf.toString());
		System.out.println("Ende:\t" + dateEnde.toString());
		


		factory.close();
		//show all table count.
		//CreateTables.countAllTables();
	}
}
