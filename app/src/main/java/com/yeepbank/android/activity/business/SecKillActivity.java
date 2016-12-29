package com.yeepbank.android.activity.business;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.yeepbank.android.Cst;
import com.yeepbank.android.R;
import com.yeepbank.android.activity.setting.RealNameAuthenticationActivity;
import com.yeepbank.android.activity.user.WebActivity;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.base.BaseModel;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.model.Web;
import com.yeepbank.android.model.business.NorProject;
import com.yeepbank.android.model.business.SecProject;
import com.yeepbank.android.request.business.ProjectDetailRequest;
import com.yeepbank.android.request.user.HasRealNameRequest;
import com.yeepbank.android.response.business.ProjectDetailResponse;
import com.yeepbank.android.response.user.HasRealNameResponse;
import com.yeepbank.android.server.LoginAndRegisterServer;
import com.yeepbank.android.server.ProjectDetailServer;
import com.yeepbank.android.utils.LoadDialog;
import com.yeepbank.android.utils.Utils;
import com.yeepbank.android.widget.ChangeItemWithAnimationLayout;
import com.yeepbank.android.widget.ShowMoreLayout;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by WW on 2015/11/10.
 */
public class SecKillActivity extends BaseActivity implements View.OnClickListener,Cst.OnSlideCompleteListener{
    private View navigationBar;
    private ImageView animatorImage;
    private Button secKillBtn;
    private ImageView triangleImg;
    private Handler handler;
    private Runnable runnable = null;
    private ProjectDetailServer pServer;
    private ShowMoreLayout showMoreLayout;
    private ChangeItemWithAnimationLayout changeItemWithAnimationLayout;
    private BaseModel secProject;
    private LinearLayout moreLayout;
    private TextView durationText,durationUnit,percentIntegerText,
            percentDecimalText,explainText,projectNameText,biddingAmountText,requestAmountText,
            repaymentMethodText,investmentCertificateText;//项目期限,项目期限单位,进度整数,进度小数,项目说明,项目名称,剩余金额,总金额，还款方式,投资券

