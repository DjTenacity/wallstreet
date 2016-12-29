package com.yeepbank.android.model;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by WW on 2016/2/24.
 */
public class Web implements Serializable {

    public Web(){

    }
    public Web(String title,String url){
        this.title = title;
        this.url = url;
    }
    public Web(String title,String url,String backBtnText){
        this.title = title;
        this.url = url;
        this.backBtnText = backBtnText;
    }
    public Web(String title,String url,String backBtnText,HashMap<String,String> data){
        this.title = title;
        this.url = url;
        this.backBtnText = backBtnText;
        this.data = data;
    }
    public String title;
    public String url;
    public String backBtnText;
    public HashMap<String,String> data;
}
