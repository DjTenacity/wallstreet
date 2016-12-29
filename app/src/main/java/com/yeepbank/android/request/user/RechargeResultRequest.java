package com.yeepbank.android.request.user;

import android.content.Context;
import com.yeepbank.android.Cst;
import com.yeepbank.android.http.BaseRequest;
import com.yeepbank.android.http.StringListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by WW on 2016/3/29.
 */
public class RechargeResultRequest extends BaseRequest {

    private String depositId;//充值返回ID
    public RechargeResultRequest(Context context, StringListener stringListener,String depositId) {
        super(context, stringListener);
        this.depositId = depositId;
    }

    @Override
    protected String getUrl() {
        return Cst.URL.RECHARGE_RESULT_URL;
    }

    @Override
    protected int getMethod() {
        return METHOD_GET;
    }

    @Override
    protected Map<String, String> getParams() {
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("depositId",depositId);
        params.put("securetKey", Cst.currentUser.appSecuretKey);
        return params;
    }

    @Override
    public void stringRequest() {
        super.stringRequest(3000);
    }
}
