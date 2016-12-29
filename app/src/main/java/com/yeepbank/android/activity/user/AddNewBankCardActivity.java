package com.yeepbank.android.activity.user;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.yeepbank.android.Cst;
import com.yeepbank.android.R;
import com.yeepbank.android.activity.business.HomeActivity;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.request.user.BankCardBindConfirmRequest;
import com.yeepbank.android.request.user.BankExamCodeRequest;
import com.yeepbank.android.request.user.CheckBankRequest;
import com.yeepbank.android.response.user.BankCardBindConfirmResponse;
import com.yeepbank.android.response.user.BankExamCodeResponse;
import com.yeepbank.android.response.user.CheckBankResponse;
import com.yeepbank.android.utils.LoadDialog;
import com.yeepbank.android.utils.Utils;

import java.util.ArrayList;

/**
 * Created by WW on 2015/11/18.
 */
public class AddNewBankCardActivity extends BaseActivity implements View.OnClickListener{

    private LinearLayout nextLayout,secondLayout,thirdLayout;
    private TextView investorNameText,idCardText,cardOfBank,sendPhoneText;//投资者姓名,身份证,发卡银行,短信发送手机号
    private ArrayList<LinearLayout> progressLayout = new ArrayList<LinearLayout>();
    private Animation animatorIn,animatorOut;
    private View navigationBar;
    private Button nextBtn,sendExamBtn,bindOkBtn;
    private EditText bankCardNumberText,phoneNumberEdit,msgExamCode;//银行卡号,手机号,短信验证码
    private String requestId;//后台返回的请求号

    @Override
    protected void initView() {
        nextLayout = (LinearLayout) findViewById(R.id.input_bank_card_code_panel);

        secondLayout = (LinearLayout) findViewById(R.id.input_phone_panel);
        thirdLayout = (LinearLayout) findViewById(R.id.input_exam_code_panel);


        progressLayout.clear();
        progressLayout.add(nextLayout);
        hideLayout();
        nextLayout.setVisibility(View.VISIBLE); nextBtn = (Button) findViewById(R.id.next_btn);
        nextBtn.setOnClickListener(this);
        bankCardNumberText = (EditText) nextLayout.findViewById(R.id.bank_card_number_text);
        bankCardNumberText.setText("");
        //bankCardNumberText.setText("6225060511008783698");
        changeButtonStateWithValue(bankCardNumberText, nextBtn, R.drawable.next_btn_icon, R.drawable.next_btn_not_activation_icon);
        investorNameText = (TextView) nextLayout.findViewById(R.id.investor_name);
        idCardText = (TextView) nextLayout.findViewById(R.id.id_card);

        phoneNumberEdit = (EditText) secondLayout.findViewById(R.id.phone_number);
        sendExamBtn = (Button) secondLayout.findViewById(R.id.send_exam_btn);
        changeButtonStateWithValue(phoneNumberEdit,sendExamBtn,R.drawable.send_examcode_icon,R.drawable.send_examcode_not_activationicon);


        sendPhoneText = (TextView) thirdLayout.findViewById(R.id.send_phone);

        navigationBar = findViewById(R.id.navigation_bar);

        sendExamBtn = (Button) findViewById(R.id.send_exam_btn);
        sendExamBtn.setOnClickListener(this);
        bindOkBtn = (Button) findViewById(R.id.bind_ok_btn);

        bindOkBtn.setOnClickListener(this);

        cardOfBank = (TextView) findViewById(R.id.card_of_bank);
        msgExamCode = (EditText) findViewById(R.id.msg_exam_code);
        changeButtonStateWithValue(msgExamCode,bindOkBtn,R.drawable.bind_ok_icon,R.drawable.bind_ok_not_activation_icon);
    }

    @Override
    protected void fillData() {
        if(HomeActivity.totalAssets != null){
            investorNameText.setText(Cst.currentUser.name);
            idCardText.setText(Cst.currentUser.idNoMask);
        }else {
            investorNameText.setText(getString(R.string.no_msg));
            idCardText.setText(getString(R.string.no_msg));
        }
    }

    @Override
    protected int getLayoutResources() {
        return R.layout.add_new_bank_card;
    }

