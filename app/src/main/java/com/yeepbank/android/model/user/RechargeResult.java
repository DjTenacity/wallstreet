package com.yeepbank.android.model.user;

import java.io.Serializable;

/**
 * Created by WW on 2016/3/30.
 */
public class RechargeResult implements Serializable {
    //{"state":{"code":200,"message":"success"},
    // "data":{"depositDatetime":"","depositDate":"","depositTime":"","amount":null,"status":"W","statusName":"≥‰÷µ÷–","faileReason":""}}

    public String depositDatetime;
    public String depositDate;
    public String depositTime;
    public double amount;
    public String status;//F W S
    public String statusName;
    public String faileReason;
}
