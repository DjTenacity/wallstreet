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
public class QRCodeRequest extends BaseRequest {

    private String userId;

    public QRCodeRequest(Context context, StringListener stringListener,String userId) {
        super(context, stringListener);
        this.userId = userId;
    }

    @Override
    protected String getUrl() {
        return Cst.URL.QR_CODE_URL;
    }

    @Override
    protected int getMethod() {
        return METHOD_GET;
    }

    @Override
    protected Map<String, String> getParams() {
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("userId",userId);
        params.put("securetKey", Cst.currentUser.appSecuretKey);

        return params;
    }

    public void stringRequest(){
        super.stringRequest();
    }
}
