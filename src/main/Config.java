package main;

/**
 * Configuration class for connection and XML path.
 *
 */
public class Config {
	public final static String JDBC_POSTGRES_URL = "jdbc:postgresql://localhost/postgres?user=postgres&password=postgres&ssl=false";
	public final static String BIN_LOCATION = Config.class.getResource("").getPath();
	public final static String SRC_LOCATION =   BIN_LOCATION+"/../../src";
	public final static String DATA_DIRECTORY = BIN_LOCATION+"/../../data";
	public final static String UTF8_SURFIX = "__to__UTF-8.xml";
	public final static String LEIPZIG = DATA_DIRECTORY+"/leipzig_transformed.xml";
	public final static String DRESDEN_ORIGINAL = DATA_DIRECTORY+"/dresden.xml";
	public final static String DRESDEN_ENCODED = DRESDEN_ORIGINAL + UTF8_SURFIX;
	public final static String CATEGORY_ORIGINAL = DATA_DIRECTORY+"/categories.xml";
	public final static String CATEGORY_ENCODED = CATEGORY_ORIGINAL + UTF8_SURFIX;

}
