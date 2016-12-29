package com.yeepbank.android.request.user;

import android.content.Context;
import com.yeepbank.android.Cst;
import com.yeepbank.android.http.BaseRequest;
import com.yeepbank.android.http.StringListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by WW on 2016/5/30.
 */
public class PushSettingRequest extends BaseRequest {

    private String pushCode;/*��������(0-����;1-��ֵ/����;2-����Ŀ����;3-����Ͷ��ȯ;4-Ͷ����Ŀ���/����)*/
    private String isOpen;

    public PushSettingRequest(Context context, StringListener stringListener,String pushCode,String isOpen) {
        super(context, stringListener);
        this.pushCode = pushCode;
        this.isOpen = isOpen;
    }

    @Override
    protected String getUrl() {
        return Cst.URL.PUSH_SETTING_URL;
    }

    @Override
    protected int getMethod() {
        return METHOD_POST;
    }

    @Override
    protected Map<String, String> getParams() {
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("userId",Cst.currentUser.investorId);
        params.put("pushCode",pushCode);
        params.put("isOpen",isOpen);
        params.put("securetKey",Cst.currentUser.appSecuretKey);
        params.put("appDeviceId",Cst.PUSH_CLIENT_ID);
        return params;
    }

    @Override
    public void stringRequest() {
        super.stringRequest();
    }
}
