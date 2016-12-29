package com.yeepbank.android.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by WW on 2015/9/9.
 */
public class ScrollerCircleView extends View {

    private Paint mPaint;
    public ScrollerCircleView(Context context) {
        super(context);
    }

    public ScrollerCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint = new Paint(Color.BLACK);
        canvas.drawCircle(200,200,200,mPaint);
        canvas.saveLayerAlpha(0, 0, 500, 500, 0, Canvas.ALL_SAVE_FLAG);
        mPaint = new Paint(Color.BLUE);
        canvas.drawCircle(500,200,200,mPaint);
        canvas.restore();
        super.onDraw(canvas);
    }
}
