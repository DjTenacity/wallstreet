package com.yeepbank.android.response.business;

import com.google.gson.reflect.TypeToken;
import com.yeepbank.android.base.BaseModel;
import com.yeepbank.android.http.BaseResponse;
import com.yeepbank.android.model.business.EdmAppCount;
import com.yeepbank.android.model.business.Purchase;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by WW on 2015/11/14.
 */
public class PurchaseResponse extends BaseResponse<ArrayList<Purchase>> {

    public int getStatus(String result){
        return super.getStatus(result);
    }
    @Override
    public ArrayList<Purchase> getObject(String result) {
        gson = getGson();
        if(result != null){
            try {
                JSONObject json = new JSONObject(result);
                String dataStr = json.getString("data");
                return gson.fromJson(dataStr,new TypeToken<ArrayList<Purchase>>(){}.getType());
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
