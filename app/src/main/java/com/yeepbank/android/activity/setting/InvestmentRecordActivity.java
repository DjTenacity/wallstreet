package com.yeepbank.android.activity.setting;

import android.os.Handler;
import android.os.Message;

import android.os.SystemClock;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.yeepbank.android.Cst;
import com.yeepbank.android.R;
import com.yeepbank.android.activity.fragment.MeFragment;
import com.yeepbank.android.adapter.InvestmentRecordAdapter;
import com.yeepbank.android.adapter.InvestmentRecordWaitEndAdapter;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.model.SocketMsg;
import com.yeepbank.android.model.user.InvestmentVo;
import com.yeepbank.android.model.user.NeProjectVo;
import com.yeepbank.android.request.user.InvestmentRecordRequest;
import com.yeepbank.android.request.user.InvestmentRecordWaitendRequest;
import com.yeepbank.android.response.user.InvestmentRecordResponse;
import com.yeepbank.android.response.user.InvestmentRecordWaitendResponse;
import com.yeepbank.android.utils.LoadDialog;
import com.yeepbank.android.utils.Utils;
import com.yeepbank.android.widget.ChangeItemWithAnimationLayout;
import com.yeepbank.android.widget.view.XListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by dongxiaogang on 2015/10/28.
 * Ͷ投资记录
 */
public class InvestmentRecordActivity extends BaseActivity implements View.OnClickListener,Cst.OnSlideCompleteListener,XListView.IXListViewListener {
    private View navigationBar;
    private XListView waitEndListView,repayingListView,repayedListView;
    private ChangeItemWithAnimationLayout changeItemWithAnimationLayout;
    private ArrayList<InvestmentVo> projectArrayList=new ArrayList<InvestmentVo>();
    private ArrayList<InvestmentVo> project=new ArrayList<InvestmentVo>();
    private ArrayList<InvestmentVo> Investment=new ArrayList<InvestmentVo>();
    private ArrayList<InvestmentVo> InvestmentVoArrayList=new ArrayList<InvestmentVo>();
    private ArrayList<NeProjectVo> neProject=new ArrayList<NeProjectVo>();
    private ArrayList<NeProjectVo> neProjects=new ArrayList<NeProjectVo>();
    private int page = 0;//当前页数
    private int repayingCurrentPage=0;
    private int repayedCurrentPage=0;
    private static final String IRP = "IRP";//还款中
    private static final String NCL = "NCL";//已还款
    private static final String WAITEND ="WAITEND" ;//待结标的标志
    private static String status = WAITEND;

    private InvestmentRecordAdapter investmentRecordAdapter;
    private InvestmentRecordAdapter investmentRecordAdapter1;
    private InvestmentRecordWaitEndAdapter investmentRecordWaitEndAdapter;
    public  static int initChoose;
    private List<View>  viewList=new ArrayList<View>();//存放视图的集合
    private ViewPager viewPager;
    private LayoutInflater inflater;//页面布局管理器
    private View waitEndView,repayingView,repayedView;//三个视图分别是待结标，还款中，已还款
    private PagerAdapter pagerAdapter;
    private LinearLayout emptyViewWaitEnd;
    private int currentIndex;
    private int lastX = 0;
    private int curPage;
    private boolean isLast = true;
    private HashMap<String,ArrayList<SocketMsg>> socketMsgHashMap = null;



