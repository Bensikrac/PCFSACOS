package container;
import java.util.ServiceLoader;

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
		catch(Exception e) {
			System.out.println(e);
		}
		System.out.println(p.getLog());
		
		
	}

}
