package com.yeepbank.android.request.user;

import android.content.Context;
import com.yeepbank.android.Cst;
import com.yeepbank.android.http.BaseRequest;
import com.yeepbank.android.http.StringListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiaogang.dong on 2015/11/21.
 */
public class InvestmentRecordRequest extends BaseRequest {
    private String userId;
    private String status;
    private int  page;
    private int pageSize;
    private String appSecuretKey;
    public InvestmentRecordRequest(Context context,StringListener stringListener,String userId,String status,int page,int pageSize) {
        super(context,stringListener);
        this.userId=userId;
        this.status=status;
        this.page=page;
        this.pageSize=pageSize;
        appSecuretKey=Cst.currentUser.appSecuretKey;
    }

    @Override
    protected String getUrl() {
        return Cst.URL.INVESTOR_INVESTMENTRECORD_URL;
    }

    @Override
    protected int getMethod() {
        return METHOD_GET;
    }

    @Override
    protected Map<String, String> getParams() {
        HashMap<String,String> params=new HashMap<String,String>();
        params.put("userId",userId);
        params.put("status",status);
        params.put("page",String.valueOf(page+1));
        params.put("pageSize",String.valueOf(pageSize));
        params.put("securetKey",appSecuretKey);
        return params;
    }
    public void stringRequest(){
        super.stringRequest();
    }
}
