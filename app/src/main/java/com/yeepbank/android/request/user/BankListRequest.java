package com.yeepbank.android.request.user;

import android.content.Context;
import com.yeepbank.android.Cst;
import com.yeepbank.android.http.BaseRequest;
import com.yeepbank.android.http.StringListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by WW on 2015/11/17.
 */
public class BankListRequest extends BaseRequest {

    private String userId;
    private String useType;
    private String appSecuretKey;
    public BankListRequest(Context context, StringListener stringListener,String userId,String useType) {
        super(context, stringListener);
        this.userId = userId;
        this.useType = useType;
        appSecuretKey=Cst.currentUser.appSecuretKey;
    }

    @Override
    protected String getUrl() {
        return Cst.URL.BANK_LIST_URL;
    }

    @Override
    protected int getMethod() {
        return METHOD_GET;
    }

    @Override
    protected Map<String, String> getParams() {
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("userId",userId);
        params.put("useType",useType);
        params.put("securetKey",appSecuretKey);
        return params;
    }

    public void stringRequest(){
        super.stringRequest();
    }
}
