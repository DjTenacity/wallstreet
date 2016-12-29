package com.yeepbank.android.model.user;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by xiaogang.dong on 2015/11/25.
 * ´ý½á±êµÄModel
 */
public class NeProjectVo implements Serializable{
    public Long biddingId;
    public Long projetId;
    public double biddingAmount;
    public String biddingDatetime;
    public String biddingMonth;
    public String biddingDay;
    public int duration;
    public String durationUnit;
    public double interestRate;
    public String durationUnitName;
    public String projectTitle;
    public String status;
    public String statusName;
    public boolean couponUse;
    public String  couponRule;

}
