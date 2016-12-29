package com.yeepbank.android.activity.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.android.volley.VolleyError;
import com.yeepbank.android.Cst;
import com.yeepbank.android.R;
import com.yeepbank.android.activity.business.HomeActivity;
import com.yeepbank.android.activity.business.PurchaseActivity;
import com.yeepbank.android.activity.business.TurnoutActivity;
import com.yeepbank.android.activity.setting.RealNameAuthenticationActivity;
import com.yeepbank.android.activity.user.WebActivity;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.base.BaseFragment;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.model.Web;
import com.yeepbank.android.model.business.EdmAppCount;
import com.yeepbank.android.model.business.EdmOverview;
import com.yeepbank.android.request.business.DailyIncreaseRequest;
import com.yeepbank.android.request.user.HasRealNameRequest;
import com.yeepbank.android.response.business.DailyIncreaseResponse;
import com.yeepbank.android.response.user.HasRealNameResponse;
import com.yeepbank.android.server.LoginAndRegisterServer;
import com.yeepbank.android.server.ShareServer;
import com.yeepbank.android.utils.LoadDialog;
import com.yeepbank.android.utils.Utils;
import com.yeepbank.android.widget.ScrollTextView;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by WW on 2015/9/8.
 *
 *天天盈
 */
public class DailyIncreaseFragment extends BaseFragment implements View.OnClickListener{

    private Button participateInBtn;//买入，立即加入
    private ImageButton purchaseBtn,turnOutBtn;//转出，
    private LinearLayout everydayProfitLayout;
    private LinearLayout scrollTextLayout;
    private View initViewPage,buyAndTurnPage;
    private boolean hasBuyedEdm;//是否买过天天盈
    private boolean hasBuyEdmProject;//是否有可买的天天盈项目,true可进买入，false不可进入
    private EdmAppCount edmAppCount;//天天盈没登录时的信息
    private EdmOverview edmOverview;//天天盈信息
    private TextView percentIntegerText,percentDecimalText,
            dailyEarningsPerText,totalBiddingCountText,everydayProfitGet;
    //收益率整数部分，收益率小数部分，盈利额，投资总人数,一万元每日收益

    private TextView cumulativeGainText,currentBuyingText,
            continuedInvestmentText,applicationTransferText,
            residualAmountText;//累计投资，当期买入,下期续投,申请转出,剩余金额
    private View navigationBar;
    private int WITCH_DO = 0;
    private LoadDialog msgDialog,alertDialog;
    private Handler handler;
    private LoginAndRegisterServer server;
    private ImageView animatorImage,wImg1,wImg2,wImg3,questionImg;
    private double numberStr = 0;
    private ImageView shareImg;

