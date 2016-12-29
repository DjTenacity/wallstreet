package com.yeepbank.android.request.user;

import android.content.Context;
import android.util.Log;
import com.yeepbank.android.Cst;
import com.yeepbank.android.http.BaseRequest;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.utils.DesUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by WW on 2015/9/7.
 */
public class RegisterRequest extends BaseRequest {

    private String mobile,password,smsCaptchaCode,recommenderCode,appDeviceId;

    public RegisterRequest(Context context, StringListener stringListener, String phone, String pass, String examCode, String recommendCode,String appDeviceId) {
        super(context, stringListener);
        this.mobile = phone;
        this.password = pass;
        this.smsCaptchaCode = examCode;
        this.recommenderCode = recommendCode;
        this.appDeviceId = appDeviceId;
    }


    @Override
    protected String getUrl() {
        return Cst.URL.REGISTER_URL;
    }

    @Override
    protected int getMethod() {
        return METHOD_POST;
    }

    @Override
    protected Map<String, String> getParams(){
        Map<String,String> params = new HashMap<String,String>();
        params.put("mobile",String.valueOf(DesUtil.encrypt(mobile)));
        params.put("password",String.valueOf(DesUtil.encrypt(password)));
        params.put("recommenderCode",recommenderCode);
        params.put("smsCaptchaCode",smsCaptchaCode);
        params.put("operateOrigin","Android".toUpperCase());
        params.put("appDeviceId",appDeviceId);
        Log.e("LOGIN","LOGIN_PARAMS:");
        return params;
    }

    @Override
    public void stringRequest() {
        super.stringRequest();
    }
}
