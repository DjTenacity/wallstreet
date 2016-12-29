package com.yeepbank.android.activity.setting;

import android.app.ActivityManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.android.volley.VolleyError;
import com.yeepbank.android.Cst;
import com.yeepbank.android.R;
import com.yeepbank.android.base.ActivityStacks;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.request.user.ExchangeRequest;
import com.yeepbank.android.response.user.ExchangeResponse;
import com.yeepbank.android.utils.LoadDialog;

import java.util.List;

/**
 * Created by 董晓刚 on 2015/10/13.
 * 兑换投资券
 */
public class ExchangeInvestmentActivity extends BaseActivity implements View.OnClickListener{
    private EditText discountCodeText;//优惠码
    private Button exchangeBtn;//兑换按钮
    private View navigationBar;
    @Override
    protected void initView() {
        discountCodeText = (EditText) findViewById(R.id.discount_code);
        exchangeBtn = (Button) findViewById(R.id.exchange_btn);
        navigationBar = findViewById(R.id.navigation_bar);
        exchangeBtn.setOnClickListener(this);
        changeButtonStateWithValue(discountCodeText,exchangeBtn,R.drawable.exchange_btn,R.drawable.exchange_not_activated);
    }

    @Override
    protected void fillData() {

    }

    @Override
    protected int getLayoutResources() {
        return R.layout.excharge_investment;
    }

    @Override
    protected View getNavigationBar() {
        //ImageView informationImg = (ImageView) navigationBar.findViewById(R.id.information);
        //informationImg.setVisibility(View.VISIBLE);
        return navigationBar;
    }

    @Override
    public LoadDialog getLoadDialog() {
        return loadding;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.exchange_btn:
                exchange();
                break;
        }
    }

    private void exchange() {
        String discountCode = discountCodeText.getText().toString().trim();
        loadding.showAs();
        ExchangeRequest request = new ExchangeRequest(mContext, new StringListener() {
            @Override
            public void ResponseListener(String result) {
                dismiss();
                ExchangeResponse response = new ExchangeResponse();
                if(response.getStatus(result) == 200){
                    setResult(1,null);
                    finish();
                }else {
                    toast("兑换码错误，请重新输入");
                }
            }

            @Override
            public void ErrorListener(VolleyError volleyError) {
                dismiss();
                showErrorMsg(getString(R.string.net_error),null);
            }
        }, Cst.currentUser.investorId,discountCode);
        request.stringRequest();
    }
    @Override
    protected void onDestroy() {
        if(loadding != null && loadding.isShowing()){
            loadding.dismiss();
        }
        super.onDestroy();
    }
}
