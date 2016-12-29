package com.yeepbank.android.response.business;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yeepbank.android.base.BaseModel;
import com.yeepbank.android.http.BaseResponse;
import com.yeepbank.android.model.business.Project;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by WW on 2015/11/6.
 */
public class ProjectListResponse extends BaseResponse<ArrayList<BaseModel>> {

    public int getStatus(String result){
        return super.getStatus(result);
    }
    @Override
    public ArrayList<BaseModel> getObject(String result) {
        gson = getGson();
        if(result != null){
            try {
                JSONObject jsonObject = new JSONObject(result);
                String dataStr = jsonObject.getString("data");
                jsonObject =  new JSONObject(dataStr);
                String edmStr = jsonObject.getString("datas");
                return gson.fromJson(edmStr,new TypeToken<ArrayList<BaseModel>>(){}.getType());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /*
  * 获取推荐项目总数
  * */
    public String getNorProjectCount(String result) {
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
        return  super.getMessage(result);
    }
}
