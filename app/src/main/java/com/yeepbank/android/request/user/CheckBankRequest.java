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
public class CheckBankRequest extends BaseRequest {

    private String cardNo;

    public CheckBankRequest(Context context, StringListener stringListener,String cardNo) {
        super(context, stringListener);
        this.cardNo = cardNo;
    }

    @Override
    protected String getUrl() {
        return Cst.URL.FIND_BANK_NAME_BY_NO;
    }

    @Override
    protected int getMethod() {
        return METHOD_POST;
    }

    @Override
    protected Map<String, String> getParams() {
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("cardNo",cardNo);
        return params;
    }

    public void stringRequest(){
        super.stringRequest();
    }
}
