package io.chestnut.core.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ChestnutClient {
	public EventLoopGroup clientEventLoopGroup;
	public Bootstrap clientBootstrap;

	public ChestnutClient() {
	
	}
	
	public ChestnutClient(int clientServiceThreadNum) {
		clientEventLoopGroup = new NioEventLoopGroup(clientServiceThreadNum);
	}

	public void connect(String ip,int port,SocketConnection protocolCodec) throws Exception {
		Object [] par = null;
		 connect(ip, port, protocolCodec,par);
	}
	
	public void connect(String ip,int port,SocketConnection protocolCodec,Object ...par) throws Exception {
		clientBootstrap = new Bootstrap()
				.group(clientEventLoopGroup)
				.channel(NioSocketChannel.class)
				.handler(NetworkCommon.newClientChannelInitializer(protocolCodec,par));
		
		clientBootstrap.connect(ip, port).sync();
	}
	
	
}