    @Override
    protected void initView() {

        navigationBar = findViewById(R.id.navigation_bar);
        inflater=getLayoutInflater();


        changeItemWithAnimationLayout = (ChangeItemWithAnimationLayout) findViewById(R.id.investment_record_navigation_item);
        changeItemWithAnimationLayout.setOnSlideCompleteListener(this);

        viewPager= (ViewPager) findViewById(R.id.id_viewPager);

        waitEndView=inflater.inflate(R.layout.one_fragment, null);

        waitEndListView= (XListView) waitEndView.findViewById(R.id.listView1);


        waitEndListView.setPullLoadEnable(true);
        waitEndListView.setXListViewListener(this);
        waitEndListView.setOverScrollMode(android.view.View.OVER_SCROLL_NEVER);

        investmentRecordWaitEndAdapter=new InvestmentRecordWaitEndAdapter(new ArrayList<NeProjectVo>(), mContext);

        waitEndListView.setAdapter(investmentRecordWaitEndAdapter);
        TextView emptyView = new TextView(mContext);
       emptyView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));//emptyView.setText("This appears when the list is empty");
        emptyView.setVisibility(View.GONE);
        ((ViewGroup)waitEndListView.getParent()).addView(emptyView);
        waitEndListView.setEmptyView(emptyView);


        viewList.add(waitEndView);

        repayingView=inflater.inflate(R.layout.one_fragment,null);
        repayingListView= (XListView) repayingView.findViewById(R.id.listView1);
        repayingListView.setPullLoadEnable(true);
        repayingListView.setXListViewListener(this);
        repayingListView.setOverScrollMode(android.view.View.OVER_SCROLL_NEVER);
        investmentRecordAdapter = new InvestmentRecordAdapter(new ArrayList<InvestmentVo>(),mContext);
        repayingListView.setAdapter(investmentRecordAdapter);


        TextView emptyView1 = new TextView(mContext);
        emptyView1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));//emptyView.setText("This appears when the list is empty");
        emptyView1.setVisibility(View.GONE);
        ((ViewGroup)repayingListView.getParent()).addView(emptyView1);
        repayingListView.setEmptyView(emptyView1);
        viewList.add(repayingView);

        repayedView=inflater.inflate(R.layout.one_fragment,null);
        repayedListView= (XListView) repayedView.findViewById(R.id.listView1);
        repayedListView.setPullLoadEnable(true);
        repayedListView.setXListViewListener(this);
        repayedListView.setOverScrollMode(android.view.View.OVER_SCROLL_NEVER);
        investmentRecordAdapter1 = new InvestmentRecordAdapter(new ArrayList<InvestmentVo>(), mContext);
        repayedListView.setAdapter(investmentRecordAdapter1);

        TextView emptyView2 = new TextView(mContext);
        emptyView2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));//emptyView.setText("This appears when the list is empty");
        emptyView2.setVisibility(View.GONE);
        ((ViewGroup)repayedListView.getParent()).addView(emptyView2);
        repayedListView.setEmptyView(emptyView2);
        viewList.add(repayedView);

        pagerAdapter=new PagerAdapter() {
            @Override
            public int getCount() {
                return viewList.size();
            }
            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }
            @Override
            public void destroyItem(ViewGroup view, int position, Object object) {
                view.removeView(viewList.get(position));
            }
            @Override
            public Object instantiateItem(ViewGroup view, int position) {
                view.addView(viewList.get(position));
                return viewList.get(position);
            }
            @Override
            public int getItemPosition(Object object) {
                return super.getItemPosition(object);
            }
        };
        viewPager.setAdapter(pagerAdapter);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                changeItemWithAnimationLayout.scroll(i);
                curPage=i;
                currentIndex = i;
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                if(i == 2) {
                    isLast = false;
                } else if(i == 0 && isLast) {
                    if(currentIndex==0){
                        InvestmentRecordActivity.this.finish();
                    }
                } else {
                    isLast = true;
                }
            }
        });
    }
