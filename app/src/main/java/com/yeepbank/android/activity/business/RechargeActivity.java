package com.yeepbank.android.activity.business;

import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.view.animation.*;
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
import com.yeepbank.android.model.user.RechargeResult;
import com.yeepbank.android.request.user.BankListRequest;
import com.yeepbank.android.request.user.RechargeRequest;
import com.yeepbank.android.request.user.RechargeResultRequest;
import com.yeepbank.android.response.user.BankListResponse;
import com.yeepbank.android.response.user.RechargeResponse;
import com.yeepbank.android.response.user.RechargeResultResponse;
import com.yeepbank.android.utils.LoadDialog;
import com.yeepbank.android.utils.Utils;
import com.yeepbank.android.widget.PasswordPanel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by WW on 2015/9/28.
 * 充值
 */
public class RechargeActivity extends BaseActivity implements View.OnClickListener,AdapterView.OnItemClickListener{
    private TextView addBankCardText;
    private LinearLayout rechargeLayout;
    private Button rechargeSureBtn;
    private View navigationBar;
    private EditText rechargeMoneyEdit;//充值金额
    private RelativeLayout showBankLayout;//展示的银行卡
    private boolean showBankListLayout = false;
    private ImageView  directionImg;

    //充值
    private TextView availableBalanceText;//可用余额
    private PopupWindow popupWindow,passwordPanelWindow;

    private ArrayList<BankCard> bankCardArrayList;
    private TextView bankCardText;
    private View bankView = null;//银行卡列表界面
    private  ListView bankList = null;//银行卡列表
    private BankListAdapter bankListAdapter;
    private RechargeResultRequest rechargeResultRequest;
    private int SEARCH_RECHARGE_TIMES = 0;

    private LoadDialog confirmDialog;
    private boolean continueReuqest = true;

