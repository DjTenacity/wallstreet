package com.yeepbank.android.response.user;

import android.support.annotation.NonNull;
import com.google.gson.Gson;
import com.yeepbank.android.http.BaseResponse;
import com.yeepbank.android.model.user.RechargeResult;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by WW on 2016/3/30.
 */
public class RechargeResultResponse extends BaseResponse<RechargeResult> {
    @Override
    public int getStatus(String result) {
        return super.getStatus(result);
    }

    @Override
    public String getMessage(String result) {
        return super.getMessage(result);
    }

    /*
    * data = {
    data =     {
        amount = "<null>";
        depositDate = "";
        depositDatetime = "";
        depositTime = "";
        faileReason = "<null>";
        status = W;
        statusName = "\U5145\U503c\U4e2d";
    };
    state =     {
        code = 200;
        message = success;
    };
}

    * */

    public String getFailReason(String result){
        try {
            JSONObject jsonObject = new JSONObject(result);
            String data = jsonObject.getString("data");
            jsonObject = new JSONObject(data);
            return jsonObject.getString("faileReason");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    @NonNull
    @Override
    public RechargeResult getObject(String result) {
        Gson gson = getGson();
        try {
            JSONObject jsonObject = new JSONObject(result);
            String data = jsonObject.getString("data");
            return gson.fromJson(data,RechargeResult.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
