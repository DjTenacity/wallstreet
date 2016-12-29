package com.yeepbank.android.request.business;

import android.content.Context;
import com.yeepbank.android.Cst;
import com.yeepbank.android.http.BaseRequest;
import com.yeepbank.android.http.StringListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by WW on 2015/11/6.
 */
public class ProjectListRequest extends BaseRequest {

    private int page,pageSize;
    private String status;

    public ProjectListRequest(Context context, StringListener stringListener,int page,int pageSize,String status) {
        super(context, stringListener);
        this.page = page;
        this.pageSize = pageSize;
        this.status = status;
    }


    @Override
    protected String getUrl() {
        return Cst.URL.PROJECT_LIST_URL;
    }

    @Override
    protected int getMethod() {
        return METHOD_GET;
    }

    @Override
    protected Map<String, String> getParams() {
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("page", String.valueOf(page));
        params.put("pageSize", String.valueOf(pageSize));
        params.put("status", status);
        return params;
    }

    public void stringRequest(){
        super.stringRequest();
    }
}
