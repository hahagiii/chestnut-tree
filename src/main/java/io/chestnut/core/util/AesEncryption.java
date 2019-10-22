package io.chestnut.core.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
public class AesEncryption{
	
    public static void main(String args[]) throws Exception {
    	System.out.println("11111111111: "+desEncrypt("CRuT+r5mSLzkq1u6mYMWqdsl40lSdjAqGo8HiyPSC5DCHDDhpprphZ0El2TjNfeg","c3e115c0263448a694b66f69957ac15f"));
        System.out.println("22222222222: "+encrypt("/getPlatformList","c3e115c0263448a694b66f69957ac15f"));
        System.out.println("33333333333: "+desEncrypt(encrypt("/getPlatformList","c3e115c0263448a694b66f69957ac15f"),"c3e115c0263448a694b66f69957ac15f"));
    }
    
	public static String encrypt(String data,String key) {
    	try {
    		String iv = "1234567812345678";
    		Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
    		int blockSize = cipher.getBlockSize();
    		byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
    		int plaintextLength = dataBytes.length;
    		if (plaintextLength % blockSize != 0) {
    			plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
    		}
    		byte[] plaintext = new byte[plaintextLength];
    		System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
    		SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
    		IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
    		cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
    		byte[] encrypted = cipher.doFinal(plaintext);
            Encoder encoder = Base64.getEncoder();
            String returnData = encoder.encodeToString(encrypted);
    		//System.out.println("encrypt " + data + "key is " + key + " returnData " + returnData);
    		return returnData;
    	} catch (Exception e) {
    		e.printStackTrace();
    		return null;
    	}
    }
	public static String desEncrypt(String data,String key){
		try {
    		String iv = "1234567812345678";
            Decoder decoder = Base64.getDecoder();
    		byte[] encrypted1 = decoder.decode(data.getBytes(StandardCharsets.UTF_8));
    		Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
    		SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
    		IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
    		cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
    		byte[] original = cipher.doFinal(encrypted1);
    		return byteToStr(original);
		} catch (Exception e) {
    		System.out.println("desEncrypt error data is " + data + "key is " + key + " e " + DebugUtil.printStackFirstLine(e));
    		return null;
    	}
    	
    }
    
    public static String byteToStr(byte[] buffer) {
		try {
			int length = buffer.length;
			while(--length>0) {
				if (buffer[length] != 0) 
					break;	
			}
			return new String(buffer, 0, length+1, "UTF-8");
		} catch (Exception e) {
			return "";
		}
	}
}

