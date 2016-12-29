package com.yeepbank.android.request.user;

import android.content.Context;
import com.yeepbank.android.Cst;
import com.yeepbank.android.http.BaseRequest;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.utils.DesUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiaogang.dong on 2015/12/8.
 */
public class CheckLoginPasswordAndSmsPasswdRequest extends BaseRequest{
    private String userId;
    private String smsCaptchaCode;
    private String loginPwd;
    private String appSecuretKey;
    public CheckLoginPasswordAndSmsPasswdRequest(Context context,StringListener stringListener,String userId,String smsCaptchaCode,String loginPwd){
        super(context,stringListener);
        this.userId=userId;
        this.smsCaptchaCode=smsCaptchaCode;
        this.loginPwd=loginPwd;
        appSecuretKey=Cst.currentUser.appSecuretKey;

    }
    @Override
    protected String getUrl() {
        return Cst.URL.INVESTOR_CHECK_PASSWD_AND_SMSCODE_URL;
    }

    @Override
    protected int getMethod() {
        return METHOD_POST;
    }

    @Override
    protected Map<String, String> getParams() {
        HashMap<String,String> params=new HashMap<String,String>();
        params.put("userId",userId);
        params.put("smsCaptchaCode",smsCaptchaCode);
        params.put("loginPwd",String.valueOf(DesUtil.encrypt(loginPwd)));
        params.put("securetKey",appSecuretKey);
        return params;
    }
    public void stringRequest(){
        super.stringRequest();
    }
}
