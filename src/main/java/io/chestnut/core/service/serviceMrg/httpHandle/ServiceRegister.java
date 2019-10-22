package io.chestnut.core.service.serviceMrg.httpHandle;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.chestnut.core.network.httpd.HttpHandle;
import io.chestnut.core.service.serviceMrg.Define;
import io.chestnut.core.service.serviceMrg.ServiceMrg;
import io.chestnut.core.service.serviceMrg.entity.Service;
import io.chestnut.core.service.serviceMrg.entity.ServiceMachine;
import io.netty.channel.ChannelHandlerContext;

public class ServiceRegister extends HttpHandle{
	private static final Logger logger = LoggerFactory.getLogger(ServiceRegister.class);


	@Override
	public void doGet(ChannelHandlerContext ctx, Map<String, String> parac) throws Exception {
		doPost(ctx, parac);
	}

	@Override
	public void doPost(ChannelHandlerContext ctx, Map<String, String> parac) throws Exception {
		String serviceName = parac.get("serviceName");
		InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String ipddr = insocket.getAddress().getHostAddress();
	    int port = Integer.valueOf(parac.get("port"));
        logger.info("ServiceRegister serviceName:" + serviceName + " ipddr:" + ipddr + " port:" + port);
		Service service = ServiceMrg.entityMrg.getEntityById(Service.class, serviceName);
		if(service == null) {
			service = new Service();
			service.serviceName = serviceName;
			service.serviceMachineList = new ArrayList<ServiceMachine>();
			service.serviceMachineList.add(new ServiceMachine(ipddr,port));
			ServiceMrg.entityMrg.addEntity(service);
		}else {
			for (ServiceMachine serviceMachine : service.serviceMachineList) {
				if(serviceMachine.ipddr.equals(ipddr)&&serviceMachine.port == port) {
					serviceMachine.state = Define.ServiceMachineStateOnline;
					ServiceMrg.entityMrg.updateField(service, "serviceMachineList");
					response(ctx, "ok");
					return;
				}
			}
			service.serviceMachineList.add(new ServiceMachine(ipddr,port));
			ServiceMrg.entityMrg.updateField(service, "serviceMachineList");
		}
		response(ctx, "ok");
	}

}
