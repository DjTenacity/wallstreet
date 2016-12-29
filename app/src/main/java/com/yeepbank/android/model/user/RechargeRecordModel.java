package com.yeepbank.android.model.user;

import java.io.Serializable;

/**
 * Created by user on 2016/3/24.
 */
public class RechargeRecordModel implements Serializable{
    public String depositDatetime;//充值时间 ": "2016-01-25 11:27:48",
    public String amount;//金额
    public String status;
    public String statusName;
    public String faileReason;
    public String depositDate;
    public String depositTime;
}
