package container;
import plugins.Storage.*;
public class Main {

	public static void main(String[] args) {
		Plugin test = new DefaultStorage();
		try {
			test.load();
		}
		catch(Exception e) {
			System.out.println(e);
		}
		
	}

}
