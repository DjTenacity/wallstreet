package com.yeepbank.android.model.business;

import com.yeepbank.android.base.BaseModel;

/**
 * Created by xiaogang.dong on 2016/9/1.
 */
public class BattleCouponModel extends BaseModel{
   /* public String couponId;//ȯID
    public String couponCode;//ȯ����(12λ)
    public String couponType;//ȯ����(ȯ����Full Cut Coupon - FC ����;Interest Adding - IA ��Ϣ;Experience Coupon - EC ����)
    public String couponTypeName;
    public double experienceAmount;//������
    public double fullAmount;//�������-��
    public double subtractAmount;//�������-��
    public double addingInterest;//��Ϣ���� - ���ӵ���Ϣ
    public double minAmount;//��Ϣ- Ͷ������
    public double maxAmount;//��Ϣ- Ͷ������
    public String expirationDate;//��ֹ����
    public String usedDatetime;//ʹ��ʱ��
    public String couponRule;//ȯ����
    public String status;//ȯ״̬(B-������ȡ;U-�Ѿ���ȡ;E-ȯ�Ѿ����ꣻF-ȯ�Ѿ�����)
    public String couponDescription;//����(����ԭ��,10������,��Ҫ����չʾ���û�)
    public boolean choose = false;//�Ƿ���
    public long beginTime;//Ͷ��ȯ�Ŀ�ʼʱ��
    public long endTime;//Ͷ��ȯ�Ľ���ʱ��*/

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
