package com.yeepbank.android.model.user;

import java.io.Serializable;

/**
 * Created by ������ on 2015/11/9.
 * �û����Ż�ȯ���󣨽��Ӻ�̨�õ�������CouponsVo����������װ��
 */
public class CouponsInfo implements Serializable{
    public int backGroundImg;
    public int leftGroundImg;
    public String couponType;
    public String title;
    public String msg;
    public String time;
    public int len;
    public int len2;
    public int getBackGroundImg() {
        return backGroundImg;
    }
    public void setBackGroundImg(int backGroundImg) {
        this.backGroundImg = backGroundImg;
    }
    public int getLeftGroundImg() {
        return leftGroundImg;
    }
    public void setLeftGroundImg(int leftGroundImg) {
        this.leftGroundImg = leftGroundImg;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }


}
