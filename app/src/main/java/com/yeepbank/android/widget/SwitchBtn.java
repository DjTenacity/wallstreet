package com.yeepbank.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.yeepbank.android.Cst;
import com.yeepbank.android.R;
import com.yeepbank.android.utils.Utils;


/**
 * Created by WW on 2015/9/16.
 */
public class SwitchBtn extends View {

    private int width,height,radius;
    private Rect rect,fontRect;
    private Paint paint;
    /*
    * ??С????
    * */
    private int minWidth = 4;
    /*
    * ????
    * */
    private int spaceLeft;
    /*
    * ?????? ???????
    * */
    private boolean isOpen;
    /*
    * ??????????
    * */
    private boolean enabled = true;
    /*
    *
    * ????????????(?????в?????)
    * */
    private boolean isClickAble = true;
    /*
    * ???????
    * */
    private Cst.OnStateChangeListener onStateChangeListener;
    /*
    * С?????????????
    * */
    private int fontColor,bgColor;
    private Context context;
    /*
    * ??????????key?
    * */
    private String key = "";






    public SwitchBtn(Context context, AttributeSet attrs) {
        super(context, attrs,0);
        this.context = context;
        initView(attrs);
    }

    public SwitchBtn(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SwitchBtn);
        isOpen = a.getBoolean(R.styleable.SwitchBtn_is_open, true);
        fontColor = a.getColor(R.styleable.SwitchBtn_font_color, Color.parseColor("#20db55"));
        bgColor = a.getColor(R.styleable.SwitchBtn_bg_color, Color.parseColor("#999999"));
        key = a.getString(R.styleable.SwitchBtn_btn_name);
        a.recycle();
        isOpen = Utils.getInstances().getAcceptPushMsg(context,key);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = measureDimension(180, widthMeasureSpec);
        height = measureDimension(80, heightMeasureSpec);
        setMeasuredDimension(width, height);
        initRect();

    }

    private void initRect() {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        rect = new Rect(0,0,width,height);
        radius = (int) (rect.height() / 1.8);
        if(isOpen){
            spaceLeft = 2*radius+minWidth;
        }else {
            spaceLeft = minWidth;
        }
    }

    private int measureDimension(int defaultSize,int measureSize){
        int result;
        int specModel = MeasureSpec.getMode(measureSize);
        int specSize = MeasureSpec.getSize(measureSize);
        if(specModel == MeasureSpec.EXACTLY){
            result = specSize;
        }else {
            result = defaultSize;
            if(specModel == MeasureSpec.AT_MOST){
                result = Math.min(result,specSize);
            }

        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint = new Paint();
        paint.setAntiAlias(true);


        if(isOpen) {
            paint.setColor(fontColor);
            canvas.drawRoundRect(new RectF(rect), radius, radius, paint);
            fontRect = new Rect(2*radius+minWidth,minWidth,getMeasuredWidth()-minWidth,getMeasuredHeight()-minWidth);

        }else {
            paint.setColor(bgColor);
            canvas.drawRoundRect(new RectF(rect), radius, radius, paint);
            fontRect = new Rect(minWidth,minWidth,getMeasuredWidth()-radius*2-minWidth,getMeasuredHeight()-minWidth);

        }
        paint.setColor(Color.WHITE);
        canvas.drawRoundRect(new RectF(fontRect), radius, radius, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!isClickAble || !enabled){
            return true;
        }

        moveBegin();
        if(onStateChangeListener != null){
            if(!isOpen){
                onStateChangeListener.onOpened(this);
            }else {
                onStateChangeListener.onClosed(this);
            }
        }

        return super.onTouchEvent(event);

    }

    private void moveBegin() {
        new Thread(){
            @Override
            public void run() {

                    while (true){
                        if(isOpen && spaceLeft > minWidth){
                            spaceLeft -= getMeasuredWidth()/2;
                            if(spaceLeft <  minWidth){
                                spaceLeft = minWidth;
                            }
                            postInvalidate();
                            isClickAble = true;
                        }else if(!isOpen && spaceLeft < 2*radius+minWidth){
                            spaceLeft += getMeasuredWidth()/2;
                            if(spaceLeft >  2*radius+minWidth){
                                spaceLeft = 2*radius+minWidth;
                            }
                            postInvalidate();
                            isClickAble = true;
                        }else {
                            break;
                        }
                    }
                    isOpen = !isOpen;

            }
        }.start();

    }

    public void setOnStateChangeListener(Cst.OnStateChangeListener onStateChangeListener) {
        this.onStateChangeListener = onStateChangeListener;
    }

    public String getKey(){
        return key;
    }

    /*
    * ?????????????????????
    * */
    public void close(){
        if (isOpen){
            moveBegin();
            if(onStateChangeListener != null){
               onStateChangeListener.onClosed(this);
            }
        }
    }
    /*
    * ????????????????????
    * */
    public void open(){
        if (!isOpen){
            moveBegin();
            if(onStateChangeListener != null){
                onStateChangeListener.onOpened(this);
            }
        }
    }

    public void setEnabled(boolean enabled){
        if (this.enabled == enabled){
            return;
        }
        this.enabled = enabled;
        if (enabled){
            fontColor = Color.parseColor("#20db55");
            bgColor = Color.parseColor("#999999");
        }else {
            fontColor = Color.parseColor("#8fedaa");
            bgColor = Color.parseColor("#dcdcdc");
        }
        postInvalidate();
    }

    public boolean getState(){
        return isOpen;
    }
}
