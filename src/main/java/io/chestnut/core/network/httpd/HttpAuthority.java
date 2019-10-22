package io.chestnut.core.network.httpd;

import java.util.HashMap;
import java.util.Map;

import io.chestnut.core.util.AesEncryption;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;

class User{
	String userId;
	String token;
	String authority;
}

public class HttpAuthority {
	public Map<String, User> userMap = new HashMap<>();

	public void encryptionHandle(Map<String, HttpHandle> httpHandleMap, ChannelHandlerContext ctx,FullHttpRequest request) throws Exception {
		String username = RequestParser.getParameter(request, "username");
		if(username == null) {
			throw new Exception("username is null: " + username);
		}
		User user = userMap.get(username);
		if(user == null) {
			throw new Exception("no username: " + username);
		}
		String encryptLoad = RequestParser.getParameter(request, "encryptLoad");
		if(encryptLoad == null) {
			throw new Exception( "encryptLoad is null: " + username);
		}
		String decryptDatas = AesEncryption.desEncrypt(encryptLoad, user.token);
		String servletName = RequestParser.getServletName(decryptDatas);
		if(servletName == null) {
			throw new Exception( "servletName is null: " + username);
		}
		HttpHandle httpHandle = httpHandleMap.get(servletName);
		if(httpHandle == null) {
			throw new Exception( "httpHandle is null: " + servletName);
		}
		if(request.method() == HttpMethod.GET) {
			httpHandle.doGet(ctx, RequestParser.parse(request));
		}else if(request.method() == HttpMethod.POST) {
			httpHandle.doPost(ctx, RequestParser.parse(request));
		}else {
			throw new Exception( "no support method: " + request.method());
		}
	}
	
	
}
