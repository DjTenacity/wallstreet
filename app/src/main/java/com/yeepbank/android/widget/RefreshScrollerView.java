package com.yeepbank.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.*;
import com.yeepbank.android.Cst;
import com.yeepbank.android.R;
import com.yeepbank.android.utils.Utils;

/**
 * Created by WW on 2015/9/21.
 * 自定义ScrollView的下拉刷新组件
 */
public class RefreshScrollerView extends ScrollView{
    private Context mContext;
    private View headerView;//刷新头布局
    private float sY,dY;

    private MarginLayoutParams mParams;
    private int headerViewHeight;
    private boolean isFirstCreate = true;//是否第一次创建，在创建界面的时候头部隐藏一次，防止代码重复执行
    private int SPEED;//刷新头显示/隐藏速度
    private ImageView arrowPic;//下拉刷新箭头
    private ProgressBar progress;//加载loading
    private TextView cmdText,updateText;
    private Cst.OnRefresh onRefresh;//定义刷新接口
    private float SENSITIVITY = 1.8f;//灵敏度
    private boolean isScrolled = false;



    private  int state = Cst.COMMON.NULL;
    private  int MIN_REFRESH_SPACE;

    private Animation upAnimation,downAnimation;

    private boolean isRefreshing = false;
    private boolean isGetSy = false;

    public RefreshScrollerView(Context context, AttributeSet attrs) {
        super(context, attrs,0);
        mContext = context;
        init(attrs);
    }

