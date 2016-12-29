package com.yeepbank.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;
import com.yeepbank.android.R;

import java.util.Random;
import java.util.logging.LogRecord;

/**
 * Created by WW on 2015/10/26.
 */
public class ScrollTextView extends ScrollView{
    private Context mContext;
    private static final String[] childTexts = new String[]{"0","1","2","3","4","5","6","7","8","9"};
    private int targetNumber;
    private Scroller mScroller;
    private float center,centerTop,centerBottom,scrollDy;
    private LinearLayout contentLayout;
    private static int durationTime = 300;
    private int delay;
    private boolean once = true;

    public ScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        initView(context, attrs);
    }

    public ScrollTextView(Context context, AttributeSet attrs, int targetNumber) {

        super(context, attrs, 0);
        this.targetNumber = targetNumber;
        initView(context, attrs);


    }


    private void initView(Context context, AttributeSet attrs) {
        this.mContext = context;
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.ScrollTextView);
        //targetNumber = a.getInteger(R.styleable.ScrollTextView_targetNumber,0);
        delay = a.getInteger(R.styleable.ScrollTextView_delay,0);
        a.recycle();
        mScroller = new Scroller(mContext);

        contentLayout = new LinearLayout(mContext);
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        contentLayout.setGravity(Gravity.CENTER);
        addView(contentLayout);
        createChild();
        initCenter();

    }



    private void createChild() {

        for(int i = 0; i < childTexts.length*3; i++){
            TextView textView = new TextView(mContext);
            LinearLayout linearLayout = new LinearLayout(mContext);
            int index = new Random().nextInt(15);
            //linearLayout.setBackgroundColor(Color.parseColor("#"+color[index]+color[index]+color[index]+color[index]+color[index]+color[index]));
            linearLayout.setGravity(Gravity.CENTER);
            textView.setText(childTexts[i % childTexts.length]);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            textView.setTextColor(Color.parseColor("#ff6babff"));
            textView.setGravity(Gravity.TOP);
            linearLayout.addView(textView);
            contentLayout.addView(linearLayout);
            if(i >= childTexts.length*2 && i < childTexts.length*3  && i % childTexts.length == targetNumber){
                break;
            }
        }
    }


    private void initCenter() {
        View view = contentLayout.getChildAt(0);
        measureView(view);
        scrollDy = view.getMeasuredHeight();
        //centerTop = center - view.getMeasuredHeight() / 2;
        //centerBottom = center + view.getMeasuredHeight() / 2;
    }

    private void measureView(View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if(params == null){
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int width,height;
        int tempWidth = params.width;
        int tempHeight = params.height;
        if(tempWidth > 0){
            width = MeasureSpec.makeMeasureSpec(tempWidth,MeasureSpec.EXACTLY);
        }else {
            width = MeasureSpec.makeMeasureSpec(tempWidth,MeasureSpec.UNSPECIFIED);
        }
        if(tempHeight > 0){
            height = MeasureSpec.makeMeasureSpec(tempHeight,MeasureSpec.EXACTLY);
        }else {
            height = MeasureSpec.makeMeasureSpec(tempHeight,MeasureSpec.UNSPECIFIED);
        }
        view.measure(width, height);
    }

    @Override
    protected int computeHorizontalScrollOffset() {
        return super.computeHorizontalScrollOffset();
    }


    @Override
    public void computeScroll() {
        if(mScroller.computeScrollOffset()){
            scrollTo(mScroller.getCurrX(),mScroller.getCurrY());
            postInvalidate();
        }
        super.computeScroll();
    }

    public  void scroll(){
        postDelayed(runnable, delay);
     }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            mScroller.startScroll(mScroller.getCurrX(), mScroller.getCurrY(), 0, (int) scrollDy, 50);
            postInvalidate();
            if( mScroller.getCurrY()+scrollDy < contentLayout.getMeasuredHeight()){
                post(runnable);
            }else {

            }
        }
    };

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        getVisibility();
        super.onScrollChanged(l, t, oldl, oldt);

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
           switch (msg.what){
               case 0:
                   synchronized (mContext){
                   mScroller.startScroll(mScroller.getCurrX(),mScroller.getCurrY(),0, (int) scrollDy);
                   invalidate();
                   }
                   break;
           }
        }
    };

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void setTargetNumber(int targetNumber) {
        this.targetNumber = targetNumber;
    }

    public void recover(){
        mScroller.startScroll(mScroller.getCurrX(),mScroller.getCurrY(),0,-mScroller.getCurrY(),10);
        invalidate();
    }
}
