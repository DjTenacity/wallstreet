package com.yeepbank.android.base;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by WW on 2015/9/8.
 */
public abstract class BaseFragment extends Fragment {

    private View view;
    private boolean isCreated = false;/*初始化完成*/
    public static final String SOCKET_MSG = "socketMsg";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(getLayoutResource(),null);
        initView(view);
        fillData();
        return view;
    }

    public abstract void initView(View view);

    public abstract int getLayoutResource();

    public abstract void fillData();

    public abstract void onShow(Context context);

    public View getShowView(){
        return view;
    }

    @Override
    public void onResume() {
        isCreated = true;
        super.onResume();
    }

    public boolean isCreated() {
        return isCreated;
    }

}
