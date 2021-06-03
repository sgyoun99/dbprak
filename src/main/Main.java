package main;


import XmlTools.XmlTool;
import entity.*;

public class Main {
	public static void main(String[] args) {
		
		try {
			
			/*
			 */
			DropTables.dropTables();
			CreateTables.createTables();
			
			//Encoding to UTF-8
			XmlTool xt = new XmlTool();
			xt.encodeFileToUTF_8(Config.DRESDEN_ORIGINAL);

			/*
			 */
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
			
			Book book = new Book(Config.LEIPZIG, "Leipzig");
			book.book();
			book.author();
			book.publisher();
			book = new Book(Config.DRESDEN_ENCODED, "Dresden");
			book.book();
			book.author();
			book.publisher();

			Music_CD music_cd = new Music_CD();
			music_cd.musicCdLeipzig();
			music_cd.musicCdDresden();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("= = = Main.main() failed = = =");
		}
		
		System.out.println("= = = Main.main() complete = = =");
	}
}
