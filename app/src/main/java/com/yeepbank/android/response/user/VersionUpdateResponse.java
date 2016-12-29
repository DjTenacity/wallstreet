package com.yeepbank.android.response.user;

import com.google.gson.Gson;
import com.yeepbank.android.http.BaseResponse;
import com.yeepbank.android.model.AppInfo;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xiaogang.dong on 2015/12/1.
 */
public class VersionUpdateResponse extends BaseResponse {

    public int getStatus(String result){
        return super.getStatus(result);
    }

    //{"state":{"code":200,"message":"success"},
    // "data":{"version":"2.2.0",
    // "message":"1.优化登录,更方便更安全;2.充值结果修改为实时获取;3.新增资金流水,可以查看充值提现记录",
    // "hasNewVersion":true,
    // "updateUrl":"http://app.yeepbank.com/apk/wallstreet_android_pro.apk",
    // "upgradeType":"2",
    // "mustUpdate":true}}
    @Override
    public AppInfo getObject(String result) {
        gson = getGson();
        try {
            JSONObject jsonObject = new JSONObject(result);
            String dataStr = jsonObject.getString("data");

            return gson.fromJson(dataStr, AppInfo.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    public String getMessage(String result){
        return super.getMessage(result);
    }

    public int getCmd(String result){
        if(result != null) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                String dataStr = jsonObject.getString("data");
                jsonObject = new JSONObject(dataStr);
                return jsonObject.getInt("upgradeType");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public String getUpdateMessage(String result){
        try {
            JSONObject jsonObject = new JSONObject(result);
            String dataStr = jsonObject.getString("data");
            jsonObject = new JSONObject(dataStr);
            return jsonObject.getString("message");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }
}
