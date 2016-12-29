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
public class BattleListRequest extends BaseRequest {
    private int page;
    private int pageSize;

    public BattleListRequest(Context context, StringListener stringListener,int page,int pageSize) {
        super(context, stringListener);
        this.page=page;
        this.pageSize=pageSize;
    }

    @Override
    protected String getUrl() {
        return Cst.URL.BATTLE_LIST_URL;
    }

    @Override
    protected int getMethod() {
        return METHOD_GET;
    }

    @Override
    protected Map<String, String> getParams() {
        HashMap<String,String> params=new HashMap<String,String>();
        if(Cst.currentUser!=null){
            params.put("userId",Cst.currentUser.investorId);
        }
        params.put("page",String.valueOf(page));
        params.put("pageSize",String.valueOf(pageSize));
        if (Cst.currentUser!=null) {
            params.put("securetKey", Cst.currentUser.appSecuretKey);
        }
        return params;
    }

    @Override
    public void stringRequest() {
        super.stringRequest();
    }
}
