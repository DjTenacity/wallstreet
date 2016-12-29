package com.yeepbank.android.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.*;
import com.yeepbank.android.Cst;
import com.yeepbank.android.R;
import com.yeepbank.android.utils.Utils;

/**
 * Created by WW on 2015/10/14.
 */
public class PullToRefresh extends ListView implements View.OnClickListener{
    private Context mContext;
    private LayoutInflater layoutInflater;
    private View headerView,footerView;
    private TextView headerText,footerText;
    private boolean onceSetAdapter = true;
    private int firstVisible,visibleCount,total;
    private Scroller mScroller;
    private boolean isRemark = false;
    private float sY,tY;
    private int STATE = Cst.COMMON.NULL;

    private int headerViewHeight,footerViewHeight;
    private boolean isUp = false;

    private TextView cmdText,refreshTime,loadMoreText;
    private ProgressBar waitDia;
    private ImageView arrowImg;
    private ProgressBar progress;
    private Animation upAnimation,downAnimation;
    private Handler handler;
    private Cst.OnRefresh<PullToRefresh> onRefresh;//刷新回调接口
    private int MIN_SPACE;//刷新时下拉最小距离
    private boolean isPushing = false;
    private boolean isContinueLoad = true;
    private RelativeLayout footerLayout;



    public PullToRefresh(Context context, AttributeSet attrs) {
        super(context, attrs,0);
        init(context, attrs);
    }

    public PullToRefresh(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void setOnRefresh(Cst.OnRefresh<PullToRefresh> onRefresh){
        this.onRefresh = onRefresh;
    }

    private void init(Context context, AttributeSet attrs) {
        this.mContext = context;
        TypedArray a = mContext.obtainStyledAttributes(attrs,R.styleable.PullToRefresh);
        isPushing = a.getBoolean(R.styleable.PullToRefresh_push, false);
        a.recycle();
        setDivider(null);
        layoutInflater = LayoutInflater.from(mContext);
        headerView = layoutInflater.inflate(R.layout.refresh_title, null);
        headerText = (TextView) headerView.findViewById(R.id.cmd_text);
        headerText.setText("下拉可刷新");
        footerView = layoutInflater.inflate(R.layout.loadmore_footer,null);
        setOnScrollListener(new ScrollListener());
        mScroller = new Scroller(mContext);

        cmdText = (TextView) headerView.findViewById(R.id.cmd_text);
        refreshTime = (TextView) headerView.findViewById(R.id.update_text);
        arrowImg = (ImageView) headerView.findViewById(R.id.direction_pic);
        progress = (ProgressBar) headerView.findViewById(R.id.progress);
        upAnimation = new RotateAnimation(0,180,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        upAnimation.setFillAfter(true);
        upAnimation.setDuration(200);

        downAnimation = new RotateAnimation(180,0,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        downAnimation.setFillAfter(true);
        downAnimation.setDuration(200);

        loadMoreText = (TextView) footerView.findViewById(R.id.loadmore_text);
        waitDia = (ProgressBar) footerView.findViewById(R.id.loadmore_progress);
        footerLayout = (RelativeLayout) footerView.findViewById(R.id.footer_layout);
        footerLayout.setOnClickListener(this);
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case Cst.COMMON.REFRESH_COMPLETE:
                        STATE = Cst.COMMON.NULL;
                        refreshViewByState();
                        Utils.getInstances().putToSharedPreference(mContext, System.currentTimeMillis());
                        topPadding(-headerViewHeight);
                        break;
                    case Cst.COMMON.NO_MORE:
                        isContinueLoad = (boolean) msg.obj;
                        if(!isContinueLoad){
                            loadMoreText.setText("无最新数据");
                            waitDia.setVisibility(GONE);
                        }
                        break;
                }
            }
        };
        refreshViewByState();
    }

    public Handler getHandler(){
        return handler;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }



    public void setBaseAdapter(BaseAdapter adapter) {


        if(onceSetAdapter){
            onceSetAdapter = false;
            addFooterView(footerView);
            measureView(footerView);
            footerViewHeight = footerView.getMeasuredHeight();
            //bottomPadding(-footerViewHeight);

            addHeaderView(headerView);
            measureView(headerView);
            headerViewHeight = headerView.getMeasuredHeight();
            MIN_SPACE = (int) (headerViewHeight+ TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,10,getResources().getDisplayMetrics()));
        }

