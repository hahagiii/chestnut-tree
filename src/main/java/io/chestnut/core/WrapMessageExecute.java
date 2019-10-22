package io.chestnut.core;


public class WrapMessageExecute implements Message{
	public Handler handler;
	public InternalMessage internalMessage;
	
	public  WrapMessageExecute(Handler handler, InternalMessage internalMessage) {
		this.internalMessage = internalMessage;
		this. handler = handler;
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
