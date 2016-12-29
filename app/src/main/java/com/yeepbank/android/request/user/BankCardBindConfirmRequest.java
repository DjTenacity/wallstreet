package com.yeepbank.android.request.user;

import android.content.Context;
import com.yeepbank.android.Cst;
import com.yeepbank.android.http.BaseRequest;
import com.yeepbank.android.http.StringListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by WW on 2015/11/20.
 */
public class BankCardBindConfirmRequest extends BaseRequest {

    private String userId,requestId,validateCode,bankCardNo;
    private String appSecuretKey;

    public BankCardBindConfirmRequest(Context context, StringListener stringListener,String userId,String requestId,String validateCode,String bankCardNo) {
        super(context, stringListener);
        this.userId = userId;
        this.requestId = requestId;
        this.validateCode = validateCode;
        this.bankCardNo = bankCardNo;
        appSecuretKey=Cst.currentUser.appSecuretKey;
    }

    @Override
    protected String getUrl() {
        return Cst.URL.BANK_CARD_BIND_OK_URL;
    }

    @Override
    protected int getMethod() {
        return METHOD_POST;
    }

    @Override
    protected Map<String, String> getParams() {
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("userId",userId);
        params.put("requestId",requestId);
        params.put("validateCode",validateCode);//��֤��
        params.put("bankCardNo",bankCardNo);
        params.put("securetKey",appSecuretKey);
        return params;
    }

    @Override
    public void stringRequest() {
        super.stringRequest();
    }
}
