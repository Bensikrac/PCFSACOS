package plugins.Storage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.AlgorithmParameters;
import java.security.MessageDigest;
import java.security.spec.KeySpec;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import container.*;
public class DefaultStorage implements Plugin {
	private String configfilelocation;
	private PluginManager p;
	private Properties configsettings; //for decrypted config file
	private Properties initsettings;
	
	private final String initfile = "init.conf";
	
	
	public DefaultStorage(PluginManager pluginManager) {
		this.p = pluginManager;
	}

	public PluginType getType() {
		return PluginType.Storage;
	}
	
	public void unload() {
		
	}
	
	public void load() throws Exception {
		//check if init file exists and recreate if needed
		Path initpath = Path.of(initfile);
		boolean exists = Files.exists(initpath);
		if(!exists) {
			recreateInitFile();
		}
		
		initsettings = new Properties();
		configsettings = new Properties();
		initsettings.load(new FileInputStream(initfile));
		configfilelocation = initsettings.getProperty("configlocation"); //read settings
		String encrypted = initsettings.getProperty("encrypted");
		String encryptionkey = initsettings.getProperty("encryptionkey"); //check if encrypted 
		if(configfilelocation != null) {
			exists = Files.exists(Path.of(configfilelocation)); //check if config file is there
			if(!exists) { 
				recreateConfigFile();
			}
		}
		else {
			recreateConfigFile();
		}
		
		
		
		//encrypted and unencrypted handling
		String result = "";
		if(encrypted.equals("true")) { //create object for config file reading if encrypted
			
			if(encryptionkey == null) {
				String returned = (String)p.sendMessage(new Message(MsgType.Request, this, p.getPlugin("UI_Default"), MsgContent.UI_Popout_Input, new MessageData("Enter Your Password")));
				result = hashPassword(returned);
			}
			else {
				result = initsettings.getProperty("encryptionkey");
			}
			configsettings.load(decryptEncryptedFile(configfilelocation, result));
		}
		else {
			configsettings.load(new FileInputStream(configfilelocation));
		}
		
		initsettings.store(new FileOutputStream(initfile),null);
		if(encrypted.equals("true")){
			OutputStream o = encryptEncryptedFile(configfilelocation, result);
			configsettings.store(o, null);
			o.close();
		}
		else {
			configsettings.store(new FileOutputStream(configfilelocation), null);
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
	
	private void recreateInitFile() throws Exception {
		boolean returned = (Boolean)p.sendMessage(new Message(MsgType.Request,this, p.getPlugin("UI_Default"),MsgContent.UI_Popout_YesNo,new MessageData("INIT config is missing! create new?")));
		if(!returned) {
			throw new Exception(ExType.File_Init_Notfound.toString());
		}
		Properties settings = new Properties();
		// TODO maybe read default values from file or copy original file to this file
		//now setting default values
		settings.setProperty("configlocation", "config.conf");
		settings.setProperty("encrypted", "false");
		settings.setProperty("encryptionkey", "");
		settings.store(new FileOutputStream(initfile),null);
	}
	
	private void recreateConfigFile() throws Exception {
		boolean returned = (Boolean)p.sendMessage(new Message(MsgType.Request,this, p.getPlugin("UI_Default"),MsgContent.UI_Popout_YesNo,new MessageData("Config File missing. Crete new?")));
		if(!returned) {
			throw new Exception(ExType.File_Config_Notfound.toString());
		}
		boolean encrypt = (Boolean)p.sendMessage(new Message(MsgType.Request,this, p.getPlugin("UI_Default"),MsgContent.UI_Popout_YesNo,new MessageData("Encrypt config file?")));
		if(encrypt) {
			initsettings.setProperty("encrypted", "true");
			//getting password and verifying if its the same
			String passwd = (String)p.sendMessage(new Message(MsgType.Request,this, p.getPlugin("UI_Default"),MsgContent.UI_Popout_Input,new MessageData("Password for config file:")));
			String passwd2 = (String)p.sendMessage(new Message(MsgType.Request,this, p.getPlugin("UI_Default"),MsgContent.UI_Popout_Input,new MessageData("Retype Password:")));
			while(!passwd.equals(passwd2)) {
				p.sendMessage(new Message(MsgType.Request,this, p.getPlugin("UI_Default"),MsgContent.UI_Popout_Error,new MessageData("Passwords don't match. Please try again")));
				passwd = (String)p.sendMessage(new Message(MsgType.Request,this, p.getPlugin("UI_Default"),MsgContent.UI_Popout_Input,new MessageData("Password for config file:")));
				passwd2 = (String)p.sendMessage(new Message(MsgType.Request,this, p.getPlugin("UI_Default"),MsgContent.UI_Popout_Input,new MessageData("Retype Password:")));
			}
			boolean savetoconf = (Boolean)p.sendMessage(new Message(MsgType.Request,this,p.getPlugin("UI_Default"),MsgContent.UI_Popout_YesNo,new MessageData("Save password hash to init config?")));
			
			
			//Key generation
			String hashedPassword = hashPassword(passwd);
			if(savetoconf) {
				initsettings.setProperty("encryptionkey", hashedPassword);
			}
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256"); //got from https://www.baeldung.com/java-aes-encryption-decryption
		    KeySpec spec = new PBEKeySpec(hashedPassword.toCharArray(), hashedPassword.getBytes(), 65536, 256);
		    SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES"); //TODO include encryption algorithm change
		    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		    
		    
		    //Cypher file geneartion
		    try{
		    	AlgorithmParameters params = cipher.getParameters();
				byte[] fileIv = params.getParameterSpec(IvParameterSpec.class).getIV();
				cipher.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(fileIv));
				CipherOutputStream s = new CipherOutputStream(new FileOutputStream(configfilelocation), cipher);
				s.write(fileIv);
				s.close();
			}
			catch(Exception e) {
				System.out.println(e);
				e.printStackTrace();//TODO convert to throw exception if working
			}
		}
	}
	
	private InputStream decryptEncryptedFile(String path, String hashedPassword) throws Exception{
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256"); //got from https://www.baeldung.com/java-aes-encryption-decryption
	    KeySpec spec = new PBEKeySpec(hashedPassword.toCharArray(), hashedPassword.getBytes(), 65536, 256);
	    SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES"); //TODO include encryption algorithm change
	    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		try{
			FileInputStream fileIn = new FileInputStream(configfilelocation);
			byte[] fileIv = new byte[16];
			fileIn.read(fileIv);
			cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(fileIv));
			CipherInputStream cipherIn = new CipherInputStream(fileIn, cipher);
			return cipherIn;
		}
		catch(Exception e) {
			System.out.println(e); //TODO convert to throw exception if working
		}
		return null;
	}
	
	private OutputStream encryptEncryptedFile(String path, String hashedPassword) throws Exception{
		FileInputStream fis = new FileInputStream(configfilelocation);
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256"); //got from https://www.baeldung.com/java-aes-encryption-decryption
	    KeySpec spec = new PBEKeySpec(hashedPassword.toCharArray(), hashedPassword.getBytes(), 65536, 256);
	    SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES"); //TODO include encryption algorithm change
	    
		byte[] fileIv = new byte[16];
        fis.read(fileIv);
        cipher.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(fileIv));
        CipherOutputStream  cipherOut = new CipherOutputStream(new FileOutputStream(configfilelocation), cipher);
        fis.close();
        return cipherOut;
	}
	
	private String hashPassword(String password) throws Exception {
		String result = "";
		byte[] encodedhash = null;
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
		StringBuilder hexString = new StringBuilder(2 * encodedhash.length); //copied from https://www.baeldung.com/sha-256-hashing-java
	    for (int i = 0; i < encodedhash.length; i++) {
	        String hex = Integer.toHexString(0xff & encodedhash[i]);
	        if(hex.length() == 1) {
	            hexString.append('0'); 
	        }
	        hexString.append(hex);
	    }
	    result = hexString.toString();
	    return result;
	}
	
}
