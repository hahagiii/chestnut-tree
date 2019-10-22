package io.chestnut.core.protocol;

import io.chestnut.core.Chestnut;
import io.chestnut.core.Message;
import io.netty.buffer.ByteBuf;

public abstract  class ProtocolIn implements Message{
	public Chestnut destChestnut;
	
	public abstract void unpackBody(ByteBuf in) throws Exception;

	@Override
	public Chestnut messageDest() {
		return destChestnut;
	}


	@Override
	public void setMessageDest(Chestnut chestnut) {
		this.destChestnut = chestnut;
	}

}
