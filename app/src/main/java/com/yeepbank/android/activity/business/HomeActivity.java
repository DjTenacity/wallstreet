package com.yeepbank.android.activity.business;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.*;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import android.widget.*;
import com.android.volley.VolleyError;
import com.yeepbank.android.Cst;
import com.yeepbank.android.R;
import com.yeepbank.android.UpdateService;
import com.yeepbank.android.WebSocketService;
import com.yeepbank.android.activity.fragment.*;
import com.yeepbank.android.activity.setting.CouponsActivity;
import com.yeepbank.android.activity.setting.InvestmentRecordActivity;
import com.yeepbank.android.activity.user.RunningAccountActivity;
import com.yeepbank.android.base.ActivityStacks;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.base.BaseFragment;
import com.yeepbank.android.base.BaseModel;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.model.SocketMsg;
import com.yeepbank.android.model.business.EdmAppCount;
import com.yeepbank.android.model.business.EdmOverview;
import com.yeepbank.android.model.user.Investor;
import com.yeepbank.android.model.user.TotalAssets;
import com.yeepbank.android.request.business.ProjectDetailRequest;
import com.yeepbank.android.request.user.AssetsRequest;
import com.yeepbank.android.response.business.ProjectDetailResponse;
import com.yeepbank.android.response.user.AssetsResponse;
import com.yeepbank.android.server.AppUpdateServer;
import com.yeepbank.android.server.LoginAndRegisterServer;
import com.yeepbank.android.utils.LoadDialog;
import com.yeepbank.android.utils.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by WW on 2015/9/8.
 */
public class HomeActivity extends BaseActivity implements View.OnClickListener,RadioButton.OnCheckedChangeListener{
    private FragmentManager fragmentManager;
    private HashMap<Integer,Fragment> fragments;
    private RadioGroup radioGroup;
    public static RadioButton investBtn,dailyIncreaseBtn,meBtn,battleCouponBtn;
    public static Fragment currentFragment;
    private InvestFragment investFragment;
    private DailyIncreaseFragment dailyIncreaseFragment;
    private TransformListFragment transformListFragment;
    private MeFragment meFragment;
    ProjectListFragment projectListFragment;
    public static LoginAndRegisterServer loginAndRegisterServer;
    private Handler msgHandler;
    public static Investor investor;//投资者信息
    public static TotalAssets totalAssets;//账户信息
    public static EdmAppCount edmAppCount;//天天盈信息
    public static EdmOverview edmOverview;//天天盈买入信息
    public static boolean hasBuyedEdm;//是否买过天天盈
    public static boolean hasBuyEdmProject;//是否有购买过的天天盈项目
    public static String transTotalCount,totalCount;
    private int checkId = -1;
    private static LoadDialog msgDialog;
    private int lastPressedId = -1;
    private static int count=0;
    private LoadDialog comentAndRatingDialog;


    /*
    * 当前显示红点的Fragment
    * */
     static {
        System.loadLibrary("signkey");
    }
    private HashMap<String,ArrayList<SocketMsg>> socketMsgHashMap = null;

