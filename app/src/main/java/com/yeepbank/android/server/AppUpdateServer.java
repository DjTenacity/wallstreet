package com.yeepbank.android.server;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import com.android.volley.VolleyError;
import com.yeepbank.android.Cst;
import com.yeepbank.android.R;
import com.yeepbank.android.base.ActivityStacks;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.model.AppInfo;
import com.yeepbank.android.request.app_update.AppCheckVersionRequest;
import com.yeepbank.android.response.app_update.AppCheckVersionResponse;
import com.yeepbank.android.utils.LoadDialog;
import com.yeepbank.android.utils.NetUtil;
import com.yeepbank.android.utils.Utils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.*;
import java.util.concurrent.*;

/**
 * Created by WW on 2015/8/31.
 */
public class AppUpdateServer {



    private Context mContext;
    private AppInfo appInfo;
    public  LoadDialog loadDialog;
    private PackageInfo packageInfo;
    private ExecutorService executorService = Executors.newFixedThreadPool(5);//创建线程池最大连接数为5
    private Handler msgHandler = null;
    private boolean DOWNLOAD_IS_COMPLETE = false;
    public static AppUpdateServer appUpdateServer;


    public static AppUpdateServer getInstances(Context context){

            synchronized (AppUpdateServer.class){
                appUpdateServer = new AppUpdateServer(context);
            }
        return appUpdateServer;
    }

    public void setAppInfo(AppInfo appInfo) {
        this.appInfo = appInfo;
        Log.e("URL","URI:"+appInfo.updateUrl);
    }

