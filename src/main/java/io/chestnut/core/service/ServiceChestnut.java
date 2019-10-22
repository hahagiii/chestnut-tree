package io.chestnut.core.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import io.chestnut.core.Chestnut;
import io.chestnut.core.Handler;
import io.chestnut.core.InternalMessage;
import io.chestnut.core.InternalMsgFactory;
import io.chestnut.core.SocketConnection;
import io.chestnut.core.network.ChestnutClient;
import io.chestnut.core.protocol.ProtocolOut;
import io.chestnut.core.protocol.SimpleProtocolUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;




class Callback{
	public Chestnut caller;
	public Handler handler;
	
	public Callback(Chestnut caller, Handler handler) {
		this.caller = caller;
		this.handler = handler;
	}
	
}

public class ServiceChestnut extends Chestnut{

	
	class SocketConnectionImp implements SocketConnection{
		public Channel channel;

		@Override
		public void sendProtocol(ProtocolOut protocolOut) {
			
		}

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
				 InternalMessage internalMessage = InternalMsgFactory.getMessage(messageId);
				 internalMessage.unpackBody(in);
				 internalMessage.setMessageSerialId(messageSerialId);
				 internalMessage.setChannel(channel);
				 Callback callback = callbackMap.get(internalMessage.getMessageSerialId());
				 if(callback != null) {
					 callback.caller.chestnutEventLoopThread().execute(callback.handler,internalMessage);
					 callbackMap.remove(internalMessage.getMessageSerialId());
				 }
			}
			
		}

		@Override
		public void channelActive(Channel channel) {
			this.channel = channel;
		}

		@Override
		public void channelInactive() {
			
		}

		
	}
	
	public String serviceName;
	private ChestnutClient serviceClient;
	public Map<String, Callback> callbackMap = new ConcurrentHashMap<String, Callback>();
	public SocketConnectionImp con;

	public ServiceChestnut(String serviceName) {
		this.serviceName = serviceName;
	}

	@Override
	public String getId() {
		return serviceName;
	}

	public ChestnutClient getServiceClient() {
		return serviceClient;
	}

	public void setServiceClient(ChestnutClient serviceClient) {
		this.serviceClient = serviceClient;
	}


	public void connectService(String ip, int port) throws Exception {
		con = new SocketConnectionImp();
		serviceClient.connect(ip, port, con);
	}

	public void request(InternalMessage message, Chestnut caller, Handler handler) {
		Callback Callback = new Callback(caller,handler);
		String uuid = UUID.randomUUID().toString();
		message.setMessageSerialId(uuid);
		callbackMap.put(uuid, Callback);
		con.channel.writeAndFlush(message);
	}
	
    	   
}

