package com.yeepbank.android.request.business;

import android.content.Context;
import com.yeepbank.android.Cst;
import com.yeepbank.android.http.BaseRequest;
import com.yeepbank.android.http.StringListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by WW on 2015/11/16.
 */
public class ProjectDetailRequest extends BaseRequest {
    private String projectId;
    private String userId;

    public ProjectDetailRequest(Context context, StringListener stringListener,String projectId,String userId) {
        super(context, stringListener);
        this.projectId = projectId;
        this.userId = userId;
    }

    @Override
    protected String getUrl() {
        return Cst.URL.PROJECT_DETAIL_URL;
    }

    @Override
    protected int getMethod() {
        return METHOD_GET;
    }

    @Override
    protected Map<String, String> getParams() {
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("projectId",projectId);
        params.put("userId",userId);
        if (!"0".equals(userId)) {
            params.put("securetKey", Cst.currentUser.appSecuretKey);
        }
        return params;
    }

    public void stringRequest(){
        super.stringRequest();
    }
}
