package container;

import java.util.ArrayList;
import plugins.Storage.DefaultStorage;
import plugins.Transfer.DefaultTransfer;

public class PluginManager { //Manage Plugins load unload error messages interplugin messaging
	private ArrayList<Plugin> list = new ArrayList<Plugin>();
	private String log = "";
	
	
	public ArrayList<Plugin> getList(){
		return list;
	}
	
	public Plugin getPlugin(String Pluginname) throws Exception{
		boolean found = false;
		Plugin pfound = null;
		for(Plugin p : list) {
			if(p.getName().equals(Pluginname)) {
				pfound = p;
			}
		}
		if(!found) {
			throw new Exception(ExType.Plugin_Notfound.toString());
		}
		return pfound;
	}
	
	public void loadPlugins() throws Exception{
		//TODO load plugins out of jars first
		loadDefaultPlugins();
	}
	
	public String getLog() {
		return log;
	}
	
	private void loadDefaultPlugins() throws Exception{
		Plugin defaults[] = new Plugin[6];
		//TODO insert all other default plugins
		
		defaults[0] = new DefaultStorage();
		defaults[1] = new DefaultTransfer();
		
		for(Plugin p : defaults) {
			if(p != null) {
				p.load();
				list.add(p);
				log = log + "Loaded Default Plugin "+p.getName()+"\n";
			}
		}
	}
	

}
