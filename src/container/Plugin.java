package container;

public interface Plugin {
	public void load();
	public void run();
	public void unload();
	public PluginType getType();
}
