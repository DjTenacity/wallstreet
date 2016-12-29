package com.yeepbank.android.server;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.android.volley.VolleyError;
import com.yeepbank.android.Cst;
import com.yeepbank.android.R;
import com.yeepbank.android.adapter.InvestListAdapter;
import com.yeepbank.android.adapter.ProjectDetailAdapter;
import com.yeepbank.android.adapter.RepaymentListAdapter;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.base.BaseModel;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.model.business.InvestListItem;
import com.yeepbank.android.model.business.RepaymentListItem;
import com.yeepbank.android.request.business.BiddingListRequest;
import com.yeepbank.android.request.business.ProjectDetailRequest;
import com.yeepbank.android.request.business.RepaymentListRequest;
import com.yeepbank.android.response.business.BiddingListResponse;
import com.yeepbank.android.response.business.ProjectDetailResponse;
import com.yeepbank.android.response.business.RepaymentListResponse;
import com.yeepbank.android.utils.LoadDialog;
import com.yeepbank.android.widget.ChangeItemWithAnimationLayout;
import com.yeepbank.android.widget.PullToRefresh;
import com.yeepbank.android.widget.ShowMoreLayout;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by WW on 2015/10/13.
 */
public class ProjectDetailServer implements View.OnTouchListener,Cst.OnRefresh<PullToRefresh>,ViewPager.OnPageChangeListener{
    private View investListProjectMore,repaymentPlanProjectMore,projectDetailLayout;
    private PullToRefresh investListView,repayPlanListView,projectDetailListView;
    private View moreInfoLayout;
    private ShowMoreLayout showMoreLayout;
    private InvestListAdapter investAdapter;
    private RepaymentListAdapter repayAdapter;
    private ProjectDetailAdapter projectDetailAdapter;
    private Context mContext;
    private float sY;
    private int state = NULL;
    private final static int NULL = 0;
    private final static int UP = 2;
    private final static int UP_AT_MOST = 3;
    private final static int UP_TO_MOST = 4;
    public final static int GET_INVEST_SUCCESS = 5;
    public final static int GET_REPAYMENT_SUCCESS = 6;
    private ArrayList<InvestListItem> investListItems = new ArrayList<InvestListItem>();
    private ArrayList<RepaymentListItem> repaymentListItems = new ArrayList<RepaymentListItem>();
    private ArrayList<BaseModel> projectDetailList = new ArrayList<BaseModel>();
    private int InvestListPage,RepaymentListPage;//投资列表当前页，计划还款当前页
    private final static int INVEST_LIST = 0;
    private final static int REPAYMENT_LIST = 1;
    private int CURRENT_LIST = INVEST_LIST;
    private String projectId;
    private int pageSize;
    private LoadDialog loadding;
    private int actionBarHeight;
    private ArrayList<Integer> layoutIds;//需要显示的页面
    private ViewPager viewPager;
    private Pager pager;
    private BaseModel data;
    private int currentItem = 0;

    private boolean isFingerTouch = false;//判断手指是否触碰到屏幕

    private ChangeItemWithAnimationLayout changeItemWithAnimationLayout;
    private boolean isClickItem = false;//如果是点击tab切换页面 不触发页面选中回调

