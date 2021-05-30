package main;


import XmlTools.XmlTool;
import entity.Item;
import entity.Shop;

public class Main {
	public static void main(String[] args) {
		
		/*
		 */
		DropTables.dropTables();
		CreateTables.createTables();
		
		XmlTool xt = new XmlTool();
		xt.encodeFileToUTF_8(Config.DRESDEN_ORIGINAL);

		/*
		 */
		//shop
		Shop shop = new Shop(Config.DRESDEN_ENCODED);
		shop.readShop();
		shop.insertShop();
		shop = new Shop(Config.LEIPZIG);
		shop.readShop();
		shop.insertShop();
		shop.selectShop();
		
//		xt.loadXML(Config.DRESDEN_ENCODED);
//		xt.filterElementNodesDFS(xt.getDocumentNode(), level -> level > 3, node -> xt.hasAttribute(node, "asin")).forEach(node -> System.out.println(node.getTextContent()));
		
		Item item = new Item();
		item.dresden();
		System.out.println(item.insertCount);
	}
}
