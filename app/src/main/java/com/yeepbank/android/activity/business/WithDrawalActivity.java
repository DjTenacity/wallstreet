package com.yeepbank.android.activity.business;


import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.android.volley.VolleyError;
import com.yeepbank.android.Cst;
import com.yeepbank.android.R;
import com.yeepbank.android.activity.user.AddNewBankCardActivity;
import com.yeepbank.android.adapter.BankListAdapter;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.model.user.BankCard;
import com.yeepbank.android.request.user.BankListRequest;
import com.yeepbank.android.request.user.WithdrawalsRequest;
import com.yeepbank.android.response.user.BankListResponse;
import com.yeepbank.android.response.user.WithdrawalsResponse;
import com.yeepbank.android.utils.LoadDialog;
import com.yeepbank.android.utils.Utils;
import com.yeepbank.android.widget.PasswordPanel;

import java.util.ArrayList;

/**
 * Created by WW on 2015/9/28.
 * ww
 * 提现
 */
public class WithDrawalActivity extends BaseActivity implements View.OnClickListener,AdapterView.OnItemClickListener{
    private LinearLayout addBankCardLayout;
    private LinearLayout withdrawalsLayout;


    private View navigationBar;
    private TextView availableBalanceText;EditText withdrawalsMoneyText;//可用余额,提取金额

    private ArrayList<BankCard> bankCardArrayList;
    private boolean showBankListLayout = false;
    private ImageView  directionImg;
    private TextView bankCardText;
    private RelativeLayout showBankLayout;//展示的银行卡
    private View bankView = null;//银行卡列表界面
    private  ListView bankList = null;//银行卡列表
    private BankListAdapter bankListAdapter;
    private PopupWindow popupWindow,passwordPanelWindow;//银行卡下拉列表，密码框;
    private Button withdrawalsSureBtn;

