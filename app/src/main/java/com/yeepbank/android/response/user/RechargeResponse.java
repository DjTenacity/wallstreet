package com.yeepbank.android.response.user;

import com.google.gson.Gson;
import com.yeepbank.android.http.BaseResponse;
import com.yeepbank.android.model.user.TotalAssets;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by WW on 2015/11/18.
 */
public class RechargeResponse extends BaseResponse {
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

    public String getDepositId(String result){
        Gson gson = getGson();
        if(result != null) {
            try {
                JSONObject json = new JSONObject(result);
                String dataStr = json.getString("data");
                return dataStr;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;

    }
}
