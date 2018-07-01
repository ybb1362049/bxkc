package cn.internetware.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author zhangying
 * @since 2014-8-29
 */
public class IO {
/*	public static String deserializeString(String filepath) throws IOException {
		int len;
		char[] chr = new char[4096];
		final StringBuilder  buffer = new StringBuilder();
		final FileReader reader = new FileReader(filepath);
		try {
			while ((len = reader.read(chr)) > 0) {
				buffer.append(chr, 0, len);
			}
		} finally {
			reader.close();
		}
		return buffer.toString();
	}*/
	
	public static String deserializeString(String filepath) throws IOException {
		return new String(deserializeString(filepath, "UTF-8"));
	}
	
	public static String deserializeString(final String filePath, String charsetName) throws IOException
	{
	  final char[] buffer = new char[4096];
	  final StringBuilder out = new StringBuilder();
	  try {
	    final Reader in = new InputStreamReader(new FileInputStream(filePath), charsetName);
	    try {
	      for (;;) {
	        int rsz = in.read(buffer, 0, buffer.length);
	        if (rsz < 0)
	          break;
	        out.append(buffer, 0, rsz);
	      }
	    }
	    finally {
	      in.close();
	    }
	  }
	  catch (UnsupportedEncodingException ex) {
	  }
	  return out.toString();
	}
	
	public static String deserializeStringFromInternet(String path) throws IOException {
		BufferedReader in = null;
        try {
        	URL url = new URL(path);
		    in = new BufferedReader(new InputStreamReader(url.openStream()));
		    final StringBuffer buffer = new StringBuffer();
		    String line;
		    while ((line = in.readLine()) != null) {
		    	buffer.append(line);
		    }
		    in.close();
		    return buffer.toString();
		}
		catch (MalformedURLException e) {
			//System.out.println("Malformed URL: " + e.getMessage());
		}
		catch (IOException e) {
			//System.out.println("I/O Error: " + e.getMessage());
		} finally {
			if(in != null)
				in.close();
		}
        return "";
	}
	
	public static String deserializeStringFromInternet(String path, String charSetName) throws IOException {
		BufferedReader in = null;
        try {
        	URL url = new URL(path);
		    in = new BufferedReader(new InputStreamReader(url.openStream(), charSetName));
		    final StringBuffer buffer = new StringBuffer();
		    String line;
		    while ((line = in.readLine()) != null) {
		    	buffer.append(line);
		    }
		    in.close();
		    return buffer.toString();
		}
		catch (MalformedURLException e) {
			//System.out.println("Malformed URL: " + e.getMessage());
		}
		catch (IOException e) {
			//System.out.println("I/O Error: " + e.getMessage());
		} finally {
			if(in != null)
				in.close();
		}
        return "";
	}
	
	public static Document getDocByContent(String content) {
		try {
			InputSource source = new InputSource(new StringReader(content.trim()));
			DOMParser parser = new DOMParser();
			parser.parse(source);
			Document doc = parser.getDocument();

			return doc;
		} catch (SAXException e) {
			//System.out.println("SAXException: " + e.getMessage());
		} catch(IOException e) {
			//System.out.println("IOException: " + e.getMessage());
		}

		return null;
	}
	
	public static final String[] CONTROL_TYPE_LIST = { "TEXTAREA", "SELECT", "INPUT" };
}

