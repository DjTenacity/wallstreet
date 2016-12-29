package com.yeepbank.android.model.user;

import java.io.Serializable;

/**
 * Created by user on 2016/3/24.
 */
public class WithDrawRecordModel implements Serializable {
    public String requestDatetime;
    public String approveDatetime;
    public String amount;
    public String status;
    public String statusName;
}
