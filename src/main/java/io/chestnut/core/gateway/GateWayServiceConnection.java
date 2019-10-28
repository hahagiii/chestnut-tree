package io.chestnut.core.gateway;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.chestnut.core.message.serviceMsg.ServiceMsgDefine;
import io.chestnut.core.message.serviceMsg.ServiceMsgRegister;
import io.chestnut.core.message.serviceMsg.ServiceMsgToClient;
import io.chestnut.core.message.serviceMsg.ServiceMsgToGateWay;
import io.chestnut.core.network.SocketConnection;
import io.chestnut.core.protocol.SimpleProtocolUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public class GateWayServiceConnection implements SocketConnection{
	public static final Logger logger = LoggerFactory.getLogger(GateWayServiceConnection.class);

	public Channel channel;

	public GateWayRecConnection gateWayRecConnection;
	public String serviceName;
	
	@SuppressWarnings("unused")
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
			String messageSerialId = SimpleProtocolUtil.getString(in);
			String destChestnutId = SimpleProtocolUtil.getString(in);
			short id = in.readShort();
			switch (id) {
			case ServiceMsgDefine.ServiceMsgRegister:
				ServiceMsgRegister serviceMsgRegister = new ServiceMsgRegister();
				serviceMsgRegister.unpackBody(in);
				serviceName = serviceMsgRegister.getServiceName();
				Service service = ServiceMrg.getService(serviceName);
				if(service == null) {
					service = new Service(serviceName);
					ServiceMrg.addService(service);
				}
				InetSocketAddress insocket = (InetSocketAddress) channel.remoteAddress();
				String ip = insocket.getAddress().getHostAddress();
				int port = serviceMsgRegister.getPort();
				service.addServiceNode(new ServiceNode(ip,port,channel));
				logger.info("serviceName " + serviceName + " ip:" + ip + " port" + port);
				break;
			case ServiceMsgDefine.ServiceMsgToClient:
				ServiceMsgToClient serviceMsgToClient = new ServiceMsgToClient();
				serviceMsgToClient.unpackBody(in);
				gateWayRecConnection.toClient(destChestnutId, serviceMsgToClient.getDest());
				break;
			case ServiceMsgDefine.ServiceMsgToGateWay:
				ServiceMsgToGateWay serviceMsgToGateWay = new ServiceMsgToGateWay();
				serviceMsgToGateWay.unpackBody(in);
				gateWayRecConnection.toGateWay(serviceName, serviceMsgToGateWay.internalMessage);
				break;
			default:
				break;
			}
		
		}
	}

	@Override
	public void channelInactive() {
		
	}

	@Override
	public Channel channel() {
		return channel;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void channelActive(Channel channel, Object[] parameter) {
		this.channel = channel;
		Class<? extends GateWayRecConnection> gateWayRecConnectionClass = (Class<? extends GateWayRecConnection>) parameter[0];
		 try {
			GateWayRecConnection gateWayRecConnection = gateWayRecConnectionClass.newInstance();
			this.gateWayRecConnection = gateWayRecConnection;
		} catch (Exception e) {
			e.printStackTrace();
			channel.close();
		}
	}

}