        setAdapter(adapter);
        topPadding(-headerViewHeight);

    }

    public void noHeadView(){
        removeHeaderView(headerView);
    }


    private void measureView(View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if(params == null){
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int height;
        int tempHeight = params.height;
        int width = ViewGroup.getChildMeasureSpec(0, 0, params.width);
        if(tempHeight > 0){
            height = MeasureSpec.makeMeasureSpec(tempHeight,MeasureSpec.EXACTLY);
        }else {
            height = MeasureSpec.makeMeasureSpec(tempHeight,MeasureSpec.UNSPECIFIED);
        }
        view.measure(width, height);
    }

    private void bottomPadding(int bottomPadding){
        footerView.setPadding(footerView.getPaddingLeft(), footerView.getPaddingTop(), footerView.getPaddingRight(), bottomPadding);
        postInvalidate();
    }

    private void topPadding(int headerHeight) {

        headerView.setPadding(headerView.getPaddingLeft(), (int) (headerHeight*0.8), headerView.getPaddingRight(), headerView.getPaddingBottom());
        postInvalidate();
    }

    @Override
    public void onClick(View v) {
        if(onRefresh!= null){
            getMoreData();
        }
    }

    private void getMoreData(){
        isContinueLoad = true;
        loadMoreText.setText("正在加载");
        waitDia.setVisibility(VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onRefresh.loadMore();
            }
        },100);
    }

    private class ScrollListener implements OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if(!isContinueLoad){
                return;
            }
            if(firstVisible + visibleCount == total && scrollState == SCROLL_STATE_IDLE){
                if(onRefresh != null){
                    getMoreData();
                }
            }else {
                if(loadMoreText.getText().equals("正在加载")){
                    loadMoreText.setText("点击加载更多");
                    waitDia.setVisibility(GONE);
                }
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            firstVisible = firstVisibleItem;
            visibleCount = visibleItemCount;
            total = totalItemCount;
        }
    }

    public int getFirstVisible(){
        return firstVisible;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(!isPushing){
            return super.onTouchEvent(ev);
        }
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(firstVisible == 0){
                    sY = ev.getY();
                    isRemark = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                 onMoveRefresh(ev);
                break;
            case MotionEvent.ACTION_UP:
                switch (STATE){
                    case Cst.COMMON.PULL_TO_REFRESH:
                        STATE = Cst.COMMON.NULL;
                        refreshViewByState();
                        isRemark = false;
                        break;
                    case Cst.COMMON.RELEASE_TO_REFRESH:
                        STATE = Cst.COMMON.REFRESH;
                        refreshViewByState();
                        break;
                }
                break;
        }
        return super.onTouchEvent(ev);
    }



    private void onMoveRefresh(MotionEvent ev) {
        float tempY = ev.getY();
        float space = tempY - sY;
        float topPadding = space - headerViewHeight;
        if(!isRemark){
            return;
        }
        switch (STATE){
            case Cst.COMMON.NULL:
                if(space > 0){
                    STATE = Cst.COMMON.PULL_TO_REFRESH;
                    refreshViewByState();
                }
                break;
            case Cst.COMMON.PULL_TO_REFRESH:
                topPadding((int) topPadding);
                if(headerView.getPaddingTop() >= MIN_SPACE ){
                    STATE = Cst.COMMON.RELEASE_TO_REFRESH;
                    refreshViewByState();
                }
                break;
            case Cst.COMMON.RELEASE_TO_REFRESH:
                topPadding((int) topPadding);
                if(headerView.getPaddingTop() < MIN_SPACE){
                    STATE = Cst.COMMON.PULL_TO_REFRESH;
                    refreshViewByState();
                }
                break;

        }
    }

    private void refreshViewByState() {


        switch (STATE){
            case Cst.COMMON.NULL:
                topPadding(-headerViewHeight);
                arrowImg.clearAnimation();
                progress.setVisibility(GONE);
                arrowImg.setVisibility(VISIBLE);
                break;
            case Cst.COMMON.PULL_TO_REFRESH:
                cmdText.setVisibility(VISIBLE);
                cmdText.setText("下拉可刷新");
                arrowImg.clearAnimation();
                if(isUp){
                    arrowImg.startAnimation(downAnimation);
                    isUp = false;
                }
                break;
            case Cst.COMMON.RELEASE_TO_REFRESH:
                cmdText.setText("松开立即刷新");
                arrowImg.clearAnimation();
                if(!isUp){
                    arrowImg.startAnimation(upAnimation);
                    isUp = true;
                }
                break;
            case Cst.COMMON.REFRESH:

                topPadding(0);
                arrowImg.setVisibility(GONE);
                arrowImg.clearAnimation();
                progress.setVisibility(VISIBLE);
                cmdText.setText("正在刷新");
                if(onRefresh != null){
                    onRefresh.refresh(this);
                }
                break;
        }
    }


    @Override
    public void computeScroll(){
        if(mScroller.computeScrollOffset()){
            scrollTo(mScroller.getCurrX(),mScroller.getCurrY());
            postInvalidate();
        }
    }

    public void reset(){
        isContinueLoad = true;
        loadMoreText.setText("点击加载更多");
        waitDia.setVisibility(GONE);
    }
}
