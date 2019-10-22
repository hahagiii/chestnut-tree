package io.chestnut.core.service.serviceMrg.httpHandle;

import java.util.ArrayList;
import java.util.Map;

import com.google.gson.Gson;

import io.chestnut.core.network.httpd.HttpHandle;
import io.chestnut.core.service.serviceMrg.ServiceMrg;
import io.chestnut.core.service.serviceMrg.entity.Service;
import io.netty.channel.ChannelHandlerContext;

public class ServiceInfo extends HttpHandle{

	@Override
	public void doGet(ChannelHandlerContext ctx, Map<String, String> parac) throws Exception {
		doPost(ctx, parac);
	}

	@Override
	public void doPost(ChannelHandlerContext ctx, Map<String, String> parac) throws Exception {
		ArrayList<Service> serviceList = ServiceMrg.entityMrg.getEntity(Service.class);
		 response(ctx, new Gson().toJson(serviceList));
	}
	

}
