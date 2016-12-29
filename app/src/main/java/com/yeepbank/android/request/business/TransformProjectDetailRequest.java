package com.yeepbank.android.request.business;

import android.content.Context;
import com.yeepbank.android.Cst;
import com.yeepbank.android.http.BaseRequest;
import com.yeepbank.android.http.StringListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by WW on 2015/11/26.
 */
public class TransformProjectDetailRequest extends BaseRequest {
    private String transferId;
    public TransformProjectDetailRequest(Context context, StringListener stringListener,String transferId) {
        super(context, stringListener);
        this.transferId = transferId;
    }

    @Override
    protected String getUrl() {
        return Cst.URL.TRANSFORM_PROJECT_DETAIL_URL;
    }

    @Override
    protected int getMethod() {
        return METHOD_GET;
    }

    @Override
    protected Map<String, String> getParams() {
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("transferId",transferId);
        return params;
    }

    @Override
    public void stringRequest() {
        super.stringRequest();
    }
}
