package com.yeepbank.android.request.user;

import android.content.Context;
import com.yeepbank.android.Cst;
import com.yeepbank.android.http.BaseRequest;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.utils.DesUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiaogang.dong on 2015/11/21.
 */
public class UpdateLoginPasswordRequest extends BaseRequest {
    private String userId;
    private String oldPasswd;
    private String newPasswd;
    private String repeatNewPasswd;
    private String appSecuretKey;
    public UpdateLoginPasswordRequest(Context context, StringListener stringListener,String userId,String oldPasswd,String newPasswd,String repeatNewPasswd) {
        super(context, stringListener);
        this.userId=userId;
        this.oldPasswd=oldPasswd;
        this.newPasswd=newPasswd;
        this.repeatNewPasswd=repeatNewPasswd;
        appSecuretKey=Cst.currentUser.appSecuretKey;
    }



    @Override
    protected String getUrl() {
        return Cst.URL.INVESTOR_UPDATELOGINPASSWD_URL;
    }

    @Override
    protected int getMethod() {
        return METHOD_POST;
    }

    @Override
    protected Map<String, String> getParams() {
        HashMap<String,String> params=new HashMap<String,String>();
        params.put("userId", userId);
        params.put("oldPasswd",String.valueOf(DesUtil.encrypt(oldPasswd)));
        params.put("newPasswd",String.valueOf(DesUtil.encrypt(newPasswd)));
        params.put("repeatNewPasswd",String.valueOf(DesUtil.encrypt(repeatNewPasswd)));
        params.put("securetKey",appSecuretKey);
        return params;
    }
    public void stringRequest(){
        super.stringRequest();
    }
}
