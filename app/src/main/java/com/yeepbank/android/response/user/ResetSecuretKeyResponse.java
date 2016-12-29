package com.yeepbank.android.response.user;

import com.google.gson.Gson;
import com.yeepbank.android.http.BaseResponse;
import org.json.JSONException;
import org.json.JSONObject;

import com.yeepbank.android.model.user.Investor;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by WW on 2016/3/23.
 */
public class ResetSecuretKeyResponse extends BaseResponse {

    public int getStatus(String result){
        return super.getStatus(result);
    }


   @Override
   public Investor getObject(String result) {
       gson = getGson();
       try {
           JSONObject json = new JSONObject(result);
           String dataStr = json.getString("data");
           json = new JSONObject(dataStr);
           String investorStr = json.getString("investorInfo");
           return gson.fromJson(investorStr,Investor.class);
       } catch (JSONException e) {
           e.printStackTrace();
       }
       return null;
   }
    public String getMessage(String result){
        return super.getMessage(result);
    }
}
