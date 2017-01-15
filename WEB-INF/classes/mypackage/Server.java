package mypackage;

import java.net.*;
import java.io.*;
import java.lang.*;
import java.util.*;
import java.util.concurrent.*;

public class Server {
	ExecutorService pool;
	ServerSocket ss;
	ArrayList<OutputStream> outList;
	DBController db;
	MainController mc = new MainController(); // should put an existing main controller into contructor in production version
	int currentPrice = 1000; // internal storage, no need to ask database every time
	int bidIncrement = 100;
	int whichItem = 1;
	Object lock = new Object();
	
	public Server() {
		pool = Executors.newCachedThreadPool();
	}
	
	public void startListening(int port) throws Exception {
		db = new DBController();
		db.init();
		pool.submit(new ListenTask(this, port));
	}
	
	public void endListening() throws Exception {
		pool.shutdownNow();
		if (!pool.awaitTermination(100, TimeUnit.MICROSECONDS)) {
			System.exit(0);
		}
	}
	
	/*public static void main(String[] args) throws Exception {
		Server server = new Server();
		server.startListening(12345);
		Thread.sleep(60 * 1000);
		server.endListening();
	}*/
}
class ListenTask implements Callable {
	Server server;
	int port;
	
	ListenTask(Server server, int port) {
		this.server = server;
		this.port = port;
	}
	
	public Object call() throws Exception {
		server.outList = new ArrayList<>();
		server.ss = new ServerSocket(port);
		System.out.println("Listening");
		while (true) {
			Socket socket = server.ss.accept();
			server.outList.add(socket.getOutputStream());
			System.out.println("Client accepted");
			server.pool.submit(new RequestHandler(server, socket));
		}
	}
}
class RequestHandler implements Callable {
	Server server;
	Socket socket;
	XMLParser xmlParse;
	XMLGenerator xmlGen;
	boolean joined;
	String guestName = null;
	
	RequestHandler (Server server, Socket socket) {
		this.server = server;
		this.socket = socket;
		joined = false;
	}
	
	public Object call() throws Exception {
		InputStream in = socket.getInputStream();
		OutputStream out = socket.getOutputStream();
		String input = "";
		while (true) {
			byte[] b = new byte[1000];
			if (in.read(b) < 1) break;
			input = new String(b, "UTF-8").trim();
			//System.out.println(input);
			xmlParse = new XMLParser(input);
			xmlParse.invoke();
			Hashtable<String, String> ht = new Hashtable<>();
			for (int i = 0; i < xmlParse.getElementList().size(); i++) {
				xmlGen = new XMLGenerator();
				//System.out.println("Element: " + xmlParse.getElementList().get(i));
				if (xmlParse.getElementList().get(i).equals("join")) {
					String sessionId = xmlParse.getAttrList().get(i).get("sessionId");
					if (joined == true) {
						ht.put("status", "failed");
						ht.put("description", "Already joined.");
					} else if (server.mc.isLoginNotExpired(server.db, sessionId, "Guest")) {
						server.mc.renewSession(xmlGen, server.db, sessionId, "Guest", 60); // assume auction lasts for 60 sec
						joined = true;
						guestName = xmlParse.getAttrList().get(i).get("guestName");
						ht.put("status", "ok");
						ht.put("description", "Join successful.");
					} else {
						ht.put("status", "failed");
						ht.put("description", "Incorrect session ID / Login expired");
					}
				} else if (xmlParse.getElementList().get(i).equals("bid")) {
					//System.out.println("bid");
					if (joined == false) {
						ht.put("status", "failed");
						ht.put("description", "Not joined.");
					} else {
						int cPrice = Integer.parseInt(xmlParse.getAttrList().get(i).get("currentPrice"));
						int bIncr = Integer.parseInt(xmlParse.getAttrList().get(i).get("bidIncrement"));
						synchronized(server.lock) {
							if (cPrice == server.currentPrice && bIncr == server.bidIncrement) {
								server.currentPrice += server.bidIncrement;
								server.db.update("bidrecord", "currentPrice = " + server.currentPrice, "id = " + server.whichItem);
								ht.put("status", "ok");
								ht.put("description", "Bid successful.");
								Hashtable<String, String> a = new Hashtable<>();
								XMLGenerator gen = new XMLGenerator();
								a.put("currentPrice", server.currentPrice + "");
								a.put("bidIncrement", server.bidIncrement + "");
								a.put("guestName", guestName);
								gen.addElement("broadcast", a);
								for (OutputStream os: server.outList) {
									os.write(gen.getOutput().getBytes("UTF-8"));
									os.flush();
								}
							} else {
								ht.put("status", "failed");
								ht.put("description", "Bid failed due to expired price values.");
							}
						}
					}
				}
				xmlGen.addElement("response", ht);
				String output = xmlGen.getOutput() + "\n";
				out.write(output.getBytes("UTF-8"));
				out.flush();
			}
		}
		return null;
	}
}