package com.yeepbank.android.model.business;

import android.widget.ImageView;
import com.yeepbank.android.base.BaseModel;

/**
 * Created by WW on 2015/10/13.
 */
public class Project extends NorProject {

    /*public String projectId;
    public String projectName;
    public String duration;
    public String durationUnit;
    public double requestAmount;
    public String publishTime;
    public String longPublistTime;
    public double interestRate;
    public double biddingAmount;
    public String couponFcFlag;
    public String couponIaFlag;
    public String couponEcFlag;
    public String status;
    public String statusName;
    public String purpose;
    public String description;
    public String guarantee;
    public String mortgage;
    public String guaranteeFlag;
    public String mortgageFlag;
    public String repaymentType;
    public String repaymentTypeName;
    public String borrowName;
    public String borrowMobile;
    public String borrowIdNo;
    public String borrowIdNoMask;
    public String projectType;
    public String projectTitle;
    public String transferId;
    public String sellerName;
    public String publishEndDate;
    public String sellerInvestmentPrice;
    public String buyerInvestmentIncome;
    public String debtEndDate;*/

    public static String parseUnit(String unit){
        if("M".equals(unit.trim())){
            return "个月";
        }else if("Y".equals(unit.trim())){
            return "年";
        }else if("D".equals(unit.trim())){
            return "天";
        }
        return unit;
    }
}