/*
* 获取待结标数据
* */
    public void loadData(){
        loadding.showAs();
        InvestmentRecordWaitendRequest waitRequest=new InvestmentRecordWaitendRequest(mContext, new StringListener() {
            @Override
            public void ResponseListener(String result) {
                InvestmentRecordWaitendResponse waitendResponse=new InvestmentRecordWaitendResponse();
                if(waitendResponse.getStatus(result)==200){
                    neProject=waitendResponse.getDatas(result);
                    if(neProject!=null&&neProject.size() > 0) {
                        neProjects.addAll(neProject);
                        investmentRecordWaitEndAdapter.getData().clear();
                        investmentRecordWaitEndAdapter.getData().addAll(neProjects);
                        investmentRecordWaitEndAdapter.notifyDataSetChanged();
                    }
                    loadding.dismiss();
                    if(neProject.size()< 10){
                        waitEndListView.setIsLoadStop(1);
                    }else{
                        waitEndListView.setIsLoadStop(0);
                    }
                }else{
                    waitEndListView.setIsLoadStop(1);
                    toast(waitendResponse.getMessage(result));
                    loadding.dismiss();


                }

            }

            @Override
            public void ErrorListener(VolleyError volleyError) {
                loadding.dismiss();
                showErrorMsg(getString(R.string.net_error),navigationBar);
            }
        },Cst.currentUser.investorId,page,Cst.PARAMS.PAGE_SIZE);
        waitRequest.stringRequest();
    }

    /*
    * 获取还款中的数据
    * */
    public void initData(){
                InvestmentRecordRequest request = new InvestmentRecordRequest(mContext, new StringListener() {
                    @Override
                    public void ResponseListener(String result) {
                        InvestmentRecordResponse response = new InvestmentRecordResponse();
                        if (response.getStatus(result) == 200) {
                            projectArrayList = response.getObject(result);
                            if(projectArrayList!=null&& projectArrayList.size()>0) {
                                project.addAll(projectArrayList);
                                investmentRecordAdapter.getData().clear();
                                investmentRecordAdapter.getData().addAll(project);
                                investmentRecordAdapter.notifyDataSetChanged();
                                if(projectArrayList.size()<10){
                                    repayingListView.setIsLoadStop(1);
                                }else{
                                    repayingListView.setIsLoadStop(0);
                                }
                            }
                        } else {
                            repayingListView.setIsLoadStop(1);

                                toast(response.getMessage(result));

                        }
                    }
                    @Override
                    public void ErrorListener(VolleyError volleyError) {
                        dismiss();
                        showErrorMsg(getString(R.string.net_error));
                    }
                }, Cst.currentUser.investorId,status,repayingCurrentPage,100);
                request.stringRequest();
    }
    /*
    * 获取已还款的数据
    * */
    public void setData(){
        loadding.showAs();
        if(Cst.currentUser!=null) {
            if(status.equals(NCL)){
                InvestmentRecordRequest request1 = new InvestmentRecordRequest(mContext, new StringListener() {
                    @Override
                    public void ResponseListener(String result) {
                        InvestmentRecordResponse response1 = new InvestmentRecordResponse();
                        if (response1.getStatus(result) == 200) {
                            Investment = response1.getObject(result);
                            if(Investment!=null&& Investment.size()>0) {
                                InvestmentVoArrayList.addAll(Investment);
                                investmentRecordAdapter1.getData().clear();
                                investmentRecordAdapter1.getData().addAll(InvestmentVoArrayList);
                                investmentRecordAdapter1.notifyDataSetChanged();
                            }
                            if(Investment.size()<10){
                                repayedListView.setIsLoadStop(1);
                            }else{
                                repayedListView.setIsLoadStop(0);
                            }
                            loadding.dismiss();
                        } else {
                            repayedListView.setIsLoadStop(1);
                            toast(response1.getMessage(result));
                            loadding.dismiss();

                        }
                    }
                    @Override
                    public void ErrorListener(VolleyError volleyError) {
                        dismiss();
                        showErrorMsg(getString(R.string.net_error));
                    }
                }, Cst.currentUser.investorId,status,repayedCurrentPage,10);
                request1.stringRequest();
            }
        }
    }
    @Override
    protected void fillData() {
        /*
         * 获取socket信息 显示小红点
         * */
        changeItemWithAnimationLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                changeItemWithAnimationLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                socketMsgHashMap = Utils.getInstances().getMeFragmentRedDocFromSharedPreference(mContext);
                if (socketMsgHashMap!= null &&
                        socketMsgHashMap.get("bidding") != null &&
                        socketMsgHashMap.get("bidding").size() > 0){
                    if (has(SocketMsg.BIDDING_END)){
                        if (initChoose == R.id.waitEnd){
                            changeItemWithAnimationLayout.setRedDocVisible(View.VISIBLE,R.id.repaying);

                        }else if (initChoose == R.id.repaying){
                            changeItemWithAnimationLayout.setRedDocVisible(View.GONE, R.id.repaying);
                            for (int i = 0; i < socketMsgHashMap.get("bidding").size(); i++) {
                                if (socketMsgHashMap.get("bidding").get(i).activeCode == SocketMsg.BIDDING_END){
                                    socketMsgHashMap.get("bidding").remove(i);
                                    Utils.getInstances().putMeFragmentRedDocToSharedPreference(mContext, socketMsgHashMap);
                                    break;
                                }
                            }

                        }
                    }

                    if (has(SocketMsg.BIDDING_END_REPAYMENTED)){
                        if (initChoose == R.id.repayed){
                            changeItemWithAnimationLayout.setRedDocVisible(View.GONE, R.id.repayed);
                            for (int i = 0; i < socketMsgHashMap.get("bidding").size(); i++) {
                                if (socketMsgHashMap.get("bidding").get(i).activeCode == SocketMsg.BIDDING_END_REPAYMENTED){
                                    socketMsgHashMap.get("bidding").remove(i);
                                    Utils.getInstances().putMeFragmentRedDocToSharedPreference(mContext, socketMsgHashMap);
                                    break;
                                }
                            }

                        }else{
                            changeItemWithAnimationLayout.setRedDocVisible(View.VISIBLE, R.id.repayed);


                        }
                    }
                }
            }
        });


        /*
         * 判断如果投资记录上有小红点，点击后小红点消失
         * */
