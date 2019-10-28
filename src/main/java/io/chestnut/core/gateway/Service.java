package io.chestnut.core.gateway;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Service{
	
	public String serviceName;
	public Service(String serviceName) {
		this.serviceName = serviceName;
	}
	public Map<String,ServiceNode> serviceNodeList = new HashMap<>();

	public void addServiceNode(ServiceNode serviceNode) {
		serviceNodeList.put(serviceNode.getNodeId(), serviceNode);
	}
	
	public void removeServiceNode(ServiceNode serviceNode) {
		serviceNodeList.remove(serviceNode.getNodeId());
	}
	
	
	public Collection<ServiceNode> getNodeList() {
		return serviceNodeList.values();
	}


}