    public RefreshScrollerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(attrs);
    }

    private void init(AttributeSet attrs){
        TypedArray a = mContext.obtainStyledAttributes(attrs,R.styleable.RefreshScrollerView);
        SPEED = a.getInteger(R.styleable.RefreshScrollerView_speed,6);
        SENSITIVITY = a.getInteger(R.styleable.RefreshScrollerView_sensitivity,1);
        a.recycle();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Cst.COMMON.REFRESH_COMPLETE:
                    refreshByState(Cst.COMMON.NULL);
                    Utils.getInstances().putToSharedPreference(mContext, System.currentTimeMillis());
                    updateText.setText("上次刷新于" + Utils.getInstances().getUpdateTimeFromSharedPreference(mContext));
                    break;
            }
        }
    };

    public void stopRefresh(){
        isRefreshing = false;
        handler.obtainMessage(Cst.COMMON.REFRESH_COMPLETE).sendToTarget();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        findView();
    }

    private void findView() {

        arrowPic = (ImageView) findViewById(R.id.direction_pic);
        progress = (ProgressBar) findViewById(R.id.progress);
        cmdText = (TextView) findViewById(R.id.cmd_text);
        updateText = (TextView) findViewById(R.id.update_text);
        if(!"".equals(Utils.getInstances().getUpdateTimeFromSharedPreference(mContext).trim())){
            updateText.setText("上次刷新于" + Utils.getInstances().getUpdateTimeFromSharedPreference(mContext));
        };
        setOverScrollMode(View.OVER_SCROLL_NEVER);

        upAnimation = new RotateAnimation(0,180,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        upAnimation.setDuration(300);
        upAnimation.setFillAfter(true);

        downAnimation = new RotateAnimation(180,0,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        downAnimation.setDuration(300);
        downAnimation.setFillAfter(true);
        hideTitle();

    }


    /*
    * 初始化的时候隐藏刷新头部
    * */
    private void hideTitle() {
        if(isFirstCreate){
            isFirstCreate = false;
            headerView = findViewById(R.id.refresh_layout);
            headerViewHeight = headerView.getMeasuredHeight();
            MIN_REFRESH_SPACE = headerViewHeight;
            topPadding(-headerViewHeight);
        }
    }

    private void measureView(View view){
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if(params == null){
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int height;
        int tempHeight = params.height;
        int width = ViewGroup.getChildMeasureSpec(0,0,params.width);
        if(tempHeight > 0){
            height = MeasureSpec.makeMeasureSpec(tempHeight,MeasureSpec.EXACTLY);
        }else {
            height = MeasureSpec.makeMeasureSpec(tempHeight,MeasureSpec.UNSPECIFIED);
        }
        measure(width, height);
    }

    private void topPadding(int topPadding){
        MarginLayoutParams params = (MarginLayoutParams) headerView.getLayoutParams();
        params.topMargin = topPadding;
        headerView.setLayoutParams(params);
    }

    private int getTopPadding(){
        MarginLayoutParams params = (MarginLayoutParams) headerView.getLayoutParams();
        return params.topMargin;
    }

    private boolean hasPermission(MotionEvent ev){
        return getScrollY() <= 0 ||
                ev.getAction() == MotionEvent.ACTION_UP ||
                ev.getAction() == MotionEvent.ACTION_CANCEL||
                (getScrollY() > 0 && getTopPadding() > 0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if(!isRefreshing && hasPermission(ev)){

            switch (ev.getAction()){
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_MOVE:
                    if(!isGetSy){
                        sY = ev.getRawY();
                        isGetSy = true;
                    }
                    if(ev.getPointerCount() == 1){
                        onMove(ev);
                    }
                    break;
                default:

                    isGetSy = false;
                    switch (state){
                        case Cst.COMMON.PULL_TO_REFRESH:
                            refreshByState(Cst.COMMON.NULL);
                            break;
                        case Cst.COMMON.RELEASE_TO_REFRESH:
                            refreshByState(Cst.COMMON.REFRESH);
                            break;
                    }
                    break;
            }
        }
        return super.onTouchEvent(ev);
    }

    private void onMove(MotionEvent ev) {
        float tempY = ev.getRawY();
        float space = tempY - sY;
        int topPadding = (int) ((space - headerViewHeight) / SENSITIVITY);
        switch (state){
            case Cst.COMMON.NULL:
                if(space > 0){
                    refreshByState(Cst.COMMON.PULL_TO_REFRESH);
                }
                break;
            case Cst.COMMON.PULL_TO_REFRESH:
                topPadding(topPadding);
                if(getTopPadding() >= MIN_REFRESH_SPACE){

                    refreshByState(Cst.COMMON.RELEASE_TO_REFRESH);
                }
                break;
            case Cst.COMMON.RELEASE_TO_REFRESH:
                topPadding(topPadding);
                if(getTopPadding() < MIN_REFRESH_SPACE){
                    refreshByState(Cst.COMMON.PULL_TO_REFRESH);
                }
                break;
        }
        setSelected(true);
    }

    private void refreshByState(int stateCode) {

        if (stateCode == state)return;
        switch (stateCode){
            case Cst.COMMON.NULL:
                topPadding(-headerViewHeight);
                setScrollY(0);
                arrowPic.clearAnimation();
                progress.setVisibility(GONE);
                arrowPic.setVisibility(VISIBLE);
                //updateText.setText("上次刷新于" + Utils.getUpdateTimeFromSharedPreference(mContext));
                break;
            case Cst.COMMON.PULL_TO_REFRESH:
                arrowPic.clearAnimation();
                arrowPic.setVisibility(VISIBLE);
                cmdText.setText("下拉可刷新");
                if(state == Cst.COMMON.RELEASE_TO_REFRESH){
                    arrowPic.startAnimation(downAnimation);
                }
                break;
            case Cst.COMMON.RELEASE_TO_REFRESH:
                cmdText.setText("松开立即刷新");
                arrowPic.clearAnimation();
                if(state == Cst.COMMON.PULL_TO_REFRESH){
                    arrowPic.startAnimation(upAnimation);
                }
                break;
            case Cst.COMMON.REFRESH:
                topPadding(0);
                cmdText.setText("正在刷新");
                isRefreshing = true;
                arrowPic.clearAnimation();
                arrowPic.setVisibility(GONE);
                progress.setVisibility(VISIBLE);
                if(onRefresh != null){
                    onRefresh.refresh(this);
                }
                break;
        }
        state = stateCode;
    }




    public void setOnRefresh(Cst.OnRefresh onRefresh) {
        this.onRefresh = onRefresh;
    }

}
