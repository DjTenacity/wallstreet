package com.yeepbank.android.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;

import java.util.List;

/**
 * Created by WW on 2015/9/6.
 */
public class PackageUtil {

    private Context mContext;

    private static PackageUtil packageUtil;

    private PackageUtil(Context mContext) {
        this.mContext = mContext;
    }

    public static PackageUtil getInstances(Context context){
        synchronized (PackageUtil.class){
            if(packageUtil == null){
                packageUtil = new PackageUtil(context);
            }
        }
        return packageUtil;
    }

    public boolean activityIsRunning(Class activityClass){
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> appTasks = activityManager.getRunningTasks(1);
        if(appTasks != null && appTasks.size() > 0){
            if(appTasks.get(0).topActivity.getClassName().equals(activityClass.getName())){
                return true;
            }
        }
        return false;
    }
}
