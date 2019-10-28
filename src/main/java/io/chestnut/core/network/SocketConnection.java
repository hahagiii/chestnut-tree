package io.chestnut.core.network;

import io.chestnut.core.InternalMessage;
import io.chestnut.core.protocol.ProtocolOut;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public interface SocketConnection {
	public default ByteBuf protocolToByteBuf(Object protocolData) {
		if(protocolData instanceof ByteBuf) {
			return (ByteBuf) protocolData;
		}
		if(protocolData instanceof ProtocolOut) {
			ProtocolOut protocolOut = (ProtocolOut) protocolData;
			ByteBuf buf = channel().alloc().directBuffer();
			try {
				protocolOut.packMessage(buf);
			} catch (Exception e) {
				e.printStackTrace();
				buf.release();
				return null;
			}
			return buf;
		}
		if(protocolData instanceof InternalMessage) {
			InternalMessage messageOutMsg = (InternalMessage) protocolData;
			ByteBuf buf = channel().alloc().directBuffer();
			try {
				messageOutMsg.packMessage(buf);
			} catch (Exception e) {
				e.printStackTrace();
				buf.release();
				return null;
			}
			return buf;
		}
		return diyProtocolToByteBuf(protocolData);
	}
	public default ByteBuf diyProtocolToByteBuf(Object diyProtocol) {
		return null;
	}
	
	void receiveData(ByteBuf in) throws Exception;
	Channel channel();
	void channelInactive();
	void channelActive(Channel channel, Object[] parameter);
	
	default void sendProtocol(Object protocol) {
		channel().writeAndFlush(protocol);
	}
}
