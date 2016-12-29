package com.yeepbank.android.request;

import android.content.Context;
import com.yeepbank.android.http.BaseRequest;
import com.yeepbank.android.http.StringListener;

import java.util.Map;

/**
 * Created by WW on 2015/9/1.
 */
public class AppDownloadAppRequest extends BaseRequest {

    public AppDownloadAppRequest(Context context, StringListener stringListener) {
        super(context, stringListener);
    }

    @Override
    protected String getUrl() {
        return null;
    }

    @Override
    protected int getMethod() {
        return 0;
    }

    @Override
    protected Map<String, String> getParams() {
        return null;
    }

    @Override
    public void stringRequest() {
        super.stringRequest();
    }
}
