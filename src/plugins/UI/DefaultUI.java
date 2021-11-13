package plugins.UI;

import container.MainWindow;
import container.Message;
import container.MessageData;
import container.MsgType;
import container.Plugin;
import container.PluginManager;
import container.PluginType;

public class DefaultUI implements Plugin {
	private PluginManager p;

	public DefaultUI(PluginManager pluginManager) {
		this.p = pluginManager;
	}

	@Override
	public void load() throws Exception {
		MainWindow.runwindow(p);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	@Override
	public void unload() {
		// TODO Auto-generated method stub

	}

	@Override
	public PluginType getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return "UI_Default";
	}

	@Override
	public boolean processMessage(Message m) {
		// TODO Auto-generated method stub
		return false;
	}

}
