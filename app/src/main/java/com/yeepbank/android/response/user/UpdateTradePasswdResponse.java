package com.yeepbank.android.response.user;

import com.google.gson.Gson;
import com.yeepbank.android.http.BaseResponse;

/**
 * Created by Administrator on 2015/11/25.
 */
public class UpdateTradePasswdResponse extends BaseResponse {

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
