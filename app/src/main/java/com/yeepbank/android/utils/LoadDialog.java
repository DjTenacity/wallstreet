package com.yeepbank.android.utils;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.*;
import android.widget.*;
import com.yeepbank.android.Cst;
import com.yeepbank.android.R;
import com.yeepbank.android.base.BaseActivity;

/**
 * Created by WW on 2015/8/27.
 */
public class LoadDialog extends Dialog {

    private Context mContext;
    private View view;
    private ImageView iv;
    private boolean cancel = true;
    private RotateAnimation rotateAnimation;
    private TextView tvPoint;
    private TextView isWhatPoint;
    private int timeCount = 6000;
    private AnimatorPoint animatorPoint;
    private ProgressBar downLoadBar;
    private TextView downLoadPercent;
    public  int DIALOG_TYPE;//当前显示dialog的类型
    private TextView downLoadTitle;

    //alert对话框组件
    private Button sureBtn,cancelBtn,centerBtn;
    private TextView title,message;




    public LoadDialog(Context context,int flag) {
        super(context);
        mContext = context;
        DIALOG_TYPE = flag;
        switch (flag){
            case Cst.CMD.NORMAL_LOADING:
                createLoading();
                break;
            case Cst.CMD.APP_CHECK_LOADING:
                createCheckLoading();
                break;
            case Cst.CMD.DOWN_LOADING:
                createDownloadLoading();
                break;
        }

    }



    public LoadDialog(Context context, int theme,boolean cancel,int flag) {
        super(context, theme);
        mContext = context;
        this.cancel = cancel;
        DIALOG_TYPE = flag;
        switch (flag){
            case Cst.CMD.NORMAL_LOADING:
                createLoading();
                break;
            case Cst.CMD.APP_CHECK_LOADING:
                createCheckLoading();
                break;
            case Cst.CMD.DOWN_LOADING:
                createDownloadLoading();
                break;

        }
    }

