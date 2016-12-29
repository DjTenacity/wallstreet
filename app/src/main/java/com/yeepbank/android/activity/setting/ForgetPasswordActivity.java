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
import com.yeepbank.android.R;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.request.user.ForgetPasswordMsgCodeRequest;
import com.yeepbank.android.request.user.SettingNewPassRequest;
import com.yeepbank.android.response.user.ForgetPasswordMsgCodeResponse;
import com.yeepbank.android.response.user.SettingNewPassResponse;
import com.yeepbank.android.utils.LoadDialog;

/**
 * Created by WW on 2015/11/20.
 */
public class ForgetPasswordActivity extends BaseActivity implements View.OnClickListener{
    private View navigationBar;
    private LinearLayout stepOneLayout,stepTwoLayout;

    private EditText registerPhone;private TextView sendPhoneText;//第一步注册的手机号，第二步显示的手机号
    private Button sendMsgCodeBtn,getMsgExamCodeBtn,settingOkBtn;//获取短信验证码按钮，第二步获取验证码,设置完成按钮
    private Animation animatorIn,animatorOut;
    private int currentShowLayout = 0;
    private EditText examCodeEdit,newPassEdit,sureNewPassEdit;//短信验证码,新密码,确认新密码
    private CountDownTimer downTimer;
    @Override
    protected void initView() {
        navigationBar = findViewById(R.id.navigation_bar);
        stepOneLayout = (LinearLayout) findViewById(R.id.forget_one);
        stepTwoLayout = (LinearLayout) findViewById(R.id.forget_two);
        stepTwoLayout.setVisibility(View.GONE);

        registerPhone = (EditText) stepOneLayout.findViewById(R.id.register_phone);
        //registerPhone.setText("13521434749");
        sendMsgCodeBtn = (Button) stepOneLayout.findViewById(R.id.send_msg_code_btn);
        sendMsgCodeBtn.setOnClickListener(this);
        changeButtonStateWithValue(registerPhone, sendMsgCodeBtn, R.drawable.send_exam_code_activated_btn, R.drawable.send_exam_code_not_activated_btn);

        sendPhoneText = (TextView) stepTwoLayout.findViewById(R.id.send_phone);
        getMsgExamCodeBtn = (Button) stepTwoLayout.findViewById(R.id.get_msg_exam_code_two);
        getMsgExamCodeBtn.setOnClickListener(this);
        examCodeEdit = (EditText) stepTwoLayout.findViewById(R.id.exam_code);
        newPassEdit = (EditText) stepTwoLayout.findViewById(R.id.new_pass);
        sureNewPassEdit = (EditText) stepTwoLayout.findViewById(R.id.sure_new_pass);
        sureNewPassEdit.addTextChangedListener(textWatcher);
        settingOkBtn = (Button) findViewById(R.id.setting_ok_btn);
        settingOkBtn.setOnClickListener(this);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(!newPassEdit.getText().toString().contains(" ")&&newPassEdit.getText().toString().length() >=8 &&
                    !sureNewPassEdit.getText().toString().contains(" ")&&sureNewPassEdit.getText().toString().length() >=8){
                settingOkBtn.setBackgroundResource(R.drawable.reset_ok_activated_btn);
                settingOkBtn.setEnabled(true);
            }else {
                settingOkBtn.setBackgroundResource(R.drawable.reset_ok_not_activated_btn);
                settingOkBtn.setEnabled(false);
            }
        }
    };


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
        View backLayout = navigationBar.findViewById(R.id.back_layout);
        TextView labelText = (TextView) navigationBar.findViewById(R.id.label_text);
        labelText.setText("忘记密码");
        backLayout.setOnClickListener(this);
    }

    @Override
    public LoadDialog getLoadDialog() {
        return loadding;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.send_msg_code_btn:
                getMsgCode();
                break;
            case R.id.back_layout:
                if (currentShowLayout == 0){
                    finish();
                }else {
                    showPre(R.anim.activity_in_from_right, R.anim.activity_out_from_left);
                }
                break;
            case R.id.get_msg_exam_code_two:
                downTime();
                getMsgCode();
                break;
            case R.id.setting_ok_btn:
                settingNewPass();
                break;

        }
    }

    private void downTime() {
        getMsgExamCodeBtn.setEnabled(false);
        getMsgExamCodeBtn.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.exam_code_timer_down));
        if(downTimer != null){
            downTimer.cancel();
            downTimer = null;
        }
        downTimer = new CountDownTimer(60000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                getMsgExamCodeBtn.setText(millisUntilFinished / 1000 +"秒后重新获取");
            }

            @Override
            public void onFinish() {
                getMsgExamCodeBtn.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.get_msg_exam_code));
                getMsgExamCodeBtn.setText("");
                getMsgExamCodeBtn.setEnabled(true);
            }
        };
        downTimer.start();
    }

    /*
    * 设置新密码
    * */
    private void settingNewPass() {
        String phone = registerPhone.getText().toString();
        String examCode = examCodeEdit.getText().toString();
        String newPass = newPassEdit.getText().toString();
        String sureNewPass = sureNewPassEdit.getText().toString();
        if(examCode.trim().equals("")){
            showErrorMsg("验证码不能为空",navigationBar);
            return;
        }
        if(!newPass.trim().equals(sureNewPass.trim())){
            showErrorMsg("两次密码不一致,重新输入",navigationBar);
            return;
        }
        loadding.showAs();
        SettingNewPassRequest request = new SettingNewPassRequest(mContext, new StringListener() {
            @Override
            public void ResponseListener(String result) {
                dismiss();
                SettingNewPassResponse response = new SettingNewPassResponse();
                if(response.getStatus(result) == 200){
                    toast("密码设置成功");
                    finish();
                }else {
                    toast("密码设置失败");
                }
            }

            @Override
            public void ErrorListener(VolleyError volleyError) {
                dismiss();
                showErrorMsg(getString(R.string.net_error),navigationBar);
            }
        },phone,examCode,newPass,sureNewPass);
        request.stringRequest();
    }


    private void showPre(int in,int out) {

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
        stepOneLayout.startAnimation(animatorIn);
        stepOneLayout.setVisibility(View.VISIBLE);
        currentShowLayout = 0;
    }

    /*
    * 获取短信验证码
    * */
    private void getMsgCode() {
        loadding.showAs();
        ForgetPasswordMsgCodeRequest request = new ForgetPasswordMsgCodeRequest(mContext, new StringListener() {
            @Override
            public void ResponseListener(String result) {
                dismiss();
                ForgetPasswordMsgCodeResponse response = new ForgetPasswordMsgCodeResponse();
                if(response.getStatus(result) == 200){
                    if(currentShowLayout == 0){
                        sendPhoneText.setText(registerPhone.getText());
                        showNext(R.anim.activity_in_from_left, R.anim.activity_out_from_right);
                        downTime();
                    }

                }else {
                    toast(response.getMessage(result));
                }
            }

            @Override
            public void ErrorListener(VolleyError volleyError) {
                dismiss();
                showErrorMsg(getString(R.string.net_error),null);
            }
        },registerPhone.getText().toString().trim());
        request.stringRequest();
    }

    private void showNext(int in,int out) {

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
        currentShowLayout = 1;
    }
    @Override
    protected void onDestroy() {
        if(loadding != null && loadding.isShowing()){
            loadding.dismiss();
        }
        super.onDestroy();
    }
}
