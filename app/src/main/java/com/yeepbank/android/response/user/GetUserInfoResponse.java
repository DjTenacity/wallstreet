package com.yeepbank.android.response.user;

import com.yeepbank.android.http.BaseResponse;
import com.yeepbank.android.model.business.EdmAppCount;
import com.yeepbank.android.model.business.EdmOverview;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by WW on 2015/11/25.
 */
public class GetUserInfoResponse extends BaseResponse {
    @Override
    public int getStatus(String result) {
        return super.getStatus(result);
    }

    @Override
    public String getMessage(String result) {
        return super.getMessage(result);
    }

    public EdmOverview getEdmOverview(String result){
        gson = getGson();
        try {
            JSONObject json = new JSONObject(result);
            String dataStr = json.getString("data");
            json = new JSONObject(dataStr);
            String edmInfoStr = json.getString("edmInfo");
            json = new JSONObject(edmInfoStr);
            String edmOverviewStr = json.getString("edmOverview");
            return gson.fromJson(edmOverviewStr,EdmOverview.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public EdmAppCount getEdmAppCount(String result){
        gson = getGson();
        try {
            JSONObject json = new JSONObject(result);
            String dataStr = json.getString("data");
            json = new JSONObject(dataStr);
            String edmInfoStr = json.getString("edmInfo");
            json = new JSONObject(edmInfoStr);
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
                String edmInfoStr = jsonObject.getString("edmInfo");
                jsonObject =  new JSONObject(edmInfoStr);
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
                String edmInfoStr = jsonObject.getString("edmInfo");
                jsonObject =  new JSONObject(edmInfoStr);
                return jsonObject.getBoolean("hasBuyEdmProject");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public Object getObject(String result) {
        return null;
    }
}
