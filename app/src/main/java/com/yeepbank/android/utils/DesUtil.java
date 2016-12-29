package com.yeepbank.android.utils;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;
import android.util.Base64;
import com.yeepbank.android.Cst;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;


public class DesUtil {
	 
    private final static String DES = "DES";
    private final static String key = Cst.KEY;
 
    public static void main(String[] args) throws Exception {
        String data = "测试";
        System.out.println(encrypt(data));
        System.out.println(decrypt(encrypt(data)));
 
    }
    
    /**
     * Description 根据键值进行加密
     * @param data 
     * @param_key  加密键byte数组
     * @return
     * @throws Exception
     */
    public static String encryptForAndorid(String data) throws Exception {
        byte[] bt = encrypt(data.getBytes(), key.getBytes());
        String strs = new BASE64Encoder().encode(bt);
        return strs;

    }
    
    /**
     * Description 根据键值进行加密
     * @param data 
     * @param_key  加密键byte数组
     * @return
     * @throws Exception
     */
    public static String encrypt(String data){

        byte[] bt = new byte[0];
        try {
            bt = encrypt(data.getBytes(), key.getBytes());
            String strs = new BASE64Encoder().encode(bt);
            return  URLEncoder.encode(strs, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
        //return Base64.encodeToString(data.trim().getBytes(), Base64.NO_WRAP);

//            return strs;


    }
 
    /**
     * Description 根据键值进行解密
     * @param data
     * @param_key  加密键byte数组
     * @return
     * @throws IOException
     * @throws Exception
     */
    public static String decrypt(String data){
    	
        if (data == null)
            return null;
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] buf;
        byte[] bt = null;
		try {
			buf = decoder.decodeBuffer(URLDecoder.decode(data, "UTF-8"));
			bt = decrypt(buf,key.getBytes("UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String decode = null;
		try {
			decode = new String(bt, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		} 
        return decode;
    }
 
    /**
     * Description 根据键值进行加密
     * @param data
     * @param key  加密键byte数组
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data, byte[] key) throws Exception {
    	
        // 生成一个可信任的随机数源
    	 SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
         sr.setSeed(key);
 
        // 从原始密钥数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);
 
        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);
 
        // Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance(DES);
 
        // 用密钥初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
 
        return cipher.doFinal(data);
    }
     
     
    /**
     * Description 根据键值进行解密
     * @param data
     * @param key  加密键byte数组
     * @return
     * @throws Exception
     */
    private static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        // 生成一个可信任的随机数源
    	 SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
         sr.setSeed(key);
 
        // 从原始密钥数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);
 
        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);
 
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance(DES);
 
        // 用密钥初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
 
        return cipher.doFinal(data);
    }
}
