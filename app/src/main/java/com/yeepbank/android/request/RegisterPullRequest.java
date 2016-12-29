package com.yeepbank.android.request;

import android.content.Context;
import com.yeepbank.android.Cst;
import com.yeepbank.android.http.BaseRequest;
import com.yeepbank.android.http.StringListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by WW on 2016/5/20.
 */
public class RegisterPullRequest extends BaseRequest{

    private String imei;
    private String appDeviceId;
    private String mac;
    private String osVersion;
    public RegisterPullRequest(Context context, StringListener stringListener,String imei,
                               String appDeviceId,String mac,String osVersion) {
        super(context, stringListener);
        this.imei = imei;
        this.appDeviceId = appDeviceId;
        this.mac = mac;
        this.osVersion = osVersion;
    }

    @Override
    protected String getUrl() {
        return Cst.URL.PULL_REGISTER_URL;
    }

    @Override
    protected int getMethod() {
        return METHOD_POST;
    }

    @Override
    protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("imei",imei);
        params.put("appDeviceId",appDeviceId);
        params.put("MAC",mac);
        params.put("osVersion",osVersion);
        //params.put("userId",Cst.currentUser == null ? "0" : Cst.currentUser.investorId);
        if(Cst.currentUser==null){
            params.put("userId","0");
        }else{
            params.put("userId",Cst.currentUser.investorId);
            params.put("securetKey",Cst.currentUser.appSecuretKey);
        }
        return params;
    }

    @Override
    public void stringRequest() {
        super.stringRequest();
    }
}
