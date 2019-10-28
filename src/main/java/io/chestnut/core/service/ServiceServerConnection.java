package io.chestnut.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.chestnut.core.Chestnut;
import io.chestnut.core.ChestnutTree;
import io.chestnut.core.InternalMessage;
import io.chestnut.core.network.SocketConnection;
import io.chestnut.core.protocol.SimpleProtocolUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public class ServiceServerConnection implements SocketConnection{
	public static final Logger logger = LoggerFactory.getLogger(ServiceServerConnection.class);

	public ChestnutTree chestnutTree;
	public Channel channel;
	

	@Override
	public void receiveData(ByteBuf in) throws Exception {
		while(true) {
			final int readableBytes = in.readableBytes();
			if(readableBytes < 2)
				return;

			 short length = in.readShort();
			 if(length < 2){
				 throw new Exception("length小于2");	
			 }
			 if(in.readableBytes() < length){
				 in.readerIndex(in.readerIndex() - 2);
				 return;
			 }
			 String messageSerialId = SimpleProtocolUtil.getString(in);
			 String chestnutId = SimpleProtocolUtil.getString(in);
			 short messageId = in.readShort();
			 InternalMessage internalMessage = chestnutTree.getMessage(messageId);
			 internalMessage.unpackBody(in);
			 internalMessage.setChannel(channel);
			 internalMessage.setMessageSerialId(messageSerialId);
			 Chestnut chestnut = chestnutTree.getChestnut(chestnutId);
			 chestnut.cast(internalMessage);
		}
		
	}

	@Override
	public void channelActive(Channel channel,Object[] parameter) {
		this.chestnutTree = (ChestnutTree) parameter[0];
		this.channel = channel;
		logger.info(chestnutTree.chestnutTreeOption().serviceName() + " 收到一个新连接 " + channel);
		
	}

	@Override
	public void channelInactive() {
		logger.info(chestnutTree.chestnutTreeOption().serviceName() + " 断开一个连接 " + channel);

	}


	@Override
	public Channel channel() {
		return channel;
	}

}
