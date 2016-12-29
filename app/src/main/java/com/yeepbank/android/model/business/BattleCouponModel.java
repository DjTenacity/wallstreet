package com.yeepbank.android.model.business;

import com.yeepbank.android.base.BaseModel;

/**
 * Created by xiaogang.dong on 2016/9/1.
 */
public class BattleCouponModel extends BaseModel{
   /* public String couponId;//券ID
    public String couponCode;//券代码(12位)
    public String couponType;//券类型(券类型Full Cut Coupon - FC 满减;Interest Adding - IA 加息;Experience Coupon - EC 体验)
    public String couponTypeName;
    public double experienceAmount;//体验金额
    public double fullAmount;//满减金额-满
    public double subtractAmount;//满减金额-减
    public double addingInterest;//加息利率 - 增加的利息
    public double minAmount;//加息- 投资下限
    public double maxAmount;//加息- 投资上限
    public String expirationDate;//截止日期
    public String usedDatetime;//使用时间
    public String couponRule;//券规则
    public String status;//券状态(B-可以领取;U-已经领取;E-券已经领完；F-券已经过期)
    public String couponDescription;//描述(发放原因,10字以内,主要用于展示给用户)
    public boolean choose = false;//是否中
    public long beginTime;//投资券的开始时间
    public long endTime;//投资券的结束时间*/

    public String batchId;
    public String couponBatchNo;
    public String couponType;
    public String couponTypeName;
    public String totalCount;
    public String receivedCount;
    public String experienceAmount;
    public String fullAmount;
    public String subtractAmount;
    public double addingInterest;
    public String minAmount;
    public String maxAmount;
    public String couponDescription;
    public String   receiveStartTime;
    public String   receiveEndTime;
    public long   receiveStartTimeStamp;
    public long   receiveEndTimeStamp;
    public String  projectDuration;
    public String  projectDurationRange;
    public String  expirationDate;
    public String  couponStatus;
    public String  couponStatusName;
    public String  couponStrTime;
    public String  couponTimeStamp;
    public String  order;

 public String getCouponStatus() {
  return couponStatus;
 }

 public void setCouponStatus(String couponStatus) {
  this.couponStatus = couponStatus;
 }
}
