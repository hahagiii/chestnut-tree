package io.chestnut.core.network.httpd;

import java.util.Map;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;


public abstract class HttpHandle {
	 
	protected static final Logger logger = LoggerFactory.getLogger(HttpHandle.class.getName());
	
	public static final AsciiString CONTENT_TYPE = AsciiString.cached("Content-Type");
	public static final AsciiString CONTENT_LENGTH = AsciiString.cached("Content-Length");
	public static final AsciiString CONNECTION = AsciiString.cached("Connection");
	public static final AsciiString KEEP_ALIVE = AsciiString.cached("keep-alive");
	public static final AsciiString Access_Control_Allow_Origin = AsciiString.cached("Access-Control-Allow-Origin");
	public static final AsciiString AccessControlAllowHeaders = AsciiString.cached("Access-Control-Allow-Headers");

	public boolean isPublicAuthority = true;
	
	public abstract void doGet(ChannelHandlerContext ctx, Map<String, String> parac) throws Exception;
	public abstract void doPost(ChannelHandlerContext ctx, Map<String, String> parac) throws Exception;

	public static void response(ChannelHandlerContext ctx, String response) {
		ByteBuf buf = Unpooled.copiedBuffer(response, CharsetUtil.UTF_8);
		FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,buf);
		fullHttpResponse.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");
		fullHttpResponse.headers().set(CONTENT_LENGTH, buf.readableBytes());
		fullHttpResponse.headers().set(Access_Control_Allow_Origin, "*");
		fullHttpResponse.headers().set(AccessControlAllowHeaders, "*");
		ctx.channel().writeAndFlush(fullHttpResponse);
	}

	
	public static void respons404(ChannelHandlerContext ctx) {
		ByteBuf buf = Unpooled.copiedBuffer("404 Not Found!", CharsetUtil.UTF_8);
		FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.NOT_FOUND, buf);
		fullHttpResponse.headers().set(CONTENT_TYPE, "text/plain");
		fullHttpResponse.headers().setInt(CONTENT_LENGTH, fullHttpResponse.content().readableBytes());
		ctx.channel().writeAndFlush(fullHttpResponse);
	}
	

	public static String getParameter(Map<String, String> request, String string) {
		return request.get(string);
	}
	
	

}
