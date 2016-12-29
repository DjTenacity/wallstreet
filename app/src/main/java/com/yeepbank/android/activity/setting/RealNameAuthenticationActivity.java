package com.yeepbank.android.activity.setting;

import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.yeepbank.android.Cst;
import com.yeepbank.android.R;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.request.user.RealNameRequest;
import com.yeepbank.android.response.user.RealNameResponse;
import com.yeepbank.android.utils.LoadDialog;
import com.yeepbank.android.utils.Utils;

/**
 * Created by Administrator on 2015/10/12.
 */
public class RealNameAuthenticationActivity extends BaseActivity implements View.OnClickListener {
    private EditText realName,realIdCard;//真实姓名，真实身份证号
    private Button realBtn;
    private View navigationBar;
    @Override
    protected void initView() {
        navigationBar=findViewById(R.id.navigation_bar);
        realName = (EditText) findViewById(R.id.real_name);
        realIdCard = (EditText) findViewById(R.id.real_id_card);
        realBtn = (Button) findViewById(R.id.real_btn);
        changeButtonStateWithValue(realName,realBtn,R.drawable.realname_btn_pressed_bg,R.drawable.realname_btn_bg);
        realBtn.setOnClickListener(this);
    }

    @Override
    protected void fillData() {

    }

    @Override
    protected int getLayoutResources() {
        return R.layout.realname_authentication;
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
            case R.id.real_btn:
                goRealName();
                break;
        }
    }

    private void goRealName() {
        String name = realName.getText().toString().trim();
        String idNo = realIdCard.getText().toString().trim();
        RealNameRequest request = new RealNameRequest(mContext, new StringListener() {
            @Override
            public void ResponseListener(String result) {
                RealNameResponse response = new RealNameResponse();
                if(response.getStatus(result) == 200){
                    Cst.currentUser.idAuthFlag = "P";
                    Utils.getInstances().putInvestorToSharedPreference(mContext, Cst.currentUser);
                    finish();
                }else {
                    toast("认证失败");
                }
            }

            @Override
            public void ErrorListener(VolleyError volleyError) {
                showErrorMsg(getString(R.string.net_error),null);
            }
        }, Cst.currentUser.investorId,name,idNo);
        request.stringRequest();
    }

}
