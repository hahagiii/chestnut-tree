package io.chestnut.core.service.serviceMrg.httpHandle;

import java.util.Map;

import com.google.gson.JsonObject;

import io.chestnut.core.network.httpd.HttpHandle;
import io.chestnut.core.service.serviceMrg.ServiceMrg;
import io.chestnut.core.service.serviceMrg.entity.Service;
import io.chestnut.core.service.serviceMrg.entity.ServiceMachine;
import io.netty.channel.ChannelHandlerContext;

public class ServiceFind extends HttpHandle{

	@Override
	public void doGet(ChannelHandlerContext ctx, Map<String, String> parac) throws Exception {
		doPost(ctx, parac);
	}

	@Override
	public void doPost(ChannelHandlerContext ctx, Map<String, String> parac) throws Exception {
		String serviceName = parac.get("serviceName");
		Service service = ServiceMrg.entityMrg.getEntityById(Service.class, serviceName);
		JsonObject jsonObject = new JsonObject();
		if(service == null) {
			jsonObject.addProperty("code", 1);
			jsonObject.addProperty("msg", serviceName + "服务未注册");
			response(ctx, jsonObject.toString());
		}else {
			ServiceMachine serviceMachine = service.randServiceMachine();
			if(serviceMachine == null) {
				jsonObject.addProperty("code", 1);
				jsonObject.addProperty("msg", serviceName + " 活跃节点不够");
				response(ctx, jsonObject.toString());
			}else {
				jsonObject.addProperty("code", 0);
				jsonObject.addProperty("ipddr", serviceMachine.ipddr);
				jsonObject.addProperty("port", serviceMachine.port);
				response(ctx, jsonObject.toString());

			}
			
		}
	}

}
