package com.yeepbank.android.response.user;

import com.yeepbank.android.http.BaseResponse;

/**
 * Created by WW on 2015/11/21.
 */
public class ExchangeResponse extends BaseResponse {
    @Override
    public Object getObject(String result) {
        return null;
    }

    @Override
    public int getStatus(String result) {
        return super.getStatus(result);
    }

    @Override
    public String getMessage(String result) {
        return super.getMessage(result);
    }
}
