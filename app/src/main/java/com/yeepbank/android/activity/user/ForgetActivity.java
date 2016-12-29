package com.yeepbank.android.activity.user;

import android.view.View;
import android.widget.TextView;
import com.yeepbank.android.R;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.utils.LoadDialog;

/**
 * Created by WW on 2015/9/11.
 */
public class ForgetActivity extends BaseActivity {
    private View navigationBar;
    @Override
    protected void initView() {
        navigationBar=findViewById(R.id.action_bar);
    }

    @Override
    protected void fillData() {

    }

    @Override
    protected int getLayoutResources() {
        return R.layout.forget_password;
    }

    @Override
    protected View getNavigationBar() {
        return navigationBar;
    }

    @Override
    protected void initNavigationBar(View navigationBar) {
        if(navigationBar!=null){
            TextView preBtn = (TextView) navigationBar.findViewById(R.id.pre_text);
            preBtn.setText("关闭");
            super.initNavigationBar(navigationBar);
        }
    }

    @Override
    public LoadDialog getLoadDialog() {
        return null;
    }
}
