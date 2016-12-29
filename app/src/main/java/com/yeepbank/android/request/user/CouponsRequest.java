package com.yeepbank.android.request.user;

import android.content.Context;
import com.yeepbank.android.Cst;
import com.yeepbank.android.http.BaseRequest;
import com.yeepbank.android.http.StringListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by WW on 2015/11/21.
 */
public class CouponsRequest extends BaseRequest {

    String userId,status;
    private String appSecuretKey;

    public CouponsRequest(Context context, StringListener stringListener,String userId,String status) {
        super(context, stringListener);
        this.userId = userId;
        this.status = status;
        appSecuretKey=Cst.currentUser.appSecuretKey;

    }

    @Override
    protected String getUrl() {
        return Cst.URL.COUPONS_URL;
    }

    @Override
    protected int getMethod() {
        return METHOD_GET;
    }

    @Override
    protected Map<String, String> getParams() {
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("userId",userId);
        params.put("status",status);
        params.put("securetKey",appSecuretKey);
        return params;
    }

    @Override
    public void stringRequest() {
        super.stringRequest();
    }
}
