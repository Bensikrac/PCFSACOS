package container;

import java.util.ArrayList;
import plugins.Storage.DefaultStorage;
import plugins.Transfer.DefaultTransfer;
import plugins.UI.DefaultUI;

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
				found = true;
			}
		}
		if(!found) {
			throw new Exception(ExType.Plugin_Notfound.toString()); //if not found in case of default plugins something is wrong.
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
	
	public boolean isPluginExistent(String pluginname) {//search for other custom plugins
		boolean pfound = false;
		for(Plugin p : list) {
			if(p.getName().equals(pluginname)) {
				pfound = true;
			}
		}
		return pfound;
	}
	
	public Object sendMessage(Message m) throws Exception {
		return m.getTo().processMessage(m);
	}
	
	private void loadDefaultPlugins() throws Exception{
		Plugin defaults[] = new Plugin[6];
		//TODO insert all other default plugins
		
		defaults[0] = new DefaultUI(this);
		defaults[1] = new DefaultStorage(this);
		defaults[2] = new DefaultTransfer(this);
		
		
		for(Plugin p : defaults) {
			if(p != null) {
				p.load();
				list.add(p);
				log = log + "Loaded Default Plugin "+p.getName()+"\n";
			}
		}
	}
	

}