    @Override
    protected View getNavigationBar() {
        return navigationBar;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.next_btn:
                getBankCardName();
                break;
            case R.id.send_exam_btn:
                getExamCode();
                break;
            case R.id.bind_ok_btn:
                bankCardBindOkConfirm();
                break;
        }
    }

    /*
    * 查询银行卡信息
    * */
    private void getBankCardName() {
        loadding.showAs();
        String cardNo = bankCardNumberText.getText().toString().trim();
        CheckBankRequest request = new CheckBankRequest(mContext, new StringListener() {
            @Override
            public void ResponseListener(String result) {
                dismiss();
                CheckBankResponse response = new CheckBankResponse();
                if(response.getStatus(result) == 200){
                    cardOfBank.setText("发卡银行：" + response.getObject(result));
                    showNext(secondLayout, R.anim.activity_in_from_right, R.anim.activity_out_from_left);
                }else {
                    toast(response.getMessage(result));
                }
            }

            @Override
            public void ErrorListener(VolleyError volleyError) {
                dismiss();
                showErrorMsg(getString(R.string.net_error),navigationBar);
            }
        },cardNo);
        request.stringRequest();
    }
    /*
    * 获取验证码
    * */

    private void getExamCode(){
        String cardNo = bankCardNumberText.getText().toString().trim();
        String phone = phoneNumberEdit.getText().toString().trim();
        String regExp = "^[1][0-9]{10}$";
        if(!phone.matches(regExp)){
            showErrorMsg("手机号格式有误",navigationBar);
            return;
        }
        loadding.showAs();
        BankExamCodeRequest request = new BankExamCodeRequest(mContext, new StringListener() {
            @Override
            public void ResponseListener(String result) {
                BankExamCodeResponse response = new BankExamCodeResponse();
                dismiss();
                if(response.getStatus(result) == 200){
                    requestId = response.getObject(result);
                    if(requestId == null || requestId.trim().equals("")){
                        showErrorMsg("请求吗获取失败",navigationBar);
                        return;
                    }
                    showNext(thirdLayout,R.anim.activity_in_from_right,R.anim.activity_out_from_left);
                }else {
                    toast(response.getMessage(result));
                }
            }

            @Override
            public void ErrorListener(VolleyError volleyError) {
                dismiss();
                showErrorMsg(getString(R.string.net_error),navigationBar);
            }
        },cardNo,phone,Cst.currentUser.investorId);
        request.stringRequest();
    }

    /*
    * 绑定完成提交
    * */

    private void bankCardBindOkConfirm(){
        String validateCode = msgExamCode.getText().toString().trim();
        String cardNo = bankCardNumberText.getText().toString().trim();
        if(validateCode.length() == 0){
            showErrorMsg("短信验证码不能为空",navigationBar);
            return;
        }
        loadding.showAs();
        BankCardBindConfirmRequest request = new BankCardBindConfirmRequest(mContext, new StringListener() {
            @Override
            public void ResponseListener(String result) {
                dismiss();
                BankCardBindConfirmResponse response = new BankCardBindConfirmResponse();
                if(response.getStatus(result) == 200){
                    finish();
                    toast("绑定成功");
                }else {
                    toast(response.getMessage(result));
                }
            }

            @Override
            public void ErrorListener(VolleyError volleyError) {
                dismiss();
                showErrorMsg(getString(R.string.net_error),navigationBar);
            }
        },Cst.currentUser.investorId,requestId,validateCode,cardNo);
        request.stringRequest();
    }

    protected void backBtnTap(){
        if(progressLayout.size()  <= 1){
            super.backBtnTap();
        }else {
            LinearLayout currentLayout = progressLayout.get(0);
            animatorOut = AnimationUtils.loadAnimation(mContext,R.anim.activity_out_from_right);
            currentLayout.startAnimation(animatorOut);
            progressLayout.remove(0);
            showNext(progressLayout.get(0),R.anim.activity_in_from_left,R.anim.activity_out_from_right);
        }

    }

    @Override
    public LoadDialog getLoadDialog() {
        return loadding;
    }

    private void showNext(LinearLayout layout,int in,int out) {
        hideLayout();
        if(progressLayout.size() >= 1){
            LinearLayout currentLayout = progressLayout.get(0);
            animatorOut = AnimationUtils.loadAnimation(mContext, out);
            currentLayout.startAnimation(animatorOut);
        }
        if(progressLayout.contains(layout)){
            progressLayout.remove(layout);
        }
        progressLayout.add(0, layout);
        setTitle((String) layout.getTag());
        animatorIn = AnimationUtils.loadAnimation(mContext,in);
        layout.startAnimation(animatorIn);
        layout.setVisibility(View.VISIBLE);
        sendPhoneText.setText(phoneNumberEdit.getText().toString());
    }

    private void hideLayout(){
        nextLayout.setVisibility(View.GONE);
        secondLayout.setVisibility(View.GONE);
        thirdLayout.setVisibility(View.GONE);
    }
    @Override
    protected void onDestroy() {
        if(loadding != null && loadding.isShowing()){
            loadding.dismiss();
        }
        super.onDestroy();
    }
}
