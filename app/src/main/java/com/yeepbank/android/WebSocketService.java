package com.yeepbank.android;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.*;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.yeepbank.android.base.BaseFragment;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.model.SocketMsg;
import com.yeepbank.android.push.NotifyManagerUtil;
import com.yeepbank.android.request.WebSocketCallBackRequest;
import com.yeepbank.android.response.DefaultResponse;
import com.yeepbank.android.utils.Utils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

/**
 * Created by WW on 2016/6/1.
 */
public class WebSocketService extends Service{
    private ExampleClient exampleClient;
    public static final String ACTION = "com.yeepbank.android.websocket";
    public static final String RED_DOT_FILTER = "com.yeepbank.android.websocket_red_dot_filter";
    private String cid = null;
    private WebSocketReceiver webSocketReceiver;
    private NotifyManagerUtil.PushMessage  message;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("=+-2",new Date().getTime()+"");
        webSocketReceiver = new WebSocketReceiver();
        registerReceiver(webSocketReceiver,new IntentFilter(ACTION));
        if (intent != null && ACTION.equals(intent.getAction())){
            cid = intent.getStringExtra("cid");
            if (cid != null && cid.trim().length() > 0){
                resetClient(cid);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }


    private class WebSocketReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //Log.e("----","广播接收者1");
            if (intent != null && ACTION.equals(intent.getAction())){
                //Log.e("----","广播接收者2");
                Bundle bundle = intent.getBundleExtra("push_message");
                if (bundle != null){
                   // Log.e("----","广播接收者3");
                    message = (NotifyManagerUtil.PushMessage) bundle .getSerializable("push_message");
                }else {
                    //Log.e("----","广播接收者4");
                    cid = intent.getStringExtra("cid");
                    if (cid != null && cid.trim().length() > 0){
                        //Log.e("----","广播接收者5");
                        exampleClient.closed(cid);
                        resetClient(cid);
                        //Log.e("----",  "广播接收者6");
                    }
                }

            }
        }
    }


    private void resetClient(String cid) {
        String userId = Cst.currentUser==null?"":Cst.currentUser.investorId;
        try {
            //ypns.yeepbank.com
            //ws.yeepbank.xyz
            exampleClient = new ExampleClient( new URI(Cst.URL.WEB_SOCKET_URL+cid+"?userId="+userId), new Draft_17());
            exampleClient.connect();
            Log.e("websocket", "open++++++++++");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(webSocketReceiver);
        if (exampleClient!= null && exampleClient.isOpen()){
            exampleClient.close();
        }
        super.onDestroy();
    }

    private class ExampleClient extends WebSocketClient {

        public ExampleClient(URI serverUri, Draft draft) {
            super(serverUri, draft);
        }

        public void closed(String mCid){
            cid = mCid;
            exampleClient.close();
        }

        @Override
        public void onOpen(ServerHandshake serverHandshake) {
            Log.e("websocket", "open");
            Cst.WEB_SOCKET_IS_CONNECTED = true;
            //send("nini");
        }

        @Override
        public void onMessage(String s) {
            Log.e("----",s+"+++sdf");
            final SocketMsg socketMsg = new Gson().fromJson(s,SocketMsg.class);
            socketMsg.currentTimes = new Date().getTime();
            //Log.e("+++","券来啦");
            if (socketMsg != null && cid != null){
                WebSocketCallBackRequest callBackRequest = new WebSocketCallBackRequest(WebSocketService.this, new StringListener() {
                    @Override
                    public void ResponseListener(String result) {
                        DefaultResponse defaultResponse = new DefaultResponse();
                        if (defaultResponse.getStatus(result) == 200){

                            if (NotifyManagerUtil.message != null && Integer.parseInt(NotifyManagerUtil.message.activeCode) == Integer.parseInt(String.valueOf(socketMsg.activeCode)) && Math.abs(NotifyManagerUtil.message.currentTimes - socketMsg.currentTimes) < 8000){
                                return;
                            }
//                            Log.e("TAG","相差时间："+(NotifyManagerUtil.message.currentTimes - socketMsg.currentTimes));
//                            Utils.getInstances().putLogToSD("推送code == null?"+(message == null)+";提醒code"+socketMsg.activeCode+";相差时间:"+(message.currentTimes - socketMsg.currentTimes));
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("socketMsg", socketMsg);
                            Intent intent = new Intent();
                            intent.setAction(BaseFragment.SOCKET_MSG);
                            intent.putExtra("socketMsg",bundle);
                            sendBroadcast(intent);
                        }
                    }

                    @Override
                    public void ErrorListener(VolleyError volleyError) {
                        Log.e("VolleyError","VolleyError:"+volleyError.getMessage());
                    }
                },socketMsg.activeCode,cid);
                callBackRequest.stringRequest();
            }
        }

        @Override
        public void onClose(int i, String s, boolean b) {
            Cst.WEB_SOCKET_IS_CONNECTED = false;

            /*if (isAppRunningForeground()){
                Looper.prepare();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (cid != null && cid.trim().length() > 0) {
                            resetClient(cid);
                            Log.e("++++", "好的---");
                        }
                    }
                }, 3000);
                Looper.loop();
            }else {
                Cst.WEB_SOCKET_IS_CONNECTED = false;
            }*/


        }

        @Override
        public void onError(Exception e) {
            Log.e("websocket", "onError:" + e.getMessage().toString());
            if (isAppRunningForeground()){
                Looper.prepare();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (cid != null && cid.trim().length() > 0) {
                            resetClient(cid);

                        }
                    }
                }, 3000);
                Looper.loop();
            }
        }
    }

    private boolean isAppRunningForeground(){
        ActivityManager  appManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = getPackageName();
        List<ActivityManager.RunningAppProcessInfo> appProcessInfos = appManager.getRunningAppProcesses();
        if (appProcessInfos == null) return false;
        for (ActivityManager.RunningAppProcessInfo info : appProcessInfos){
            if (info.processName.equals(packageName)){
                return true;
            }
        }
        return false;
    }
}
