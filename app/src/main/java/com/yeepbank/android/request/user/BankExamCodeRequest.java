package com.yeepbank.android.request.user;

import android.content.Context;
import com.yeepbank.android.Cst;
import com.yeepbank.android.http.BaseRequest;
import com.yeepbank.android.http.StringListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by WW on 2015/11/20.
 * 获取银行验证码
 */
public class BankExamCodeRequest extends BaseRequest {

    private String cardNo,phone,userId;
    private String appSecuretKey;

    public BankExamCodeRequest(Context context, StringListener stringListener,String cardNo,String phone,String userId) {
        super(context, stringListener);
        this.userId = userId;
        this.phone = phone;
        this.cardNo = cardNo;
        appSecuretKey=Cst.currentUser.appSecuretKey;
    }

    @Override
    protected String getUrl() {
        return Cst.URL.SEND_BANK_CARD_EXAM_CODE_URL;
    }

    @Override
    protected int getMethod() {
        return METHOD_POST;
    }

    @Override
    protected Map<String, String> getParams() {

        HashMap<String,String> params = new HashMap<String,String>();
        params.put("cardNo",cardNo);
        params.put("phone",phone);
        params.put("userId",userId);
        params.put("securetKey",appSecuretKey);
        return params;
    }

    public void stringRequest(){
        super.stringRequest();
    }
}
