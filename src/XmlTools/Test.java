package XmlTools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import entity.Shop;
import exception.XmlDataException;
import main.Config;
import main.ErrorLogger;

public class Test {
	
	static int count = 0;
	
	public static void dumpString(String text)
	{
	    for (int i=0; i < text.length(); i++)
	    {
	        System.out.println("U+" + Integer.toString(text.charAt(i), 16) 
	                           + " " + text.charAt(i));
	    }
	}

	public static void main(String[] args) throws Exception {

		String fileToRead  = Config.CATEGORY_ORIGINAL;
		String fileToWrite = Config.CATEGORY_ENCODED;

		try {
			InputStream is = new FileInputStream(new File(fileToRead));
			OutputStream os = new FileOutputStream(fileToWrite, true);
			
		    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileToRead), "ISO-8859-1"));
			br.readLine(); // skip the first line and write <?xml version=\"1.0\" encoding=\"UTF-8\"?>
			os.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n".getBytes("UTF-8")); //specify encoding
		    String str;
			while((str = br.readLine()) != null) {
				String strInUTF8 = new String(str.getBytes(), "UTF-8");
				System.out.println(strInUTF8);
				os.write(strInUTF8.getBytes("UTF-8"));
				os.write("\n".getBytes());
			}
			os.flush();
			os.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
