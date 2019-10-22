package io.chestnut.core;

import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.chestnut.core.network.ChestnutClient;
import io.chestnut.core.network.ChestnutHttpd;
import io.chestnut.core.network.ChestnutServer;
import io.chestnut.core.network.httpd.HttpClient;
import io.chestnut.core.service.ServiceChestnut;
import io.chestnut.core.service.ServiceServerConnection;
import io.netty.util.NettyRuntime;

public class ChestnutTree {
	public static final Logger logger = LoggerFactory.getLogger(ChestnutTree.class);
	public ChestnutTreeOption chestnutTreeOption;
	public ChestnutServer chestnutServer;
	public ChestnutHttpd chestnutHttpd;
	public ChestnutClient chestnutClient;
	public ChestnutClient serviceClient;
	public ChestnutServer serviceServer;
	public ChestnutEventLoopGroup defaultChestnutEventLoopGroup;
	
	
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
		chestnut.start();
		chestnutMap.put(chestnut.getId(), chestnut);
	}
	
	public void connect(String ip,int port, SocketConnection socketConnection) throws Exception {
		if(chestnutClient == null) {
			int clientNettyThreadNum = chestnutTreeOption.clientNettyThreadNum;
			if(clientNettyThreadNum <=0) {
				clientNettyThreadNum = NettyRuntime.availableProcessors();
			}
			chestnutClient = new ChestnutClient(clientNettyThreadNum);
		}
		chestnutClient.connect(ip, port, socketConnection);
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
		if(chestnutTreeOption.serverPort > 0) {
			chestnutServer = new ChestnutServer(chestnutTreeOption.serverNettyThreadNum);
			chestnutServer.listen(chestnutTreeOption.serverPort, chestnutTreeOption.serverConnectionClass);
		}
		if(chestnutTreeOption.httpPort > 0) {
			chestnutHttpd = new ChestnutHttpd(chestnutTreeOption.httpNettyThreadNum);
			chestnutHttpd.listen(chestnutTreeOption.httpPort, chestnutTreeOption.httpHandlePath);
		}
		
		if(chestnutTreeOption.servicePort > 0) {
			serviceServer = new ChestnutServer(chestnutTreeOption.serviceNettyThreadNum);
			serviceServer.listen(chestnutTreeOption.servicePort, new ServiceServerConnection(this));
			String serviceMrgAddress = chestnutTreeOption.serviceMrgAddress;
			if(!serviceMrgAddress.contains("http")) {
				serviceMrgAddress = "http://" + serviceMrgAddress;
			}
			if(serviceMrgAddress.charAt(serviceMrgAddress.length() - 1) != '/') {
				serviceMrgAddress += '/';
			}
			String pac = "ServiceRegister?serviceName=" + chestnutTreeOption.serviceName + "&port=" + chestnutTreeOption.servicePort;
			String httpGet = serviceMrgAddress + pac;
			String res = HttpClient.httpGet(httpGet);
			logger.info(chestnutTreeOption.serviceName + " service ServiceRegister " + res);
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


	public void connectService(String serviceName,JsonObject parameter) throws Exception {
		String serviceReq = "/ServiceFind?serviceName="+serviceName;
		if(parameter != null) {
			serviceReq += "&parameter="+parameter.toString();
		}
		String res = HttpClient.httpGet("http://"+chestnutTreeOption.serviceMrgAddress + serviceReq);
		JsonObject jsonObject = JsonParser.parseString(res).getAsJsonObject();
		if(jsonObject.get("code").getAsInt() == 0) {
			String ip = jsonObject.get("ipddr").getAsString();
			int port = jsonObject.get("port").getAsInt();
			if(serviceClient == null) {
				serviceClient = new ChestnutClient(1);
			}
			ServiceChestnut serviceChestnut = new ServiceChestnut(serviceName);
			serviceChestnut.setServiceClient(serviceClient);
			serviceChestnut.connectService(ip,port);
			chestnutMap.put(serviceName, serviceChestnut);
			logger.info("connectService " + serviceName + " ip:" + ip + " port:" + port);
		}else {
			throw new Exception(res);
		}
		
	}


	public void connectService(String serviceName) throws Exception {
		connectService(serviceName, null);
	}


	
	


	
	
	
	
}
