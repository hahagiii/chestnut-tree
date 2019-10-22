package io.chestnut.core.protocol;

import io.netty.buffer.ByteBuf;

public interface ProtocolInFactory {
	public ProtocolIn getProtocolIn(ByteBuf byteBuf);
}
