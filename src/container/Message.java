package container;

public class Message {
	private MsgType type;
	private String from;
	private String to;
	private MessageData messagedata;
	
	public Message(MsgType type, String from, String to,MessageData messagedata) {
		this.type = type;
		this.from = from;
		this.to = to;
		this.messagedata = messagedata;
	}
	
	public MsgType getType() {
		return type;
	}
	
	public String getFrom() {
		return from;
	}
	
	public String getTo() {
		return to;
	}
	
	public MessageData getData() {
		return messagedata;
	}
	
}
