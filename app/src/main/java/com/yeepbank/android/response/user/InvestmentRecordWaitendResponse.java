package com.yeepbank.android.response.user;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yeepbank.android.http.BaseResponse;
import com.yeepbank.android.model.user.InvestmentVo;
import com.yeepbank.android.model.user.NeProjectVo;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by xiaogang.dong on 2015/11/24.
 */
public class InvestmentRecordWaitendResponse extends BaseResponse {

    public int getStatus(String result){
        return super.getStatus(result);
    }

    public ArrayList<NeProjectVo> getDatas(String result) {
        if(result != null) {
            gson = getGson();
            try {
                JSONObject json = new JSONObject(result);
                String dataStr = json.getString("data");
                json = new JSONObject(dataStr);
                String datasStr = json.getString("datas");
                return gson.fromJson(datasStr, new TypeToken<ArrayList<NeProjectVo>>() {
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
    @Override
    public Object getObject(String result) {
        return null;
    }
}
