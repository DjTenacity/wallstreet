package com.yeepbank.android.request.business;

import android.content.Context;
import com.yeepbank.android.Cst;
import com.yeepbank.android.http.BaseRequest;
import com.yeepbank.android.http.StringListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by WW on 2015/11/28.
 */
public class TransformRequest extends BaseRequest {

    private String userId,transferId;
    public TransformRequest(Context context, StringListener stringListener,String userId,String transferId) {
        super(context, stringListener);
        this.userId = userId;
        this.transferId = transferId;
    }

    @Override
    protected String getUrl() {
        return Cst.URL.TRANSFER_DO_BIDDING_URL;
    }

    @Override
    protected int getMethod() {
        return METHOD_POST;
    }

    @Override
    protected Map<String, String> getParams() {
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("userId",userId);
        params.put("transferId", transferId);
        params.put("securetKey",Cst.currentUser.appSecuretKey);
        return params;
    }

    @Override
    public void stringRequest() {
        super.stringRequest();
    }
}
