package com.yeepbank.android.activity.setting;


import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.yeepbank.android.Cst;
import com.yeepbank.android.R;
import com.yeepbank.android.UpdateService;
import com.yeepbank.android.base.ActivityStacks;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.model.AppInfo;
import com.yeepbank.android.request.user.VersionUpdateRequest;
import com.yeepbank.android.response.user.VersionUpdateResponse;
import com.yeepbank.android.server.AppUpdateServer;
import com.yeepbank.android.utils.LoadDialog;

/**
 * Created by xiaogang.dong on 2015/9/23.
 */
public class AboutYeepActivity extends BaseActivity implements View.OnClickListener,View.OnFocusChangeListener{
    private Button btn;
    private TextView versionMessage;
    private String str,longStr;
    private LoadDialog msgDialog;
    private int cmd;
    private View navigationBar;
    private AppInfo appInfo;
    private AppUpdateServer appUpdateServer;
    @Override
    protected void initView() {
        btn= (Button) findViewById(R.id.about_version_update_btn);
        btn.setOnClickListener(this);
        versionMessage= (TextView) findViewById(R.id.about_version_number);
        str=Cst.getVersionCode(mContext);
        longStr="易宝金融"+str;
        SpannableString sb=new SpannableString(longStr);
        sb.setSpan(new AbsoluteSizeSpan(55), 4,longStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        versionMessage.setText(sb);
        navigationBar = findViewById(R.id.navigation_bar);

    }

    @Override
    protected void fillData() {

    }

    @Override
    protected int getLayoutResources() {
        return R.layout.about_yeepbank;
    }

    @Override
    protected View getNavigationBar() {
        return navigationBar;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.about_version_update_btn:
                getUpdateMessage();
                break;
        }

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }
    public void getUpdateMessage(){
        loadding.showAs();
        Intent intent = new Intent(UpdateService.ACTION);
        intent.putExtra("FROM","EVERY_EXAM");
        startService(intent);

//        VersionUpdateRequest request=new VersionUpdateRequest(mContext, new StringListener() {
//            @Override
//            public void ResponseListener(String result) {
//                loadding.dismiss();
//                VersionUpdateResponse response=new VersionUpdateResponse();
//                if(response.getStatus(result)==200){
//                    cmd = response.getCmd(result);
//                    appInfo=response.getObject(result);
//                    appUpdateServer = AppUpdateServer.getInstances(mContext);
//                    appUpdateServer.setAppInfo(appInfo);
//
//                    switch (cmd){
//
//                        case 1://非强制升级
//                        case 2://强制升级
//
//                            if(msgDialog == null){
//                                createDialog(cmd);
//                            }
//                            msgDialog.setMessage(response.getUpdateMessage(result));
//                            msgDialog.showAs();
//                            break;
//
//                    }
//                }else {
//                    toast("当前已是最新版本");
//                }
//
//            }
//
//            @Override
//            public void ErrorListener(VolleyError volleyError) {
//                loadding.dismiss();
//                showErrorMsg(getString(R.string.net_error),navigationBar);
//            }
//        },str,"Android");
//        request.stringRequest();
    }

    private void createDialog(int cmd){
        String leftBtnText = "取消";
        if(cmd == 2){
            leftBtnText = "退出";
        }
        msgDialog = new LoadDialog(mContext, R.style.dialog, false, new View.OnClickListener() {
            //确定
            @Override
            public void onClick(View v) {
                    /*
                    * 如果是强制更新，则从服务端下载，否则跳转应用市场
                    *
                    * */

                msgDialog.dismiss();
                //Utils.getInstances().cleanSharedPreference(mContext);
                appUpdateServer.onStartCommand(Cst.CMD.DOWNLOAD_APP);
//                     if(cmd == 2){
//                        LoadDialog.dismiss(msgDialog);
//                        AppUpdateServer.getInstances(mContext).onStartCommand(Cst.CMD.DOWNLOAD_APP);
//                    }else {
//                        Intent i = getIntent(mContext);
//                        boolean b = judge(mContext, i);
//                        if(b==false)
//                        {
//                            mContext.startActivity(i);
//                            LoadDialog.dismiss(msgDialog);
//                            ActivityStacks.getInstances().finishActivity();
//                        }else {
//                            LoadDialog.dismiss(msgDialog);
//                        }
//                    }
            }
        }, new View.OnClickListener() {
            //取消
            @Override
            public void onClick(View v) {
                msgDialog.dismiss();
                switch (AboutYeepActivity.this.cmd){
                    case 2:
                        ActivityStacks.getInstances().finishActivity();
                        break;
                }

            }
        },0).setTitle("提示").setSureBtn("去升级").setCancelBtn(leftBtnText).selfGravity(Gravity.LEFT);
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
        if(msgDialog!=null&&msgDialog.isShowing()){
            msgDialog.dismiss();
        }
        super.onDestroy();
    }

}
