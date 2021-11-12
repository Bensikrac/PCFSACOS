package plugins.Storage;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import container.*;
public class DefaultStorage implements Plugin {
	private Path configfile;
	
	
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
			throw new Exception("The init file is missing.");
		}
		Properties initconf = new Properties();
		FileInputStream fis = new FileInputStream("init.conf");
		initconf.load(fis);
		configfile = Path.of(initconf.getProperty("configlocation")); //read settings
		String encrypted = initconf.getProperty("encrypted");
		String encryptionkey = initconf.getProperty("EncryptionKey");
		System.out.println(encrypted);
		if(encrypted.equals("true") && encryptionkey == null) {
			System.out.println("Enter Encryption Key please");
			return; //not yet implemented what happens if config file is encrypted without stored key
		}
		
		
		
		
		
		
	}
	
	public void run() {
		
	}

}
