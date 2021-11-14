package container;

public interface Plugin {
	public void load() throws Exception;
	public void run();
	public void unload();
	public PluginType getType();
	public String getName();
	public boolean processMessage(Message m) throws Exception;
}
