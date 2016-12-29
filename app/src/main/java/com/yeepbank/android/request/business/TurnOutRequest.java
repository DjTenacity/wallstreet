package com.yeepbank.android.request.business;

import android.content.Context;
import com.yeepbank.android.Cst;
import com.yeepbank.android.http.BaseRequest;
import com.yeepbank.android.http.StringListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by WW on 2015/11/17.
 */
public class TurnOutRequest extends BaseRequest {

    private String userId,ruleType,exitAmount;

    public TurnOutRequest(Context context, StringListener stringListener,String userId,String ruleType,String exitAmount) {
        super(context, stringListener);
        this.userId = userId;
        this.ruleType = ruleType;
        this.exitAmount = exitAmount;
    }

    @Override
    protected String getUrl() {
        return Cst.URL.EDM_OVERVIEW_MSG_TURN_OUT_URL;
    }

    @Override
    protected int getMethod() {
        return METHOD_POST;
    }

    @Override
    protected Map<String, String> getParams() {
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("userId",userId);
        params.put("ruleType",ruleType);
        params.put("exitAmount",exitAmount);
        params.put("securetKey",Cst.currentUser.appSecuretKey);
        return params;
    }

    public void stringRequest(){
        super.stringRequest();
    }
}
