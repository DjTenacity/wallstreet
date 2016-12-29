package com.yeepbank.android.server;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.android.volley.VolleyError;
import com.yeepbank.android.Cst;
import com.yeepbank.android.R;
import com.yeepbank.android.WebSocketService;
import com.yeepbank.android.activity.business.HomeActivity;
import com.yeepbank.android.activity.setting.ForgetPasswordActivity;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.model.Web;
import com.yeepbank.android.model.business.EdmAppCount;
import com.yeepbank.android.model.business.EdmOverview;
import com.yeepbank.android.model.user.Investor;
import com.yeepbank.android.model.user.TotalAssets;
import com.yeepbank.android.request.user.ExamCodeRequest;
import com.yeepbank.android.request.user.LoginRequest;
import com.yeepbank.android.request.user.RegisterRequest;
import com.yeepbank.android.response.user.ExamCodeResponse;
import com.yeepbank.android.response.user.LoginAndRegisterResponse;
import com.yeepbank.android.utils.Utils;

import java.util.Timer;
import java.util.TimerTask;



/**
 * Created by WW on 2015/10/21.
 */
public class LoginAndRegisterServer implements View.OnClickListener,View.OnFocusChangeListener{
    private View view;
    private Context mContext;
    private View loginLayout,registerLayout;
    private TextView loginText;
    private EditText userNameEdit,passEdit,recommendCodeEditText;
    private boolean hideOrShowRecommendCodeEditText = false;
    private Button getMsgExamCodeBtn;
    private ImageButton exitLoginBtn,exitRegisterBtn;
    private Button registerBtn,loginBtn;
    private LinearLayout footerLayout;
    private Investor investor;//投资者信息
    private TotalAssets totalAssets;//账户信息
    private EdmAppCount edmAppCount;//天天盈信息
    private EdmOverview edmOverview;//天天盈买入信息
    private TextView forgetPasswordText;//忘记密码
    private ImageView  openImg;
    private CountDownTimer downTimer;

    private EditText phoneRegister,examCodeRegister,passRegister,recommendCodeRegister;
    private Handler msgHandler;
    private TextView serviceAgreementText,privacyClauseText;//服务协议，隐私条款
    private LinearLayout loginDialog,registerDialog;
    private LinearLayout loginDoingLayout,registerDoingLayout;
    private ImageView loginRightImg,registerRightImg;

    public LoginAndRegisterServer(Context context,View view,Handler handler) {
        this.view = view;
        this.mContext = context;
        msgHandler = handler;
        initView();
    }
    /*public Handler conHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
           Intent intent = new Intent(WebSocketService.ACTION);
           intent.putExtra("cid", Cst.PUSH_CLIENT_ID);
           mContext.sendBroadcast(intent);
        }
    };*/


