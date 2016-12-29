
package com.yeepbank.android.activity.setting;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import com.android.volley.VolleyError;
import com.yeepbank.android.Cst;
import com.yeepbank.android.R;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.request.user.AdviceFeedbackRequest;
import com.yeepbank.android.response.user.AdviceFeedbackResponse;
import com.yeepbank.android.utils.LoadDialog;

/**

 * Created by 董晓刚 on 2015/9/23.
 * 意见反馈

 */
public class AdviceFeedbackActivity extends BaseActivity implements View.OnClickListener,View.OnFocusChangeListener{
    private EditText adviceContext,adviceTelePhoneNumber;
    private Button adviceBtn;
    private View navigationBar;
    @Override
    protected void initView() {
        navigationBar = findViewById(R.id.navigation_bar);
        adviceContext= (EditText) findViewById(R.id.advice_context);
        adviceTelePhoneNumber= (EditText) findViewById(R.id.advice_telePhoneNumber);
        adviceBtn= (Button) findViewById(R.id.advice_btn);
        adviceBtn.setOnClickListener(this);
        if(Cst.currentUser!=null){
            adviceTelePhoneNumber.setVisibility(View.GONE);
        }


    }

    @Override
    protected void fillData() {

    }

    @Override
    protected int getLayoutResources() {
        return R.layout.advice_feedback;
    }

    @Override
    protected View getNavigationBar() {
        return  navigationBar;
    }

    @Override
    public LoadDialog getLoadDialog() {
        return null;
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.advice_btn:
                if(adviceContext.getText().length()>0){
                    upload();
                }else{

                    toast("内容不能为空");

                }

                break;

        }

    }

    private void upload() {
        String param1;
        if(Cst.currentUser==null){
            param1="";
        }else{
            param1=Cst.currentUser.investorId;
        }


            AdviceFeedbackRequest request = new AdviceFeedbackRequest(mContext,new StringListener() {

                @Override
                public void ResponseListener(String result) {
                    AdviceFeedbackResponse response=new AdviceFeedbackResponse();
                    if(response.getStatus(result) == 200){
                        toast("提交成功");
                     finish();
                    }else {
                        toast(response.getMessage(result));
                    }

                }

                @Override
                public void ErrorListener(VolleyError volleyError) {
                    showErrorMsg(getString(R.string.net_error),null);
                }
            },param1,adviceTelePhoneNumber.getText().toString().trim(),adviceContext.getText().toString().trim());
            request.stringRequest();

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }
}

