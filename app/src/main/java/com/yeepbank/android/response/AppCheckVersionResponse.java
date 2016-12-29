package com.yeepbank.android.response;

import com.google.gson.Gson;
import com.yeepbank.android.http.BaseResponse;
import com.yeepbank.android.model.AppInfo;

/**
 * Created by WW on 2015/9/1.
 */
public class AppCheckVersionResponse extends BaseResponse<AppInfo> {


    public AppCheckVersionResponse() {
        gson = new Gson();
    }

    @Override
    public int getStatus(String result) {
        return 0;
    }

    @Override
    public AppInfo getObject(String result) {
        gson = getGson();
        if(result != null) {
            return gson.fromJson(result, AppInfo.class);
        }
        return new AppInfo();
    }
}
