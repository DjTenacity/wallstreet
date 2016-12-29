package com.yeepbank.android.response;

import android.support.annotation.NonNull;
import com.yeepbank.android.http.BaseResponse;

/**
 * Created by WW on 2016/6/15.
 */
public class DefaultResponse extends BaseResponse {

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
