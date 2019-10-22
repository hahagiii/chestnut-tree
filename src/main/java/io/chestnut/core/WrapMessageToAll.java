package io.chestnut.core;


public class WrapMessageToAll implements Message{
	public Message message;
	
	public  WrapMessageToAll(Message message) {
		this.message = message;
	}

	@Override
	public short id() {
		return 0;
	}

	@Override
	public Chestnut messageDest() {
		return null;
	}

	@Override
	public void setMessageDest(Chestnut chestnut) {
	}
}
