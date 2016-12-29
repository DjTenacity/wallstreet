package com.yeepbank.android;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import com.android.volley.VolleyError;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.model.AppInfo;
import com.yeepbank.android.request.user.VersionUpdateRequest;
import com.yeepbank.android.response.user.VersionUpdateResponse;
import com.yeepbank.android.server.AppUpdateServer;
import com.yeepbank.android.utils.Utils;

import java.io.Serializable;

/**
 * Created by WW on 2015/12/8.
 */
public class UpdateService extends Service implements Serializable{

    public static final String ACTION = "com.yeepbank.android";
    public static final int  CHECK_VERSION = 0;
    public static final String UPDATE_MSG = "UPDATE_MSG";
    private int cmd;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        check(intent);
        return super.onStartCommand(intent, flags, startId);
    }



    private void check(final Intent intentP) {

        VersionUpdateRequest request=new VersionUpdateRequest(getApplicationContext(), new StringListener() {
            @Override
            public void ResponseListener(String result) {
                VersionUpdateResponse response=new VersionUpdateResponse();
                Intent intentService = new Intent(UpdateService.ACTION);
                intentService.setPackage(getPackageName());
                stopService(intentService);

                if(response.getStatus(result)==200){

                    cmd = response.getCmd(result);
                    String message = response.getUpdateMessage(result);
                    Intent intent = new Intent();
                    intent.setAction(UPDATE_MSG);
                    Bundle bundle = new Bundle();
                    Msg msg = new Msg();
                    msg.cmd = cmd;
                    msg.message = message;
                    msg.appInfo = response.getObject(result);
                    if (msg.appInfo == null){
                        return;
                    }
                    if (!"EVERY_EXAM".equals(intentP.getStringExtra("FROM"))
                            && Utils.getInstances().getUpdateInfoToPreference(getApplicationContext(), msg.appInfo.version)){
                        return;
                    }
                    bundle.putSerializable("data", msg);
                    intent.putExtra("data", bundle);
                    sendBroadcast(intent);
               }

            }

            @Override
            public void ErrorListener(VolleyError volleyError) {
                Intent intent = new Intent(UpdateService.ACTION);
                intent.setPackage("com.yeepbank.android");

                stopService(intent);
            }
        },Cst.getVersionCode(getApplicationContext()),"Android");
        request.stringRequest();
    }

    public class Msg implements Serializable{
        public int cmd;
        public String message;
        public AppInfo appInfo;

    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }
}
