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

		String fileToRead  = "data/categories.xml";
		InputStream is = new FileInputStream(new File(fileToRead));

//		byte[] arr = new byte[1621269+100];

		byte[] arrB = new byte[3250];
		try {
			System.out.println("read:"+is.read(arrB));
//			System.out.println(new String(arrB, "ISO-8859-1"));
			for(byte b : arrB) {
//				System.out.println(b);
			}
			
//			System.out.println(new String(arrB, "ISO-8859-1"));
//			System.out.println(new String(arrB, "UTF-8"));

			/*
			byte[] arrToWrite = new String(arrB).getBytes("ISO-8859-1");

			OutputStream os = new FileOutputStream(fileToWrite, true);
			os.write("\n".getBytes());
			os.write(arrToWrite);
			os.flush();
			os.close();
			 */
			
			/*
		    BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileToRead),
		            "8859_1"));
		    String str;
			while((str = in.readLine()) != null) {
				System.out.println(str);
			}
			*/

			Path path = Paths.get(fileToRead);
			byte[] data = Files.readAllBytes(path);
			for (int i = 3188; i < 3200; i++) {
				System.out.print(data[i]+" ");
//				System.out.println(new String((char)data[i]));
			}
			System.out.println();
			System.out.println("s".getBytes()[0]);
			System.out.println("p".getBytes()[0]);
			System.out.println("i".getBytes()[0]);
			System.out.println("e".getBytes()[0]);
			System.out.println("l".getBytes()[0]);
			System.out.println("e".getBytes()[0]);
			
			String latin1 = new String(data, "ISO-8859-1");
			System.out.println(latin1);
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}

}
