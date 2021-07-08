package main;


import XmlTools.XmlTool;
import entity.*;
import csv.*;

import java.util.Date;

/**
 * Main class for project.
 *
 */
public class Main {
	/**
	 * main method for project.
	 * @param args args are not used.
	 */
	public static void main(String[] args) {

		Date dateAnf = new Date();
		//long secAnf = dateAnf.getTime();
		
		try {
			
			ErrorLogger.willLogSQL_DUPLICATE = false;

			//drop and create tables
			DropTables.dropTables();
			CreateTables.createTables();
			
			//Encoding to UTF-8
			XmlTool xt = new XmlTool();
			xt.encodeDresdenXMLToUTF8();
			xt.encodeCategoriesXMLToUTF8();
			 
			//Shop
			Shop shop = new Shop(Config.DRESDEN_ENCODED);
			shop.readShop();
			shop.insertShop();
			shop = new Shop(Config.LEIPZIG);
			shop.readShop();
			shop.insertShop();
			shop.selectShop();
			
			//Item
			Item item = new Item();
			item.dresden();
			item.leipzig();
			
			Item_Shop item_shop = new Item_Shop();
			item_shop.dresden();
			item_shop.leipzig();

			//Similar Items
			Similar_Items simItems = new Similar_Items();
			simItems.dresden();
			simItems.leipzig();
			
			//DVD
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
			
			//Book
			Book book = new Book(Config.LEIPZIG, "Leipzig");
			book.book();
			book.author();
			book.publisher();
			book = new Book(Config.DRESDEN_ENCODED, "Dresden");
			book.book();
			book.author();
			book.publisher();

			//Music_CD
			Music_CD music_cd = new Music_CD();
			music_cd.musicCdLeipzig();
			music_cd.musicCdDresden();
			
			//Review
			Review review = new Review();
			review.writeReviewInDB();
			review.addRatings();
			
			//Category
			Category cat = new Category();
			cat.readCategory();
 
		
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
		System.out.println("Dauer:\t" + ((dateEnde.getTime()-dateAnf.getTime()) / 60000) + " min" + ( ( (dateEnde.getTime()-dateAnf.getTime() ) % 60000) % 1000) + " s");
		System.out.println("Start:\t" + dateAnf.toString());
		System.out.println("Ende:\t" + dateEnde.toString());
		
		//show all table count.
		CreateTables.countAllTables();
	}
}
