package com.yeepbank.android.activity.setting;


import android.content.Intent;
import android.os.Handler;
import android.widget.*;
import com.android.volley.VolleyError;
import com.yeepbank.android.Cst;
import com.yeepbank.android.R;
import com.yeepbank.android.WebSocketService;
import com.yeepbank.android.activity.business.HomeActivity;
import com.yeepbank.android.activity.user.ForgetActivity;
import com.yeepbank.android.base.ActivityStacks;
import com.yeepbank.android.base.BaseActivity;
import android.view.View;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.request.user.LogOutRequest;
import com.yeepbank.android.response.user.LogOutResponse;
import com.yeepbank.android.utils.LoadDialog;
import com.yeepbank.android.utils.Utils;
import com.yeepbank.android.widget.LayoutInSetting;
import com.yeepbank.android.widget.SwitchBtn;

/**
 * Created by 董晓刚 on 2015/9/16.
 */
public class SettingListActivity extends BaseActivity implements View.OnClickListener,View.OnFocusChangeListener{
    private LinearLayout linearLayout;//账户与安全
    private LinearLayout adviceLinearLayout;//意见反馈
    private LinearLayout about;//关于易宝金融
    private Button exitBtn;
    private TextView welcomeText;
    private LinearLayout switchBtn;
    private LoadDialog cancelDialog;
    private View navigationBar;
    private Spinner settingSpinner;
    private LayoutInSetting accountAndSecurityContentLayout;
    @Override
    protected void initView() {
        navigationBar = findViewById(R.id.navigation_bar);
        linearLayout=(LinearLayout)findViewById(R.id.account_and_security);
        linearLayout.setOnClickListener(this);
        accountAndSecurityContentLayout = (LayoutInSetting) findViewById(R.id.account_and_security_content);
        adviceLinearLayout=(LinearLayout)findViewById(R.id.advice_feedback);
        adviceLinearLayout.setOnClickListener(this);
        about=(LinearLayout)findViewById(R.id.about);
        about.setOnClickListener(this);
        exitBtn = (Button) findViewById(R.id.re_btn);
        exitBtn.setOnClickListener(this);
        welcomeText = (TextView) findViewById(R.id.welcome);
        switchBtn = (LinearLayout) findViewById(R.id.slipBtn);
        switchBtn.setOnClickListener(this);
//        switchBtn.setOnStateChangeListener(this);
        cancelDialog=new LoadDialog(mContext, R.style.dialog, false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelDialog.dismiss();
                cancelSafety();

            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelDialog.dismiss();
            }
        },0);

//        settingSpinner = (Spinner) findViewById(R.id.setting_url);
//        settingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Cst.ROOT_URL = ((TextView)view).getText().toString();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//        if (Cst.currentUser.loginName.equals("15829061487")){
//            settingSpinner.setVisibility(View.VISIBLE);
//        }else settingSpinner.setVisibility(View.GONE);
    }

    @Override
    protected void fillData() {
        if(Cst.currentUser != null){
            welcomeText.setText("欢迎,"+Cst.currentUser.loginName);
            if (Cst.currentUser.investLevel != null && !Cst.currentUser.investLevel.trim().equals("")){
                accountAndSecurityContentLayout.setValue("投资级别:"+Cst.currentUser.investLevel);
            }else {
                accountAndSecurityContentLayout.setValue("");
            }
        }else {
            welcomeText.setText("欢迎,");
        }
    }

    @Override
    protected int getLayoutResources() {
        return R.layout.setting_list;
    }

    @Override
    protected View getNavigationBar() {
        return navigationBar;
    }

    @Override
    public LoadDialog getLoadDialog() {
        return loadding;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.account_and_security:
                gotoTarget(AccountAndSecurityActivity.class, R.anim.activity_in_from_right, R.anim.activity_out_from_left,"");
                break;
            case R.id.advice_feedback:
               gotoTarget(AdviceFeedbackActivity.class, R.anim.activity_in_from_right, R.anim.activity_out_from_left,"");
                break;
            case R.id.about:
               gotoTarget(AboutYeepActivity.class, R.anim.activity_in_from_right, R.anim.activity_out_from_left,"");
                break;
            case R.id.re_btn:
                cancelDialog.setMessage("确定要退出吗？");
                cancelDialog.setCancelBtn("取消");
                cancelDialog.setSureBtn("确定");

                cancelDialog.showAs();
               // Cst.currentUser = null;
                //Utils.removeInvestorFromSharedPreference(mContext);
                //HomeActivity.totalAssets = null;
                //finish();
                break;
            case R.id.slipBtn:
                gotoTarget(AcceptPushSettingActivity.class, R.anim.activity_in_from_right, R.anim.activity_out_from_left,"设置");
                break;
        }

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }


    /*
    *安全退出的方法
     */
    public void cancelSafety(){
        loadding.showAs();
        LogOutRequest request=new LogOutRequest(mContext, new StringListener() {
            @Override
            public void ResponseListener(String result) {
                loadding.dismiss();
                LogOutResponse response=new LogOutResponse();
                if(response.getStatus(result)==200){
                    Cst.currentUser = null;
                    Utils.getInstances().removeInvestorFromSharedPreference(mContext);
                    HomeActivity.totalAssets = null;
                    mContext.finish();

                        Intent intent = new Intent(WebSocketService.ACTION);
                        intent.putExtra("cid", Cst.PUSH_CLIENT_ID);
                        sendBroadcast(intent);





                }else{
                    toast(response.getMessage(result));
                    loadding.dismiss();
                }
            }

            @Override
            public void ErrorListener(VolleyError volleyError) {
                loadding.dismiss();
                showErrorMsg(getString(R.string.net_error),navigationBar);
            }
        },Cst.currentUser.investorId);
        request.stringRequest();
    }
    @Override
    protected void onDestroy() {
        if(loadding != null && loadding.isShowing()){
            loadding.dismiss();
        }
        if(cancelDialog!=null&&cancelDialog.isShowing()){
            cancelDialog.dismiss();
        }
        super.onDestroy();
    }
}
