package io.chestnut.core.message.serviceMsg;

import io.chestnut.core.InternalMessage;
import io.chestnut.core.protocol.SimpleProtocolUtil;
import io.netty.buffer.ByteBuf;

public class ServiceMsgRegister extends InternalMessage{
	private String serviceName;
	private int port;
	
	public ServiceMsgRegister() {
	}

	@Override
	public short id() {
		return ServiceMsgDefine.ServiceMsgRegister;
	}
	
    public  void unpackBody(ByteBuf in) {
    	this.serviceName = SimpleProtocolUtil.getString(in);
    	this.port = in.readInt();
	};
	
    public  void packBody(ByteBuf out) {
    	SimpleProtocolUtil.putString(out, serviceName);
    	out.writeInt(port);
    }

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	};

}
