package com.yeepbank.android.response.user;

import com.yeepbank.android.http.BaseResponse;

/**
 * Created by WW on 2015/11/19.
 */
public class RealNameResponse extends BaseResponse {

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
