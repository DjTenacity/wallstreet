package com.yeepbank.android.activity.fragment;

import android.content.*;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.android.volley.VolleyError;
import com.yeepbank.android.Cst;
import com.yeepbank.android.R;
import com.yeepbank.android.activity.business.HomeActivity;
import com.yeepbank.android.activity.account.RechargeActivity;
import com.yeepbank.android.activity.account.WithDrawalActivity;
import com.yeepbank.android.activity.setting.*;
import com.yeepbank.android.activity.user.AssetsRatioActivity;
import com.yeepbank.android.activity.user.BankListActivity;
import com.yeepbank.android.activity.user.RunningAccountActivity;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.base.BaseFragment;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.model.SocketMsg;
import com.yeepbank.android.request.user.AssetsRequest;
import com.yeepbank.android.request.user.HasRealNameRequest;
import com.yeepbank.android.response.user.AssetsResponse;
import com.yeepbank.android.response.user.HasRealNameResponse;
import com.yeepbank.android.utils.LoadDialog;
import com.yeepbank.android.utils.Utils;
import com.yeepbank.android.widget.LayoutInSetting;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by WW on 2015/9/8.
 *
 */
public class MeFragment extends BaseFragment implements View.OnClickListener {
  private LinearLayout settingLayout,bankCardListLayout;//设置

  private LayoutInSetting recommendCodeLayout,investTickLayout,investmentRecord,runningAccountLayout;//推荐码,投资券

  // private LayoutInSetting recommendCodeLayout,investTickLayout,investmentRecordLayout;//推荐码,投资券,投资记录

