package main;


import XmlTools.XmlTool;
import entity.Shop;

public class ProjectMain {
	public static void main(String[] args) {
		
		XmlTool xt = new XmlTool();
		xt.encodeFileToUTF_8(Config.DRESDEN_ORIGINAL);

		//shop
		Shop shop = new Shop(Config.DRESDEN_ENCODED);
		shop.readShop();
		shop.insertShop();
		shop = new Shop(Config.LEIPZIG);
		shop.readShop();
		shop.insertShop();
		shop.selectShop();
	}
}
