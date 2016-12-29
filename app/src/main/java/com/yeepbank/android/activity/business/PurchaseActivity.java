package com.yeepbank.android.activity.business;

import android.graphics.Color;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import com.android.volley.VolleyError;
import com.yeepbank.android.Cst;
import com.yeepbank.android.R;
import com.yeepbank.android.adapter.PurchaseListAdapter;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.model.business.Purchase;
import com.yeepbank.android.request.business.DoBiddingRequest;
import com.yeepbank.android.request.business.PurchaseRequest;
import com.yeepbank.android.response.business.DoBiddingResponse;
import com.yeepbank.android.response.business.PurchaseResponse;
import com.yeepbank.android.utils.LoadDialog;
import com.yeepbank.android.utils.Utils;

import java.util.ArrayList;

/**
 * Created by WW on 2015/10/20.
 * 买入
 */
public class PurchaseActivity extends BaseActivity implements View.OnClickListener{
    private EditText purchaseText;
    private Button purchaseSureBtn;
    private ListView listView;
    private PurchaseListAdapter purchaseListAdapter;
    private ArrayList<Purchase> projectList;
    private Purchase purchase;//买入选项
    private TextView availableAmountText,expectedReturnText;//可用余额,预期收益
    private LoadDialog msgDialog,alertDialog;
    private double notifyMoney;
    private View navigationBar;
    @Override
    protected void initView() {
        projectList = new ArrayList<Purchase>();
        purchaseText = (EditText) findViewById(R.id.purchase_money);
        purchaseSureBtn = (Button) findViewById(R.id.purchase_sure_btn);
        purchaseSureBtn.setOnClickListener(this);
        availableAmountText = (TextView) findViewById(R.id.available_amount);
        availableAmountText.setText("可用余额:"+ Utils.getInstances().thousandFormat(HomeActivity.totalAssets.freeBalance)+"元");
        navigationBar = findViewById(R.id.navigation_bar);
        expectedReturnText = (TextView) findViewById(R.id.expected_return);
        expectedReturnText.setText("0.00");

        listView = (ListView) findViewById(R.id.purchase_list);

        msgDialog = new LoadDialog(mContext, R.style.dialog, false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(notifyMoney >= 0){
                    purchaseText.setText(Utils.getInstances().format(notifyMoney));
                    Selection.setSelection(purchaseText.getText(), purchaseText.getText().length());
                }
                msgDialog.dismiss();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msgDialog.dismiss();
            }
        },0).setTitle("提示");

        alertDialog = new LoadDialog(mContext, R.style.dialog, false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        },0).setTitle("提示").setSureBtn("知道了");

        purchaseText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculation(s,start);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        purchaseListAdapter = new PurchaseListAdapter(projectList,mContext);
        listView.setAdapter(purchaseListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < listView.getChildCount(); i++) {
                    View v = listView.getChildAt(i);
                    v.findViewById(R.id.back_bg).setBackgroundResource(R.drawable.round_bg);
                    ((TextView)v.findViewById(R.id.project_name)).setTextColor(Color.parseColor("#CACACA"));
                    ((TextView)v.findViewById(R.id.rate)).setTextColor(Color.parseColor("#CACACA"));
                    ((TextView)v.findViewById(R.id.unit)).setTextColor(Color.parseColor("#CACACA"));
                    ((TextView)v.findViewById(R.id.explain)).setTextColor(Color.parseColor("#CACACA"));
                    ((TextView)v.findViewById(R.id.surplus_money)).setTextColor(Color.parseColor("#CACACA"));
                    ((TextView)v.findViewById(R.id.surplus_unit)).setTextColor(Color.parseColor("#CACACA"));
                    ((TextView)v.findViewById(R.id.residual_amount)).setTextColor(Color.parseColor("#CACACA"));
                    ((ImageView)v.findViewById(R.id.radio_img)).setImageResource(R.drawable.radio_uncheck_icon);
                }
                view.findViewById(R.id.back_bg).setBackgroundResource(R.drawable.daily_increast_bg_icon);
                ((TextView)view.findViewById(R.id.project_name)).setTextColor(Color.parseColor("#ff39ddff"));
                ((TextView)view.findViewById(R.id.rate)).setTextColor(Color.parseColor("#ff39ddff"));
                ((TextView)view.findViewById(R.id.unit)).setTextColor(Color.parseColor("#ff39ddff"));
                ((TextView)view.findViewById(R.id.explain)).setTextColor(Color.parseColor("#666666"));
                ((TextView)view.findViewById(R.id.surplus_money)).setTextColor(Color.parseColor("#666666"));
                ((TextView)view.findViewById(R.id.surplus_unit)).setTextColor(Color.parseColor("#666666"));
                ((TextView)view.findViewById(R.id.residual_amount)).setTextColor(Color.parseColor("#ffff7b45"));
                ((ImageView)view.findViewById(R.id.radio_img)).setImageResource(R.drawable.radio_checked_icon);
                purchase = purchaseListAdapter.getData().get(position);

                if(purchaseText.getText().toString().length() > 0){
                    double money = Double.parseDouble(purchaseText.getText().toString());
                    expectedReturnText.setText(Utils.getInstances().format(purchase.interestRate * money / 365));
                }
            }
        });
    }

    @Override
    protected void fillData() {
        loadding.showAs();
        PurchaseRequest request = new PurchaseRequest(mContext, new StringListener() {
            @Override
            public void ResponseListener(String result) {
                loadding.dismiss();
                PurchaseResponse response = new PurchaseResponse();
                if(response.getStatus(result) == 200){
                    purchaseListAdapter.getData().clear();
                    purchaseListAdapter.getData().addAll(response.getObject(result));
                    purchaseListAdapter.notifyDataSetChanged();
                    listView.post(new Runnable() {
                        @Override
                        public void run() {
                            View view = listView.getChildAt(0);
                            view.findViewById(R.id.back_bg).setBackgroundResource(R.drawable.daily_increast_bg_icon);
                            ((TextView)view.findViewById(R.id.project_name)).setTextColor(Color.parseColor("#44bad6"));
                            ((TextView)view.findViewById(R.id.rate)).setTextColor(Color.parseColor("#44bad6"));
                            ((TextView)view.findViewById(R.id.unit)).setTextColor(Color.parseColor("#44bad6"));
                            ((TextView)view.findViewById(R.id.explain)).setTextColor(Color.parseColor("#666666"));
                            ((TextView)view.findViewById(R.id.surplus_money)).setTextColor(Color.parseColor("#666666"));
                            ((TextView)view.findViewById(R.id.surplus_unit)).setTextColor(Color.parseColor("#666666"));
                            ((TextView)view.findViewById(R.id.residual_amount)).setTextColor(Color.parseColor("#ffff7b45"));
                            ((ImageView)view.findViewById(R.id.radio_img)).setImageResource(R.drawable.radio_checked_icon);
                            purchase = purchaseListAdapter.getData().get(0);
                        }
                    });

                }else {
                    toast(response.getMessage(result));
                }
            }

            @Override
            public void ErrorListener(VolleyError volleyError) {
                loadding.dismiss();
                showErrorMsg(getString(R.string.net_error),navigationBar);
            }
        });
        request.stringRequest();
    }


    @Override
    protected int getLayoutResources() {
        return R.layout.purchase;
    }

    @Override
    protected View getNavigationBar() {
        return navigationBar;
    }

    @Override
    public LoadDialog getLoadDialog() {
        return loadding;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.purchase_sure_btn:
                buy();
                break;
        }
    }

    private void buy() {

        String investAmount = purchaseText.getText().toString();
        if (Double.parseDouble(investAmount) < 100){
            alertDialog.setMessage("金额不能小于100元起投金额");
            alertDialog.showAs();
            return;
        }
        if (Double.parseDouble(investAmount) % 100 != 0){
            notifyMoney = ((int)Double.parseDouble(investAmount)/100)*100;
            msgDialog.setMessage("投资金额必须是100的整数倍，是否修改为:"+ notifyMoney);
            msgDialog.showAs();
            return;
        }
        if(Double.parseDouble(investAmount) > HomeActivity.totalAssets.freeBalance){
            alertDialog.setMessage("余额不足");
            alertDialog.showAs();
            return;
        }
        if(Double.parseDouble(investAmount) > (purchase.requestAmount - purchase.biddingAmount)){
            alertDialog.setMessage("项目剩余金额小于投资金额");
            alertDialog.showAs();
            return;
        }
        loadding.showAs();
        DoBiddingRequest doBiddingRequest = new DoBiddingRequest(mContext, new StringListener() {
            @Override
            public void ResponseListener(String result) {
                loadding.dismiss();
                DoBiddingResponse response = new DoBiddingResponse();
                if(response.getStatus(result) == 200){
                    HomeActivity.totalAssets = response.getTotalAssets(result);
                    toast("投资成功");
                    finish();
                    //HomeActivity.dailyIncreaseBtn.setChecked(true);
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
        },purchase.projectId,purchaseText.getText().toString(),"0",Cst.currentUser.investorId);
        doBiddingRequest.stringRequest();

    }

    private void calculation(CharSequence s, int start){
        try {
            if(start == 0 && s.toString().trim().length()==0){
                //purchaseText.setText("0.00");
                expectedReturnText.setText("0.00");
                return;
            }
            double money = Double.parseDouble(s.toString());
            expectedReturnText.setText(Utils.getInstances().format(purchase.interestRate * money / 365));
            if (s.toString().length() > 0) {
                purchaseSureBtn.setBackgroundResource(R.drawable.purchase_sure_icon);
                purchaseSureBtn.setEnabled(true);
            } else {
                purchaseSureBtn.setBackgroundResource(R.drawable.purchase_sure_icon_not_activation);
                purchaseSureBtn.setEnabled(false);
            }
        } catch (Exception e) {
            if(s.length() > 0){
                purchaseText.setText(s.toString().substring(0,s.toString().length()-1));
                Selection.setSelection(purchaseText.getText(), purchaseText.getText().length());
            }
        }
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
