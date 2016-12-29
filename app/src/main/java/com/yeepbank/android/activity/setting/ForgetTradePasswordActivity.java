package com.yeepbank.android.activity.setting;

import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.yeepbank.android.Cst;
import com.yeepbank.android.R;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.request.user.*;
import com.yeepbank.android.response.user.*;
import com.yeepbank.android.utils.LoadDialog;
import com.yeepbank.android.widget.gridpasswordview.GridPasswordView;

/**
 * Created by xiaogang.dong on 2015/11/28.
 */
public class ForgetTradePasswordActivity extends BaseActivity implements View.OnClickListener,GridPasswordView.OnPasswordChangedListener{
    private View navigationBar;
    private LinearLayout stepOneLayout,stepTwoLayout,stepThirdLayout,stepFourLayout;

    private EditText registerPhone;//第一步注册的手机号
    private Button sendMsgCodeBtn,getMsgExamCodeBtn,settingOkBtn;//获取短信验证码按钮，第二步获取验证码,设置完成按钮
    private Animation animatorIn,animatorOut;
    private int currentShowLayout = 0;
    private EditText examCodeEdit,newPassEdit,sureNewPassEdit;//短信验证码,新密码,确认新密码
    private CountDownTimer downTimer;
    //第二个页面上的控件
    private TextView textView;
    private EditText forgetTradePasswordExamCode;//填写验证码
    private Button forgetTradePasswordGetmsgExamCodeTwo;//获取验证码的按键
    private EditText forgetLoginPassword;//输入登录密码
    private Button forgetSettingNewTradepasswordBtn;
    //第三个页面上的控件
    private GridPasswordView forgetGv;
    //第四个页面上的控件
    private GridPasswordView forgetGvSure;
    private TextView labelText;
    @Override
    protected void initView() {
        navigationBar = findViewById(R.id.navigation_bar);

        stepOneLayout = (LinearLayout) findViewById(R.id.forget_trade_password_one);
        stepTwoLayout = (LinearLayout) findViewById(R.id.forget_trade_password_two);
        stepThirdLayout = (LinearLayout) findViewById(R.id.forget_trade_password_third);
        stepFourLayout = (LinearLayout) findViewById(R.id.forget_trade_password_four);

        stepTwoLayout.setVisibility(View.GONE);
        stepThirdLayout.setVisibility(View.GONE);
        stepFourLayout.setVisibility(View.GONE);
//第一个页面上的控件
        registerPhone = (EditText) stepOneLayout.findViewById(R.id.forget_tradepassword_register_phone);
        sendMsgCodeBtn = (Button) stepOneLayout.findViewById(R.id.forget_tradepassword_send_msg_code_btn);
        sendMsgCodeBtn.setOnClickListener(this);
        changeButtonStateWithValue(registerPhone, sendMsgCodeBtn, R.drawable.send_exam_code_activated_btn, R.drawable.send_exam_code_not_activated_btn);

//第二个页面上的控件
        textView= (TextView) stepTwoLayout.findViewById(R.id.forget_tradePassword_tv);

        forgetTradePasswordExamCode= (EditText) stepTwoLayout.findViewById(R.id.forget_tradepassword_exam_code);
        forgetTradePasswordGetmsgExamCodeTwo= (Button) stepTwoLayout.findViewById(R.id.forget_tradepassword_get_msg_exam_code_two);
        forgetTradePasswordGetmsgExamCodeTwo.setOnClickListener(this);
        forgetLoginPassword= (EditText) stepTwoLayout.findViewById(R.id.forget_login_password);
        forgetSettingNewTradepasswordBtn=(Button) stepTwoLayout.findViewById(R.id.forget_setting_new_tradepassword_btn);
        forgetSettingNewTradepasswordBtn.setOnClickListener(this);

//第三个页面上的控件
        forgetGv= (GridPasswordView)stepThirdLayout.findViewById(R.id.forget_gv);
        forgetGv.setOnPasswordChangedListener(this);


 //第四个页面上的控件
        forgetGvSure= (GridPasswordView) stepFourLayout.findViewById(R.id.forget_gv_sure);
        forgetGvSure.setOnPasswordChangedListener(this);


        textChange tc1=new textChange();
        forgetTradePasswordExamCode.addTextChangedListener(tc1);
        forgetLoginPassword.addTextChangedListener(tc1);
    }
    class textChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            boolean sign1=forgetTradePasswordExamCode.getText().toString().trim().length()>0;
            boolean sign2=forgetLoginPassword.getText().toString().trim().length()>0;

