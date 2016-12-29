package com.yeepbank.android.request.user;

import android.content.Context;
import com.yeepbank.android.Cst;
import com.yeepbank.android.http.BaseRequest;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.utils.DesUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by WW on 2015/9/8.
 */
public class LoginRequest extends BaseRequest {
    private String userName;
    private String passWord;
    private String currentVersion;
    private String appDeviceId;

    public LoginRequest(Context context, StringListener stringListener,String userName,String passWord,String appDeviceId) {
        super(context, stringListener);
        this.userName = userName;
        this.passWord = passWord;
        currentVersion = Cst.getVersionCode(context);
        this.appDeviceId = appDeviceId;
    }


    @Override
    protected String getUrl() {
        return Cst.URL.LOGIN_URL;
    }

    @Override
    protected int getMethod() {
        return METHOD_POST;
    }

    @Override
    protected Map<String,String> getParams(){
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("username", String.valueOf(DesUtil.encrypt(userName)));
        params.put("password",String.valueOf(DesUtil.encrypt(passWord)));
        //params.put("currentVersion",currentVersion);
        params.put("appDeviceId",appDeviceId);
        return params;
    }

    @Override
    public void stringRequest(){
        super.stringRequest();
    }
}
