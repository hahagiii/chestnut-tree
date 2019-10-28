package io.chestnut.core;

import java.util.Collection;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.chestnut.core.gateway.ConnectionServiceLinkGateWay;
import io.chestnut.core.gateway.GateWayServiceConnection;
import io.chestnut.core.gateway.ServiceMrg;
import io.chestnut.core.gateway.ServiceNode;
import io.chestnut.core.network.ChestnutClient;
import io.chestnut.core.network.ChestnutHttpd;
import io.chestnut.core.network.ChestnutServer;
import io.chestnut.core.network.SocketConnection;
import io.chestnut.core.protocol.ProtocolIn;
import io.chestnut.core.protocol.ProtocolInFactory;
import io.chestnut.core.protocol.SimpleProtocolInFactory;
import io.chestnut.core.service.ServiceChestnut;
import io.chestnut.core.service.ServiceServerConnection;
import io.chestnut.core.util.DebugUtil;
import io.netty.util.NettyRuntime;

public class ChestnutTree {
	public static final Logger logger = LoggerFactory.getLogger(ChestnutTree.class);
	private ChestnutTreeOption chestnutTreeOption;
	private ChestnutServer chestnutServer;
	private ChestnutHttpd chestnutHttpd;
	private ChestnutClient chestnutClient;
	private ChestnutClient serviceClient;
	private ChestnutClient serviceLinkGateWayClient;
	private ChestnutServer serviceServer;
	private ChestnutServer gateWayServerForClient;
	private ChestnutServer gateWayServerForService;
	private ProtocolInFactory protocolInFactory;
	private InternalMsgFactory internalMsgFactory;
	
	private ChestnutEventLoopGroup defaultChestnutEventLoopGroup;
	
	
	Map<String, ChestnutEventLoopGroup> chestnutEventLoopGroupMap = new ConcurrentHashMap<>();
	public Timer timer = new Timer("timerEvent");
	
	public Map<String,Chestnut> chestnutMap = new ConcurrentHashMap<>();
	