    public ProjectDetailServer(Context context,ShowMoreLayout parentView,
                               ChangeItemWithAnimationLayout changeItemWithAnimationLayout,
                               BaseModel data,ArrayList<Integer> layoutIds) {
        this.mContext = context;
        this.layoutIds = layoutIds;
        this.data = data;
        actionBarHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75, mContext.getResources().getDisplayMetrics());
        showMoreLayout = parentView;
        this.changeItemWithAnimationLayout = changeItemWithAnimationLayout;
        moreInfoLayout = parentView.findViewById(R.id.more_detail);
        viewPager = (ViewPager) moreInfoLayout.findViewById(R.id.project_detail_pager);
        pager = new Pager(layoutIds);
        viewPager.setAdapter(pager);
        viewPager.setOnPageChangeListener(this);
        loadding = new LoadDialog(mContext,R.style.dialog,false,Cst.CMD.NORMAL_LOADING);


    }

    @Override
    public void onPageScrolled(int arg0, float arg1v, int arg2) {
        //如果手指触碰了屏幕 并且没有滑动 并且是最右边的item 则表示当前滑动继续向右进行

        if (isFingerTouch && arg1v == 0 && currentItem == 0) {
            ((BaseActivity)mContext).finish();
        }
        isFingerTouch = false;
    }

    @Override
    public void onPageSelected(int i) {
        if (isClickItem){
            isClickItem = false;
            return;
        }
        if (currentItem < i){
            if(changeItemWithAnimationLayout.getNextTextModel() != null)
                changeItemWithAnimationLayout.moveImgWithoutLoadData(changeItemWithAnimationLayout.getNextTextModel().textLayout);
        }else if (currentItem > i){
            if(changeItemWithAnimationLayout.getPreTextModel() != null)
                changeItemWithAnimationLayout.moveImgWithoutLoadData(changeItemWithAnimationLayout.getPreTextModel().textLayout);
        }
        currentItem = i;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                View  view = pager.getViews().get(currentItem);
                if(view.getTag().equals("INVEST_LIST"))
                    loadBiddingData(getProjectId(mContext), getPage(mContext), Cst.PARAMS.PAGE_SIZE);
                else if (view.getTag().equals("REPAYMENT_LIST"))
                    loadRepaymentData(getProjectId(mContext), getPage(mContext), Cst.PARAMS.PAGE_SIZE);
            }
        },350);
    }

    private int getPage(Context mContext) {
        try {
            Method method = mContext.getClass().getDeclaredMethod("getPage",null);
            return (int) method.invoke(mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private String getProjectId(Context mContext) {
        try {
            Method method = mContext.getClass().getDeclaredMethod("getProjectId",null);
            return (String) method.invoke(mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    private class Pager extends PagerAdapter{

        private ArrayList<View> views;

        public Pager(ArrayList<Integer> pageIdList) {
            views = new ArrayList<View>();
            for (int i = 0; i < pageIdList.size(); i++) {
                views.add(getViewById(pageIdList.get(i)));
            }
        }

        private View getViewById(int id){
            switch (id){
                case R.id.project_detail:
                    projectDetailLayout = LayoutInflater.from(mContext).inflate(R.layout.more_detail_project_detail_list,null);
                    projectDetailLayout.setTag("PROJECT_DETAIL");
                    projectDetailListView = (PullToRefresh) projectDetailLayout.findViewById(R.id.more_detail_project_detail_listview);
                    initProjectDetailListView();
                    return projectDetailLayout;
                case R.id.invest_list:
                    investListProjectMore = LayoutInflater.from(mContext).inflate(R.layout.more_detail_invest_list,null);
                    investListProjectMore.setTag("INVEST_LIST");
                    investListView = (PullToRefresh) investListProjectMore.findViewById(R.id.invest_list_more_detail_listview);
                    initInvestListView();
                    return investListProjectMore;
                case R.id.repayment_plan:
                    repaymentPlanProjectMore =  LayoutInflater.from(mContext).inflate(R.layout.more_detail_repay_plan,null);
                    repaymentPlanProjectMore.setTag("REPAYMENT_LIST");
                    repayPlanListView = (PullToRefresh) repaymentPlanProjectMore.findViewById(R.id.repayment_plan_more_detail_listview);
                    initRepaymentListView();
                    return repaymentPlanProjectMore;
            }
            return null;

//            private int getIdByText(String text){
//                int id = 0;
//                if(text.trim().equals("项目详情")){
//                    id = R.id.project_detail;
//                }else if(text.trim().equals("风险控制")){
//                    id = R.id.risk_control;
//                }else if(text.trim().equals("投资列表")){
//                    id = R.id.invest_list;
//                }else if(text.trim().equals("还款记录")||text.trim().equals("投资还款计划")){
//                    id = R.id.repayment_plan;
//                }else if(text.trim().equals("待结标")){
//                    id=R.id.waitEnd;
//                }else if(text.trim().equals("还款中")){
//                    id=R.id.repaying;
//                }else if(text.trim().equals("已还款")){
//                    id=R.id.repayed;
//                }else if(text.trim().equals("充值")){
//                    id=R.id.recharge;
//                }else if(text.trim().equals("提现")){
//                    id=R.id.withdrawal;
//                }
//
//                return id;
//
//            }
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position));
            return views.get(position);
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position));
        }

        public ArrayList<View> getViews() {
            return views;
        }
    }
    /*
        * 给还款计划添加适配器及监听
        * */
    private void initRepaymentListView() {
        repayPlanListView.setOnTouchListener(ProjectDetailServer.this);
        repayPlanListView.setOnRefresh(ProjectDetailServer.this);
        repayAdapter = new RepaymentListAdapter(new ArrayList<RepaymentListItem>(), mContext);
        repayPlanListView.setBaseAdapter(repayAdapter);
        repayPlanListView.noHeadView();
    }

    /*
    * 给投资列表添加适配器及监听
    * */
    private void initInvestListView() {
        investListView.setOnTouchListener(ProjectDetailServer.this);
        investListView.setOnRefresh(ProjectDetailServer.this);
        investAdapter = new InvestListAdapter(new ArrayList<InvestListItem>(), mContext);
        investListView.setBaseAdapter(investAdapter);
        investListView.noHeadView();
    }

    /*
    * 给项目详情添加适配器及监听
    * */
    private void initProjectDetailListView() {
        projectDetailListView.setOnTouchListener(ProjectDetailServer.this);
       // projectDetailListView.setOnRefresh(ProjectDetailServer.this);
        projectDetailList.clear();
        projectDetailList.add(data);
        projectDetailAdapter = new ProjectDetailAdapter(projectDetailList,mContext);
        projectDetailListView.setAdapter(projectDetailAdapter);
        projectDetailAdapter.notifyDataSetChanged();
    }


    /*
  * 显示投资计划还款列表
  * */
    public void showInvestRepaymentList(String projectId, int page, int pageSize) {

    }
    /*
    * 加载投资列表数据
    * */
    public void showInvestList(String projectId,int page,int pageSize){

        for (int i = 0; i < pager.getViews().size(); i++) {
            if (pager.getViews().get(i).getTag().equals("INVEST_LIST")){
                isClickItem = true;
                viewPager.setCurrentItem(i);
                currentItem = i;
                break;
            }
        }
        loadBiddingData(projectId, page, pageSize);

    }

    private void loadBiddingData(String projectId,int page,int pageSize){
        this.InvestListPage = page;
        this.projectId = projectId;
        this.pageSize = pageSize;
        CURRENT_LIST = INVEST_LIST;
        if(investListItems!= null && investListItems.size() > 0){
            return;
        }
        getBiddingList(projectId, page, pageSize);
    }
    /*
   * 获取投资记录项目列表
   * */
    public void getBiddingList(String projectId,int page,int pageSize){

        loadding.showAs();
        BiddingListRequest request = new BiddingListRequest(mContext, new StringListener() {
            @Override
            public void ResponseListener(String result) {
                BiddingListResponse response = new BiddingListResponse();
                if(response.getStatus(result) == 200){
                    investListItems = response.getObject(result);
                    setInvestData();
                }else {
                    Handler handler = investListView.getHandler();
                    Message msg = handler.obtainMessage();
                    msg.what = Cst.COMMON.NO_MORE;
                    msg.obj = false;
                    handler.sendMessage(msg);
                    ((BaseActivity) mContext).toast(response.getMessage(result));
                }
                loadding.dismiss();
            }

            @Override
            public void ErrorListener(VolleyError volleyError) {
                loadding.dismiss();
                ((BaseActivity)mContext).showErrorMsg(mContext.getString(R.string.net_error),null);
            }
        },projectId,page,pageSize);
        request.stringRequest();
    }





    /*
    * 获取计划还款
    * */
    public void showRepaymentList(String projectId,int page,int pageSize){

        for (int i = 0; i < pager.getViews().size(); i++) {
            if (pager.getViews().get(i).getTag().equals("REPAYMENT_LIST")){
                isClickItem = true;
                viewPager.setCurrentItem(i);
                currentItem = i;
                break;
            }
        }
        loadRepaymentData(projectId, page, pageSize);

    }

    private void loadRepaymentData(String projectId,int page,int pageSize){
        this.RepaymentListPage = page;
        this.projectId = projectId;
        this.pageSize = pageSize;
        CURRENT_LIST = REPAYMENT_LIST;
        if(repaymentListItems!= null && repaymentListItems.size() > 0){
            return;
        }
        getRepaymentList(projectId, RepaymentListPage, pageSize);
    }

    /*
    * 获取还款计划项目列表
    * */
    public void getRepaymentList(String projectId,int page,int pageSize){
        loadding.showAs();
        RepaymentListRequest request = new RepaymentListRequest(mContext, new StringListener() {
            @Override
            public void ResponseListener(String result) {
                RepaymentListResponse response = new RepaymentListResponse();
                if(response.getStatus(result) == 200){
                    repaymentListItems = response.getObject(result);
                    setRepaymentData();
                }else {
                    Handler handler = repayPlanListView.getHandler();
                    Message msg = handler.obtainMessage();
                    msg.what = Cst.COMMON.NO_MORE;
                    msg.obj = false;
                    handler.sendMessage(msg);
                    ((BaseActivity)mContext).toast(response.getMessage(result));
                }
                loadding.dismiss();
            }

            @Override
            public void ErrorListener(VolleyError volleyError) {
                loadding.dismiss();
                ((BaseActivity)mContext).showErrorMsg(mContext.getString(R.string.net_error),null);
            }
        },projectId,page,pageSize);
        request.stringRequest();
    }


    public void showProjectDetailLayout(BaseModel data){
        for (int i = 0; i < pager.getViews().size(); i++) {
            if (pager.getViews().get(i).getTag().equals("PROJECT_DETAIL")){
                isClickItem = true;
                viewPager.setCurrentItem(i);
                currentItem = i;
                /*projectDetailList.clear();
                projectDetailList.add(data);
                projectDetailAdapter = new ProjectDetailAdapter(projectDetailList,mContext);
                projectDetailListView.setAdapter(projectDetailAdapter);
                projectDetailAdapter.notifyDataSetChanged();*/
                break;
            }
        }

    }


    /*
    * 给投资列表赋值
    * */
    private void setInvestData() {
        if(InvestListPage == 0){
            investAdapter.getData().clear();
        }
        investAdapter.getData().addAll(investListItems);
        investAdapter.notifyDataSetChanged();
    }
    /*
    * 给计划还款列表赋值
    * */
    private void setRepaymentData() {
        if(RepaymentListPage == 0){
           repayAdapter.getData().clear();
        }
        repayAdapter.getData().addAll(repaymentListItems);
        repayAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(((PullToRefresh) v).getChildAt(0) != null &&((PullToRefresh) v).getChildAt(0).getTop() < 0){
            return false;
        }
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                isFingerTouch = true;
                if(((PullToRefresh) v).getFirstVisible() == 0 ){
                    sY = event.getY();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float tempY = event.getY();
                float space = tempY - sY;
                if(((PullToRefresh) v).getFirstVisible() == 0 && space > 0){
                    onMove(event);
                    return true;
                }else {
                    return false;
                }
            case MotionEvent.ACTION_UP:
                if(((PullToRefresh) v).getFirstVisible() == 0){
                    if(state == UP){
                        state = NULL;
                        topMargin(-(showMoreLayout.getHeight() - moreInfoLayout.getHeight()));
                    }else if(state == UP_AT_MOST){
                        state = UP_TO_MOST;
                        topMargin(actionBarHeight);
                    }
                }else if(getTopMargin() > -(showMoreLayout.getHeight() - moreInfoLayout.getHeight())){
                    if(state == UP){
                        state = NULL;
                    }else {
                        state = UP_TO_MOST;
                    }
                    topMargin(actionBarHeight);
                }
                break;

        }

        return false;
    }

    private void onMove(MotionEvent event) {
        float tempY = event.getY();
        float space = tempY - sY;
        switch (state){
            case NULL:
                if(space > 0){
                    state = UP;
                    topMargin(space);
                }
                break;
            case UP:
                if(space > 0){
                    topMargin(space);
                    if(Math.abs(getTopMargin()) <= 2*(showMoreLayout.getMeasuredHeight() - moreInfoLayout.getMeasuredHeight())/3){
                        state = UP_AT_MOST;
                    }
                }
                break;
            case UP_AT_MOST:
                if(space > 0){
                    topMargin(space);
                }
        }
    }

    private void topMargin(float space) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) showMoreLayout.getLayoutParams();
        if(state == UP_TO_MOST){
            params.topMargin = actionBarHeight ;
        }else if(state == NULL){
            params.topMargin = -(showMoreLayout.getHeight() - moreInfoLayout.getHeight())+actionBarHeight ;
        }else {
            params.topMargin += (int) space ;
        }
        if(params.topMargin > actionBarHeight){
            params.topMargin = actionBarHeight;
        }
        showMoreLayout.setLayoutParams(params);
        showMoreLayout.postInvalidate();
        if(state == UP_TO_MOST){
            state = NULL;
        }
    }

    private int getTopMargin(){
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) showMoreLayout.getLayoutParams();
        return params.topMargin;
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    investAdapter.notifyDataSetChanged();
                    break;
                case 1:
                    repayAdapter.notifyDataSetChanged();
                    break;
                case GET_INVEST_SUCCESS://数据获取成功，当前页加一，为了下次取值
                    //investListItems = biddingServer.getInvestListItemArrayList();



                    break;
                case GET_REPAYMENT_SUCCESS://数据获取成功，当前页加一，为了下次取值



                    break;


            }
        }
    };


    @Override
    public void refresh(PullToRefresh target) {

    }

    @Override
    public void loadMore() {
        switch (CURRENT_LIST){
            case INVEST_LIST:
                if(investListItems!= null && investListItems.size() > 0){
                    InvestListPage++;
                }
                getBiddingList(projectId, InvestListPage, pageSize);
                break;
            case REPAYMENT_LIST:
                if(repaymentListItems!= null && repaymentListItems.size() > 0){
                    RepaymentListPage++;
                }
                getRepaymentList(projectId, RepaymentListPage, pageSize);
                break;
        }
    }



}
