package com.yeepbank.android.request.app_update;

import android.content.Context;
import com.yeepbank.android.Cst;
import com.yeepbank.android.http.BaseRequest;
import com.yeepbank.android.http.StringListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by WW on 2015/12/10.
 */
public class BugRequest extends BaseRequest {

    private String appVersion,clientSysVersion,debugDesc;

    public BugRequest(Context context, StringListener stringListener,
                      String appVersion,String clientSysVersion,String debugDesc) {
        super(context, stringListener);
        this.appVersion = appVersion;
        this.clientSysVersion = clientSysVersion;
        this.debugDesc = debugDesc;
    }

    @Override
    protected String getUrl() {
        return Cst.URL.BUG_PATH;
    }

    @Override
    protected int getMethod() {
        return METHOD_POST;
    }

    @Override
    protected Map<String, String> getParams() {
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("appVersion",appVersion);
        params.put("clientSys","Android");
        params.put("clientSysVersion",clientSysVersion);
        params.put("debugDesc",debugDesc);
        return params;
    }

    @Override
    public void stringRequest() {
        super.stringRequest();
    }
}
