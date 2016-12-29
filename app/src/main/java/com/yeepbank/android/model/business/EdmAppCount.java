package com.yeepbank.android.model.business;

import com.yeepbank.android.base.BaseModel;

/**
 * Created by WW on 2015/11/5.
 * 没登录时天天盈信息
 */
public class EdmAppCount extends BaseModel {

    /*
* 最大利率
* */
    public double maxInterestRate;

    /*
    * 项目金额
    * */
    public double totalRequestAmount;

    /*
    *计算进度条
    * */
    public double totalBiddingAmount;

    /*
    *投资人数
    * */
    public String totalBiddingCount;

    /*
    * 剩余金额
    * */
    public double totalSurplusAmount;
    /*
    * 每天收益
    * */
    public double dailyEarningsPer;

    /*
   *累计投资
   * */
    public double allTotalBiddingAmount;


}