    @Override
    public void initView(View view) {

        navigationBar = view.findViewById(R.id.navigation_bar);
        shareImg = (ImageView) navigationBar.findViewById(R.id.share);
        shareImg.setVisibility(View.GONE);
        //shareImg.setVisibility(View.VISIBLE);
        shareImg.setOnClickListener(this);
        initViewPage = view.findViewById(R.id.init_page);
        buyAndTurnPage = view.findViewById(R.id.buy_and_turn_out);
        everydayProfitLayout = (LinearLayout) view.findViewById(R.id.everyday_profit);
        percentIntegerText = (TextView) view.findViewById(R.id.percent_integer);
        percentDecimalText = (TextView) view.findViewById(R.id.percent_decimal);
        everydayProfitGet = (TextView) view.findViewById(R.id.everyday_profit_get);

        dailyEarningsPerText = (TextView) initViewPage.findViewById(R.id.daily_earnings_per);
        totalBiddingCountText = (TextView) initViewPage.findViewById(R.id.total_bidding_count);
        scrollTextLayout = (LinearLayout) initViewPage.findViewById(R.id.invest_add);

        turnOutBtn = (ImageButton) buyAndTurnPage.findViewById(R.id.turn_out_btn);
        purchaseBtn = (ImageButton) buyAndTurnPage.findViewById(R.id.purchase_btn);
        participateInBtn = (Button) initViewPage.findViewById(R.id.participate_in_btn);
        cumulativeGainText = (TextView) buyAndTurnPage.findViewById(R.id.cumulative_gain);
        currentBuyingText = (TextView) buyAndTurnPage.findViewById(R.id.current_buying);
        continuedInvestmentText = (TextView) buyAndTurnPage.findViewById(R.id.continued_investment);
        applicationTransferText = (TextView) buyAndTurnPage.findViewById(R.id.application_transfer);
        residualAmountText = (TextView) buyAndTurnPage.findViewById(R.id.residual_amount);
        turnOutBtn.setOnClickListener(this);
        purchaseBtn.setOnClickListener(this);
        participateInBtn.setOnClickListener(this);
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
        },0);
        msgDialog.setSureBtn("立即登录");
        msgDialog.setCancelBtn("再看看");
        alertDialog = new LoadDialog(getActivity(), R.style.dialog, false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        },0);

        animatorImage = (ImageView) view.findViewById(R.id.curve_line);
        wImg1 = (ImageView) view.findViewById(R.id.curve_word1);
        wImg2 = (ImageView) view.findViewById(R.id.curve_word2);
        wImg3 = (ImageView) view.findViewById(R.id.curve_word3);

        final ClipDrawable drawable = (ClipDrawable)animatorImage.getDrawable();
        final ClipDrawable wDrawable1 = (ClipDrawable)wImg1.getDrawable();
        final ClipDrawable wDrawable2 = (ClipDrawable)wImg2.getDrawable();
        final ClipDrawable wDrawable3 = (ClipDrawable)wImg3.getDrawable();
        handler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                switch (msg.what){
                    case 0x1233:
                        int level = drawable.getLevel() +100;
                        if(level >= 10000){
                            drawable.setLevel(10000);
                            showWords(wDrawable1,0,0x1234);showWords(wDrawable2,500,0x1235);showWords(wDrawable3,1000,0x1236);
                        }else {
                            drawable.setLevel(level);
                        }
                        break;
                    case 0x1234:
                        int level1 = wDrawable1.getLevel() +300;
                        wDrawable1.setLevel(level1 > 10000? 10000:level1);
                        break;
                    case 0x1235:
                        int level2 = wDrawable2.getLevel() +300;
                        wDrawable2.setLevel(level2 > 10000? 10000:level2);
                        break;
                    case 0x1236:
                        int level3 = wDrawable3.getLevel() +300;
                        wDrawable3.setLevel(level3 > 10000? 10000:level3);
                        break;
                    case Cst.CMD.LOGIN_SUCCESS:
                    case Cst.CMD.REGISTER_SUCCESS:
                        ((BaseActivity)getActivity()).cancelMsg();
                        Cst.currentUser = server.getInvestor();
                        onShow(getActivity());
                        break;
                }
            }
        };

        if(Cst.currentUser != null){
            hasBuyedEdm = Cst.currentUser.hasBuyedEdm;
            hasBuyEdmProject = Cst.currentUser.hasBuyEdmProject;
        }


    }


    @Override
    public int getLayoutResource() {
        return R.layout.daily_increase;
    }

    @Override
    public void fillData() {
        navigationBar.setBackgroundColor(Color.parseColor("#00000000"));
        TextView labelText = (TextView) navigationBar.findViewById(R.id.label_text);
        labelText.setText("天天盈");
        ImageView backLogoImg = (ImageView) navigationBar.findViewById(R.id.back_logo);
        backLogoImg.setVisibility(View.GONE);
        questionImg = (ImageView) navigationBar.findViewById(R.id.question_icon_left);
        questionImg.setVisibility(View.VISIBLE);
        questionImg.setOnClickListener(this);
    }

    @Override
    public void onShow(Context context) {

        edmAppCount = HomeActivity.edmAppCount;
        edmOverview = HomeActivity.edmOverview;
        setInitPageData(edmAppCount);
        if(hasBuyedEdm){
            showFunctionPage();
        }else {
            showInitPage();
        }
        final ClipDrawable drawable = (ClipDrawable)animatorImage.getDrawable();
        final ClipDrawable wDrawable1 = (ClipDrawable)wImg1.getDrawable();
        final ClipDrawable wDrawable2 = (ClipDrawable)wImg2.getDrawable();
        final ClipDrawable wDrawable3 = (ClipDrawable)wImg3.getDrawable();
        drawable.setLevel(0);wDrawable1.setLevel(0);wDrawable2.setLevel(0);wDrawable3.setLevel(0);

        showWords(drawable,0,0x1233);


        if(Cst.currentUser != null){
            DailyIncreaseRequest request = new DailyIncreaseRequest(getActivity(), new StringListener() {
                @Override
                public void ResponseListener(String result) {
                    //loadding.dismiss();
                    DailyIncreaseResponse response = new DailyIncreaseResponse();
                    if(response.getStatus(result) == 200){
                        Cst.currentUser.hasBuyedEdm = hasBuyedEdm = response.hasBuyedEdm(result);
                        Cst.currentUser.hasBuyEdmProject = hasBuyEdmProject = response.hasBuyEdmProject(result);
                        edmAppCount = response.getEdmAppCount(result);
                        edmOverview = response.getObject(result);
                        HomeActivity.edmAppCount = edmAppCount;
                        HomeActivity.edmOverview = edmOverview;
                        setInitPageData(edmAppCount);
                        setBuyAndTurnPageData(edmOverview);
                        if(hasBuyedEdm){
                            showFunctionPage();
                        }else {
                            showInitPage();
                        }
                    }else {
                        if(isAdded()){
                            ((BaseActivity)getActivity()).toast(response.getMessage(result));
                        }
                    }
                }

                @Override
                public void ErrorListener(VolleyError volleyError) {
//                    loadding.dismiss();
                    if(isAdded()){
                        ((BaseActivity)getActivity()).showErrorMsg(getString(R.string.net_error), navigationBar);
                    }
                }
            },Cst.currentUser.investorId);
            request.stringRequest();
        }else {
            showInitPage();
        }
    }

    private void showWords(final ClipDrawable wordDraw,long duration, final int cmd){

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(cmd);
                if(wordDraw.getLevel() < 10000){
                    handler.sendEmptyMessage(cmd);
                    handler.post(this);
                }
            }
        };
        new Handler().postDelayed(runnable,duration);
    }

    public void setInitPageData(EdmAppCount edmAppCount){
        if(edmAppCount != null){
            String  maxInterestRate = Utils.getInstances().formatUp(edmAppCount.maxInterestRate * 100);
            String[] values = maxInterestRate.trim().split("\\.");
            percentIntegerText.setText(values[0]);
            percentDecimalText.setText("."+values[1]);
            dailyEarningsPerText.setText(Utils.getInstances().formatUp(edmAppCount.dailyEarningsPer));
            everydayProfitGet.setText(Utils.getInstances().formatUp(edmAppCount.dailyEarningsPer));
            totalBiddingCountText.setText(edmAppCount.totalBiddingCount);
            Log.e("tag", "edmAppCount.allTotalBiddingAmount:" + edmAppCount.allTotalBiddingAmount + " ; numberStr" + numberStr);
            if (edmAppCount.allTotalBiddingAmount != numberStr){

                numberStr = edmAppCount.allTotalBiddingAmount;
                addScrollText();
                Log.e("tag", " ; numberStr" + numberStr);
            }
        }
    }

    public void setBuyAndTurnPageData(EdmOverview edmOverview){
        if(edmOverview != null){
            cumulativeGainText.setText(Utils.getInstances().formatUp(edmOverview.totalInterest));
            currentBuyingText.setText(Utils.getInstances().formatUp(edmOverview.totalBiddingAmountToday));
            continuedInvestmentText.setText(Utils.getInstances().formatUp(edmOverview.autoAmount));
            applicationTransferText.setText(Utils.getInstances().formatUp(edmOverview.exitingAmount));
            residualAmountText.setText(Utils.getInstances().formatUp(edmOverview.projectBalanceAmount));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.turn_out_btn:
                if(edmOverview != null){
                    if(edmOverview.autoEdmAmount > 0 || edmOverview.autoPjtAmount > 0||edmOverview.autoRjtAmount > 0){
                        ((BaseActivity)getActivity()).gotoTarget(TurnoutActivity.class,R.anim.activity_in_from_right,R.anim.activity_out_from_left,"");
                    }else {
                        ((BaseActivity)getActivity()).toast("暂无可转出项目");
                    }
                }
                break;
            case R.id.purchase_btn:
                if(hasBuyEdmProject){
                    if (! Cst.currentUser.answerFlag){
                        HashMap<String,String> dataMap = new HashMap<String,String>();
                        dataMap.put("investorId", Cst.currentUser.investorId);
                        ((HomeActivity)getActivity()).gotoTargetForResult(WebActivity.class, R.anim.activity_in_from_right, R.anim.activity_out_from_left, "",
                                new Web("问卷调查", Cst.URL.RISK_QUESTION_URL, "", dataMap), 0);
                    }else {
                        ((BaseActivity) getActivity()).
                                gotoTarget(PurchaseActivity.class, R.anim.activity_in_from_right, R.anim.activity_out_from_left, "");
                    }
                }else {
                    ((BaseActivity)getActivity()).toast("暂无可买项目");
                }
                break;
            case R.id.participate_in_btn:


                if(Cst.currentUser == null){
                    WITCH_DO = 0;
                    msgDialog.setMessage("登录后才能进行投资，是否立即登录");
                    msgDialog.setSureBtn("立即登录");
                    msgDialog.setCancelBtn("再看看");
                    msgDialog.showAs();
                }else {
                    if (Cst.currentUser.idAuthFlag != null && Cst.currentUser.idAuthFlag.equals("Y")) {
                        showFunctionPage();
                    } else {
                        hasRealName();
                    }
                }
                break;
            case R.id.question_icon_left:
                ((BaseActivity)getActivity()).gotoWeb(new Web("常见问题", Cst.URL.QUESTION_DETAIL_URL,"天天盈"));
                //((BaseActivity)getActivity()).gotoWeb(new Web("常见问题", "http://test.app.yeepbank.xyz/views/questionPage.html","天天盈"));
                break;
            case R.id.share:
                ShareServer.getInstances(getActivity(), ((BaseActivity)getActivity()).getIwxapi(),
                        "我理财赚了不少钱，抱我大腿带你上船", "点击注册易宝金融，你我各得好礼")
                        .show(LayoutInflater.from(getActivity()).inflate(getLayoutResource(),null));
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
                        showFunctionPage();
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
                ((BaseActivity)getActivity()).showErrorMsg(getString(R.string.net_error),navigationBar);
            }
        },Cst.currentUser.investorId);
        request.stringRequest();
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



    
    private void addScrollText(){
        if(scrollTextLayout == null)return;
        scrollTextLayout.removeAllViews();
        String value = Utils.getInstances().thousandFormat(edmAppCount.allTotalBiddingAmount);
        for(int i = 0; i < value.length(); i++){
            String number = String.valueOf(value.charAt(i));
            if(isShouldBeCreate(number)){
                View view = createScrollText(number);
                if(view != null){
                    scrollTextLayout.addView(view);
                }

            }else {
                View view = createSymbolText(number);
                if(view != null){
                    scrollTextLayout.addView(view);
                }

            }
        }
    }

    private View createSymbolText(String number) {
        if(getActivity() == null){
            return null;
        }
        TextView textView = new TextView(getActivity());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.FILL_PARENT);
        params.bottomMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,2,getResources().getDisplayMetrics());
        textView.setLayoutParams(params);
        textView.setTextColor(Color.parseColor("#ff6babff"));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        textView.setText(number);
        return textView;
    }

    private ScrollTextView createScrollText(String number){
        if(getActivity() == null){
            return null;
        }
        XmlPullParser parser = getActivity().getResources().getLayout(R.layout.scroll_text);
        AttributeSet attributeSet = Xml.asAttributeSet(parser);
        int type;
        try {
            while ((type = parser.next()) != XmlPullParser.START_TAG &&
                    type != XmlPullParser.END_DOCUMENT) {
            }
            ScrollTextView scrollTextView = new ScrollTextView(getActivity(),attributeSet,Integer.parseInt(number));
            return scrollTextView;
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  null;

    }

    private boolean isShouldBeCreate(String number) {
        return number.matches("^\\d$");
    }

    /*
    * 直接显示买入转出界面
    * */
    private void showFunctionPage(){
        buyAndTurnPage.setVisibility(View.VISIBLE);
        everydayProfitLayout.setVisibility(View.VISIBLE);
        initViewPage.setVisibility(View.GONE);
    }

    /*
    * 显示初始界面
    * */
    private void showInitPage(){
        buyAndTurnPage.setVisibility(View.GONE);
        everydayProfitLayout.setVisibility(View.GONE);
        initViewPage.setVisibility(View.VISIBLE);

        for(int i = 0; i < scrollTextLayout.getChildCount(); i++){
            if(scrollTextLayout.getChildAt(i) instanceof ScrollTextView ){
                ((ScrollTextView)scrollTextLayout.getChildAt(i)).recover();
            }
        }
        for(int i = 0; i < scrollTextLayout.getChildCount(); i++){
            if(scrollTextLayout.getChildAt(i) instanceof ScrollTextView ){
                ((ScrollTextView)scrollTextLayout.getChildAt(i)).scroll();
            }
        }
    }
}
