package com.yeepbank.android.activity.business;

import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
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
import com.yeepbank.android.request.business.TransformProjectDetailRequest;
import com.yeepbank.android.request.user.HasRealNameRequest;
import com.yeepbank.android.response.business.TransformProjectDetailResponse;
import com.yeepbank.android.response.user.HasRealNameResponse;
import com.yeepbank.android.server.LoginAndRegisterServer;
import com.yeepbank.android.server.ProjectDetailServer;
import com.yeepbank.android.utils.LoadDialog;
import com.yeepbank.android.utils.Utils;
import com.yeepbank.android.widget.ChangeItemWithAnimationLayout;
import com.yeepbank.android.widget.ShowMoreLayout;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by WW on 2015/11/10.
 */
public class TransformActivity extends BaseActivity implements View.OnClickListener,Cst.OnSlideCompleteListener{

    private ImageView animatorImage,animationImage1;
    private View navigationBar;
    private Button investBtn;
    private LinearLayout moreLayout;
    private int page;//当前页数
    private ImageView triangleImg;
    private ChangeItemWithAnimationLayout changeItemWithAnimationLayout;
    private ProjectDetailServer pServer;
    private ShowMoreLayout showMoreLayout;
    private Handler handler;
    private ClipDrawable drawable1;
    private Runnable runnable = null;
    private BaseModel data;
    private LoginAndRegisterServer server;
    private TextView durationText,durationUnit,percentIntegerText,
            percentDecimalText,explainText,projectNameText,transferAmountText,
            repaymentMethodText,investmentRequestmentText;//项目期限,项目期限单位,进度整数,进度小数,
            // 项目说明,项目名称,转让金额，还款方式,投资券,投资需求
    private LoadDialog msgDialog,alertDialog;
    private int WITCH_DO = 0;
    public  int initChoose = 0;
    private TextView projectRiskLevelTrans;

