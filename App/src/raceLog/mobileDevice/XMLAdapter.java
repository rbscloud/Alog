package raceLog.mobileDevice;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.content.Context;
import android.util.Log;
import android.widget.SimpleAdapter;

/**
 * Converts a XML list to a SimpleAdapter to be presented by a ListView
 * 
 * @author Mads
 */
public class XMLAdapter {
	public static SimpleAdapter getAdapter(Context context, String xml, String elementName, String[] elementFields,
			int viewElement, String[] showFields, int[] viewFields) {

		ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
		Document doc = XMLfromString(xml);

		NodeList nodes = doc.getElementsByTagName(elementName);

		for (int i = 0; i < nodes.getLength(); i++) {
			HashMap<String, String> map = new HashMap<String, String>();

			Element e = (Element) nodes.item(i);
			for (String ef : elementFields) {
				Log.d(ef, getValue(e, ef));
				map.put(ef, getValue(e, ef));
			}
			mylist.add(map);
		}
		return new SimpleAdapter(context, mylist, viewElement, showFields, viewFields);

	}

	public final static Document XMLfromString(String xml) {
		Document doc = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));
			doc = db.parse(is);
		} catch (ParserConfigurationException e) {
			System.out.println("XML parse error: " + e.getMessage());
			return null;
		} catch (SAXException e) {
			System.out.println("Wrong XML file structure: " + e.getMessage());
			return null;
		} catch (IOException e) {
			System.out.println("I/O exeption: " + e.getMessage());
			return null;
		}
		return doc;
	}

	/**
	 * Returns element value
	 * 
	 * @param elem element (it is XML tag)
	 * @return Element value otherwise empty String
	 */
	public final static String getElementValue(Node elem) {
		Node kid;
		if (elem != null) {
			if (elem.hasChildNodes()) {
				for (kid = elem.getFirstChild(); kid != null; kid = kid.getNextSibling()) {
					if (kid.getNodeType() == Node.TEXT_NODE) {
						return kid.getNodeValue();
					}
				}
			}
		}
		return "";
	}

	public static String getValue(Element item, String str) {
		NodeList n = item.getElementsByTagName(str);
		return getElementValue(n.item(0));
	}

}
