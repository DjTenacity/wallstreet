package com.yeepbank.android.activity.user;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.*;
import android.widget.TextView;
import android.widget.Toast;
import com.yeepbank.android.Cst;
import com.yeepbank.android.R;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.model.Banner;
import com.yeepbank.android.model.Web;
import com.yeepbank.android.utils.LoadDialog;
import com.yeepbank.android.utils.Utils;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by WW on 2015/12/8.
 * 用于显示用户协议和服务条款
 */
public class WebActivity extends BaseActivity{
    private View navigationBar;
    private WebView webView;
    private String url = null;
    private String title = null;
    private String backBtnText = null;
    private HashMap<String,String> dataMap = null;
    private LoadDialog loadDialog = null;
    @Override

    protected void initView() {
        navigationBar = findViewById(R.id.navigation_bar);
        webView = (WebView) findViewById(R.id.web);
        webView.setWebChromeClient(new WebChromeClient(){
        });
//        //支持javascript
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new Object() {//增加接口方法,让html页面调用

            @JavascriptInterface
            public void postResultCallback(String result) {
//                Toast.makeText(getApplicationContext(), "JS调用Android成功:"+result, Toast.LENGTH_LONG).show();
                try {
                    JSONObject jsonState = new JSONObject(result);
                    String state = jsonState.getString("state");
                    jsonState = new JSONObject(state);
                    if ("200".equals(jsonState.getString("code"))){
                        setResult(0, new Intent().setAction("ANSWER_OK"));
                        finish();
                    }else {
                        loadDialog = new LoadDialog(mContext, R.style.dialog, false, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loadDialog = null;
                                setResult(0, new Intent().setAction("ANSWER_ERROR"));
                                finish();
                            }
                        },0).setMessage("答题提交失败").setSureBtn("确定");
                        loadDialog.show();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                }

            }

            @JavascriptInterface
            public String getInvestorIdFromClient () {
                return dataMap.get("investorId");
            }

        },"client");
//// 设置可以支持缩放
//        webView.getSettings().setSupportZoom(true);
//// 设置出现缩放工具
//        webView.getSettings().setBuiltInZoomControls(true);
////扩大比例的缩放
//        webView.getSettings().setUseWideViewPort(true);
//自适应屏幕
//        webView.getSettings().setLayouotAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//        webView.getSettings().setLoadWithOverviewMode(true);
        Intent intent = getIntent();
        if(intent != null){
            Bundle bundle = intent.getBundleExtra("data");
            Object result = bundle.getSerializable("data");
            if(result instanceof Banner){
                url = ((Banner)result).bannerLink;
                //title = ((Banner)result).bannerTitle;
                title = "易宝金融";
                if(url.trim().equals("")){
                    url = "https://www.yeepbank.com/index.html";
                    //title = "易宝金融";
                }
            }else if(result instanceof Web){
                url = ((Web)result).url;
                title = ((Web)result).title;
                backBtnText = ((Web)result).backBtnText;
                if(url.trim().equals("")){
                    url = "https://www.yeepbank.com/index.html";
                }
                dataMap = ((Web)result).data;
            }

        }
    }

    @Override
    protected void fillData() {
        if(url != null){
            webView.loadUrl(url);
        }
    }

    @Override
    protected int getLayoutResources() {
        return R.layout.web;
    }

    @Override
    protected View getNavigationBar() {
        return navigationBar;
    }

    protected void initNavigationBar(View navigationBar){
        super.initNavigationBar(navigationBar);
        if(backBtnText != null && !"".equals(backBtnText.trim())){
            /*
            * TODO 现在不需要返回标题，需要的时候在这里添加 backBtnText
            * */
            ((TextView)navigationBar.findViewById(R.id.pre_text)).setText("");
        }
        if(title != null){
            ((TextView)navigationBar.findViewById(R.id.label_text)).setText(title);

        }else {
            if(url.equals(Cst.URL.SERVER_DETAIL)){
                ((TextView)navigationBar.findViewById(R.id.label_text)).setText("服务协议");
            }else {
                ((TextView)navigationBar.findViewById(R.id.label_text)).setText("隐私条款");
            }
        }

    }

    @Override
    public LoadDialog getLoadDialog() {
        return null;
    }

    final class PostInterface{
        public PostInterface(){}

        public void post(Object value){
            toast("提交成功");
        }
    }

}