    @Override
    protected void initView() {

        Log.e("Time","初始化界面do_time:"+System.currentTimeMillis());
        showMoreLayout = (ShowMoreLayout) findViewById(R.id.show_more);
        animatorImage = (ImageView) findViewById(R.id.curve_line_trans);
        animationImage1 = (ImageView) findViewById(R.id.curve_line_font_trans);
        navigationBar = findViewById(R.id.navigation_bar);
        investBtn = (Button) findViewById(R.id.invest_at_must_trans);
        investBtn.setOnClickListener(this);
        moreLayout = (LinearLayout) findViewById(R.id.more);
        moreLayout.setOnClickListener(this);
        //webView = (WebView) findViewById(R.id.web_more);
        triangleImg = (ImageView) findViewById(R.id.triangle);
        changeItemWithAnimationLayout = (ChangeItemWithAnimationLayout) findViewById(R.id.navigation_item);
        changeItemWithAnimationLayout.setOnSlideCompleteListener(this);
//        investListProjectMore = (LinearLayout) findViewById(R.id.invest_list_more_detail);
//        repaymentPlanProjectMore = (LinearLayout) findViewById(R.id.repayment_plan_more_detail);
//        projectDetailLayout = (LinearLayout) findViewById(R.id.project_detail_layout);

        durationText = (TextView) findViewById(R.id.duration_trans);
        durationUnit = (TextView) findViewById(R.id.duration_unit_trans);
        percentIntegerText = (TextView) findViewById(R.id.percent_integer_trans);
        percentDecimalText = (TextView) findViewById(R.id.percent_decimal_trans);
        explainText = (TextView) findViewById(R.id.explain_trans);
        projectNameText = (TextView) findViewById(R.id.project_name_trans);
        transferAmountText = (TextView) findViewById(R.id.transfer_amount_trans);
        repaymentMethodText = (TextView) findViewById(R.id.repayment_method_trans);
        //investmentCertificateText = (TextView) findViewById(R.id.investment_certificate_trans);
        //investmentRequestmentText = (TextView) findViewById(R.id.investment_requestment_trans);
        projectRiskLevelTrans= (TextView) findViewById(R.id.project_risk_level_trans);
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
        },0).setMessage("不能购买自己转出的项目");
        alertDialog.setSureBtn("知道了");

        if(getIntent().getBundleExtra("data") != null){
            data = (BaseModel) getIntent().getBundleExtra("data").getSerializable("data");
            changeItemWithAnimationLayout.setStatus(data.status, "TRANS_PROJECT");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (changeItemWithAnimationLayout.getItemList() != null && changeItemWithAnimationLayout.getItemList().size() > 0){
                        ArrayList<Integer> layoutIds = new ArrayList<Integer>();
                        for (int i = 0; i < changeItemWithAnimationLayout.getItemList().size(); i++) {
                            layoutIds.add(changeItemWithAnimationLayout.getItemList().get(i).textLayout.getId());

                        }
                        pServer = new ProjectDetailServer(mContext,showMoreLayout,changeItemWithAnimationLayout,data,layoutIds);
                    }

                }
            });
        }

    }

    @Override
    protected void fillData() {
        final ClipDrawable drawable = (ClipDrawable)animatorImage.getDrawable();
        drawable1 = (ClipDrawable) animationImage1.getDrawable();
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
        loadData();

    }

    /*获取到控件尺寸后把箭头移向第一个选项中间位置*/
    private void moveTriangleImg(){
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) triangleImg.getLayoutParams();
        if (params != null) {
            params.leftMargin = changeItemWithAnimationLayout.getCheckLayoutWidth() / 2;
            triangleImg.setLayoutParams(params);
        }
    }

    private void setData(){
        if(data != null){
            durationText.setText(data.buyerHoldingDays);
            percentIntegerText.setText(Utils.getInstances().formatUp(data.buyerRoi * 100).split("\\.")[0]);
            percentDecimalText.setText("."+Utils.getInstances().formatUp(data.buyerRoi * 100).split("\\.")[1]);
            explainText.setText("将于"+data.debtEndDate+"获得收益 "+data.buyerInvestmentIncome+" 元");
            projectNameText.setText(data.projectTitle);
            String tranStr=Utils.getInstances().thousandFormatWithUnit(data.transferPrice);
            SpannableString trans=new SpannableString(tranStr);
            trans.setSpan(new ForegroundColorSpan(Color.parseColor("#fa886d")), 0, tranStr.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            transferAmountText.setText(trans);
            repaymentMethodText.setText(data.repaymentTypeName);
            if(data.status.equals("IPB")){
                investBtn.setEnabled(true);
                investBtn.setText("立即投资");
            }else {
                investBtn.setEnabled(false);
                investBtn.setText(data.statusName);
            }
            projectRiskLevelTrans.setText(data.riskLevel);
        }
    }

    @Override
    protected int getLayoutResources() {
        return R.layout.transform;
    }

    @Override
    protected View getNavigationBar() {
        ((TextView)navigationBar.findViewById(R.id.label_text)).setText("项目详情");
        return navigationBar;
    }

    protected void initNavigationBar(View navigationBar){
//        navigationBar.setBackgroundColor(Color.parseColor("#00000000"));
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
            case R.id.more:
                ShowMoreLayout.msgHandler.sendEmptyMessage(ShowMoreLayout.UP_TO_AT_MOST);
                break;
            case R.id.invest_at_must_trans:
                    if(Cst.currentUser == null){
                        WITCH_DO = 0;
                        msgDialog.setMessage("登陆后才能进行投资，是否立即登录");
                        msgDialog.showAs();
                    }else {
                        if (Cst.currentUser.idAuthFlag != null && Cst.currentUser.idAuthFlag.equals("Y")) {
                            if(data.sellerId.trim().equals(Cst.currentUser.investorId)){
                                alertDialog.showAs();
                                return;
                            }
                            if ( ! Cst.currentUser.answerFlag){
                                HashMap<String,String> dataMap = new HashMap<String,String>();
                                dataMap.put("investorId", Cst.currentUser.investorId);
                                gotoTargetForResult(WebActivity.class, R.anim.activity_in_from_right, R.anim.activity_out_from_left, "",
                                        new Web("问卷调查", Cst.URL.RISK_QUESTION_URL, "", dataMap), 0);
                            }else {
                                gotoTargetRemovePre(InvestSureActivity.class, R.anim.activity_in_from_right, R.anim.activity_out_from_left, "", data);
                            }
                        } else {
                            hasRealName();
                        }
                    }
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
                        if(data.sellerId.trim().equals(Cst.currentUser.investorId)){
                            alertDialog.showAs();
                            return;
                        }
                        gotoTargetRemovePre(InvestSureActivity.class, R.anim.activity_in_from_right, R.anim.activity_out_from_left, "", data);
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

    @Override
    public void onComplete(View view) {

        witchBusiness(view.getId());
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

    public void loadData(){

        if(data != null){
            loadding.showAs();
            TransformProjectDetailRequest request = new TransformProjectDetailRequest(mContext, new StringListener() {
                @Override
                public void ResponseListener(String result) {
                    loadding.dismiss();
                    TransformProjectDetailResponse response = new TransformProjectDetailResponse();
                    if(response.getStatus(result) == 200){
                        data = response.getObject(result);
                        setData();
                    }else {
                        toast(response.getMessage(result));
                    }
                }


                @Override
                public void ErrorListener(VolleyError volleyError) {
                    loadding.dismiss();
                    showErrorMsg(getString(R.string.net_error),navigationBar);
                }
            },data.transferId);
            request.stringRequest();
        }

    }


    public void loadInitChooseData( int id){
        witchBusiness(id);
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
