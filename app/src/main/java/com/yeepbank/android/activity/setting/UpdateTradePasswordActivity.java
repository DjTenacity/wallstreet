package com.yeepbank.android.activity.setting;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.android.volley.VolleyError;
import com.yeepbank.android.Cst;
import com.yeepbank.android.R;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.request.user.SureTradePasswordRequest;
import com.yeepbank.android.request.user.UpdateTradePasswdRequest;
import com.yeepbank.android.response.user.SureTradePasswordResponse;
import com.yeepbank.android.response.user.UpdateTradePasswdResponse;
import com.yeepbank.android.utils.LoadDialog;
import com.yeepbank.android.utils.Utils;
import com.yeepbank.android.widget.gridpasswordview.GridPasswordView;

/**
 * Created by dongxiaogang on 2015/10/8.
 *
 */
public class UpdateTradePasswordActivity extends BaseActivity implements View.OnClickListener,View.OnFocusChangeListener,GridPasswordView.OnPasswordChangedListener {
    private View navigationBar;
    private LinearLayout stepOneLayout,stepTwoLayout,stepThirdLayout;//
    private Animation animatorIn,animatorOut;
    private int currentShowLayout = 0;
    private TextView labelText;
    //第一个页面上的控件
    private EditText updateTradePasswordLoginName;
    private Button updateTradePasswordBtn;
    //第二个页面上的控件
    private GridPasswordView gridPasswordViewTwo;
    //第三个页面上的控件
    private GridPasswordView gridPasswordViewThird;
    @Override
    protected void initView() {
        navigationBar = findViewById(R.id.navigation_bar);
        stepOneLayout= (LinearLayout) findViewById(R.id.update_trade_password_one);
        stepTwoLayout=(LinearLayout) findViewById(R.id.update_trade_password_two);
        stepThirdLayout=(LinearLayout) findViewById(R.id.update_trade_password_third);

        stepTwoLayout.setVisibility(View.GONE);
        stepThirdLayout.setVisibility(View.GONE);
        //第一个页面上的控件
        updateTradePasswordBtn=(Button)stepOneLayout.findViewById(R.id.update_tradePassword_btn);
        updateTradePasswordBtn.setOnClickListener(this);
        updateTradePasswordLoginName=(EditText)stepOneLayout.findViewById(R.id.update_tradePassword_loginName);
        changeButtonStateWithValue(updateTradePasswordLoginName, updateTradePasswordBtn, R.drawable.update_trade_password, R.drawable.update_trade_password_not_active);
        //第二个页面上的控件
        gridPasswordViewTwo = (GridPasswordView)stepTwoLayout.findViewById(R.id.gv);
        gridPasswordViewTwo.setOnPasswordChangedListener(this);
        //第三个页面上的控件
        gridPasswordViewThird= (GridPasswordView)stepThirdLayout.findViewById(R.id.gv_sure);
        gridPasswordViewThird.setOnPasswordChangedListener(this);
    }

    @Override
    protected void fillData() {

    }

    @Override
    protected int getLayoutResources() {
        return R.layout.update_trade_password;
    }

    @Override
    protected View getNavigationBar() {
        return navigationBar;

    }
    @Override
    protected void initNavigationBar(View navigationBar) {
        View backLayout = navigationBar.findViewById(R.id.back_layout);
        labelText = (TextView) navigationBar.findViewById(R.id.label_text);

        labelText.setText("设置交易密码");







        backLayout.setOnClickListener(this);
    }

