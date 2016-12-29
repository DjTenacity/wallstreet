package com.yeepbank.android.http;

import android.support.annotation.NonNull;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by WW on 2015/9/1.
 */
public abstract class BaseResponse<T> {

    protected Gson gson;

    protected Gson getGson(){
        synchronized (BaseResponse.class){
            if(gson == null){
                gson = new Gson();
            }
        }
        return gson;
    }

    public int getStatus(String result){

        if (result == null){
            return 0;
        }

        JSONObject json = null;

        try {
            json = new JSONObject(result);
            if(json.getString("state") != null){
                JSONObject codeObject = new JSONObject(json.getString("state"));
                return codeObject.getInt("code");
            }

        } catch (JSONException e) {
            try {
                return json.getInt("code");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return 0;
    }


    public abstract T getObject(@NonNull String result);

    public String getMessage(String result){
        if (result == null){
            return "";
        }
        JSONObject json = null;
        try {
            json = new JSONObject(result);
            if(json.getString("state") != null){
                JSONObject msgObject = new JSONObject(json.getString("state"));
                return msgObject.getString("message");
            }

        } catch (JSONException e) {
            try {
                return json.getString("message");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return "";
    }
}
