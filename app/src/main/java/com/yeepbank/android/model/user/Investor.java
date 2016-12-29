package com.yeepbank.android.model.user;

import com.yeepbank.android.base.BaseModel;

import java.io.Serializable;

/**
 * Created by WW on 2015/10/13.
 */
public class Investor extends BaseModel implements Serializable{

      public String investorId;//Ͷ����ID
     // public String loginName;//�˺�
      public String name;//����
      public String idNo;//���֤��
      public String idNoMask;//���֤��ʾ
      public String idAuthFlag;//�Ƿ�ʵ����֤
      public String mobile;//�ֻ���
      public String mobileMask;//�ֻ�����ʾ
      public String email;//��������
      public String address;//��ַ
      public String zipCode;//
      public boolean hasBuyedEdm;
      public boolean hasBuyEdmProject;
      public boolean hastxnPwd;//�Ƿ����ý�������
      public String appSecuretKey;//
      public String noviciate;//�Ƿ�����
      public boolean answerFlag;//�Ƿ������ʾ����

      public boolean  pushAllFlag;
      public boolean  pushDepositWithdrawFlag;
      public boolean pushNewProjectFlag;
      public boolean pushGrantCouponFlag;
      public boolean pushBiddingRelatedFlag;

      public String investLevel;
      public boolean hasBindCard;
}
