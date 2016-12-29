package com.yeepbank.android.response.user;

import com.yeepbank.android.http.BaseResponse;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by WW on 2015/11/20.
 */
public class BankExamCodeResponse extends BaseResponse<String> {

    @Override
    public int getStatus(String result) {
        return super.getStatus(result);
    }

    @Override
    public String getMessage(String result) {
        return super.getMessage(result);
    }

    @Override
    public String getObject(String result) {
        if(result != null){
            try {
                JSONObject json = new JSONObject(result);
                String jsonStr = json.getString("data");
                json = new JSONObject(jsonStr);
                return json.getString("requestId");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return "";
    }
}