    @Override
    public LoadDialog getLoadDialog() {
        return null;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.update_tradePassword_btn:
                updateTradePasswd();

                break;
            case R.id.back_layout:
                if (currentShowLayout == 0){
                    finish();
                }else if(currentShowLayout == 1){
                    showPreFromTwotoOne(R.anim.activity_in_from_right, R.anim.activity_out_from_left);
                }else if(currentShowLayout == 2){
                    showPreFromThirdtoTwo(R.anim.activity_in_from_right, R.anim.activity_out_from_left);
                }
                break;
        }

    }
    private void showPreFromTwotoOne(int in,int out) {

        animatorOut = AnimationUtils.loadAnimation(mContext, out);
        animatorOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                stepTwoLayout.setVisibility(View.GONE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        stepTwoLayout.startAnimation(animatorOut);
        animatorIn = AnimationUtils.loadAnimation(mContext, in);
        stepOneLayout.startAnimation(animatorIn);
        stepOneLayout.setVisibility(View.VISIBLE);
        currentShowLayout =0;

    }
    private void showPreFromThirdtoTwo(int in,int out) {

        animatorOut = AnimationUtils.loadAnimation(mContext, out);
        animatorOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                gridPasswordViewThird.clearPassword();
                gridPasswordViewTwo.clearPassword();
                stepThirdLayout.setVisibility(View.GONE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        stepThirdLayout.startAnimation(animatorOut);
        animatorIn = AnimationUtils.loadAnimation(mContext, in);
        stepTwoLayout.startAnimation(animatorIn);
        stepTwoLayout.setVisibility(View.VISIBLE);
        currentShowLayout =1;

    }

    public void updateTradePasswd(){
        UpdateTradePasswdRequest request=new UpdateTradePasswdRequest(mContext,new StringListener() {
            @Override
            public void ResponseListener(String result) {
                UpdateTradePasswdResponse response=new UpdateTradePasswdResponse();
                if(response.getStatus(result)==200){
                    
                    showTwoLayout(R.anim.activity_in_from_left, R.anim.activity_out_from_right);
                }else{
                    toast(response.getMessage(result));
                }

            }

            @Override
            public void ErrorListener(VolleyError volleyError) {

            }
        },Cst.currentUser.investorId,updateTradePasswordLoginName.getText().toString().trim());
        request.stringRequest();
    }
    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }

    private void showTwoLayout(int in,int out) {

        animatorOut = AnimationUtils.loadAnimation(mContext, out);
        animatorOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                stepOneLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        stepOneLayout.startAnimation(animatorOut);
        animatorIn = AnimationUtils.loadAnimation(mContext,in);
        stepTwoLayout.startAnimation(animatorIn);
        stepTwoLayout.setVisibility(View.VISIBLE);
        currentShowLayout =1;
    }
    private void showThirdLayout(int in,int out) {

        animatorOut = AnimationUtils.loadAnimation(mContext, out);
        animatorOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                stepTwoLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        stepTwoLayout.startAnimation(animatorOut);
        animatorIn = AnimationUtils.loadAnimation(mContext,in);
        stepThirdLayout.startAnimation(animatorIn);
        stepThirdLayout.setVisibility(View.VISIBLE);
        currentShowLayout =2;
    }


    @Override
    public void onChanged(String psw) {

    }

    @Override
    public void onMaxLength(String psw) {
        if(currentShowLayout==1){
            if(gridPasswordViewTwo.getPassWord().length()==6){
                showThirdLayout(R.anim.activity_in_from_left, R.anim.activity_out_from_right);
            }

        }
        if(currentShowLayout==2&&gridPasswordViewThird.getPassWord().length()==6){
            if(gridPasswordViewTwo.getPassWord().equals(gridPasswordViewThird.getPassWord())){
                sureTradePassword();
            } else {
                toast("2次输入的密码不一致，请重新输入");



                showPreFromThirdtoTwo(R.anim.activity_in_from_right, R.anim.activity_out_from_left);

            }


        }




    }
    public void sureTradePassword() {
        if (Cst.currentUser != null) {
            SureTradePasswordRequest request = new SureTradePasswordRequest(mContext,new StringListener() {
                @Override
                public void ResponseListener(String result) {
                    SureTradePasswordResponse response = new SureTradePasswordResponse();
                    if (response.getStatus(result) == 200) {

                        toast("设置交易密码成功");

                        Cst.currentUser.hastxnPwd=true;
                        Utils.getInstances().putInvestorToSharedPreference(mContext, Cst.currentUser);
                        mContext.finish();
                    }else{
                        toast(response.getMessage(result));
                        showPreFromThirdtoTwo(R.anim.activity_in_from_right, R.anim.activity_out_from_left);
                    }

                }

                @Override
                public void ErrorListener(VolleyError volleyError) {

                }
            },Cst.currentUser.investorId,gridPasswordViewTwo.getPassWord().toString().trim(),gridPasswordViewThird.getPassWord().toString().trim());
            request.stringRequest();
        }
    }
}
