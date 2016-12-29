package com.yeepbank.android.response.business;

import com.google.gson.Gson;
import com.yeepbank.android.activity.business.HomeActivity;
import com.yeepbank.android.http.BaseResponse;
import com.yeepbank.android.model.user.TotalAssets;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by WW on 2015/11/11.
 */
public class DoBiddingResponse extends BaseResponse{

    public int getStatus(String result){
        return super.getStatus(result);
    }
    @Override
    public Object getObject(String result) {
        return null;
    }

    public String getMessage(String result){
        return super.getMessage(result);
    }
    //{"state":{"code":200,"message":"success"},"data":{"totalAssets":{"freeBalance":975690.56,"balance":5061980.08,"totalPIReceivable":1849722.77,"totalBiddingAmountToday":0,"waitingBiddingAmount":1994500.00,"waitingBiddingAmountWithoutECcoupon":1989500.00,"withDrawingAmount":600439.52,"totalAssets":5415352.85}}}
    public TotalAssets getTotalAssets(String result){
        gson = getGson();
        if(result != null){
            try {
                JSONObject json = new JSONObject(result);
                String dataStr = json.getString("data");
                json = new JSONObject(dataStr);
                return gson.fromJson(json.getString("totalAssets"),TotalAssets.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return HomeActivity.totalAssets;

    }
}
