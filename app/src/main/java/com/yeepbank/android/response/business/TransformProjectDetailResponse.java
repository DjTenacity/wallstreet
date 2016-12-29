package com.yeepbank.android.response.business;

import com.yeepbank.android.base.BaseModel;
import com.yeepbank.android.http.BaseResponse;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by WW on 2015/11/26.
 */
public class TransformProjectDetailResponse extends BaseResponse<BaseModel> {
    @Override
    public BaseModel getObject(String result) {
        if(result != null){
            gson = getGson();
            try {
                JSONObject json = new JSONObject(result);
                String dataStr = json.getString("data");
                if(dataStr != null){
                    return gson.fromJson(dataStr,BaseModel.class);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public int getStatus(String result) {
        return super.getStatus(result);
    }

    @Override
    public String getMessage(String result) {
        return super.getMessage(result);
    }
}
