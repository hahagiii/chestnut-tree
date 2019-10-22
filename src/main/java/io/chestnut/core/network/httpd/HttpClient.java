package io.chestnut.core.network.httpd;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import io.chestnut.core.util.DebugUtil;



public class HttpClient {
	private static Logger logger = LoggerFactory.getLogger(HttpClient.class);    //鏃ュ織璁板綍


    public static String httpPost(String url,JsonObject jsonParam){
        return httpPost(url, jsonParam, false);
    }
 

    public static String httpPost(String url,JsonObject jsonParam, boolean noNeedResponse){
    	CloseableHttpClient httpClient = HttpClientBuilder.create().build();
    	RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(3000).setConnectionRequestTimeout(3000).setSocketTimeout(3000).setCookieSpec(CookieSpecs.IGNORE_COOKIES).build();
    	String jsonResult = "";
        HttpPost method = new HttpPost(url);
        method.setConfig(requestConfig);
        
        try {
            if (null != jsonParam) {
                StringEntity entity = new StringEntity(jsonParam.toString(), "utf-8");
                entity.setContentEncoding("UTF-8");
                entity.setContentType("application/json");
                method.setEntity(entity);
            }
            HttpResponse result = httpClient.execute(method);
            url = URLDecoder.decode(url, "UTF-8");
            if (result.getStatusLine().getStatusCode() == 200) {
                String str = "";
                try {
                    str = EntityUtils.toString(result.getEntity());
                    if (noNeedResponse) {
                        return null;
                    }
                    jsonResult = str; //JSONObject.parseObject(str);
                } catch (Exception e) {
                    logger.error("post璇锋眰鎻愪氦澶辫触:" + url, e);
                    return DebugUtil.printStack(e);
                }
            }
        } catch (IOException e) {
            logger.error("post璇锋眰鎻愪氦澶辫触:" + url, e);
            return DebugUtil.printStack(e);
        }
        return jsonResult;
    }
 
 
    public static String httpGet(String strUrl) throws Exception{
    	return httpGet(strUrl, 15000);
    }
    public static String httpGet(String strUrl,int connectTimeout) throws Exception{
    	String strResult = "";
    	CloseableHttpClient client = HttpClientBuilder.create().build();            
    	try {
    		URI uri = new URI(strUrl);
        	HttpGet request = new HttpGet(uri);
        	RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectTimeout).setSocketTimeout(connectTimeout).build();
        	request.setConfig(requestConfig);
        	HttpResponse response = client.execute(request);
        	if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
        		strResult = EntityUtils.toString(response.getEntity());
        	} 
		} catch (Exception e) {
			//System.out.println("strUrl: " + strUrl + " error " + DebugUtil.printStackFirstLine(e));
	    	throw e;
		}finally {
			client.close();
		}
        return strResult;
    }

	// send bytes and recv bytes
	public static byte[] post(String url, byte[] bytes, String contentType) throws IOException {
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(new ByteArrayEntity(bytes));
		if (contentType != null)
			httpPost.setHeader("Content-type", contentType);
		httpPost.setHeader("Accept-Encoding", "identity");
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(3000).setConnectionRequestTimeout(3000)
				.setSocketTimeout(3000).build();
		httpPost.setConfig(requestConfig);
		CloseableHttpResponse httpResponse = HttpClients.createDefault().execute(httpPost);
		try {
			HttpEntity entityResponse = httpResponse.getEntity();
			return EntityUtils.toByteArray(entityResponse);
			// int contentLength = (int) entityResponse.getContentLength();
			// if (contentLength <= 0)
			// throw new IOException("No response");
			// byte[] respBuffer = new byte[contentLength];
			// if (entityResponse.getContent().read(respBuffer) !=
			// respBuffer.length)
			// throw new IOException("Read response buffer error");
			// return respBuffer;
		} finally {
			httpResponse.close();
		}
	}

	public static byte[] get(String url) throws IOException {
		HttpGet httpGet = new HttpGet(url);
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(3000).setConnectionRequestTimeout(3000)
				.setSocketTimeout(3000).build();
		httpGet.setConfig(requestConfig);
		httpGet.setHeader("Accept-Encoding", "identity");
		CloseableHttpResponse httpResponse = HttpClients.createDefault().execute(httpGet);
		try {
			HttpEntity entityResponse = httpResponse.getEntity();
			return EntityUtils.toByteArray(entityResponse);
			// int contentLength = (int)
			// EntityUtils.toString(entityResponse).length();
			// if (contentLength <= 0)
			// throw new IOException("No response");
			// byte[] respBuffer = new byte[contentLength];
			// if (entityResponse.getContent().read(respBuffer) !=
			// respBuffer.length)
			// throw new IOException("Read response buffer error");
			// return respBuffer;
		} finally {
			httpResponse.close();
		}
	}

	public static byte[] post(String url, byte[] bytes) throws IOException {
		return post(url, bytes, null);
	}

	public static String postXml(String url, String str) throws IOException {
		byte[] reqBuffer = str.getBytes(Charset.forName("UTF-8"));
		byte[] respBuffer = post(url, reqBuffer, "application/xml; charset=UTF-8");
		String resp = new String(respBuffer, Charset.forName("UTF-8"));
		return resp;
	}

}