    private void initView() {
        loginLayout = view.findViewById(R.id.login_page);
        loginDialog = (LinearLayout) loginLayout.findViewById(R.id.login_dialog);
        loginDialog.setVisibility(View.GONE);
        loginDoingLayout = (LinearLayout) loginDialog.findViewById(R.id.login_doing);
        loginRightImg = (ImageView) loginDialog.findViewById(R.id.login_right);

        registerLayout =  view.findViewById(R.id.register_page);
        registerDialog = (LinearLayout) registerLayout.findViewById(R.id.register_dialog);
        registerDialog.setVisibility(View.GONE);
        registerDoingLayout = (LinearLayout) registerDialog.findViewById(R.id.register_doing);
        registerRightImg = (ImageView) registerDialog.findViewById(R.id.register_right);

        passEdit = (EditText) view.findViewById(R.id.pass_login);
        passEdit.addTextChangedListener(loginWatcher);
        loginText = (TextView) registerLayout.findViewById(R.id.reg_login);
        TextView registerText = (TextView) loginLayout.findViewById(R.id.go_register);
        registerText.setOnClickListener(this);
        loginText.setOnClickListener(this);
        exitLoginBtn = (ImageButton) view.findViewById(R.id.exist_login);
        exitLoginBtn.setOnClickListener(this);

        exitRegisterBtn = (ImageButton) view.findViewById(R.id.exist_register);
        exitRegisterBtn.setOnClickListener(this);
        view.findViewById(R.id.recommend_code_layout).setOnClickListener(this);
        recommendCodeEditText = (EditText) view.findViewById(R.id.recommend_code);
        getMsgExamCodeBtn = (Button) view.findViewById(R.id.get_msg_exam_code);
        getMsgExamCodeBtn.setOnClickListener(this);
        loginBtn = (Button) view.findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(this);
        userNameEdit = (EditText) view.findViewById(R.id.user_name);
        if(Utils.getInstances().getUserNameFromSharedPreference(mContext)!=null){
            userNameEdit.setText(Utils.getInstances().getUserNameFromSharedPreference(mContext));
        }else{
            userNameEdit.setText("");
        }
        userNameEdit.addTextChangedListener(loginWatcher);

        forgetPasswordText = (TextView) loginLayout.findViewById(R.id.forget_password);
        forgetPasswordText.setOnClickListener(this);

        //注册页面控件
        phoneRegister = (EditText) registerLayout.findViewById(R.id.register_phone);
        phoneRegister.setOnFocusChangeListener(this);
        phoneRegister.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(downTimer != null){
                    downTimer.cancel();
                    getMsgExamCodeBtn.setText("");
                }
                if (s.toString().length() == 11) {
                    getMsgExamCodeBtn.setEnabled(true);


                    getMsgExamCodeBtn.setBackgroundResource(R.drawable.get_msg_exam_code);
                } else {
                    getMsgExamCodeBtn.setEnabled(false);
                    getMsgExamCodeBtn.setBackgroundResource(R.drawable.get_msg_exam_code_unable);
                }
                if (!passRegister.getText().toString().trim().equals("") &&
                        !phoneRegister.getText().toString().trim().equals("") &&
                        !examCodeRegister.getText().toString().trim().equals("")) {
                    registerBtn.setBackgroundResource(R.drawable.register_visible);
                    registerBtn.setEnabled(true);
                } else {
                    registerBtn.setBackgroundResource(R.drawable.register_invisible);
                    registerBtn.setEnabled(false);
                }
            }
        });
        examCodeRegister = (EditText) registerLayout.findViewById(R.id.msg_exam_code);
        examCodeRegister.setOnFocusChangeListener(this);
        examCodeRegister.addTextChangedListener(watcher);
        passRegister = (EditText) registerLayout.findViewById(R.id.password_register);
        passRegister.setOnFocusChangeListener(this);
        passRegister.addTextChangedListener(watcher);
        recommendCodeRegister = (EditText) registerLayout.findViewById(R.id.recommend_code);
        registerBtn = (Button) registerLayout.findViewById(R.id.register_btn);
        registerBtn.setOnClickListener(this);
        footerLayout = (LinearLayout) registerLayout.findViewById(R.id.register_footer);
        openImg = (ImageView) registerLayout.findViewById(R.id.open);
        /*
        * TODO 这里做过修改
        * */
        recommendCodeEditText.setVisibility(View.VISIBLE);


        hideOrShowRecommendCodeEditText = false;
        Animation animation = new RotateAnimation(0,180,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        animation.setDuration(0);
        animation.setFillAfter(true);
        openImg.startAnimation(animation);

        serviceAgreementText = (TextView) view.findViewById(R.id.service_agreement);
        serviceAgreementText.setOnClickListener(this);
        serviceAgreementText.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        serviceAgreementText.getPaint().setAntiAlias(true);//抗锯齿
        privacyClauseText = (TextView) view.findViewById(R.id.privacy_clause);
        privacyClauseText.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        privacyClauseText.getPaint().setAntiAlias(true);//抗锯齿
        privacyClauseText.setOnClickListener(this);
    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(!passRegister.getText().toString().trim().equals("")&&
                    !phoneRegister.getText().toString().trim().equals("")&&
                    !examCodeRegister.getText().toString().trim().equals("")){
                registerBtn.setBackgroundResource(R.drawable.register_visible);
                registerBtn.setEnabled(true);
            }else {
                registerBtn.setBackgroundResource(R.drawable.register_invisible);
                registerBtn.setEnabled(false);
            }
        }
    };

    private TextWatcher loginWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(!userNameEdit.getText().toString().trim().equals("")&&
                    !passEdit.getText().toString().trim().equals("")){
                loginBtn.setBackgroundResource(R.drawable.login_visible);
                loginBtn.setEnabled(true);
            }else {
                loginBtn.setBackgroundResource(R.drawable.login_invisible);
                loginBtn.setEnabled(false);
            }
        }
    };


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.go_register://从登录页面切换到注册页面
                changeRegister();
                break;
            case R.id.reg_login://从注册页面切换到登录页面
                changeLogin();
                break;
            case R.id.exist_login://退出登录框
            case R.id.exist_register:
                Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.activity_out_from_bottom);
