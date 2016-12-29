package com.yeepbank.android.request.business;

import android.content.Context;
import com.yeepbank.android.Cst;
import com.yeepbank.android.http.BaseRequest;
import com.yeepbank.android.http.StringListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by WW on 2015/11/12.
 */
public class BiddingListRequest extends BaseRequest {

    private String projectId;
    private int page;
    private int pageSize;

    public BiddingListRequest(Context context, StringListener stringListener,String projectId,int page,int pageSize) {
        super(context, stringListener);
        this.projectId = projectId;
        this.page = page;
        this.pageSize = pageSize;
    }

    @Override
    protected String getUrl() {
        return Cst.URL.DO_BIDDING_LIST_URL;
    }

    @Override
    protected int getMethod() {
        return METHOD_GET;
    }

    @Override
    protected Map<String, String> getParams() {
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("projectId",projectId);
        params.put("page", String.valueOf(page));
        params.put("pageSize", String.valueOf(pageSize));
        return params;
    }

    public void stringRequest(){
        super.stringRequest();
    }
}
