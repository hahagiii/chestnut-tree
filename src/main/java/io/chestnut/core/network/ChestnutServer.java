package io.chestnut.core.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ChestnutServer {
	public ServerBootstrap serverBootstrap;
	
	public EventLoopGroup nettyAcceptThreadGroup;
	public EventLoopGroup nettyWorkerThreadGroup;
	

	
	public ChestnutServer(int protocolServiceThreadNum) {
		nettyAcceptThreadGroup = new NioEventLoopGroup(1);
		nettyWorkerThreadGroup = new NioEventLoopGroup(protocolServiceThreadNum);
	}
	
	public void listen(int port,Class<? extends SocketConnection> protocolCodecClass) throws Exception {
		Object [] parameter = null;
		listen(port, protocolCodecClass, parameter);
	}
	public void listen(int port,Class<? extends SocketConnection> protocolCodecClass,Object ...parameter) throws Exception {
		serverBootstrap = new ServerBootstrap()
				.group(nettyAcceptThreadGroup, nettyWorkerThreadGroup)
				.channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_BACKLOG, 100)
				.childHandler(NetworkCommon.newChannelInitializer(protocolCodecClass,parameter));
		serverBootstrap.bind(port).sync();
	}
	
}
