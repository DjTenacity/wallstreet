package com.yeepbank.android.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.yeepbank.android.Cst;

/**
 * Created by WW on 2015/9/2.
 */
public class NetUtil {

    private static Context mContext;
    private static NetUtil netUtil;
    private static ConnectivityManager connectivityManager;

    public static int NET_TYPE;

    private NetUtil(Context context){
        mContext = context;
    };

    public static NetUtil getInstances(Context context){
        if(netUtil == null){
            synchronized (NetUtil.class){
                netUtil = new NetUtil(context);
                connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            }
        }
        return netUtil;
    }

    public boolean isConnected(){
        if(connectivityManager != null){
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if(netInfo!=null && netInfo.getState().equals(NetworkInfo.State.CONNECTED)){
                return true;
            }
        }else {
            new Throwable("initialize failed");
        }
        return false;
    }

    public int getNetType(){
        if(connectivityManager != null){
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if(networkInfo!=null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI){
                NET_TYPE = Cst.COMMON.WIFI;
            }else if(networkInfo!=null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE){
                NET_TYPE = Cst.COMMON.MOBILE;
            }
        }else {
            new Throwable("initialize failed");
        }
        return NET_TYPE;
    }
}
