package com.yeepbank.android.response.user;

import com.google.gson.Gson;
import com.yeepbank.android.http.BaseResponse;

/**
 * Created by  on 2015/11/21.
 */
public class AdviceFeedbackResponse extends BaseResponse {

    public int getStatus(String result){
        return super.getStatus(result);
    }
    @Override
    public Object getObject(String result) {
        return null;
    }
    public String getMessage(String result){
        return super.getMessage(result);
    }
}
