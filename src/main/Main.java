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
			
			Dvd dvd = new Dvd();
			dvd.leipzig();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("= = = Main.main() failed = = =");
		}
		
		System.out.println("= = = Main.main() complete = = =");
	}
}
