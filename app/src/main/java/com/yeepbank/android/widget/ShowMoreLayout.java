package com.yeepbank.android.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.yeepbank.android.R;


/**
 * Created by WW on 2015/10/8.
 */
public class ShowMoreLayout extends RelativeLayout implements View.OnTouchListener{
    private Context mContext;
    private View navigationLayout;
    private boolean isOnce = true;
    private float sY,tY;
    private MarginLayoutParams mp;
    private final static int SPEED = 30;
    private final static int MIN_LENGTH = 10;
    private final static int NULL = 0;
    public final static int UP = 1;
    public final static int DOWN = 2;
    public final static int UP_TO_AT_MOST = 3;
    public final static int DOWN_TO_AT_LEST = 4;
    public final static int AT_MOST = 5;
    public static Handler  msgHandler;
    private ImageView triangleImg;
    private TextView repaymentPlanText;
    private int state = NULL;
    private int contentHeight;
    private int navigationLayoutHeight;
    private int screenHeight;
    private int actionBarHeight;
    public ShowMoreLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    public ShowMoreLayout(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.mContext = context;
        actionBarHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,75,getResources().getDisplayMetrics());
        msgHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(mp != null){
                    switch (msg.what){

                        case UP_TO_AT_MOST:
                           state = AT_MOST;
                            topMargin(0);
                            break;
                    }
                }

            }
        };
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if(isOnce){
            isOnce = false;
            screenHeight = ((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight();
            navigationLayout = findViewById(R.id.more_detail);
            //navigationLayout.findViewById(R.id.triangle).bringToFront();
            measureView(navigationLayout);
            mp = (MarginLayoutParams) this.getLayoutParams();
            navigationLayoutHeight = navigationLayout.getMeasuredHeight();
            contentHeight = getMeasuredHeight();

        }
        setOnTouchListener(this);
    }

    private void measureView(View navigationLayout) {
        ViewGroup.LayoutParams params =  navigationLayout.getLayoutParams();
        if(params == null){
            params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int width = ViewGroup.getChildMeasureSpec(0,0,params.width);
        int height;
        int tempHeight = params.height;
        if(tempHeight > 0){
            height = MeasureSpec.makeMeasureSpec(tempHeight,MeasureSpec.EXACTLY);
        }else {
            height = MeasureSpec.makeMeasureSpec(tempHeight, MeasureSpec.UNSPECIFIED);
        }
        measure(width, height);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                sY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if(event.getPointerCount() == 1){
                onMove(event);
                }
                break;
            case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:

                switch (state){
                    case UP:
                        state = NULL;
                        topMargin(0);
                        break;
                    case UP_TO_AT_MOST:
                        state = AT_MOST;
                        topMargin(0);
                        break;
                }
                break;

        }
        return true;
    }

    private void onMove(MotionEvent event) {
        float tempY = event.getY();
        int topMargin = (int) (tempY - sY);
        switch (state){
            case NULL:
                if(topMargin < 0){
                    state = UP;
                    topMargin(topMargin);
                }
                break;
            case UP:
                if(Math.abs(getTopMargin()) >= 50){//(contentHeight - navigationLayoutHeight-actionBarHeight)/10){
                    state = UP_TO_AT_MOST;

                }
                topMargin(topMargin);
                break;
            case UP_TO_AT_MOST:
                if(Math.abs(getTopMargin()) < 50){//(contentHeight - navigationLayoutHeight-actionBarHeight)/3){
                    state = UP;
                }
                topMargin(topMargin);
                break;
        }
    }

    private void topMargin(int topMargin) {
        if(state == NULL){
            mp.topMargin = actionBarHeight;
        }else if(state == AT_MOST){
            mp.topMargin = -(contentHeight - navigationLayoutHeight)+actionBarHeight;

        }else {
            mp.topMargin += topMargin;
        }

        setLayoutParams(mp);
        postInvalidate();
        if(state == AT_MOST){
            state = NULL;
        }
    }

    private int getTopMargin(){
        return mp.topMargin;
    }
}
