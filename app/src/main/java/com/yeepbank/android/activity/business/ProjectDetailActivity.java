package com.yeepbank.android.activity.business;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.widget.*;
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
import com.yeepbank.android.widget.gridpasswordview.Util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by WW on 2015/9/18.
 */
public class ProjectDetailActivity extends BaseActivity implements View.OnClickListener,Cst.OnSlideCompleteListener{
    private ImageView animatorImage;
    private View navigationBar;
    private Button investBtn;
    private LinearLayout moreLayout;
    private ImageView triangleImg;
    private ChangeItemWithAnimationLayout changeItemWithAnimationLayout;
   // private LinearLayout investListProjectMore,repaymentPlanProjectMore,projectDetailLayout;
    private ProjectDetailServer pServer;
    private ShowMoreLayout showMoreLayout;
    private Handler handler;
    private Runnable runnable = null;
    private BaseModel data;
    private TextView durationText,durationUnit,percentIntegerText,
            percentDecimalText,explainText,projectNameText,biddingAmountText,requestAmountText,
            repaymentMethodText,investmentCertificateText;//项目期限,项目期限单位,进度整数,进度小数,项目说明,项目名称,剩余金额,总金额，还款方式,投资券
    private LoginAndRegisterServer server;
    private int page = 0;
    private LoadDialog msgDialog,alertDialog;
    private int WITCH_DO = 0;//0表示未登录，1表示未实名认证，2表示非新手投资新手项目
    public int initChoose = 0;
    private TextView projectRiskLevel;//项目的风险等级

    @Override
    protected void initView() {
        showMoreLayout = (ShowMoreLayout) findViewById(R.id.show_more);
        animatorImage = (ImageView) findViewById(R.id.curve_line);
        navigationBar = findViewById(R.id.navigation_bar);
        investBtn = (Button) findViewById(R.id.invest_at_must);
        investBtn.setOnClickListener(this);
        moreLayout = (LinearLayout) findViewById(R.id.more);
        moreLayout.setOnClickListener(this);
        triangleImg = (ImageView) findViewById(R.id.triangle);
        changeItemWithAnimationLayout = (ChangeItemWithAnimationLayout) findViewById(R.id.navigation_item);

        changeItemWithAnimationLayout.setOnSlideCompleteListener(this);
        projectRiskLevel= (TextView) findViewById(R.id.project_risk_level);

        durationText = (TextView) findViewById(R.id.duration);
        durationUnit = (TextView) findViewById(R.id.duration_unit);
        percentIntegerText = (TextView) findViewById(R.id.percent_integer);
        percentDecimalText = (TextView) findViewById(R.id.percent_decimal);
        explainText = (TextView) findViewById(R.id.explain);
        projectNameText = (TextView) findViewById(R.id.project_name);
        biddingAmountText = (TextView) findViewById(R.id.bidding_amount);
        requestAmountText = (TextView) findViewById(R.id.request_amount);
        repaymentMethodText = (TextView) findViewById(R.id.repayment_method);
        //investmentCertificateText = (TextView) findViewById(R.id.investment_certificate);
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
        msgDialog.setSureBtn("立即登录");
        msgDialog.setCancelBtn("再看看");

        alertDialog = new LoadDialog(mContext, R.style.dialog, false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        },0).setMessage("您已做过投资，请把机会留给新手吧").setSureBtn("我知道了");

