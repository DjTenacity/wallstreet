package com.yeepbank.android.activity.business;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.android.volley.VolleyError;
import com.yeepbank.android.Cst;
import com.yeepbank.android.R;
import com.yeepbank.android.activity.account.RechargeActivity;
import com.yeepbank.android.activity.setting.CouponsActivity;
import com.yeepbank.android.activity.user.AddNewBankCardActivity;
import com.yeepbank.android.activity.user.WebActivity;
import com.yeepbank.android.base.ActivityStacks;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.base.BaseModel;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.model.Web;
import com.yeepbank.android.model.business.DetailModel;
import com.yeepbank.android.model.business.NorProject;
import com.yeepbank.android.model.user.CouponsVo;
import com.yeepbank.android.request.business.DoBiddingRequest;
import com.yeepbank.android.request.business.ProjectDetailRequest;
import com.yeepbank.android.request.business.TransformRequest;
import com.yeepbank.android.request.user.AssetsRequest;
import com.yeepbank.android.request.user.ChooseCouponsRequest;
import com.yeepbank.android.response.business.DoBiddingResponse;
import com.yeepbank.android.response.business.ProjectDetailResponse;
import com.yeepbank.android.response.user.AssetsResponse;
import com.yeepbank.android.response.user.CouponsResponse;
import com.yeepbank.android.utils.LoadDialog;
import com.yeepbank.android.utils.Utils;
import java.util.ArrayList;

/**
 * Created by WW on 2015/9/30.
 * 确认投资页面
 */
public class InvestSureActivity extends BaseActivity implements View.OnClickListener{
    //private ImageView shareBtn;
    private View navigationBar;
    private LinearLayout sureInvestLayout,successInvestLayout,tickChooseLayout;//确认投资，投资成功，选择投资券控件
    private Button investBtn;ImageButton allInvestBtn,rechargeBtn;//投资按钮，全投按钮,充值按钮
    private Button scanAccountBtn,scanMoreProjectBtn;
    private BaseModel data;
    private TextView annualInterestRate,durationText,residualAmountText,durationUnitText,
            availableBalanceText,expectedReturnText,paidAmountText,expectedReturnSuccessText,profitDurationText,tickDescriptionText;
    //年利化率,期限,剩余金额,可用余额,预期收益,实付金额,投资成功界面预期收益,收益期限
    private EditText investmentAmountEdit;//投入金额
    private LoadDialog msgDialog,alertDialog;
    private double notifyMoney,couponMoney;
    private CouponsVo couponsVo;
    private LinearLayout belongsToNorProjectLayout,belongsToTransProjectLayout;
    private TextView transferProfitText,transformRepaymentDateText;
    private TextView whitchRateText;
    private ArrayList<CouponsVo> couponsVoArrayList;
    private ImageView openArraown;
    private LoadDialog bindCardDialog,rechargeDialog;
    public static Handler mHandler;
    /*
     TODO  非转债项目 > 2000 显示 否则不显示
    * */
    private LinearLayout showOrHideLayout;
    private TextView transferText;

    /*三个协议*/
//    private LinearLayout borrowDetailLayout,grantDetailLayout,riskDetailLayout;
    private LinearLayout detailLayout;

