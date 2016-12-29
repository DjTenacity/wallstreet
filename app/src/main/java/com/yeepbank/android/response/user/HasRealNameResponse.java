package com.yeepbank.android.response.user;

import com.yeepbank.android.http.BaseResponse;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by WW on 2015/11/24.
 */
public class HasRealNameResponse extends BaseResponse<String> {
   /* @Override
    public String getObject(String result) {
        if(result != null) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                return jsonObject.getString("data");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }*/
   @Override
    public String getObject(String result){
       if(result != null) {
           try {
               JSONObject jsonObject = new JSONObject(result);
               String dataString=jsonObject.getString("data");
               jsonObject=new JSONObject(dataString);
               return jsonObject.getString("idAuthFlag");
           } catch (JSONException e) {
               e.printStackTrace();
           }
       }
       return null;
   }
    public String getIdNO(String result){
        if(result != null) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                String dataString=jsonObject.getString("data");
                jsonObject=new JSONObject(dataString);
                return jsonObject.getString("idNo");
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
