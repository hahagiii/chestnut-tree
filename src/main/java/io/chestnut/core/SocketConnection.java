package io.chestnut.core;

import io.chestnut.core.protocol.ProtocolOut;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public interface SocketConnection {
	public void sendProtocol(ProtocolOut protocolOut);
	void receiveData(ByteBuf in) throws Exception;
	void channelActive(Channel channel);
	void channelInactive();
}