    @Override
    protected void initView() {

        navigationBar = findViewById(R.id.navigation_bar);

        addBankCardLayout = (LinearLayout) findViewById(R.id.add_bank_card);
        addBankCardLayout.setOnClickListener(this);
        availableBalanceText = (TextView) findViewById(R.id.available_balance);
        withdrawalsMoneyText = (EditText) findViewById(R.id.withdrawals_money);
        withdrawalsMoneyText.requestFocus();


        //directionImg = (ImageView) findViewById(R.id.direction_img);
        showBankLayout = (RelativeLayout) findViewById(R.id.bank_list_layout);

        //设置银行卡
        bankCardText = (TextView) findViewById(R.id.bank_card_name_text);
        bankListAdapter = new BankListAdapter(new ArrayList<BankCard>(),mContext);
        bankView = LayoutInflater.from(mContext).inflate(R.layout.bank_list,null);
        bankList = (ListView) bankView.findViewById(R.id.bank_list);
        bankList.setOnItemClickListener(this);
        bankList.setAdapter(bankListAdapter);
        withdrawalsMoneyText.addTextChangedListener(new TextWatcher() {
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
        withdrawalsSureBtn = (Button) findViewById(R.id.withdrawals_sure_btn);
        //changeButtonStateWithValue(withdrawalsMoneyText,withdrawalsSureBtn,R.drawable.withdrawals_sure,R.drawable.withdrawals_sure_not_activation);
        withdrawalsSureBtn.setOnClickListener(this);
    }

    @Override
    protected void fillData() {
        if(HomeActivity.totalAssets != null){
            availableBalanceText.setText(Utils.getInstances().thousandFormat(HomeActivity.totalAssets.freeBalance)+"元");
        }else {
            availableBalanceText.setText(Utils.getInstances().thousandFormat(0)+"元");
        }
    }

    @Override
    protected void onResume() {
        loadData();
        super.onResume();
    }

    @Override
    protected int getLayoutResources() {
        return R.layout.withdrawals;
    }

    @Override
    protected View getNavigationBar() {
        //navigationBar.findViewById(R.id.withdrawals_logo).setVisibility(View.VISIBLE);
        return navigationBar;
    }

    @Override
    public LoadDialog getLoadDialog() {
        return loadding;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_bank_card:
                gotoTarget(AddNewBankCardActivity.class, R.anim.activity_in_from_right,
                        R.anim.activity_out_from_left, "");
                break;

            case R.id.bank_list_layout:
                showBankListLayout = !showBankListLayout;
                //directionImg.clearAnimation();
                if(showBankListLayout){
                    showBankList();
                }else {
                    hideBankList();
                }
                break;
            case R.id.withdrawals_sure_btn:
                withdrawalsPre();
                break;
        }
    }

    private void withdrawalsPre() {
        if(bankCardText.getTag() == null){
            showErrorMsg(getString(R.string.please_add_bankcard_at_first),navigationBar);
            return;
        }
        String withdrawAmount = withdrawalsMoneyText.getText().toString();
        if(Double.parseDouble(withdrawAmount) == 0){
            toast("提现金额不能为零");
            return;
        }
        if(Double.parseDouble(withdrawAmount) > HomeActivity.totalAssets.freeBalance){
            toast("已超过提现限额");
            return;
        }

        if(withdrawAmount.contains(".")){
            if(withdrawAmount.substring(withdrawAmount.lastIndexOf(".")+1,withdrawAmount.length()).length() > 2){
                toast("金额最多到小数后两位,请输入正确的金额");
                return;
            };

        }
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        //判断隐藏软键盘是否弹出
        if(imm.isActive(withdrawalsMoneyText)){
            imm.hideSoftInputFromWindow(mContext.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        if(passwordPanelWindow == null){
            passwordPanelWindow =  PasswordPanel.getInstances(mContext).createPasswordWindow();
        }
        PasswordPanel.getInstances(mContext).setOnInputCompleted(new PasswordPanel.OnInputCompleted() {
            @Override
            public void onCompleted(String pass) {
                withdrawals(pass);
            }
        });
        PasswordPanel.getInstances(mContext).show(getContentView());



    }

    private void withdrawals(String password) {

        String withdrawAmount = withdrawalsMoneyText.getText().toString();
        String bankCardId = ((BankCard)bankCardText.getTag()).bankCardId;
        String bankCardNo = ((BankCard)bankCardText.getTag()).bankCardNo;

        if(password.toString().trim().equals("")){
            toast("密码不能为空");
            return;
        }

        loadding.showAs();

        WithdrawalsRequest request = new WithdrawalsRequest(mContext, new StringListener() {
            @Override
            public void ResponseListener(String result) {
                loadding.dismiss();
                WithdrawalsResponse response = new WithdrawalsResponse();
                if(response.getStatus(result) == 200){
                    HomeActivity.totalAssets = response.getTotalAssets(result);
                    toast("提现申请成功，将于下个工作日完成审核并出款，请留意银行到账提醒");
                    finish();
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
        },withdrawAmount,bankCardId,bankCardNo,password,Cst.currentUser.investorId);
        request.stringRequest();
    }


    @Override
    public void onBackPressed() {
        navigationBar.findViewById(R.id.withdrawals_logo).setVisibility(View.GONE);
        super.onBackPressed();
    }

    private void loadData(){
        loadding.showAs();
        BankListRequest request = new BankListRequest(mContext,new StringListener(){
            @Override
            public void ResponseListener(String result) {
                dismiss();
                BankListResponse response = new BankListResponse();
                if(response.getStatus(result) == 200){
                    bankCardArrayList = response.getObject(result);
                    setBankList();
                }else {
                    toast(response.getMessage(result));
                }
            }

            @Override
            public void ErrorListener(VolleyError volleyError) {
                dismiss();
                showErrorMsg(getString(R.string.net_error),null);
            }
        },Cst.currentUser.investorId,"W,Q");
        request.stringRequest();
    }

     /*
    * 显示银行卡列表
    * */

    private void showBankList(){
        if(popupWindow == null){
            popupWindow = new PopupWindow(bankView, showBankLayout.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setAnimationStyle(R.style.bank_list);
            //popupWindow.setFocusable(true);
            popupWindow.setOutsideTouchable(true);
        }
        Animation animation = new RotateAnimation(0,180,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(200);
        animation.setFillAfter(true);
        //directionImg.startAnimation(animation);
        popupWindow.showAsDropDown(showBankLayout);
    }

    /*
    * 隐藏银行卡列表
    * */

    private void hideBankList(){
        if(popupWindow == null){

            popupWindow = new PopupWindow(bankView, showBankLayout.getWidth(),ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setAnimationStyle(R.style.bank_list);
            popupWindow.setOutsideTouchable(true);
        }
        Animation animation = new RotateAnimation(180,0,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        animation.setDuration(200);
        animation.setFillAfter(true);
        //directionImg.startAnimation(animation);
        popupWindow.dismiss();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(bankListAdapter.getData().size() > 0){
            bankCardText.setText(bankListAdapter.getData().get(position).bankInfo);
            bankCardText.setTag(bankListAdapter.getData().get(position));
            Utils.getInstances().putBankCardOfWithDrawalToSharedPreference(mContext, bankListAdapter.getData().get(position).bankCardId);
        }
        showBankListLayout = !showBankListLayout;
        hideBankList();
    }

    /*
* 银行卡列表赋值
* */
    private void setBankList() {
        if(bankCardArrayList != null && bankCardArrayList.size() > 0){
            bankListAdapter.getData().clear();
            bankListAdapter.getData().addAll(bankCardArrayList);
            bankListAdapter.notifyDataSetChanged();

            if("".equals(Utils.getInstances().getBankCardOfWithDrawalFromSharedPreference(mContext))){
                bankCardText.setText(bankCardArrayList.get(0).bankInfo);
                bankCardText.setTag(bankCardArrayList.get(0));
                Utils.getInstances().putBankCardOfWithDrawalToSharedPreference(mContext, bankCardArrayList.get(0).bankCardId);
            }else {
                for (int i = 0; i < bankCardArrayList.size(); i++) {
                    BankCard bankCard = bankCardArrayList.get(i);
                    if(bankCard.bankCardId.equals(Utils.getInstances().getBankCardOfWithDrawalFromSharedPreference(mContext))){
                        bankCardText.setText(bankCard.bankInfo);
                        bankCardText.setTag(bankCard);
                        break;
                    }
                    if(i == bankCardArrayList.size() - 1){
                        bankCardText.setText(bankCardArrayList.get(0).bankInfo);
                        bankCardText.setTag(bankCardArrayList.get(0));
                        Utils.getInstances().putBankCardOfRechargeToSharedPreference(mContext, bankCardArrayList.get(0).bankCardId);
                    }
                }
            }

            showBankLayout.setOnClickListener(this);
        }else {
            showBankLayout.setOnClickListener(null);
        }
    }


    private void calculation(CharSequence s, int start){
        try {
            if(start == 0 && s.toString().trim().length()==0){
                withdrawalsSureBtn.setBackgroundResource(R.drawable.withdrawals_sure_not_activation);
                withdrawalsSureBtn.setEnabled(false);
                return;
            }
            double money = Double.parseDouble(s.toString());
            if (s.toString().length() > 0) {

                withdrawalsSureBtn.setBackgroundResource(R.drawable.withdrawals_sure);
                withdrawalsSureBtn.setEnabled(true);
            } else {
                withdrawalsSureBtn.setBackgroundResource(R.drawable.withdrawals_sure_not_activation);
                withdrawalsSureBtn.setEnabled(false);
            }
        } catch (Exception e) {
            if(s.length() > 0){
                withdrawalsMoneyText.setText(s.toString().substring(0,s.toString().length()-1));
                Selection.setSelection(withdrawalsMoneyText.getText(), withdrawalsMoneyText.getText().length());
            }
        }
    }
    @Override
    protected void onDestroy() {
        if(loadding != null && loadding.isShowing()){
            loadding.dismiss();
        }
        super.onDestroy();
    }

}
