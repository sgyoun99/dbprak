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
//public class Main {
public class DataLoader {

	private SessionFactory factory; 

	public DataLoader(SessionFactory factory) {
		this.factory = factory;
	}

	/**
	 * main method for project.
	 * @param args args are not used.
	 */
//	public static void main(String[] args) {
	public void load() {
 

		Date dateAnf = new Date();
		//long secAnf = dateAnf.getTime();


		
		try {
			
			ErrorLogger.willLogSQL_DUPLICATE = false;


			// die sind momentan hier drin, damit sie compiliert werden
			Item i = new Item();
			Shop s = new Shop();
			Item_Shop is = new Item_Shop();
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
			
			//Book
			ManageBook mb = new ManageBook();
			mb.manageBooks(factory);

			//Music_CD
			ManageMusic_CD mc = new ManageMusic_CD();
			mc.manageCDs(factory);
			
			//Review
			ManageReview mr = new ManageReview();
			mr.manageReviews(factory);
			
			//Category
			ManageCategory mcat = new ManageCategory();
			mcat.manageCategories(factory);
 
		
		} catch (RuntimeException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
//			System.out.println("= = = Main.main() failed = = =");
			System.out.println("= = = Data Loading has failed = = =");

			Date dateEnde = new Date();
			System.out.println("Dauer: " + (dateEnde.getTime()-dateAnf.getTime()) + " ms");
			System.out.println("Start:\t" + dateAnf.toString());
			System.out.println("Ende:\t" + dateEnde.toString());
		}
		
//		System.out.println("= = = Main.main() complete = = =");
		System.out.println("= = = Data Loading complete = = =");
		
		Date dateEnde = new Date();
		System.out.println("Dauer:\t" + ((dateEnde.getTime()-dateAnf.getTime()) / 60000) + " min" + ( ( (dateEnde.getTime()-dateAnf.getTime() ) % 60000) / 1000) + " s");
		System.out.println("Start:\t" + dateAnf.toString());
		System.out.println("Ende:\t" + dateEnde.toString());
		


	}
}
