package com.yeepbank.android.response.user;

import android.support.annotation.NonNull;
import com.google.gson.reflect.TypeToken;
import com.yeepbank.android.http.BaseResponse;
import com.yeepbank.android.model.order.PdsDepositInfo;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by WW on 2016/4/25.
 */
public class ConfirmOrderResponse extends BaseResponse<PdsDepositInfo> {

    @Override
    public int getStatus(String result) {
        return super.getStatus(result);
    }

    @Override
    public String getMessage(String result) {
        return super.getMessage(result);
    }

    @Override
    public PdsDepositInfo getObject(@NonNull String result) {
        gson = getGson();
        try {
            JSONObject jsonObject = new JSONObject(result);
            String dataStr = jsonObject.getString("data");
            jsonObject = new JSONObject(dataStr);
            return gson.fromJson(jsonObject.getString("pdsDepositInfo"),PdsDepositInfo.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new PdsDepositInfo();
    }
}