	public ChestnutTree(ChestnutTreeOption chestnutTreeOption) {
		this.chestnutTreeOption = chestnutTreeOption;
	}
	
	
	public ChestnutEventLoopGroup newChestnutEventLoopGroup(ThreadGroupOptions options) throws Exception {
		if(options.getThreadGroupName() == null) {
			throw new Exception("ThreadGroupName is null");
		}
		if(options.getThreadNum() <= 0) {
			throw new Exception("getThreadNum is 0");
		}
		ChestnutEventLoopGroup chestnutEventLoopGroup = chestnutEventLoopGroupMap.get(options.getThreadGroupName());
		if(chestnutEventLoopGroup != null) {
			throw new Exception("ThreadGroupName已经存在");
		}
		chestnutEventLoopGroup = new ChestnutEventLoopGroup(options);
		chestnutEventLoopGroup.setTimerEventTimer(timer);
		chestnutEventLoopGroupMap.put(options.getThreadGroupName(), chestnutEventLoopGroup);
		return chestnutEventLoopGroup;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Chestnut> T getChestnut(String chestnutId) {
		Chestnut chestnut = chestnutMap.get(chestnutId);
		if(chestnut == null) {
			return null;
		}else {
			return (T) chestnut;
		}
	}
	
	
	
	public void deployChestnut(Chestnut chestnut) throws Exception {
		if(chestnutMap.get(chestnut.getId()) != null) {
			throw new Exception(chestnut.getId() + "已经存在");
		}
		if(defaultChestnutEventLoopGroup == null) {
			defaultChestnutEventLoopGroup = new ChestnutEventLoopGroup(new ThreadGroupOptions().setThreadGroupInfo("defaultChestnutEventLoopGroup", NettyRuntime.availableProcessors()));
		}
		ChestnutEventLoopThread chestnutEventLoopThread = defaultChestnutEventLoopGroup.getLowestLoadThread();
		chestnut.setChestnutEventLoopThread(chestnutEventLoopThread);
		chestnutEventLoopThread.chestnutBindThread(chestnut);
		chestnut.setChestnutTree(this);
		chestnutMap.put(chestnut.getId(), chestnut);
		chestnut.start();
	}


	public void deployChestnut(Chestnut chestnut,DeployOptions deployOptions) throws Exception {
		if(chestnutMap.get(chestnut.getId()) != null) {
			throw new Exception(chestnut.getId() + "已经存在");
		}
		if(deployOptions.getThreadGroupName() == null) {
			throw new Exception("未指定线程组");
		}
		ChestnutEventLoopGroup chestnutEventLoopGroup = chestnutEventLoopGroupMap.get(deployOptions.getThreadGroupName());
		if(chestnutEventLoopGroup == null) {
			throw new Exception(deployOptions.getThreadGroupName() + "线程组为初始化");
		}
		ChestnutEventLoopThread chestnutEventLoopThread = chestnutEventLoopGroup.getLowestLoadThread();
		chestnut.setChestnutEventLoopThread(chestnutEventLoopThread);
		chestnutEventLoopThread.chestnutBindThread(chestnut);
		chestnut.setChestnutTree(this);
		chestnut.start();
		chestnutMap.put(chestnut.getId(), chestnut);
	}
	
	public SocketConnection connect(String ip,int port, Class<? extends SocketConnection> protocolCodec, Object ...par) throws Exception {
		if(chestnutClient == null) {
			int clientNettyThreadNum = chestnutTreeOption.clientNettyThreadNum();
			if(clientNettyThreadNum <=0) {
				clientNettyThreadNum = NettyRuntime.availableProcessors();
			}
			chestnutClient = new ChestnutClient(clientNettyThreadNum);
		}
		SocketConnection socketConnection = protocolCodec.newInstance();
		chestnutClient.connect(ip, port, socketConnection, par);
		return socketConnection;
	}
	
	public SocketConnection connect(String ip,int port, Class<? extends SocketConnection> protocolCodec) throws Exception {
		Object []par = null;
		return connect(ip, port, protocolCodec,par);
	}


	public void removeChestnut(String id) {
		Chestnut chestnut = chestnutMap.get(id);
		if(chestnut == null) {
			return;
		}
		chestnutMap.remove(id);
		chestnut.chestnutEventLoopThread().removeChestnut(id);
	}



	public void run() throws Exception {
		if(chestnutTreeOption.serverPort() > 0) {
			chestnutServer = new ChestnutServer(chestnutTreeOption.serverNettyThreadNum());
			chestnutServer.listen(chestnutTreeOption.serverPort(), chestnutTreeOption.serverConnectionClass());
		}
		if(chestnutTreeOption.httpPort() > 0) {
			chestnutHttpd = new ChestnutHttpd(chestnutTreeOption.httpNettyThreadNum());
			chestnutHttpd.listen(chestnutTreeOption.httpPort(), chestnutTreeOption.httpHandlePath());
		}
		
		if(chestnutTreeOption.servicePort() > 0) {
			serviceServer = new ChestnutServer(chestnutTreeOption.serviceNettyThreadNum());
			serviceServer.listen(chestnutTreeOption.servicePort(), ServiceServerConnection.class,this);
		}
		
		if(chestnutTreeOption.gateWayNettyThreadNum() > 0) {
			gateWayServerForClient = new ChestnutServer(chestnutTreeOption.gateWayNettyThreadNum());
			gateWayServerForClient.listen(chestnutTreeOption.gateWayClientListenPort(), chestnutTreeOption.gateWayConnectionClass());
			
			gateWayServerForService = new ChestnutServer(2);
			gateWayServerForService.listen(chestnutTreeOption.gateWayServiceListenPort(), GateWayServiceConnection.class, chestnutTreeOption.gateWayRecConnection());
			
			chestnutHttpd = new ChestnutHttpd(1);
			chestnutHttpd.listen(chestnutTreeOption.gateWayHttpMrgPort(), "io.chestnut.core.gateway.httpHandle");
		}
		if(chestnutTreeOption.messagePathList() != null) {
			internalMsgFactory = new InternalMsgFactory();
			for (String path : chestnutTreeOption.messagePathList()) {
				internalMsgFactory.messageFactory.addPath(path);
			}
		}
		if(chestnutTreeOption.inProtocolPathList() != null) {
			protocolInFactory = new SimpleProtocolInFactory();
			for (String path : chestnutTreeOption.inProtocolPathList()) {
				protocolInFactory.addPath(path);
			}
		}
	}

	public void request(String serviceId, String destChestnutId, InternalMessage message, Chestnut callerChestnut, Handler handler) throws Exception {
		Chestnut chestnut = chestnutMap.get(serviceId);
		if(chestnut == null) {
			throw new Exception("serviceId is not exist");
		}
		ServiceChestnut serviceChestnut = (ServiceChestnut) chestnut;
		message.setDestChestnutId(destChestnutId);
		serviceChestnut.request(message, callerChestnut,handler);
	}

	public ConnectionServiceLinkGateWay linkGateWay(String gateWayIp, int gateWayPort,String serviceName, int serviceStartServerId,int serviceEndServerId) throws Exception {
		serviceLinkGateWayClient = new ChestnutClient(1);
		ConnectionServiceLinkGateWay connectionServiceLinkGateWay = new ConnectionServiceLinkGateWay();
		serviceLinkGateWayClient.connect(gateWayIp, gateWayPort, connectionServiceLinkGateWay,serviceName, this);
		return connectionServiceLinkGateWay;
	}
	
	public void connectServiceFromGateWay(String serviceName,int selfServerId,String gateWayIp, int gateWayPort) throws Exception {

	}
	
	public void connectService(String serviceName,String ip,int port) throws Exception {
			if(serviceClient == null) {
				serviceClient = new ChestnutClient(1);
			}
			ServiceChestnut serviceChestnut = new ServiceChestnut(serviceName);
			deployChestnut(serviceChestnut);
			serviceChestnut.setServiceClient(serviceClient);
			try {
				serviceChestnut.connectService(ip,port);
			} catch (Exception e) {
				logger.info("connectService " + serviceName + " ip:" + ip + " port:" + port + " 连接失败，原因是 " + DebugUtil.printStackFirstLine(e));
				removeChestnut(serviceChestnut.getId());
				return;
			}
			logger.info("connectService " + serviceName + " ip:" + ip + " port:" + port);
	}

	public void transmitProtocol(String serviceName,int serverId, String playerId, byte dst[]) {
		ServiceMrg.transmitProtocol(serviceName,serverId,playerId,dst);
	}
	
	
	public Collection<ServiceNode> getServiceNodeList(String serviceName) {
		return ServiceMrg.getServiceNodeList(serviceName);
	}

	public ChestnutTreeOption chestnutTreeOption() {
		return chestnutTreeOption;
	}


	@SuppressWarnings("unchecked")
	public <T extends ProtocolIn> T getProtocolIn(short id) {
		ProtocolIn protocolIn = protocolInFactory.getProtocolIn(id);
		if(protocolIn == null) {
			logger.error("protocolIn is null messageId " + id);
			return null;
		}
		return (T) protocolIn;
	}


	@SuppressWarnings("unchecked")
	public <T extends InternalMessage> T getMessage(short messageId) {
		InternalMessage internalMessage = internalMsgFactory.getMessage(messageId);
		if(internalMessage == null) {
			logger.error("internalMessage is null messageId " + messageId);
			return null;
		}
		return (T) internalMessage;
	}
}
