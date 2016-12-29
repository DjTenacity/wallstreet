package com.yeepbank.android.request.business;

import android.content.Context;
import com.yeepbank.android.Cst;
import com.yeepbank.android.http.BaseRequest;
import com.yeepbank.android.http.StringListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by WW on 2015/11/11.
 */
public class DoBiddingRequest extends BaseRequest {
    private String projectId;
    private String biddingAmount;
    private String couponId;
    private String userId;

    public DoBiddingRequest(Context context, StringListener stringListener,String projectId,String biddingAmount,String couponId,String userId) {
        super(context, stringListener);
        this.projectId = projectId;
        this.biddingAmount = biddingAmount;
        this.userId = userId;
        this.couponId = couponId;
    }

    @Override
    protected String getUrl() {
        return Cst.URL.DO_BIDDING_URL;
    }

    @Override
    protected int getMethod() {
        return METHOD_POST;
    }

    @Override
    protected Map<String, String> getParams() {
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("projectId",projectId);
        params.put("biddingAmount", biddingAmount);
        params.put("couponId", String.valueOf(couponId));
        params.put("investorId",userId);
        params.put("securetKey",Cst.currentUser.appSecuretKey);
        return params;
    }

    public void stringRequest(){
        super.stringRequest();
    }
}
