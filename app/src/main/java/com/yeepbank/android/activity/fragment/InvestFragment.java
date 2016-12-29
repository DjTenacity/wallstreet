package com.yeepbank.android.activity.fragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;

import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;

import android.text.style.ForegroundColorSpan;
import android.util.Log;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;


import com.android.volley.VolleyError;
import com.yeepbank.android.Cst;
import com.yeepbank.android.R;
import com.yeepbank.android.activity.business.*;
import com.yeepbank.android.activity.setting.AdviceFeedbackActivity;
import com.yeepbank.android.activity.setting.RealNameAuthenticationActivity;
import com.yeepbank.android.activity.user.WebActivity;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.base.BaseFragment;
import com.yeepbank.android.base.BaseModel;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.model.Web;
import com.yeepbank.android.model.business.*;
import com.yeepbank.android.request.business.InvestRequest;
import com.yeepbank.android.request.user.HasRealNameRequest;
import com.yeepbank.android.response.business.InvestResponse;
import com.yeepbank.android.response.user.HasRealNameResponse;
import com.yeepbank.android.server.LoginAndRegisterServer;
import com.yeepbank.android.utils.LoadDialog;
import com.yeepbank.android.utils.Utils;
import com.yeepbank.android.widget.AnimationProgressBar;
import com.yeepbank.android.widget.GuidePage;
import com.yeepbank.android.widget.RefreshScrollerView;
import org.w3c.dom.Text;

import javax.xml.parsers.ParserConfigurationException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.Inflater;

/**
 * Created by WW on 2015/9/8.
 */
public class InvestFragment extends BaseFragment implements View.OnClickListener,Cst.OnRefresh {

    private TextView hourText,minText,secText,explanText;
    private long time = 10000;
    private LinearLayout dailyIncreaseLayout,speedBuyLayout,recommendListLayout;//天天盈，秒杀,历史记录;
    private TextView moreTransformLayout;//更多债权转让
    private RefreshScrollerView refreshScrollerView;
    private View dailyIncreaseView;
    private TextView adviseText;//意见与反馈
    private String totalCount,transTotalCount;

    private LinearLayout timeCountLayout,countDownBtnLayout,investRecommendLayout,TransProjectLayout;


    private TextView rateIntegerText,rateDecimalText,residualAmountText;//天天盈卡片利率整数位，小数位,剩余金额
    private AnimationProgressBar progressBar;//天天盈卡片进度条

    private TextView secRateIntegerText,secRateDecimalText,cycleText,projectAmountText;//秒杀项目利率,周期,项目金额

    private CountDownTimer countDownTimer;

    private EdmAppCount edmAppCount;//天天盈初始页项目
    private SecProject secProject;//秒杀项目
    private NorProject ffiProject;//新手项目
    public ArrayList<NorProject> norProjectList = new ArrayList<>();//推荐项目集合
    private ArrayList<NorProject[]> investRecommendList;//推荐项目数组集合，为了一行显示两个
    private ArrayList<TranProject[]> transRecommendList;//转债项目数组集合，为了一行显示两个

    private ArrayList<TranProject> tranProjectList;//转债项目集合

    private boolean isPullToRefresh = false;
    private Button countBtn;
    private TextView norProjectCountText;
    private LoadDialog msgDialog,alertDialog;
    private int WITCH_DO = 0;
    private LoginAndRegisterServer server;
    private Handler handler;
    private GuidePage guidePage;
    private boolean hasCreated = false;

