package com.yeepbank.android.activity.user;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.yeepbank.android.Cst;
import com.yeepbank.android.R;
import com.yeepbank.android.activity.business.HomeActivity;
import com.yeepbank.android.adapter.RunningAccountAdapter;
import com.yeepbank.android.base.ActivityStacks;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.model.user.RechargeRecordModel;
import com.yeepbank.android.model.user.RunningAccount;
import com.yeepbank.android.model.user.WithDrawRecordModel;
import com.yeepbank.android.request.user.RechargeRecordRequest;
import com.yeepbank.android.request.user.WithDrawRecordRequest;
import com.yeepbank.android.response.user.RechargeRecordResponse;
import com.yeepbank.android.response.user.WithDrawRecordResponse;
import com.yeepbank.android.utils.LoadDialog;
import com.yeepbank.android.utils.Utils;
import com.yeepbank.android.widget.ChangeItemWithAnimationLayout;
import com.yeepbank.android.widget.PullToRefresh;
import com.yeepbank.android.widget.RefreshScrollerView;
import com.yeepbank.android.widget.SwitchItemLayout;
import com.yeepbank.android.widget.view.XListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by WW on 2016/3/15.
 * 资金流水
 */
public class RunningAccountActivity extends BaseActivity implements Cst.OnSlideCompleteListener,AdapterView.OnItemClickListener,XListView.IXListViewListener{

    private View navigationBar;
    private ChangeItemWithAnimationLayout changeItemWithAnimationLayout;
    private boolean isFirst = true;
    private ImageView triangleImg;
    private HashMap<Integer,ArrayList<RunningAccount>> runningAccountMap;
    private XListView runningListRecharge,runningListWithdraw;
    private RunningAccountAdapter runningAccountRechargeAdapter,runningAccountWithDrawAdapter;
    private ArrayList<RechargeRecordModel> rechargeRecordList=new ArrayList<RechargeRecordModel>();
    private ArrayList<WithDrawRecordModel> withDrawRecordList=new ArrayList<WithDrawRecordModel>();
    private static int currentRechargePage=0;
    private static int currentWithDrawPage=0;
    private boolean isPullToRefresh = false;
    private TextView noRechargeData,noWithdrawData;
    public static int initChoose = R.id.recharge;

