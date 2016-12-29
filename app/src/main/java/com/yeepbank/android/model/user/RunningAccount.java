package com.yeepbank.android.model.user;

import com.yeepbank.android.base.BaseModel;

import java.io.Serializable;

/**
 * Created by WW on 2016/3/16.
 */
public class RunningAccount implements Serializable{

    public String type;
    public String happenDate;
    public String happenTime;
    public String success;//EÒÑ³ö¿î
    public String stateName;
    public String happenMoney;
    public String finishDate;
    public String finishTime;
    public String failReason;
    public boolean reasonIsOpen;
    public String requestDatetime;
    public String approveDatetime;


}
