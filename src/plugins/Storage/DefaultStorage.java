package plugins.Storage;
import java.io.File;
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
	private String configfile;
	private PluginManager p;
	private Properties settings; //for decrypted config file
	private final String initfile = "init.conf";
	private OutputStream configout;
	
	
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
		
		//now read encryption and decrypt file
		Properties initconf = new Properties();
		FileInputStream fis = new FileInputStream(initfile);
		settings = new Properties();
		initconf.load(fis);
		configfile = initconf.getProperty("configlocation"); //read settings
		String encrypted = initconf.getProperty("encrypted");
		String encryptionkey = initconf.getProperty("EncryptionKey"); //check if encrypted 
		
		exists = Files.exists(Path.of(configfile)); //check if config file is there
		if(!exists) { 
			recreateConfigFile();
		}
		
		//encrypted and unencrypted handling
		if(encrypted.equals("true")) { //create object for config file reading if encrypted
			String result = "";
			if(encryptionkey == null) {
				String returned = (String)p.sendMessage(new Message(MsgType.Request, this, p.getPlugin("UI_Default"), MsgContent.UI_Popout_Input, new MessageData("Enter Your Password")));
				result = hashPassword(returned);
			}
			else {
				result = initconf.getProperty("EncryptionKey");
			}
			settings.load(decryptEncryptedFile(configfile, result));
			configout = encryptEncryptedFile(configfile, result);
		}
		else {
			settings.load(new FileInputStream(configfile));
		}
		propToConf("test", "encryptionkey");
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
			
		try {
		      File confile = new File(initfile);
		      confile.createNewFile();
		      //whacky copy paste code do not understand really 
		      FileInputStream fis = new FileInputStream(initfile);
		      FileOutputStream fos = new FileOutputStream(initfile);
		      Properties initconf = new Properties();
		      initconf.load(fis);
		      // TODO maybe read default values from file or copy original file to this file
		      //now setting default values
		      initconf.setProperty("configlocation", "config.conf");
		      initconf.setProperty("encrypted", "false");
		      initconf.setProperty("encryptionkey", "");
		      
		      initconf.store(fos,null);
		      fos.close();
		}
		catch (Exception e) {
			throw new Exception(ExType.File_Creation_Error.toString());
		}
	}
	
	private void recreateConfigFile() throws Exception {
		boolean returned = (Boolean)p.sendMessage(new Message(MsgType.Request,this, p.getPlugin("UI_Default"),MsgContent.UI_Popout_YesNo,new MessageData("Config File missing. Crete new?")));
		if(!returned) {
			throw new Exception(ExType.File_Config_Notfound.toString());
		}
		boolean encrypt = (Boolean)p.sendMessage(new Message(MsgType.Request,this, p.getPlugin("UI_Default"),MsgContent.UI_Popout_YesNo,new MessageData("Encrypt config file?")));
		File yourfile = new File(configfile);
		yourfile.createNewFile();
		FileInputStream fis = new FileInputStream(configfile);
		InputStream is = fis;
		if(encrypt) {
			//getting password and verifying if its the same
			String passwd = (String)p.sendMessage(new Message(MsgType.Request,this, p.getPlugin("UI_Default"),MsgContent.UI_Popout_Input,new MessageData("Password for config file:")));
			String passwd2 = (String)p.sendMessage(new Message(MsgType.Request,this, p.getPlugin("UI_Default"),MsgContent.UI_Popout_Input,new MessageData("Retype Password:")));
			while(!passwd.equals(passwd2)) {
				p.sendMessage(new Message(MsgType.Request,this, p.getPlugin("UI_Default"),MsgContent.UI_Popout_Error,new MessageData("Passwords don't match. Please try again")));
				passwd = (String)p.sendMessage(new Message(MsgType.Request,this, p.getPlugin("UI_Default"),MsgContent.UI_Popout_Input,new MessageData("Password for config file:")));
				passwd2 = (String)p.sendMessage(new Message(MsgType.Request,this, p.getPlugin("UI_Default"),MsgContent.UI_Popout_Input,new MessageData("Retype Password:")));
			}
			
			
			//Key generation
			String hashedPassword = hashPassword(passwd); 
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256"); //got from https://www.baeldung.com/java-aes-encryption-decryption
		    KeySpec spec = new PBEKeySpec(hashedPassword.toCharArray(), hashedPassword.getBytes(), 65536, 256);
		    SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES"); //TODO include encryption algorithm change
		    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		    
		    
		    //Cypher file geneartion
		    try{
		    	AlgorithmParameters params = cipher.getParameters();
		    	
				byte[] fileIv = params.getParameterSpec(IvParameterSpec.class).getIV();
				cipher.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(fileIv));
				CipherInputStream cipherIn = new CipherInputStream(fis, cipher);
				is = cipherIn;
			}
			catch(Exception e) {
				System.out.println(e);
				e.printStackTrace();//TODO convert to throw exception if working
			}
		}
		settings.load(is);
		settings.setProperty("test", "configfile");
		System.out.println(settings.getProperty("test"));
	}
	
	private InputStream decryptEncryptedFile(String path, String hashedPassword) throws Exception{
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256"); //got from https://www.baeldung.com/java-aes-encryption-decryption
	    KeySpec spec = new PBEKeySpec(hashedPassword.toCharArray(), hashedPassword.getBytes(), 65536, 256);
	    SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES"); //TODO include encryption algorithm change
	    
	    //byte[] iv = new byte[16];
	    //SecureRandom secure = new SecureRandom();
	    //secure.nextBytes(iv); do you even need this or is this decryption only
	    
	    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		
		try{
			FileInputStream fileIn = new FileInputStream(configfile);
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
		FileInputStream fis = new FileInputStream(configfile);
		FileOutputStream fos = new FileOutputStream(configfile);
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256"); //got from https://www.baeldung.com/java-aes-encryption-decryption
	    KeySpec spec = new PBEKeySpec(hashedPassword.toCharArray(), hashedPassword.getBytes(), 65536, 256);
	    SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES"); //TODO include encryption algorithm change
	    
		byte[] fileIv = new byte[16];
        fis.read(fileIv);
        cipher.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(fileIv));
        CipherOutputStream  cipherIn = new CipherOutputStream(fos, cipher);
        fis.close();
        return cipherIn;
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
	
	private void propToConf(String property, String value) throws Exception {
		settings.setProperty(property, value);
		settings.store(configout, null);
		
	}
	
}
