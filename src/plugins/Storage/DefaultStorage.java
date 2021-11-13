package plugins.Storage;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import container.*;
public class DefaultStorage implements Plugin {
	private Path configfile;
	
	
	public DefaultStorage(PluginManager pluginManager) {
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
			throw new Exception(ExType.File_Init_Notfound.toString());
		}
		Properties initconf = new Properties();
		FileInputStream fis = new FileInputStream("init.conf");
		initconf.load(fis);
		
		configfile = Path.of(initconf.getProperty("configlocation")); //read settings
		String encrypted = initconf.getProperty("encrypted");
		String encryptionkey = initconf.getProperty("EncryptionKey");
		
		
		if(encrypted.equals("true") && encryptionkey == null) {
			System.out.println("Enter Encryption Key please");
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
