package com.yeepbank.android.activity.setting;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;
import com.android.volley.VolleyError;
import com.yeepbank.android.Cst;
import com.yeepbank.android.R;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.request.user.SureTradePasswordRequest;
import com.yeepbank.android.response.user.SureTradePasswordResponse;
import com.yeepbank.android.utils.LoadDialog;
import com.yeepbank.android.widget.gridpasswordview.GridPasswordView;


/**
 * Created by ������ on 2015/10/12.
 * ���������ȷ�ϴ���
 */
public class TradePasswordSureActivity extends BaseActivity implements View.OnClickListener,GridPasswordView.OnPasswordChangedListener {
    private GridPasswordView gridPasswordView;
    private String str;
    private View navigationBar;
    @Override
    protected void initView() {
        gridPasswordView = (GridPasswordView) findViewById(R.id.gv_sure);
        navigationBar=findViewById(R.id.navigation_bar);
        //gridPasswordView.setOnClickListener(this);
        gridPasswordView.setOnPasswordChangedListener(this);
        Intent intent=getIntent();
        if(intent!=null){
            str=intent.getStringExtra("tradePassword");

        }
        toast(str);

    }

    @Override
    protected void fillData() {

    }
    @Override
    protected int getLayoutResources() {
        return R.layout.trade_password_sure;
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


    }
    @Override
    public void onChanged(String psw) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMaxLength(String psw) {
        // TODO Auto-generated method stub
        if(psw.length()==6){
            String surePassword=gridPasswordView.getPassWord() + "";
            if(surePassword.equals(str)){
                sureTradePassword(surePassword);
            }else{
           toast("2次输入密码不一致，请重新输入");
            }
        }

    }
    public void sureTradePassword(String tradePassword) {
        if (Cst.currentUser != null) {
            SureTradePasswordRequest request = new SureTradePasswordRequest(mContext,new StringListener() {
                @Override
                public void ResponseListener(String result) {
                    SureTradePasswordResponse response = new SureTradePasswordResponse();
                    if (response.getStatus(result) == 200) {
                        toast(response.getMessage(result));
                        finish();
                    }else if(response.getStatus(result)==213){
                        toast(response.getMessage(result));
                        finish();
                    }

                }

                @Override
                public void ErrorListener(VolleyError volleyError) {

                }
            },Cst.currentUser.investorId,tradePassword.trim(),tradePassword.trim());
            request.stringRequest();
        }
    }

}
