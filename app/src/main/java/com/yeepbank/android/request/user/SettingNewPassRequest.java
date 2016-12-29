package com.yeepbank.android.request.user;

import android.content.Context;
import com.yeepbank.android.Cst;
import com.yeepbank.android.http.BaseRequest;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.utils.DesUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by WW on 2015/11/20.
 */
public class SettingNewPassRequest extends BaseRequest {

    private String mobile,smsCaptchaCode,newPasswd,repeatNewPasswd;

    public SettingNewPassRequest(Context context, StringListener stringListener,
                                 String mobile,String smsCaptchaCode,String newPasswd,String repeatNewPasswd) {
        super(context, stringListener);
        this.mobile = mobile;
        this.smsCaptchaCode = smsCaptchaCode;
        this.newPasswd = newPasswd;
        this.repeatNewPasswd = repeatNewPasswd;
    }

    @Override
    protected String getUrl() {
        return Cst.URL.FORGET_PASSWORD_SETTING_NEW_PASS_URL;
    }

    @Override
    protected int getMethod() {
        return METHOD_POST;
    }

    @Override
    protected Map<String, String> getParams() {
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("mobile",mobile);
        params.put("smsCaptchaCode",smsCaptchaCode);
        params.put("newPasswd", DesUtil.encrypt(newPasswd));
        params.put("repeatNewPasswd",DesUtil.encrypt(repeatNewPasswd));
        return params;
    }

    @Override
    public void stringRequest() {
        super.stringRequest();
    }
}
