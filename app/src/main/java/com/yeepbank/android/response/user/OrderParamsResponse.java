package com.yeepbank.android.response.user;

import android.support.annotation.NonNull;
import com.yeepbank.android.http.BaseResponse;
import com.yeepbank.android.model.order.PayOrder;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by WW on 2016/4/20.
 */
public class OrderParamsResponse extends BaseResponse<PayOrder> {

    @Override
    public int getStatus(String result) {
        return super.getStatus(result);
    }

    @Override
    public String getMessage(String result) {
        return super.getMessage(result);
    }

    @NonNull
    @Override
    public PayOrder getObject(String result) {
        gson = getGson();
        try {
            JSONObject jsonObject = new JSONObject(result);
            String data = jsonObject.getString("data");
            return gson.fromJson(data,PayOrder.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
