package com.yeepbank.android.response.user;

import com.google.gson.Gson;
import com.yeepbank.android.http.BaseResponse;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by WW on 2015/11/19.
 */
public class CheckBankResponse extends BaseResponse<String> {

    public int getStatus(String result){
        return super.getStatus(result);
    }

    @Override
    public String getObject(String result) {
        if(result != null) {
            try {
                JSONObject json = new JSONObject(result);
                return json.getString("data");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public String getMessage(String result){
        return super.getMessage(result);
    }
}
