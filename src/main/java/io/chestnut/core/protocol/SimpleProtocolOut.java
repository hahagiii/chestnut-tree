package io.chestnut.core.protocol;

import io.chestnut.core.Chestnut;
import io.netty.buffer.ByteBuf;

public abstract  class SimpleProtocolOut extends ProtocolOut{

	@Override
	public void packTail(ByteBuf out) throws Exception {
		int messageSize = out.writerIndex()-2;
		out.setShort(0,messageSize);
		
	}

	@Override
	public void packHead(ByteBuf out) throws Exception {
		out.writerIndex(2);
		out.writeShort(id());
	}
	
	@Override
	public Chestnut messageDest() {
		return null;
	}

	@Override
	public void setMessageDest(Chestnut chestnut) {
		
	}

}
