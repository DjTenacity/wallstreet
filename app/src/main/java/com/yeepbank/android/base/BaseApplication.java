package com.yeepbank.android.base;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import com.android.volley.VolleyError;
import com.yeepbank.android.Cst;
import com.yeepbank.android.LaunchActivity;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.request.app_update.BugRequest;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;

/**
 * Created by WW on 2015/9/2.
 */
public class BaseApplication extends Application implements Thread.UncaughtExceptionHandler{

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        String versionInfo = getVersionInfo();
        String mobileInfo = getMobileInfo();
        String errorInfo = getErrorInfo(ex);
        Log.e("ERROR", "error:" + errorInfo);

        BugRequest request = new BugRequest(this, new StringListener() {
            @Override
            public void ResponseListener(String result) {
            }

            @Override
            public void ErrorListener(VolleyError volleyError) {
            }

        }, Cst.getVersionCode(this),"Product Model: " + android.os.Build.MODEL + ","
                + android.os.Build.VERSION.SDK + ","
                + android.os.Build.VERSION.RELEASE,errorInfo);
        request.stringRequest();
        restart();





    }

    private void restart(){
        Intent intent = new Intent(getApplicationContext(), LaunchActivity.class);
        PendingIntent restartIntent = PendingIntent.getActivity(
                getApplicationContext(), 0, intent,Intent.FLAG_ACTIVITY_NEW_TASK);

        AlarmManager mgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,
                restartIntent);
        ActivityStacks.getInstances().finishActivity();
    }


    private String getVersionInfo() {

        try {
            PackageManager packageManager = getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(),0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "?�ڦ�?";
        }
    }

    /**
     * ??????????????
     * @return
     */
    private String getMobileInfo() {
        StringBuffer sb = new StringBuffer();
        //???????????????????
        try {

            Field[] fields = Build.class.getDeclaredFields();
            for(Field field: fields){
                //???????? ,?????��????
                field.setAccessible(true);
                String name = field.getName();
                String value = field.get(null).toString();
                sb.append(name+"="+value);
                sb.append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * ???????????
     * @param arg1
     * @return
     */
    private String getErrorInfo(Throwable arg1) {
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        arg1.printStackTrace(pw);
        pw.close();
        String error= writer.toString();
        return error;
    }
}
