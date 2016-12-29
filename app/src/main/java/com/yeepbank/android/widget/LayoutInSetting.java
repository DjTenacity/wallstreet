package com.yeepbank.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.yeepbank.android.R;

/**
 * Created by WW on 2015/9/24.
 */
public class LayoutInSetting extends LinearLayout {

    private Context mContext;
    private TextView firstTextView,secondTextView;
    private ImageView thirdImage,drawLeft;
    private int imgResource,drawLeftResource;
    private String firstText,secondText;
    private boolean isShowDefaultImg = true;
    private LinearLayout redDoc;
    private String textColorStr;
    public LayoutInSetting(Context context, AttributeSet attrs) {
        super(context, attrs,0);
        init(context,attrs);
    }

    public LayoutInSetting(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.LayoutInSetting);
        firstText = a.getString(R.styleable.LayoutInSetting_f_text);
        secondText = a.getString(R.styleable.LayoutInSetting_s_text);
        textColorStr = a.getString(R.styleable.LayoutInSetting_text_color);
        textColorStr = textColorStr == null? "#999999":textColorStr;
        imgResource = 0;
        isShowDefaultImg = a.getBoolean(R.styleable.LayoutInSetting_is_show_default,true);
        if(isShowDefaultImg){
            imgResource = R.drawable.jt;
        }

        drawLeftResource = a.getResourceId(R.styleable.LayoutInSetting_draw_left,0);
        a.recycle();
        createView();
    }



    private void createView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_in_setting, null);
        redDoc = (LinearLayout) view.findViewById(R.id.red_dot);
        int measureHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,45,getResources().getDisplayMetrics());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,measureHeight);
        view.setLayoutParams(params);
        drawLeft = (ImageView) view.findViewById(R.id.draw_left);
        if(drawLeftResource != 0){
            drawLeft.setVisibility(VISIBLE);
            drawLeft.setImageResource(drawLeftResource);
        }else {
            drawLeft.setVisibility(GONE);
        }

        firstTextView = (TextView) view.findViewById(R.id.first_text);
        firstTextView.setText(firstText);

        secondTextView = (TextView) view.findViewById(R.id.second_text);
        secondTextView.setText(secondText);
        secondTextView.setTextColor(Color.parseColor(textColorStr));
        thirdImage = (ImageView) view.findViewById(R.id.forward_img);
        thirdImage.setImageResource(imgResource);
        if(imgResource == 0){
            thirdImage.setVisibility(INVISIBLE);

        }else {
            thirdImage.setVisibility(VISIBLE);
        }
        addView(view);

    }

    public void setRedDocVisible(int visible){
        redDoc.setVisibility(visible);
    }

    public void setValue(String value){
        secondTextView.setText(value);
    }
    public void setColor(String color){
        secondTextView.setTextColor(Color.parseColor(color));
    }

    public String getValue(){
        return secondTextView.getText().toString().trim();
    }
}
