package com.yeepbank.android.request;

import android.content.Context;
import com.yeepbank.android.Cst;
import com.yeepbank.android.http.BaseRequest;
import com.yeepbank.android.http.StringListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by WW on 2015/9/1.
 */
public class AppCheckVersionRequest extends BaseRequest {

    public AppCheckVersionRequest(Context context, StringListener stringListener) {
        super(context, stringListener);
    }

    @Override
    protected String getUrl() {
        return Cst.URL.CHECK_VERSION;
    }

    @Override
    protected int getMethod() {
        return METHOD_POST;
    }

    @Override
    protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<String, String>();
        return params;
    }

    @Override
    public void stringRequest() {
        super.stringRequest();
    }
}
