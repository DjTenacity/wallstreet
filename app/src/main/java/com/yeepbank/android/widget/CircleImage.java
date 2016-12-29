package com.yeepbank.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.util.AttributeSet;


import android.view.View;
import com.yeepbank.android.R;

import static android.graphics.Bitmap.createBitmap;

/**
 * Created by WW on 2015/8/25.
 */

public class CircleImage extends View {


    private Paint paint;
    private Context mContext;
    private int top;
    private int resource;
    private int height,width;
    private Bitmap bitmap;
    public CircleImage(Context context, AttributeSet attrs) {
        super(context, attrs,0);
        mContext = context;
        init(attrs);
    }

    public CircleImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(attrs);
    }

    private void init(AttributeSet attrs) {

        TypedArray a = mContext.obtainStyledAttributes(attrs,R.styleable.CircleImage2);
        top = (int) a.getDimension(R.styleable.CircleImage2_top,0);
        resource = a.getResourceId(R.styleable.CircleImage2_draw_resource,R.drawable.ic_launcher);
        bitmap = BitmapFactory.decodeResource(getResources(), resource);
        a.recycle();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        width = measureSize(bitmap.getWidth(), widthMeasureSpec);
        height = measureSize(bitmap.getHeight(), heightMeasureSpec);

        setMeasuredDimension(width, height);

    }

    private int measureSize(int defaultSize, int spaceMeasureSize) {
        int result = defaultSize;
        int model = MeasureSpec.getMode(spaceMeasureSize);
        int space = MeasureSpec.getSize(spaceMeasureSize);
        if(model == MeasureSpec.EXACTLY){
            result = space;
        }else {
            if(model == MeasureSpec.AT_MOST){
                result = Math.min(defaultSize,space);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        Bitmap localBitmap = Bitmap.createBitmap(getWidth(),getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(localBitmap);
        c.drawCircle(getWidth() / 2, getHeight() / 2, Math.min(getWidth(), getHeight()) / 2 - 12, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        c.drawBitmap(bitmap, getWidth() / 2 - bitmap.getWidth() / 2, getHeight() / 2 - bitmap.getHeight() / 2, paint);
        canvas.drawBitmap(localBitmap,0,top,null);
    }
}
