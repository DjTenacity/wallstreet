package com.yeepbank.android.model;

import java.io.Serializable;

/**
 * Created by WW on 2015/9/1.
 */
public class AppInfo implements Serializable {


    /*版本号*/
    public String version;

//    /*app名*/
//    public String name = "wallstreet_android_pro.apk";

    /*下载地址*/
   /* public String url;*/

    public String compel;

    /*是否强制更新*/
    public boolean mustUpdate;


     public String message;
     public boolean hasNewVersion;
     public String  updateUrl;
     public String upgradeType;

}
