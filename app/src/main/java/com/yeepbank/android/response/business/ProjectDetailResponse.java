package com.yeepbank.android.response.business;

import com.google.gson.Gson;
import com.yeepbank.android.activity.business.HomeActivity;
import com.yeepbank.android.base.BaseModel;
import com.yeepbank.android.http.BaseResponse;
import com.yeepbank.android.model.business.EdmAppCount;
import com.yeepbank.android.model.business.NorProject;
import com.yeepbank.android.model.business.SecProject;
import com.yeepbank.android.model.business.TranProject;
import com.yeepbank.android.model.user.TotalAssets;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by WW on 2015/11/16.
 */
public class ProjectDetailResponse extends BaseResponse<BaseModel> {



    private Gson gson = getGson();

    public int getStatus(String result){
        return super.getStatus(result);
    }

    @Override
    public BaseModel getObject(String result) {
        if(result != null){
            try {
                JSONObject json = new JSONObject(result);
                String dataStr = json.getString("data");
                if(dataStr!=null){
                    return gson.fromJson(dataStr,BaseModel.class);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public SecProject getSecProject(String result) {
        try {
            JSONObject json = new JSONObject(result);
            String dataStr = json.getString("data");
            return gson.fromJson(dataStr,SecProject.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public TranProject getTranProject(String result) {
        gson = getGson();
        try {
            JSONObject json = new JSONObject(result);
            String dataStr = json.getString("data");
            return gson.fromJson(dataStr,TranProject.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getMessage(String result){
        return super.getMessage(result);
    }
}
