package com.yeepbank.android.request.user;

import android.content.Context;
import com.yeepbank.android.Cst;
import com.yeepbank.android.http.BaseRequest;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.utils.DesUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by WW on 2015/11/18.
 */
public class RechargeRequest extends BaseRequest {

    private String amount,userId,bankCardNo,txnpwd;

    public RechargeRequest(Context context, StringListener stringListener,String amount,String userId,String bankCardNo,String txnpwd) {
        super(context, stringListener);
        this.amount = amount;
        this.userId = userId;
        this.bankCardNo = bankCardNo;
        this.txnpwd = txnpwd;
    }

    @Override
    protected String getUrl() {
        return Cst.URL.MORE_VERSION_RECHARGE_URL;
    }

    @Override
    protected int getMethod() {
        return METHOD_POST;
    }

    @Override
    protected Map<String, String> getParams() {
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("amount",amount);
        params.put("userId",userId);
        params.put("bankCardNo",bankCardNo);
        params.put("txnpwd", DesUtil.encrypt(txnpwd));
        params.put("securetKey",Cst.currentUser.appSecuretKey);
        return params;
    }

    public void stringRequest(){
        super.stringRequest();
    }
}