    @Override
    protected void initView() {
        navigationBar = findViewById(R.id.navigation_bar);
        sureInvestLayout = (LinearLayout) findViewById(R.id.sure_invest_layout);
        sureInvestLayout.setVisibility(View.VISIBLE);
        successInvestLayout = (LinearLayout) findViewById(R.id.success_invest_layout);
        investBtn = (Button) findViewById(R.id.sure_invest);
        investBtn.setOnClickListener(this);
        scanAccountBtn = (Button) successInvestLayout.findViewById(R.id.scan_account);
        scanAccountBtn.setOnClickListener(this);
        scanMoreProjectBtn = (Button) successInvestLayout.findViewById(R.id.scan_more_project_list);
        scanMoreProjectBtn.setOnClickListener(this);

        expectedReturnSuccessText = (TextView) successInvestLayout.findViewById(R.id.expected_return_success);
        profitDurationText = (TextView) successInvestLayout.findViewById(R.id.profit_duration);

        allInvestBtn = (ImageButton) findViewById(R.id.all_invest_btn);
        allInvestBtn.setOnClickListener(this);

        openArraown= (ImageView) findViewById(R.id.open);

        rechargeBtn = (ImageButton) findViewById(R.id.recharge_btn);
        rechargeBtn.setOnClickListener(this);
        annualInterestRate = (TextView) findViewById(R.id.annual_interest_rate);
        durationText = (TextView) findViewById(R.id.duration);
        residualAmountText = (TextView) findViewById(R.id.residual_amount);
        durationUnitText = (TextView) findViewById(R.id.duration_unit);
        availableBalanceText = (TextView) findViewById(R.id.available_balance);
        investmentAmountEdit = (EditText) findViewById(R.id.investment_amount);

        showOrHideLayout = (LinearLayout) findViewById(R.id.show_or_hide);
        expectedReturnText = (TextView) findViewById(R.id.expected_return);
        paidAmountText = (TextView) findViewById(R.id.paid_amount);
        msgDialog = new LoadDialog(mContext, R.style.dialog, false, new View.OnClickListener() {
            //确定
            @Override
            public void onClick(View v) {
                if(notifyMoney > 0){
                    investmentAmountEdit.setText(String.valueOf(notifyMoney));
                    Selection.setSelection(investmentAmountEdit.getText(), investmentAmountEdit.getText().length());
                }
                msgDialog.dismiss();
            }
        }, new View.OnClickListener() {
            //取消
            @Override
            public void onClick(View v) {
                msgDialog.dismiss();
            }
        },0).setTitle("提示");

        alertDialog = new LoadDialog(mContext, R.style.dialog, false, new View.OnClickListener() {
            //确定
            @Override
            public void onClick(View v) {
                if(!isTransformProject()){
                    /*
                    * 非转债项目调整投资额为可使用券或和投资的金额
                    * */
                    if(couponMoney > 0){
                        investmentAmountEdit.setText(Utils.getInstances().format(couponMoney));
                        Selection.setSelection(investmentAmountEdit.getText(), investmentAmountEdit.getText().length());
                        //计算收益
                        computationalBenefits();
                    }
                }
                alertDialog.dismiss();
            }
        },0).setTitle("提示").setCancelBtn("我知道了");
        bindCardDialog=new LoadDialog(mContext, R.style.dialog, false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindCardDialog.dismiss();
                gotoTarget(AddNewBankCardActivity.class, R.anim.activity_in_from_right,
                        R.anim.activity_out_from_left, "");
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindCardDialog.dismiss();

            }
        },0);
        bindCardDialog.setTitle("余额不足");
        bindCardDialog.setMessage("请先绑定银行卡以完成充值");
        bindCardDialog.setCancelBtn("取消");
        bindCardDialog.setSureBtn("绑定银行卡");

        rechargeDialog=new LoadDialog(mContext, R.style.dialog, false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rechargeDialog.dismiss();
                gotoTarget(RechargeActivity.class, R.anim.activity_in_from_right,
                        R.anim.activity_out_from_left, "");
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rechargeDialog.dismiss();
            }
        },0);
        rechargeDialog.setMessage("余额不足，请您先充值吧");
        rechargeDialog.setSureBtn("充值");
        rechargeDialog.setCancelBtn("取消");

        if(getIntent().getBundleExtra("data") != null){
            data = (BaseModel) getIntent().getBundleExtra("data").getSerializable("data");
        }

        tickChooseLayout = (LinearLayout) findViewById(R.id.tick_choose_layout);
        tickChooseLayout.setOnClickListener(this);
        tickDescriptionText = (TextView) findViewById(R.id.tick_description);
        tickDescriptionText.setText("");

        belongsToNorProjectLayout = (LinearLayout) findViewById(R.id.belongs_to_norproject);
        belongsToTransProjectLayout = (LinearLayout) findViewById(R.id.belongs_to_trans_project);
        transferProfitText = (TextView) belongsToTransProjectLayout.findViewById(R.id.transfer_profit);
        transformRepaymentDateText = (TextView) belongsToTransProjectLayout.findViewById(R.id.transform_repayment_date);

        /*显示是年华利率还是转让利率的文字说明*/
        whitchRateText = (TextView) findViewById(R.id.whitch_rate);

        transferText = (TextView) findViewById(R.id.transfer_text);


        initDetail();

        if(isTransformProject()){
            transferText.setText("项目转让金额");
        }else {
            transferText.setText("项目剩余金额");
        }
        mHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {

                loadtotalData();

            }
        };
    }

    private void initDetail() {

        if (data!= null && data.protocols != null && data.protocols.size() > 0){
            detailLayout = (LinearLayout) findViewById(R.id.detail_layout);
            detailLayout.findViewById(R.id.text_description).setVisibility(View.VISIBLE);
            for (int i = 0; i < data.protocols.size(); i++) {
                LinearLayout view = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.detail_layout_item,null);
                ((TextView)view.getChildAt(1)).setText(data.protocols.get(i).protocolName);
                ((TextView)view.getChildAt(1)).getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                view.setId(R.id.detail_item);
                view.setOnClickListener(this);
                view.setTag(data.protocols.get(i));
                detailLayout.addView(view);
            }

        }

