package mypackage;

import java.util.Hashtable;

public class XMLGenerator {
    String output;

    public XMLGenerator() {
        output = "";
    }

    public void addElement(String element) {
        output += "\t<" + element + " />\n";
    }

    public void addElement(String element, Hashtable<String, String> attr) {
        output += "\t<" + element + " ";
        for (String key : attr.keySet()) {
            output += key + "=\"" + attr.get(key) + "\" ";
        }
        output += "/>\n";
    }

    public String getOutput() {
		return "<message>\n" + output + "</message>";
    }
}