package com.yeepbank.android.request.user;

import android.content.Context;
import com.yeepbank.android.Cst;
import com.yeepbank.android.http.BaseRequest;
import com.yeepbank.android.http.StringListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiaogang.dong on 2016/3/25.
 * °²È«ÍË³ö
 */
public class LogOutRequest extends BaseRequest{
    private String userId;

    public LogOutRequest(Context context, StringListener stringListener, String userId) {
        super(context, stringListener);
        this.userId = userId;
    }

    @Override
    protected String getUrl() {
        return Cst.URL.SAFETY_LOGOUT;
    }

    @Override
    protected int getMethod() {
        return METHOD_POST;
    }

    @Override
    protected Map<String, String> getParams() {
        HashMap<String,String> params=new HashMap<>();
        params.put("userId",userId);
        params.put("securetKey",Cst.currentUser.appSecuretKey);
        return params;
    }
    public void stringRequest(){
        super.stringRequest();
    }
}
