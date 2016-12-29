package com.yeepbank.android.request.user;

import android.content.Context;
import com.yeepbank.android.Cst;
import com.yeepbank.android.http.BaseRequest;
import com.yeepbank.android.http.StringListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiaogang.dong on 2016/3/24.
 */
public class WithDrawRecordRequest extends BaseRequest {
    private String userId;
    private int  page;
    private int pageSize;
    private String appSecuretKey;

    public WithDrawRecordRequest(Context context, StringListener stringListener, String userId, int page, int pageSize) {
        super(context, stringListener);
        this.userId = userId;
        this.page = page;
        this.pageSize = pageSize;
        appSecuretKey=Cst.currentUser.appSecuretKey;
    }

    @Override
    protected String getUrl() {
        return Cst.URL.INVESTOR_WITHDRAW_RECORD;
    }

    @Override
    protected int getMethod() {
        return METHOD_GET;
    }

    @Override
    protected Map<String, String> getParams() {
        HashMap<String,String> params=new HashMap<>();
        params.put("page",String.valueOf(page));
        params.put("pageSize",String.valueOf(pageSize));
        params.put("userId",userId);
        params.put("securetKey",appSecuretKey);
        return params;
    }
    public void stringRequest(){
        super.stringRequest();
    }
}
