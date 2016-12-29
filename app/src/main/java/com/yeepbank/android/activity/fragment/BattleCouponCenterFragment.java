package com.yeepbank.android.activity.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.android.volley.VolleyError;
import com.yeepbank.android.Cst;
import com.yeepbank.android.R;
import com.yeepbank.android.activity.business.HomeActivity;
import com.yeepbank.android.activity.setting.CouponsActivity;
import com.yeepbank.android.adapter.BattleCouponCenterAdapter;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.base.BaseFragment;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.model.business.BattleCouponModel;
import com.yeepbank.android.request.business.BattleListRequest;
import com.yeepbank.android.request.business.BattleRequest;
import com.yeepbank.android.response.business.BattleListResponse;
import com.yeepbank.android.response.business.BattleResponse;
import com.yeepbank.android.response.user.LoginAndRegisterResponse;
import com.yeepbank.android.server.LoginAndRegisterServer;
import com.yeepbank.android.utils.DateUtils;
import com.yeepbank.android.utils.LoadDialog;
import com.yeepbank.android.utils.Utils;
import com.yeepbank.android.widget.view.XListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by xiaogang.dong on 2016/8/19.
 * 抢券中心
 */
public class BattleCouponCenterFragment extends BaseFragment implements View.OnClickListener,XListView.IXListViewListener,AdapterView.OnItemClickListener{
    private View navigationBar;
    private ImageView meCoupon;
    private LoadDialog msgDialog;//登录的弹窗
    private LoginAndRegisterServer server;
    private Handler handler;
    private XListView listView;
    private ArrayList<BattleCouponModel> couponList=new ArrayList<BattleCouponModel>();//数据的集合
    private BattleCouponCenterAdapter adapter;
    private ArrayList<BattleCouponModel> middleList=new ArrayList<BattleCouponModel>();//中间的数据集合
    private ArrayList<BattleCouponModel> lastList=new ArrayList<BattleCouponModel>();//中间的数据集合
    private final Timer timer = new Timer();
    private TimerTask task;
    private int currentPage=0;
    private LoadDialog loadding;
    private boolean hasCreated = false;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    //Toast.makeText(getActivity(),"定时器调用啦",Toast.LENGTH_LONG).show();
                    //Log.e("+++++","定时器调用啦");
                    adapter.getData().clear();
                    adapter.getData().addAll(lastList);
                    adapter.notifyDataSetChanged();
                    break;
            }

        }

    };
    @Override
    public void initView(View view) {
        loadding = ((BaseActivity)getActivity()).getLoadding();
        navigationBar=view.findViewById(R.id.navigation_bar);
        msgDialog=new LoadDialog(getActivity(), R.style.dialog, false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msgDialog.dismiss();
                showLoginPage();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msgDialog.dismiss();
            }
        },0);

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
               switch (msg.what){
                   case Cst.CMD.LOGIN_SUCCESS:
                   case Cst.CMD.REGISTER_SUCCESS:
                       ((BaseActivity)getActivity()).cancelMsg();
                       Cst.currentUser=server.getInvestor();
                       onShow(getActivity());
                   break;
               }
            }
        };

        listView= (XListView) view.findViewById(R.id.listview);

        adapter=new BattleCouponCenterAdapter(couponList,getActivity());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setPullLoadEnable(true);
        listView.setXListViewListener(this);
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);


        task = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                mHandler.sendMessage(message);
            }
        };

        timer.schedule(task, 45000, 45000);

    }

    @Override
    public int getLayoutResource() {
        return R.layout.battle_coupon_center;
    }

    @Override
    public void fillData() {
        TextView labelText = (TextView) navigationBar.findViewById(R.id.label_text);
        labelText.setText("抢券中心");
        ImageView backLogoImg = (ImageView) navigationBar.findViewById(R.id.back_logo);
        backLogoImg.setVisibility(View.GONE);
        meCoupon= (ImageView) navigationBar.findViewById(R.id.me_coupon);
        meCoupon.setVisibility(View.VISIBLE);
        meCoupon.setOnClickListener(this);

    }

    @Override
    public void onShow(Context context) {
        //Toast.makeText(getActivity(), DateUtils.getDateToString(1472711655270l),Toast.LENGTH_LONG).show();
        //Toast.makeText(getActivity(), DateUtils.getStringToDate("2016年9月1日")+"",Toast.LENGTH_LONG).show();
        //Toast.makeText(getActivity(), new Date().getTime()+"",Toast.LENGTH_LONG).show();
        hideRedDoc();
        currentPage=0;
        if(lastList!=null&&lastList.size()>0){
            lastList.clear();

        }
        if(middleList!=null&&middleList.size()>0){
            middleList.clear();
        }
        loadData();


    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.me_coupon:
                if(Cst.currentUser == null){
                msgDialog.setMessage("你未登录，是否立即登录");
                msgDialog.setSureBtn("立即登录");
                msgDialog.setCancelBtn("再看看");
                msgDialog.showAs();
                }else{
                    ((BaseActivity)getActivity()).gotoMyCoupon();
                }
                break;

        }
    }
    private void showLoginPage(){
        ((BaseActivity)getActivity()).showDialogWindow(R.layout.login_and_register, R.style.exist_style, new BaseActivity.OnShowListener() {
            @Override
            public void show(View view) {
                rotation(view);
            }
        });
    }
    private void rotation(View view){
        server = new LoginAndRegisterServer(getActivity(),view,handler);
    }

    @Override
    public void onRefresh() {
            currentPage=0;
        if(lastList!=null&&lastList.size()>0){
            lastList.clear();
        }
            loadData();
            onLoad();
    }

    @Override
    public void onLoadMore() {
            currentPage++;
        loadData();
        onLoad();
    }
    private void onLoad() {

        listView.stopRefresh();
        listView.stopLoadMore();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(position == 0 || position > adapter.getData().size()){
            return;
        }
        if(Cst.currentUser==null&&adapter.getData().get(position-1).couponStatus.equals("0")){
            msgDialog.setMessage("你未登录，是否立即登录");
            msgDialog.setSureBtn("立即登录");
            msgDialog.setCancelBtn("再看看");
            msgDialog.showAs();
        }else if(Cst.currentUser!=null&&adapter.getData().get(position-1).couponStatus.equals("0")&&adapter.getData().get(position-1).receiveStartTimeStamp<new Date().getTime()&&adapter.getData().get(position-1).receiveEndTimeStamp>new Date().getTime()){
           int index=position-1;
            //lll.get(position-1).setStatus("U");
            /*view.findViewById(R.id.battle_coupon_riqi_layout).setVisibility(View.GONE);
            ((TextView)view.findViewById(R.id.battle_coupon_status)).setText("已经领取");
            view.findViewById(R.id.coupon_center_item_left_layout).setBackgroundResource(R.drawable.coupon_al_left);
            view.findViewById(R.id.coupon_center_item_right_layout).setBackgroundResource(R.drawable.coupon_al_right);*/
            battle(adapter.getData().get(position-1).couponBatchNo,index);
        }
    }
    //获取券的列表的信息
    private void loadData(){
        //loadding.showAs();
        //String userId = Cst.currentUser == null? "0":Cst.currentUser.investorId;
        BattleListRequest request=new BattleListRequest(getActivity(), new StringListener() {
            @Override
            public void ResponseListener(String result) {

                BattleListResponse response=new BattleListResponse();
                if(response.getStatus(result)==200){
                    //loadding.dismiss();

                    middleList=response.getObject(result);
                    if(middleList!=null&&middleList.size()>0){
                        lastList.addAll(middleList);
                        adapter.getData().clear();
                        adapter.getData().addAll(lastList);
                        adapter.notifyDataSetChanged();
                        Log.e("------", lastList.size() + "" + middleList.size());
                    }

                }else{
                    //loadding.dismiss();
                }
            }

            @Override
            public void ErrorListener(VolleyError volleyError) {
                loadding.dismiss();
                ((BaseActivity)getActivity()).showErrorMsg(getString(R.string.net_error), null);
            }
        },currentPage,10);
        request.stringRequest();
    }

    private void battle(String couponBatchNo, final int index){
        loadding.showAs();
        BattleRequest request=new BattleRequest(getActivity(), new StringListener() {
            @Override
            public void ResponseListener(String result) {
                BattleResponse response=new BattleResponse();
                if(response.getStatus(result)==200){
                    loadding.dismiss();
                    ((BaseActivity)getActivity()).toast("领券成功");
                    lastList.get(index).setCouponStatus("3");
                }else{
                    loadding.dismiss();
                    BattleCouponModel model=response.getObject(result);
                    if(model!=null){
                        ((BaseActivity)getActivity()).toast(response.getMessage(result));
                        lastList.get(index).setCouponStatus(model.getCouponStatus());
                    }else {
                        ((BaseActivity)getActivity()).toast(response.getMessage(result));
                    }
                }
                adapter.getData().clear();
                adapter.getData().addAll(lastList);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void ErrorListener(VolleyError volleyError) {
                loadding.dismiss();
                ((BaseActivity)getActivity()).showErrorMsg(getString(R.string.net_error), null);
            }
        },couponBatchNo,Cst.currentUser.investorId);
        request.stringRequest();
    }
    private void hideRedDoc(){
            if (HomeActivity.currentFragment instanceof BattleCouponCenterFragment && Utils.getInstances().getBattleCouponFragmentTabRedDocToSharedPreference(getActivity())){
                Utils.getInstances().putBattleCouponFragmentTabRedDocToSharedPreference(getActivity(), false);
                ((HomeActivity)getActivity()).hideRedDot(HomeActivity.battleCouponBtn);
            }

    }


}
