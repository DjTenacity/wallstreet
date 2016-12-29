package com.yeepbank.android.utils;

import com.yeepbank.android.Cst;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;

/**
 * @author songyu.li@yeepay.com
 * @createDatetime 2016年3月18日 下午4:21:44
 */

public class ApiUtils {
	
	public static String signTopRequest(Map<String, String> params, String secret, String signMethod){
	   
	    String[] keys = params.keySet().toArray(new String[0]);
	    Arrays.sort(keys);
	 
	    StringBuffer query = new StringBuffer();
	    if (Cst.COMMON.SIGN_METHOD_MD5.equals(signMethod)) {
	        query.append(secret);
	    }
	    for (String key : keys) {
	        String value = params.get(key);
	        if (StringUtils.areNotEmpty(key, value)) {
	            query.append(key).append(value);
	        }
	    }
	 
	   
	    byte[] bytes;
		try {
			if (Cst.COMMON.SIGN_METHOD_HMAC.equals(signMethod)) {
				bytes = encryptHMAC(query.toString(), secret);
			} else {
				query.append(secret);
				bytes = encryptMD5(query.toString());
			}
		}catch (IOException e) {
			e.printStackTrace();
			bytes = new byte[]{};
		}


		return byte2hex(bytes);
	}
	 
	public static byte[] encryptHMAC(String data, String secret) throws IOException {
	    byte[] bytes = null;
	    try {
	        SecretKey secretKey = new SecretKeySpec(secret.getBytes(Cst.COMMON.CHARSET_UTF8), "HmacMD5");
	        Mac mac = Mac.getInstance(secretKey.getAlgorithm());
	        mac.init(secretKey);
	        bytes = mac.doFinal(data.getBytes(Cst.COMMON.CHARSET_UTF8));


	    } catch (GeneralSecurityException gse) {
	        throw new IOException(gse.toString());
	    }
	    return bytes;
	}
	 
	public static byte[] encryptMD5(String data) throws IOException {
		MessageDigest md5 = getMd5MessageDigest();
		return md5.digest(data.getBytes(Cst.COMMON.CHARSET_UTF8));
	}
	 
	private static MessageDigest getMd5MessageDigest() throws IOException {
		try {
			return MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IOException(e.getMessage());
		}
	}
	
	public static String byte2hex(byte[] bytes) {
	    StringBuffer sign = new StringBuffer();
	    for (int i = 0; i < bytes.length; i++) {
	        String hex = Integer.toHexString(bytes[i] & 0xFF);
	        if (hex.length() == 1) {
	            sign.append("0");
	        }
	        sign.append(hex.toUpperCase());
	    }
	    return sign.toString();
	}

	public static String getUtf8EscapedString(String input) {
		if(input == null){
			return null;
		}
		String result = null;
		try {
			result = URLEncoder.encode(input, "utf8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
		return result;
	}
	
}
