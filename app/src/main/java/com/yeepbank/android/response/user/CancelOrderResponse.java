package com.yeepbank.android.response.user;

import android.support.annotation.NonNull;
import com.yeepbank.android.http.BaseResponse;

/**
 * Created by WW on 2016/4/20.
 */
public class CancelOrderResponse extends BaseResponse {

    @Override
    public int getStatus(String result) {
        return super.getStatus(result);
    }

    @Override
    public String getMessage(String result) {
        return super.getMessage(result);
    }

    @Override
    public Object getObject(@NonNull String result) {
        return null;
    }
}
