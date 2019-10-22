package io.chestnut.core.protocol;

import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.chestnut.core.MessageFactory;
import io.netty.buffer.ByteBuf;

public final class SimpleProtocolUtil {
	public static final Charset decoder = Charset.forName("UTF-8");
	private static final Logger logger = LoggerFactory.getLogger(SimpleProtocolUtil.class);

	public final static String getString(ByteBuf ioBuffer) {
		short length = ioBuffer.readShort();
		try {
			if (length > 0)
				return ioBuffer.readCharSequence(length, decoder).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public final static ByteBuf putString(ByteBuf ioBuffer, String s) {
		if (s == null || s.equals("")) {
			ioBuffer.writeShort(0);
			return ioBuffer;
		}
		byte[] b = s.getBytes(decoder);
		ioBuffer.writeShort(b.length);
		ioBuffer.writeBytes(b);
		return ioBuffer;
	}
	
	public final static ProtocolIn decode(ByteBuf in,MessageFactory<ProtocolIn> protocolFactory) throws Exception {	
		final int readableBytes = in.readableBytes();
		if(readableBytes < 2)
			return null;

		 short length = in.readShort();
		 if(length < 2){
			 throw new Exception("length小于2");	
		 }
		 if(in.readableBytes() < length){
			 in.readerIndex(in.readerIndex() - 2);
			 return null;
		 }
		 short id = in.readShort();
		 ProtocolIn messageDecode = protocolFactory.get(id);
		 if(messageDecode == null) {
			 logger.error("协议未加入工厂 " + id);
			 in.skipBytes(length-2);
			 return decode(in, protocolFactory);
		 }
		 messageDecode.unpackBody(in);
		 return messageDecode;
	}
	
	
	
	
}