    private AppUpdateServer(final Context mContext) {
        this.mContext = mContext;

        try {
            packageInfo = mContext.getPackageManager().getPackageInfo("com.yeepbank.android",0);
            msgHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what){
                        case Cst.CMD.CHANGE_DOWNLOAD_PERCENT:
                            loadDialog.setDownLoadPercent(msg.obj + "%");
                            loadDialog.setDownLoadBar((Integer) msg.obj);
                            break;
                        case Cst.CMD.ALERT_ERROR_MSG:
                            String error = (String) msg.obj;
                            ((BaseActivity) mContext).showErrorMsg(error,null);
                            Utils.getInstances().putUpdateInfoToPreference(mContext, appInfo.version);
                            break;
                    }
                }
            };
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void onStartCommand(int flags) {
        switch (flags){
            case Cst.CMD.CHECK_APP_VERSION_CODE:
                checkIsNeedToVersionUp();
                break;
            case Cst.CMD.DOWNLOAD_APP:
                downLoad();
                break;
            case Cst.CMD.DOWNLOAD_UNDER_WIFI:
                downLoadTask(appInfo.updateUrl);
                break;
            case Cst.CMD.INSTALL_APK:
                if(DOWNLOAD_IS_COMPLETE){
                    installApk();
                }
                break;
            default:
                break;
        }

    }

    public boolean isDownloading(){
        return loadDialog!=null && loadDialog.isShowing();
    }

    /*
    * 下载新版本apk*/
    private void downLoad() {
        if(appInfo.mustUpdate){
            downLoadTask(appInfo.updateUrl);
        }else{
            loadDialog = new LoadDialog(mContext, R.style.dialog, false, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadDialog.dismiss();
                   downLoadTask(appInfo.updateUrl);
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadDialog.dismiss();
                    Utils.getInstances().putUpdateInfoToPreference(mContext, appInfo.version, true);
                }
            },Cst.CMD.ALERT_DIALOG_APP_UPDATE);
            loadDialog.setTitle("提示");
            loadDialog.setMessage("发现新版本 version " + appInfo.version+",是否下载");
            loadDialog.showAs();
        }
    }

    private void showDownLoading() {
        if(loadDialog!=null){
            loadDialog.dismiss();
        }

        loadDialog = new LoadDialog(mContext,R.style.dialog,false,Cst.CMD.DOWN_LOADING);
        loadDialog.show();
    }

    private void checkIsNeedToVersionUp() {

        if(NetUtil.getInstances(mContext).isConnected()){
            loadDialog = new LoadDialog(mContext, R.style.dialog,false,Cst.CMD.APP_CHECK_LOADING);

                loadDialog.showAs();



            AppCheckVersionRequest checkVersionRequest = new AppCheckVersionRequest(mContext, new StringListener() {
                @Override
                public void ResponseListener(String result) {
                    loadDialog.dismiss();
                    AppCheckVersionResponse response = new AppCheckVersionResponse();
                    appInfo = response.getObject(result);

                    if(packageInfo != null){
                        int versionCode = packageInfo.versionCode;
                        String serverVersionCode = appInfo.version;
                        if(!(versionCode+"").equals(serverVersionCode.trim())){
                            onStartCommand(Cst.CMD.DOWNLOAD_APP);
                        }else {
                            //TODO  直接跳进界面

                        }
                    }

                }

                @Override
                public void ErrorListener(VolleyError volleyError) {
                    loadDialog.dismiss();
                    VolleyError error = volleyError;
                }
            });
            try {
                checkVersionRequest.stringRequest();
            } catch (Exception e) {
                ((BaseActivity)mContext).showErrorMsg(mContext.getString(R.string.net_error),null);
            }
        }

    }

    /*
    * 下载apk
    * */
    private  void downLoadTask (final String url){

        if(NetUtil.getInstances(mContext).getNetType() != Cst.COMMON.WIFI){
            loadDialog = new LoadDialog(mContext, R.style.dialog, false, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadDialog.dismiss();
                    if(!appInfo.mustUpdate){
                        Utils.getInstances().putUpdateInfoToPreference(mContext, appInfo.version, true);
                    }
                    doDownload(url);
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadDialog.dismiss();
                    if(appInfo.mustUpdate){
                        ActivityStacks.getInstances().finishActivity();
                    }
                }
            },Cst.CMD.ALERT_DIALOG_APP_UPDATE);
            loadDialog.setTitle("提示").setMessage("当前网络不是WIFI环境，是否继续下载").setSureBtn("确定");
            if(appInfo.mustUpdate){
                loadDialog.setCancelBtn("退出");
            }else {
                loadDialog.setCancelBtn("取消");
            }
            loadDialog.showAs();


        }else {
            if(!appInfo.mustUpdate){
                Utils.getInstances().putUpdateInfoToPreference(mContext, appInfo.version, true);
            }
            doDownload(url);
        }


    }
    /*
    * 下载
    * */

    private synchronized void doDownload(String url){
        showDownLoading();//显示下载进度条
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                //判断sd卡状态是否可用
                if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
                    try {
                        File tempFile = null;
                        HttpClient client = new DefaultHttpClient();
                        HttpGet get = new HttpGet(appInfo.updateUrl);
                        //HttpGet get=new HttpGet("http://app.yeepbank.com/apk/wallstreet_android_pro_2.1.5.apk");
                        HttpResponse response = client.execute(get);
                        HttpEntity entity = response.getEntity();
                        long length = entity.getContentLength();
                        InputStream is = entity.getContent();
                        if (is != null) {
                            File pathFile = new File(Cst.URL.APK_PATH);
                            if (!pathFile.exists() && pathFile.isDirectory()) {
                                pathFile.mkdirs();
                            }
                            tempFile = new File(Cst.URL.APK_PATH + Cst.COMMON.APK_NAME);
                            if (tempFile != null && tempFile.exists()) {
                                tempFile.delete();
                            }
                            tempFile.createNewFile();

                            BufferedInputStream in = new BufferedInputStream(is);
                            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(tempFile));
                            int read = 0;
                            int count = 0;
                            int percent = 0;
                            byte[] buffer = new byte[1024];
                            while ((read = in.read(buffer)) != -1) {
                                out.write(buffer, 0, read);
                                count += read;
                                percent = (int) (((double) count / length) * 100);
                                Message message = msgHandler.obtainMessage();
                                message.what = Cst.CMD.CHANGE_DOWNLOAD_PERCENT;
                                message.obj = percent;
                                msgHandler.sendMessage(message);
                            }
                            out.flush();
                            is.close();
                            in.close();
                            out.close();
                            DOWNLOAD_IS_COMPLETE = true;
                        }

                    } catch (IOException e) {
                        Message msg = msgHandler.obtainMessage();
                        msg.what = Cst.CMD.ALERT_ERROR_MSG;
                        msg.obj = "网络错误";
                        msgHandler.sendMessage(msg);
                    } finally {
                        loadDialog.dismiss();
                        onStartCommand(Cst.CMD.INSTALL_APK);
                    }
                }else{
                    Message msg = msgHandler.obtainMessage();
                    msg.what = Cst.CMD.ALERT_ERROR_MSG;
                    msg.obj = "SD卡不可用";
                    msgHandler.sendMessage(msg);
                }
            }
        });
    }

    /*
    * 安装apk
    * */
    private void installApk(){
        File file = new File(Cst.URL.APK_PATH, Cst.COMMON.APK_NAME);
        if (!file.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        mContext.startActivity(intent);
        Utils.getInstances().cleanSharedPreference(mContext);
        ActivityStacks.getInstances().finishActivity();
    }

    public void recycle(){
        if(appUpdateServer != null) appUpdateServer = null;
    }

}
