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
public class WithdrawalsRequest extends BaseRequest {

    private String withdrawAmount,bankCardId,bankCardNo,txnpwd,userId;
    public WithdrawalsRequest(Context context, StringListener stringListener,String withdrawAmount,String bankCardId,String bankCardNo,String txnpwd,String userId) {
        super(context, stringListener);
        this.withdrawAmount = withdrawAmount;
        this.bankCardId = bankCardId;
        this.bankCardNo = bankCardNo;
        this.txnpwd = txnpwd;
        this.userId = userId;
    }

    @Override
    protected String getUrl() {
        return Cst.URL.WITHDRAWALS_URL;
    }

    @Override
    protected int getMethod() {
        return METHOD_POST;
    }

    @Override
    protected Map<String, String> getParams() {
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("withdrawAmount",withdrawAmount);
        params.put("bankCardId",bankCardId);
        params.put("bankCardNo",bankCardNo);
        params.put("txnpwd", DesUtil.encrypt(txnpwd));
        params.put("userId",userId);
        params.put("securetKey",Cst.currentUser.appSecuretKey);
        return params;
    }

    public void stringRequest(){
        super.stringRequest();
    }
}
