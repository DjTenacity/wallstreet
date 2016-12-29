package com.yeepbank.android.request.user;

import android.content.Context;
import com.yeepbank.android.Cst;
import com.yeepbank.android.http.BaseRequest;
import com.yeepbank.android.http.StringListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by WW on 2015/11/19.
 */
public class RealNameRequest extends BaseRequest {

    private String userId,userName,idNo;
    private String appSecuretKey;

    public RealNameRequest(Context context, StringListener stringListener,String userId,String userName,String idNo) {
        super(context, stringListener);
        this.userId = userId;
        this.userName = userName;
        this.idNo = idNo;
        appSecuretKey=Cst.currentUser.appSecuretKey;
    }

    @Override
    protected String getUrl() {
        return Cst.URL.REAL_NAME_URL;
    }

    @Override
    protected int getMethod() {
        return METHOD_POST;
    }

    @Override
    protected Map<String, String> getParams() {
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("userId",userId);
        params.put("userName",userName);
        params.put("idNo",idNo);
        params.put("securetKey",appSecuretKey);
        return params;
    }

    public void stringRequest(){
        super.stringRequest();
    }
}