        if(getIntent().getBundleExtra("data") != null){
            data = (BaseModel) getIntent().getBundleExtra("data").getSerializable("data");

            changeItemWithAnimationLayout.setStatus(data.status, "YB");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (changeItemWithAnimationLayout.getItemList() != null && changeItemWithAnimationLayout.getItemList().size() > 0) {
                        ArrayList<Integer> layoutIds = new ArrayList<Integer>();
                        for (int i = 0; i < changeItemWithAnimationLayout.getItemList().size(); i++) {
                            layoutIds.add(changeItemWithAnimationLayout.getItemList().get(i).textLayout.getId());
                            pServer = new ProjectDetailServer(mContext, showMoreLayout, changeItemWithAnimationLayout, data, layoutIds);

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
        handler.postDelayed(runnable, 1500);

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
            params.leftMargin = changeItemWithAnimationLayout.getCheckLayoutWidth() / 2 - - triangleImg.getWidth() / 2;
            triangleImg.setLayoutParams(params);
        }
    }


    private void setData() {

        if(data != null){
            durationText.setText(data.duration);
            durationUnit.setText(NorProject.parseUnit(data.durationUnit));
            percentIntegerText.setText(Utils.getInstances().formatUp(data.interestRate * 100).split("\\.")[0]);
            percentDecimalText.setText("."+Utils.getInstances().formatUp(data.interestRate * 100).split("\\.")[1]);
            explainText.setText("投资10万元，预期收益"+ data.expectedReturnForOht+"元");
            projectNameText.setText(data.projectTitle);
            biddingAmountText.setText(Utils.getInstances().format(data.requestAmount - data.biddingAmount)+"元");
            requestAmountText.setText(Utils.getInstances().format(data.requestAmount)+"元");
            repaymentMethodText.setText(data.repaymentTypeName);
            //investmentCertificateText.setText("");
            StringBuilder sb = new StringBuilder();
            if(data.couponEcFlag != null && data.couponEcFlag.equals("Y")){
                sb.append("体验券、");
            }
            if(data.couponFcFlag != null && data.couponFcFlag.equals("Y")){
                sb.append("满减券、");
            }
            if(data.couponIaFlag != null && data.couponIaFlag.equals("Y")){
                sb.append("加息券、");
                //investmentCertificateText.setText(investmentCertificateText.getText().toString()+"加息券、");
            }

            if (sb.toString().trim().contains("、")){
                //investmentCertificateText.setText(sb.toString().substring(0, sb.toString().length()-1));
            }else {
                //investmentCertificateText.setText("该类项目不支持使用投资券");
            }
            /*
            * 判断项目的剩余金额是否为0，如果不问0，并且是秒杀项目或新手项目
            * */
//            if(data.requestAmount - data.biddingAmount <= 0){
//                investBtn.setEnabled(false);
//                investBtn.setText(data.statusName);
//            }else {
//                investBtn.setEnabled(true);
//                if((data.projectType.equals("NOR")||data.projectType.equals("FFI"))){
//                    investBtn.setText("立即投资");
//                }else {
//                    investBtn.setText(data.statusName);
//                }
//            }

           if(data.status.equals("IPB")){
                investBtn.setEnabled(true);
                investBtn.setText("立即投资");
            }else {
                investBtn.setEnabled(false);
                investBtn.setText(data.statusName);
            }

        }
        projectRiskLevel.setText(data.riskLevel);

    }

    @Override
    protected int getLayoutResources() {
        return R.layout.project_detail;
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_layout:
                onBackPressed();
                break;
            /*
            * 投资前判断，如果没登录，提示登录，登陆过如果没实名认证提示实名认证，如果实名认证过，并且项目是新手项目，判断该投资者是不是新手
            * */
            case R.id.invest_at_must:
                if(Cst.currentUser == null){
                    WITCH_DO = 0;
                    msgDialog.setMessage("登陆后才能进行投资，是否立即登录");
                    msgDialog.showAs();
                }else {
                    if(Cst.currentUser.idAuthFlag!= null && Cst.currentUser.idAuthFlag.equals("Y")){
                        if(data.projectType.trim().equals("FFI")){
                            if(Cst.currentUser.noviciate != null && !Cst.currentUser.noviciate.equals("Y")){
                                WITCH_DO = 2;
                                alertDialog.showAs();
                            }else {
                                if (!Cst.currentUser.answerFlag){
                                    HashMap<String,String> dataMap = new HashMap<String,String>();
                                    dataMap.put("investorId", Cst.currentUser.investorId);
                                    gotoTargetForResult(WebActivity.class, R.anim.activity_in_from_right, R.anim.activity_out_from_left, "",
                                            new Web("问卷调查", Cst.URL.RISK_QUESTION_URL, "", dataMap),0);
                                }else {
                                    gotoTargetRemovePre(InvestSureActivity.class,R.anim.activity_in_from_right,R.anim.activity_out_from_left,"",data);
                                }

                            }
                        }else {
                            if (!Cst.currentUser.answerFlag){
                                HashMap<String,String> dataMap = new HashMap<String,String>();
                                dataMap.put("investorId", Cst.currentUser.investorId);
                                gotoTargetForResult(WebActivity.class, R.anim.activity_in_from_right, R.anim.activity_out_from_left, "",
                                        new Web("问卷调查", Cst.URL.RISK_QUESTION_URL, "", dataMap),0);
                            }else {
                                gotoTargetRemovePre(InvestSureActivity.class,R.anim.activity_in_from_right,R.anim.activity_out_from_left,"",data);
                            }
                            //
                        }
                    }else {
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
                        gotoTargetRemovePre(InvestSureActivity.class,R.anim.activity_in_from_right,R.anim.activity_out_from_left,"",data);
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
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onComplete(View view) {

        if(pServer == null){
            return;
        }

        switch (view.getId()){
            case R.id.project_detail:
                pServer.showProjectDetailLayout(data);
                break;
            case R.id.risk_control:
                break;
            case R.id.invest_list:
                pServer.showInvestList(data.projectId,page,Cst.PARAMS.PAGE_SIZE);
                break;
            case R.id.repayment_plan:
                pServer.showRepaymentList(data.projectId, page, Cst.PARAMS.PAGE_SIZE);
                break;
            case R.id.invest_repayment_plan:
                pServer.showInvestRepaymentList(data.projectId,page,Cst.PARAMS.PAGE_SIZE);/*投资计划还款*/
                break;
        }
    }

    /*
    * 获取项目Id
    * */
    public String getProjectId(){
        return data.projectId;
    }
    /*
    * 获取当前页数
    * */
    public int getPage(){
        return page;
    }

    public void loadData(){
        if(data != null){
            loadding.showAs();
            String userId = Cst.currentUser == null? "0":Cst.currentUser.investorId;
            ProjectDetailRequest request = new ProjectDetailRequest(mContext, new StringListener() {
                @Override
                public void ResponseListener(String result) {
                    dismiss();
                    ProjectDetailResponse response = new ProjectDetailResponse();
                    if(response.getStatus(result) == 200){
                        data = response.getObject(result);
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
            },data.projectId,userId);
            request.stringRequest();
        }

    }

    @Override
    protected void onDestroy() {
        if(loadding!= null && loadding.isShowing()){
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


    public void loadInitChooseData( int id){
        witchBusiness(id);
    }


    private void witchBusiness(int id){
        if(pServer == null){
            return;
        }
        switch (id){
            case R.id.project_detail:
                pServer.showProjectDetailLayout(data);
                break;
            case R.id.risk_control:
                break;
            case R.id.invest_list:
                pServer.showInvestList(data.projectId,page,Cst.PARAMS.PAGE_SIZE);
                break;
            case R.id.repayment_plan:
                pServer.showRepaymentList(data.projectId, page, Cst.PARAMS.PAGE_SIZE);
                break;
            case R.id.invest_repayment_plan:
                pServer.showInvestRepaymentList(data.projectId, page, Cst.PARAMS.PAGE_SIZE);/*投资计划还款*/
                break;
        }
    }
}
