package com.yeepbank.android.model.user;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by xiaogang.dong on 2015/11/21.
 */
public class InvestmentVo implements Serializable{
    public Long investmentId;//投资ID
    public String debtStartDate;//投资时间
    public String biddingMonth;
    public String biddingDay;
    public double investmentPrice;//投资金额
    public Long projectId;
    public int duration;//项目期限
    public String durationUnit;//单位
    public String durationUnitName;
    public double interestRate;//年化利率
    public String planPeriodEndDate;//预计还款日期
    public String planPaymentDueDate;//还款日期
    public String investmentStatus;//IRP-还款中;ECL-已还款
    public String investmentStatusName;
    public String projectTitle;
    public double receivable;//本金利息
    public String couponRule;//投资券的使用情况

    public boolean hasTransfer;//判断此项目是不是转债项目
    public Long transferId;//转债的Id
    public String transferDatetime;
    public double transferPrice;
    public int buyerHoldingDays;
    public double buyerRoi;
    public String debtEndDate;

}




