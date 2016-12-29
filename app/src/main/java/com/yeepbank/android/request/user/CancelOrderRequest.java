package com.yeepbank.android.request.user;

import android.content.Context;
import com.yeepbank.android.Cst;
import com.yeepbank.android.http.BaseRequest;
import com.yeepbank.android.http.StringListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by WW on 2016/4/20.
 */
public class CancelOrderRequest extends BaseRequest {

    private String userId;
    private String tradeMoney;
    private String depositId;
    private String orderNo;
    private String location;

    public CancelOrderRequest(Context context, StringListener stringListener,String userId,String tradeMoney,String depositId,String orderNo,String location) {
        super(context, stringListener);
        this.userId = userId;
        this.tradeMoney = tradeMoney;
        this.depositId = depositId;
        this.orderNo = orderNo;
        this.location = location;
    }

    @Override
    protected String getUrl() {
        return Cst.URL.CANCEL_ORDER_URL;
    }

    @Override
    protected int getMethod() {
        return METHOD_POST;
    }

    @Override
    protected Map<String, String> getParams() {
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("userId",userId);
        params.put("depositId",depositId);
        params.put("tradeMoney",tradeMoney);
        params.put("orderNo",orderNo);
        params.put("location",location);
        params.put("securetKey",Cst.currentUser.appSecuretKey);
        return params;
    }

    @Override
    public void stringRequest() {
        super.stringRequest();
    }
}
