package io.chestnut.core.gateway.httpHandle;

import java.util.Collection;
import java.util.Map;

import com.google.gson.Gson;

import io.chestnut.core.gateway.Service;
import io.chestnut.core.gateway.ServiceMrg;
import io.chestnut.core.network.httpd.HttpHandle;
import io.netty.channel.ChannelHandlerContext;

public class ServiceInfo extends HttpHandle{

	@Override
	public void doGet(ChannelHandlerContext ctx, Map<String, String> parac) throws Exception {
		doPost(ctx, parac);
	}

	@Override
	public void doPost(ChannelHandlerContext ctx, Map<String, String> parac) throws Exception {
		Collection<Service> serviceList = ServiceMrg.getAllService();
		 response(ctx, new Gson().toJson(serviceList));
	}
	

}