    private LoginAndRegisterServer server;
    private boolean canSecKill = false;
    private int page = 0;
    private int WITCH_DO = 0;
    private LoadDialog msgDialog,alertDialog;
    private TextView projectRiskLevelSeckill;//秒杀项目的风险等级
    @Override
    protected void initView() {
        navigationBar = findViewById(R.id.navigation_bar);
        animatorImage = (ImageView) findViewById(R.id.curve_line);
        secKillBtn = (Button) findViewById(R.id.sec_kill_duration);
        secKillBtn.setEnabled(false);
        secKillBtn.setOnClickListener(this);
        triangleImg = (ImageView) findViewById(R.id.triangle);
        moreLayout = (LinearLayout) findViewById(R.id.more);
        moreLayout.setOnClickListener(this);
        showMoreLayout = (ShowMoreLayout) findViewById(R.id.show_more);
        changeItemWithAnimationLayout = (ChangeItemWithAnimationLayout) findViewById(R.id.navigation_item);
        changeItemWithAnimationLayout.setOnSlideCompleteListener(this);
        durationText = (TextView) findViewById(R.id.duration);
        durationUnit = (TextView) findViewById(R.id.duration_unit);
        percentIntegerText = (TextView) findViewById(R.id.percent_integer);
        percentDecimalText = (TextView) findViewById(R.id.percent_decimal);
        explainText = (TextView) findViewById(R.id.explain);
        projectNameText = (TextView) findViewById(R.id.project_name);
        biddingAmountText = (TextView) findViewById(R.id.bidding_amount);
        requestAmountText = (TextView) findViewById(R.id.request_amount);
        repaymentMethodText = (TextView) findViewById(R.id.repayment_method);

        projectRiskLevelSeckill= (TextView) findViewById(R.id.project_risk_level_seckill);

        investmentCertificateText = (TextView) findViewById(R.id.investment_certificate);
        msgDialog = new LoadDialog(mContext, R.style.dialog, false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msgDialog.dismiss();
                if(WITCH_DO == 0){
                    showLoginPage();
                }else if(WITCH_DO == 1){
                    gotoTarget(RealNameAuthenticationActivity.class, R.anim.activity_in_from_right, R.anim.activity_out_from_left, "");
                }

            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msgDialog.dismiss();
            }
        },0).setMessage("登陆后才能进行投资，是否立即登录");

        alertDialog = new LoadDialog(mContext, R.style.dialog, false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        },0);
        


        if(getIntent().getBundleExtra("data") != null){
            secProject = (BaseModel) getIntent().getBundleExtra("data").getSerializable("data");
            changeItemWithAnimationLayout.setStatus(secProject.status,"YB");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (changeItemWithAnimationLayout.getItemList() != null && changeItemWithAnimationLayout.getItemList().size() > 0){
                        ArrayList<Integer> layoutIds = new ArrayList<Integer>();
                        for (int i = 0; i < changeItemWithAnimationLayout.getItemList().size(); i++) {
                            layoutIds.add(changeItemWithAnimationLayout.getItemList().get(i).textLayout.getId());
                            pServer = new ProjectDetailServer(mContext,showMoreLayout,changeItemWithAnimationLayout,secProject,layoutIds);
                        }
                    }
                }
            });

            loadData();
        }
    }

    @Override
    protected void fillData() {

        final ClipDrawable drawable = (ClipDrawable)animatorImage.getDrawable();
        handler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                switch (msg.what){
                    case 0x1233:
                        drawable.setLevel(drawable.getLevel() +100);
                        break;
                    case Cst.CMD.LOGIN_SUCCESS:
                    case Cst.CMD.REGISTER_SUCCESS:
                        cancelMsg();
                        Cst.currentUser = server.getInvestor();
                        break;
                }

            }
        };

        runnable = new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 0x1233;
                //发送消息,通知应用修改ClipDrawable对象的level值
                handler.sendMessage(msg);
                //取消定时器
                if (drawable.getLevel() < 10000) {
                    handler.sendEmptyMessage(0x1233);
                    handler.post(runnable);
                }
            }
        };
        handler.postDelayed(runnable, 1000);
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

        setData();


    }

    /*获取到控件尺寸后把箭头移向第一个选项中间位置*/
    private void moveTriangleImg(){
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) triangleImg.getLayoutParams();
        if (params != null) {
            params.leftMargin = changeItemWithAnimationLayout.getCheckLayoutWidth() / 2;
            triangleImg.setLayoutParams(params);
        }
    }

    private String convert(long value){
        if(value<10){
            return "0"+value;
        }
        return value+"";
    }

    @Override
    protected int getLayoutResources() {
        return R.layout.seckill;
    }

    @Override
    protected View getNavigationBar() {
        ((TextView)navigationBar.findViewById(R.id.label_text)).setText("项目详情");
        return navigationBar;
    }

    protected void initNavigationBar(View navigationBar){
        //navigationBar.setBackgroundColor(Color.parseColor("#00000000"));
//        ImageView shareLogo = (ImageView) navigationBar.findViewById(R.id.share);
//        shareLogo.setVisibility(View.VISIBLE);
        LinearLayout backLayout = (LinearLayout) navigationBar.findViewById(R.id.back_layout);
        backLayout.setOnClickListener(this);
    }

    @Override
    public LoadDialog getLoadDialog() {
        return loadding;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_layout:
                onBackPressed();
                break;
            case R.id.sec_kill_duration:
                if(Cst.currentUser == null){
                    WITCH_DO = 0;
                    msgDialog.setMessage("登陆后才能进行投资，是否立即登录");
                    msgDialog.setSureBtn("立即登录");
                    msgDialog.setCancelBtn("再看看");
                    msgDialog.showAs();
                }else {
                    if (Cst.currentUser.idAuthFlag != null && Cst.currentUser.idAuthFlag.equals("Y")) {
                        if ( ! Cst.currentUser.answerFlag){
                            HashMap<String,String> dataMap = new HashMap<String,String>();
                            dataMap.put("investorId", Cst.currentUser.investorId);
                            gotoTargetForResult(WebActivity.class, R.anim.activity_in_from_right, R.anim.activity_out_from_left, "",
                                    new Web("问卷调查", Cst.URL.RISK_QUESTION_URL, "", dataMap), 0);
                        }else {
                            gotoTargetRemovePre(InvestSureActivity.class, R.anim.activity_in_from_right, R.anim.activity_out_from_left, "", secProject);
                        }

                    } else {
                        hasRealName();
                    }
                }
                break;
            case R.id.more:
                ShowMoreLayout.msgHandler.sendEmptyMessage(ShowMoreLayout.UP_TO_AT_MOST);
                break;
        }
    }


    /*
  * 检查是否实名认证
  * */
    private void hasRealName() {
        HasRealNameRequest request = new HasRealNameRequest(mContext, new StringListener() {
            @Override
            public void ResponseListener(String result) {
                HasRealNameResponse response = new HasRealNameResponse();
                if(response.getStatus(result) == 200){
                    Cst.currentUser.idAuthFlag = response.getObject(result);
                    if(Cst.currentUser.idAuthFlag!= null && Cst.currentUser.idAuthFlag.equals("Y")){
                        Utils.getInstances().putInvestorToSharedPreference(mContext, Cst.currentUser);
                        gotoTargetRemovePre(InvestSureActivity.class, R.anim.activity_in_from_right, R.anim.activity_out_from_left, "", secProject);
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
                    toast(response.getMessage(result));
                }
            }

            @Override
            public void ErrorListener(VolleyError volleyError) {
                showErrorMsg(getString(R.string.net_error),navigationBar);
            }
        },Cst.currentUser.investorId);
        request.stringRequest();
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


    private void showLoginPage(){
        showDialogWindow(R.layout.login_and_register, R.style.exist_style, new OnShowListener() {
            @Override
            public void show(View view) {
                rotation(view);
            }
        });
    }
    private void rotation(View view) {
        server = new LoginAndRegisterServer(mContext,view,handler);
    }


    @Override
    public void onComplete(View view) {
        if(pServer == null){
            return;
        }
        switch (view.getId()){
            case R.id.project_detail:
                pServer.showProjectDetailLayout(secProject);
                break;
            case R.id.risk_control:
                break;
            case R.id.invest_list:
                pServer.showInvestList(secProject.projectId,page,Cst.PARAMS.PAGE_SIZE);
                break;
            case R.id.repayment_plan:
                pServer.showRepaymentList(secProject.projectId,page,Cst.PARAMS.PAGE_SIZE);
                break;
        }
    }
    public void loadData(){
        if(secProject != null){
            loadding.showAs();
            String userId = Cst.currentUser == null? "0":Cst.currentUser.investorId;
            ProjectDetailRequest request = new ProjectDetailRequest(mContext, new StringListener() {
                @Override
                public void ResponseListener(String result) {
                    dismiss();
                    ProjectDetailResponse response = new ProjectDetailResponse();
                    if(response.getStatus(result) == 200){
                        secProject = response.getSecProject(result);
                        setData();
                    }else {
                        toast(response.getMessage(result));
                    }
                }

                @Override
                public void ErrorListener(VolleyError volleyError) {
                    dismiss();
                    showErrorMsg(getString(R.string.net_error),navigationBar);
                }
            },secProject.projectId,userId);
            request.stringRequest();
        }
    }

    private void setData() {

        if(secProject != null){
            durationText.setText(secProject.duration);
            durationUnit.setText(NorProject.parseUnit(secProject.durationUnit));
            percentIntegerText.setText(Utils.getInstances().formatUp(secProject.interestRate * 100).split("\\.")[0]);
            percentDecimalText.setText("."+Utils.getInstances().formatUp(secProject.interestRate * 100).split("\\.")[1]);
            explainText.setText("投资10万元，预期收益"+ secProject.expectedReturnForOht+"元");
            projectNameText.setText(secProject.projectTitle);
            projectRiskLevelSeckill.setText(secProject.riskLevel);
            requestAmountText.setText(Utils.getInstances().formatUp(secProject.requestAmount)+"元");
            repaymentMethodText.setText(secProject.repaymentTypeName);
            investmentCertificateText.setText("");
            if(secProject.couponEcFlag != null && secProject.couponEcFlag.equals("Y")){
                investmentCertificateText.setText("体验券、");
            }
            if(secProject.couponFcFlag != null && secProject.couponFcFlag.equals("Y")){
                investmentCertificateText.setText(investmentCertificateText.getText().toString()+"满减券、");
            }
            if(secProject.couponIaFlag != null && secProject.couponIaFlag.equals("Y")){
                investmentCertificateText.setText(investmentCertificateText.getText().toString()+"加息券、");
            }else {
                investmentCertificateText.setText("该类项目不支持使用投资券");
            }
            if (investmentCertificateText.getText().toString().trim().contains("、")){
                String rawText = investmentCertificateText.getText().toString().trim();
                investmentCertificateText.setText(rawText.substring(0, rawText.length()-1));
            }

            long time = secProject.longPublistTime - new Date().getTime();
            biddingAmountText.setText(Utils.getInstances().formatUp(secProject.requestAmount - secProject.biddingAmount));
            if(time > 0){
                new CountDownTimer(time,1000){

                    @Override
                    public void onTick(long millisUntilFinished) {
                        secKillBtn.setText("距秒杀开始    "+convert(millisUntilFinished/(60*60*1000))+":"
                                +convert(millisUntilFinished%(60*60*1000)/(60*1000))+":"+
                                convert(millisUntilFinished%(60*60*1000)%(60*1000)/1000));
                    }
                    @Override
                    public void onFinish() {
                        secKillBtn.setEnabled(true);
                        secKillBtn.setText("立即秒杀");
                    }
                }.start();
            }else {
                secKillBtn.setEnabled(true);
                secKillBtn.setText("立即秒杀");
            }

            if(secProject.status.equals("IPB")){
                secKillBtn.setEnabled(true);
                secKillBtn.setText("立即投资");
            }else {
                secKillBtn.setEnabled(false);
                secKillBtn.setText(secProject.statusName);
            }

        }

    }
    /*
    * 获取项目Id
    * */
    public String getProjectId(){
        return secProject.projectId;
    }
    /*
    * 获取当前页数
    * */
    public int getPage(){
        return page;
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
}
