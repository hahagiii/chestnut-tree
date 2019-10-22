package io.chestnut.core.network;

import java.util.Map;

import io.chestnut.core.network.httpd.HttpAuthority;
import io.chestnut.core.network.httpd.HttpHandle;
import io.chestnut.core.network.httpd.RequestParser;
import io.chestnut.core.util.ClassScanner;
import io.chestnut.core.util.DebugUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

public class ChestnutHttpd {
	public ServerBootstrap serverBootstrap;
	public HttpAuthority httpAuthority = new HttpAuthority();
	public Map<String, HttpHandle> httpHandleMap;
	public EventLoopGroup nettyThreadGroup;
	
	public ChestnutHttpd(int protocolServiceThreadNum) {
		nettyThreadGroup = new NioEventLoopGroup(protocolServiceThreadNum);
		serverBootstrap = new ServerBootstrap();
	}
	
	//"chestnut.wood.manageWeb.servlet"
	public void listen(int port, String path) throws InterruptedException {
		httpHandleMap = ClassScanner.httpServletInit(path);
		
		serverBootstrap.
		group(nettyThreadGroup).
		channel(NioServerSocketChannel.class).
		option(ChannelOption.SO_BACKLOG, 100).
		childHandler(new ChannelInitializer<SocketChannel>() {
			
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				ch.pipeline().addLast(new HttpServerCodec());
				ch.pipeline().addLast(new HttpObjectAggregator(65536));
				pipeline.addLast("deflater", new HttpContentCompressor());
				pipeline.addLast("handler", new SimpleChannelInboundHandler<FullHttpRequest>() {

					@Override
					protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
						if (!request.method().equals(HttpMethod.GET)&&!request.method().equals(HttpMethod.POST)) {
							HttpHandle.response(ctx, "");
							ctx.close();
							return;
						}
						String servletName = RequestParser.getServletName(request.uri());
						HttpHandle httpHandle = httpHandleMap.get(servletName);
						try {
							if(servletName.equals("chestnutEncryption")) {
								httpAuthority.encryptionHandle(httpHandleMap, ctx, request);
							}else if(httpHandle !=null && httpHandle.isPublicAuthority && request.method() == HttpMethod.GET) {
								httpHandle.doGet(ctx, RequestParser.parse(request));
							}else if(httpHandle !=null && httpHandle.isPublicAuthority && request.method() == HttpMethod.POST) {
								httpHandle.doPost(ctx, RequestParser.parse(request));
							}else {
								HttpHandle.response(ctx, "no permission: " + servletName);
								ctx.close();
							}
						} catch (Exception e) {
							e.printStackTrace();
							HttpHandle.response(ctx, "servletName " + servletName + " ereror: " + DebugUtil.printStack(e));
							ctx.close();
						}
						
					}
				});

			}
		});
		serverBootstrap.bind(port).sync();	
	}
	
}
