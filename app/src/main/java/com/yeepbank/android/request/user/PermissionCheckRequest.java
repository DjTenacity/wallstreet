package com.yeepbank.android.request.user;

import android.content.Context;
import com.yeepbank.android.Cst;
import com.yeepbank.android.http.BaseRequest;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.utils.DesUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by WW on 2016/4/20.
 */
public class PermissionCheckRequest extends BaseRequest{

    private String userId;
    private String tradePass;

    public PermissionCheckRequest(Context context, StringListener stringListener,String userId,String tradePass) {
        super(context, stringListener);
        this.userId = userId;
        this.tradePass = tradePass;
    }

    @Override
    protected String getUrl() {
        return Cst.URL.INVESTOR_PERMISSION_CHECK_URL;
    }

    @Override
    protected int getMethod() {
        return METHOD_GET;
    }

    @Override
    protected Map<String, String> getParams() {
        HashMap<String,String> params = new HashMap<String, String>();
        params.put("userId",userId);
        params.put("txnpwd", DesUtil.encrypt(tradePass));
        params.put("securetKey",Cst.currentUser.appSecuretKey);
        return params;
    }

    @Override
    public void stringRequest() {
        super.stringRequest();
    }
}
