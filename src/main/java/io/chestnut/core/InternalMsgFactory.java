package io.chestnut.core;

public class InternalMsgFactory {
	public static MessageFactory<InternalMessage> messageFactory = new MessageFactory<>();
	
	public static void init(String path) {
		messageFactory.add(path);
	}
	
	@SuppressWarnings("unchecked")
	public static  <T extends InternalMessage>  T getMessage(short id) throws Exception {
		InternalMessage internalMessage = messageFactory.get(id);
		if(internalMessage == null) {
			throw new Exception(id + " 未能初始化");
		}
		internalMessage.setMessageId(id);
		return (T) internalMessage;
	}
}
