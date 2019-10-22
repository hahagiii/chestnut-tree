package io.chestnut.core;

import io.chestnut.core.protocol.SimpleProtocolUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public abstract class InternalMessage implements  Message{
	private short messageId = 0;
	private Channel channel = null;
	private Chestnut destChestnut = null;
	private String destChestnutId = null;
	private String messageSerialId = null;

	
	@Override
	public Chestnut messageDest() {
		return destChestnut;
	}

	@Override
	public void setMessageDest(Chestnut chestnut) {
		this.destChestnut = chestnut;
		this.destChestnutId = chestnut.getId();
	}
	

	public void packMessage(ByteBuf out) {
		out.writerIndex(2);
		SimpleProtocolUtil.putString(out, messageSerialId);
		SimpleProtocolUtil.putString(out, destChestnutId);
		out.writeShort(id());
		packBody(out);
		int messageSize = out.writerIndex()-2;
		out.setShort(0,messageSize);
	}
	
	public  void unpackBody(ByteBuf in) {
		
	};
	
    public  void packBody(ByteBuf out) {
    	
    };
    
    
    public  void reply(InternalMessage internalMessage) {
    	internalMessage.messageSerialId = messageSerialId;
    	channel.writeAndFlush(internalMessage);
    }

    
	public short getMessageId() {
		return messageId;
	}

	public void setMessageId(short messageId) {
		this.messageId = messageId;
	};
    
	@Override
	public short id() {
		return messageId;
	}

	public String getMessageSerialId() {
		return messageSerialId;
	}

	public void setMessageSerialId(String messageSerialId) {
		this.messageSerialId = messageSerialId;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public String getDestChestnutId() {
		return destChestnutId;
	}

	public void setDestChestnutId(String destChestnutId) {
		this.destChestnutId = destChestnutId;
	}
	
}