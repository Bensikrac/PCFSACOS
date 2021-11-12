package container;

public interface Plugin {
	public void load() throws Exception;
	public void run();
	public void unload();
	public PluginType getType();
}
