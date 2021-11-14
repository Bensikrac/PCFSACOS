package plugins.UI;

import javax.swing.JOptionPane;

import container.ExType;
import container.MainWindow;
import container.Message;
import container.MessageData;
import container.MsgContent;
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
	public boolean processMessage(Message m) throws Exception {
		//switch if request or data
		MsgType t = m.getType();
		switch(t) {
		case Data:
			break;
		case Request:
			MsgContent c = m.getContent();
			switch(c) {
			case UI_Popout_Error:
				JOptionPane.showMessageDialog(null, m.getDataObject().getData(),"Error", JOptionPane.ERROR_MESSAGE);
				break;
			case UI_Popout_Input:
				String input =JOptionPane.showInputDialog(null,m.getDataObject().getData());
				p.sendMessage(new Message(MsgType.Response, this, m.getFrom(),MsgContent.UI_Popout_Response, new MessageData(input))); //return popout data
				break;
			default:
				throw new Exception(ExType.MsgContent_Unknown.toString());
			
			}
			
			
		
			
		break;
		default:
			throw new Exception(ExType.MsgType_Unknown.toString());
		}
		
		
		
		// TODO Auto-generated method stub
		return false;
	}

}
