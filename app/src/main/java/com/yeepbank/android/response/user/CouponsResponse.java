package com.yeepbank.android.response.user;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yeepbank.android.http.BaseResponse;
import com.yeepbank.android.model.user.CouponsVo;
import com.yeepbank.android.model.user.TotalAssets;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by WW on 2015/11/21.
 */
public class CouponsResponse extends BaseResponse<ArrayList<CouponsVo>> {

    @Override
    public int getStatus(String result) {
        return super.getStatus(result);
    }

    @Override
    public String getMessage(String result) {
        return super.getMessage(result);
    }

    @Override
    public ArrayList<CouponsVo> getObject(String result) {
        gson = getGson();
        if(result != null){
            try {
                JSONObject jsonObject = new JSONObject(result);
                String dataStr = jsonObject.getString("data");
                jsonObject =  new JSONObject(dataStr);
                String datasStr = jsonObject.getString("datas");
                return gson.fromJson(datasStr,new TypeToken<ArrayList<CouponsVo>>(){}.getType());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