    @Override
    public void initView(View view) {

        hourText = (TextView) view.findViewById(R.id.hour);
        minText = (TextView) view.findViewById(R.id.min);
        secText = (TextView) view.findViewById(R.id.sec);
        dailyIncreaseLayout = (LinearLayout) view.findViewById(R.id.invest_daily_increase);
        dailyIncreaseLayout.setOnClickListener(this);

        speedBuyLayout = (LinearLayout) view.findViewById(R.id.invest_speed_buy_layout);
        countBtn = (Button) speedBuyLayout.findViewById(R.id.countBtn);
        countBtn.setOnClickListener(this);
        speedBuyLayout.setOnClickListener(this);

        refreshScrollerView = (RefreshScrollerView) view.findViewById(R.id.scroll_view);
        refreshScrollerView.setOnRefresh(this);

        recommendListLayout = (LinearLayout) view.findViewById(R.id.recommend_list);
        recommendListLayout.setOnClickListener(this);

        adviseText = (TextView) view.findViewById(R.id.advise);
        //adviseText.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        adviseText.getPaint().setAntiAlias(true);//抗锯齿
        adviseText.setText("新版体验如何?快来告诉我们");
        adviseText.setOnClickListener(this);


        guidePage = (GuidePage) view.findViewById(R.id.banner);



        timeCountLayout = (LinearLayout) view.findViewById(R.id.time_count_layout);
        countDownBtnLayout = (LinearLayout) view.findViewById(R.id.countBtnLayout);

        investRecommendLayout = (LinearLayout) view.findViewById(R.id.invest_recommend_layout);

        TransProjectLayout = (LinearLayout) view.findViewById(R.id.assignment_rights);

        dailyIncreaseView = view.findViewById(R.id.invest_daily_increase_layout);
        rateIntegerText = (TextView) dailyIncreaseView.findViewById(R.id.rate_integer);
        rateDecimalText = (TextView) dailyIncreaseView.findViewById(R.id.rate_decimal);
        progressBar = (AnimationProgressBar) dailyIncreaseView.findViewById(R.id.progress);
        residualAmountText = (TextView) dailyIncreaseView.findViewById(R.id.residual_amount);

        secRateIntegerText = (TextView) view.findViewById(R.id.sec_rate_integer);
        secRateDecimalText = (TextView) view.findViewById(R.id.sec_rate_decimal);
        cycleText = (TextView) view.findViewById(R.id.cycle);
        projectAmountText = (TextView) view.findViewById(R.id.project_amount);

        moreTransformLayout = (TextView) view.findViewById(R.id.more_transform);
        moreTransformLayout.setOnClickListener(this);

        norProjectCountText = (TextView) view.findViewById(R.id.total_count);



        msgDialog = new LoadDialog(getActivity(), R.style.dialog, false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msgDialog.dismiss();
                if(WITCH_DO == 0){
                    showLoginPage();
                }else if(WITCH_DO == 1){
                    ((BaseActivity)getActivity()).gotoTarget(RealNameAuthenticationActivity.class, R.anim.activity_in_from_right, R.anim.activity_out_from_left, "");
                }

            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msgDialog.dismiss();
            }
        },0).setMessage("登陆后才能进行投资，是否立即登录");

        alertDialog = new LoadDialog(getActivity(), R.style.dialog, false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        },0);




        handler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                switch (msg.what){
                    case Cst.CMD.LOGIN_SUCCESS:
                    case Cst.CMD.REGISTER_SUCCESS:
                        ((BaseActivity)getActivity()).cancelMsg();
                        Cst.currentUser = server.getInvestor();
                        break;
                }

            }
        };
        explanText = (TextView) view.findViewById(R.id.explan_text);


    }

    private void showLoginPage(){
        ((BaseActivity)getActivity()).showDialogWindow(R.layout.login_and_register, R.style.exist_style, new BaseActivity.OnShowListener() {
            @Override
            public void show(View view) {
                rotation(view);
            }
        });
    }
    private void rotation(View view) {
        server = new LoginAndRegisterServer(getActivity(),view,handler);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.invest;
    }

    @Override
    public void fillData() {
        investRecommendList = new ArrayList<NorProject[]>();
        transRecommendList = new ArrayList<TranProject[]>();

        //loadData();
    }

    /*
    * 天天盈卡片赋值
    * */

    private void setEdmProjectData(){
        progressBar.setProgress(0);
        if(edmAppCount != null){
            String  maxInterestRate = Utils.getInstances().formatUp(edmAppCount.maxInterestRate * 100);
            //利率
                String[] values = maxInterestRate.trim().split("\\.");
                rateIntegerText.setText(values[0]);
                rateDecimalText.setText("."+values[1]);

            //进度条进度
            if(edmAppCount.totalRequestAmount > 0){
                progressBar.updateProgress((int) ((edmAppCount.totalBiddingAmount) * 100 / edmAppCount.totalRequestAmount));
            }else {
                progressBar.updateProgress(0);
            }
            //剩余金额
            if(edmAppCount.totalSurplusAmount==0){
                String str=Utils.getInstances().formatWithUnit(edmAppCount.totalSurplusAmount, 1);
                SpannableString trans=new SpannableString(str);
                trans.setSpan(new ForegroundColorSpan(Color.parseColor("#fa886d")), 0,trans.length()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                residualAmountText.setText(trans);
            }else{
                residualAmountText.setText(Utils.getInstances().formatWithUnit(edmAppCount.totalSurplusAmount, 1));
            }

            dailyIncreaseLayout.setVisibility(View.VISIBLE);

        }else {
            dailyIncreaseLayout.setVisibility(View.GONE);
        }
    }


    /*
    * 秒杀项目卡片赋值
    * */
    private void setSecProjectData(){
        if(secProject != null){
            String  interestRate = Utils.getInstances().formatUp(secProject.interestRate * 100);
            String[] values = interestRate.trim().split("\\.");
            secRateIntegerText.setText(values[0]);
            secRateDecimalText.setText("."+values[1]);
            //TextView中的字体局部放大
            SpannableString ssbb=new SpannableString(secProject.duration+ BaseModel.parseUnit(secProject.durationUnit));
            ssbb.setSpan(new AbsoluteSizeSpan(55), 0,secProject.duration.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            //TextView中的字体局部放大

            cycleText.setText(ssbb);

            speedBuyLayout.setVisibility(View.VISIBLE);
            time = secProject.longPublistTime - new Date().getTime();
            if (time > 0){
                projectAmountText.setText(Utils.getInstances().formatWithUnit(secProject.requestAmount, 1));
                hourText.setText(convert(time / (60 * 60 * 1000)));
                minText.setText(convert(time % (60 * 60 * 1000) / (60 * 1000)));
                secText.setText(convert(time % (60 * 60 * 1000) % (60 * 1000) / 1000));
                timeCountLayout.setVisibility(View.VISIBLE);
                countDownBtnLayout.setVisibility(View.GONE);
                countDownTimer = new CountDownTimer(time,1000){

                    @Override
                    public void onTick(long millisUntilFinished) {
                        hourText.setText(convert(millisUntilFinished/(60*60*1000)));
                        minText.setText(convert(millisUntilFinished%(60*60*1000)/(60*1000)));
                        secText.setText(convert(millisUntilFinished % (60 * 60 * 1000) % (60 * 1000) / 1000));


                    }

                    @Override
                    public void onFinish() {
                        explanText.setText("剩余金额");
                        projectAmountText.setText(Utils.getInstances().formatWithUnit(secProject.requestAmount - secProject.biddingAmount, 1));
                        Animation out = AnimationUtils.loadAnimation(getActivity(),R.anim.activity_out_from_right);
                        out.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                countDownBtnLayout.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                timeCountLayout.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        timeCountLayout.startAnimation(out);

                        Animation in = AnimationUtils.loadAnimation(getActivity(),R.anim.activity_in_from_left);
                        countDownBtnLayout.startAnimation(in);

                    }
                };
                countDownTimer.start();
            }else {
                explanText.setText("剩余金额");
                projectAmountText.setText(Utils.getInstances().formatWithUnit(secProject.requestAmount - secProject.biddingAmount, 1));
                timeCountLayout.setVisibility(View.GONE);
                countDownBtnLayout.setVisibility(View.VISIBLE);
            }
        }else {
            speedBuyLayout.setVisibility(View.GONE);
        }
    }


     /*
    * 获取推荐项目数据
    * */
    private void getTnvestRecommendData() {
        investRecommendLayout.removeAllViews();
        if(norProjectList != null || ffiProject != null){
            NorProject[] investRecommends = null;
            investRecommendList.clear();
            if(norProjectList == null){
                norProjectList = new ArrayList<NorProject>();
            }
            if(ffiProject != null){
                norProjectList.add(0, ffiProject);
            }

            for (int i = 0; i < norProjectList.size(); i++) {
                if(i % 2 == 0){
                    investRecommends = new NorProject[2];
                    investRecommendList.add(investRecommends);
                }

                investRecommends[i % 2] = norProjectList.get(i);

            }
            createInvestRecommendCrad();
        }
    }

    /*
    * 创建推荐项目卡片
    * */
    private void createInvestRecommendCrad(){

        for (int i = 0; i < investRecommendList.size(); i++) {

            LinearLayout view = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.invest_recommend_layout,null);
            View viewLeft = view.findViewById(R.id.left);
            viewLeft.setOnClickListener(this);
            View viewRight = view.findViewById(R.id.right);
            viewRight.setOnClickListener(this);
            if(i == 0 && ffiProject != null ){
                initNewerCard(viewLeft);
                if(i != investRecommendList.size() - 1 ||
                        (i == investRecommendList.size() - 1 && investRecommendList.get(i).length > 1)){
                    if(investRecommendList.get(i)[1] == null){
                        viewRight.setVisibility(View.INVISIBLE);
                    }else {
                        initCard(viewRight,i,1);
                    }
                }
            }else {

                initCard(viewLeft, i, 0);
                if(i != investRecommendList.size() - 1 ||
                        (i == investRecommendList.size() - 1 && investRecommendList.get(i).length > 1)){
                    if(investRecommendList.get(i)[1] == null){
                        viewRight.setVisibility(View.INVISIBLE);
                    }else {
                        initCard(viewRight,i,1);
                    }
                }
            }
            investRecommendLayout.addView(view);

        }
    }

    /*
   * 获取转债项目数据
   * */
    private void getAssignmentRightsData() {
        TransProjectLayout.removeAllViews();
        TranProject[] transRecommends = null;
        transRecommendList.clear();
        for (int i = 0; i < tranProjectList.size(); i++) {
            if(i % 2 == 0){
                transRecommends = new TranProject[2];
                transRecommendList.add(transRecommends);
            }

            transRecommends[i % 2] = tranProjectList.get(i);

        }
        createTransRecommendCrad();
    }

    /*
    * 创建转债项目卡片
    * */
    private void createTransRecommendCrad(){

        for (int i = 0; i < transRecommendList.size(); i++) {

            LinearLayout view = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.assignment_rights_layout,null);
            View viewLeft = view.findViewById(R.id.left_project);
            View viewRight = view.findViewById(R.id.right_project);
            initTransCard(viewLeft, i, 0);
            if(i != transRecommendList.size() - 1 ||
                 (i == transRecommendList.size() - 1 && transRecommendList.get(i).length > 1)){
                 if(transRecommendList.get(i)[1] == null){
                     viewRight.setVisibility(View.INVISIBLE);
                 }else {
                     initTransCard(viewRight, i, 1);
                 }
             }
            TransProjectLayout.addView(view);

        }

    }
    /*
    *
    * 转债卡片赋值
    * */
    private void initTransCard(View view,int i,int index){
        view.setTag(transRecommendList.get(i)[index]);
        view.setOnClickListener(this);
        ((TextView)view.findViewById(R.id.project_name)).setText(transRecommendList.get(i)[index].projectTitle);

//        transRecommendList.get(i)[index].duration
//                + NorProject.parseUnit(transRecommendList.get(i)[index].durationUnit)
        //TextView中的字体局部放大
       SpannableString s=new SpannableString(transRecommendList.get(i)[index].buyerHoldingDays);
       s.setSpan(new AbsoluteSizeSpan(55), 0, transRecommendList.get(i)[index].buyerHoldingDays.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //TextView中的字体局部放大
        ((TextView)view.findViewById(R.id.limit_day)).setText(s);

        ((TextView)view.findViewById(R.id.percent_integer)).setText(Utils.getInstances().formatUp(transRecommendList.get(i)[index].buyerRoi * 100).split("\\.")[0]);
        ((TextView)view.findViewById(R.id.percent_decimal)).setText("." + Utils.getInstances().formatUp(transRecommendList.get(i)[index].buyerRoi * 100).split("\\.")[1]);

        String tranStr=Utils.getInstances().thousandFormatWithUnit(transRecommendList.get(i)[index].transferPrice);
        SpannableString trans=new SpannableString(tranStr);
        trans.setSpan(new ForegroundColorSpan(Color.parseColor("#666666")), 0,tranStr.length()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ((TextView)view.findViewById(R.id.transfer_amount)).setText(trans);
        //((TextView)view.findViewById(R.id.transfer_amount)).setText(Utils.thousandFormatWithUnit(transRecommendList.get(i)[index].transferPrice));
    }



    /*
    * 推荐项目卡片赋值
    * */
    private void initCard(View view,int i,int index){
        AnimationProgressBar bar = (AnimationProgressBar)view.findViewById(R.id.progress);
        bar.setProgress(0);
        view.setTag(investRecommendList.get(i)[index]);
        ((TextView)view.findViewById(R.id.recommend_title)).setText(investRecommendList.get(i)[index].projectTitle);
        ImageView newImgLeft = (ImageView) view.findViewById(R.id.new_flag);
        if(investRecommendList.get(i)[index].projectType.trim().equals("FFI")){//新手
            newImgLeft.setVisibility(View.VISIBLE);
            newImgLeft.setImageResource(R.drawable.recommendnewicon);
        }else if(investRecommendList.get(i)[index].projectType.trim().equals("SEC")){//新手
            newImgLeft.setVisibility(View.VISIBLE);
            newImgLeft.setImageResource(R.drawable.sec_icon);
        }else {
            newImgLeft.setVisibility(View.INVISIBLE);
        }
        //TextView中的字体局部放大
        SpannableString ssb=new SpannableString(investRecommendList.get(i)[index].duration+ NorProject.parseUnit(investRecommendList.get(i)[index].durationUnit));
        ssb.setSpan(new AbsoluteSizeSpan(55), 0, investRecommendList.get(i)[index].duration.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //TextView中的字体局部放大

        ((TextView) view.findViewById(R.id.time_limit)).setText(ssb);
        ((TextView)view.findViewById(R.id.progress_percent_text)).setText(Utils.getInstances().formatUp(investRecommendList.get(i)[index].interestRate * 100).split("\\.")[0]);
        ((TextView)view.findViewById(R.id.progress_decimal_text)).setText("."+Utils.getInstances().formatUp(investRecommendList.get(i)[index].interestRate * 100).split("\\.")[1]);
        bar.updateProgress((int) (investRecommendList.get(i)[index].biddingAmount * 100
                / investRecommendList.get(i)[index].requestAmount));
        ImageView experienceImgLeft = (ImageView) view.findViewById(R.id.experience);
        if(investRecommendList.get(i)[index].couponEcFlag != null && investRecommendList.get(i)[index].couponEcFlag.equals("Y")){
            experienceImgLeft.setVisibility(View.VISIBLE);
        }else {
            experienceImgLeft.setVisibility(View.GONE);
        }
        ImageView fullCutImgLeft = (ImageView) view.findViewById(R.id.full_cut);
        if(investRecommendList.get(i)[index].couponFcFlag != null && investRecommendList.get(i)[index].couponFcFlag.equals("Y")){
            fullCutImgLeft.setVisibility(View.VISIBLE);
        }else {
            fullCutImgLeft.setVisibility(View.GONE);
        }
        ImageView increaseInterestImgLeft = (ImageView) view.findViewById(R.id.increase_interest);
        if(investRecommendList.get(i)[index].couponIaFlag != null && investRecommendList.get(i)[index].couponIaFlag.equals("Y")){
            increaseInterestImgLeft.setVisibility(View.VISIBLE);
        }else {
            increaseInterestImgLeft.setVisibility(View.GONE);
        }
        String str=Utils.getInstances().formatWithUnit(investRecommendList.get(i)[index].requestAmount
                - investRecommendList.get(i)[index].biddingAmount, 1);
       SpannableString sStr=new SpannableString(str);
       sStr.setSpan(new ForegroundColorSpan(Color.parseColor("#666666")), 0, str.length()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ((TextView)view.findViewById(R.id.residual_amount)).setText(sStr);

    }

    private void initNewerCard(View view){
        view.setTag(ffiProject);
        ((TextView)view.findViewById(R.id.recommend_title)).setText(ffiProject.projectTitle);
        ImageView newImgLeft = (ImageView) view.findViewById(R.id.new_flag);
        if(ffiProject.projectType.trim().equals("FFI")){//新手
            newImgLeft.setVisibility(View.VISIBLE);
        }else {
            newImgLeft.setVisibility(View.INVISIBLE);
        }
        SpannableString sb=new SpannableString(ffiProject.duration + NorProject.parseUnit(ffiProject.durationUnit));
        sb.setSpan(new AbsoluteSizeSpan(55),0,ffiProject.duration.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ((TextView)view.findViewById(R.id.time_limit)).setText(sb);
        ((TextView)view.findViewById(R.id.progress_percent_text)).setText(Utils.getInstances().formatUp(ffiProject.interestRate * 100).split("\\.")[0]);
        ((TextView)view.findViewById(R.id.progress_decimal_text)).setText("."+Utils.getInstances().formatUp(ffiProject.interestRate * 100).split("\\.")[1]);
        ((AnimationProgressBar)view.findViewById(R.id.progress)).updateProgress((int) (ffiProject.biddingAmount * 100
                / ffiProject.requestAmount));
        ImageView experienceImgLeft = (ImageView) view.findViewById(R.id.experience);
        if(ffiProject.couponEcFlag != null && ffiProject.couponEcFlag.equals("Y")){
            experienceImgLeft.setVisibility(View.VISIBLE);
        }else {
            experienceImgLeft.setVisibility(View.GONE);
        }
        ImageView fullCutImgLeft = (ImageView) view.findViewById(R.id.full_cut);
        if(ffiProject.couponFcFlag != null && ffiProject.couponFcFlag.equals("Y")){
            fullCutImgLeft.setVisibility(View.VISIBLE);
        }else {
            fullCutImgLeft.setVisibility(View.GONE);
        }
        ImageView increaseInterestImgLeft = (ImageView) view.findViewById(R.id.increase_interest);
        if(ffiProject.couponIaFlag != null && ffiProject.couponIaFlag.equals("Y")){
            increaseInterestImgLeft.setVisibility(View.VISIBLE);
        }else {
            increaseInterestImgLeft.setVisibility(View.GONE);
        }
        String strNew=Utils.getInstances().formatWithUnit(ffiProject.requestAmount
                - ffiProject.biddingAmount, 1);
        SpannableString sNewStr=new SpannableString(strNew);
        sNewStr.setSpan(new ForegroundColorSpan(Color.parseColor("#666666")), 0,strNew.length()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ((TextView)view.findViewById(R.id.residual_amount)).setText(sNewStr);

    }

    @Override
    public void onShow(Context context) {

        if(getActivity() != null){

            hideRedDoc();
            loadData();
            guidePage.loadData();
        }

    }

    private String convert(long value){
        if(value<10){
            return "0"+value;
        }
        return value+"";
    }

    private void loadData(){
        InvestRequest projectListRequest = new InvestRequest(getActivity(), new StringListener() {
            @Override
            public void ResponseListener(String result) {
                InvestResponse investResponse = new InvestResponse();

                if(investResponse.getStatus(result) == 200) {
                    edmAppCount = investResponse.getObject(result);
                    HomeActivity.edmAppCount = edmAppCount;
                    secProject = investResponse.getSecProject(result);
                    ffiProject = investResponse.getFfiProject(result);
                    if(norProjectList!= null && norProjectList.size() > 0){
                        norProjectList.clear();
                    }

                    norProjectList = investResponse.getNorProjectList(result);

                    if(tranProjectList!= null && tranProjectList.size() > 0){
                        tranProjectList.clear();
                    }
                    tranProjectList = investResponse.getTranProjectList(result);
                    totalCount = investResponse.getNorProjectCount(result);
                    transTotalCount = investResponse.getTransProjectCount(result);
                    HomeActivity.totalCount = totalCount;
                    HomeActivity.transTotalCount = transTotalCount;
                    moreTransformLayout.setText("更多("+transTotalCount+")");
                    norProjectCountText.setText("历史项目(" + totalCount + ")");
                    getTnvestRecommendData();
                    getAssignmentRightsData();
                    setEdmProjectData();
                    setSecProjectData();
                    if(isPullToRefresh){
                        refreshScrollerView.stopRefresh();
                        isPullToRefresh = false;
                    }
                    hideRedDoc();
                }else {

                    if(isPullToRefresh){
                        refreshScrollerView.stopRefresh();
                        isPullToRefresh = false;
                    }
                    if(isAdded()){
                        ((BaseActivity)getActivity()).toast(investResponse.getMessage(result));
                    }
                    hideRedDoc();
                }
            }

            @Override
            public void ErrorListener(VolleyError volleyError) {
                if(isPullToRefresh){
                    refreshScrollerView.stopRefresh();
                    isPullToRefresh = false;
                }
                if(isAdded()){
                    ((BaseActivity)getActivity()).showErrorMsg(getString(R.string.net_error),null);
                }
                hideRedDoc();
            }
        });

        projectListRequest.stringRequest();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.invest_daily_increase:
                HomeActivity.dailyIncreaseBtn.setChecked(true);
                break;
            case R.id.invest_speed_buy_layout:
                /*
                * TODO
                * */


                    ((BaseActivity)getActivity()).gotoTarget(SecKillActivity.class, R.anim.activity_in_from_right,
                        R.anim.activity_out_from_left, getString(R.string.invest), secProject);
                break;
            case R.id.recommend_list:
                ((HomeActivity)getActivity()).showFragment(R.id.project_list);
                break;
            case R.id.more_transform:
                ((HomeActivity)getActivity()).showFragment(R.id.more_transform_project);
                break;
            case R.id.left:
            case R.id.right:
                ((BaseActivity)getActivity()).gotoTarget(ProjectDetailActivity.class, R.anim.activity_in_from_right,
                        R.anim.activity_out_from_left, getString(R.string.invest), v.getTag());
                break;
            case R.id.left_project:
            case R.id.right_project:
                //TransformActivity.initChoose = R.id.repayment_plan;
                ((BaseActivity) getActivity()).gotoTarget(TransformActivity.class, R.anim.activity_in_from_right,
                        R.anim.activity_out_from_left, getString(R.string.invest), v.getTag());
                break;
            case R.id.countBtn:
                if(Cst.currentUser == null){
                    WITCH_DO = 0;
                    msgDialog.setMessage("登陆后才能进行投资，是否立即登录");
                    msgDialog.setSureBtn("立即登录");
                    msgDialog.setCancelBtn("再看看");
                    msgDialog.showAs();
                }else {
                    if (Cst.currentUser.idAuthFlag != null && Cst.currentUser.idAuthFlag.equals("Y")) {
                        if (! Cst.currentUser.answerFlag){
                            HashMap<String,String> dataMap = new HashMap<String,String>();
                            dataMap.put("investorId", Cst.currentUser.investorId);
                            ((HomeActivity)getActivity()).gotoTargetForResult(WebActivity.class, R.anim.activity_in_from_right, R.anim.activity_out_from_left, "",
                                    new Web("问卷调查", Cst.URL.RISK_QUESTION_URL, "", dataMap), 0);
                        }else {
                            ((BaseActivity)getActivity()).gotoTarget(InvestSureActivity.class,
                                    R.anim.activity_in_from_right,
                                    R.anim.activity_out_from_left, "", secProject);
                        }
                    } else {
                        hasRealName();
                    }
                }

                break;
            case R.id.advise:
                ((BaseActivity)getActivity()).gotoTarget(AdviceFeedbackActivity.class,
                        R.anim.activity_in_from_right,
                        R.anim.activity_out_from_left, "");
                break;

        }
    }

    /*
* 检查是否实名认证
* */
    private void hasRealName() {
        HasRealNameRequest request = new HasRealNameRequest(getActivity(), new StringListener() {
            @Override
            public void ResponseListener(String result) {
                HasRealNameResponse response = new HasRealNameResponse();
                if(response.getStatus(result) == 200){
                    Cst.currentUser.idAuthFlag = response.getObject(result);
                    if(Cst.currentUser.idAuthFlag!= null && Cst.currentUser.idAuthFlag.equals("Y")){
                        Utils.getInstances().putInvestorToSharedPreference(getActivity(), Cst.currentUser);
                        ((BaseActivity)getActivity()).gotoTargetRemovePre(InvestSureActivity.class, R.anim.activity_in_from_right, R.anim.activity_out_from_left, "", secProject);
                    }else if(Cst.currentUser.idAuthFlag!= null && Cst.currentUser.idAuthFlag.equals("P")){
                        alertDialog.setMessage("您的实名认证信息正在审核,暂时无法投资/提现/充值,我们会加紧审核");
                        alertDialog.setSureBtn("我知道了");
                        alertDialog.showAs();
                    }else if(Cst.currentUser.idAuthFlag!= null && Cst.currentUser.idAuthFlag.equals("N")){
                        WITCH_DO = 1;
                        msgDialog.setMessage("您的实名认证信息审核未通过,是否重新认证");
                        msgDialog.setSureBtn("重新认证");
                        msgDialog.setCancelBtn("取消");
                        msgDialog.showAs();
                    }else {
                        WITCH_DO = 1;
                        msgDialog.setMessage("完成实名认证后可投资，是否立即实名认证");
                        msgDialog.setSureBtn("实名认证");
                        msgDialog.setCancelBtn("再看看");
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



    @Override
    public void refresh(Object target) {
        isPullToRefresh = true;
        loadData();
        guidePage.loadData();
    }

    @Override
    public void loadMore() {

    }

    private void hideRedDoc(){
        if (hasCreated){
            if (HomeActivity.currentFragment instanceof InvestFragment &&
                    Utils.getInstances().getInvestFragmentTabRedDocToSharedPreference(getActivity())){
                Utils.getInstances().putInvestFragmentTabRedDocToSharedPreference(getActivity(), false);
                ((HomeActivity)getActivity()).hideRedDot(HomeActivity.investBtn);
            }
        }else {
            hasCreated = true;
        }
    }

}
