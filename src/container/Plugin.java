package container;

public interface Plugin {
	public void load() throws Exception; //maybe change from void to some other, throw exception or return error?
	public void run();
	public void unload();
	public PluginType getType(); //This is fixed, should probably not be changed.
	public String getName(); //This is also fixed, should probably not be changed.
	public Object processMessage(Message m) throws Exception; //variable return type true / false or some other object on invoke from init
}
