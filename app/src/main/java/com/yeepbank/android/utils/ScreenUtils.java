package com.yeepbank.android.utils;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by WW on 2015/12/29.
 */
public class ScreenUtils {
    private static int screenW;
    private static int screenH;
    private static float screenDensity;

    public static int getScreenW(Context context){
        if (screenW == 0){
            initScreen(context);
        }
        return screenW;
    }

    public static int getScreenH(Context context){
        if (screenH == 0){
            initScreen(context);
        }
        return screenH;
    }

    public static float getScreenDensity(Context context){
        if (screenDensity == 0){
            initScreen(context);
        }
        return screenDensity;
    }

    private static void initScreen(Context context){
        DisplayMetrics metric = context.getResources().getDisplayMetrics();
        screenW = metric.widthPixels;
        screenH = metric.heightPixels;
        screenDensity = metric.density;
    }

    /** �����ֻ��ķֱ��ʴ� dp �ĵ�λ ת��Ϊ px(����) */
    public static int dp2px(Context context, float dpValue) {
        return (int) (dpValue * getScreenDensity(context) + 0.5f);
    }

    /** �����ֻ��ķֱ��ʴ� px(����) �ĵ�λ ת��Ϊ dp */
    public static int px2dp(Context context, float pxValue) {
        return (int) (pxValue / getScreenDensity(context) + 0.5f);
    }
}

