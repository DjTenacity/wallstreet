package com.yeepbank.android.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.yeepbank.android.R;

/**
 * Created by WW on 2015/10/21.
 */
public class ListViewLeftBond extends LinearLayout {
    private Context mContext;
    private int itemHeight;
    private int num;
    private Drawable imgDraw;
    private boolean isOnce = true;
    private View view;
    public ListViewLeftBond(Context context, AttributeSet attrs) {
        super(context, attrs,0);
        initView(context, attrs);
    }

    public ListViewLeftBond(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        this.mContext = context;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.invest_list_item_in_project_more, null);
        measureView(view);
        itemHeight = view.getMeasuredHeight();
        getViewTreeObserver().addOnGlobalLayoutListener(globalLayout);

    }

   /* @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(isOnce){
            isOnce = false;
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.invest_list_item_in_project_more, null);
            measureView(view);
            itemHeight = view.getMeasuredHeight();
            getViewTreeObserver().addOnGlobalLayoutListener(globalLayout);

        }
    }*/

    private ViewTreeObserver.OnGlobalLayoutListener globalLayout = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            num = getHeight() / itemHeight;
            createImg();
        }
    };

    private void createImg() {
        new Thread(){
            @Override
            public void run() {
                for(int i = 0; i < num; i++){
                    LinearLayout linearLayout = new LinearLayout(mContext);
                    LinearLayout.LayoutParams parentP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, itemHeight);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    ImageView imageView = new ImageView(mContext);
                    imageView.setLayoutParams(params);
                    imageView.setImageResource(R.drawable.time_point_icon);
                    linearLayout.setGravity(Gravity.TOP);
                    linearLayout.setLayoutParams(parentP);
                    linearLayout.addView(imageView);
                    Message msg = handler.obtainMessage();
                    msg.what = 0;
                    msg.obj = linearLayout;
                    handler.sendMessage(msg);
                }
            }
        }.start();

    }

    private void measureView(View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if(params == null){
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,100,mContext.getResources().getDisplayMetrics()));
        }
        int height,width;
        int tempHeight = params.height;
        int tempWidth = params.width;
        if(tempHeight > 0){
            height = MeasureSpec.makeMeasureSpec(tempHeight,MeasureSpec.EXACTLY);
            width = MeasureSpec.makeMeasureSpec(tempWidth,MeasureSpec.EXACTLY);
        }else {
            height = MeasureSpec.makeMeasureSpec(tempHeight,MeasureSpec.UNSPECIFIED);
            width = MeasureSpec.makeMeasureSpec(tempWidth,MeasureSpec.UNSPECIFIED);
        }
        view.measure(width, height);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            addView((LinearLayout)msg.obj);
        }
    };
}
