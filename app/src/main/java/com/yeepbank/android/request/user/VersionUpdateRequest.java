package com.yeepbank.android.request.user;

import android.content.Context;
import com.yeepbank.android.Cst;
import com.yeepbank.android.http.BaseRequest;
import com.yeepbank.android.http.StringListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiaogang.dong on 2015/12/1.
 */
public class VersionUpdateRequest extends BaseRequest {
    private String version;
    private String client;
    public VersionUpdateRequest(Context context,StringListener stringListener,String version,String client){
        super(context,stringListener);
        this.version=version;
        this.client=client;

    }
    @Override
    protected String getUrl() {
        return Cst.URL.VERSION_UPDATE_URL;
    }

    @Override
    protected int getMethod() {
        return METHOD_GET;
    }

    @Override
    protected Map<String, String> getParams() {
        HashMap<String,String> params=new HashMap<String,String>();
        params.put("version",version);
        params.put("client",client);
        return params;
    }
    public void stringRequest(){
        super.stringRequest();
    }
}
