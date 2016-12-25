package mypackage;

import java.util.*;

public class TestXMLParser {
	static XMLParser xmlParse = null;
	
	public static void main(String[] args) throws Exception {
		parseMulAttr();
	}
	
	public static void parseEmpty() throws Exception {
		xmlParse = new XMLParser("<message></message>");
		xmlParse.invoke();
		printElementList();
		printAttrList();
	}
	
	public static void parseOneAttr() throws Exception {
		xmlParse = new XMLParser(
			"<message>" + 
			"<select table=\"Guest\" />" + 
			"</message>"
		);
		xmlParse.invoke();
		printElementList();
		printAttrList();
	}
	
	public static void parseMulAttr() throws Exception {
		xmlParse = new XMLParser(
			"<message>" + 
			"<select table=\"Guest\" column=\"id, username\" condition=\"id=3 AND username='aaa'\" />" + 
			"<insert table=\"Guest\" values=\"3, 'bbb'\"/>" + 
			"</message>"
		);
		xmlParse.invoke();
		printElementList();
		printAttrList();
	}
	
	public static void printElementList() {
		System.out.println("Print Element List");
		for (String s : xmlParse.getElementList()) {
			System.out.println(s);
		}
	}
	
	public static void printAttrList() {
		System.out.println("Print Attribute List");
		int i = 0;
		ArrayList<String> elementList = xmlParse.getElementList();
		for (Hashtable<String, String> ht : xmlParse.getAttrList()) {
			for (String key : ht.keySet()) {
				System.out.println("From element \"" + elementList.get(i) + "\": " + key + " : " + ht.get(key));
			}
			i++;
		}
	}
}