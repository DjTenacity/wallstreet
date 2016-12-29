package com.yeepbank.android.request;

import android.content.Context;
import com.yeepbank.android.Cst;
import com.yeepbank.android.http.BaseRequest;
import com.yeepbank.android.http.StringListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by WW on 2016/6/15.
 */
public class WebSocketCallBackRequest extends BaseRequest {
    private int msType;
    private String appDeviceId;

    public WebSocketCallBackRequest(Context context, StringListener stringListener,int msType,String appDeviceId) {
        super(context, stringListener);
        this.msType = msType;
        this.appDeviceId = appDeviceId;
    }

    @Override
    protected String getUrl() {
        return Cst.URL.WEB_SOCKET_CALLBACK;
    }

    @Override
    protected int getMethod() {
        return METHOD_POST;
    }

    @Override
    protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("msType",String.valueOf(msType));
//        if(Cst.currentUser==null){
//            params.put("userId","0");
//        }else{
//            params.put("userId",Cst.currentUser.investorId);
//            params.put("appSecuretKey",Cst.currentUser.appSecuretKey);
//        }
        params.put("appDeviceId",String.valueOf(appDeviceId));
        return params;
    }

    @Override
    public void stringRequest() {
        super.stringRequest();
    }
}
