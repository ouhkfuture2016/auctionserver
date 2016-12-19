package mypackage;

import java.util.Hashtable;

public class XMLGenerator {
	public static String getSuccessResponse(String description) {
		return "<response\n\tstatus=\"ok\"\n\tdescription=\"" + description + "\"\n/>";
	}
	
	public static String getSuccessResponse(String description, Hashtable<String, String> ht) {
		String temp = getSuccessResponse(description);
		temp = temp.substring(0, temp.length()-2) + "\t";
		for (String s : ht.keySet()) {
			temp += s + "=\"" + ht.get(s) + "\"\n\t";
		}
		return temp.substring(0, temp.length()-2) + "/>";
	}
	
	public static String getFailedResponse(String description) {
		return "<response\n\tstatus=\"failed\"\n\tdescription=\"" + description + "\"\n/>";
	}
}