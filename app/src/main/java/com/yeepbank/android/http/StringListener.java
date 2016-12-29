package com.yeepbank.android.http;

import com.android.volley.VolleyError;
import org.json.JSONException;

/**
 * Created by WW on 2015/8/21.
 */
public interface StringListener {

    void ResponseListener(String result);

    void ErrorListener(VolleyError volleyError);
}
