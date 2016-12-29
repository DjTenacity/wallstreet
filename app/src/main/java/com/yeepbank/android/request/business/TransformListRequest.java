package com.yeepbank.android.request.business;

import android.content.Context;
import com.yeepbank.android.Cst;
import com.yeepbank.android.http.BaseRequest;
import com.yeepbank.android.http.StringListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by WW on 2015/11/10.
 */
public class TransformListRequest extends BaseRequest {

    private int page,pageSize;
    private String status,sortColumn,sort;

    public TransformListRequest(Context context, StringListener stringListener,int page,int pageSize,String status,String sortColumn,
                                String sort) {
        super(context, stringListener);
        this.page = page;
        this.pageSize = pageSize;
        this.status = status;
        this.sortColumn = sortColumn;
        this.sort = sort;
    }

    @Override
    protected String getUrl() {
        return Cst.URL.PROJECT_TRANSFORM_URL;
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
        params.put("sortColumn", sortColumn);
        params.put("sort", sort);
        return params;
    }

    public void stringRequest(){
        super.stringRequest();
    }
}
