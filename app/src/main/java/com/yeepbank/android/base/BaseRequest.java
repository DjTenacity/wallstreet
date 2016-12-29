package com.yeepbank.android.base;

import android.content.Context;
import android.util.Log;
import com.android.volley.Request;
import com.yeepbank.android.http.HttpRequestQueueManager;
import com.yeepbank.android.http.StringListener;


import java.util.HashMap;
import java.util.Map;

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

        HttpRequestQueueManager.getInstances().init(context);
        if(getMethod() == Request.Method.POST){
            HttpRequestQueueManager.getInstances().stringRequest(getMethod(),getParams(),getUrl(),stringListener);
        }else if(getMethod() == Request.Method.GET){
            HashMap<String,String> params = (HashMap<String, String>) getParams();
            String url = getUrl();
            if(params != null){
                url +="?";
                for(String key:params.keySet()){
                    if(params.get(key) != null){
                        url+=key+"="+params.get(key)+"&";
                    }
                }
                if(url.lastIndexOf("&") == url.length() - 1){
                    url = url.substring(0,url.length()-1);
                }
            }

            HttpRequestQueueManager.getInstances().stringRequest(getMethod(),url,stringListener);
        }

    }
}
