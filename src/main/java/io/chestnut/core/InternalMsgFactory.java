package io.chestnut.core;

public class InternalMsgFactory {
	public MessageFactory<InternalMessage> messageFactory = new MessageFactory<>();
	
	
	@SuppressWarnings("unchecked")
	public  <T extends InternalMessage>  T getMessage(short id)  {
		InternalMessage internalMessage = messageFactory.get(id);
		if(internalMessage == null) {
			return null;
		}
		internalMessage.setMessageId(id);
		return (T) internalMessage;
	}
}
