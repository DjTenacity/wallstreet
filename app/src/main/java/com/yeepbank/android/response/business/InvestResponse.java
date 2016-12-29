package com.yeepbank.android.response.business;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yeepbank.android.http.BaseResponse;
import com.yeepbank.android.model.business.*;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * Created by WW on 2015/11/5.
 */
public class InvestResponse extends BaseResponse<EdmAppCount> {


    public int getStatus(String result){
        return super.getStatus(result);
    }
    /*
    * 获取天天盈项目
    * */
    @Override
    public EdmAppCount getObject(String result) {
        gson = getGson();
        if(result != null){
            try {
                JSONObject jsonObject = new JSONObject(result);
                String dataStr = jsonObject.getString("data");
                jsonObject =  new JSONObject(dataStr);
                String edmStr = jsonObject.getString("edmProject");
                return gson.fromJson(edmStr,EdmAppCount.class);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /*
    * 获取秒杀项目
    * */
    public SecProject getSecProject(String result) {
        gson = getGson();
        if(result != null){
            try {
                JSONObject jsonObject = new JSONObject(result);
                String dataStr = jsonObject.getString("data");
                jsonObject = new JSONObject(dataStr);
                String edmStr = jsonObject.getString("secProject");
                return gson.fromJson(edmStr,SecProject.class);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /*
    * 获取新手项目
    * */
    public NorProject getFfiProject(String result) {
        gson = getGson();
        if(result != null){
            try {
                JSONObject jsonObject = new JSONObject(result);
                String dataStr = jsonObject.getString("data");
                jsonObject = new JSONObject(dataStr);
                String edmStr = jsonObject.getString("ffiProject");
                return gson.fromJson(edmStr,NorProject.class);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /*
    * 获取推荐项目集合
    * */
    public ArrayList<NorProject> getNorProjectList(String result) {
        gson = getGson();
        if(result != null){
            try {
                JSONObject jsonObject = new JSONObject(result);
                String dataStr = jsonObject.getString("data");
                jsonObject = new JSONObject(dataStr);
                String norProjectStr = jsonObject.getString("norProject");
                jsonObject = new JSONObject(norProjectStr);
                String norProjectJsonStr = jsonObject.getString("datas");
                return gson.fromJson(norProjectJsonStr, new TypeToken<ArrayList<NorProject>>(){}.getType());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<NorProject>();
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
                String norProjectStr = jsonObject.getString("norProject");
                jsonObject = new JSONObject(norProjectStr);
                return jsonObject.getString("totalCount");

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
                String norProjectStr = jsonObject.getString("tranProject");
                jsonObject = new JSONObject(norProjectStr);
                return jsonObject.getString("totalCount");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /*
     * 获取转债项目集合
     * */
    public ArrayList<TranProject> getTranProjectList(String result) {
        gson = getGson();
        if(result != null){
            try {
                JSONObject jsonObject = new JSONObject(result);
                String dataStr = jsonObject.getString("data");
                jsonObject = new JSONObject(dataStr);
                String tranProjectStr = jsonObject.getString("tranProject");
                jsonObject = new JSONObject(tranProjectStr);
                String tranProjectStrJsonStr = jsonObject.getString("datas");
                return gson.fromJson(tranProjectStrJsonStr, new TypeToken<ArrayList<TranProject>>(){}.getType());

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