//                view.findViewById(R.id.contentView).setAnimation(animation);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        ((BaseActivity) mContext).cancelMsg();
                        animation = null;
                        if(Cst.currentUser != null){
                            ((BaseActivity)mContext).loginOutTimeHandler.sendEmptyMessage(Cst.CMD.LOGIN_FRAME_CLOSED);
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                view.findViewById(R.id.contentView).startAnimation(animation);

                break;
            case R.id.recommend_code_layout://显示推荐吗
                hideOrShowRecommendCodeEditText = !hideOrShowRecommendCodeEditText;
                if(hideOrShowRecommendCodeEditText){
//                    Animation showAnimation = AnimationUtils.loadAnimation(mContext,R.anim.activity_in_from_up);
//                    showAnimation.setAnimationListener(new Animation.AnimationListener() {
//                        @Override
//                        public void onAnimationStart(Animation animation) {
//
//                        }
//
//                        @Override
//                        public void onAnimationEnd(Animation animation) {
//                            recommendCodeEditText.setVisibility(View.VISIBLE);
//                        }
//
//                        @Override
//                        public void onAnimationRepeat(Animation animation) {
//
//                        }
//                    });
//                    Animation downAnimation = AnimationUtils.loadAnimation(mContext,R.anim.activity_in_from_up);
//                    //loginText.startAnimation(downAnimation);
//                    footerLayout.startAnimation(showAnimation);

                    Animation roatImg = new RotateAnimation(180,0,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                    roatImg.setDuration(200);
                    roatImg.setFillAfter(true);
                    openImg.startAnimation(roatImg);

                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Message msg = setHeightHandler.obtainMessage();
                            msg.obj = 10;
                            setHeightHandler.sendMessage(msg);
                        }
                    },0,10);


                }else {
                    Animation roatImg = new RotateAnimation(0,180,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                    roatImg.setDuration(200);
                    roatImg.setFillAfter(true);
                    openImg.startAnimation(roatImg);
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Message msg = setHeightHandler.obtainMessage();
                            msg.obj = -10;
                            setHeightHandler.sendMessage(msg);
                        }
                    }, 0, 10);


                }
                break;
            case R.id.get_msg_exam_code://获取验证码
                getMsgExamCodeBtn.setEnabled(false);
                getMsgExamCodeBtn.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.exam_code_timer_down));
                if(downTimer != null){
                    downTimer.cancel();
                    getMsgExamCodeBtn.setText("");
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
                getMsgExamCode();
                break;
            case R.id.register_btn:
                register();
                break;
            case R.id.login_btn:
                login();
                break;
            case R.id.forget_password:
                ((BaseActivity)mContext).cancelMsg();
                ((BaseActivity)mContext).gotoTarget(ForgetPasswordActivity.class, R.anim.activity_in_from_left, R.anim.activity_out_from_right, "");
                break;
            case R.id.service_agreement://服务协议
                ((BaseActivity)mContext).cancelMsg();
                 ((BaseActivity) mContext).gotoWeb(new Web("服务协议", Cst.URL.SERVER_DETAIL));
//                ((BaseActivity)mContext).gotoTarget(WebActivity.class, R.anim.activity_in_from_left,
//                        R.anim.activity_out_from_right, "", Cst.URL.SERVER_DETAIL);
                break;
            case R.id.privacy_clause:
                ((BaseActivity)mContext).cancelMsg();
                ((BaseActivity)mContext).gotoWeb(new Web("隐私条款", Cst.URL.PRIVACY_DETAIL));
//                ((BaseActivity)mContext).gotoTarget(WebActivity.class, R.anim.activity_in_from_left,
//                        R.anim.activity_out_from_right, "",Cst.URL.PRIVACY_DETAIL);
                break;
        }
    }

    private Timer timer;
    private Handler setHeightHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            ViewGroup.LayoutParams lp = recommendCodeEditText.getLayoutParams();
            lp.height = lp.height + (int)msg.obj;
            if (lp.height >= TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,50,mContext.getResources().getDisplayMetrics())){
                lp.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,50,mContext.getResources().getDisplayMetrics());
                recommendCodeEditText.setLayoutParams(lp);
                timer.cancel();
            }else if (lp.height <= 0){
                lp.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,0,mContext.getResources().getDisplayMetrics());
                recommendCodeEditText.setLayoutParams(lp);
                timer.cancel();
            }
            recommendCodeEditText.setLayoutParams(lp);
        }
    };

