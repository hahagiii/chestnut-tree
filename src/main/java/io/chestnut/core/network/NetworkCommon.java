package io.chestnut.core.network;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.chestnut.core.InternalMessage;
import io.chestnut.core.protocol.ProtocolOut;
import io.chestnut.core.util.DebugUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.EncoderException;

public class NetworkCommon {
	private static final Logger logger = LoggerFactory.getLogger(NetworkCommon.class);
	final static public ChannelInitializer<?> newClientChannelInitializer(final SocketConnection protocolCodec,Object []parameter) {
		return new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				ByteToMessageDecoder byteToMessageDecoder = new ByteToMessageDecoder() {
								
					@Override
				    public void  exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
				    	if(cause.getMessage().equals("Connection reset by peer")||cause.getMessage().equals("远程主机强迫关闭了一个现有的连接。")||cause.getMessage().equals("你的主机中的软件中止了一个已建立的连接。")) {
				    		//logger.error("playerId: " + playerId + " netty exceptionCaught : "+ cause.getMessage());	
				    	}else {
					        logger.error("exceptionCaught " +  DebugUtil.printStack(cause)); 
				        	ctx.close();
				    	}
				    }
					
					@Override
					public void channelActive(ChannelHandlerContext ctx) throws Exception {
						protocolCodec.channelActive(ctx.channel(),parameter);
						ctx.fireChannelActive();
					}
					
					@Override
					public void channelInactive(ChannelHandlerContext ctx) throws Exception {
						protocolCodec.channelInactive();
						ctx.fireChannelInactive();
					}
					
					@Override
					protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
						 try {
							protocolCodec.receiveData(in);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
				byteToMessageDecoder.setCumulator(ByteToMessageDecoder.COMPOSITE_CUMULATOR);
				ch.pipeline().addLast(byteToMessageDecoder);
								
				ch.pipeline().addLast(new ChannelOutboundHandlerAdapter() {
					@Override
					public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
						ByteBuf byteBuf = protocolCodec.protocolToByteBuf(msg);
						if(byteBuf != null) {
							ctx.writeAndFlush(byteBuf, promise);
						}
					}
					
				});
				
			}
		};
	}
	final static public ChannelInitializer<?> newChannelInitializer(final Class<? extends SocketConnection> protocolCodecClass,Object []parameter) {
		return new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				SocketConnection protocolCodec = protocolCodecClass.newInstance();
				ByteToMessageDecoder byteToMessageDecoder = new ByteToMessageDecoder() {
								
					@Override
				    public void  exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
				    	if(cause.getMessage().equals("Connection reset by peer")||cause.getMessage().equals("远程主机强迫关闭了一个现有的连接。")||cause.getMessage().equals("你的主机中的软件中止了一个已建立的连接。")) {
				    		//logger.error("playerId: " + playerId + " netty exceptionCaught : "+ cause.getMessage());	
				    	}else {
					        logger.error("exceptionCaught " +  DebugUtil.printStack(cause)); 
				        	ctx.close();
				    	}
				    }
					
					@Override
					public void channelActive(ChannelHandlerContext ctx) throws Exception {
						protocolCodec.channelActive(ctx.channel(),parameter);
						ctx.fireChannelActive();
					}
					
					@Override
					public void channelInactive(ChannelHandlerContext ctx) throws Exception {
						protocolCodec.channelInactive();
						ctx.fireChannelInactive();
					}
					
					@Override
					protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
						 try {
							protocolCodec.receiveData(in);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
				byteToMessageDecoder.setCumulator(ByteToMessageDecoder.COMPOSITE_CUMULATOR);
				ch.pipeline().addLast(byteToMessageDecoder);
								
				ch.pipeline().addLast(new ChannelOutboundHandlerAdapter() {
					@Override
					public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
						ByteBuf byteBuf = protocolCodec.protocolToByteBuf(msg);
						if(byteBuf != null) {
							ctx.writeAndFlush(byteBuf, promise);
						}
					}
					
				});
				
			}
		};
	
	};
	
	final static public void writePackMessage(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
		if(msg instanceof ByteBuf) {
			ctx.writeAndFlush(msg,promise);
			return;
		}
		ByteBuf buf = null;
		try {
			buf = ctx.alloc().ioBuffer();
			if(msg instanceof ProtocolOut) {
				ProtocolOut messageOutMsg = (ProtocolOut) msg;
				messageOutMsg.packMessage(buf);
			}else if(msg instanceof InternalMessage) {
				InternalMessage messageOutMsg = (InternalMessage) msg;
				messageOutMsg.packMessage(buf);
			}
			if (buf.isReadable()) {
				ctx.writeAndFlush(buf, promise);
			} else {
				buf.release();
				ctx.write(Unpooled.EMPTY_BUFFER, promise);
			}
			buf = null;
		} catch (EncoderException e) {
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			throw new EncoderException(e);
		} finally {
			if (buf != null) {
				buf.release();
			}
		}

		
	}
}
