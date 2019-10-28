package io.chestnut.core.message.serviceMsg;

import io.chestnut.core.InternalMessage;
import io.netty.buffer.ByteBuf;

public class ServiceMsgToGateWay extends InternalMessage{
	
	public InternalMessage internalMessage;

	public ServiceMsgToGateWay() {
	}

	@Override
	public short id() {
		return ServiceMsgDefine.ServiceMsgToGateWay;
	}
	
    public  void unpackBody(ByteBuf in) {
    	short msgId = in.readShort();
    	switch (msgId) {
		case ServiceMsgDefine.ServiceMsgToClient:
			ServiceMsgToClient serviceMsgToClient = new ServiceMsgToClient();
			serviceMsgToClient.unpackBody(in);
			this.internalMessage = serviceMsgToClient;
			break;
		case ServiceMsgDefine.ServiceMsgToGateWay:
			ServiceMsgToGateWay serviceMsgToGateWay = new ServiceMsgToGateWay();
			serviceMsgToGateWay.unpackBody(in);
			this.internalMessage = serviceMsgToGateWay;
		default:
			break;
		}
		
	};
	
    public  void packBody(ByteBuf out) {
    	out.writeShort(internalMessage.id());
    	internalMessage.packBody(out);
    }

}
