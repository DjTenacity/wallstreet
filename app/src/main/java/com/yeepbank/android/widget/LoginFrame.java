package com.yeepbank.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.WindowManager;
import android.widget.FrameLayout;
import com.yeepbank.android.R;

/**
 * Created by WW on 2015/9/23.
 */
public class LoginFrame extends FrameLayout {
    private Context mContext;
    private float showWidth,showHeight;
    private int screenWidth,screenHeight;

    public LoginFrame(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }



    public LoginFrame(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.LoginFrame);
        showWidth = a.getFloat(R.styleable.LoginFrame_show_width,1.0f);
        showHeight = a.getFloat(R.styleable.LoginFrame_show_height,1.0f);
        a.recycle();
        WindowManager manager = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
        screenWidth = manager.getDefaultDisplay().getWidth();
        screenHeight = manager.getDefaultDisplay().getHeight();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension((int)(screenWidth*showWidth),(int)(screenHeight*showHeight));

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }
}