    @Override
    protected void initView() {

//        Cst.SIGN_METHOD_SECRET = SignKeys.getSignKeyFromC().substring(0, SignKeys.getSignKeyFromC().lastIndexOf("|"));
//        Cst.KEY = SignKeys.getSignKeyFromC().substring(SignKeys.getSignKeyFromC().lastIndexOf("|") + 1, SignKeys.getSignKeyFromC().length());
//        Cst.currentUser = Utils.getInstances().getInvestorFromSharedPreference(mContext);


        /*try {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            //参数是应用程序的包名
            intent.setData(Uri.parse("market://details?id=" + getPackageName()));
            //通过隐式意图激活activity
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(mContext, "抱歉，你没有安装应用市场", Toast.LENGTH_LONG);
        }*/
        /*
        * 注册接收websocket的广播
        *
        * */



        registerReceiver(socketMsgReceiver, new IntentFilter(BaseFragment.SOCKET_MSG));
        fragments = new HashMap<Integer,Fragment>();
        radioGroup = (RadioGroup) findViewById(R.id.function_btn);
        investBtn = (RadioButton) findViewById(R.id.touzi);
        dailyIncreaseBtn = (RadioButton) findViewById(R.id.tty);
        meBtn = (RadioButton) findViewById(R.id.me_btn);
        battleCouponBtn= (RadioButton) findViewById(R.id.battle_coupon_btn);

        investBtn.setOnClickListener(this);
        dailyIncreaseBtn.setOnClickListener(this);
        meBtn.setOnClickListener(this);
        battleCouponBtn.setOnClickListener(this);

        investBtn.setOnCheckedChangeListener(this);
        dailyIncreaseBtn.setOnCheckedChangeListener(this);
        meBtn.setOnCheckedChangeListener(this);
        battleCouponBtn.setOnCheckedChangeListener(this);
        initFragment();
        msgHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case Cst.CMD.LOGIN_SUCCESS:
                    case Cst.CMD.REGISTER_SUCCESS:
                        Log.e("++++111","我到这里啦");
                        cancelMsg();
                        Log.e("+++++333", "我到这里啦");

                        investor = loginAndRegisterServer.getInvestor();
                        totalAssets = loginAndRegisterServer.getTotalAssets();
                        edmAppCount = loginAndRegisterServer.getEdmAppCount();
                        edmOverview = loginAndRegisterServer.getEdmOverview();
                        Cst.currentUser = investor;

                        if(currentFragment instanceof MeFragment){
                            ((MeFragment)currentFragment).onShow(mContext);
                        }else if(currentFragment instanceof DailyIncreaseFragment){
                            ((DailyIncreaseFragment)currentFragment).onShow(mContext);
                        }else if(currentFragment instanceof BattleCouponCenterFragment){
                            ((BattleCouponCenterFragment)currentFragment).onShow(mContext);
                        }

                        break;
                    case Cst.CMD.SHOW_FRAGMENT:
                        ((BaseFragment) currentFragment).onShow(mContext);
                        break;
                    case Cst.CMD.NET_ERROR:
                        isNetErrorNow = true;//调用这个方法时说明网络异常
                        showFragment(R.id.net_error);
                        break;

                }
            }
        };
        showFragment(R.id.invest);
        resetRadioBtn(R.id.touzi);
        checkVersion();
        if (Utils.getInstances().getInvestFragmentTabRedDocToSharedPreference(mContext)){
            showRedDot(HomeActivity.investBtn);
        }else {
            hideRedDot(HomeActivity.investBtn);
        }
        if(Utils.getInstances().getBattleCouponFragmentTabRedDocToSharedPreference(mContext)){
            showRedDot(HomeActivity.battleCouponBtn);
        }else{
            hideRedDot(HomeActivity.battleCouponBtn);
        }
        if (Utils.getInstances().getMeFragmentTabRedDocToSharedPreference(mContext)){
            showRedDot(HomeActivity.meBtn);
        }else {
            hideRedDot(HomeActivity.meBtn);
        }
    }

    public void checkVersion(){
        Intent intent = new Intent(UpdateService.ACTION);
        intent.setPackage(getPackageName());
        startService(intent);
    }

    private void resetRadioBtn(int id) {
        if(id!=R.id.touzi && id != R.id.tty && id != R.id.me_btn && id != R.id.battle_coupon_btn){
            return;
        }
        investBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.tab_invest_icon_unchecked), null, null);
        investBtn.setTextColor(Color.parseColor("#a3a3a3"));
        dailyIncreaseBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.tab_daily_increast_icon_unchecked), null, null);
        dailyIncreaseBtn.setTextColor(Color.parseColor("#a3a3a3"));
        meBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.tab_account_icon_unchecked), null, null);
        meBtn.setTextColor(Color.parseColor("#a3a3a3"));
        battleCouponBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.coupon_center_nf), null, null);
        battleCouponBtn.setTextColor(Color.parseColor("#a3a3a3"));
        switch (id){
            case R.id.touzi:
                investBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.tab_invest_icon_checked),
                        null, null);
                investBtn.setTextColor(Color.parseColor("#3887be"));
                break;
            case R.id.tty:
                dailyIncreaseBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.tab_daily_increast_icon_checked), null, null);
                dailyIncreaseBtn.setTextColor(Color.parseColor("#3887be"));
                break;
            case R.id.me_btn:
                meBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.tab_account_icon_checked), null, null);
                meBtn.setTextColor(Color.parseColor("#3887be"));
                break;
            case R.id.battle_coupon_btn:
                battleCouponBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.coupon_center_nf_chosen),null,null);
                battleCouponBtn.setTextColor(Color.parseColor("#3887be"));
                break;
        }
    }


    /*
        * 显示Fragment
        * */
    public void showFragment(int id) {
        if(id == -1){
            return;
        }

        if(id != R.id.net_error){
            checkId = id;
        };
        radioGroup.clearCheck();
        FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
        Fragment fragment = fragments.get(id);
        if(fragment == null){
            switch (id){
                case R.id.invest:
                    if(isNetErrorNow)return;
                    fragment = new InvestFragment();
                    fragments.put(R.id.invest, fragment);
                    break;
                case R.id.daily_increase:
                    if(isNetErrorNow)return;
                    fragment = new DailyIncreaseFragment();
                    fragments.put(R.id.daily_increase, fragment);

                    break;
                case R.id.me:
                    if(isNetErrorNow)return;
                    fragment = new MeFragment();
                    fragments.put(R.id.me, fragment);
                    break;
                case R.id.battle_coupon:
                    if(isNetErrorNow)return;
                    fragment = new BattleCouponCenterFragment();
                    fragments.put(R.id.battle_coupon,fragment);
                    break;
                case R.id.project_list:
                    if(isNetErrorNow)return;
                    fragment = new ProjectListFragment();
                    fragments.put(R.id.project_list, fragment);
                    break;
                case R.id.more_transform_project:
                    if(isNetErrorNow)return;
                    fragment = new TransformListFragment();
                    fragments.put(R.id.more_transform_project, fragment);
                    break;
                case R.id.net_error:
                    fragment = new ErrorFragment();
                    fragments.put(R.id.net_error, fragment);
                    break;

            }
            fragmentTransaction.add(R.id.function_panel, fragment);

        }

        if(fragment != null){
            for(Fragment f:fragments.values()){
                if(f != null){
                    fragmentTransaction.hide(f);
                }
            }
            if(!(fragment instanceof ErrorFragment)){
                currentFragment = fragment;
            }
            /*
            * 当前网络异常则不显示其他fragment
            * */
            if(isNetErrorNow && !(fragment instanceof ErrorFragment)){
                return;
            }
            fragmentTransaction.show(fragment);
            fragmentTransaction.commitAllowingStateLoss();
            final Fragment finalFragment = fragment;
            msgHandler.sendEmptyMessage(Cst.CMD.SHOW_FRAGMENT);
//            new Thread(){
//                @Override
//                public void run() {
////                    while (true){
////                        if(((BaseFragment) finalFragment).isCreated()){
//
////                            break;
////                        }
////                    }
//                }
//            }.start();
        }
    }

    /*
    * 初始化各个功能模块
    * */