    private ImageButton withdrawalsBtn,rechargeBtn,loginBtn,registerBtn;//提现，充值,登录,注册
    private LinearLayout availableBalanceLayout,totalAssetsLayout;//可用余额,总资产
    private View splitLine;
    private TextView availableBalanceValueText,balanceText;//可用余额，总资产
    private String countCoupon,rmdCode;
    private LoadDialog alertDialog,msgDialog,telDialog;
    private int WITCH_DO = 0;//0表示未登录，1表示未实名认证,2表示设置交易密码
    private TextView companyTelephone;
    private HashMap<String,ArrayList<SocketMsg>> socketMsgHashMap = null;
    private LoadDialog comentAndRatingDialog;
    public static Handler mHandler;
    @Override
    public void initView(View view) {
        mHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(Utils.getInstances().getFistInfoFromPreference(getActivity(),"is_first")&&Utils.getInstances().getSceondInfoFromPreference(getActivity(),"is_two")){
                    comentAndRatingDialog.showAs();
                    Utils.getInstances().putFirstInfoToPreference(getActivity(), "is_first", false);
                }
            }
        };
        comentAndRatingDialog = new LoadDialog(getActivity(), R.style.dialog, false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comentAndRatingDialog.dismiss();
                try {

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    //参数是应用程序的包名
                    intent.setData(Uri.parse("market://details?id=" + getActivity().getPackageName()));
                    //通过隐式意图激活activity
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getActivity(), "抱歉，你没有安装应用市场", Toast.LENGTH_LONG);
                }
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comentAndRatingDialog.dismiss();
                ((BaseActivity)getActivity()).gotoTarget(AdviceFeedbackActivity.class, R.anim.activity_in_from_right, R.anim.activity_out_from_left, "");
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comentAndRatingDialog.dismiss();
            }
        });

        settingLayout = (LinearLayout) view.findViewById(R.id.setting);
        settingLayout.setOnClickListener(this);
        withdrawalsBtn = (ImageButton) view.findViewById(R.id.withdrawals_btn);
        withdrawalsBtn.setOnClickListener(this);
        rechargeBtn = (ImageButton) view.findViewById(R.id.recharge_btn);
        rechargeBtn.setOnClickListener(this);
        availableBalanceLayout = (LinearLayout) view.findViewById(R.id.available_balance);
        totalAssetsLayout = (LinearLayout) view.findViewById(R.id.total_assets);
        totalAssetsLayout.setOnClickListener(this);
        splitLine = view.findViewById(R.id.accont_delete);
        loginBtn = (ImageButton) view.findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(this);
        registerBtn = (ImageButton) view.findViewById(R.id.register_btn);
        registerBtn.setOnClickListener(this);
        availableBalanceValueText = (TextView) view.findViewById(R.id.available_balance_value);
        balanceText = (TextView) view.findViewById(R.id.balance);
        bankCardListLayout = (LinearLayout) view.findViewById(R.id.bank_card_list);
        bankCardListLayout.setOnClickListener(this);
        recommendCodeLayout = (LayoutInSetting) view.findViewById(R.id.recommend_code);
        recommendCodeLayout.setOnClickListener(this);
        investTickLayout = (LayoutInSetting) view.findViewById(R.id.invest_tick);
        investTickLayout.setOnClickListener(this);
       // investmentRecordLayout = (LayoutInSetting) view.findViewById(R.id.investment_record);
       // investmentRecordLayout.setOnClickListener(this);
        companyTelephone= (TextView) view.findViewById(R.id.company_telephone);
        companyTelephone.setOnClickListener(this);
            alertDialog = new LoadDialog(getActivity(), R.style.dialog, false, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            },0).setTitle("提示").setMessage("请先登录").setSureBtn("我知道了");


            msgDialog = new LoadDialog(getActivity(), R.style.dialog, false, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    msgDialog.dismiss();
                    if(WITCH_DO == 1){
                        ((BaseActivity)getActivity()).gotoTarget(RealNameAuthenticationActivity.class, R.anim.activity_in_from_right, R.anim.activity_out_from_left, "");
                    }else if(WITCH_DO == 2){
                        ((BaseActivity)getActivity()).gotoTarget(UpdateTradePasswordActivity.class, R.anim.activity_in_from_right, R.anim.activity_out_from_left, "");
                    }

                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    msgDialog.dismiss();
                }
            },0);
        investmentRecord=(LayoutInSetting)view.findViewById(R.id.investment_record);
        investmentRecord.setOnClickListener(this);



        telDialog = new LoadDialog(getActivity(), R.style.dialog, false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(),"dfdfdfdfd", Toast.LENGTH_LONG).show();
                telDialog.dismiss();
                Intent phoneIntent=new Intent("android.intent.action.CALL", Uri.parse("tel:"+Cst.CMD.SERVICE_TELNUMBER_ONE));
                getActivity().startActivity(phoneIntent);
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                telDialog.dismiss();
            }
        },0);
        runningAccountLayout = (LayoutInSetting)view.findViewById(R.id.running_account);
        runningAccountLayout.setOnClickListener(this);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.me_setting;
    }

    @Override
    public void fillData() {

    }
    /*
    * 隐藏用户信息
    * */
    private void hideUserMsg(){
        availableBalanceLayout.setVisibility(View.GONE);
        totalAssetsLayout.setVisibility(View.GONE);
        splitLine.setVisibility(View.GONE);
        withdrawalsBtn.setVisibility(View.GONE);
        rechargeBtn.setVisibility(View.GONE);
        loginBtn.setVisibility(View.VISIBLE);
        registerBtn.setVisibility(View.VISIBLE);
        /*
        * 隐藏小红点
        * */
        investTickLayout.setRedDocVisible(View.GONE);
        investmentRecord.setRedDocVisible(View.GONE);
        runningAccountLayout.setRedDocVisible(View.GONE);
         setData();
    }
    /*
    * 显示用户信息
    * */
    public void showUserMsg(){
        availableBalanceLayout.setVisibility(View.VISIBLE);
        totalAssetsLayout.setVisibility(View.VISIBLE);
        splitLine.setVisibility(View.VISIBLE);
        withdrawalsBtn.setVisibility(View.VISIBLE);
        rechargeBtn.setVisibility(View.VISIBLE);
        loginBtn.setVisibility(View.GONE);
        registerBtn.setVisibility(View.GONE);
        setData();
    }
    /*
    * 用户信息赋值
    * */

    private void setData(){

        if(HomeActivity.totalAssets != null){
            availableBalanceValueText.setText(Utils.getInstances().thousandFormat(HomeActivity.totalAssets.freeBalance));
            balanceText.setText(Utils.getInstances().thousandFormat(HomeActivity.totalAssets.totalAssets));
            investTickLayout.setValue(countCoupon + "张可用");
            recommendCodeLayout.setValue(rmdCode);
           if(HomeActivity.totalAssets.waitingBiddingAmount>0){
            investmentRecord.setValue("待结标:  " + Utils.getInstances().thousandFormat(HomeActivity.totalAssets.waitingBiddingAmount));
            }else if(HomeActivity.totalAssets.waitingBiddingAmount==0&&HomeActivity.totalAssets.totalPIReceivable>0){
               investmentRecord.setValue("待收本息:  " + Utils.getInstances().thousandFormat(HomeActivity.totalAssets.totalPIReceivable));
           }else{
               investmentRecord.setValue("");
           }

        }else {
            availableBalanceValueText.setText(Utils.getInstances().format(0));
            balanceText.setText(Utils.getInstances().format(0));
            investTickLayout.setValue("");
            recommendCodeLayout.setValue("");
            investmentRecord.setValue("");
        }
    }
    @Override
    public void onShow(Context context) {
        /*
         * 获取socket信息 显示小红点
         * */

        if(Cst.currentUser == null){
            hideUserMsg();
        }else {
            loadData();

            refreshRedDoc();
        }
    }

    public void refreshRedDoc(){
        socketMsgHashMap = Utils.getInstances().getMeFragmentRedDocFromSharedPreference(getActivity());
        if (socketMsgHashMap != null){
            for (Map.Entry<String, ArrayList<SocketMsg>> socketMsgs : socketMsgHashMap.entrySet()){
                String key = socketMsgs.getKey();
                ArrayList<SocketMsg> socketMsgArrayList = socketMsgs.getValue();
                    /*
                    * 资金流水小红点
                    * */
                if (key.equals("runningAccount")){
                    if (socketMsgArrayList !=null && socketMsgArrayList.size() > 0){
                        runningAccountLayout.setRedDocVisible(View.VISIBLE);
                        HomeActivity.showRedDot(HomeActivity.meBtn);}
                    else runningAccountLayout.setRedDocVisible(View.GONE);
                }

                     /*
                    * 投资券小红点
                    * */
                else if (key.equals("investTick")){
                    if(socketMsgArrayList != null && socketMsgArrayList.size() > 0){
                        investTickLayout.setRedDocVisible(View.VISIBLE);
                        HomeActivity.showRedDot(HomeActivity.meBtn);
                    }else investTickLayout.setRedDocVisible(View.GONE);
                }

                     /*
                    * 投资记录小红点
                    * */
                else if (key.equals("bidding")){
                    if (socketMsgArrayList != null && socketMsgArrayList.size() > 0){
                        investmentRecord.setRedDocVisible(View.VISIBLE);
                        HomeActivity.showRedDot(HomeActivity.meBtn);
                    }else investmentRecord.setRedDocVisible(View.GONE);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.setting:
                if(Cst.currentUser == null){
                    alertDialog.showAs();
                    return;
                }
                ((BaseActivity)getActivity()).gotoTarget(SettingListActivity.class,R.anim.activity_in_from_right,
                        R.anim.activity_out_from_left,"");
                break;
            case R.id.withdrawals_btn://提现
                if(Cst.currentUser.idAuthFlag!= null && Cst.currentUser.idAuthFlag.equals("Y")){
                    if(!Cst.currentUser.hastxnPwd){
                        WITCH_DO = 2;
                        msgDialog.setMessage("请先设置交易密码");
                        msgDialog.setCancelBtn("再看看");
                        msgDialog.setSureBtn("立即设置");
                        msgDialog.showAs();
                    }else {
                        ((BaseActivity)getActivity()).gotoTarget(WithDrawalActivity.class,R.anim.activity_in_from_right,
                                R.anim.activity_out_from_left,"");
                    }
                }else {
                    hasRealName(1);
                }

                break;
            case R.id.recharge_btn://充值
                if(Cst.currentUser.idAuthFlag!= null && Cst.currentUser.idAuthFlag.equals("Y")){
                    /*if(!Cst.currentUser.hastxnPwd){
                        WITCH_DO = 2;
                        msgDialog.setMessage("请先设置交易密码");
                        msgDialog.setCancelBtn("再看看");
                        msgDialog.setSureBtn("立即设置");
                        msgDialog.showAs();
                    }else {*/
                        ((BaseActivity)getActivity()).gotoTarget(RechargeActivity.class, R.anim.activity_in_from_right,
                                R.anim.activity_out_from_left, "");
                    //}
                }else {
                    hasRealName(0);
                }
//                if(Cst.currentUser.idAuthFlag!= null && Cst.currentUser.idAuthFlag.equals("Y")){
//                    ((BaseActivity)getActivity()).gotoTarget(RechargeActivity.class, R.anim.activity_in_from_right,
//                            R.anim.activity_out_from_left, getString(R.string.me));
//                }else {
//                    hasRealName(0);
//                }
                break;
            case R.id.login_btn:
                loginBtn.setEnabled(false);
                showLoginPage(0);
                break;
            case R.id.register_btn:
                v.setEnabled(false);
                showLoginPage(1);
                break;
            case R.id.total_assets://饼图
                if(Cst.currentUser == null){
                    alertDialog.showAs();
                    return;
                }
                ((BaseActivity)getActivity()).gotoTarget(AssetsRatioActivity.class, R.anim.activity_in_from_right,
                        R.anim.activity_out_from_left, "");
                break;
            case R.id.bank_card_list://银行卡列表
                if(Cst.currentUser == null){
                    alertDialog.showAs();
                    return;
                }
                ((BaseActivity)getActivity()).gotoTarget(BankListActivity.class,R.anim.activity_in_from_right,
                        R.anim.activity_out_from_left,"");
                break;
            //推荐码
            case R.id.recommend_code:
                if(Cst.currentUser == null){
                    alertDialog.showAs();
                    return;
                }
                ((BaseActivity)getActivity()).gotoTarget(RecommendCodeActivity.class,R.anim.activity_in_from_right,
                        R.anim.activity_out_from_left,"",rmdCode);
                break;
            //投资券
            case R.id.invest_tick:
                if(Cst.currentUser == null){
                    alertDialog.showAs();
                    return;
                }


                ((BaseActivity)getActivity()).gotoTarget(CouponsActivity.class, R.anim.activity_in_from_right,
                        R.anim.activity_out_from_left, getString(R.string.me));

                break;
            //投资记录
            case R.id.investment_record:
                if(Cst.currentUser == null){
                    alertDialog.showAs();
                    return;
                }

                if(HomeActivity.totalAssets == null){
                    InvestmentRecordActivity.initChoose=R.id.waitEnd;
                }else {
                    if(HomeActivity.totalAssets.waitingBiddingAmount>0){
                /*
                *   有待结镖数据 默认跳转到待结镖
                */
                        InvestmentRecordActivity.initChoose=R.id.waitEnd;
                    }else {
                        InvestmentRecordActivity.initChoose=R.id.repaying;
                    }
                }
                ((BaseActivity)getActivity()).gotoTarget(InvestmentRecordActivity.class,R.anim.activity_in_from_right,
                        R.anim.activity_out_from_left,"",1);
                Utils.getInstances().putSceondInfoToPreference(getActivity(),"is_two", true);
                break;
            case R.id.company_telephone:
                telDialog.setMessage("确定要拨打客服电话:"+Cst.CMD.SERVICE_TELNUMBER_TWO);
                telDialog.setCancelBtn("取消");
                telDialog.setSureBtn("确定");
                telDialog.showAs();

                break;
            case R.id.running_account:/*资金流水*/
                if(Cst.currentUser == null){
                    alertDialog.showAs();
                    return;
                }
                ((BaseActivity)getActivity()).gotoTarget(RunningAccountActivity.class,R.anim.activity_in_from_right,
                        R.anim.activity_out_from_left,"");
                break;

        }

    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("HAS_SHOW".equals(intent.getAction())){
                loginBtn.setEnabled(true);
                registerBtn.setEnabled(true);
            }
        }
    };


    private void showLoginPage(final int flag){
        ((BaseActivity)getActivity()).showDialogWindow(R.layout.login_and_register, R.style.exist_style, new BaseActivity.OnShowListener() {
            @Override
            public void show(View view) {
                ((HomeActivity) getActivity()).rotation(view);
                if (flag == 1) {
                    HomeActivity.loginAndRegisterServer.showRegister();
                }
            }
        });
    }

    private void loadData(){
        Log.e("-----",new Date().getTime()+"");
        AssetsRequest request = new AssetsRequest(getActivity(), new StringListener() {

            @Override
            public void ResponseListener(String result) {
                AssetsResponse response = new AssetsResponse();
                if(response.getStatus(result) == 200){
                    HomeActivity.totalAssets = response.getObject(result);
                    Cst.currentUser = response.getInvestor(result);
                    Utils.getInstances().putInvestorToSharedPreference(getActivity(), Cst.currentUser);
                    countCoupon = response.getCountCoupon(result);
                    rmdCode = response.getRmdCode(result);
                    showUserMsg();
                }else {
                    if(isAdded()){
                        ((BaseActivity)getActivity()).toast(response.getMessage(result));
                    }
                }
            }

            @Override
            public void ErrorListener(VolleyError volleyError) {
                if(isAdded()){
                    ((BaseActivity)getActivity()).showErrorMsg(getString(R.string.net_error),null);
                }
            }
        },Cst.currentUser.investorId);
        request.stringRequest();
    }


    /*
   * 检查是否实名认证
   * flag = 1,表示提现，flag=0表示充值
   * */
    private void hasRealName(final int flag) {
        HasRealNameRequest request = new HasRealNameRequest(getActivity(), new StringListener() {
            @Override
            public void ResponseListener(String result) {
                HasRealNameResponse response = new HasRealNameResponse();
                if(response.getStatus(result) == 200){
                    Cst.currentUser.idAuthFlag = response.getObject(result);
                    if(Cst.currentUser.idAuthFlag!= null && Cst.currentUser.idAuthFlag.equals("Y")){
                        Utils.getInstances().putInvestorToSharedPreference(getActivity(), Cst.currentUser);
                        if(flag == 1){
                            ((BaseActivity)getActivity()).gotoTarget(WithDrawalActivity.class, R.anim.activity_in_from_right,
                                    R.anim.activity_out_from_left, getString(R.string.me));
                        }else {
                            ((BaseActivity)getActivity()).gotoTarget(RechargeActivity.class, R.anim.activity_in_from_right,
                                    R.anim.activity_out_from_left, getString(R.string.me));
                        }
                    }else if(Cst.currentUser.idAuthFlag!= null && Cst.currentUser.idAuthFlag.equals("P")){
                        alertDialog.setMessage("您的实名认证信息正在审核,暂时无法投资/提现/充值,我们会加紧审核");
                        alertDialog.showAs();
                    }else if(Cst.currentUser.idAuthFlag!= null && Cst.currentUser.idAuthFlag.equals("N")){
                        WITCH_DO = 1;
                        msgDialog.setMessage("您的实名认证信息审核未通过,是否重新认证");
                        msgDialog.setSureBtn("重新认证");
                        msgDialog.setCancelBtn("取消");
                        msgDialog.showAs();
                    }else {
                        WITCH_DO = 1;
                        if(flag == 1){
                            msgDialog.setMessage("为了您的资金安全，提现需要先完成实名认证");
                        }else {
                            msgDialog.setMessage("为了您的资金安全，充值需要先完成实名认证");
                        }
                        //msgDialog.setSureBtn(Html.fromHtml("<b>"+"去认证"+"</b>"));
                        msgDialog.setSureBtn("去认证");
                        msgDialog.setCancelBtn("取消");
                        msgDialog.showAs();
                    }
                }else {
                    ((BaseActivity)getActivity()).toast(response.getMessage(result));
                }
            }

            @Override
            public void ErrorListener(VolleyError volleyError) {
                ((BaseActivity)getActivity()).showErrorMsg(getString(R.string.net_error), null);
            }
        },Cst.currentUser.investorId);
        request.stringRequest();
    }




    public LayoutInSetting getLayoutInSettingByCode(int code){
        switch (code){
            case 1:
                return runningAccountLayout;
            case 3:
                return investTickLayout;
            case 4:
            case 5:
                return investmentRecord;
            default:
                return null;

        }
    }



    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(receiver, new IntentFilter("HAS_SHOW"));

    }
    public void setHasLoginOutTime(boolean hasLoginOutTime){
        if (hasLoginOutTime){
            hideUserMsg();
        }
    }

    @Override
    public void onStart() {

        super.onStart();

    }
}
