package com.yeepbank.android.activity.setting;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import com.android.volley.VolleyError;
import com.yeepbank.android.Cst;
import com.yeepbank.android.R;
import com.yeepbank.android.activity.account.RechargeActivity;
import com.yeepbank.android.activity.account.WithDrawalActivity;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.request.user.HasRealNameRequest;
import com.yeepbank.android.response.user.HasRealNameResponse;
import com.yeepbank.android.utils.LoadDialog;
import com.yeepbank.android.utils.Utils;
import com.yeepbank.android.widget.LayoutInSetting;

/**
 * Created by 董晓刚 on 2015/9/22.
 */
public class AccountAndSecurityActivity extends BaseActivity implements View.OnClickListener,View.OnFocusChangeListener {

    private LayoutInSetting updateTradePassword;//修改交易密码
    private LayoutInSetting updateLoginPassword;//修改登录密码
    private LayoutInSetting realnameAuthentication;//实名认证
    private LayoutInSetting myAccountName;//我的账户名
    private LayoutInSetting investLevel;//投资级别
    private LinearLayout investLevelContent;
    private View navigationBar;
    private LoadDialog alertDialog,msgDialog;
    @Override
    protected void initView() {
        navigationBar=findViewById(R.id.action_bar);
        updateTradePassword=(LayoutInSetting)findViewById(R.id.update_trade_password);
        updateTradePassword.setOnClickListener(this);

        realnameAuthentication=(LayoutInSetting)findViewById(R.id.realnameAuthenticationId);
        updateLoginPassword=(LayoutInSetting)findViewById(R.id.update_loginPassword);
        updateLoginPassword.setOnClickListener(this);
        myAccountName=(LayoutInSetting)findViewById(R.id.my_accountName);
        investLevel = (LayoutInSetting) findViewById(R.id.invest_level);
        investLevelContent = (LinearLayout) findViewById(R.id.invest_level_content);
        realnameAuthentication.setOnClickListener(this);
        alertDialog = new LoadDialog(mContext, R.style.dialog, false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        },0).setTitle("提示").setMessage("请先登录").setSureBtn("我知道了");

        msgDialog = new LoadDialog(mContext, R.style.dialog, false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msgDialog.dismiss();
                gotoTarget(RealNameAuthenticationActivity.class, R.anim.activity_in_from_right, R.anim.activity_out_from_left, "");
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msgDialog.dismiss();
            }
        },0);
    }
    @Override
    protected void fillData() {
        if(Cst.currentUser!=null){
            myAccountName.setValue(Cst.currentUser.loginName);
        }
    }
    @Override
    protected void onResume() {
        if(Cst.currentUser.idAuthFlag!=null){
            if(Cst.currentUser.idAuthFlag.trim().equals("Y")) {
                realnameAuthentication.setValue("已认证");
                realnameAuthentication.setColor("#999999");
            }else if(Cst.currentUser.idAuthFlag.trim().equals("P")) {
                realnameAuthentication.setValue("审核中");
                realnameAuthentication.setColor("#999999");
            }else if(Cst.currentUser.idAuthFlag.trim().equals("N")) {
                realnameAuthentication.setValue("重新认证");
                realnameAuthentication.setColor("#3887be");
            }else {
                realnameAuthentication.setValue("去认证");
                realnameAuthentication.setColor("#3887be");
            }
        }
        if(Cst.currentUser.hastxnPwd){
            updateTradePassword.setValue("修改");
        }else{
            updateTradePassword.setValue("设置");
        }

        if (Cst.currentUser.investLevel != null && !Cst.currentUser.investLevel.trim().equals("")){
            investLevelContent.setVisibility(View.VISIBLE);
            investLevel.setValue(Cst.currentUser.investLevel);
        }else {
            investLevelContent.setVisibility(View.GONE);
            investLevel.setValue("");
        }
        super.onResume();
    }

    @Override
    protected int getLayoutResources() {
        return R.layout.account_and_security;
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




        switch(v.getId()){
            case R.id.update_trade_password:
                if(Cst.currentUser.idAuthFlag!=null&&Cst.currentUser.idAuthFlag.equals("Y")){
                    if(Cst.currentUser.hastxnPwd){
                        gotoTarget(ForgetTradePasswordActivity.class,R.anim.activity_in_from_right,R.anim.activity_out_from_left,null);
                    }else{
                        gotoTarget(UpdateTradePasswordActivity.class,R.anim.activity_in_from_right,R.anim.activity_out_from_left,null);
                    }
                }else{
                    hasRealName();
                }
                break;
            case R.id.realnameAuthenticationId:
                if(Cst.currentUser.idAuthFlag != null &&(Cst.currentUser.idAuthFlag.trim().equals("Y")||Cst.currentUser.idAuthFlag.trim().equals("P"))){
                    return;
                }
                gotoTarget(RealNameAuthenticationActivity.class,R.anim.activity_in_from_right,R.anim.activity_out_from_left,null);
                break;
            case R.id.update_loginPassword:
                gotoTarget(UpdateLoginPasswordActivity.class,R.anim.activity_in_from_right,R.anim.activity_out_from_left,null);
        }


    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }
    private void hasRealName() {
        HasRealNameRequest request = new HasRealNameRequest(mContext, new StringListener() {
            @Override
            public void ResponseListener(String result) {
                HasRealNameResponse response = new HasRealNameResponse();
                if(response.getStatus(result) == 200){
                    Cst.currentUser.idAuthFlag = response.getObject(result);
                    Cst.currentUser.idNo =response.getIdNO(result);
                    if(Cst.currentUser.idAuthFlag!= null && Cst.currentUser.idAuthFlag.equals("Y")){
                        Utils.getInstances().putInvestorToSharedPreference(mContext, Cst.currentUser);
                    }else if(Cst.currentUser.idAuthFlag!= null && Cst.currentUser.idAuthFlag.equals("P")){
                        alertDialog.setMessage("您的实名认证信息正在审核,暂时无法投资/提现/充值,我们会加紧审核");
                        alertDialog.showAs();
                    }else if(Cst.currentUser.idAuthFlag!= null && Cst.currentUser.idAuthFlag.equals("N")){
                        msgDialog.setMessage("您的实名认证信息审核未通过,是否重新认证");
                        msgDialog.setSureBtn("重新认证");
                        msgDialog.setCancelBtn("取消");
                        msgDialog.showAs();
                    }else {
                        msgDialog.setMessage("需要完成实名认证");
                        msgDialog.setSureBtn("去认证");
                        msgDialog.setCancelBtn("取消");
                        msgDialog.showAs();
                    }
                }else {
                    toast(response.getMessage(result));
                }
            }

            @Override
            public void ErrorListener(VolleyError volleyError) {
                showErrorMsg(getString(R.string.net_error), null);
            }
        },Cst.currentUser.investorId);
        request.stringRequest();
    }
}
