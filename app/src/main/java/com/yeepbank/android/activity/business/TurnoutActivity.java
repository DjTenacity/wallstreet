package com.yeepbank.android.activity.business;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.android.volley.VolleyError;
import com.yeepbank.android.Cst;
import com.yeepbank.android.R;
import com.yeepbank.android.activity.user.WebActivity;
import com.yeepbank.android.adapter.TurnoutAdapter;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.model.Web;
import com.yeepbank.android.model.business.Purchase;
import com.yeepbank.android.request.business.TurnOutRequest;
import com.yeepbank.android.response.business.TurnOutResponse;
import com.yeepbank.android.utils.LoadDialog;
import com.yeepbank.android.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by WW on 2015/10/20.
 * 转出
 */
public class TurnoutActivity extends BaseActivity implements View.OnClickListener{

    private EditText turnOutEdit;

    private Button turnOutToBalanceBtn;
    private TurnoutAdapter turnoutAdapter;
    private ArrayList<Purchase> purchaseArrayList;
    private ListView turnOutList;
    private Purchase purchase;
    private LoadDialog alertDialog,notifyDialog;
    private double notifyMoney;
    private String ruleType = "";
    private View navigationBar;
    private TextView estimatedTimeText;//预计到账时间
    private ImageView questionImg;
    @Override
    protected void initView() {

        navigationBar = findViewById(R.id.navigation_bar);
        turnOutEdit = (EditText) findViewById(R.id.turn_out_money);
        turnOutEdit.requestFocus();
        turnOutToBalanceBtn = (Button) findViewById(R.id.turn_out_to_balance_btn);
        turnOutToBalanceBtn.setOnClickListener(this);
        changeButtonStateWithValue(turnOutEdit, turnOutToBalanceBtn, R.drawable.turn_out_to_balance,
                R.drawable.turn_out_to_balance_not_activation);

        purchaseArrayList = new ArrayList<Purchase>();
        Purchase hjbPurchase = new Purchase();
        hjbPurchase.biddingAmount = HomeActivity.edmOverview.autoEdmAmount;
        hjbPurchase.projectTitle = "黄金宝";

        if(HomeActivity.edmOverview.autoEdmAmount > 0){
            purchaseArrayList.add(hjbPurchase);
        }

        Purchase pjtPurchase = new Purchase();
        pjtPurchase.biddingAmount = HomeActivity.edmOverview.autoPjtAmount;
        pjtPurchase.projectTitle = "票据通";
        if(HomeActivity.edmOverview.autoPjtAmount > 0){
            purchaseArrayList.add(pjtPurchase);
        }

        //2016.9.8号xiaogang.dong增加
        Purchase rjtPurchase=new Purchase();
        rjtPurchase.biddingAmount=HomeActivity.edmOverview.autoRjtAmount;
        rjtPurchase.projectTitle="日结通";
        if(HomeActivity.edmOverview.autoRjtAmount>0){
            purchaseArrayList.add(rjtPurchase);
        }

        turnoutAdapter = new TurnoutAdapter(purchaseArrayList,mContext);

        turnOutList = (ListView) findViewById(R.id.turn_out_list);
        turnOutList.setAdapter(turnoutAdapter);
        turnoutAdapter.notifyDataSetChanged();

        estimatedTimeText = (TextView) findViewById(R.id.estimated_time);
        estimatedTimeText.setText("预计下个工作日 ");

        notifyDialog = new LoadDialog(mContext, R.style.dialog, false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(notifyMoney >= 0){
                    turnOutEdit.setText(Utils.getInstances().format(notifyMoney));
                    Selection.setSelection(turnOutEdit.getText(), turnOutEdit.getText().length());
                }
                notifyDialog.dismiss();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyDialog.dismiss();
            }
        },0).setTitle("提示");
        alertDialog = new LoadDialog(mContext, R.style.dialog, false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    turnOutEdit.setText("");
                alertDialog.dismiss();
            }
        },0).setTitle("提示").setSureBtn("我知道了");


    }

    @Override
    protected void fillData() {
        turnOutList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < turnOutList.getChildCount(); i++) {
                    View v = turnOutList.getChildAt(i);
                    v.setBackgroundResource(R.drawable.round_bg);
                    ((TextView) v.findViewById(R.id.project_name)).setTextColor(Color.parseColor("#CACACA"));
                    ((TextView) v.findViewById(R.id.trun_out_textViewOne)).setTextColor(Color.parseColor("#CACACA"));
                    ((TextView) v.findViewById(R.id.residual_amount)).setTextColor(Color.parseColor("#CACACA"));
                    ((TextView) v.findViewById(R.id.turn_out_textViewTwo)).setTextColor(Color.parseColor("#CACACA"));
                    ((ImageView)v.findViewById(R.id.radio_img)).setImageResource(R.drawable.radio_uncheck_icon);
                }
                view.setBackgroundResource(R.drawable.daily_increast_bg_icon);
                ((TextView) view.findViewById(R.id.project_name)).setTextColor(Color.parseColor("#ff41b1ff"));
                ((TextView) view.findViewById(R.id.trun_out_textViewOne)).setTextColor(Color.parseColor("#666666"));
                ((TextView) view.findViewById(R.id.residual_amount)).setTextColor(Color.parseColor("#3887BE"));
                ((TextView) view.findViewById(R.id.turn_out_textViewTwo)).setTextColor(Color.parseColor("#666666"));
                ((ImageView)view.findViewById(R.id.radio_img)).setImageResource(R.drawable.radio_checked_icon);
                purchase = turnoutAdapter.getData().get(position);
                /*if(purchase.projectTitle.toString().trim().equals("黄金宝")){
                    ruleType = "EDM";
                }else {
                    ruleType = "PJT";
                }*/
                //2016.9.8号xiaogang.dong注释并且修改
                if(purchase.projectTitle.toString().trim().equals("黄金宝")){
                    ruleType = "EDM";
                }else if(purchase.projectTitle.toString().trim().equals("日结通")){
                    ruleType="RJT";
                }else{
                    ruleType = "PJT";
                }

            }
        });
        turnOutList.post(new Runnable() {
            @Override
            public void run() {

                if(turnOutList!= null && turnOutList.getChildCount() > 0){
                    View view = turnOutList.getChildAt(0);
                    view.setBackgroundResource(R.drawable.daily_increast_bg_icon);
                    ((TextView) view.findViewById(R.id.project_name)).setTextColor(Color.parseColor("#ff41b1ff"));
                    ((TextView) view.findViewById(R.id.trun_out_textViewOne)).setTextColor(Color.parseColor("#666666"));
                    ((TextView) view.findViewById(R.id.residual_amount)).setTextColor(Color.parseColor("#3887BE"));
                    ((TextView) view.findViewById(R.id.turn_out_textViewTwo)).setTextColor(Color.parseColor("#666666"));
                    ((ImageView)view.findViewById(R.id.radio_img)).setImageResource(R.drawable.radio_checked_icon);
                    purchase = turnoutAdapter.getData().get(0);
                   /* if(purchase.projectTitle.toString().trim().equals("黄金宝")){
                        ruleType = "EDM";
                    }else {
                        ruleType = "PJT";
                    }*/
                    if(purchase.projectTitle.toString().trim().equals("黄金宝")){
                        ruleType = "EDM";
                    }else if(purchase.projectTitle.toString().trim().equals("日结通")){
                        ruleType="RJT";
                    }else{
                        ruleType = "PJT";
                    }

                }
            }
        });

        turnOutEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculation(s, start);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private String getNextDate(){
        Date date=new Date();//取时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);//把日期往后增加一天.整数往后推,负数往前移动
        date=calendar.getTime(); //这个时间就是日期往后推一天的结果
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }

    @Override
    protected int getLayoutResources() {
        return R.layout.turn_out;
    }

    @Override
    protected View getNavigationBar() {

        questionImg = (ImageView) findViewById(R.id.question_icon);
        questionImg.setVisibility(View.VISIBLE);
        questionImg.setOnClickListener(this);
        return navigationBar;
    }

    @Override
    public LoadDialog getLoadDialog() {
        return loadding;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.turn_out_to_balance_btn:
                turnOut();
                break;
            case R.id.question_icon:
                gotoWeb(new Web("转出说明",Cst.URL.TURN_OUT_URL,"转出"));
                break;
        }
    }

    private void turnOut() {
        String investAmount = turnOutEdit.getText().toString();
        if (Double.parseDouble(investAmount) % 100 != 0){

            if(Double.parseDouble(investAmount) > 100){
                notifyMoney = ((int)Double.parseDouble(investAmount)/100)*100;
                notifyDialog.setMessage("转出金额必须是100的整数倍，是否修改为:"+ Utils.getInstances().format(notifyMoney) );
                notifyDialog.showAs();
            }else {
                alertDialog.setMessage("金额不能小于100");
                alertDialog.showAs();
            }

            return;
        }

        if (Double.parseDouble(investAmount) > purchase.biddingAmount){
            notifyMoney = purchase.biddingAmount;
            notifyDialog.setMessage("持有金额不足，是否修改为:"+Utils.getInstances().format(purchase.biddingAmount));
            notifyDialog.showAs();
            return;
        }
        loadding.showAs();
        TurnOutRequest request = new TurnOutRequest(mContext, new StringListener() {
            @Override
            public void ResponseListener(String result) {
                TurnOutResponse response = new TurnOutResponse();
                loadding.dismiss();
                if(response.getStatus(result) == 200){
                    HomeActivity.totalAssets = response.getTotalAssets(result);
                    toast("转出成功");
                    finish();
                   // HomeActivity.dailyIncreaseBtn.setChecked(true);
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
        }, Cst.currentUser.investorId,ruleType,turnOutEdit.getText().toString());
        request.stringRequest();
    }

    private void calculation(CharSequence s, int start){
        try {
            if(start == 0 && s.toString().trim().length()==0){
                //turnOutEdit.setText("");
                return;
            }
            Double.parseDouble(s.toString());

        } catch (Exception e) {
            if(s.length() > 0){
                turnOutEdit.setText(s.toString().substring(0,s.toString().length()-1));
                Selection.setSelection(turnOutEdit.getText(), turnOutEdit.getText().length());
            }

        }
    }
    @Override
    protected void onDestroy() {
        if(loadding != null && loadding.isShowing()){
            loadding.dismiss();
        }
        if(notifyDialog!=null&&notifyDialog.isShowing()){
            notifyDialog.dismiss();
        }
        if(alertDialog!=null&&alertDialog.isShowing()){
            alertDialog.dismiss();
        }
        super.onDestroy();
    }
}
