package container;

public class MessageData {
	private Object data;
	
	public MessageData(String msg) {
		this.data = msg;
		//TODO add more types of data if needed
	}
	
	public Object getData() {
		return data;
	}

}
