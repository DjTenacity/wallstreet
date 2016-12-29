package com.yeepbank.android;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import com.android.volley.VolleyError;
import com.lib.signkey.SignKeys;
import com.yeepbank.android.activity.business.HomeActivity;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.model.user.Investor;
import com.yeepbank.android.request.user.ResetSecuretKeyRequest;
import com.yeepbank.android.response.user.LoginAndRegisterResponse;
import com.yeepbank.android.utils.LoadDialog;
import com.yeepbank.android.utils.Utils;
import org.java_websocket.client.WebSocketClient;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by WW on 2015/9/11.
 */
public class LaunchActivity extends BaseActivity {
    private Investor investor;


    @Override
    protected void initView() {

    }

    @Override
    protected void fillData() {

        /*重置*/
        if (Cst.currentUser != null){
            ResetSecuretKeyRequest request = new ResetSecuretKeyRequest(mContext, new StringListener() {
                @Override
                public void ResponseListener(String result) {

                    LoginAndRegisterResponse response = new LoginAndRegisterResponse();
                    if(response.getStatus(result) == 200){
                        investor=response.getObject(result);
                        Log.e("重置KEY","重置KEY时间："+new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date()));
                        Cst.currentUser=investor;
                        Utils.getInstances().putInvestorToSharedPreference(mContext, Cst.currentUser);
                        Utils.getInstances().updatePushState(mContext);

                        Intent intent = new Intent(WebSocketService.ACTION);
                        intent.putExtra("cid", Cst.PUSH_CLIENT_ID);
                        sendBroadcast(intent);
                    }else{
                        toast(response.getMessage(result));
                        Investor investor = response.getObject(result);
                        if(investor != null){
                            Cst.currentUser = investor;
                            Utils.getInstances().putInvestorToSharedPreference(mContext, Cst.currentUser);
                            Utils.getInstances().updatePushState(mContext);
                        }

                    }
                }

                @Override
                public void ErrorListener(VolleyError volleyError) {
                }
            },Cst.currentUser.investorId);
            request.stringRequest();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Utils.getInstances().isUsed(mContext)) {
                    gotoTargetRemovePre(HomeActivity.class, R.anim.activity_in_from_left, R.anim.activity_out_from_right, "");

                } else {
                    gotoTargetRemovePre(HomeActivity.class, R.anim.activity_in_from_left, R.anim.activity_out_from_right, "");
                    Utils.getInstances().putFirstInfoToPreference(mContext,"is_first",true);
                }
            }
        }, 500);

    }

    @Override
    protected int getLayoutResources() {
        return R.layout.launch_page;
    }

    @Override
    protected View getNavigationBar() {
        return null;
    }

    @Override
    protected void initNavigationBar(View navigationBar) {

    }

    @Override
    public LoadDialog getLoadDialog() {
        return null;
    }

}
