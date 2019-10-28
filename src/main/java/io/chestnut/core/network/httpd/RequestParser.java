package io.chestnut.core.network.httpd;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.CharsetUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class RequestParser {
    public static Map<String, String> parse(FullHttpRequest fullReq) throws IOException{
        HttpMethod method = fullReq.method();
        Map<String, String> parmMap = new HashMap<>();
        if (HttpMethod.GET == method) {
            QueryStringDecoder decoder = new QueryStringDecoder(fullReq.uri(),CharsetUtil.UTF_8);
            decoder.parameters().entrySet().forEach( entry -> {
                parmMap.put(entry.getKey(), entry.getValue().get(0));
            });
        } else if (HttpMethod.POST == method) {
        	String json = fullReq.content().toString(CharsetUtil.UTF_8);
        	parmMap.put("body-Json", json);
        	if(json != null && !json.equals("")) {
        		JsonObject jsonObject = null;
        		try {
        			JsonElement jsonElement = JsonParser.parseString(json);
        			if(jsonElement.isJsonObject()) {
        				jsonObject = (JsonObject) jsonElement;
        			}
        		} catch (Exception e) {
				}
       
        		if(jsonObject != null) {
        			for (Entry<String, JsonElement> entry : jsonObject.entrySet()){
    					StringBuilder value = new StringBuilder(entry.getValue().toString());
    					if(value.length()>0&&value.charAt(0)==34) {
    						parmMap.put(entry.getKey(),value.substring(1, value.length()-1));
    					}else {
    						parmMap.put(entry.getKey(),value.toString());
    					}
        		}
        		}else {
        	     	HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(fullReq);  // 鏄疨OST璇锋眰
                	decoder.offer(fullReq);
                	List<InterfaceHttpData> parmList = decoder.getBodyHttpDatas();
                	for (InterfaceHttpData parm : parmList) {
                		Attribute data = (Attribute) parm;
                		parmMap.put(data.getName(), data.getValue());
                	}
        		}
        		
        	}

        }
        return parmMap;
    }
    
    public static Map<String, String> parse(String uri) {
    	Map<String, String> parmMap = new HashMap<>();
    	 QueryStringDecoder decoder = new QueryStringDecoder(uri,CharsetUtil.UTF_8);
         decoder.parameters().entrySet().forEach( entry -> {
             parmMap.put(entry.getKey(), entry.getValue().get(0));
         });
         return parmMap;
	}
    
    public static String getParameter(HttpRequest request, String parameterName) {
		QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri(),CharsetUtil.UTF_8);
		Map<String, List<String>> params = queryStringDecoder.parameters();
		if (params.get(parameterName) != null)
			return params.get(parameterName).get(0);
		else
			return null;
	}
    
    public static String getServletName(String url) {
    	int index = url.indexOf("?");
    	if(index < 0) {
    		return url;
    	}else {
    		return url.substring(0, index);
    	}
	} 
    
    /**
     *
     * @param host host
     * @return string
     * @throws IOException IOException
     */
    public static String parseIp(String host) throws IOException {
        InetAddress inetAddress = InetAddress.getByName(host);
        String address = inetAddress.getHostAddress();
        return address;
    }

}
