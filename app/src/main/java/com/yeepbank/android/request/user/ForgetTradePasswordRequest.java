package com.yeepbank.android.request.user;

import android.content.Context;
import com.yeepbank.android.Cst;
import com.yeepbank.android.http.BaseRequest;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.utils.DesUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiaogang.dong on 2015/11/28.
 */
public class ForgetTradePasswordRequest extends BaseRequest {
    private String userId;
    private String smsCaptchaCode;
    private String loginPwd;
    private String idNo;
    private String newTxnPwd;
    private String newTxnPwdAgain;
    private String appSecuretKey;

    public ForgetTradePasswordRequest(Context context, StringListener stringListener,String userId,String smsCaptchaCode,String loginPwd,String idNo,String newTxnPwd,String newTxnPwdAgain) {
        super(context, stringListener);
        this.userId = userId;
        this.smsCaptchaCode=smsCaptchaCode;
        this.loginPwd=loginPwd;
        this.idNo=idNo;
        this.newTxnPwd=newTxnPwd;
        this.newTxnPwdAgain=newTxnPwdAgain;
        appSecuretKey=Cst.currentUser.appSecuretKey;
    }

    @Override
    protected String getUrl() {
        return Cst.URL.UPDATE_USER_TRADEPASSWORD_URL;
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
        params.put("idNo",idNo);
        params.put("newTxnPwd",String.valueOf(DesUtil.encrypt(newTxnPwd)));
        params.put("newTxnPwdAgain",String.valueOf(DesUtil.encrypt(newTxnPwdAgain)));
        params.put("securetKey",appSecuretKey);
        return params;
    }
    public void stringRequest(){
        super.stringRequest();
    }
}