    @Override
    protected void initView() {
        navigationBar = findViewById(R.id.navigation_bar);
        changeItemWithAnimationLayout = (ChangeItemWithAnimationLayout) findViewById(R.id.navigation_item);
        triangleImg = (ImageView) findViewById(R.id.triangle);
        runningAccountMap = new HashMap<Integer, ArrayList<RunningAccount>>();
        runningAccountMap.put(R.id.recharge, new ArrayList<RunningAccount>());
        runningAccountMap.put(R.id.withdrawal, new ArrayList<RunningAccount>());
        changeItemWithAnimationLayout.setOnSlideCompleteListener(this);


        noRechargeData= (TextView) findViewById(R.id.no_recharge_data);
        noWithdrawData= (TextView) findViewById(R.id.no_withdraw_data);

        runningListRecharge = (XListView) findViewById(R.id.running_account_list_recharge);
        runningListRecharge.setEmptyView(noRechargeData);
        runningAccountRechargeAdapter = new RunningAccountAdapter(new ArrayList<RunningAccount>(),mContext);
        runningListRecharge.setAdapter(runningAccountRechargeAdapter);
        runningListRecharge.setOnItemClickListener(this);
        runningListRecharge.setPullLoadEnable(true);
        runningListRecharge.setXListViewListener(this);
        runningListRecharge.setOverScrollMode(View.OVER_SCROLL_NEVER);
        runningListRecharge.setVisibility(View.VISIBLE);

        runningListWithdraw= (XListView) findViewById(R.id.running_account_list_withdraw);


        runningAccountWithDrawAdapter = new RunningAccountAdapter(new ArrayList<RunningAccount>(),mContext);
        runningListWithdraw.setAdapter(runningAccountWithDrawAdapter);
        runningListWithdraw.setPullLoadEnable(true);
        runningListWithdraw.setXListViewListener(this);
        runningListWithdraw.setOverScrollMode(View.OVER_SCROLL_NEVER);
        runningListWithdraw.setVisibility(View.GONE);
        //SWITCH_BTN = R.id.recharge;

        changeItemWithAnimationLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                changeItemWithAnimationLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        changeItemWithAnimationLayout.setRedDocVisible(View.VISIBLE);
//                    }
//                },20);
            }
        });

    }

    @Override
    protected void fillData() {
        new Thread(){
            @Override
            public void run() {
                while (true){
                    if(changeItemWithAnimationLayout!= null &&
                            changeItemWithAnimationLayout.getWidth() > 0){
                        moveTriangleImg();
                        break;
                    }
                }
            }
        }.start();
        if(runningAccountMap.get(R.id.recharge)!=null&&runningAccountMap.get(R.id.recharge).size()>0){
            runningAccountMap.get(R.id.recharge).clear();

        }
        currentRechargePage=0;

    }

    @Override
    protected void onResume() {
        if (initChoose == R.id.withdrawal){
            getWithDrawRecord();
        }else{
            getRechargeRecord();
        }
        super.onResume();
    }

    @Override
    protected int getLayoutResources() {
        return R.layout.running_account_list;
    }

    @Override
    protected View getNavigationBar() {
        return navigationBar;
    }

    @Override
    protected void backBtnTap() {
        ActivityStacks.getInstances().popToWitch(HomeActivity.class.getName());
    }

    @Override
    public LoadDialog getLoadDialog() {
        return loadding;
    }

    /*获取到控件尺寸后把箭头移向第一个选项中间位置*/

    private void moveTriangleImg(){
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) triangleImg.getLayoutParams();
        if (params != null) {
            params.leftMargin = changeItemWithAnimationLayout.getCheckLayoutWidth() / 2 - triangleImg.getWidth() / 2;
            triangleImg.setLayoutParams(params);
        }
    }
    public void moveTriangle(int dx,int ml,int width){
        Animation tAnimation = new TranslateAnimation(0,dx,0,0);
        tAnimation.setFillAfter(true);
        tAnimation.setDuration(300);
        tAnimation.setInterpolator(new AccelerateInterpolator());
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) triangleImg.getLayoutParams();
        params.leftMargin = ml+width/2;
        triangleImg.setLayoutParams(params);
        triangleImg.setAnimation(tAnimation);
        triangleImg.startAnimation(tAnimation);
    }

    @Override
    public void onComplete(View view) {
        switch (view.getId()){
            case R.id.recharge:
                initChoose=R.id.recharge;
                runningListWithdraw.setVisibility(View.GONE);
                runningListRecharge.setVisibility(View.VISIBLE);
                noRechargeData.setVisibility(View.VISIBLE);
                noWithdrawData.setVisibility(View.GONE);
                runningListRecharge.setEmptyView(noRechargeData);
                if(runningAccountMap.get(initChoose)!=null&&runningAccountMap.get(initChoose).size()>0){
                    return;
                }else {
                    getRechargeRecord();
                }
                break;
            case R.id.withdrawal:
                initChoose=R.id.withdrawal;
                runningListWithdraw.setVisibility(View.VISIBLE);
                runningListRecharge.setVisibility(View.GONE);
                noRechargeData.setVisibility(View.GONE);
                noWithdrawData.setVisibility(View.VISIBLE);
                runningListWithdraw.setEmptyView(noWithdrawData);

                    if(runningAccountMap.get(initChoose)!=null&&runningAccountMap.get(initChoose).size()>0){
                        return;
                    }else {

                        getWithDrawRecord();
                    }
//                    currentWithDrawPage=0;
//                    isFirst=false;
//                }
                break;
        }


    }

    private void setData() {
        if(initChoose==R.id.recharge){
            runningListRecharge.setVisibility(View.VISIBLE);
            runningListWithdraw.setVisibility(View.GONE);
            runningAccountRechargeAdapter.getData().clear();
            runningAccountRechargeAdapter.getData().addAll(runningAccountMap.get(R.id.recharge));
            runningAccountRechargeAdapter.notifyDataSetChanged();
        }else if(initChoose==R.id.withdrawal){
            runningListRecharge.setVisibility(View.GONE);
            runningListWithdraw.setVisibility(View.VISIBLE);
            runningAccountWithDrawAdapter.getData().clear();
            runningAccountWithDrawAdapter.getData().addAll(runningAccountMap.get(R.id.withdrawal));
            runningAccountWithDrawAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(position == 0 || position > runningAccountRechargeAdapter.getData().size()){
                return;
            }
        if(runningAccountRechargeAdapter.getData().get(position-1).success.equals("F")){
            if(!runningAccountRechargeAdapter.getData().get(position-1).reasonIsOpen){
                view.findViewById(R.id.fail_reason).setVisibility(View.VISIBLE);
                view.findViewById(R.id.open_btn).setRotationX(180f);
                runningAccountRechargeAdapter.getData().get(position-1).reasonIsOpen = true;
            }else {
                view.findViewById(R.id.fail_reason).setVisibility(View.GONE);
                view.findViewById(R.id.open_btn).setRotationX(0f);
                runningAccountRechargeAdapter.getData().get(position-1).reasonIsOpen = false;
            }

        }
    }

    /*
    获取充值记录
     */
    public void getRechargeRecord(){
        loadding.showAs();
        RechargeRecordRequest request=new RechargeRecordRequest(mContext, new StringListener() {
            @Override
            public void ResponseListener(String result) {
                RechargeRecordResponse response=new RechargeRecordResponse();
                if(response.getStatus(result)==200){
                    rechargeRecordList=response.getObject(result);
                    setRechargeData();
                    setData();
                    loadding.dismiss();
                }else{
                    toast(response.getMessage(result));
                    loadding.dismiss();
                }
            }

            @Override
            public void ErrorListener(VolleyError volleyError) {
                    loadding.dismiss();
                    showErrorMsg(getString(R.string.net_error));
            }
        },Cst.currentUser.investorId,currentRechargePage,10);
        request.stringRequest();
    }
    /*
    获取提现记录
     */
    public void getWithDrawRecord(){
        loadding.showAs();
        WithDrawRecordRequest request=new WithDrawRecordRequest(mContext, new StringListener() {
            @Override
            public void ResponseListener(String result) {
                WithDrawRecordResponse response=new WithDrawRecordResponse();
                if(response.getStatus(result)==200){
                    withDrawRecordList=response.getObject(result);
                    setWithDrawRecordData();
                    loadding.dismiss();
                }else{
                    toast(response.getMessage(result));
                    loadding.dismiss();
                }
            }

            @Override
            public void ErrorListener(VolleyError volleyError) {
                    loadding.dismiss();
                    showErrorMsg(getString(R.string.net_error));
            }
        },Cst.currentUser.investorId,currentWithDrawPage,10);
        request.stringRequest();
    }
    public void setRechargeData(){
        for (int i = 0; i < rechargeRecordList.size(); i++) {
            RunningAccount runningAccount = new RunningAccount();
            runningAccount.type = "recharge";
            runningAccount.happenDate = rechargeRecordList.get(i).depositDate;
            runningAccount.happenTime = rechargeRecordList.get(i).depositTime;

            runningAccount.happenMoney = Utils.getInstances().thousandFormat(Double.parseDouble(rechargeRecordList.get(i).amount == null?"0" : rechargeRecordList.get(i).amount));

            runningAccount.failReason = rechargeRecordList.get(i).faileReason == null ? "" : rechargeRecordList.get(i).faileReason;
            runningAccount.success =rechargeRecordList.get(i).status;

            runningAccount.stateName=rechargeRecordList.get(i).statusName;
            runningAccount.reasonIsOpen = false;
            runningAccountMap.get(initChoose).add(runningAccount);
        }

    }
    public void setWithDrawRecordData(){
        for(int i=0;i<withDrawRecordList.size();i++){
            RunningAccount runningAccount = new RunningAccount();
            runningAccount.type = "withdrawals";
           runningAccount.requestDatetime=withDrawRecordList.get(i).requestDatetime;
            runningAccount.happenMoney = Utils.getInstances().thousandFormat(Double.parseDouble(withDrawRecordList.get(i).amount));
            if(withDrawRecordList.get(i).status.equals("E")) {
                runningAccount.approveDatetime=withDrawRecordList.get(i).approveDatetime;
            }else{
                runningAccount.approveDatetime="";
            }
            runningAccount.success =withDrawRecordList.get(i).status;
            runningAccount.stateName=withDrawRecordList.get(i).statusName;
            runningAccount.reasonIsOpen = false;
            runningAccountMap.get(initChoose).add(runningAccount);

        }
        setData();
    }

    @Override
    public void onRefresh() {
        if(initChoose==R.id.recharge){
            currentRechargePage=0;
            if(rechargeRecordList!=null&&rechargeRecordList.size()>0){
                rechargeRecordList.clear();
            }
            if(runningAccountMap.get(initChoose)!=null&&runningAccountMap.get(initChoose).size()>0){
                runningAccountMap.get(initChoose).clear();
            }
            getRechargeRecord();
            onLoad();
        }else if(initChoose==R.id.withdrawal){
            currentWithDrawPage=0;
            if(withDrawRecordList!=null&&withDrawRecordList.size()>0){
                withDrawRecordList.clear();
            }
            if(runningAccountMap.get(initChoose)!=null&&runningAccountMap.get(initChoose).size()>0){
                runningAccountMap.get(initChoose).clear();
            }
            getWithDrawRecord();
            onLoad();
        }
    }

   @Override
    public void onLoadMore() {
        if(initChoose==R.id.recharge){
            currentRechargePage++;
            getRechargeRecord();
            onLoad();
        }else if(initChoose==R.id.withdrawal){
            currentWithDrawPage++;
            getWithDrawRecord();
            onLoad();
        }

    }
    private void onLoad() {
        if(initChoose==R.id.recharge){
            runningListRecharge.stopRefresh();
            runningListRecharge.stopLoadMore();
        }else if(initChoose==R.id.withdrawal){
            runningListWithdraw.stopRefresh();
            runningListWithdraw.stopLoadMore();
        }
    }
    @Override
    protected void onDestroy() {
        initChoose = R.id.recharge;
        if(loadding != null && loadding.isShowing()){
            loadding.dismiss();
        }
        super.onDestroy();
    }
}
