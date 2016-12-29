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
public class ExchangeRequest extends BaseRequest {

    private String userId,couponCode;
    private String appSecuretKey;

    public ExchangeRequest(Context context, StringListener stringListener,String userId,String codeText) {
        super(context, stringListener);
        this.userId = userId;
        this.couponCode = codeText;
        appSecuretKey=Cst.currentUser.appSecuretKey;
    }

    @Override
    protected String getUrl() {
        return Cst.URL.EXCHANGE_URL;
    }

    @Override
    protected int getMethod() {
        return METHOD_POST;
    }

    @Override
    protected Map<String, String> getParams() {
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("userId",userId);
        params.put("couponCode",couponCode);
        params.put("securetKey",appSecuretKey);
        return params;
    }

    @Override
    public void stringRequest() {
        super.stringRequest();
    }
}
