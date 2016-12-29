package com.yeepbank.android.request.user;

import android.content.Context;
import com.yeepbank.android.Cst;
import com.yeepbank.android.http.BaseRequest;
import com.yeepbank.android.http.StringListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/11/21.
 */
public class AdviceFeedbackRequest extends BaseRequest {
    private String userId;
    private String mobile;
    private String feedbackContent;
    private String appSecuretKey;


    public AdviceFeedbackRequest(Context context, StringListener stringListener,String userId,String mobile,String feedbackContent) {
        super(context, stringListener);
        this.userId=userId;
        this.mobile=mobile;
        this.feedbackContent=feedbackContent;
        if(Cst.currentUser==null){
            appSecuretKey="";
        }else{
            appSecuretKey=Cst.currentUser.appSecuretKey;
        }


    }

    @Override
    protected String getUrl() {
        return Cst.URL.INVESTOR_ADVICEFEEDBACK_URL;
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
        params.put("feedback",feedbackContent);
        params.put("securetKey",appSecuretKey);
        return params;
    }
    public void stringRequest(){
        super.stringRequest();
    }
}
