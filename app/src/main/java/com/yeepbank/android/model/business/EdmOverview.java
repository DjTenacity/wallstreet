package com.yeepbank.android.model.business;

import com.yeepbank.android.base.BaseModel;

/**
 * Created by WW on 2015/11/5.
 * 天天盈
 */
public class EdmOverview extends BaseModel {

    /*
    * 当期买入金额
    * */
    public double totalBiddingAmountToday;

    public double totalPjtBiddingAmountToday;

    public double totalEdmBiddingAmountToday;

    public double totalRjtBiddingAmountToday;

    /*
    * 下期续投
    * */
    public double autoAmount;

    public double autoPjtAmount;

    public double autoEdmAmount;

    public double autoRjtAmount;
    /*
    * 申请转出金额
    * */
    public double exitingAmount;

    public String exitingPjtAmount;

    public String exitingEdmAmount;

    public String exitingRjtAmount;

    /*
    * 项目剩余金额
    * */
    public double projectBalanceAmount;

    public String projectPjtBalanceAmount;

    public String projectEdmBalanceAmount;

    public String projectRjtBalanceAmount;
    /*
    * 累计收益
    * */
    public double totalInterest;

    public String totalPjtInterest;

    public String totalEdmInterest;

    public String totalRjtInterest;
}
