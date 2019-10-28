package io.chestnut.core.gateway;

import io.chestnut.core.InternalMessage;

public interface GateWayRecConnection {
	public void toClient(String playerId, byte[] dest);
	
	public void toGateWay(String serviceName,InternalMessage internalMessage);
}
