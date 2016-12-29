package com.yeepbank.android.http;

import android.content.Context;
import android.provider.SyncStateContract;
import android.util.Log;
import com.android.volley.Request;
import com.yeepbank.android.Cst;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.http.HttpRequestQueueManager;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.utils.ApiUtils;
import com.yeepbank.android.utils.Utils;


import java.util.*;

/**
 * Created by WW on 2015/8/21.
 */
public abstract class BaseRequest {

    private Context context;
    private StringListener stringListener;
    protected final int METHOD_POST = Request.Method.POST;
    protected final int METHOD_GET = Request.Method.GET;

    public BaseRequest(Context context, StringListener stringListener) {
        this.context = context;
        this.stringListener = stringListener;
    }

    protected abstract String getUrl();

    protected abstract int getMethod();

    protected abstract Map<String,String> getParams();

    public void stringRequest(){
        if(context == null || BaseActivity.isNetErrorNow){
            return;
        }
        HttpRequestQueueManager.getInstances().init(context);
        Map<String,String> rawParams = getParams();
        if(rawParams == null){
            rawParams = new HashMap<String,String>();
        }
        rawParams.put("timestamp", String.valueOf(System.currentTimeMillis()));

        rawParams.put("version", Cst.getVersionCode(context));
        rawParams.put("client", "Android");
        if (Cst.SIGN_METHOD_SECRET == null){
            return;
        }
        String sign = ApiUtils.signTopRequest(rawParams, Cst.SIGN_METHOD_SECRET, Cst.COMMON.SIGN_METHOD_HMAC);
        rawParams.put("sign", sign);
        //Log.e("PARAMS",getUrl()+"\n"+logGenerateQueryString(rawParams,true));
        if(getMethod() == Request.Method.POST){
            HttpRequestQueueManager.getInstances().stringRequest(getMethod(), rawParams, getUrl(), stringListener);
        }else if(getMethod() == Request.Method.GET){
            String url = getUrl();
            if(rawParams != null){
                url +="?"+generateQueryString(rawParams,true);
            }
            HttpRequestQueueManager.getInstances().stringRequest(getMethod(),url,stringListener);
        }

    }

    public void stringRequest(int connectTimeOut){
        if(context == null || BaseActivity.isNetErrorNow){
            return;
        }
        HttpRequestQueueManager.getInstances().init(context);
        Map<String,String> rawParams = getParams();
        if(rawParams == null){
            rawParams = new HashMap<String,String>();
        }
        rawParams.put("timestamp", String.valueOf(System.currentTimeMillis()));

        rawParams.put("version", Cst.getVersionCode(context));
        rawParams.put("client", "Android");
        String sign = ApiUtils.signTopRequest(rawParams, Cst.SIGN_METHOD_SECRET, Cst.COMMON.SIGN_METHOD_HMAC);
        rawParams.put("sign", sign);



        if(getMethod() == Request.Method.POST){
            HttpRequestQueueManager.getInstances().stringRequest(getMethod(), rawParams, getUrl(), stringListener,connectTimeOut);
        }else if(getMethod() == Request.Method.GET){
            String url = getUrl();
            if(rawParams != null){
                url +="?"+generateQueryString(rawParams,true);
            }

            HttpRequestQueueManager.getInstances().stringRequest(getMethod(),url,stringListener,connectTimeOut);
        }

    }

    public static String generateQueryString(Map<String, String> params, boolean needUrlEncode) {
        if (params == null || params.isEmpty()) {
            return null;
        }
        StringBuffer buffer = new StringBuffer();
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            String value = params.get(key);
            if (needUrlEncode) {
                value = ApiUtils.getUtf8EscapedString(value);
            }
            buffer.append("&").append(key).append("=").append(value);
        }
        return buffer.substring(1);
    }

    public static String logGenerateQueryString(Map<String, String> params, boolean needUrlEncode) {
        if (params == null || params.isEmpty()) {
            return null;
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append("params：{\n");
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            String value = params.get(key);
            if (needUrlEncode) {
                value = ApiUtils.getUtf8EscapedString(value);
            }
            buffer.append("\n").append("  "+key).append(" = ").append(value);
        }
        buffer.append("\n}");
        return buffer.substring(1);
    }
}
