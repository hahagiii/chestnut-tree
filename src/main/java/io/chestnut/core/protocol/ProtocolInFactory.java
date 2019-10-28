package io.chestnut.core.protocol;

import io.netty.buffer.ByteBuf;

public interface ProtocolInFactory {
	ProtocolIn getProtocolIn(ByteBuf byteBuf);
	ProtocolIn getProtocolIn(short protocolId);
	void addPath(String path);
}
