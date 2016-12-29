package com.yeepbank.android.response.user;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yeepbank.android.http.BaseResponse;
import com.yeepbank.android.model.user.BankCard;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by WW on 2015/11/17.
 */
public class BankListResponse extends BaseResponse<ArrayList<BankCard>> {

    public int getStatus(String result){
        return super.getStatus(result);
    }
    @Override
    public ArrayList<BankCard> getObject(String result) {
        gson = getGson();
        if(result != null){
            try {
                JSONObject json = new JSONObject(result);
                String dataStr = json.getString("data");
                return gson.fromJson(dataStr,new TypeToken<ArrayList<BankCard>>(){}.getType());
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
