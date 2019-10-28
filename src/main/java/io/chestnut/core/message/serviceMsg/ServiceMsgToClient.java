package io.chestnut.core.message.serviceMsg;

import io.chestnut.core.InternalMessage;
import io.netty.buffer.ByteBuf;

public class ServiceMsgToClient extends InternalMessage{
	
	private byte[] dest;

	public ServiceMsgToClient() {
	}

	@Override
	public short id() {
		return ServiceMsgDefine.ServiceMsgToClient;
	}

	 public  void unpackBody(ByteBuf in) {
	    	int len = in.readInt();
	    	if(len > 0) {
	    		dest = new byte[len];
	    		in.readBytes(dest);
	    	}
		};
		
	    public  void packBody(ByteBuf out) {
	    	if(dest == null) {
	    		out.writeInt(0);
	    	}else {
	    		int len = dest.length;
	    		out.writeInt(len);
	    		out.writeBytes(dest);
	    	}

	    }
	    
	public byte[] getDest() {
		return dest;
	}

	public void setDest(byte[] dest) {
		this.dest = dest;
	}
	
   

}
