package io.chestnut.core.service;

import io.chestnut.core.InternalMessage;
import io.chestnut.core.network.SocketConnection;
import io.chestnut.core.protocol.SimpleProtocolUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public class ServiceClientSocketConnection implements SocketConnection{
		public Channel channel;
		public ServiceChestnut serviceChestnut;
		@SuppressWarnings("unused")
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
				 InternalMessage internalMessage = serviceChestnut.chestnutTree().getMessage(messageId);
				 internalMessage.unpackBody(in);
				 internalMessage.setMessageSerialId(messageSerialId);
				 internalMessage.setChannel(channel);
				 Callback callback = this.serviceChestnut.callbackMap.get(internalMessage.getMessageSerialId());
				 if(callback != null) {
					 callback.caller.chestnutEventLoopThread().execute(callback.handler,internalMessage);
					 this.serviceChestnut.callbackMap.remove(internalMessage.getMessageSerialId());
				 }
			}
			
		}

		@Override
		public void channelActive(Channel channel,Object[] parameter) {
			this.channel = channel;
			this.serviceChestnut = (ServiceChestnut) parameter[0];
			this.serviceChestnut.setServiceClientSocketConnection(this);
		}

		@Override
		public void channelInactive() {
			
		}

		@Override
		public Channel channel() {
			return channel;
		}

		
	
	
}
