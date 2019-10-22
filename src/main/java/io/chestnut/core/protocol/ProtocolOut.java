package io.chestnut.core.protocol;

import io.chestnut.core.Chestnut;
import io.chestnut.core.Message;
import io.netty.buffer.ByteBuf;

public abstract  class ProtocolOut implements Message{
	
	@Override
	public Chestnut messageDest() {
		return null;
	}

	@Override
	public void setMessageDest(Chestnut chestnut) {
		
	}
	public void packMessage(ByteBuf buf) throws Exception{
		packHead(buf);
		packBody(buf);
		packTail(buf);
	};

	public abstract void packBody(ByteBuf buf) throws Exception;
	public abstract void packTail(ByteBuf buf) throws Exception;
	public abstract void packHead(ByteBuf buf) throws Exception;
}
