package com.yeepbank.android.response.user;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yeepbank.android.http.BaseResponse;

import com.yeepbank.android.model.user.InvestmentVo;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by xiaogang.dong on 2015/11/21.
 * 投资记录的响应
 */
public class InvestmentRecordResponse extends BaseResponse<ArrayList<InvestmentVo>> {

    public int getStatus(String result){
       return super.getStatus(result);
    }
    @Override
    public ArrayList<InvestmentVo> getObject(String result) {
        if(result != null) {
            gson = getGson();
            try {
                JSONObject json = new JSONObject(result);
                String dataStr = json.getString("data");
                json = new JSONObject(dataStr);
                String datasStr = json.getString("datas");
                return gson.fromJson(datasStr, new TypeToken<ArrayList<InvestmentVo>>() {
                }.getType());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String getMessage(String result){
        return super.getMessage(result);
    }
}
