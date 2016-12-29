package com.yeepbank.android.model;

import java.io.Serializable;

/**
 * Created by WW on 2016/6/2.
 */
public class SocketMsg implements Serializable{

    public static final int RUNNING_ACCOUNT = 1;
    public static final int NEW_PROJECT = 2;
    public static final int INVEST_TICK = 3;
    public static final int BIDDING_END = 4;
    public static final int BIDDING_END_REPAYMENTED = 5;


    public int activeCode;
    public boolean hasNew;
    public long currentTimes;
}
