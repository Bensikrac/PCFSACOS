package plugins.Transfer;
import container.*;

public class DefaultTransfer implements Plugin {
	public DefaultTransfer(PluginManager pluginManager) {
		// TODO Auto-generated constructor stub
	}

	public PluginType getType() {
		return PluginType.Transfer;
	}
	
	public void unload() {
	
	}
	public void run() {
		
	}
	public void load() {
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Transfer_Default";
	}

	@Override
	public Object processMessage(Message m) {
		// TODO Auto-generated method stub
		return false;
	}
	

}
