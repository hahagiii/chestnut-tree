package io.chestnut.core.gateway;

import io.chestnut.core.InternalMessage;
import io.chestnut.core.protocol.SimpleProtocolUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public class ServiceNode{
	private Channel channel;
	String nodeId;

	public ServiceNode(String ipddr, int port,Channel channel) {
		this.ipddr = ipddr;
		this.port = port;
		this.nodeId = ipddr+ "_" + port;
		this.channel = channel;
	}
	public String ipddr;
	public int port;
	public int state;
	
	public String getNodeId() {
		return nodeId;
	}

	public Channel getChannel() {
		return channel;
	}
	
	public void transmitMessage(String destId,InternalMessage internalMessage) {
		ByteBuf byteBuf = channel.alloc().directBuffer();
		byteBuf.writerIndex(2);
		SimpleProtocolUtil.putString(byteBuf, destId);
		byteBuf.writeByte(ConnectionServiceLinkGateWay.isInternalMessage);
		byteBuf.writeShort(internalMessage.id());
		internalMessage.packBody(byteBuf);
		int messageSize = byteBuf.writerIndex()-2;
		byteBuf.setShort(0,messageSize);
		channel.writeAndFlush(byteBuf);
	}
	
	public void transmitProtocol(String destId,int protocolId, byte[] dest) {
		ByteBuf byteBuf = channel.alloc().directBuffer();
		byteBuf.writerIndex(2);
		SimpleProtocolUtil.putString(byteBuf, destId);
		byteBuf.writeByte(ConnectionServiceLinkGateWay.isProtocol);
		byteBuf.writeShort(protocolId);
		byteBuf.writeBytes(dest);
		int messageSize = byteBuf.writerIndex()-2;
		byteBuf.setShort(0,messageSize);
		channel.writeAndFlush(byteBuf);
	}
}
