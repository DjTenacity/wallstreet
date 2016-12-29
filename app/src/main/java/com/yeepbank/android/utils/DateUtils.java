package com.yeepbank.android.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xiaogang.dong on 2016/9/1.
 * 将时间戳转换成日期的格式
 */
public class DateUtils {
    private static SimpleDateFormat sf = null;
    private static SimpleDateFormat sdf = null;
    /*获取系统时间 格式为："yyyy/MM/dd "*/
    public static String getCurrentDate() {
        Date d = new Date();
        sf = new SimpleDateFormat("yyyy年MM月dd日");
        return sf.format(d);
    }
    //  1472711655270
    /*时间戳转换成字符窜*/
    public static String getDateToString(long time) {
        Date d = new Date(time);
        sf = new SimpleDateFormat("yyyy年MM月dd日");
        return sf.format(d);
    }

    //  1472711655270
    /*时间戳转换成字符窜*/
    public static String getDateToStringTwo(long time) {
        Date d = new Date(time);
        sf = new SimpleDateFormat("yyyy年MM月dd HH:mm");
        return sf.format(d);
    }

    /*将字符串转为时间戳*/
    //时间格式转为时间戳
    public static long getStringToDate(String time) {
        sdf = new SimpleDateFormat("yyyy年MM月dd日");
        Date date = new Date();
        try{
            date = sdf.parse(time);
        } catch(ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date.getTime();
    }

    public static String DateCompare(String s1,String s2) {
        //设定时间的模板
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //得到指定模范的时间
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = sdf.parse(s1);
            d2 = sdf.parse(s2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //比较
        if(Math.abs(((d1.getTime() - d2.getTime())/(24*3600*1000))) >=3) {
            //System.out.println("大于三天");
            return "大于三天";
        }else{
            //System.out.println("小于三天");
            return "小于三天";
        }
    }
    /*
     * 毫秒转化时分秒毫秒
     */
    public static String formatTime(Long ms) {
        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long day = ms / dd;
        Long hour = (ms - day * dd) / hh;
        Long minute = (ms - day * dd - hour * hh) / mi;
        Long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        Long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        StringBuffer sb = new StringBuffer();
        if(day > 0) {
            sb.append(day+"天");
        }
        if(hour > 0) {
            sb.append(hour+"小时");
        }
        if(minute > 0) {
            sb.append(minute+"分");
        }
        /*if(second > 0) {
            sb.append(second+"秒");
        }
        if(milliSecond > 0) {
            sb.append(milliSecond+"毫秒");
        }*/
        return sb.toString();
    }
}
