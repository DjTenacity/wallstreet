package com.yeepbank.android.activity.setting;

import android.view.View;

import android.widget.Toast;
import com.yeepbank.android.R;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.utils.LoadDialog;
import com.yeepbank.android.widget.gridpasswordview.GridPasswordView;


/**
 * Created by 董晓刚 on 2015/10/12.
 * 重新设置交易密码框窗体
 */
public class TradePasswordActivity extends BaseActivity implements View.OnClickListener,GridPasswordView.OnPasswordChangedListener {
    private GridPasswordView gridPasswordView;
    private View nanavigationBar;
    @Override
    protected void initView() {
        nanavigationBar=findViewById(R.id.navigation_bar);
        gridPasswordView = (GridPasswordView) findViewById(R.id.gv);
        //gridPasswordView.setOnClickListener(this);
        gridPasswordView.setOnPasswordChangedListener(this);


    }

    @Override
    protected void fillData() {

    }

    @Override
    protected int getLayoutResources() {
        return R.layout.trade_password;
    }

    @Override
    protected View getNavigationBar() {
        return nanavigationBar;
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
            Toast.makeText(this, gridPasswordView.getPassWord() + "",Toast.LENGTH_LONG).show();
        }

    }


}
