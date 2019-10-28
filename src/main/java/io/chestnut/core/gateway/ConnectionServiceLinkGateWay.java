package io.chestnut.core.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.chestnut.core.Chestnut;
import io.chestnut.core.ChestnutTree;
import io.chestnut.core.InternalMessage;
import io.chestnut.core.message.serviceMsg.ServiceMsgRegister;
import io.chestnut.core.message.serviceMsg.ServiceMsgToClient;
import io.chestnut.core.network.SocketConnection;
import io.chestnut.core.protocol.ProtocolIn;
import io.chestnut.core.protocol.ProtocolOut;
import io.chestnut.core.protocol.SimpleProtocolUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public class ConnectionServiceLinkGateWay implements SocketConnection{
	public final static byte isInternalMessage = 1;
	public final static byte isProtocol = 0;
	public static final Logger logger = LoggerFactory.getLogger(ConnectionServiceLinkGateWay.class);

	Channel channel=null;
	ChestnutTree chestnutTree;
	String serviceName;
	
	@Override
	public void receiveData(ByteBuf in) throws Exception {
		while(true) {
			final int readableBytes = in.readableBytes();
			if(readableBytes < 2)
				return;
	
			 short length = in.readShort();
			 if(length < 2){
				 throw new Exception("length 小于2");	
			 }
			 if(in.readableBytes() < length){
				 in.readerIndex(in.readerIndex() - 2);
				 return;
			}
			String chestnutId = SimpleProtocolUtil.getString(in);
			Chestnut chestnut = chestnutTree.getChestnut(chestnutId);
			if(chestnut == null) {
				int nowReadableBytes = in.readableBytes();
				int readBytes = readableBytes - nowReadableBytes;
				int skipBytes = length - readBytes + 2;
				in.skipBytes(skipBytes);
				logger.info(chestnutId + " chestnutId is null");
				continue;
			}
			byte type = in.readByte();
			short id = in.readShort();
			if(type == isProtocol) {
				ProtocolIn protocolIn = chestnutTree.getProtocolIn(id);
				protocolIn.unpackBody(in);
				chestnut.cast(protocolIn);
			}else {
				InternalMessage internalMessage = chestnutTree.getMessage(id);
				internalMessage.unpackBody(in);
				chestnut.cast(internalMessage);
			}
			
		}
	}

	@Override
	public Channel channel() {
		return channel;
	}

	@Override
	public void channelInactive() {
		
	}

	@Override
	public void channelActive(Channel channel, Object[] parameter) {
		this.channel = channel;
		this.serviceName =  (String) parameter[0];
		this.chestnutTree = (ChestnutTree) parameter[1];
		ServiceMsgRegister serviceMsgRegister = new ServiceMsgRegister();
		serviceMsgRegister.setServiceName(serviceName);
		serviceMsgRegister.setPort(this.chestnutTree.chestnutTreeOption().servicePort());
		this.channel.writeAndFlush(serviceMsgRegister);
	}

	@Override
	public void sendProtocol(Object protocol) {
		if(protocol instanceof ProtocolOut) {
			ProtocolOut protocolOut = (ProtocolOut) protocol;
			ServiceMsgToClient serviceMsgToClient = new ServiceMsgToClient();
			serviceMsgToClient.setDestChestnutId(protocolOut.messageDest().getId());
			ByteBuf buf = channel.alloc().heapBuffer();
			try {
				protocolOut.packMessage(buf);
				byte [] dst = new byte[buf.readableBytes()];
				buf.readBytes(dst);
				serviceMsgToClient.setDest(dst);
				channel.writeAndFlush(serviceMsgToClient);
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				buf.release();
			}
		}else {
			channel.writeAndFlush(protocol);
		}
	}
}
