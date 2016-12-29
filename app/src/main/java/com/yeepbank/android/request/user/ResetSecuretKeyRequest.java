package com.yeepbank.android.request.user;

import android.content.Context;
import com.yeepbank.android.Cst;
import com.yeepbank.android.http.BaseRequest;
import com.yeepbank.android.http.StringListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by WW on 2016/3/23.
 */
public class ResetSecuretKeyRequest extends BaseRequest {

    private String userId;

    public ResetSecuretKeyRequest(Context context, StringListener stringListener,String userId) {
        super(context, stringListener);
        this.userId = userId;
    }

    @Override
    protected String getUrl() {
        return Cst.URL.RESET_SECURE_KEY_PATH;
    }

    @Override
    protected int getMethod() {
        return METHOD_POST;
    }

    @Override
    protected Map<String, String> getParams() {
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("userId",userId);
        params.put("securetKey", Cst.currentUser.appSecuretKey);
        return params;
    }

    @Override
    public void stringRequest() {
        super.stringRequest();
    }
}
