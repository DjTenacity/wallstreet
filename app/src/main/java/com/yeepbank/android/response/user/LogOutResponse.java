package com.yeepbank.android.response.user;

import com.google.gson.Gson;
import com.yeepbank.android.http.BaseResponse;

/**
 * Created by xiaogang.dong on 2016/3/25.
 */
public class LogOutResponse extends BaseResponse{
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
