package com.yeepbank.android.activity.account;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import cn.com.kuaishua.sdk.activity.PayActivity;
import cn.com.kuaishua.sdk.entity.PayInfo;
import com.android.volley.VolleyError;
import com.kuaishua.tools.encrypt.StringUtil;
import com.yeepbank.android.BuildConfig;
import com.yeepbank.android.Cst;
import com.yeepbank.android.R;
import com.yeepbank.android.activity.business.HomeActivity;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.model.order.PayOrder;
import com.yeepbank.android.request.user.OrderParamsRequest;
import com.yeepbank.android.response.user.OrderParamsResponse;
import com.yeepbank.android.utils.DesUtil;
import com.yeepbank.android.utils.LoadDialog;
import com.yeepbank.android.utils.Utils;
import com.yeepbank.android.widget.PasswordPanel;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by WW on 2016/4/14.
 * 刷卡充值
 */
public class PayByCardForRechargeActivity extends BaseActivity implements View.OnClickListener{

    private View navigationBar;
    private LocationManager locationManager;
    private TextView availableBalanceText;//可用余额
    private EditText rechargeMoneyEdit;//充值金额
    private ImageButton linkPosBtn;
    private PopupWindow passwordPanelWindow;
    private String comId,orderNo;//商户ID,商户订单号
    public static PayOrder payOrder = null;
    private LoadDialog alertDialog;
    private LoadDialog confirmDialog;
    private List<String> providers;
    @Override
    protected void initView() {
        navigationBar = findViewById(R.id.navigation_bar);
        //充值页面
        availableBalanceText = (TextView) findViewById(R.id.available_balance_recharge);
        rechargeMoneyEdit = (EditText) findViewById(R.id.recharge_money);
        linkPosBtn = (ImageButton) findViewById(R.id.link_pos_btn);
        linkPosBtn.setEnabled(false);
        linkPosBtn.setOnClickListener(this);
    }

    @Override
    protected void fillData() {
        if(HomeActivity.totalAssets != null){
            availableBalanceText.setText(Utils.getInstances().thousandFormat(HomeActivity.totalAssets.freeBalance));
        }
        rechargeMoneyEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (rechargeMoneyEdit.getText().toString().trim().length() > 0 && !linkPosBtn.isEnabled()) {
                    linkPosBtn.setEnabled(true);
                } else if (rechargeMoneyEdit.getText().toString().trim().length() == 0 && linkPosBtn.isEnabled()) {
                    linkPosBtn.setEnabled(false);
                }
            }
        });
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    protected int getLayoutResources() {
        return R.layout.pay_by_card_for_recharge;
    }

    @Override
    protected View getNavigationBar() {
        return navigationBar;
    }

    @Override
    public LoadDialog getLoadDialog() {
        return loadding;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.link_pos_btn:
                invokeKsPayBateSdk();
                break;
        }
    }

    private void invokeKsPayBateSdk() {
        getOrderInfo();
        //check();

    }

    /*
    * 获取订单信息
    * */

    private void getOrderInfo(){
        try{
            if (Double.parseDouble(rechargeMoneyEdit.getText().toString()) < 10){
                toast("单笔充值最低为10元");
                return;
            }
        }catch (Exception e){
            toast("请输入正确的金额");
            return;
        }
        loadding.showAs();
        OrderParamsRequest request = new OrderParamsRequest(mContext, new StringListener() {
            @Override
            public void ResponseListener(String result) {
                loadding.dismiss();
                OrderParamsResponse response = new OrderParamsResponse();
                if (response.getStatus(result) == 200){
                    Log.e("result","result:"+result);
                    payOrder = response.getObject(result);
                    if (payOrder != null)
                        link();
                    else
                        toast("获取订单信息失败");
                }else {
                    toast(response.getMessage(result));
                }
            }

            @Override
            public void ErrorListener(VolleyError volleyError) {
                loadding.dismiss();
                showErrorMsg(getString(R.string.net_error), navigationBar);
            }
        }, Cst.currentUser.investorId,rechargeMoneyEdit.getText().toString());
        request.stringRequest();
    }

    /*
    * 链接刷卡器
    * */
    private void link() {

        confirmDialog = new LoadDialog(mContext, R.style.dialog, false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog.dismiss();
                getLocation();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog.dismiss();
            }
        },0).setMessage("为了刷卡安全，需要获取您的地理位置信息").setSureBtn("确定").setCancelBtn("取消");
        //获取所有可用的位置提供器
        confirmDialog.show();

    }

    private void getLocation() {

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            Location location = null;
            providers = locationManager.getProviders(true);
            for (int i = 0; i < providers.size(); i++) {
                location = locationManager.getLastKnownLocation(providers.get(i));
                if (location != null){
                    break;
                }
            }

            //Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location!=null){
//                boolean has_permission = (PackageManager.PERMISSION_GRANTED
//                        == getPackageManager().checkPermission("android.permission.ACCESS_FINE_LOCATION", "com.yeepbank.android"));
//                if (has_permission){
//                    toast("有权限");
//                }else {
//                    toast("没有权限");
//                    return;
//                }

                String locationStr = location.getLatitude()+";"+location.getLongitude();


                if ("0.0;0.0".equals(locationStr)){
                    createAlertDialog("system");
                    alertDialog.showAs();
                    return;
                }else if ("1.0;1.0".equals(locationStr)){
                    createAlertDialog("unknow");
                    alertDialog.showAs();
                    return;
                }else if("".equals(locationStr)){
                    toast("获取地理位置信息失败");
                    return;
                }
                Log.e("TAG", "定位提供者："+location.getProvider());
                gotoKsSDK(locationStr);
            }else {


                    toast("获取地理位置信息失败,请检查定位权限是否开启");
//                boolean gpsEnabled = Settings.Secure.isLocationProviderEnabled( getContentResolver(), LocationManager.GPS_PROVIDER );
//                if(!gpsEnabled)
//                {
////                    Settings.Secure.setLocationProviderEnabled( getContentResolver(), LocationManager.GPS_PROVIDER, false );
////                } else {
//                    //打开GPS
//                    Settings.Secure.setLocationProviderEnabled( getContentResolver(), LocationManager.GPS_PROVIDER, true);
//                    loadding.setWaitDialogWords("使用GPS定位中");
//                    loadding.showAs();
//                }
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
//                    // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
//                    @Override
//                    public void onStatusChanged(String provider, int status, Bundle extras) {
//
//                    }
//
//                    // Provider被enable时触发此函数，比如GPS被打开
//                    public void onProviderEnabled(String provider) {
//
//                    }
//                    // Provider被disable时触发此函数，比如GPS被关闭
//
//                    @Override
//                    public void onProviderDisabled(String provider) {
//
//                    }
//
//                    // 当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
//                    @Override
//                    public void onLocationChanged(Location location) {
//                        loadding.dismiss();
//                        if (location != null) {
//                            Log.e("TAG", "定位提供者："+location.getProvider());
//                            String locationStr = location.getLatitude()+";"+location.getLongitude();
//                            if ("0.0;0.0".equals(locationStr)){
//                                createAlertDialog("system");
//                                alertDialog.showAs();
//                                return;
//                            }else if ("1.0;1.0".equals(locationStr)){
//                                createAlertDialog("unknow");
//                                alertDialog.showAs();
//                                return;
//                            }else if("".equals(locationStr)){
//                                toast("获取地理位置信息失败");
//                                return;
//                            }
//                            gotoKsSDK(locationStr);
//                        }
//                    }
//                });
            }

        }else {
            createAlertDialog("location_setting");
            alertDialog.showAs();
        }
    }
