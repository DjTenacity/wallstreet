package com.yeepbank.android.model.user;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by ������ on 2015/11/11.
 * �Ż�ȯ��ʵ��
 */
public class CouponsVo implements Serializable {
    public String couponId;//ȯID
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
    public String status;//ȯ״̬(B-�Ѷ�;U-��ʹ��;E-�ѹ���)
    public String couponDescription;//����(����ԭ��,10������,��Ҫ����չʾ���û�)
    public boolean choose = false;//�Ƿ���
}
