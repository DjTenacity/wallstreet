package com.yeepbank.android.activity.setting;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.android.volley.VolleyError;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.yeepbank.android.Cst;
import com.yeepbank.android.R;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.http.BaseRequest;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.request.user.CouponsRequest;
import com.yeepbank.android.request.user.QRCodeRequest;
import com.yeepbank.android.response.user.CouponsResponse;
import com.yeepbank.android.response.user.QRCodeResponse;
import com.yeepbank.android.server.ShareServer;
import com.yeepbank.android.utils.ApiUtils;
import com.yeepbank.android.utils.LoadDialog;
import com.yeepbank.android.widget.gridpasswordview.Util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by 董晓刚 on 2015/10/15.
 * 推荐码页面
 */
public class RecommendCodeActivity extends BaseActivity implements View.OnClickListener {
    private ImageView myImg,shareImg;
    private Handler myHandler;
    private Bitmap bitmap;
    private View navigationBar;
    private String rmdCode;//推荐码
    private TextView recCodeText;
    private TextView explanTwoText,explanOneText;



    @Override
    protected void initView() {
        navigationBar=findViewById(R.id.navigation_bar);
        myImg=(ImageView)findViewById(R.id.rec_code_img);
        recCodeText = (TextView) findViewById(R.id.rec_code);
        explanOneText = (TextView) findViewById(R.id.explan_one);
        explanTwoText = (TextView) findViewById(R.id.explan_two);
        shareImg = (ImageView) navigationBar.findViewById(R.id.share);
        //shareImg.setVisibility(View.VISIBLE);
        shareImg.setVisibility(View.GONE);
        shareImg.setOnClickListener(this);
        Intent intent = getIntent();
        if(intent!=null){
            Bundle bundle = intent.getBundleExtra("data");
            if(bundle != null){
                rmdCode = (String) bundle.getSerializable("data");
                recCodeText.setText(rmdCode);
            }
        }
        loadDataTwo();
    }

    @Override
    protected void fillData() {
        loadData();
    }

    /*
    * 获取二维码
    * */
    private void loadData() {
        new Thread(){
            @Override
            public void run() {
                try {
                    HashMap<String,String> rawParams = new HashMap<String,String>();
                    rawParams.put("userId", Cst.currentUser.investorId);
                    rawParams.put("securetKey", Cst.currentUser.appSecuretKey);
                    rawParams.put("timestamp", String.valueOf(System.currentTimeMillis()));
                    rawParams.put("version", Cst.getVersionCode(mContext));
                    rawParams.put("client", "Android");
                    String sign = ApiUtils.signTopRequest(rawParams, Cst.SIGN_METHOD_SECRET, Cst.COMMON.SIGN_METHOD_HMAC);
                    rawParams.put("sign", sign);
                    URL url = new URL(Cst.URL.QR_CODE_URL+"?"+ BaseRequest.generateQueryString(rawParams,true));
                    HttpURLConnection conn = null;
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setReadTimeout(5000);

                    InputStream input = conn.getInputStream();
                    byte[] data = readInputStream(input);
                    bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    msgHandler.sendEmptyMessage(0);
                    input.close();


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    /*
    * 此方法无实际意义*/
    private void loadDataTwo() {

        CouponsRequest request = new CouponsRequest(mContext, new StringListener() {
            @Override
            public void ResponseListener(String result) {

                CouponsResponse response = new CouponsResponse();
                if(response.getStatus(result) == 200){

                }else {
                    //toast(response.getMessage(result));
                }
            }

            @Override
            public void ErrorListener(VolleyError volleyError) {

                showErrorMsg(getString(R.string.net_error),navigationBar);
            }
        }, Cst.currentUser.investorId,"");
        request.stringRequest();
    }
    /*
   * 此方法无实际意义*/

     private static byte[] readInputStream(InputStream input) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            int len = 0;
            while((len = input.read(buffer)) != -1) {
                output.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output.toByteArray();
    }

    @Override
    protected int getLayoutResources() {
        return R.layout.recommend_code;
    }

    @Override
    protected View getNavigationBar() {

//        navigationBar.setBackgroundColor(Color.parseColor("#00000000"));
        return navigationBar;
    }

    @Override
    public LoadDialog getLoadDialog() {
        return null;
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.share:
                ShareServer.getInstances(mContext,getIwxapi(),"我理财赚了不少钱，抱我大腿带你上船","点击注册易宝金融，你我各得好礼").show(getContentView());
                break;
        }

    }

    private Handler msgHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    myImg.setImageBitmap(bitmap);
            }
        }
    };


}
