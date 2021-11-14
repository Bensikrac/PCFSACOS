package container;

public class Message {
	private MsgType type;
	private Plugin from;
	private Plugin to;
	private MessageData messagedata;
	private MsgContent content;
	
	public Message(MsgType type, Plugin from, Plugin to,MsgContent content, MessageData messagedata) {
		this.type = type;
		this.from = from;
		this.to = to;
		this.messagedata = messagedata;
		this.content = content;
	}
	
	public MsgType getType() {
		return type;
	}
	
	public Plugin getFrom() {
		return from;
	}
	
	public Plugin getTo() {
		return to;
	}
	
	public MessageData getDataObject() {
		return messagedata;
	}
	
	public MsgContent getContent() {
		return content;
	}
	
}
