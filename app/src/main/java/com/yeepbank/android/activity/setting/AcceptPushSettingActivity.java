package com.yeepbank.android.activity.setting;

import android.view.View;
import com.android.volley.VolleyError;
import com.yeepbank.android.Cst;
import com.yeepbank.android.R;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.request.user.PushSettingRequest;
import com.yeepbank.android.utils.LoadDialog;
import com.yeepbank.android.utils.Utils;
import com.yeepbank.android.widget.SwitchBtn;

import java.util.ArrayList;

/**
 * Created by WW on 2016/5/19.
 */
/*
* 接收通知设置假面
* */
public class AcceptPushSettingActivity extends BaseActivity implements Cst.OnStateChangeListener{

    private View navigationBar;
    private ArrayList<SwitchBtn> switchBtns = new ArrayList<SwitchBtn>();
    @Override
    protected void initView() {
        navigationBar = findViewById(R.id.navigation_bar);
        switchBtns.add((SwitchBtn) findViewById(R.id.accept_all_btn));
        switchBtns.add((SwitchBtn) findViewById(R.id.new_project_btn));
        switchBtns.add((SwitchBtn) findViewById(R.id.withdraw_and_purcharge_btn));
        switchBtns.add((SwitchBtn) findViewById(R.id.invest_reapy_btn));
        switchBtns.add((SwitchBtn) findViewById(R.id.add_invest_tick_btn));
    }

    @Override
    protected void fillData() {
        for (int i = 0; i < switchBtns.size(); i++) {
            switchBtns.get(i).setOnStateChangeListener(this);
        }
        if (!switchBtns.get(0).getState()){
            disabledAllBtn();
        }else {
            enabledAllBtn();
        }
    }

    @Override
    protected int getLayoutResources() {
        return R.layout.accept_push_setting;
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
    public void onOpened(SwitchBtn switchBtn) {
        Utils.getInstances().setAcceptPushMsg(mContext,switchBtn.getKey(),true);
        if (switchBtn.getId() == R.id.accept_all_btn){
            enabledAllBtn();
        }
        settingPush(switchBtn);
    }



    @Override
    public void onClosed(SwitchBtn switchBtn) {
        Utils.getInstances().setAcceptPushMsg(mContext, switchBtn.getKey(), false);
        if (switchBtn.getId() == R.id.accept_all_btn){
            disabledAllBtn();
        }
        settingPush(switchBtn);
    }

    private void disabledAllBtn() {
        for (int i = 0; i < switchBtns.size(); i++) {
            if (switchBtns.get(i).getId() != R.id.accept_all_btn){
                switchBtns.get(i).setEnabled(false);
            }
        }

    }

    private void enabledAllBtn() {
        for (int i = 0; i < switchBtns.size(); i++) {
            if (switchBtns.get(i).getId() != R.id.accept_all_btn){
                switchBtns.get(i).setEnabled(true);
            }
        }
    }

    private void settingPush(SwitchBtn swichBtn){
        String open = swichBtn.getState() ? "Y" : "N";
        String pushName = getPushName(swichBtn.getKey());
        PushSettingRequest request = new PushSettingRequest(mContext, new StringListener() {
            @Override
            public void ResponseListener(String result) {

            }

            @Override
            public void ErrorListener(VolleyError volleyError) {

            }
        },pushName,open);
        request.stringRequest();
    }

    private String getPushName(String key) {
        if (key.equals(mContext.getString(R.string.ACCESS_ALL))){
            return "0";
        }else if (key.equals(mContext.getString(R.string.ACCESS_DEPOSIT))){
            return "1";
        }else if (key.equals(mContext.getString(R.string.ACCESS_NEW_PROJECT))){
            return "2";
        }else if (key.equals(mContext.getString(R.string.ACCESS_GRANT_COUPON))){
            return "3";
        }else if (key.equals(mContext.getString(R.string.ACCESS_BIDDING))){
            return "4";
        }
        return "";
    }
}
