package com.yeepbank.android.model.order;

import java.io.Serializable;

/**
 * Created by WW on 2016/4/25.
 */
public class PdsDepositInfo implements Serializable{

        public String depositId;
        public String comId;
        public String ptdealerId;
        public String orderNo;
        public String businessName;
        public String orderTime;
        public String userNumber;
        public String location;
        public String tradeMoney;
        public String successTradeMoney;
        public String  orderCompleteTime;
        public String orderState;
        public String orderExplain;
        public String cardAfterFour;
        public boolean hasDeposit;

}
