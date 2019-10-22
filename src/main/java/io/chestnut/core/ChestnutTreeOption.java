package io.chestnut.core;

import io.netty.util.NettyRuntime;

public class ChestnutTreeOption {
	public String serviceMrgAddress;
	public int servicePort;
	public String serviceName;
	public int serviceNettyThreadNum;
	
	public Class<? extends SocketConnection> serverConnectionClass;
	public int serverPort = 0;
	public int serverNettyThreadNum = 0;
	
	public int httpPort = 0;
	public int httpNettyThreadNum = 0;
	public String httpHandlePath;
	
	public int clientNettyThreadNum = 0;
	
	public void serverOpt(int serverNettyThreadNum, Class<? extends SocketConnection> socketConnectionClass, int port) {
		this.serverConnectionClass = socketConnectionClass;
		this.serverNettyThreadNum = serverNettyThreadNum;
		this.serverPort = port;
	}
	
	public void serverOpt(Class<? extends SocketConnection> connectionClass, int port) {
		serverOpt(NettyRuntime.availableProcessors(), connectionClass, port);
	}

	public void serviceMrgAddress(String ip) {
		this.serviceMrgAddress = ip;
	}
	
	public void httpdOpt(int port, String httpHandlePath) {
		httpdOpt(NettyRuntime.availableProcessors(), port, httpHandlePath);
	}
	public void httpdOpt(int httpNettyThreadNum,int port, String httpHandlePath) {
		this.httpNettyThreadNum = httpNettyThreadNum;
		this.httpHandlePath = httpHandlePath;
		this.httpPort = port;
	}
	
	public ChestnutTreeOption clientOpt(int clientNettyThreadNum) {
		this.clientNettyThreadNum = clientNettyThreadNum;
		return this;
	}
	
	public ChestnutTreeOption serviceOpt(String serviceName, int servicePort,String serviceMrgAddress) {
		return serviceOpt(NettyRuntime.availableProcessors(), serviceName, servicePort, serviceMrgAddress);
	}
	public ChestnutTreeOption serviceOpt(int nettyThreadNum, String serviceName, int servicePort,String serviceMrgAddress) {
		this.serviceName = serviceName;
		this.servicePort = servicePort;
		this.serviceMrgAddress = serviceMrgAddress;
		this.serviceNettyThreadNum = nettyThreadNum;
		return this;
	}


}
