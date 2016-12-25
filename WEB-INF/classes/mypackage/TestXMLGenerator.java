package mypackage;

import java.util.*;

public class TestXMLGenerator {
	static XMLGenerator xmlGen = new XMLGenerator();
	
	public static void main(String[] args) {
		addAttr();
	}
	
	public static void emptyMessage() {
		System.out.println("Get empty message");
		System.out.println(xmlGen.getOutput());
	}
	
	public static void addElement() {
		System.out.println("Add Element");
		xmlGen.addElement("select");
		xmlGen.addElement("insert");
		System.out.println(xmlGen.getOutput());
	}
	
	public static void addAttr() {
		System.out.println("Add Attribute");
		Hashtable<String, String> ht = new Hashtable<>();
		ht.put("table", "Guest");
		//ht.put("column", "username, sessionId");
		ht.put("condition", "id = 3");
		xmlGen.addElement("select", ht);
		xmlGen.addElement("insert", ht);
		System.out.println(xmlGen.getOutput());
	}
}