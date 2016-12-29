package com.yeepbank.android.http;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import com.android.volley.*;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yeepbank.android.Cst;
import com.yeepbank.android.GuideActivity;
import com.yeepbank.android.LaunchActivity;
import com.yeepbank.android.activity.business.HomeActivity;
import com.yeepbank.android.activity.business.InvestSureActivity;
import com.yeepbank.android.activity.business.ProjectDetailActivity;
import com.yeepbank.android.activity.setting.ForgetTradePasswordActivity;
import com.yeepbank.android.base.BaseActivity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by WW on 2015/8/21.
 */
public class HttpRequestQueueManager {

    private static HttpRequestQueueManager manager;
    private RequestQueue requestQueue;
    private Context context;

    private HttpRequestQueueManager(){

    }

    public static HttpRequestQueueManager getInstances(){
        if(manager == null){
            synchronized (HttpRequestQueueManager.class){
                manager = new HttpRequestQueueManager();
            }

        }
        return manager;
    }

    public void init(Context context){
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);

    }
    /*
    * GET ????
    * */
    public void stringRequest(int method,String url, final StringListener listener) {

       //Log.e("URL","URL:"+url);
        StringRequest jsonObjectRequest = new StringRequest(
                method, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                       // Log.e("RESULT","RESULT:+\n"+"{"+result+"}");
                        try {
                            JSONObject json = new JSONObject(result);
                            String state = json.getString("state");
                            json = new JSONObject(state);
                            int code = json.getInt("code");
                            if(code == -102 && !(context instanceof LaunchActivity) && !(context instanceof GuideActivity)){//????Ч
                               Intent intent = new Intent(Cst.CMD.LOGIN_OUT_TIME_ACTION);
                               context.sendBroadcast(intent);

                            }else {
                                if(listener != null){
                                    listener.ResponseListener(result);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        if(listener != null){
                            listener.ErrorListener(arg0);
                        }
                    }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,//?????????????????????????????籾????500000
                0,//???????????
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjectRequest);
    }



    /*
    * GET ????  ???ó?????
    * */
    public void stringRequest(int method,String url, final StringListener listener,int timeOut) {
//        Log.e("URL","URL:"+url);
        StringRequest jsonObjectRequest = new StringRequest(
                method, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        //???????JSON????
                        try {
                            JSONObject json = new JSONObject(result);
                            String state = json.getString("state");
                            json = new JSONObject(state);
                            int code = json.getInt("code");
                            if(code == -102 && !(context instanceof LaunchActivity) && !(context instanceof GuideActivity)){//????Ч
                                Intent intent = new Intent(Cst.CMD.LOGIN_OUT_TIME_ACTION);
                                context.sendBroadcast(intent);
                            }else {
                                if(listener != null){
                                    listener.ResponseListener(result);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError arg0) {
                if(listener != null){
                    listener.ErrorListener(arg0);
                }
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(timeOut,//?????????????????????????????籾????500000
                0,//???????????
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjectRequest);
    }

    /*
    * POST ??
    * */
    public void stringRequest(int method, final Map<String,String> params,String url, final StringListener listener){

        StringRequest stringRequest = new StringRequest(method, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
               // Log.e("RESULT","RESULT:+\n"+"{"+s+"}");
                try {
                    JSONObject json = new JSONObject(s);
                    String state = json.getString("state");
                    json = new JSONObject(state);
                    int code = json.getInt("code");
                    if(code == -102 && !(context instanceof LaunchActivity) && !(context instanceof GuideActivity)){//????Ч
                        Intent intent = new Intent(Cst.CMD.LOGIN_OUT_TIME_ACTION);
                        context.sendBroadcast(intent);
                    }else {
                        listener.ResponseListener(s);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                listener.ErrorListener(volleyError);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                if(params != null){
                    return params;
                }
                return super.getParams();
            }
            @Override
            protected String getParamsEncoding() {
                return HTTP.UTF_8;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String str = null;
                try {
                    str = new String(response.data, HTTP.UTF_8);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return Response.success(str, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,//?????????????????????????????籾????500000
                0,//???????????
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.addMarker("request");
        requestQueue.add(stringRequest);
    }

    /*
    * POST  ???ó?????
    * */

    public void stringRequest(int method, final Map<String,String> params,String url, final StringListener listener,int timeOut){
//        Log.e("URL","URL:"+url);
        StringRequest stringRequest = new StringRequest(method, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject json = new JSONObject(s);
                    String state = json.getString("state");
                    json = new JSONObject(state);
                    int code = json.getInt("code");
                    if(code == -102 && !(context instanceof LaunchActivity) && !(context instanceof GuideActivity)){//????Ч
                        Intent intent = new Intent(Cst.CMD.LOGIN_OUT_TIME_ACTION);
                        context.sendBroadcast(intent);
                    }else {
                        listener.ResponseListener(s);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                listener.ErrorListener(volleyError);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                if(params != null){
                    return params;
                }
                return super.getParams();
            }
            @Override
            protected String getParamsEncoding() {
                return HTTP.UTF_8;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String str = null;
                try {
                    str = new String(response.data, HTTP.UTF_8);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return Response.success(str, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(timeOut,//?????????????????????????????籾????500000
                0,//???????????
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.addMarker("request");
        requestQueue.add(stringRequest);
    }



    public void release() {
        manager = null;
        if (requestQueue != null) {
            requestQueue.cancelAll("request");
        }
    }
}
