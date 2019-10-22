package io.chestnut.core.network;

import io.chestnut.core.SocketConnection;
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

	public void connect(String ip,int port,SocketConnection socketConnection) throws InterruptedException, InstantiationException, IllegalAccessException {
		clientBootstrap = new Bootstrap()
				.group(clientEventLoopGroup)
				.channel(NioSocketChannel.class)
				.handler(NetworkCommon.newChannelInitializer(socketConnection));
		
		clientBootstrap.connect(ip, port).sync();
	}
	
	
}
