package container;
import java.util.ServiceLoader;

import javax.swing.JOptionPane;

import plugins.Storage.*;
public class Main {

	public static void main(String[] args) {
		/*ServiceLoader<Plugin> loader = ServiceLoader.load(Plugin.class);
				for (Plugin p : loader) {
					try {
				    p.load();
					}
					catch(Exception e) {
						System.out.println(e);
					}
				}
		*/
		/*Plugin test = new DefaultStorage();
		try {
			test.load();
		}
		catch(Exception e) {
			System.out.println(e);
		}*/
		PluginManager p = new PluginManager();
		try {
			p.loadPlugins();
		}
		catch(Exception e1) {
			System.out.println(e1);
			e1.printStackTrace();
		}
		
		
		
		
		
	}

}
