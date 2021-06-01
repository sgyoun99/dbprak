package main;


public class Config {

	public final static String SRC_LOCATION = Config.class.getResource("").getPath();
	public final static String DATA_DIRECTORY = SRC_LOCATION+"/../../data";
	public final static String UTF8_SURFIX = "__to__UTF-8.xml";
	public final static String LEIPZIG = DATA_DIRECTORY+"/leipzig_transformed.xml";
	public final static String DRESDEN_ORIGINAL = DATA_DIRECTORY+"/dresden.xml";
	public final static String DRESDEN_ENCODED = DRESDEN_ORIGINAL + UTF8_SURFIX;

}
