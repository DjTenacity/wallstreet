package com.yeepbank.android.response.business;

import android.support.annotation.NonNull;
import com.yeepbank.android.http.BaseResponse;
import com.yeepbank.android.model.business.BattleCouponModel;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xiaogang.dong on 2016/9/9.
 */
public class BattleResponse extends BaseResponse{
    @Override
    public int getStatus(String result) {
        return super.getStatus(result);
    }

    @Override
    public BattleCouponModel getObject(String result) {
        gson=getGson();
        if(result!=null){
            try {
                JSONObject json=new JSONObject(result);
                String dataStr=json.getString("data");
                return gson.fromJson(dataStr,BattleCouponModel.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public String getMessage(String result) {
        return super.getMessage(result);
    }
}
