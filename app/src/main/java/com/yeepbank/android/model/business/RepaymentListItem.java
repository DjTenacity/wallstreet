package com.yeepbank.android.model.business;

import java.io.Serializable;

/**
 * Created by WW on 2015/11/12.
 * 还款计划
 */
public class RepaymentListItem implements Serializable{

    public String projectRepaymentPlanId;
    public String principal;
    public String interest;
    public String status;
    public String statusName;
    public String paymentDueDate;
}
