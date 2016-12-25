package mypackage;

import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import java.sql.*;
import java.util.*;
import java.io.*;

public class XMLParser extends DefaultHandler {
	private String fileContent;
	private ArrayList<String> elementList;
	private ArrayList<Hashtable<String, String>> attrList;
	
	public XMLParser(String fileContent) {
		this.fileContent = fileContent;
	}
	
	public void startDocument () throws SAXException {
		elementList = new ArrayList<>();
		attrList = new ArrayList<>();
	}
	
	public void startElement(String namespaceURI, 
							String localName, 
							String qName, 
							Attributes atts) throws SAXException {
		elementList.add(localName);
		Hashtable<String, String> ht = new Hashtable<>();
		for (int i = 0; i < atts.getLength(); i++)
			ht.put(atts.getQName(i), atts.getValue(i));
		attrList.add(ht);
	}
	
	public void invoke() throws ParserConfigurationException,
							SAXException,
							IOException {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setNamespaceAware(true);
		SAXParser saxParser = spf.newSAXParser();
		XMLReader xmlReader = saxParser.getXMLReader();
		xmlReader.setContentHandler(this);
		xmlReader.parse(new InputSource(new StringReader(fileContent)));
	}
	
	public ArrayList<String> getElementList() {
		return elementList;
	}
	
	public ArrayList<Hashtable<String, String>> getAttrList() {
		return attrList;
	}
}