package com.yeepbank.android.model.user;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by 董晓刚 on 2015/11/11.
 * 优惠券的实体
 */
public class CouponsVo implements Serializable {
    public String couponId;//券ID
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
    public String status;//券状态(B-已定;U-已使用;E-已过期)
    public String couponDescription;//描述(发放原因,10字以内,主要用于展示给用户)
    public boolean choose = false;//是否中
}
