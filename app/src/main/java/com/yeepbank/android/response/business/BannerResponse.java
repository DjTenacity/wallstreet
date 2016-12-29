package com.yeepbank.android.response.business;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yeepbank.android.http.BaseResponse;
import com.yeepbank.android.model.Banner;
import com.yeepbank.android.model.business.EdmOverview;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by WW on 2015/11/19.
 */
public class BannerResponse extends BaseResponse<ArrayList<Banner>> {

    public int getStatus(String result){
        return super.getStatus(result);
    }
    @Override
    public ArrayList<Banner> getObject(String result) {
        if(result != null){
            try {
                gson = getGson();
                JSONObject json = new JSONObject(result);
                String dataStr = json.getString("data");
                return gson.fromJson(dataStr, new TypeToken<ArrayList<Banner>>(){}.getType());
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
