package plugins.Storage;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Key;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import container.*;
public class DefaultStorage implements Plugin {
	private String configfile;
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
		
		configfile = initconf.getProperty("configlocation"); //read settings
		String encrypted = initconf.getProperty("encrypted");
		String encryptionkey = initconf.getProperty("EncryptionKey"); //check if encrypted 
		
		exists = Files.exists(Path.of(configfile)); //check if config file is there
		if(!exists) { 
			throw new Exception(ExType.File_Config_Notfound.toString());
		}
		
		
		
		
		if(encrypted.equals("true")) { //create object for config file reading if encrypted
			String result = "";
			if(encryptionkey == null) {
				byte[] encodedhash = null;
				String returned = (String)p.sendMessage(new Message(MsgType.Request, this, p.getPlugin("UI_Default"), MsgContent.UI_Popout_Input, new MessageData("Enter Your Password")));
				MessageDigest digest = MessageDigest.getInstance("SHA-256");
				encodedhash = digest.digest(returned.getBytes(StandardCharsets.UTF_8));
				StringBuilder hexString = new StringBuilder(2 * encodedhash.length); //copied from https://www.baeldung.com/sha-256-hashing-java
			    for (int i = 0; i < encodedhash.length; i++) {
			        String hex = Integer.toHexString(0xff & encodedhash[i]);
			        if(hex.length() == 1) {
			            hexString.append('0'); 
			        }
			        hexString.append(hex);
			    }
			    result = hexString.toString();
			}
			else {
				result = initconf.getProperty("EncryptionKey");
			}
			
			
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256"); //got from https://www.baeldung.com/java-aes-encryption-decryption
		    KeySpec spec = new PBEKeySpec(result.toCharArray(), result.getBytes(), 65536, 256);
		    SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES"); //TODO include encryption algorithm change
		    
		    byte[] iv = new byte[16];
		    SecureRandom secure = new SecureRandom();
		    secure.nextBytes(iv);
		    
		    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			
			try{
				FileInputStream fileIn = new FileInputStream(configfile);
				byte[] fileIv = new byte[16];
				fileIn.read(fileIv);
				cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(fileIv));
				CipherInputStream cipherIn = new CipherInputStream(fileIn, cipher);
				settings = new Properties();
				settings.load(cipherIn);
			}
			catch(Exception e) {
				System.out.println(e);
			}
		}
		else {
			settings = new Properties();
			settings.load(new FileInputStream(configfile));
		}
		System.out.println(settings.getProperty("test"));
		settings.setProperty("test", "true");
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
