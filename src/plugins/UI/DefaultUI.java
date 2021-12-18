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
	public Object processMessage(Message m) throws Exception { //return object is used for Result returning, if invoked from init method.
		//switch if request or data Should maybe split in submethods of object for readability
		MsgType t = m.getType();
		switch(t) {
		case Data:
			return processMessageData(m);
		case Request:
			return processMessageRequest(m);
		default:
			throw new Exception(ExType.MsgType_Unknown.toString());
		}
	}
	
	private Object processMessageData(Message m) throws Exception{
		//TODO create function
		return null;
	}
	
	private Object processMessageRequest(Message m) throws Exception { //maybe split further
		MsgContent c = m.getContent();
		switch(c) {
		case UI_Popout_Error:
		case UI_Popout_Input:
		case UI_Popout_Response:
		case UI_Popout_YesNo:
			return processPopout(m);
		default:
			return null;
		}
	}
	
	private Object processPopout(Message m) throws Exception{
		MsgContent c = m.getContent();
		switch(c) {
			case UI_Popout_Error:
				JOptionPane.showMessageDialog(null, m.getDataObject().getData(),"Error", JOptionPane.ERROR_MESSAGE);
			break;
			case UI_Popout_Input:
				String input =JOptionPane.showInputDialog(null,m.getDataObject().getData());
				return input; //return popout data
			case UI_Popout_YesNo:
				int n = JOptionPane.showConfirmDialog(null, (String)m.getDataObject().getData(), "Confirm?",JOptionPane.YES_NO_OPTION);
				boolean mdata = false;
				if(n == JOptionPane.YES_OPTION) {
					mdata = true;
				}
				return mdata;
			default:
			throw new Exception(ExType.MsgContent_Unknown.toString());
		}
		return null;
	}
	
}
