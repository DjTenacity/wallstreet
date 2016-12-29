package com.yeepbank.android.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.*;
import com.yeepbank.android.R;

import java.security.Policy;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by WW on 2015/10/30.
 */
public class CalendarView extends ViewGroup implements GestureDetector.OnGestureListener{

    private Context mContext;
    private int childCount = 5;
    private float initScale = 0.6f;
    private int screenWidth;
    private Scroller mScroller;
    private float sX;
    private int currentMonth = 11;
    private int rawX;
    private boolean reLayout = true;
    private int itemWidth,itemHeight;
    private int currentIndex,preIndex,nextIndex;
    private View currentView,preView,nextView;
    private GestureDetector detector;
    private boolean mCurrentViewAtLeft = true;
    private LayoutInflater layoutInflater;
    private boolean onceMeasure = true;
    private boolean isAtFirstMove = true;
    // Fling distance.
    private int mFlingX = 0;
    private View selectedView;

    private int  initScrollWidth;
    /**
     * Fling duration.
     */
    public static final int FLING_DURATION = 2000;
    /**
     * Filing max velocity x.
     */
    public static final int MAX_VELOCITY_X = 1000;

    HashMap<View,Float> leftMap = new HashMap<>();


    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        initView(context, attrs);
    }



    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        if(reLayout){
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                if(i < childCount / 2){
                    child.setScaleX(i/10f+initScale);
                    child.setScaleY(i / 10f + initScale);
                }else if( i > childCount/2){
                    child.setScaleX((childCount - 1 - i)/10f+initScale);
                    child.setScaleY((childCount - 1 - i) / 10f + initScale);
                }
                child.layout(i*itemWidth,0,(i+1)*itemWidth,itemHeight);
            }

            initScrollWidth = screenWidth/2 - getChildAt(2).getLeft() - itemWidth/2;
            mScroller.startScroll(0, 0, -initScrollWidth, 0);
            int[] position = new int[2];
            getChildAt(2).getLocationOnScreen(position);
            rawX = position[0];

            reLayout = false;
        }

    }

    private void initView(Context context, AttributeSet attrs) {
        this.mContext = context;
        layoutInflater = LayoutInflater.from(mContext);
        mScroller = new Scroller(mContext);
        detector = new GestureDetector(this);
        screenWidth = ((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
        createCalendarCard();
        createIndex();
    }

    private void createIndex() {
        preIndex = childCount - 1;
        currentIndex = 0;
        nextIndex = 1;

        preView = getChildAt(preIndex);
        currentView = getChildAt(currentIndex);
        nextView = getChildAt(nextIndex);

    }

    private void createCalendarCard() {
        for (int i = 0; i < childCount; i++) {
            View view = getView(i);
            measureView(view);
            addView(view);
        }
    }

    private void measureView(View view) {

            int w = View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED);
            int h = View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED);
            view.measure(w, h);
            itemWidth = view.getMeasuredWidth();
            itemHeight = view.getMeasuredHeight();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){

            case MotionEvent.ACTION_DOWN:
                sX = event.getX();
                break;

            case MotionEvent.ACTION_MOVE:
                if(event.getPointerCount()== 1){
                    float tX = event.getX();
                    float ds = tX - sX;
                    sX = tX;
                    scrollView(ds);
                }
                break;

            case MotionEvent.ACTION_UP:
//                if(selectedView != null){
//                    mScroller.startScroll(0,0,-(selectedView.getLeft() - 2*itemWidth),0);
//                }
                break;
        }
        return this.detector.onTouchEvent(event);
    }

    private void scrollView(float ds) {
        moveView((int) ds);
        if(ds > 0){

            moveToNextView();
        }else {


            moveToPreView();

        }
        invalidate();
    }


    private void moveToPreView() {
        int[] position = new int[2];
        currentView.getLocationOnScreen(position);
        if(preView.getLeft() < screenWidth){
            currentView.layout(preView.getRight() + 10, preView.getTop(), preView.getRight() + itemWidth, preView.getBottom());
            currentView.setScaleX(initScale);
            currentView.setScaleY(initScale);

            preIndex = currentIndex;
            currentIndex = nextIndex;

            nextIndex ++;
            if(nextIndex > childCount - 1){
                nextIndex = 0;
            }
            preView = getChildAt(preIndex);
            currentView = getChildAt(currentIndex);
            nextView = getChildAt(nextIndex);

        }
    }

    private void moveToNextView() {
        int[] position = new int[2];
        currentView.getLocationOnScreen(position);
        if(currentView.getLeft() > itemWidth){
            preView.layout(currentView.getLeft() - itemWidth + 10, currentView.getTop(), currentView.getLeft() - 10, currentView.getBottom());
            preView.setScaleX(initScale);
            preView.setScaleY(initScale);

            nextIndex = currentIndex;
            currentIndex = preIndex;
            preIndex --;
            if(preIndex < 0){
                preIndex = childCount - 1;
            }
            preView = getChildAt(preIndex);
            currentView = getChildAt(currentIndex);
            nextView = getChildAt(nextIndex);
        }
    }


    private synchronized void  moveView(int ds) {

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            child.layout(child.getLeft() + ds, child.getTop(), child.getRight() + ds, child.getBottom());
            int[] position = new int[2];
            child.getLocationOnScreen(position);

            if(child.getLeft() >= 2 * itemWidth - itemWidth/4
                    &&child.getLeft() <= 2 * itemWidth + itemWidth/4){
                calendarSelected(child);
            }else {
                calendarUnSelected(child);
            }
            if((child.getLeft() >= itemWidth && child.getLeft() < 2 * itemWidth - itemWidth/4)
                    || (child.getLeft() > 2 * itemWidth + itemWidth/4 && child.getLeft() <= 3*itemWidth)){
                child.setScaleX(initScale+0.1f);
                child.setScaleY(initScale+0.1f);
            }else if(child.getLeft() >= 2 * itemWidth - itemWidth/4
                    &&child.getLeft() <= 2 * itemWidth + itemWidth/4){
                child.setScaleX(initScale+0.3f);
                child.setScaleY(initScale+0.3f);
            }

//            if(ds > 0){
//                if(child.getLeft() < itemWidth){
//                    float scale = child.getScaleX() + ((float)Math.abs(ds) / itemWidth)*0.1f;
//                    child.setScaleX(scale > initScale+0.1f? initScale + 0.1f :scale);
//                    child.setScaleY(scale > initScale+0.1f? initScale + 0.1f :scale);
//                }else if(child.getLeft() >= itemWidth &&child.getLeft() < 2*itemWidth){
//                    float scale = child.getScaleX() + ((float)Math.abs(ds) / itemWidth)*0.2f;
//                    child.setScaleX(scale > initScale+0.2f? initScale + 0.2f :scale);
//                    child.setScaleY(scale > initScale+0.2f? initScale + 0.2f :scale);
//                }else if(child.getLeft() >= 2*itemWidth &&child.getLeft() < 3*itemWidth) {
//                    float scale = child.getScaleX() - ((float)Math.abs(ds) / itemWidth)*0.2f;
//                    child.setScaleX(scale > initScale+0.2f? initScale + 0.2f :scale);
//                    child.setScaleY(scale > initScale+0.2f? initScale + 0.2f :scale);
//                }else if(child.getLeft() > 3*itemWidth){
//                    float scale = child.getScaleX() - ((float)Math.abs(ds) / itemWidth)*0.1f;
//                    child.setScaleX(scale > initScale+0.1f? initScale + 0.1f :scale);
//                    child.setScaleY(scale > initScale+0.1f? initScale + 0.1f :scale);
//                }
//            }else if(ds < 0){
//                if(child.getLeft() > 3*itemWidth){
//                    float scale = child.getScaleX() + ((float)Math.abs(ds) / itemWidth)*0.1f;
//                    child.setScaleX(scale > initScale+0.1f? initScale + 0.1f :scale);
//                    child.setScaleY(scale > initScale+0.1f? initScale + 0.1f :scale);
//                }else if(child.getLeft() <= 3*itemWidth &&child.getLeft() > 2*itemWidth){
//                    float scale = child.getScaleX() + ((float)Math.abs(ds) / itemWidth)*0.2f;
//                    child.setScaleX(scale > initScale+0.2f? initScale + 0.2f :scale);
//                    child.setScaleY(scale > initScale+0.2f? initScale + 0.2f :scale);
//                }else if(child.getLeft() <= 2*itemWidth &&child.getLeft() > itemWidth) {
//                    float scale = child.getScaleX() - ((float)Math.abs(ds) / itemWidth)*0.2f;
//                    child.setScaleX(scale > initScale+0.2f? initScale + 0.2f :scale);
//                    child.setScaleY(scale > initScale+0.2f? initScale + 0.2f :scale);
//                }else if(child.getLeft() <= itemWidth){
//                    float scale = child.getScaleX() - ((float)Math.abs(ds) / itemWidth)*0.1f;
//                    child.setScaleX(scale > initScale+0.1f? initScale + 0.1f :scale);
//                    child.setScaleY(scale > initScale+0.1f? initScale + 0.1f :scale);
//                }
//            }

        }
    }



    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