//        borrowDetailLayout = (LinearLayout) findViewById(R.id.borrow_detail);
//        ((TextView)borrowDetailLayout.getChildAt(1)).getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
//        borrowDetailLayout.setOnClickListener(this);
//
//        grantDetailLayout = (LinearLayout) findViewById(R.id.grant_detail);
//        ((TextView)grantDetailLayout.getChildAt(1)).getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
//        grantDetailLayout.setOnClickListener(this);
//
//        riskDetailLayout = (LinearLayout) findViewById(R.id.risk_detail);
//        ((TextView)riskDetailLayout.getChildAt(1)).getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
//        riskDetailLayout.setOnClickListener(this);

    }

    /*
    * 输入金额框计算收益计算收益
    * */
    private void computationalBenefits() {
        String money = investmentAmountEdit.getText().toString().trim().equals("")?"0":investmentAmountEdit.getText().toString().trim();
        double expectedReturn = 0;//预期收益
        double couponRate = 0;//如果有加息券，加息券利率
        double couponMoney = 0;//如果有体验券，体验券金额
        double value = Double.parseDouble(money);

        if(couponsVo != null && couponsVo.couponType.equals("FC")){
            if(value - couponsVo.subtractAmount > 0){
                paidAmountText.setText(Utils.getInstances().format(value - couponsVo.subtractAmount));
            }else {
                paidAmountText.setText("0");
            }
        }else {
            paidAmountText.setText(Utils.getInstances().format(value));
        }
        if(couponsVo != null && couponsVo.couponType.equals("IA")){
            couponRate = couponsVo.addingInterest;
        }
        if(couponsVo != null && couponsVo.couponType.equals("EC")){
            couponMoney = couponsVo.experienceAmount;
            if(value - couponMoney > 0){
                paidAmountText.setText(Utils.getInstances().format(Utils.getInstances().sub(value, couponMoney)));
            }else {
                paidAmountText.setText("0");
            }
            couponMoney = 0;
        }


        if(data.durationUnit.equals("M")){
            //expectedReturn = (float)((value+couponMoney) * Integer.parseInt(data.duration)*(data.interestRate+couponRate)/12);

            expectedReturn = Utils.getInstances().div(
                    Utils.getInstances().mul(
                            Utils.getInstances().add(value, couponMoney),
                            Integer.parseInt(data.duration),
                            Utils.getInstances().add(data.interestRate, couponRate)
                    ),
                    12);
        }else if(data.durationUnit.equals("D")){
            //expectedReturn = (float)((value+couponMoney) * Integer.parseInt(data.duration)*(data.interestRate+couponRate)/365);
            expectedReturn = Utils.getInstances().div(
                    Utils.getInstances().mul(
                            Utils.getInstances().add(value, couponMoney),
                            Integer.parseInt(data.duration),
                            Utils.getInstances().add(data.interestRate, couponRate)
                    ),
                    365);
        }else if(data.durationUnit.equals("Y")){
            //expectedReturn = (float)((value+couponMoney) * Integer.parseInt(data.duration)*(data.interestRate+couponRate));
            expectedReturn = Utils.getInstances().div(
                    Utils.getInstances().mul(
                            Utils.getInstances().add(value, couponMoney),
                            Integer.parseInt(data.duration),
                            Utils.getInstances().add(data.interestRate, couponRate)
                    ),
                    1);
        }
        expectedReturnText.setText(Utils.getInstances().format(expectedReturn));
        expectedReturnSuccessText.setText(Utils.getInstances().format(expectedReturn));
    }

    @Override
    protected void fillData() {

        if(data != null){



            if(isTransformProject()){
                //如果是转债项目设置金额为转让金额
                residualAmountText.setText(Utils.getInstances().thousandFormat(data.transferPrice));
                durationText.setText(data.buyerHoldingDays);
                durationUnitText.setText("天");
                annualInterestRate.setText(Utils.getInstances().formatUp(data.buyerRoi * 100));
            }else {
                //如果是其他项目，设置金额为项目余额
                residualAmountText.setText(Utils.getInstances().thousandFormat(data.requestAmount - data.biddingAmount));
                durationText.setText(data.duration);
                durationUnitText.setText(NorProject.parseUnit(data.durationUnit));
                annualInterestRate.setText(Utils.getInstances().formatUp(data.interestRate * 100));
            }

            availableBalanceText.setText(Utils.getInstances().thousandFormat(HomeActivity.totalAssets == null ? 0 : HomeActivity.totalAssets.freeBalance));
            investmentAmountEdit.setHint(Utils.getInstances().format(data.biddingStartAmount)+"起投");

            if(HomeActivity.totalAssets != null && HomeActivity.totalAssets.freeBalance < data.biddingStartAmount){

                allInvestBtn.setBackgroundResource(R.drawable.all_invest_btn_not_activated_icon);
                allInvestBtn.setEnabled(false);

            }else {
                allInvestBtn.setBackgroundResource(R.drawable.all_invest_btn_icon);
                allInvestBtn.setEnabled(true);
            }

            if(isTransformProject()){
                belongsToTransProjectLayout.setVisibility(View.VISIBLE);
                belongsToNorProjectLayout.setVisibility(View.GONE);
                //whitchRateText.setText("转让利率");
                //annualInterestRate.setText(Utils.format(data.buyerRoi * 100));
                transferProfitText.setText(data.buyerInvestmentIncome);
                transformRepaymentDateText.setText(data.debtEndDate);
            }else {
                //whitchRateText.setText("年化利率");
                belongsToNorProjectLayout.setVisibility(View.VISIBLE);
                belongsToTransProjectLayout.setGravity(View.GONE);
            }
            loadCanUsedData();


        }
        investmentAmountEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (start == 0 && s.toString().trim().length() == 0) {

                        paidAmountText.setText("0.00");
                        expectedReturnText.setText("0.00");
                        expectedReturnSuccessText.setText("0.00");
                        computationalBenefits();
                        return;
                    }
                    computationalBenefits();


                } catch (Exception e) {
                    if (s.length() > 0) {
                        investmentAmountEdit.setText(s.toString().substring(0, s.toString().length() - 1));
                        Selection.setSelection(investmentAmountEdit.getText(), investmentAmountEdit.getText().length());
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        profitDurationText.setText("元预计收益于" + data.duration + BaseModel.parseUnit(data.durationUnit) + "后到账");

        if(data.projectType.equals("SEC")){
            tickChooseLayout.setVisibility(View.GONE);
        }


    }
    /*
            * 获取账户数据
            * */
    private void loadtotalData(){
        AssetsRequest request = new AssetsRequest(mContext, new StringListener() {
            @Override
            public void ResponseListener(String result) {
                AssetsResponse response = new AssetsResponse();
                if(response.getStatus(result) == 200){
                    HomeActivity.totalAssets = response.getObject(result);
                    availableBalanceText.setText(Utils.getInstances().thousandFormat(HomeActivity.totalAssets == null ? 0 : HomeActivity.totalAssets.freeBalance));
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
    /*
    * 查找可使用的投资券个数
    * */
    private void loadCanUsedData() {
        //String filter = getFilter();
        //if(!filter.equals("")){
            ChooseCouponsRequest request = new ChooseCouponsRequest(mContext, new StringListener() {
                @Override
                public void ResponseListener(String result) {;
                    CouponsResponse response = new CouponsResponse();
                    if(response.getStatus(result) == 200){
                        couponsVoArrayList = response.getObject(result);

                        if(couponsVoArrayList != null && couponsVoArrayList.size() > 0){
                            tickDescriptionText.setText(couponsVoArrayList.size()+"张投资券可用");
                            tickDescriptionText.setTextColor(Color.parseColor("#666666"));
                            openArraown.setVisibility(View.VISIBLE);
                        }else {
                            tickDescriptionText.setText("0张投资券可用");
                            tickDescriptionText.setTextColor(Color.parseColor("#999999"));
                        }
                    }
                }

                @Override
                public void ErrorListener(VolleyError volleyError) {
                }
            },Cst.currentUser.investorId,data.duration);
            request.stringRequest();
        //}
    }

    @Override
    protected int getLayoutResources() {
        return R.layout.invest_sure;
    }

    @Override
    protected View getNavigationBar() {
        return navigationBar;
    }

    private String getFilter(){
        StringBuilder sb = new StringBuilder();
        if(data.couponFcFlag != null && data.couponFcFlag.equals("Y")){
            sb.append("FC,");
        }
        if(data.couponEcFlag != null && data.couponEcFlag.equals("Y")){
            sb.append("EC,");
        }
        if(data.couponIaFlag != null && data.couponIaFlag.equals("Y")){
            sb.append("IA,");
        }
        String filter = sb.toString();
        if(filter.equals("")){
            tickDescriptionText.setText("该项目暂不支持投资券");
            tickDescriptionText.setTextColor(Color.parseColor("#999999"));
            return "";
        }else {
            filter = filter.substring(0,filter.length()-1);
            return filter;
        }
    }

    protected void backBtnTap(){
        if(sureInvestLayout.getVisibility() == View.VISIBLE){
            if(data != null){
                Cst.couponId = null;
                if(data.transferId!= null && !data.transferId.trim().equals("")){
                    gotoTargetRemovePre(TransformActivity.class, R.anim.activity_in_from_left, R.anim.activity_out_from_right, "",data);
                }else if(data.projectType.equals("SEC")){
                    gotoTargetRemovePre(SecKillActivity.class, R.anim.activity_in_from_left, R.anim.activity_out_from_right, "",data);
                }else {
                    gotoTargetRemovePre(ProjectDetailActivity.class, R.anim.activity_in_from_left, R.anim.activity_out_from_right, "",data);
                }
            }

        }else if(successInvestLayout.getVisibility() == View.VISIBLE){
            if(isTransformProject()){
                finish();
            }else {
                Animation outAnimation = AnimationUtils.loadAnimation(mContext,R.anim.activity_out_from_right);
                successInvestLayout.startAnimation(outAnimation);
                successInvestLayout.setVisibility(View.GONE);


                Animation inAnimation = AnimationUtils.loadAnimation(mContext,R.anim.activity_in_from_left);
                sureInvestLayout.startAnimation(inAnimation);
                sureInvestLayout.setVisibility(View.VISIBLE);
                setTitle("确认投资");

                couponsVo = null;
                loadCanUsedData();
                loadData();
                investmentAmountEdit.setText("");
                expectedReturnText.setText("0.00");
                paidAmountText.setText("0.00");
            }
        }
    }


    /*
    * 刷新普通项目数据
    * */

    public void loadData(){
        if(data != null){
            loadding.showAs();
            String userId = Cst.currentUser == null? "0":Cst.currentUser.investorId;
            ProjectDetailRequest request = new ProjectDetailRequest(mContext, new StringListener() {
                @Override
                public void ResponseListener(String result) {
                    loadding.dismiss();
                    ProjectDetailResponse response = new ProjectDetailResponse();
                    if(response.getStatus(result) == 200){
                        data = response.getObject(result);
                        if(data != null){
                            fillData();
                        }
                    }else {
                        toast(response.getMessage(result));
                    }
                }

                @Override
                public void ErrorListener(VolleyError volleyError) {
                    loadding.dismiss();
                    showErrorMsg(getString(R.string.net_error),navigationBar);
                }
            },data.projectId,userId);
            request.stringRequest();
        }

    }


     @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sure_invest:
                /*
                * 投资按钮
                * */
                notifyMoney = 0;
                String inputMoney = investmentAmountEdit.getText().toString().trim().equals("")?"0":investmentAmountEdit.getText().toString().trim();
                if(isTransformProject()){
                    if(HomeActivity.totalAssets!= null && HomeActivity.totalAssets.freeBalance < data.transferPrice){
                        //alertDialog.setMessage("余额不足");
                        //alertDialog.showAs();
                        if(Cst.currentUser.hasBindCard){
                            rechargeDialog.showAs();
                        }else{
                            bindCardDialog.showAs();
                        }

                        return;
                    }
                   /* if(HomeActivity.totalAssets!= null && HomeActivity.totalAssets.freeBalance < data.transferPrice){
                        //alertDialog.setMessage("余额不足");
                        //alertDialog.showAs();
                        rechargeDialog.showAs();
                        return;
                    }*/
                   /* if(HomeActivity.totalAssets!= null && HomeActivity.totalAssets.freeBalance < data.transferPrice){
                        alertDialog.setMessage("余额不足");
                        alertDialog.showAs();
                        return;
                    }*/
                    if(data.transferPrice <= 0){
                        alertDialog.setMessage("项目余额不足");
                        alertDialog.showAs();
                        return;
                    }
                    expectedReturnSuccessText.setText(data.buyerInvestmentIncome);
                    profitDurationText.setText("元预计收益于" + data.buyerHoldingDays +"天后到账");

                    //inputMoney =Math.min(HomeActivity.totalAssets.freeBalance, data.transferPrice)+"";
                    transform();
                }else {
                    if(couponsVo != null && couponsVo.couponType.equals("FC")){
                        fcLogic(couponsVo);
                        if(HomeActivity.totalAssets != null && Double.parseDouble(inputMoney) > HomeActivity.totalAssets.freeBalance){
                            alertDialog.setMessage("余额不足,不能使用该券");
                            alertDialog.showAs();
                            return;
                        }else if(data.requestAmount - data.biddingAmount < couponsVo.fullAmount){
                            alertDialog.setMessage("项目余额不足,不能使用该券");
                            alertDialog.showAs();
                            return;
                        }else if(Double.parseDouble(inputMoney) < couponsVo.fullAmount){
                            alertDialog.setMessage("投资金额不能低于使用该券的最小值");
                            alertDialog.showAs();
                            return;
                        }
                    }else if(couponsVo != null && couponsVo.couponType.equals("EC")){

                        if(data.requestAmount - data.biddingAmount < couponsVo.experienceAmount){
                            alertDialog.setMessage("项目余额不足,不能使用该券");
                            alertDialog.showAs();
                            return;
                        }else
                        if(Double.parseDouble(inputMoney) < couponsVo.experienceAmount){
                            alertDialog.setMessage("投资金额不能低于使用该券的最小值");
                            //alertDialog.setMessage("可用余额不足，所选投资券最低使用金额，无法使用此券，请先充值");
                            alertDialog.showAs();
                            return;
                        }
                    }else if(couponsVo != null && couponsVo.couponType.equals("IA")){
                        iaLogic(couponsVo);
                        if(HomeActivity.totalAssets!= null && Double.parseDouble(inputMoney) > HomeActivity.totalAssets.freeBalance){
                            alertDialog.setMessage("余额不足,不能使用该券");
                            alertDialog.showAs();
                            return;
                        }else if(Double.parseDouble(inputMoney) < couponsVo.minAmount){
                            alertDialog.setMessage("投资金额不能低于使用该券的最小值");
                            alertDialog.showAs();
                            return;
                        }else if(Double.parseDouble(inputMoney) > couponsVo.maxAmount){
                            alertDialog.setMessage("投资金额不能高于使用该券的最大值");
                            alertDialog.showAs();
                            return;
                        }else if(data.requestAmount - data.biddingAmount < couponsVo.minAmount){
                            alertDialog.setMessage("项目余额不足,不能使用该券");
                            alertDialog.showAs();
                            return;
                        }
                    }
                    /*
                    * 如果 investAmount 为空说明不是转债项目，提示
                    * */
                    if(!isTransformProject() && "".equals(inputMoney)){
                        alertDialog.setMessage("请输入投资金额");
                        alertDialog.showAs();
                        return;
                    }

                    if(!isTransformProject()&Double.parseDouble(inputMoney) < data.biddingStartAmount){

                        alertDialog.setMessage("金额不能小于"+Utils.getInstances().format(data.biddingStartAmount)+"元起投金额");
                        alertDialog.showAs();
                        return;
                    }
                    if(!isTransformProject()&&Double.parseDouble(inputMoney) > data.biddingEndAmount){
                        alertDialog.setMessage("已超出最大投资额");
                        alertDialog.showAs();
                        return;
                    }

                    if(!isTransformProject()&&Double.parseDouble(inputMoney) > (data.requestAmount - data.biddingAmount)){
                        alertDialog.setMessage("项目可供投标余额不足");
                        alertDialog.showAs();
                        return;
                    }

                    if (!isTransformProject()&&Double.parseDouble(inputMoney) % 100 != 0){
//                        notifyMoney = (((int)(Double.parseDouble(inputMoney) - data.biddingStartAmount)) / (int)data.biddingStepAmount)
//                                * data.biddingStepAmount+data.biddingStartAmount;
                        notifyMoney = (((int)(Double.parseDouble(inputMoney) - data.biddingStartAmount)) / (int)data.biddingStepAmount)
                                * data.biddingStepAmount+data.biddingStartAmount;
                        msgDialog.setMessage("投资金额必须是100的整数倍，是否修改为:"+notifyMoney);
                        msgDialog.showAs();
                        return;
                    }
                    if(couponsVo != null && couponsVo.couponType.equals("EC")){

                    }

                    if(!isTransformProject() && HomeActivity.totalAssets!= null && HomeActivity.totalAssets.freeBalance < Double.parseDouble(inputMoney)){
                        //alertDialog.setMessage("余额不足");
                        //alertDialog.showAs();
                        if(Cst.currentUser.hasBindCard){
                            rechargeDialog.showAs();
                        }else{
                            bindCardDialog.showAs();

                        }
                        return;
                    }
                    /*if(!isTransformProject() && HomeActivity.totalAssets!= null && HomeActivity.totalAssets.freeBalance < Double.parseDouble(inputMoney)){
                        //alertDialog.setMessage("余额不足");
                        //alertDialog.showAs();
                        return;
                    }*/
                    String id = couponsVo == null?"0":couponsVo.couponId;
                    doBidding(inputMoney,id);
                }
                break;
            case R.id.scan_account:
                 /*
                * 投资成功页面，查看我的账户
                * */
                ActivityStacks.getInstances().pop(ActivityStacks.getInstances().getTop());
                finish();
                ((HomeActivity)ActivityStacks.getInstances().getTop()).meBtn.setChecked(true);
                break;

            case R.id.scan_more_project_list:
                /*
                * 投资成功页面，查看更多详情
                * */
                ActivityStacks.getInstances().pop(ActivityStacks.getInstances().getTop());
                finish();
                ((HomeActivity)ActivityStacks.getInstances().getTop()).showFragment(R.id.invest);

                break;
            case R.id.all_invest_btn:
                /*
                * 全投
                * */
                String money = "0.00";
                if(HomeActivity.totalAssets != null)
                money = Math.min(data.requestAmount - data.biddingAmount,HomeActivity.totalAssets.freeBalance)+"";
                money = (((int)(Double.parseDouble(money) - data.biddingStartAmount)) / (int)data.biddingStepAmount)
                        * data.biddingStepAmount+data.biddingStartAmount+"";
                investmentAmountEdit.setText(money);
                Selection.setSelection(investmentAmountEdit.getText(), investmentAmountEdit.getText().length());
                break;
            case R.id.recharge_btn:
                gotoTarget(RechargeActivity.class, R.anim.activity_in_from_right, R.anim.activity_out_from_left, "确认投资");
                break;
            case R.id.tick_choose_layout:
                if("0张投资券可用".equals(tickDescriptionText.getText().toString())){
                    return;
                }
                  //String filter = getFilter();
                  //if(!filter.equals("")){
                    gotoTargetForResult(CouponsActivity.class, R.anim.activity_in_from_right, R.anim.activity_out_without_move, "确认投资", data.duration,0);
                  //}
                break;
//            case R.id.borrow_detail:
//                gotoTarget(WebActivity.class,"",new Web("借款协议",""));
//                break;
//            case R.id.grant_detail:
//                gotoTarget(WebActivity.class,"",new Web("授权委托书",""));
//                break;
            case R.id.detail_item:
                DetailModel detail = (DetailModel) v.getTag();
                gotoTarget(WebActivity.class,"",new Web(detail.protocolName,detail.protocolUrl));
                break;
        }
    }

    /*
    * 转债接口
    * */
    private void transform() {
        loadding.showAs();
        TransformRequest request = new TransformRequest(mContext, new StringListener() {
            @Override
            public void ResponseListener(String result) {
                DoBiddingResponse response = new DoBiddingResponse();
                loadding.dismiss();
                if(response.getStatus(result) == 200){
                    if(data.projectType.trim().equals("FFI") && Cst.currentUser.noviciate != null
                            && Cst.currentUser.noviciate.equals("Y")){//如果当前用户是新手投资了一次新手项目之后，就不是新手了
                        Cst.currentUser.noviciate = "N";
                        Utils.getInstances().putInvestorToSharedPreference(mContext, Cst.currentUser);
                    }
                    Animation inAnimation = AnimationUtils.loadAnimation(mContext,R.anim.activity_in_from_right);
                    successInvestLayout.startAnimation(inAnimation);
                    successInvestLayout.setVisibility(View.VISIBLE);
                    sureInvestLayout.setVisibility(View.GONE);
                    Animation outAnimation = AnimationUtils.loadAnimation(mContext,R.anim.activity_out_from_left);
                    outAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            sureInvestLayout.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    sureInvestLayout.startAnimation(outAnimation);
                    HomeActivity.totalAssets = response.getTotalAssets(result);
                }else {
                    toast(response.getMessage(result));
                    HomeActivity.totalAssets = response.getTotalAssets(result);
                }
            }

            @Override
            public void ErrorListener(VolleyError volleyError) {
                loadding.dismiss();
                showErrorMsg(getString(R.string.net_error),navigationBar);
            }
        },Cst.currentUser.investorId,data.transferId);
        request.stringRequest();
    }

    /*
    * 投资接口
    * */
    private void doBidding(String money,String couponsVoId) {
        loadding.showAs();
        DoBiddingRequest doBiddingRequest = new DoBiddingRequest(mContext, new StringListener() {
            @Override
            public void ResponseListener(String result) {
                DoBiddingResponse response = new DoBiddingResponse();
                loadding.dismiss();
                if(response.getStatus(result) == 200){
                    if(data.projectType.trim().equals("FFI") && Cst.currentUser.noviciate != null && Cst.currentUser.noviciate.equals("Y")){//如果当前用户是新手投资了一次新手项目之后，就不是新手了
                        Cst.currentUser.noviciate = "N";
                        Utils.getInstances().putInvestorToSharedPreference(mContext, Cst.currentUser);
                    }
                    Animation inAnimation = AnimationUtils.loadAnimation(mContext,R.anim.activity_in_from_right);
                    successInvestLayout.startAnimation(inAnimation);
                    successInvestLayout.setVisibility(View.VISIBLE);
                    sureInvestLayout.setVisibility(View.GONE);
                    Animation outAnimation = AnimationUtils.loadAnimation(mContext,R.anim.activity_out_from_left);
                    outAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            sureInvestLayout.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    sureInvestLayout.startAnimation(outAnimation);
                    HomeActivity.totalAssets = response.getTotalAssets(result);
                }else {
                    HomeActivity.totalAssets = response.getTotalAssets(result);
                    toast(response.getMessage(result));
                }
            }

            @Override
            public void ErrorListener(VolleyError volleyError) {
                loadding.dismiss();
                showErrorMsg(getString(R.string.net_error),navigationBar);
            }
        },data.projectId,money,couponsVoId,Cst.currentUser.investorId);
        doBiddingRequest.stringRequest();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 0:
                resultData = data;
                if(resultData !=null){
                    Bundle bundle = data.getBundleExtra("data");
                    if(bundle != null){
                        couponsVo = (CouponsVo) bundle.getSerializable("data");
                        if(couponsVo != null){
                            if(couponsVo.couponType.equals("FC")){
                                fcLogic(couponsVo);
                                tickDescriptionText.setText("满" + Utils.getInstances().format(couponsVo.fullAmount) + "减" + Utils.getInstances().format(couponsVo.subtractAmount)+" 投资券");
                            }else if(couponsVo.couponType.equals("IA")){
                                tickDescriptionText.setText("年利率+"+Utils.getInstances().format(couponsVo.addingInterest * 100)+"% 投资券");
                                iaLogic(couponsVo);
                            }else if(couponsVo.couponType.equals("EC")){
                                tickDescriptionText.setText(Utils.getInstances().format(couponsVo.experienceAmount) + "元体验金 投资券");
                                ecLogic(couponsVo);

                            }
                            computationalBenefits();
                        }else {
                            if(couponsVoArrayList != null && couponsVoArrayList.size() > 0){
                                tickDescriptionText.setText(couponsVoArrayList.size()+"张投资券可用");
                            }else {
                                tickDescriptionText.setText("0张投资券可用");
                            }
                            String money = investmentAmountEdit.getText().toString().equals("")?"0":investmentAmountEdit.getText().toString();
                            double investMoney = Double.parseDouble(money);
                            double expectedReturn = 0;
                            if(this.data.durationUnit.equals("M")){
                                expectedReturn = Utils.getInstances().div(Utils.getInstances().mul(investMoney, Integer.parseInt(this.data.duration), this.data.interestRate), 12);
                            }else if(this.data.durationUnit.equals("D")){
                                expectedReturn = Utils.getInstances().div(Utils.getInstances().mul(investMoney, Integer.parseInt(this.data.duration), this.data.interestRate), 365);
                            }else if(this.data.durationUnit.equals("Y")){
                                expectedReturn = Utils.getInstances().mul(investMoney, Integer.parseInt(this.data.duration), this.data.interestRate);
                            }
                            expectedReturnText.setText(Utils.getInstances().format(expectedReturn));
                            expectedReturnSuccessText.setText(Utils.getInstances().format(expectedReturn));
                            paidAmountText.setText(money);
                        }
                    }
                }

                break;
        }
        //super.onActivityResult(requestCode, resultCode, data);
    }

    private void ecLogic(CouponsVo couponsVo) {
        couponMoney = 0;
        String money = investmentAmountEdit.getText().toString().trim().equals("")?"0":investmentAmountEdit.getText().toString().trim();
        if(Double.parseDouble(money) < couponsVo.experienceAmount){
            couponMoney = couponsVo.experienceAmount;
            alertDialog.setCancelBtn("我知道了");
            alertDialog.setMessage("所选投资券最低使用额度高于已输入投资金额，已自动补齐");
            alertDialog.show();
        }
    }


    /*
        * 满减券逻辑处理
        * */
    private void fcLogic(CouponsVo couponsVo) {
        couponMoney = 0;
        String money = investmentAmountEdit.getText().toString().trim().equals("")?"0":investmentAmountEdit.getText().toString().trim();
        if(Double.parseDouble(money) < couponsVo.fullAmount){
            couponMoney = couponsVo.fullAmount;
            alertDialog.setCancelBtn("我知道了");
            alertDialog.setMessage("所选投资券最低使用额度高于已输入投资金额，已自动补齐");
            alertDialog.show();
            return;
        }


    }
    /*
    * 加息券逻辑处理
    * */
    private void iaLogic(CouponsVo couponsVo) {
        couponMoney = 0;
        String money = investmentAmountEdit.getText().toString().trim().equals("")?"0":investmentAmountEdit.getText().toString().trim();
        if(Double.parseDouble(money) < couponsVo.minAmount || Double.parseDouble(money) > couponsVo.maxAmount){
            if(Double.parseDouble(money) < couponsVo.minAmount){
                couponMoney = couponsVo.minAmount;
                alertDialog.setMessage("所选投资券最低使用额度高于已输入投资金额，已自动补齐");
            }else {
                couponMoney = couponsVo.maxAmount;
                alertDialog.setMessage("所选投资券最高使用额度低于已输入投资金额，已自动补齐");
            }
            alertDialog.setCancelBtn("我知道了");

            alertDialog.show();
            return;
        }


    }

    private boolean isTransformProject(){
        if(data == null)return false;
        return data.transferId!=null && !data.transferId.trim().equals("");
    }

    @Override
    protected void onDestroy() {
        if(loadding != null && loadding.isShowing()){
            loadding.dismiss();
        }
        if(msgDialog!=null&&msgDialog.isShowing()){
            msgDialog.dismiss();
        }
        if(alertDialog!=null&&alertDialog.isShowing()){
            alertDialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public LoadDialog getLoadDialog() {
        return loadding;
    }

}
