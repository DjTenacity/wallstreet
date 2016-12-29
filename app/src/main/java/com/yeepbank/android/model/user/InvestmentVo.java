package com.yeepbank.android.model.user;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by xiaogang.dong on 2015/11/21.
 */
public class InvestmentVo implements Serializable{
    public Long investmentId;//Ͷ��ID
    public String debtStartDate;//Ͷ��ʱ��
    public String biddingMonth;
    public String biddingDay;
    public double investmentPrice;//Ͷ�ʽ��
    public Long projectId;
    public int duration;//��Ŀ����
    public String durationUnit;//��λ
    public String durationUnitName;
    public double interestRate;//�껯����
    public String planPeriodEndDate;//Ԥ�ƻ�������
    public String planPaymentDueDate;//��������
    public String investmentStatus;//IRP-������;ECL-�ѻ���
    public String investmentStatusName;
    public String projectTitle;
    public double receivable;//������Ϣ
    public String couponRule;//Ͷ��ȯ��ʹ�����

    public boolean hasTransfer;//�жϴ���Ŀ�ǲ���תծ��Ŀ
    public Long transferId;//תծ��Id
    public String transferDatetime;
    public double transferPrice;
    public int buyerHoldingDays;
    public double buyerRoi;
    public String debtEndDate;

}