//        if (e1 == null || e2 == null) {
//            return false;
//        }
//
//        // When deltaX and velocityX not good return false.
////        if (Math.abs(velocityX) < MAX_VELOCITY_X) {
////            return false;
////        }
//
//        // Get the delta x.
//        float deltaX = (e1.getX() - e2.getX());
//
//        /**
//         * If can fling stop other scroll task at first , delay the task after
//         * fling.
//         */
////        mHandler.removeCallbacks(mScrollTask);
////        if (canScroll) {
////            mHandler.postDelayed(mScrollTask, TOUCH_DELAYMILLIS
////                    + FLING_DURATION - 1000);
////        }
//
//        /**
//         * The flingX is fling distance.
//         */
//        mFlingX = (int) deltaX;
//
//        // Start scroll with fling x.
//        mScroller.startScroll(0, 0, mFlingX, 0, FLING_DURATION);

        return false;
    }

    public View getView(int index) {
        View view = layoutInflater.inflate(R.layout.calendar_card, null);
        if(index != childCount/2){
            calendarUnSelected(view);
        }

        return view;
    }

    private void calendarUnSelected(View view){
        TextView titleText = (TextView) view.findViewById(R.id.calendar_title);
        titleText.setBackgroundResource(R.drawable.calendar_unselected_title);
        titleText.setTextColor(Color.parseColor("#c3c3c3"));

        LinearLayout body = (LinearLayout) view.findViewById(R.id.calendar_body);
        body.setBackgroundResource(R.drawable.calendar_unselected_body);
        TextView monthText = (TextView) body.findViewById(R.id.calendar_body_month);
        monthText.setTextColor(Color.parseColor("#c3c3c3"));
        TextView unitText = (TextView) body.findViewById(R.id.calendar_body_unit);
        unitText.setTextColor(Color.parseColor("#c3c3c3"));
        selectedView = view;
//        view.setScaleX(0.7f);
//        view.setScaleY(0.7f);
    }

    private void calendarSelected(View view){
        TextView titleText = (TextView) view.findViewById(R.id.calendar_title);
        titleText.setBackgroundResource(R.drawable.calendar_selected_title);
        titleText.setTextColor(Color.parseColor("#ffffff"));

        LinearLayout body = (LinearLayout) view.findViewById(R.id.calendar_body);
        body.setBackgroundResource(R.drawable.calendar_selected_body);
        TextView monthText = (TextView) body.findViewById(R.id.calendar_body_month);
        monthText.setTextColor(Color.parseColor("#ff5965ff"));
        TextView unitText = (TextView) body.findViewById(R.id.calendar_body_unit);
        unitText.setTextColor(Color.parseColor("#ff5965ff"));
        selectedView = null;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(onceMeasure){
            onceMeasure = !onceMeasure;
            int width = measureActivity(widthMeasureSpec,0);
            int height = measureActivity(heightMeasureSpec,1);
            measure(width,height);
        }
    }

    private int measureActivity(int measureSpace,int flag) {
        int model = MeasureSpec.getMode(measureSpace);
        int size = MeasureSpec.getSize(measureSpace);
        int result = 0;
        int defaultResult = flag == 0?itemWidth:itemHeight;
        if(model == MeasureSpec.EXACTLY){
            result = size;
        }else {
            if(model == MeasureSpec.AT_MOST){
                result = defaultResult;
            }
        }
        return result;
    }

    @Override
    public void computeScroll() {
        if(mScroller.computeScrollOffset()){



            scrollTo(mScroller.getCurrX(),0);
            postInvalidate();
        }
    }
}
