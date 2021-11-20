package plugins.Storage;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import container.*;
public class DefaultStorage implements Plugin {
	private Path configfile;
	private PluginManager p;
	
	
	public DefaultStorage(PluginManager pluginManager) {
		this.p = pluginManager;
		
		// TODO Auto-generated constructor stub
	}

	public PluginType getType() {
		return PluginType.Storage;
	}
	
	public void unload() {
		
	}
	
	public void load() throws Exception {
		//First get config file location from programm location
		Path initpath = Path.of("init.conf");
		boolean exists = Files.exists(initpath);
		if(!exists) {
			p.sendMessage(new Message(MsgType.Request,this, p.getPlugin("Default_UI"),MsgContent.UI_Popout_YesNo,new MessageData("INIT config is missing! create new?")));
			
			//TODO fix recreation after message recieve
			throw new Exception(ExType.File_Init_Notfound.toString());
		}
		Properties initconf = new Properties();
		FileInputStream fis = new FileInputStream("init.conf");
		initconf.load(fis);
		
		configfile = Path.of(initconf.getProperty("configlocation")); //read settings
		String encrypted = initconf.getProperty("encrypted");
		String encryptionkey = initconf.getProperty("EncryptionKey");
		
		
		if(encrypted.equals("true") && encryptionkey == null) {
			p.sendMessage(new Message(MsgType.Request, this, p.getPlugin("UI_Default"), MsgContent.UI_Popout_Input, new MessageData("Enter Your Password")));
			return; //not yet implemented what happens if config file is encrypted without stored key
		}
		
		
		exists = Files.exists(configfile); //check if config file is there
		if(!exists) { 
			throw new Exception(ExType.File_Config_Notfound.toString());
		}
	}
	
	public void run() {
		
	}
	
	private void decrypt() {
		
	}

	@Override
	public String getName() {
		return "Storage_Default";
	}

	@Override
	public boolean processMessage(Message m) {
		// TODO Auto-generated method stub
		return false;
	}
}
