package com.yeepbank.android.response.user;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yeepbank.android.http.BaseResponse;
import com.yeepbank.android.model.business.NorProject;
import com.yeepbank.android.model.user.CouponsVo;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by xiaogang.dong on 2015/11/11.
 */
public class CouponInfoResponse extends BaseResponse  {
    public int getStatus(String result){
        return super.getStatus(result);
    }
    @Override
    public Object getObject(String result) {
        return null;
    }
    public ArrayList<CouponsVo> getData(String result){
        gson =getGson();
        if(result!=null){
            try{
                JSONObject jsonObject = new JSONObject(result);
                String dataStr = jsonObject.getString("data");
                jsonObject = new JSONObject(dataStr);
                String couponStr = jsonObject.getString("datas");
                return gson.fromJson(couponStr, new TypeToken<ArrayList<CouponsVo>>(){}.getType());
            }catch(JSONException e){
                e.printStackTrace();
            }
        }

        return null;
    }
    public String getMessage(String result){
        return super.getMessage(result);
    }


}
