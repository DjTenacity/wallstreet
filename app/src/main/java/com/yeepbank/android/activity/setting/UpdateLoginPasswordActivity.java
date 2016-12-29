package com.yeepbank.android.activity.setting;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.android.volley.VolleyError;
import com.yeepbank.android.Cst;
import com.yeepbank.android.R;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.request.user.UpdateLoginPasswordRequest;
import com.yeepbank.android.response.user.UpdateLoginPasswordResponse;
import com.yeepbank.android.utils.LoadDialog;

/**
 * Created by xiaogang.dong on 2015/10/14.
 */
public class UpdateLoginPasswordActivity extends BaseActivity implements View.OnClickListener,View.OnFocusChangeListener {
    private EditText oldLoginPassword,xinLoginPassword,querenLoginPassword;
    private Button btn;
    private View navigationBar;
    @Override
    protected void initView() {
        navigationBar = findViewById(R.id.navigation_bar);
        oldLoginPassword= (EditText) findViewById(R.id.old_login_password);
        xinLoginPassword= (EditText) findViewById(R.id.xin_login_password);
        querenLoginPassword= (EditText) findViewById(R.id.queren_login_password);
        btn= (Button) findViewById(R.id.xin_login_btn);
        btn.setOnClickListener(this);
        textChange tc1=new textChange();

        oldLoginPassword.addTextChangedListener(tc1);
        xinLoginPassword.addTextChangedListener(tc1);
        xinLoginPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (xinLoginPassword.getText().toString().trim().length() < 8) {
                        toast("请输入8位以上密码");

                    }
                }
            }
        });
        querenLoginPassword.addTextChangedListener(tc1);
        querenLoginPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if (xinLoginPassword.getText().toString().trim().length() >= 8&&!querenLoginPassword.getText().toString().trim().equals(xinLoginPassword.getText().toString().trim())){
                        toast("两次输入密码不一致");
                    }
                }
            }
        });


    }
    class textChange implements  TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            boolean sign1=oldLoginPassword.getText().toString().trim().length()>0;
            boolean sign2=xinLoginPassword.getText().toString().trim().length()>0;
            boolean sign3=querenLoginPassword.getText().toString().trim().length()>0;
            if(sign1&&sign2&&sign3){
                btn.setBackgroundResource(R.drawable.update_sure);
                btn.setEnabled(true);
            }else{

                btn.setBackgroundResource(R.drawable.update_not_acticice);
                btn.setEnabled(false);
            }
        }
    }


    @Override
    protected void fillData() {

    }

    @Override
    protected int getLayoutResources() {
        return R.layout.update_login_password;
    }

    @Override
    protected View getNavigationBar() {
        return navigationBar;
    }

    @Override
    public LoadDialog getLoadDialog() {
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.xin_login_btn:
                if(xinLoginPassword.getText().toString().trim().length()>=8&&querenLoginPassword.getText().toString().trim().length()>=8){
                    updatePassword();
                }else{
                    toast("请输入8位以上密码");
                }

                break;
        }

    }

    private void updatePassword() {
        if(Cst.currentUser!=null){
            UpdateLoginPasswordRequest request=new UpdateLoginPasswordRequest(mContext, new StringListener(){
                @Override
                public void ResponseListener(String result) {
                    UpdateLoginPasswordResponse response=new UpdateLoginPasswordResponse();
                    if(response.getStatus(result) == 200){
                        toast("修改成功");
                        finish();
                    }else {
                        toast("密码错误:"+response.getMessage(result));
                    }

                }

                @Override
                public void ErrorListener(VolleyError volleyError) {

                }
 },Cst.currentUser.investorId,oldLoginPassword.getText().toString().trim(),xinLoginPassword.getText().toString().trim(),querenLoginPassword.getText().toString().trim());
            request.stringRequest();

        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }
}
