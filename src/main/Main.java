package main;


import XmlTools.XmlTool;
import entity.Item;
import entity.Item_Shop;
import entity.Shop;

public class Main {
	public static void main(String[] args) {
		
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
		
		System.out.println("= = = Main.main() complete = = =");
	}
}
