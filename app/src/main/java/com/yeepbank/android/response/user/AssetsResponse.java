package com.yeepbank.android.response.user;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yeepbank.android.http.BaseResponse;
import com.yeepbank.android.model.business.InvestListItem;
import com.yeepbank.android.model.user.Investor;
import com.yeepbank.android.model.user.TotalAssets;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by WW on 2015/11/14.
 */
public class AssetsResponse extends BaseResponse<TotalAssets> {

    public int getStatus(String result){
        return super.getStatus(result);
    }
    @Override
    public TotalAssets getObject(String result) {
        gson = getGson();
        if(result != null){
            try {
                JSONObject jsonObject = new JSONObject(result);
                String dataStr = jsonObject.getString("data");
                jsonObject =  new JSONObject(dataStr);
                String totalAssetsStr = jsonObject.getString("totalAssets");
                return gson.fromJson(totalAssetsStr,TotalAssets.class);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Investor getInvestor(String result) {
        Gson gson = getGson();
        if(result != null){
            try {
                JSONObject jsonObject = new JSONObject(result);
                String dataStr = jsonObject.getString("data");
                jsonObject =  new JSONObject(dataStr);
                String totalAssetsStr = jsonObject.getString("investorInfo");
                return gson.fromJson(totalAssetsStr,Investor.class);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String getMessage(String result){
        return super.getMessage(result);
    }

    public String getCountCoupon(String result) {

        if(result != null){
            try {
                JSONObject jsonObject = new JSONObject(result);
                String dataStr = jsonObject.getString("data");
                jsonObject =  new JSONObject(dataStr);
                return  jsonObject.getString("countCoupon");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return "";

    }

    public String getRmdCode(String result) {

        if(result != null){
            try {
                JSONObject jsonObject = new JSONObject(result);
                String dataStr = jsonObject.getString("data");
                jsonObject =  new JSONObject(dataStr);
                return  jsonObject.getString("rmdCode");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return "";
    }
}
