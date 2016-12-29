package com.yeepbank.android.model.user;

import java.io.Serializable;

/**
 * Created by WW on 2015/11/14.
 * 账户信息
 */
public class TotalAssets implements Serializable{

    /*
    * 可用余额
    * */
    public double freeBalance;
    /*
    * 账户余额
    * */
    public double balance;
    /*
    * 待收本息
    * */
    public double totalPIReceivable;
    /*
    * 当期买入金额
    * */
    public double totalBiddingAmountToday;
    /*
    * 待劫镖
    * */
    public double waitingBiddingAmount;
    /*
    * 待劫镖不含天天盈
    * */
    public double waitingBiddingAmountWithoutECcoupon;
    /*
    * 提现中
    * */
    public double withDrawingAmount;
    /*
    * 总资产
    * */
    public double totalAssets;
}
