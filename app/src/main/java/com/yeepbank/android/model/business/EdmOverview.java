package com.yeepbank.android.model.business;

import com.yeepbank.android.base.BaseModel;

/**
 * Created by WW on 2015/11/5.
 * ����ӯ
 */
public class EdmOverview extends BaseModel {

    /*
    * ����������
    * */
    public double totalBiddingAmountToday;

    public double totalPjtBiddingAmountToday;

    public double totalEdmBiddingAmountToday;

    public double totalRjtBiddingAmountToday;

    /*
    * ������Ͷ
    * */
    public double autoAmount;

    public double autoPjtAmount;

    public double autoEdmAmount;

    public double autoRjtAmount;
    /*
    * ����ת�����
    * */
    public double exitingAmount;

    public String exitingPjtAmount;

    public String exitingEdmAmount;

    public String exitingRjtAmount;

    /*
    * ��Ŀʣ����
    * */
    public double projectBalanceAmount;

    public String projectPjtBalanceAmount;

    public String projectEdmBalanceAmount;

    public String projectRjtBalanceAmount;
    /*
    * �ۼ�����
    * */
    public double totalInterest;

    public String totalPjtInterest;

    public String totalEdmInterest;

    public String totalRjtInterest;
}
