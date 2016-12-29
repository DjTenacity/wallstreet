package com.yeepbank.android.activity.business;

import android.view.View;
import android.widget.TextView;
import com.yeepbank.android.R;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.utils.LoadDialog;

/**
 * Created by WW on 2015/9/18.
 */
public class RecommendProjectActivity extends BaseActivity implements View.OnClickListener{
    private TextView recommendItem;
    private View navigationBar;
    @Override
    protected void initView() {
        navigationBar=findViewById(R.id.action_bar);
        recommendItem = (TextView) findViewById(R.id.recommend_item);
        recommendItem.setOnClickListener(this);
    }

    @Override
    protected void fillData() {

    }

    @Override
    protected int getLayoutResources() {
        return R.layout.recommend_project;
    }

    @Override
    protected View getNavigationBar() {
        return navigationBar;
    }

    @Override
    public LoadDialog getLoadDialog() {
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.recommend_item:
                gotoTargetRemovePre(ProjectDetailActivity.class,R.anim.activity_in_from_right,
                        R.anim.activity_out_from_left,getString(R.string.invest));
                break;
        }
    }
}
