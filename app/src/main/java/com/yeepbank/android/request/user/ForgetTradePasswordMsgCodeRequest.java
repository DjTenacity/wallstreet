package com.yeepbank.android.request.user;

import android.content.Context;
import com.yeepbank.android.Cst;
import com.yeepbank.android.http.BaseRequest;
import com.yeepbank.android.http.StringListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiaogang.dong on 2015/11/28.
 */
public class ForgetTradePasswordMsgCodeRequest extends BaseRequest {
    private String userId;
    private String mobile;
    private String appSecuretKey;

    public ForgetTradePasswordMsgCodeRequest(Context context,StringListener stringListener,String userId,String mobile){
        super(context,stringListener);
        this.userId=userId;
        this.mobile=mobile;
        appSecuretKey=Cst.currentUser.appSecuretKey;


    }
    @Override
    protected String getUrl() {
        return Cst.URL.UPDATE_USER_TRADEPASSWORD_GET_EXAM_CODE_URL;
    }

    @Override
    protected int getMethod() {
        return METHOD_POST;
    }

    @Override
    protected Map<String, String> getParams() {
        HashMap<String,String> params=new HashMap<String,String>();
        params.put("userId",userId);
        params.put("mobile",mobile);
        params.put("type","FIND_TXN_PWD");
        params.put("securetKey",appSecuretKey);
        return params;
    }
    public void stringRequest(){
        super.stringRequest();
    }
}
