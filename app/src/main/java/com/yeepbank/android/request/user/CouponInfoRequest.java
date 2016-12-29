package com.yeepbank.android.request.user;

import android.content.Context;
import com.yeepbank.android.http.BaseRequest;
import com.yeepbank.android.http.StringListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiaogang.dong on 2015/11/11.
 */
public class CouponInfoRequest extends BaseRequest {
    private String userId;
    private String status;
    public CouponInfoRequest(Context context,StringListener stringListener,String userId,String status){
        super(context,stringListener);
        this.userId=userId;
        this.status=status;
    }
    @Override
    protected String getUrl() {
       return null;

    }

    @Override
    protected int getMethod() {
        return METHOD_GET;
    }

    @Override
    protected Map<String,String> getParams() {
        HashMap<String,String> params=new HashMap<String,String>();
        params.put("userId",userId);
        params.put("status",status);
        return params;
    }
    public void stringRequest(){
        super.stringRequest();
    }
}