    public LoadDialog(Context context,int theme,
                      boolean cancel,View.OnClickListener sureBtnOnClickListener,
                      View.OnClickListener cancelBtnOnClickListener,int flag){

        super(context, theme);
        mContext = context;
        DIALOG_TYPE = flag;
        setCancelable(cancel);
        if (flag == Cst.CMD.NEW_VERSION)createUpdateDialog(sureBtnOnClickListener, cancelBtnOnClickListener);
        else createAlertDialog(sureBtnOnClickListener, cancelBtnOnClickListener);
    }
    /*
    * 存在三个button的弹窗*/
    public LoadDialog(Context context,int theme,boolean cancel,View.OnClickListener sureBtnOnClickListener,View.OnClickListener centerBtnOnClickListener,View.OnClickListener cancelBtnOnClickListener){
        super(context,theme);
        mContext=context;
        setCancelable(cancel);
        view = LayoutInflater.from(mContext).inflate(R.layout.alert_dialog_third_line,null);
        setContentView(view);
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,280,mContext.getResources().getDisplayMetrics());
        layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,250,mContext.getResources().getDisplayMetrics());
        window.setAttributes(layoutParams);

        sureBtn = (Button) view.findViewById(R.id.alert_sure_btn_second);
        cancelBtn = (Button) view.findViewById(R.id.alert_cancel_btn_second);
        centerBtn = (Button) view.findViewById(R.id.alert_center_btn_second);

        sureBtn.setOnClickListener(sureBtnOnClickListener);
        centerBtn.setOnClickListener(centerBtnOnClickListener);
        cancelBtn.setOnClickListener(cancelBtnOnClickListener);
    }

    public LoadDialog(Context context,int theme,
                      boolean cancel,View.OnClickListener sureBtnOnClickListener,int flag){
        super(context, theme);
        mContext = context;
        DIALOG_TYPE = flag;
        setCancelable(cancel);
        createAlertDialog(sureBtnOnClickListener);
    }


    /*
    * 下载进度对话框
    * */
    private void createDownloadLoading() {
        view = LayoutInflater.from(mContext).inflate(R.layout.download,null);
        this.setContentView(view);
        setCancelable(cancel);
        downLoadBar = (ProgressBar) view.findViewById(R.id.download_progress);
        downLoadBar.setProgress(0);
        downLoadPercent = (TextView) view.findViewById(R.id.download_percent);
        downLoadTitle = (TextView)findViewById(R.id.versionCode_tv);

    }

    /*
    * 检查对话框
    * */
    private void createCheckLoading() {
        view = LayoutInflater.from(mContext).inflate(R.layout.check_version,null);
        this.setContentView(view);
        setCancelable(cancel);
        tvPoint = (TextView) findViewById(R.id.version_tv_point);
        tvPoint.setText("");
    }

    /*
    * 等待对话框
    * */
    private void createLoading() {

        view = LayoutInflater.from(mContext).inflate(R.layout.loading_dialog,null);
        setContentView(view);
        setCancelable(cancel);
        iv = (ImageView) view.findViewById(R.id.loading_icon);
        rotateAnimation = new RotateAnimation(0,+360, Animation.RELATIVE_TO_SELF,0.5F,Animation.RELATIVE_TO_SELF,0.5F);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setRepeatMode(Animation.INFINITE);
        rotateAnimation.setRepeatCount(-1);
        rotateAnimation.setInterpolator(new LinearInterpolator());

    }


    private void initView(){
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,280,mContext.getResources().getDisplayMetrics());
        layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,150,mContext.getResources().getDisplayMetrics());
        window.setAttributes(layoutParams);
    }
    /*设置等待框文字*/
    public void setWaitDialogWords(String msg){
        if (view != null){
            View textView = view.findViewById(R.id.msg);
            if (textView != null){
                ((TextView)textView).setText(msg);
            }
        }

    }

    /*
    * 显示对话框
    * */
    public void showAs(){
        if(PackageUtil.getInstances(mContext).activityIsRunning(mContext.getClass())){
            if(iv !=null){
                iv.startAnimation(rotateAnimation);
            }
            if(tvPoint != null){
                animatorPoint = new AnimatorPoint(1800,300);
                animatorPoint.start();
            }
            try{
                super.show();
            }catch (Exception e){

            }

        }
    }

    /*
    * 隐藏对话框
    * */
    public void dismiss(){
        DIALOG_TYPE = -1;

        super.dismiss();
    }



    class AnimatorPoint extends CountDownTimer{

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public AnimatorPoint(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if(isShowing() && tvPoint!=null){
                tvPoint.setText(tvPoint.getText().toString()+".");
            }
        }

        @Override
        public void onFinish() {
            if(isShowing() && tvPoint!=null){
                timeCount = 1200;
                tvPoint.setText("");
                super.start();
            }
        }
    }

    private void createUpdateDialog(View.OnClickListener sureBtnOnClickListener, View.OnClickListener cancelBtnOnClickListener) {

        view = LayoutInflater.from(mContext).inflate(R.layout.update_version_layout,null);
        setContentView(view);
        message = (TextView) view.findViewById(R.id.alert_msg);
        sureBtn = (Button) view.findViewById(R.id.alert_sure_btn);
        cancelBtn = (Button) view.findViewById(R.id.alert_cancel_btn);
        sureBtn.setOnClickListener(sureBtnOnClickListener);
        cancelBtn.setOnClickListener(cancelBtnOnClickListener);
    }


    private void createAlertDialog(View.OnClickListener sureBtnOnClickListener,View.OnClickListener cancelBtnOnClickListener){
        view = LayoutInflater.from(mContext).inflate(R.layout.alert_dialog,null);
        setContentView(view);
        initView();
        title = (TextView) view.findViewById(R.id.alert_title);
        message = (TextView) view.findViewById(R.id.alert_msg);
        sureBtn = (Button) view.findViewById(R.id.alert_sure_btn);
        cancelBtn = (Button) view.findViewById(R.id.alert_cancel_btn);
        centerBtn = (Button) view.findViewById(R.id.alert_center_btn);
        centerBtn.setVisibility(View.GONE);
        sureBtn.setOnClickListener(sureBtnOnClickListener);
        cancelBtn.setOnClickListener(cancelBtnOnClickListener);
    }

    private void createAlertDialog(View.OnClickListener centerBtnOnClickListener){
        view = LayoutInflater.from(mContext).inflate(R.layout.alert_dialog,null);
        setContentView(view);
        title = (TextView) view.findViewById(R.id.alert_title);
        message = (TextView) view.findViewById(R.id.alert_msg);
        sureBtn = (Button) view.findViewById(R.id.alert_sure_btn);
        sureBtn.setVisibility(View.GONE);
        cancelBtn = (Button) view.findViewById(R.id.alert_cancel_btn);
        cancelBtn.setVisibility(View.GONE);
        centerBtn = (Button) view.findViewById(R.id.alert_center_btn);
        centerBtn.setVisibility(View.VISIBLE);
        centerBtn.setOnClickListener(centerBtnOnClickListener);
    }

    /*
    * 设置alert对话框title
    * */
    public LoadDialog setTitle(String title_msg) {
        if(title != null){
            title.setText(title_msg);
            return this;
        }
        return null;
    }
    /*
    * 设置alert对话框内容
    * */
    public LoadDialog setMessage(String msg) {
        if(message != null ){
            message.setText(msg);
            return this;
        }
        return null;
    }

    /*
    * 设置alert对话框内容
    * */
    public LoadDialog setMessage(Spanned msg) {
        if(message != null ){
            message.setText(msg);
            return this;
        }
        return null;
    }

    /*
    * 设置确定按钮显示文字
    * */

    public LoadDialog setSureBtn(String sureBtnText) {
        if(sureBtn != null){
            sureBtn.setText(sureBtnText);
            return this;
        }
        return null;
    }
    public LoadDialog setSureBtn(Spanned sureBtnText) {
        if(sureBtn != null){
            sureBtn.setText(sureBtnText);
            return this;
        }
        return null;
    }

    /*
        * 设置取消按钮显示文字
        * */
    public LoadDialog setCancelBtn(String cancelBtnText) {
        if(cancelBtn != null){
            cancelBtn.setText(cancelBtnText);
            return this;
        }else {
            cancelBtn.setVisibility(View.GONE);
            return this;
        }
    }


    public void setDownLoadBar(int percent) {
        if(isShowing() && downLoadBar !=null ){
            downLoadBar.setProgress(percent);
        }
    }

    public void setDownLoadPercent(String percent) {
        if(isShowing() && downLoadBar !=null){
            downLoadPercent.setText(percent);
        }
    }

    public void setDownLoadTitle(String title){
        if(isShowing() && downLoadTitle != null){
            downLoadTitle.setText(title);
        }
    }

    public LoadDialog selfGravity(int position){
        if(view != null){
            ((TextView)view.findViewById(R.id.alert_msg)).setGravity(position);
        }
        return this;
    }
}