//    private void initFragment() {
//        fragmentManager = getFragmentManager();
//        FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
//        investFragment = new InvestFragment();
//        fragmentTransaction.add(R.id.function_panel, investFragment);
//        fragments.put(R.id.invest, investFragment);
//        dailyIncreaseFragment = new DailyIncreaseFragment();
//        fragmentTransaction.add(R.id.function_panel, dailyIncreaseFragment);
//        fragments.put(R.id.daily_increase, dailyIncreaseFragment);
//        meFragment = new MeFragment();
//        fragmentTransaction.add(R.id.function_panel, meFragment);
//        fragments.put(R.id.me, meFragment);
//        projectListFragment = new ProjectListFragment();
//        fragmentTransaction.add(R.id.function_panel, projectListFragment);
//        fragments.put(R.id.project_list, projectListFragment);
//        transformListFragment = new TransformListFragment();
//        fragmentTransaction.add(R.id.function_panel, transformListFragment);
//        fragments.put(R.id.more_transform_project, transformListFragment);
//        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//        fragmentTransaction.commit();
//
//    }

    private void initFragment() {
        fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
        investFragment = new InvestFragment();
        fragmentTransaction.add(R.id.function_panel, investFragment);
        fragments.put(R.id.invest, investFragment);
        fragmentTransaction.commit();
    }
    @Override
    protected void fillData() {
        if(null != Cst.currentUser){
            loadData();
        }
    }
    /*
    * 获取账户数据
    * */
    private void loadData(){
        AssetsRequest request = new AssetsRequest(mContext, new StringListener() {
            @Override
            public void ResponseListener(String result) {
                AssetsResponse response = new AssetsResponse();
                if(response.getStatus(result) == 200){
                    HomeActivity.totalAssets = response.getObject(result);
                    Cst.currentUser = response.getInvestor(result);
                    Utils.getInstances().putInvestorToSharedPreference(mContext, Cst.currentUser);
                }
            }

            @Override
            public void ErrorListener(VolleyError volleyError) {
            }
        },Cst.currentUser.investorId);
        request.stringRequest();
    }


    @Override
    protected int getLayoutResources() {
        return R.layout.home_activity;
    }

    @Override
    protected View getNavigationBar() {
        return null;
    }

    @Override
    protected void initNavigationBar(View navigationBar) {

    }

    @Override
    public void onClick(View v) {
        if (v instanceof RadioButton){
            investBtn.setChecked(false);
            dailyIncreaseBtn.setChecked(false);
            meBtn.setChecked(false);
            battleCouponBtn.setChecked(false);
            ((RadioButton)v).setChecked(true);

        }
    }
    @Override
    public void onBackPressed() {
      if(transformListFragment != null && transformListFragment.getPopupWindow()!= null
              && transformListFragment.getPopupWindow().isShowing()){
           transformListFragment.getPopupWindow().dismiss();
       }
        super.onBackPressed();
    }

    public void rotation(View view) {

        loginAndRegisterServer = new LoginAndRegisterServer(mContext,view,msgHandler);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        Intent intent = getIntent();
        ActivityStacks.getInstances().popToWitch(HomeActivity.class.getName());
        resetMeBtnStyle();
        if (intent != null){
            final String projectId = intent.getStringExtra("projectId");
            if (projectId!= null){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadProjectData(projectId);
                        setIntent(null);
                    }
                }, 300);

            }else if(intent.getStringExtra("target")!=null&&intent.getStringExtra("target").equals("BATTLE_COUPON_NOTIFY")){
                showFragment(R.id.battle_coupon);
                resetRadioBtn(R.id.battle_coupon_btn);
            }else if (intent.getStringExtra("target")!=null && intent.getStringExtra("target").equals("RECHARGE_NOTIFY")){
                resetKey("RECHARGE_NOTIFY");

                setIntent(null);
            }else if (intent.getStringExtra("target")!=null && intent.getStringExtra("target").equals("WITHDRAW_NOTIFY")) {
                resetKey("WITHDRAW_NOTIFY");
                setIntent(null);
            }else if (intent.getStringExtra("target")!=null){
                if (intent.getStringExtra("target").equals("investTickActivity")){
                   resetKey("investTickActivity");
                    setIntent(null);
                }else if (intent.getStringExtra("target").equals("BIDDING_END")){
                    resetKey("BIDDING_END");

                    setIntent(null);
                }else if (intent.getStringExtra("target").equals("BIDDING_END_REPAYMENTED")){
                   resetKey("BIDDING_END_REPAYMENTED");
                    setIntent(null);
                }
            }
            else {
              if(currentFragment != null && !(currentFragment instanceof ProjectListFragment) && !(currentFragment instanceof TransformListFragment)){
                    ((BaseFragment)currentFragment).onShow(mContext);
                }
            }
        }else {
            if(currentFragment != null && !(currentFragment instanceof ProjectListFragment) && !(currentFragment instanceof TransformListFragment)){
                ((BaseFragment)currentFragment).onShow(mContext);
            }
        }
        super.onResume();
    }

    /*
    * 我的账户tab按钮是否应该显示小红点
    * */
    private void resetMeBtnStyle() {
        HashMap<String,ArrayList<SocketMsg>> socketMaps = Utils.getInstances().getMeFragmentRedDocFromSharedPreference(mContext);
        hideRedDot(meBtn);
        Utils.getInstances().putMeFragmentTabRedDocToSharedPreference(mContext,false);
        if (socketMaps != null){
                for (Map.Entry<String,ArrayList<SocketMsg>> entrty : socketMaps.entrySet()){
                    ArrayList<SocketMsg> socketList = entrty.getValue();
                    if (socketList != null && socketList.size() > 0){
                        showRedDot(meBtn);
                        Utils.getInstances().putMeFragmentTabRedDocToSharedPreference(mContext, true);
                        break;
                    }
                }
        }
    }


    public void loadProjectData(String projectId){
            String userId = Cst.currentUser == null? "0":Cst.currentUser.investorId;
            ProjectDetailRequest request = new ProjectDetailRequest(mContext, new StringListener() {
                @Override
                public void ResponseListener(String result) {
                    dismiss();
                    ProjectDetailResponse response = new ProjectDetailResponse();
                    if(response.getStatus(result) == 200){
                        BaseModel data = response.getObject(result);
                        Intent detailIntent = new Intent(HomeActivity.this,ProjectDetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("data", data);
                        detailIntent.putExtra("data",bundle);
                        startActivity(detailIntent);
                    }
                }

                @Override
                public void ErrorListener(VolleyError volleyError) {

                }
            },projectId,userId);
            request.stringRequest();
    };


    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    public LoadDialog getLoadDialog() {
        return loadding;
    }

    public Handler getMsgHandler() {
        return msgHandler;
    }

    /*
    * 当网络正常时，调用该方法获取当前要正常显示的fragmentID
    * */
    public int getKeyFromMap(Fragment fragment){
        isNetErrorNow = false;//调用这个方法时说明网络已正常
        return  checkId;
    }
    @Override
    protected void onDestroy() {
        if(msgDialog != null && msgDialog.isShowing()){
            msgDialog.dismiss();
            msgDialog = null;
        }
        Cst.currentUser = null;
        Cst.SIGN_METHOD_SECRET = null;
        Cst.KEY = null;
        unregisterReceiver(socketMsgReceiver);
        Utils.getInstances().recycle();
        AppUpdateServer.getInstances(mContext).recycle();
        super.onDestroy();
    }

    /*
    * tab显示红点
    * */
    public static void showRedDot(RadioButton radioButton){
        if (radioButton!=null && radioButton.getParent() != null ){
            /*
            * 如果在我的账户页退出登录，有小红点的情况下隐藏小红点
            * */
            if (radioButton.getId() == R.id.me_btn &&  Cst.currentUser == null){
                hideRedDot(radioButton);
                return;
            }
            ((RelativeLayout)radioButton.getParent()).getChildAt(1).setVisibility(View.VISIBLE);
            ((RelativeLayout)radioButton.getParent()).getChildAt(1).postInvalidate();

        }
    }
    /*
    * tab隐藏红点
    * */
    public static void hideRedDot(RadioButton radioButton){
        if (radioButton!=null && radioButton.getParent() != null){
            ((RelativeLayout)radioButton.getParent()).getChildAt(1).setVisibility(View.GONE);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked){
            int checkedId = buttonView.getId();
            if (checkedId != R.id.touzi && checkedId != R.id.tty && checkedId != R.id.me_btn && checkedId !=R.id.battle_coupon_btn){
                return;
            }

//            if ((currentFragment instanceof MeFragment) &&
//                    Utils.getInstances().getMeFragmentTabRedDocToSharedPreference(mContext) &&
//                    meBtn.getId() != checkedId){
//                Utils.getInstances().putMeFragmentTabRedDocToSharedPreference(mContext, false);
//                hideRedDot(meBtn);
//            }
            Log.e("TAG","触发次数"+System.currentTimeMillis());
            if (lastPressedId == checkedId){
                return;
            }
            lastPressedId = checkedId;
            resetRadioBtn(checkedId);
            switch (checkedId) {
                case R.id.touzi:
                    showFragment(R.id.invest);
                    break;
                case R.id.tty:
                    showFragment(R.id.daily_increase);
                    break;
                case R.id.me_btn:
                    showFragment(R.id.me);
                    break;
                case R.id.battle_coupon_btn:
                    showFragment(R.id.battle_coupon);
                    break;
            }
        }
    }


    private void resetKey(String cmd){
                        if (cmd.equals("RECHARGE_NOTIFY")){
                            RunningAccountActivity.initChoose = R.id.recharge;
                            Intent detailIntent = new Intent(HomeActivity.this,RunningAccountActivity.class);
                            startActivity(detailIntent);
                        }else if (cmd.equals("WITHDRAW_NOTIFY")){
                            RunningAccountActivity.initChoose = R.id.withdrawal;
                            Intent detailIntent = new Intent(HomeActivity.this,RunningAccountActivity.class);
                            startActivity(detailIntent);
                        }else if (cmd.equals("investTickActivity")){
                            Intent detailIntent = new Intent(HomeActivity.this,CouponsActivity.class);
                            startActivity(detailIntent);
                        }else if (cmd.equals("BIDDING_END")){
                            InvestmentRecordActivity.initChoose=R.id.repaying;
                            Intent detailIntent = new Intent(HomeActivity.this,InvestmentRecordActivity.class);
                            startActivity(detailIntent);
                        }else if (cmd.equals("BIDDING_END_REPAYMENTED")){
                            InvestmentRecordActivity.initChoose=R.id.repayed;
                            Intent detailIntent = new Intent(HomeActivity.this,InvestmentRecordActivity.class);
                            startActivity(detailIntent);
                        }
    }

    /*
    * websocket 相关
    * 注册广播接收 websocket消息
    * */

     private BroadcastReceiver socketMsgReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BaseFragment.SOCKET_MSG)){
                SocketMsg socketMsg = (SocketMsg) intent.getBundleExtra("socketMsg").get("socketMsg");

                if (socketMsg.activeCode == 1 || socketMsg.activeCode == 3 || socketMsg.activeCode == 4 || socketMsg.activeCode == 5){

                    socketMsgHashMap = Utils.getInstances().getMeFragmentRedDocFromSharedPreference(mContext);

                    if (socketMsgHashMap == null){
                        socketMsgHashMap = new HashMap<String,ArrayList<SocketMsg>>();
                        if (socketMsg.activeCode == 1){
                            ArrayList<SocketMsg> socketMsgArrayList = new ArrayList<SocketMsg>();
                            socketMsgArrayList.add(socketMsg);
                            socketMsgHashMap.put("runningAccount",socketMsgArrayList);
                        }else if (socketMsg.activeCode == 3){
                            ArrayList<SocketMsg> socketMsgArrayList = new ArrayList<SocketMsg>();
                            socketMsgArrayList.add(socketMsg);
                            socketMsgHashMap.put("investTick",socketMsgArrayList);
                        }else if (socketMsg.activeCode == 4 || socketMsg.activeCode == 5){
                            ArrayList<SocketMsg> socketMsgArrayList = new ArrayList<SocketMsg>();
                            socketMsgArrayList.add(socketMsg);
                            socketMsgHashMap.put("bidding",socketMsgArrayList);
                        }

                    }else {
                        if (socketMsg.activeCode == 1){
                            ArrayList<SocketMsg> socketMsgArrayList = socketMsgHashMap.get("runningAccount");
                            if (socketMsgArrayList == null){
                                socketMsgArrayList = new ArrayList<SocketMsg>();
                                socketMsgArrayList.add(socketMsg);
                                socketMsgHashMap.put("runningAccount",socketMsgArrayList);
                            }else {
                                if (socketMsgArrayList.size() == 0){
                                    socketMsgArrayList.add(socketMsg);
                                }else
                                    for (int i = 0; i < socketMsgArrayList.size(); i++) {
                                    if (socketMsgArrayList.get(i).activeCode == socketMsg.activeCode){
                                        socketMsgArrayList.remove(i);
                                        socketMsgArrayList.add(socketMsg);
                                        return;
                                    }
                                    if (i == socketMsgArrayList.size() - 1){
                                        socketMsgArrayList.add(socketMsg);
                                    }
                                }
                            }


                        }else if (socketMsg.activeCode == 3){
                            ArrayList<SocketMsg> socketMsgArrayList = socketMsgHashMap.get("investTick");
                            if (socketMsgArrayList == null){
                                socketMsgArrayList = new ArrayList<SocketMsg>();
                                socketMsgArrayList.add(socketMsg);
                                socketMsgHashMap.put("investTick",socketMsgArrayList);
                            }else {
                                if (socketMsgArrayList.size() == 0){
                                    socketMsgArrayList.add(socketMsg);
                                }else
                                for (int i = 0; i < socketMsgArrayList.size(); i++) {
                                    if (socketMsgArrayList.get(i).activeCode == socketMsg.activeCode){
                                        socketMsgArrayList.remove(i);
                                        socketMsgArrayList.add(socketMsg);
                                        break;
                                    }
                                    if (i == socketMsgArrayList.size() - 1){
                                        socketMsgArrayList.add(socketMsg);
                                    }
                                }
                            }
                        }else if (socketMsg.activeCode == 4 || socketMsg.activeCode == 5){
                            ArrayList<SocketMsg> socketMsgArrayList = socketMsgHashMap.get("bidding");
                            if (socketMsgArrayList == null){
                                socketMsgArrayList = new ArrayList<SocketMsg>();
                                socketMsgArrayList.add(socketMsg);
                                socketMsgHashMap.put("bidding",socketMsgArrayList);
                            }else {
                                if (socketMsgArrayList.size() == 0){
                                    socketMsgArrayList.add(socketMsg);
                                }else
                                    for (int i = 0; i < socketMsgArrayList.size(); i++) {
                                    if (socketMsgArrayList.get(i).activeCode == socketMsg.activeCode){
                                        socketMsgArrayList.remove(i);
                                        socketMsgArrayList.add(socketMsg);
                                        break;
                                    }
                                    if (i == socketMsgArrayList.size() - 1){
                                        socketMsgArrayList.add(socketMsg);
                                    }
                                }
                            }
                        }
                    }
                    Utils.getInstances().putMeFragmentRedDocToSharedPreference(mContext,socketMsgHashMap);
                    Utils.getInstances().putMeFragmentTabRedDocToSharedPreference(mContext, true);
                    showRedDot(meBtn);
                    if ( currentFragment!= null && currentFragment instanceof MeFragment){
                        ((MeFragment)currentFragment).refreshRedDoc();
                    }
                }else if(socketMsg.activeCode == 7){
                    Utils.getInstances().putBattleCouponFragmentTabRedDocToSharedPreference(mContext, true);
                    showRedDot(battleCouponBtn);
                }else {
                    Utils.getInstances().putInvestFragmentTabRedDocToSharedPreference(mContext, true);
                    showRedDot(investBtn);
                }
            }
        }
    };
    /*
    * 显示fragment时 获取对应的socket推送消息
    * */
    public ArrayList<SocketMsg> getSocketMsgById(int id){
        return socketMsgHashMap.get(id);
    }
}
