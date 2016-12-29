package com.yeepbank.android.widget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import com.yeepbank.android.utils.ScreenUtils;

import java.text.DecimalFormat;

/**
 * Created by WW on 2015/12/29.
 */
public class PieChartView extends View {

    private boolean once = true;
    private Context mContext;

    private int screenW, screenH;
    /**
     * The paint to draw text, pie and line.
     */
    private Paint textPaint, piePaint, linePaint,centerPaint,alphaPaint;

    /**
     * The center and the radius of the pie.
     */
    private int pieCenterX, pieCenterY, pieRadius;
    /**
     * The oval to draw the oval in.
     */
    private RectF pieOval;

    private float smallMargin;

    private int[] mPieColors = new int[]{Color.rgb(56,135,190),Color.WHITE,Color.rgb(82,196,234), Color.WHITE,Color.rgb(255,187,104),
            Color.WHITE,Color.rgb(249,136,108),Color.WHITE, Color.rgb(86,184,129),Color.WHITE};

    private PieItemBean[] mPieItems;
    private float totalValue;

    public PieChartView(Context context) {
        super(context);
        mContext = context;
    }

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public PieChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(once){
            once = false;
            init(mContext);
        }
    }

    private void init(Context context) {
        //init screen
        screenW = getMeasuredWidth();
        screenH = getMeasuredHeight();

        pieCenterX = screenW / 2;
        pieCenterY = screenH / 2;
        pieRadius = screenW / 4;
        smallMargin = ScreenUtils.dp2px(context, 5);

        pieOval = new RectF();
        pieOval.left = pieCenterX - pieRadius;
        pieOval.top = pieCenterY - pieRadius;
        pieOval.right = pieCenterX + pieRadius;
        pieOval.bottom = pieCenterY + pieRadius;

        //The paint to draw text.
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(ScreenUtils.dp2px(context, 16));

        //The paint to draw circle.
        piePaint = new Paint();
        piePaint.setAntiAlias(true);
        piePaint.setStyle(Paint.Style.FILL);

        //The paint to draw line to show the concrete text
        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(ScreenUtils.dp2px(context, 1));

        centerPaint = new Paint();
        centerPaint.setAntiAlias(true);
        centerPaint.setColor(Color.WHITE);

        alphaPaint = new Paint();
        alphaPaint.setAntiAlias(true);
        alphaPaint.setColor(Color.WHITE);
        alphaPaint.setAlpha(125);


    }

    //The degree position of the last item arc's center.
    private float lastDegree = 0;
    //The count of the continues 'small' item.
    private int addTimes = 0;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mPieItems != null && mPieItems.length > 0) {
            float start = 0.0f;
            for (int i = 0; i < mPieItems.length; i++) {
                //draw pie
                piePaint.setColor(mPieColors[i % mPieColors.length]);
                float sweep = 0;
                if(mPieItems[i].getItemValue() > 0){
                    sweep = mPieItems[i].getItemValue() / totalValue < 0.001? 0.001f * 360 : mPieItems[i].getItemValue() / totalValue * 360;
                }
                canvas.drawArc(pieOval, start, sweep, true, piePaint);
                start += sweep;
            }
            canvas.drawCircle(pieCenterX, pieCenterY, pieRadius / 2 + 55, alphaPaint);
            canvas.drawCircle(pieCenterX,pieCenterY,pieRadius/2,centerPaint);
        }
    }

    public PieItemBean[] getPieItems() {
        return mPieItems;
    }

    public void setPieItems(PieItemBean[] pieItems) {
        this.mPieItems = pieItems;

        totalValue = 0;
        for (PieItemBean item : mPieItems) {
            totalValue += item.getItemValue();
        }

        invalidate();
    }

    private float getOffset(float radius) {
        int a = (int) (radius % 360 / 90);
        switch (a) {
            case 0:
                return radius;
            case 1:
                return 180 - radius;
            case 2:
                return radius - 180;
            case 3:
                return 360 - radius;
        }

        return radius;
    }


    public static class PieItemBean {
        private String itemType;
        private float itemValue;

        public PieItemBean(String itemType, float itemValue) {
            this.itemType = itemType;
            this.itemValue = itemValue;
        }

        public String getItemType() {
            return itemType;
        }

        public void setItemType(String itemType) {
            this.itemType = itemType;
        }

        public float getItemValue() {
            return itemValue;
        }

        public void setItemValue(float itemValue) {
            this.itemValue = itemValue;
        }
    }

    private static String formatFloat(double value) {
        DecimalFormat df = new DecimalFormat("0.0");
        return df.format(value);
    }

}