            if(sign1&&sign2){
                forgetSettingNewTradepasswordBtn.setBackgroundResource(R.drawable.setting_trade_password);
                forgetSettingNewTradepasswordBtn.setEnabled(true);
            }else{
                forgetSettingNewTradepasswordBtn.setBackgroundResource(R.drawable.setting_trade_password_not_active);
                forgetSettingNewTradepasswordBtn.setEnabled(false);
            }
        }
    }

    @Override
    protected void fillData() {

    }

    @Override
    protected int getLayoutResources() {
        return R.layout.forget_trade_password;
    }

    @Override
    protected View getNavigationBar() {
        return navigationBar;
    }

    @Override
    protected void initNavigationBar(View navigationBar) {
       View backLayout = navigationBar.findViewById(R.id.back_layout);
        labelText = (TextView) navigationBar.findViewById(R.id.label_text);

            labelText.setText("忘记交易密码");







        backLayout.setOnClickListener(this);
    }

    @Override
    public LoadDialog getLoadDialog() {
        return loadding;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.forget_tradepassword_send_msg_code_btn:


                getMsgCode();

                break;
            case R.id.back_layout:
                if (currentShowLayout == 0){
                    finish();
                }else if(currentShowLayout == 1){
                    showPreFromTwotoOne(R.anim.activity_in_from_right, R.anim.activity_out_from_left);
                    downTimer.cancel();
                }else if(currentShowLayout == 2){
                    showPreFromThirdtoTwo(R.anim.activity_in_from_right, R.anim.activity_out_from_left);
                }else if(currentShowLayout == 3){
                    showPreFromFourtothird(R.anim.activity_in_from_right, R.anim.activity_out_from_left);
                }
                break;
            case R.id.forget_tradepassword_get_msg_exam_code_two:

                timeDown();
                getMsgCode();
                break;

            case R.id.forget_setting_new_tradepassword_btn:
                updateTradePasswd();
                break;

        }
    }
    public void timeDown(){

            forgetTradePasswordGetmsgExamCodeTwo.setEnabled(false);
            forgetTradePasswordGetmsgExamCodeTwo.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.exam_code_timer_down));
        if(downTimer != null){
            downTimer.cancel();
            downTimer = null;
        }
        downTimer = new CountDownTimer(60000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    forgetTradePasswordGetmsgExamCodeTwo.setText(millisUntilFinished / 1000 + "秒后重新获取");
                }

                @Override
                public void onFinish() {
                    forgetTradePasswordGetmsgExamCodeTwo.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.get_msg_exam_code));
                    forgetTradePasswordGetmsgExamCodeTwo.setText("");
                    forgetTradePasswordGetmsgExamCodeTwo.setEnabled(true);
                }
            };
            downTimer.start();

    }

    //校验登录密码与短信验证码是否正确
    public void updateTradePasswd(){
        CheckLoginPasswordAndSmsPasswdRequest request=new CheckLoginPasswordAndSmsPasswdRequest(mContext, new StringListener() {
            @Override
            public void ResponseListener(String result) {
                CheckLoginPasswordAndSmsPasswdReponse response=new CheckLoginPasswordAndSmsPasswdReponse();
                if(response.getStatus(result)==200){
                    showToThirdLayout(R.anim.activity_in_from_left, R.anim.activity_out_from_right);
                }else{
                    toast(response.getMessage(result));
                }

            }

            @Override
            public void ErrorListener(VolleyError volleyError) {

            }
        },Cst.currentUser.investorId,forgetTradePasswordExamCode.getText().toString().trim(),forgetLoginPassword.getText().toString().trim());
        request.stringRequest();

    }

    private void showPreFromFourtothird(int in,int out) {
        labelText.setText("设置交易密码");
        animatorOut = AnimationUtils.loadAnimation(mContext, out);
        animatorOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                forgetGv.clearPassword();
                forgetGvSure.clearPassword();

                stepFourLayout.setVisibility(View.GONE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        stepFourLayout.startAnimation(animatorOut);
        animatorIn = AnimationUtils.loadAnimation(mContext, in);
        stepThirdLayout.startAnimation(animatorIn);
        stepThirdLayout.setVisibility(View.VISIBLE);
        currentShowLayout = 2;

    }
    private void showPreFromThirdtoTwo(int in,int out) {
        labelText.setText("设置新交易密码");
        animatorOut = AnimationUtils.loadAnimation(mContext, out);
        animatorOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
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
    private void showPreFromTwotoOne(int in,int out) {
        labelText.setText("忘记新交易密码");
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



    /*
    * 获取短信验证码
    * */
    private void getMsgCode() {
        loadding.showAs();
        ForgetTradePasswordMsgCodeRequest request = new ForgetTradePasswordMsgCodeRequest(mContext, new StringListener() {
            @Override
            public void ResponseListener(String result) {
                dismiss();
                ForgetTradePasswordMsgCodeResponse response = new ForgetTradePasswordMsgCodeResponse();
                if(response.getStatus(result) == 200){
                    if(currentShowLayout == 0){
                        showNext(R.anim.activity_in_from_left, R.anim.activity_out_from_right);

                    }else if(currentShowLayout == 1){
                        //forgetTradePasswordGetmsgExamCodeTwo.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.get_msg_exam_code));
                        //forgetTradePasswordGetmsgExamCodeTwo.setText("");
                        //forgetTradePasswordGetmsgExamCodeTwo.setEnabled(true);
                    }
                }else {
                    toast(response.getMessage(result));
                }
            }
            @Override
            public void ErrorListener(VolleyError volleyError) {
                dismiss();
                showErrorMsg(getString(R.string.net_error),navigationBar);
            }
        },Cst.currentUser.investorId,registerPhone.getText().toString().trim());
        request.stringRequest();
    }
    private void showNext(int in,int out) {
        labelText.setText("设置新交易密码");
        textView.setText("短信验证码已发送至" + registerPhone.getText());
        animatorOut = AnimationUtils.loadAnimation(mContext, out);
        timeDown();
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
        currentShowLayout = 1;
    }
    private void showToThirdLayout(int in,int out) {
        labelText.setText("设置交易密码");
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
        currentShowLayout = 2;
    }
    private void showToFourLayout(int in,int out) {
        labelText.setText("确认交易密码");
        animatorOut = AnimationUtils.loadAnimation(mContext, out);
        animatorOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                stepThirdLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        stepThirdLayout.startAnimation(animatorOut);
        animatorIn = AnimationUtils.loadAnimation(mContext,in);
        stepFourLayout.startAnimation(animatorIn);
        stepFourLayout.setVisibility(View.VISIBLE);
        currentShowLayout =3;
    }

    @Override
    public void onChanged(String psw) {

    }

    @Override
    public void onMaxLength(String psw) {
        if(currentShowLayout==2) {
            if (forgetGv.getPassWord().length() == 6) {

                showToFourLayout(R.anim.activity_in_from_left, R.anim.activity_out_from_right);
            }
        }else if (currentShowLayout==3) {
            if (forgetGvSure.getPassWord().length() == 6 && forgetGvSure.getPassWord().equals(forgetGv.getPassWord())) {
                ForgetTradePasswordRequest request = new ForgetTradePasswordRequest(mContext, new StringListener() {
                    @Override
                    public void ResponseListener(String result) {
                        ForgetTradePasswordResponse response = new ForgetTradePasswordResponse();
                        if (response.getStatus(result) == 200) {
                            toast("修改交易密码成功");
                            mContext.finish();
                        }else{
                            toast(response.getMessage(result));
                            showPreFromFourtothird(R.anim.activity_in_from_right, R.anim.activity_out_from_left);

                        }

                    }

                    @Override
                    public void ErrorListener(VolleyError volleyError) {

                    }
                }, Cst.currentUser.investorId, forgetTradePasswordExamCode.getText().toString().trim(), forgetLoginPassword.getText().toString().trim(), Cst.currentUser.idNo, forgetGvSure.getPassWord().trim(), forgetGvSure.getPassWord().trim());
                request.stringRequest();
            }else{
                toast("2次密码不一致，请重新输入");
                showPreFromFourtothird(R.anim.activity_in_from_right, R.anim.activity_out_from_left);
            }
        }

    }
    @Override
    protected void onDestroy() {
        if(loadding != null && loadding.isShowing()){
            loadding.dismiss();
        }
        super.onDestroy();
    }
}
