package com.yeepbank.android.response.business;

import com.google.gson.Gson;
import com.yeepbank.android.http.BaseResponse;
import com.yeepbank.android.model.business.EdmAppCount;
import com.yeepbank.android.model.business.EdmOverview;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by WW on 2015/11/14.
 */
public class DailyIncreaseResponse extends BaseResponse<EdmOverview> {

    public int getStatus(String result){
        return super.getStatus(result);
    }
    @Override
    public EdmOverview getObject(String result) {
        if(result != null) {
            gson = getGson();
            try {
                JSONObject json = new JSONObject(result);
                String dataStr = json.getString("data");
                json = new JSONObject(dataStr);
                String edmOverviewStr = json.getString("edmOverView");
                return gson.fromJson(edmOverviewStr, EdmOverview.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String getMessage(String result){
        return super.getMessage(result);
    }
    /*
    * 天天盈没登录时的信息
    * */
    public EdmAppCount getEdmAppCount(String result){
        gson = getGson();
        try {
            JSONObject json = new JSONObject(result);
            String dataStr = json.getString("data");
            json = new JSONObject(dataStr);
            String edmAppCountStr = json.getString("edmAppCount");
            return gson.fromJson(edmAppCountStr,EdmAppCount.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
    * 是否买过天天盈
    * */
    public boolean hasBuyedEdm(String result){

        if(result != null){
            try {
                JSONObject jsonObject = new JSONObject(result);
                String dataStr = jsonObject.getString("data");
                jsonObject =  new JSONObject(dataStr);
                return jsonObject.getBoolean("hasBuyedEdm");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /*
   * 是否买过天天盈项目
   * */
    public boolean hasBuyEdmProject(String result){

        if(result != null){
            try {
                JSONObject jsonObject = new JSONObject(result);
                String dataStr = jsonObject.getString("data");
                jsonObject =  new JSONObject(dataStr);
                return jsonObject.getBoolean("hasBuyEdmProject");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
