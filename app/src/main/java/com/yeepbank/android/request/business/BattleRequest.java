package com.yeepbank.android.request.business;

import android.content.Context;
import com.yeepbank.android.Cst;

import com.yeepbank.android.http.BaseRequest;
import com.yeepbank.android.http.StringListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiaogang.dong on 2016/9/8.
 */
public class BattleRequest extends BaseRequest {
    private String couponBatchNo;
    private String userId;

    public BattleRequest(Context context, StringListener stringListener, String couponBatchNo, String userId) {
        super(context, stringListener);
        this.couponBatchNo = couponBatchNo;
        this.userId = userId;
    }

    @Override
    protected String getUrl() {
        return Cst.URL.BATTLE_URL;
    }

    @Override
    protected int getMethod() {
        return METHOD_POST;
    }

    @Override
    protected Map<String, String> getParams() {
        HashMap<String,String> params=new HashMap<String,String>();
        params.put("couponBatchNo",couponBatchNo);
        params.put("userId",userId);
        params.put("securetKey", Cst.currentUser.appSecuretKey);
        return params;
    }

    @Override
    public void stringRequest() {
        super.stringRequest();
    }
}
