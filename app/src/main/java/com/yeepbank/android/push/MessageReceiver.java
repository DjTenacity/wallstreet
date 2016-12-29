package com.yeepbank.android.push;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.VolleyError;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.yeepbank.android.Cst;
import com.yeepbank.android.R;
import com.yeepbank.android.WebSocketService;
import com.yeepbank.android.activity.business.HomeActivity;
import com.yeepbank.android.http.BaseResponse;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.model.user.Investor;
import com.yeepbank.android.request.RegisterPullRequest;
import com.yeepbank.android.request.user.ResetSecuretKeyRequest;
import com.yeepbank.android.response.DefaultResponse;
import com.yeepbank.android.response.user.LoginAndRegisterResponse;
import com.yeepbank.android.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by WW on 2016/3/7.
 */
public class MessageReceiver extends BroadcastReceiver {
    private static final String TAG = "JPush";
    private NotifyManagerUtil notifyManagerUtil;
    public static StringBuilder payloadData = new StringBuilder();

    public void onReceive(final Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        Log.d("GetuiSdkDemo", "onReceive() action=" + bundle.getInt("action"));

        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_MSG_DATA:
                // 获取透传数据
                // String appid = bundle.getString("appid");
                byte[] payload = bundle.getByteArray("payload");

                String taskid = bundle.getString("taskid");
                String messageid = bundle.getString("messageid");

                if (!isRunningForeground(context)){
                    notifyManagerUtil = new NotifyManagerUtil(context);
                    notifyManagerUtil.notify(new Random().nextInt(),"有新消息",new String(payload));
                }
                break;

            case PushConsts.GET_CLIENTID:
                // 获取ClientID(CID)
                // 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
                Cst.PUSH_CLIENT_ID = bundle.getString("clientid");
//                Intent socketIntent = new Intent(WebSocketService.ACTION);
//                socketIntent.putExtra("cid",Cst.PUSH_CLIENT_ID);
//                context.startService(socketIntent);

                Intent mIntent = new Intent(WebSocketService.ACTION);
                mIntent.setPackage(context.getPackageName());//这里你需要设置你应用的包名
                mIntent.putExtra("cid", Cst.PUSH_CLIENT_ID);
                context.startService(mIntent);
                Log.e("=+-1",new Date().getTime()+"");

                if (isRunningForeground(context)){
                    String uuid = ((TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                    //Utils.getInstances().putLogToSD(Cst.PUSH_CLIENT_ID + "CREATE_TIME:" + new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒 E ").format(new Date()));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            registerPull(context, Cst.PUSH_CLIENT_ID);
                        }
                    },100);

                }

                break;

            case PushConsts.THIRDPART_FEEDBACK:

                 String appid = bundle.getString("appid");
                 /* bundle.getString("taskid"); String actionid = bundle.getString("actionid");
                 * String result = bundle.getString("result"); long timestamp =
                 * bundle.getLong("timestamp");
                 *
                 * Log.d("GetuiSdkDemo", "appid = " + appid); Log.d("GetuiSdkDemo", "taskid = " +
                 * taskid); Log.d("GetuiSdkDemo", "actionid = " + actionid); Log.d("GetuiSdkDemo",
                 * "result = " + result); Log.d("GetuiSdkDemo", "timestamp = " + timestamp);
                 */
                break;

            default:
                break;
        }
    }

    /*
    * 在服务端注册设备
    * */
    private void registerPull(final Context context, final String cid) {
       // Log.e("注册设备","注册设备时间："+new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date()));
        String mac = getLocalMacAddress(context);
        String imei = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        RegisterPullRequest request = new RegisterPullRequest(context, new StringListener() {
            @Override
            public void ResponseListener(String result) {
//                DefaultResponse response = new DefaultResponse();
//                if (response.getStatus(result) == 200){
//                    Toast.makeText(context,"手机注册成功",Toast.LENGTH_LONG).show();
//                }else {
//                    Toast.makeText(context,"手机注册失败"+response.getMessage(result),Toast.LENGTH_LONG).show();
//                }
            }

            @Override
            public void ErrorListener(VolleyError volleyError) {
                //Toast.makeText(context,"手机注册失败：服务端未响应",Toast.LENGTH_LONG).show();
            }
        },imei,cid,mac,android.os.Build.VERSION.RELEASE);
        request.stringRequest();
    }


    public String getLocalMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    private boolean isRunningForeground (Context context)
    {
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();
        if(!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(context.getPackageName()))
        {
            return true ;
        }

        return false ;
    }
}