//    private void goToWeb(Web web) {
//        Intent intent = new Intent(mContext,WebActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("data",web);
//        intent.putExtra("data", bundle);
//        mContext.startActivity(intent);
//    }

    /*
    * 获取验证码
    * */
    private void getMsgExamCode() {
        String mobile = phoneRegister.getText().toString().trim();
        ExamCodeRequest examCodeRequest = new ExamCodeRequest(mContext, new StringListener() {
            @Override
            public void ResponseListener(String result) {
                ExamCodeResponse examCodeResponse = new ExamCodeResponse();
                if(examCodeResponse.getStatus(result) != 200){
                    Toast.makeText(mContext,examCodeResponse.getMessage(result),Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void ErrorListener(VolleyError volleyError) {
                Toast.makeText(mContext,"请求失败",Toast.LENGTH_SHORT).show();
            }
        },mobile);
        try {
            examCodeRequest.stringRequest();
        } catch (Exception e) {
            Toast.makeText(mContext,"请求失败",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(!hasFocus){

        }
    }

    public void showRegister(){
        registerLayout.setVisibility(View.VISIBLE);
        loginLayout.setVisibility(View.GONE);

    }

    public void changeRegister(){
        registerLayout.setVisibility(View.GONE);
        loginLayout.setRotationX(0);
        loginLayout.setRotationY(0);
        registerLayout.setRotationX(0);
        registerLayout.setRotationY(0);
        ObjectAnimator animatorIn = new ObjectAnimator().ofFloat(loginLayout, "rotationY", 0, 90).setDuration(500);
        animatorIn.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                loginLayout.setVisibility(View.GONE);
                registerLayout.setVisibility(View.VISIBLE);
                ObjectAnimator.ofFloat(registerLayout, "rotationY", -90, 0).setDuration(500).start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animatorIn.start();
    }

    private void changeLogin(){
        loginLayout.setVisibility(View.GONE);
        loginLayout.setRotationX(0);
        loginLayout.setRotationY(0);
        registerLayout.setRotationX(0);
        registerLayout.setRotationY(0);
        ObjectAnimator animatorOut = new ObjectAnimator().ofFloat(registerLayout, "rotationY", 0, 90).setDuration(500);
        animatorOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                registerLayout.setVisibility(View.GONE);
                loginLayout.setVisibility(View.VISIBLE);
                ObjectAnimator.ofFloat(loginLayout, "rotationY", -90, 0).setDuration(500).start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animatorOut.start();
    }

    private void login(){

        final  String  userName = userNameEdit.getText().toString().trim();
        String passWord = passEdit.getText().toString().trim();
        if(userName.toString().trim().equals("")||passWord.toString().trim().equals("")){
            //Toast.makeText(mContext,"用户名或密码不能为空",Toast.LENGTH_LONG).show();
            ((BaseActivity)mContext).toast("用户名或密码不能为空");
            return;
        }
            loginDialog.setVisibility(View.VISIBLE);
            disableSubControls((ViewGroup) loginLayout, false);
            //String uuid = ((TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        //Log.e("uuid","uuid:"+uuid);
            LoginRequest loginRequest = new LoginRequest(mContext, new StringListener(){
                @Override
                public void ResponseListener(String result) {
                    LoginAndRegisterResponse response = new LoginAndRegisterResponse();
                    InputMethodManager imm = (InputMethodManager) mContext
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    //判断隐藏软键盘是否弹出
                    if(imm.isActive()){
                        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                    if(response.getStatus(result) == 200){
                        Log.e("+++++++++", result);
                        loginDoingLayout.setVisibility(View.GONE);
                        loginRightImg.setVisibility(View.VISIBLE);
                        investor = response.getObject(result);
                        HomeActivity.edmOverview = edmOverview = response.getEdmOverview(result);
                        HomeActivity.edmAppCount = edmAppCount = response.getEdmAppCount(result);
                        HomeActivity.totalAssets = totalAssets = response.getTotalAssets(result);
                        investor.hasBuyedEdm = response.hasBuyedEdm(result);
                        investor.hasBuyedEdm = response.hasBuyEdmProject(result);
                        Utils.getInstances().putInvestorToSharedPreference(mContext, investor);
                        Cst.currentUser = investor;
                        Utils.getInstances().updatePushState(mContext);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                msgHandler.sendEmptyMessage(Cst.CMD.LOGIN_SUCCESS);

                            }
                        }, 1000);
                        Utils.getInstances().putUserNameToSharedPreference(mContext,userName);
                        Intent intent = new Intent(WebSocketService.ACTION);
                        intent.putExtra("cid", Cst.PUSH_CLIENT_ID);
                        mContext.sendBroadcast(intent);

                    }else {

                        ((BaseActivity)mContext).toast(response.getMessage(result));
                        loginDialog.setVisibility(View.GONE);
                        disableSubControls((ViewGroup) loginLayout, true);
                    }

                }

                @Override
                public void ErrorListener(VolleyError volleyError) {
                    disableSubControls((ViewGroup) loginLayout,true);
                    loginDialog.setVisibility(View.GONE);
                    ((BaseActivity)mContext).showErrorMsg(mContext.getString(R.string.net_error),null);
                }
            },userName,passWord,Cst.PUSH_CLIENT_ID);
            loginRequest.stringRequest();

    }

    private void register(){
        final String  phone = phoneRegister.getText().toString().trim();
        String examCode = examCodeRegister.getText().toString().trim();
        String pass = passRegister.getText().toString().trim();
        if(!isPassWordOk(pass)){
            ((BaseActivity)mContext).toast("密码格式不正确");
            return;
        }
        if(examCode.trim().equals("")){
            ((BaseActivity)mContext).toast("短信验证码不能为空");
            return;
        }
        String recommendCode = recommendCodeRegister.getText().toString().trim();
        registerDialog.setVisibility(View.VISIBLE);
        disableSubControls((ViewGroup) registerLayout, false);
        RegisterRequest registerRequest = new RegisterRequest(mContext, new StringListener() {
            @Override
            public void ResponseListener(String result) {
                LoginAndRegisterResponse registerResponse = new LoginAndRegisterResponse();

                if(registerResponse.getStatus(result) == 200){
                    registerDoingLayout.setVisibility(View.GONE);
                    registerRightImg.setVisibility(View.VISIBLE);
                    investor = registerResponse.getObject(result);
                    HomeActivity.edmOverview = edmOverview = registerResponse.getEdmOverview(result);
                    HomeActivity.edmAppCount = edmAppCount = registerResponse.getEdmAppCount(result);
                    HomeActivity.totalAssets = totalAssets = registerResponse.getTotalAssets(result);
                    investor.hasBuyedEdm = registerResponse.hasBuyedEdm(result);
                    investor.hasBuyedEdm = registerResponse.hasBuyEdmProject(result);
                    Cst.currentUser = investor;
                    Utils.getInstances().putInvestorToSharedPreference(mContext, investor);
                    Utils.getInstances().updatePushState(mContext);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            msgHandler.sendEmptyMessage(Cst.CMD.REGISTER_SUCCESS);
                        }
                    }, 1000);
                    Intent intent = new Intent(WebSocketService.ACTION);
                    intent.putExtra("cid", Cst.PUSH_CLIENT_ID);
                    mContext.sendBroadcast(intent);
                    Utils.getInstances().putUserNameToSharedPreference(mContext, phone);
                }else {
                    registerDialog.setVisibility(View.GONE);
                    ((BaseActivity)mContext).toast(registerResponse.getMessage(result));
                    disableSubControls((ViewGroup) registerLayout, true);
                }

            }

            @Override
            public void ErrorListener(VolleyError volleyError) {
                disableSubControls((ViewGroup) registerLayout,true);
                registerDialog.setVisibility(View.GONE);
                Toast.makeText(mContext,mContext.getString(R.string.net_error),Toast.LENGTH_SHORT).show();
            }
        },phone,pass,examCode,recommendCode,Cst.PUSH_CLIENT_ID);
        try {
            registerRequest.stringRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Investor getInvestor(){
        return investor;
    }

    public TotalAssets getTotalAssets() {
        return totalAssets;
    }

    public EdmAppCount getEdmAppCount() {
        return edmAppCount;
    }

    public EdmOverview getEdmOverview() {
        return edmOverview;
    }

    public boolean isPassWordOk(String pass){
        return !pass.trim().contains(" ") && pass.length() >= 8;
    }


    public void disableSubControls(ViewGroup viewGroup,boolean use) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View v = viewGroup.getChildAt(i);
            if (v instanceof ViewGroup) {
                if (v instanceof Spinner) {
                    Spinner spinner = (Spinner) v;
                    spinner.setClickable(use);
                    spinner.setEnabled(use);
                } else if (v instanceof ListView) {
                    ((ListView) v).setClickable(use);
                    ((ListView) v).setEnabled(use);
                } else {
                    v.setEnabled(use);
                    disableSubControls((ViewGroup) v, use);
                }
            } else if (v instanceof EditText) {
                ((EditText) v).setEnabled(use);
                ((EditText) v).setClickable(use);
            } else if (v instanceof Button) {
                ((Button) v).setEnabled(use);
            }else if (v instanceof ImageButton) {
                ((ImageButton) v).setEnabled(use);
            }else if (v instanceof TextView) {
                ((TextView) v).setEnabled(use);
            }else if (v instanceof ImageView){
                ((ImageView) v).setEnabled(use);
            }
        }
    }
}