//        if (socketMsgHashMap!= null &&
//                socketMsgHashMap.get("bidding") != null &&
//                socketMsgHashMap.get("bidding").size() > 0){
//            socketMsgHashMap.get("bidding").clear();
//            Utils.getInstances().putMeFragmentRedDocToSharedPreference(mContext, socketMsgHashMap);
//        }
    }

    private boolean has(int activityCode) {
        if (socketMsgHashMap != null && socketMsgHashMap.get("bidding") != null && socketMsgHashMap.get("bidding").size() > 0){
            for (int i = 0; i < socketMsgHashMap.get("bidding").size(); i++) {
                if (socketMsgHashMap.get("bidding").get(i).activeCode == activityCode){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected int getLayoutResources() {
        return R.layout.investment_record;
    }

    @Override
    protected View getNavigationBar() {
        return  navigationBar;
    }

    @Override
    public void onClick(View v) {




    }


    @Override
    public void onComplete(View view) {
        witchBusiness(view.getId());
        if (has(SocketMsg.BIDDING_END_REPAYMENTED) && view.getId() == R.id.repayed){
            changeItemWithAnimationLayout.setRedDocVisible(View.GONE, R.id.repayed);
            for (int i = 0; i < socketMsgHashMap.get("bidding").size(); i++) {
                if (socketMsgHashMap.get("bidding").get(i).activeCode == SocketMsg.BIDDING_END_REPAYMENTED){
                    socketMsgHashMap.get("bidding").remove(i);
                    Utils.getInstances().putMeFragmentRedDocToSharedPreference(mContext, socketMsgHashMap);
                    break;
                }
            }
        }
        if (has(SocketMsg.BIDDING_END) && view.getId() == R.id.repaying){
            changeItemWithAnimationLayout.setRedDocVisible(View.GONE, R.id.repaying);
            for (int i = 0; i < socketMsgHashMap.get("bidding").size(); i++) {
                if (socketMsgHashMap.get("bidding").get(i).activeCode == SocketMsg.BIDDING_END){
                    socketMsgHashMap.get("bidding").remove(i);
                    Utils.getInstances().putMeFragmentRedDocToSharedPreference(mContext, socketMsgHashMap);
                    break;
                }
            }
        }
    }

    private void witchBusiness(int id){
        switch (id){
            case R.id.waitEnd:
                status=WAITEND;
                /*if(neProjects!=null&&neProjects.size()>0){
                    neProjects.clear();
                }
                page=0;
                loadData();
                viewPager.setCurrentItem(0);*/
                if(neProjects.size()==0){
                    page=0;
                    loadData();
                    viewPager.setCurrentItem(0);
                }else{
                    viewPager.setCurrentItem(0);
                }
                break;
            case R.id.repaying:
                status=IRP;
                /*repayingCurrentPage=0;
                if(project.size()>0){
                    project.clear();
                }
                initData();
                viewPager.setCurrentItem(1);*/
                if(project.size()==0){
                    repayingCurrentPage=0;
                    initData();
                    viewPager.setCurrentItem(1);
                }else{
                    viewPager.setCurrentItem(1);
                }
                break;
            case R.id.repayed:
                status = NCL;
               /* repayedCurrentPage=0;
                if(InvestmentVoArrayList.size()>0){
                    InvestmentVoArrayList.clear();
                }
                setData();
                viewPager.setCurrentItem(2);*/
                if(InvestmentVoArrayList.size()==0){
                    repayedCurrentPage=0;
                    setData();
                    viewPager.setCurrentItem(2);
                }else{
                    viewPager.setCurrentItem(2);
                }
                break;
        }
    }

    public void loadInitChooseData( int id){
        witchBusiness(id);
    }

    @Override
    public void onRefresh() {
        if(status.equals(WAITEND)){
            page=0;

            if(neProjects!=null&&neProjects.size()>0){
                neProjects.clear();
            }
            loadData();
            onLoad();
        }else if(status.equals(IRP)){
            repayingCurrentPage=0;
            if(project!=null&&project.size()>0){
                project.clear();
            }
            initData();
            onLoad();
        }else if(status.equals(NCL)){
            repayedCurrentPage=0;
            if(InvestmentVoArrayList!=null&&InvestmentVoArrayList.size()>0){
                InvestmentVoArrayList.clear();
            }
            setData();
            onLoad();
        }
    }

    @Override
    public void onLoadMore() {
        if(status.equals(WAITEND)){
            page++;
            loadData();
            onLoad();
        }else if(status.equals(IRP)){
            repayingCurrentPage++;
            initData();
            onLoad();
        }else if(status.equals(NCL)){
            repayedCurrentPage++;
            setData();
            onLoad();
        }


    }
    private void onLoad() {
        if(status.equals(WAITEND)){
            waitEndListView.stopRefresh();
            waitEndListView.stopLoadMore();
        }else if(status.equals(IRP)){
            repayingListView.stopRefresh();
            repayingListView.stopLoadMore();
        }else if(status.equals(NCL)){
            repayedListView.stopRefresh();
            repayedListView.stopLoadMore();
        }
    }

    @Override
    protected void onDestroy() {
        if(loadding != null && loadding.isShowing()){
            loadding.dismiss();
        }
        MeFragment.mHandler.sendEmptyMessage(0);
        super.onDestroy();
    }

    @Override
    public LoadDialog getLoadDialog() {
        return loadding;
    }
}
