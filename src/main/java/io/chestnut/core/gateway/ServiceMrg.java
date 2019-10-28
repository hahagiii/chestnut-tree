package io.chestnut.core.gateway;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ServiceMrg {
	private static Map<String,Service> serviceMap = new HashMap<>();

	public static Service getService(String serviceName) {
		return serviceMap.get(serviceName);
	}

	public static Collection<Service> getAllService() {
		return serviceMap.values();
	}

	public static void addService(Service service) {
		serviceMap.put(service.serviceName, service);
	}

	public static void transmitProtocol(String serviceName, int serverId, String playerId, byte[] dst) {
		Service service = serviceMap.get(serviceName);
		if(service == null) {
			return;
		}
	}

	public static Collection<ServiceNode> getServiceNodeList(String serviceName) {
		Service service = serviceMap.get(serviceName);
		if(service == null) {
			return null;
		}
		return service.getNodeList();
		
	}
}
