package com.yeepbank.android.request.user;

import android.content.Context;
import com.yeepbank.android.Cst;
import com.yeepbank.android.http.BaseRequest;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.utils.DesUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/11/25.
 */
public class UpdateTradePasswdRequest extends BaseRequest {
    private String userId;
    private String passwd;
    private String appSecuretKey;

    public UpdateTradePasswdRequest(Context context,StringListener stringListener,String userId, String passwd) {
        super(context,stringListener);
        this.userId = userId;
        this.passwd = passwd;
        appSecuretKey=Cst.currentUser.appSecuretKey;
    }

    @Override
    protected String getUrl() {
        return Cst.URL.INVESTOR_CHECKPASSWD_URL;
    }

    @Override
    protected int getMethod() {
        return METHOD_POST;
    }

    @Override
    protected Map<String, String> getParams() {
        HashMap<String,String> params=new HashMap<String,String>();
        params.put("userId",userId);
        params.put("passwd",String.valueOf(DesUtil.encrypt(passwd)));
        params.put("securetKey",appSecuretKey);
        return params;
    }
    public void stringRequest(){
        super.stringRequest();
    }
}
