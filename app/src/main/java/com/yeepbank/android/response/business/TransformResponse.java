package com.yeepbank.android.response.business;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yeepbank.android.http.BaseResponse;
import com.yeepbank.android.model.business.TranProject;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by WW on 2015/11/10.
 */
public class TransformResponse extends BaseResponse<ArrayList<TranProject>> {

    public int getStatus(String result){
        return super.getStatus(result);
    }
    @Override
    public ArrayList<TranProject> getObject(String result) {
        gson = getGson();
        if(result != null){
            try {
                JSONObject jsonObject = new JSONObject(result);
                String dataStr = jsonObject.getString("data");
                jsonObject = new JSONObject(dataStr);
                return gson.fromJson(jsonObject.getString("datas"),new TypeToken<ArrayList<TranProject>>(){}.getType());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /*
* 获取转债项目总数
* */
    public String getTransProjectCount(String result) {
        if(result != null){
            try {
                JSONObject jsonObject = new JSONObject(result);
                String dataStr = jsonObject.getString("data");
                jsonObject = new JSONObject(dataStr);
                return jsonObject.getString("totalCount");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public String getMessage(String result){
        return super.getMessage(result);
    }
}
