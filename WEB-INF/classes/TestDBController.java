package mypackage;

public class TestDBController {
	public static void main(String[] args) throws Exception {
		DBController db = new DBController();
		db.init();
		System.out.println("Succesful");
	}
}