//        providers = locationManager.getProviders(true);
//        if(providers == null || providers.size() == 0){
//            createAlertDialog("location_setting");
//            alertDialog.showAs();
//            return;
//        }
//
//        Location location = null;
//        for (int i = 0; i < providers.size(); i++) {
//            location = locationManager.getLastKnownLocation(providers.get(i));
//            if (location != null){
//                break;
//            }
//        }
//


    private void gotoKsSDK(String locationStr) {
        String tradeMoney = rechargeMoneyEdit.getText().toString().trim();
        PayInfo payInfo = new PayInfo();
        payInfo.setComId(DesUtil.decrypt(payOrder.comId));
        payInfo.setTradeMoney(new BigDecimal(tradeMoney));
        payInfo.setOrderNo(payOrder.orderNo);
        payInfo.setOrderTime(payOrder.orderTime);
        payInfo.setLocation(locationStr);
        Log.e("location", "location:" + locationStr);
        payInfo.setCallBackClassName(Cst.COMMON.RESULT_CALL_BACK_CLASS_NAME);
        payInfo.setCancelCallBackClassName(Cst.COMMON.CANCEL_CALL_BACK_CLASS_NAME);
        payInfo.setCustomInfo(StringUtil.encodeURL(payOrder.businessName));
        payOrder.location = payInfo.getLocation();
        payOrder.tradeMoney = tradeMoney;
        Intent intent = new Intent(mContext, PayActivity.class);
        intent.putExtra("payInfo", payInfo);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_out_from_left);
    }

    private void createAlertDialog(String flag) {
        if (flag.equals("system")){
            alertDialog = new LoadDialog(mContext, R.style.dialog, false, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(Settings.ACTION_SETTINGS);
                    startActivity(intent);
                    alertDialog.dismiss();
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            },0).setMessage("为了刷卡安全，需要获取您的地理位置信息，请到“设置-权限管理”中进行设置").setSureBtn("设置").setCancelBtn("取消");

        }else if (flag.equals("location_setting")){
            alertDialog = new LoadDialog(mContext, R.style.dialog, false, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                    alertDialog.dismiss();
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            },0).setMessage("为了刷卡安全，需要获取您的地理位置信息，请开启“定位服务”").setSureBtn("开启").setCancelBtn("取消");
        }else {
            alertDialog = new LoadDialog(mContext, R.style.dialog, false,new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            },0).setMessage("为了刷卡安全，需要获取您的地理位置信息，请到相关软件中进行权限设置").setSureBtn("知道了");
        }

    }


    /*
    * 检查用户是否实名认证，是否设置交易密码
    * */
    private void check() {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        //判断隐藏软键盘是否弹出
        if(imm.isActive(rechargeMoneyEdit)){
            imm.hideSoftInputFromWindow(mContext.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        if(passwordPanelWindow == null){
            passwordPanelWindow = PasswordPanel.getInstances(mContext).createPasswordWindow();
        }
        PasswordPanel.getInstances(mContext).setOnInputCompleted(new PasswordPanel.OnInputCompleted() {
            @Override
            public void onCompleted(String pass) {

                toast(String.valueOf(pass));
            }
        });
        PasswordPanel.getInstances(mContext).show(getContentView());
    }
}
