package com.yeepbank.android.model.user;

import java.io.Serializable;

/**
 * Created by WW on 2015/11/14.
 * �˻���Ϣ
 */
public class TotalAssets implements Serializable{

    /*
    * �������
    * */
    public double freeBalance;
    /*
    * �˻����
    * */
    public double balance;
    /*
    * ���ձ�Ϣ
    * */
    public double totalPIReceivable;
    /*
    * ����������
    * */
    public double totalBiddingAmountToday;
    /*
    * ������
    * */
    public double waitingBiddingAmount;
    /*
    * �����ڲ�������ӯ
    * */
    public double waitingBiddingAmountWithoutECcoupon;
    /*
    * ������
    * */
    public double withDrawingAmount;
    /*
    * ���ʲ�
    * */
    public double totalAssets;
}
