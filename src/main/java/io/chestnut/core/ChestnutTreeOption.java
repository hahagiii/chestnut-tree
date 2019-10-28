package io.chestnut.core;

import java.util.ArrayList;
import java.util.List;

import io.chestnut.core.gateway.GateWayRecConnection;
import io.chestnut.core.network.SocketConnection;
import io.netty.util.NettyRuntime;

public class ChestnutTreeOption {
	private List<String> inProtocolPathList;
	private List<String> outProtocolPathList;
	private List<String> messagePathList;
	
	private int gateWayClientListenPort;
	private int gateWayServiceListenPort;
	private int gateWayHttpMrgPort;
	private int gateWayNettyThreadNum;
	private Class<? extends SocketConnection> gateWayConnectionClass;
	private Class<? extends GateWayRecConnection> gateWayRecConnection;

	private int servicePort;
	private String serviceName;
	private int serviceNettyThreadNum;
	
	private Class<? extends SocketConnection> serverConnectionClass;
	private int serverPort = 0;
	private int serverNettyThreadNum = 0;
	
	private int httpPort = 0;
	private int httpNettyThreadNum = 0;
	private String httpHandlePath;
	
	private int clientNettyThreadNum = 0;
	
	public void serverOpt(int serverNettyThreadNum, Class<? extends SocketConnection> socketConnectionClass, int port) {
		this.setServerConnectionClass(socketConnectionClass);
		this.setServerNettyThreadNum(serverNettyThreadNum);
		this.setServerPort(port);
	}
	
	public void serverOpt(Class<? extends SocketConnection> connectionClass, int port) {
		serverOpt(NettyRuntime.availableProcessors(), connectionClass, port);
	}

	public void httpdOpt(int port, String httpHandlePath) {
		httpdOpt(NettyRuntime.availableProcessors(), port, httpHandlePath);
	}
	public void httpdOpt(int httpNettyThreadNum,int port, String httpHandlePath) {
		this.setHttpNettyThreadNum(httpNettyThreadNum);
		this.setHttpHandlePath(httpHandlePath);
		this.setHttpPort(port);
	}
	
	public ChestnutTreeOption clientOpt(int clientNettyThreadNum) {
		this.setClientNettyThreadNum(clientNettyThreadNum);
		return this;
	}
	
	public ChestnutTreeOption serviceOpt(String serviceName, int servicePort) {
		return serviceOpt(NettyRuntime.availableProcessors(), serviceName, servicePort);
	}
	public ChestnutTreeOption serviceOpt(int nettyThreadNum, String serviceName, int servicePort) {
		this.setServiceName(serviceName);
		this.servicePort = servicePort;
		this.setServiceNettyThreadNum(nettyThreadNum);
		return this;
	}
	
	
	public void gateWayOpt(int gateWayClientListenPort,int gateWayServiceListenPort, int gateWayHttpMrgPort, int gateWayNettyThreadNum,Class<? extends SocketConnection> gateWayConnectionClass,Class<? extends GateWayRecConnection> gateWayRecConnection){
		this.setGateWayConnectionClass(gateWayConnectionClass);
		this.setGateWayNettyThreadNum(gateWayNettyThreadNum);
		this.setGateWayHttpMrgPort(gateWayHttpMrgPort);
		this.setGateWayClientListenPort(gateWayClientListenPort);
		this.setGateWayServiceListenPort(gateWayServiceListenPort);
		this.setGateWayRecConnection(gateWayRecConnection);
	}

	public List<String> inProtocolPathList() {
		return inProtocolPathList;
	}

	public void addInProtocolPath(String path) {
		if(this.inProtocolPathList == null) {
			this.inProtocolPathList = new ArrayList<String>();
		}
		this.inProtocolPathList.add(path);
	}

	public List<String> outProtocolPathList() {
		return outProtocolPathList;
	}

	public void addOutProtocolPath(String path) {
		if(this.outProtocolPathList == null) {
			this.outProtocolPathList = new ArrayList<String>();
		}
		this.outProtocolPathList.add(path);
	}

	public List<String> messagePathList() {
		return messagePathList;
	}

	public void addMessagePathList(String path) {
		if(this.messagePathList == null) {
			this.messagePathList = new ArrayList<String>();
		}
		this.messagePathList.add(path);
	}

	public int gateWayClientListenPort() {
		return gateWayClientListenPort;
	}

	public void setGateWayClientListenPort(int gateWayClientListenPort) {
		this.gateWayClientListenPort = gateWayClientListenPort;
	}

	public int gateWayServiceListenPort() {
		return gateWayServiceListenPort;
	}

	public void setGateWayServiceListenPort(int gateWayServiceListenPort) {
		this.gateWayServiceListenPort = gateWayServiceListenPort;
	}

	public int gateWayHttpMrgPort() {
		return gateWayHttpMrgPort;
	}

	public void setGateWayHttpMrgPort(int gateWayHttpMrgPort) {
		this.gateWayHttpMrgPort = gateWayHttpMrgPort;
	}

	public int gateWayNettyThreadNum() {
		return gateWayNettyThreadNum;
	}

	public void setGateWayNettyThreadNum(int gateWayNettyThreadNum) {
		this.gateWayNettyThreadNum = gateWayNettyThreadNum;
	}

	public Class<? extends SocketConnection> gateWayConnectionClass() {
		return gateWayConnectionClass;
	}

	public void setGateWayConnectionClass(Class<? extends SocketConnection> gateWayConnectionClass) {
		this.gateWayConnectionClass = gateWayConnectionClass;
	}

	public Class<? extends GateWayRecConnection> gateWayRecConnection() {
		return gateWayRecConnection;
	}

	public void setGateWayRecConnection(Class<? extends GateWayRecConnection> gateWayRecConnection) {
		this.gateWayRecConnection = gateWayRecConnection;
	}

	public String serviceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public int serviceNettyThreadNum() {
		return serviceNettyThreadNum;
	}

	public void setServiceNettyThreadNum(int serviceNettyThreadNum) {
		this.serviceNettyThreadNum = serviceNettyThreadNum;
	}

	public Class<? extends SocketConnection> serverConnectionClass() {
		return serverConnectionClass;
	}

	public void setServerConnectionClass(Class<? extends SocketConnection> serverConnectionClass) {
		this.serverConnectionClass = serverConnectionClass;
	}

	public int serverPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public int serverNettyThreadNum() {
		return serverNettyThreadNum;
	}

	public void setServerNettyThreadNum(int serverNettyThreadNum) {
		this.serverNettyThreadNum = serverNettyThreadNum;
	}

	public int httpPort() {
		return httpPort;
	}

	public void setHttpPort(int httpPort) {
		this.httpPort = httpPort;
	}

	public int httpNettyThreadNum() {
		return httpNettyThreadNum;
	}

	public void setHttpNettyThreadNum(int httpNettyThreadNum) {
		this.httpNettyThreadNum = httpNettyThreadNum;
	}

	public String httpHandlePath() {
		return httpHandlePath;
	}

	public void setHttpHandlePath(String httpHandlePath) {
		this.httpHandlePath = httpHandlePath;
	}

	public int clientNettyThreadNum() {
		return clientNettyThreadNum;
	}

	public void setClientNettyThreadNum(int clientNettyThreadNum) {
		this.clientNettyThreadNum = clientNettyThreadNum;
	}

	public int servicePort() {
		return servicePort;
	}

}
