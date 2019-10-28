package io.chestnut.core.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import io.chestnut.core.Chestnut;
import io.chestnut.core.Handler;
import io.chestnut.core.InternalMessage;
import io.chestnut.core.network.ChestnutClient;

class Callback{
	public Chestnut caller;
	public Handler handler;
	
	public Callback(Chestnut caller, Handler handler) {
		this.caller = caller;
		this.handler = handler;
	}
}

public class ServiceChestnut extends Chestnut{
	public String serviceName;
	private ServiceClientSocketConnection serviceClientSocketConnection;
	private ChestnutClient serviceClient;
	public Map<String, Callback> callbackMap = new ConcurrentHashMap<String, Callback>();

	public ServiceChestnut(String serviceName) {
		this.serviceName = serviceName;
	}

	@Override
	public String getId() {
		return serviceName;
	}

	public ChestnutClient getServiceClient() {
		return serviceClient;
	}

	public void setServiceClient(ChestnutClient serviceClient) {
		this.serviceClient = serviceClient;
	}


	public ServiceClientSocketConnection connectService(String ip, int port) throws Exception {
		ServiceClientSocketConnection ServiceClientSocketConnection = new ServiceClientSocketConnection();
		serviceClient.connect(ip, port, ServiceClientSocketConnection, this);
		return ServiceClientSocketConnection;
	}

	public void request(InternalMessage message, Chestnut caller, Handler handler) {
		Callback Callback = new Callback(caller,handler);
		String uuid = UUID.randomUUID().toString();
		message.setMessageSerialId(uuid);
		callbackMap.put(uuid, Callback);
		serviceClientSocketConnection.channel.writeAndFlush(message);
	}

	public ServiceClientSocketConnection getServiceClientSocketConnection() {
		return serviceClientSocketConnection;
	}

	public void setServiceClientSocketConnection(ServiceClientSocketConnection serviceClientSocketConnection) {
		this.serviceClientSocketConnection = serviceClientSocketConnection;
	}


	
    	   
}

