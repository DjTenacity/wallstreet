package com.yeepbank.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.yeepbank.android.Cst;
import com.yeepbank.android.R;

/**
 * Created by WW on 2015/10/13.
 */
public class SwitchItemLayout extends RelativeLayout implements View.OnClickListener{

    private Context mContext;
    private boolean isOnce = true;
    private TextView repayingText,repayedText;
    private TextView checkedText;
    private ImageView img;
    private Animation animationLeft,animationRight;
    private RelativeLayout.LayoutParams lp;
    private int backgroundResource;
    private String checkedTextColor,uncheckedTextColor;
    private String leftBtnText = "还款中",rightBtnText = "已还款";
    private Cst.OnSlideCompleteListener onSlideCompleteListener;
    public SwitchItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }



    public SwitchItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        this.mContext = context;
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs,R.styleable.SwitchItemLayout);
        leftBtnText = typedArray.getString(R.styleable.SwitchItemLayout_btn_left);
        rightBtnText = typedArray.getString(R.styleable.SwitchItemLayout_btn_right);
        typedArray.recycle();
    }

    public void setOnSlideCompleteListener(Cst.OnSlideCompleteListener onSlideCompleteListener){
        this.onSlideCompleteListener = onSlideCompleteListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(isOnce){
            isOnce = false;

//            repayingText = (TextView) findViewById(R.id.repaying);
//            repayedText = (TextView) findViewById(R.id.repayed);
//            repayedText.setOnClickListener(this);
//            repayingText.setOnClickListener(this);
//            checkedText = repayingText;


            LinearLayout linearLayout = new LinearLayout(mContext);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            repayingText = new TextView(mContext);
            repayingText.setText(leftBtnText);
            repayingText.setTextColor(Color.parseColor("#999999"));
            repayingText.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f));
            repayingText.setGravity(Gravity.CENTER);
            repayingText.setId(R.id.repaying);
            repayingText.setOnClickListener(this);
            //repayingText.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_bg_radius_big_white));
            checkedText = repayingText;
            linearLayout.addView(repayingText);

            repayedText = new TextView(mContext);
            repayedText.setText(rightBtnText);
            repayedText.setTextColor(Color.parseColor("#666666"));
            repayedText.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f));
            repayedText.setGravity(Gravity.CENTER);
            repayedText.setId(R.id.repayed);
            repayedText.setOnClickListener(this);
            linearLayout.addView(repayedText);
            createImg();
            addView(img);
            addView(linearLayout);
        }
    }

    @Override
    public void onClick(View v) {

        if(checkedText.getId() == v.getId()){
            return;
        }
        if(img == null){
            createImg();
        }
        switch (v.getId()){
            case R.id.repaying:
                moveToRepaying();
                break;
            case R.id.repayed:
                moveToRepayed();
                break;
        }
    }

    private void moveToRepaying() {
        animationRight = new TranslateAnimation(checkedText.getWidth(),0,0,0);
        animationRight.setDuration(300);
        animationRight.setInterpolator(new AccelerateInterpolator());
        animationRight.setFillAfter(true);
        animationRight.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //repayedText.setBackgroundDrawable(null);
                repayedText.setTextColor(Color.parseColor("#666666"));
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                //repayingText.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_bg_radius_big_white));
                repayingText.setTextColor(Color.parseColor("#999999"));
                checkedText = repayingText;
//                removeView(img);
                if (onSlideCompleteListener != null) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onSlideCompleteListener.onComplete(checkedText);
                        }
                    },100);

                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        lp = new RelativeLayout.LayoutParams(checkedText.getWidth(),checkedText.getMeasuredHeight());
        lp.leftMargin = 0;
        img.setLayoutParams(lp);
        img.setAnimation(animationRight);
    }



    private void moveToRepayed() {

        //addView(img);
        animationLeft = new TranslateAnimation(0,checkedText.getWidth(),0,0);
        animationLeft.setDuration(300);
        animationLeft.setInterpolator(new AccelerateInterpolator());
        animationLeft.setFillAfter(true);
        animationLeft.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                repayingText.setTextColor(Color.parseColor("#666666"));
                //repayingText.setBackgroundDrawable(null);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //repayedText.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_bg_radius_big_white));
                repayedText.setTextColor(Color.parseColor("#999999"));
                checkedText = repayedText;
               // removeView(img);
                if(onSlideCompleteListener != null){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onSlideCompleteListener.onComplete(checkedText);
                        }
                    },100);

                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        lp = new RelativeLayout.LayoutParams(checkedText.getWidth(),checkedText.getMeasuredHeight());
        lp.leftMargin = checkedText.getLeft();
        img.setLayoutParams(lp);
        img.setAnimation(animationLeft);
    }

    private void createImg(){
        img = new ImageView(mContext);
        img.setLayoutParams(new ViewGroup.LayoutParams(getMeasuredWidth()/2, getMeasuredHeight()));
        img.setImageResource(R.drawable.round_bg_radius_big_white);
    }

    public void setButtonText(String left,String right){
        if(repayingText!= null && repayedText!= null){
                repayingText.setText(left);
                repayedText.setText(right);
        }


    }
    public void setLeftButtonText(String left){
        if(repayingText!= null && repayedText!= null){
            repayingText.setText(left);
        }
    }

    public void setRightButtonText(String right){
        if(repayingText!= null && repayedText!= null){
            repayedText.setText(right);
        }
    }



}
