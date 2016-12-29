package com.yeepbank.android.request.user;

import android.content.Context;
import com.yeepbank.android.Cst;
import com.yeepbank.android.http.BaseRequest;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.utils.DesUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by WW on 2016/4/20.
 */
public class OrderParamsRequest extends BaseRequest {

    private String userId,tradeMoney;

    public OrderParamsRequest(Context context, StringListener stringListener,String userId,String tradeMoney) {
        super(context, stringListener);
        this.userId = userId;
        this.tradeMoney = tradeMoney;
    }

    @Override
    protected String getUrl() {
        return Cst.URL.GET_BUSINESS_PARAMS_URL;
    }

    @Override
    protected int getMethod() {
        return METHOD_POST;
    }

    @Override
    protected Map<String, String> getParams() {
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("userId",userId);
        params.put("tradeMoney",tradeMoney);
        params.put("securetKey",Cst.currentUser.appSecuretKey);
        return params;
    }

    @Override
    public void stringRequest() {
        super.stringRequest();
    }
}
