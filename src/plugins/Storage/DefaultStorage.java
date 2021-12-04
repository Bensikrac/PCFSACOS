package plugins.Storage;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import javax.crypto.Cipher;

import container.*;
public class DefaultStorage implements Plugin {
	private Path configfile;
	private PluginManager p;
	private Properties settings; //for decrypted config file
	
	
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
			boolean returned = (Boolean)p.sendMessage(new Message(MsgType.Request,this, p.getPlugin("Default_UI"),MsgContent.UI_Popout_YesNo,new MessageData("INIT config is missing! create new?")));
			if(returned) {
				try {
				      File myObj = new File("init.conf"); //whacky copy paste code do not understand really 
				      FileInputStream fis = new FileInputStream("init.conf");
				      Properties initconf = new Properties();
				      initconf.load(fis);
				      // TODO maybe read default values from file or copy original file to this file
				      //now setting default values
				      initconf.setProperty("configlocation", "config.conf");
				      initconf.setProperty("encrypted", "false");
				      initconf.setProperty("encryptionkey", "");
				}
				catch (Exception e) {
					throw new Exception(ExType.File_Init_Notfound.toString());
				}
			}
			else {
				throw new Exception(ExType.File_Init_Notfound.toString());
			}
		}
		
		//now read encryption and decrypt file
		Properties initconf = new Properties(); //properties object (property = value in file parser)
		FileInputStream fis = new FileInputStream("init.conf");
		initconf.load(fis);
		
		configfile = Path.of(initconf.getProperty("configlocation")); //read settings
		String encrypted = initconf.getProperty("encrypted");
		String encryptionkey = initconf.getProperty("EncryptionKey"); //check if encrypted 
		//TODO include encryption algorithm change
		
		
		if(encrypted.equals("true") && encryptionkey == null) {
			String returned = (String)p.sendMessage(new Message(MsgType.Request, this, p.getPlugin("UI_Default"), MsgContent.UI_Popout_Input, new MessageData("Enter Your Password")));
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); //not secure I guess maybe make settable in settings or something
			cipher.init(Cipher.DECRYPT_MODE, returned);
			
			return; 
		}
		
		
		exists = Files.exists(configfile); //check if config file is there
		if(!exists) { 
			throw new Exception(ExType.File_Config_Notfound.toString());
		}
	}
	
	public void run() {
		
	}
	

	@Override
	public String getName() {
		return "Storage_Default";
	}

	@Override
	public Object processMessage(Message m) { //Consider Config values prefixed with plugin type like Storage_Default.fontsize = 2 to avoid collisions.
		//also should might use database or hashmap if config values are read often
		
		// TODO Auto-generated method stub
		return false;
	}
}
