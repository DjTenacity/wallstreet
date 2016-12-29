package com.yeepbank.android.response.business;

import android.support.annotation.NonNull;
import com.google.gson.reflect.TypeToken;
import com.yeepbank.android.http.BaseResponse;
import com.yeepbank.android.model.business.BattleCouponModel;
import com.yeepbank.android.model.user.RechargeRecordModel;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by xiaogang.dong on 2016/9/8.
 */
public class BattleListResponse extends BaseResponse<ArrayList<BattleCouponModel>>{
    @Override
    public int getStatus(String result) {
        return super.getStatus(result);
    }
    //解析数据获得 数据集合
    @Override
    public ArrayList<BattleCouponModel> getObject(String result) {
        gson = getGson();
        try {
            JSONObject json=new JSONObject(result);
            String dataStr=json.getString("data");
            json=new JSONObject(dataStr);
            String datasStr=json.getString("datas");
            return gson.fromJson(datasStr,new TypeToken<ArrayList<BattleCouponModel>>(){}.getType());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getMessage(String result) {
        return super.getMessage(result);
    }
}
