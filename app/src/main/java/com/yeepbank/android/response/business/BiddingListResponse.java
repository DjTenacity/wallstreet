package com.yeepbank.android.response.business;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yeepbank.android.http.BaseResponse;
import com.yeepbank.android.model.business.InvestListItem;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by WW on 2015/11/12.
 */
public class BiddingListResponse extends BaseResponse<ArrayList<InvestListItem>>{

    public int getStatus(String result){
        return super.getStatus(result);
    }
    @Override
    public ArrayList<InvestListItem> getObject(String result) {
        gson = getGson();
        if(result != null){
            try {
                JSONObject jsonObject = new JSONObject(result);
                String dataStr = jsonObject.getString("data");
                jsonObject =  new JSONObject(dataStr);
                String edmStr = jsonObject.getString("datas");
                return gson.fromJson(edmStr,new TypeToken<ArrayList<InvestListItem>>(){}.getType());

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
