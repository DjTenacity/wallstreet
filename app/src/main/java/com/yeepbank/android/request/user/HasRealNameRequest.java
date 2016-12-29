package com.yeepbank.android.request.user;

import android.content.Context;
import com.yeepbank.android.Cst;
import com.yeepbank.android.http.BaseRequest;
import com.yeepbank.android.http.StringListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by WW on 2015/11/24.
 * 检查是否实名认证
 */
public class HasRealNameRequest extends BaseRequest {

    private String userId;
    public HasRealNameRequest(Context context, StringListener stringListener,String userId) {
        super(context, stringListener);
        this.userId = userId;
    }

    @Override
    protected String getUrl() {
        return Cst.URL.HAS_REAL_NAME_URL;
    }

    @Override
    protected int getMethod() {
        return METHOD_GET;
    }

    @Override
    protected Map<String, String> getParams() {
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("userId",userId);
        params.put("securetKey",Cst.currentUser.appSecuretKey);
        return params;
    }

    @Override
    public void stringRequest() {
        super.stringRequest();
    }
}
