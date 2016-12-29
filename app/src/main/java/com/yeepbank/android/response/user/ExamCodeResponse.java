package com.yeepbank.android.response.user;

import com.yeepbank.android.http.BaseResponse;
import org.json.JSONException;

/**
 * Created by WW on 2015/11/3.
 */
public class ExamCodeResponse extends BaseResponse {
    @Override
    public int getStatus(String result) {
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
