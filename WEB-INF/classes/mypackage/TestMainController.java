package mypackage;

import java.util.*;

public class TestMainController {
	public String testGenSessionId() throws Exception {
		MainController mc = new MainController();
		return mc.genSessionId();
	}
	
	public String testIsLoginNotExpired() throws Exception {
		MainController mc = new MainController();
		DBController db = new DBController();
		db.init();
		/*String sessionId = "HIYCDVAIVLBVPNREWFCB";
		String userType = "Admin";
		String sessionId = "ZYTJZDXHMWTLNVQKINNM123";
		String userType = "Host";*/
		String sessionId = "UORNRJSJIQIBGYUWCJLZ";
		String userType = "Guest";
		return mc.isLoginNotExpired(db, sessionId, userType) + "";
	}
	
	public String testRenewSession() throws Exception {
		MainController mc = new MainController();
		XMLGenerator xmlGen = new XMLGenerator();
		DBController db = new DBController();
		db.init();
		String sessionId = "HTNEEBMEKVHIPMZAFEBD";
		String userType = "Guest";
		return mc.renewSession(xmlGen, db, sessionId, userType) + "";
	}
	
	public String testIsHostHasRight() throws Exception {
		MainController mc = new MainController();
		DBController db = new DBController();
		db.init();
		String sessionId = "LYEFMINEJAOOFVIFSDGL";
		String table = "descriptor";
		String operation = "select";
		return mc.isHostHasRight(db, sessionId, table, operation) + "";
	}
	
	public String testIsGuestHasRight() throws Exception {
		MainController mc = new MainController();
		String table = "bidrecord";
		String operation = "insert";
		return mc.isGuestHasRight(table, operation) + "";
	}
	
	public String testDoLogin() throws Exception {
		MainController mc = new MainController();
		XMLGenerator xmlGen = new XMLGenerator();
		DBController db = new DBController();
		db.init();
		String username = "admin";
		String password = "admin";
		String userType = "admin";
		return mc.doLogin(xmlGen, db, username, password, userType, 1);
	}
	
	/*public String testSendBlankMessage() throws Exception {
		MainController mc = new MainController();
		return mc.getResponse("<message></message>");
	}
	
	public String testSendLoginMessage() throws Exception {
		MainController mc = new MainController();
		return mc.getResponse("<message><login username=\"guest\" password=\"guest\" userType=\"Guest\" /></message>");
	}
	
	public String testSendLogoutMessage() throws Exception {
		MainController mc = new MainController();
		return mc.getResponse("<message><logout sessionId=\"JMVQCBXCQEKHQCLFIFHT\" userType=\"Guest\" /></message>");
	}
	
	public String testSelect() throws Exception {
		MainController mc = new MainController();
		return mc.getResponse("<message sessionId=\"LVVABDDGTONGGQDWKEOE\" userType=\"Host\"><select table=\"finance\" /></message>");
	}*/
}