    @Override
    protected void initView() {

        navigationBar = findViewById(R.id.navigation_bar);

        addBankCardText = (TextView) findViewById(R.id.add_bank_card);
        addBankCardText.setOnClickListener(this);
        rechargeLayout = (LinearLayout) findViewById(R.id.recharge_layout);

        rechargeMoneyEdit = (EditText) findViewById(R.id.recharge_money);
        rechargeSureBtn = (Button) findViewById(R.id.recharge_sure_btn);
        rechargeMoneyEdit.addTextChangedListener(new TextWatcher() {
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
        //changeButtonStateWithValue(rechargeMoneyEdit, rechargeSureBtn, R.drawable.recharge_sure_icon, R.drawable.recharge_sure_not_activation_icon);
        rechargeSureBtn.setOnClickListener(this);

         bankCardText = (TextView) findViewById(R.id.bank_card_name_text);
        //设置银行卡
        bankListAdapter = new BankListAdapter(new ArrayList<BankCard>(),mContext);
        bankView = LayoutInflater.from(mContext).inflate(R.layout.bank_list,null);
        bankList = (ListView) bankView.findViewById(R.id.bank_list);
        bankList.setOnItemClickListener(this);
        bankList.setAdapter(bankListAdapter);
        //充值页面
        availableBalanceText = (TextView) findViewById(R.id.available_balance_recharge);
        //绑定银行卡页面
        showBankLayout = (RelativeLayout) findViewById(R.id.bank_list_layout);

        //directionImg = (ImageView) findViewById(R.id.direction_img);




    }

    @Override
    protected void fillData() {

        if(Cst.currentUser != null){
            if(HomeActivity.totalAssets != null){
                availableBalanceText.setText(Utils.getInstances().thousandFormat(HomeActivity.totalAssets.freeBalance));
            }

        }
    }

    @Override
    protected void onResume() {
        loadData();
        super.onResume();
    }

    @Override
    protected int getLayoutResources() {
        return R.layout.recharge;
    }

    @Override
    protected View getNavigationBar() {
        //navigationBar.findViewById(R.id.recharge_logo).setVisibility(View.VISIBLE);
        return navigationBar;
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
            case R.id.recharge_sure_btn:
                rechargePre();
                break;

        }
    }

    /*
    * 充值
    * */
    private void rechargePre() {
        String amount = rechargeMoneyEdit.getText().toString();
        if(bankCardText.getTag() == null){
            showErrorMsg(getString(R.string.please_add_bankcard_at_first),navigationBar);
            return;
        }

        String rechargeAmount = rechargeMoneyEdit.getText().toString();
        if(Double.parseDouble(rechargeAmount) == 0){
            toast("充值金额不能为零");
            return;
        }
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        //判断隐藏软键盘是否弹出
        if(imm.isActive(rechargeMoneyEdit)){
            imm.hideSoftInputFromWindow(mContext.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        if(passwordPanelWindow == null){
            passwordPanelWindow = PasswordPanel.getInstances(mContext).createPasswordWindow();
        }
        PasswordPanel.getInstances(mContext).setOnInputCompleted(new PasswordPanel.OnInputCompleted() {
            @Override
            public void onCompleted(String pass) {
                String amount = rechargeMoneyEdit.getText().toString();
                String bankCardNo = ((BankCard) bankCardText.getTag()).bankCardNo;
                recharge(amount, bankCardNo, pass);
            }
        });
        PasswordPanel.getInstances(mContext).show(getContentView());

    }

    private void recharge(String amount,String bankCardNo,String txnpwd){
        loadding.showAs();
        RechargeRequest request = new RechargeRequest(mContext, new StringListener() {
            @Override
            public void ResponseListener(String result) {
                final RechargeResponse response = new RechargeResponse();

                if(response.getStatus(result) == 200){
                    String depositId = response.getDepositId(result);
                    if(depositId == null){
                        loadding.dismiss();
                        return;
                    }
                    rechargeResultRequest = new RechargeResultRequest(mContext, new StringListener() {
                        @Override
                        public void ResponseListener(String result) {
                            RechargeResultResponse rechargeResultResponse = new RechargeResultResponse();
                            if(rechargeResultResponse.getStatus(result) == 200){
                                RechargeResult rechargeResult = rechargeResultResponse.getObject(result);
                               // Log.e("RechargeResult","RechargeResult:"+result);
                                if("W".equals(rechargeResult.status)){
                                    if(SEARCH_RECHARGE_TIMES == 3){
                                        continueReuqest = false;
                                        loadding.dismiss();
                                        toast(getString(R.string.search_recharge_result_unknow));
                                        finish();
                                        return;
                                    }
                                }else if("S".equals(rechargeResult.status)){
                                    continueReuqest = false;
                                    loadding.dismiss();
                                    toast(getString(R.string.search_recharge_result_success));
                                    finish();
                                }else if("F".equals(rechargeResult.status)){
                                    continueReuqest = false;
                                    loadding.dismiss();
                                    if(confirmDialog == null){
                                        confirmDialog = new LoadDialog(mContext, R.style.dialog, false,new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                loadding.dismiss();
                                                confirmDialog.dismiss();
                                                finish();
                                            }
                                        },0).setSureBtn("我知道了");
                                    };
                                    confirmDialog.setMessage(rechargeResultResponse.getFailReason(result));
                                    confirmDialog.show();
                                }
                            }else {
                                if(SEARCH_RECHARGE_TIMES == 3){
                                    continueReuqest = false;
                                    loadding.dismiss();
                                    toast(response.getMessage(result));
                                }
                            }
                        }

                        @Override
                        public void ErrorListener(VolleyError volleyError) {
                            if(SEARCH_RECHARGE_TIMES == 3){
                                continueReuqest = false;
                                loadding.dismiss();
                                showErrorMsg(getString(R.string.net_error),navigationBar);
                            }
                        }
                    },depositId);
                    getRechargeResult();
                }else {
//                    HomeActivity.totalAssets = response.getTotalAssets(result);
                    if(confirmDialog == null){
                        confirmDialog = new LoadDialog(mContext, R.style.dialog, false,new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loadding.dismiss();
                                confirmDialog.dismiss();
                                finish();
                            }
                        },0).setSureBtn("我知道了");
                    };
                    confirmDialog.setMessage(response.getMessage(result));
                    confirmDialog.show();
                }
            }

            @Override
            public void ErrorListener(VolleyError volleyError) {
                dismiss();
                showErrorMsg(getString(R.string.net_error),navigationBar);
            }
        },amount,Cst.currentUser.investorId,bankCardNo,txnpwd);
            request.stringRequest();
    }


    @Override
    public void onBackPressed() {
        navigationBar.findViewById(R.id.recharge_logo).setVisibility(View.GONE);
        super.onBackPressed();
    }

    /*
    * 获取银行卡列表
    * */
    private void loadData(){
        loadding.showAs();
        BankListRequest request = new BankListRequest(mContext,new StringListener(){
            @Override
            public void ResponseListener(String result) {
                dismiss();
                BankListResponse response = new BankListResponse();
                if(response.getStatus(result) == 200){
                    if(bankCardArrayList!= null){
                        bankCardArrayList.clear();
                    }
                    bankCardArrayList = response.getObject(result);
                    setBankList();
                }else {
                    toast(response.getMessage(result));
                }
            }

            @Override
            public void ErrorListener(VolleyError volleyError) {
                dismiss();
                showErrorMsg(getString(R.string.net_error),navigationBar);
            }
        },Cst.currentUser.investorId,"Q");
        request.stringRequest();
    }

    /*
    * 银行卡列表赋值
    * */
    private void setBankList() {
        if(bankCardArrayList != null&&bankCardArrayList.size() > 0){
            bankListAdapter.getData().clear();
            bankListAdapter.getData().addAll(bankCardArrayList);
            bankListAdapter.notifyDataSetChanged();
            if("".equals(Utils.getInstances().getBankCardOfRechargeFromSharedPreference(mContext))){
                bankCardText.setText(bankCardArrayList.get(0).bankInfo);
                bankCardText.setTag(bankCardArrayList.get(0));
                Utils.getInstances().putBankCardOfRechargeToSharedPreference(mContext, bankCardArrayList.get(0).bankCardId);
            }else {
                for (int i = 0; i < bankCardArrayList.size(); i++) {
                    BankCard bankCard = bankCardArrayList.get(i);
                    if(bankCard.bankCardId.equals(Utils.getInstances().getBankCardOfRechargeFromSharedPreference(mContext))){
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
    /*
    * 显示银行卡列表
    * */

    private void showBankList(){
        if(popupWindow == null){

            popupWindow = new PopupWindow(bankView, showBankLayout.getWidth(),ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setAnimationStyle(R.style.bank_list);
            //popupWindow.setFocusable(true);
            popupWindow.setOutsideTouchable(true);
        }
        Animation animation = new RotateAnimation(0,180,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
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
            Utils.getInstances().putBankCardOfRechargeToSharedPreference(mContext, bankListAdapter.getData().get(position).bankCardId);
        }
         showBankListLayout = !showBankListLayout;
         hideBankList();
    }


    private void calculation(CharSequence s, int start){
        try {
            if(start == 0 && s.toString().trim().length()==0){
                rechargeSureBtn.setBackgroundResource(R.drawable.recharge_sure_not_activation_icon);
                rechargeSureBtn.setEnabled(false);
                return;
            }
            double money = Double.parseDouble(s.toString());
            if (s.toString().length() > 0) {
                rechargeSureBtn.setBackgroundResource(R.drawable.recharge_sure_icon);
                rechargeSureBtn.setEnabled(true);
            } else {
                rechargeSureBtn.setBackgroundResource(R.drawable.recharge_sure_not_activation_icon);
                rechargeSureBtn.setEnabled(false);
            }
        } catch (Exception e) {
            if(s.length() > 0){
                rechargeMoneyEdit.setText(s.toString().substring(0,s.toString().length()-1));
                Selection.setSelection(rechargeMoneyEdit.getText(), rechargeMoneyEdit.getText().length());
            }
        }
    }

    private void getRechargeResult(){

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(continueReuqest && SEARCH_RECHARGE_TIMES < 3){
                    rechargeResultRequest.stringRequest();
                    SEARCH_RECHARGE_TIMES++;
                }else {
                    timer.cancel();
                }
            }
        },3000,3000);
//
//        Log.e("REQUEST","rechargeResultRequest-TIMES:"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date()));
//        SEARCH_RECHARGE_TIMES++;
    }


    @Override
    public LoadDialog getLoadDialog() {
        return loadding;
    }
    @Override
    protected void onDestroy() {
        if(loadding != null && loadding.isShowing()){
            loadding.dismiss();
        }
        if(confirmDialog!=null&&confirmDialog.isShowing()){
            confirmDialog.dismiss();
        }
        super.onDestroy();
    }
}
