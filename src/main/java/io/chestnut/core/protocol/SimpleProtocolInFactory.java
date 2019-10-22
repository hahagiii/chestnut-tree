package io.chestnut.core.protocol;

import io.chestnut.core.MessageFactory;
import io.netty.buffer.ByteBuf;

public class SimpleProtocolInFactory extends MessageFactory<ProtocolIn> implements ProtocolInFactory{

	public SimpleProtocolInFactory(short[] messageIdList, Class<? extends ProtocolIn>[] classList) {
		super(messageIdList, classList);
	}

	public SimpleProtocolInFactory(String path) {
		super(path);
	}
	
	@Override
	public ProtocolIn getProtocolIn(ByteBuf in) {
		try {
			return SimpleProtocolUtil.decode(in, this);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
