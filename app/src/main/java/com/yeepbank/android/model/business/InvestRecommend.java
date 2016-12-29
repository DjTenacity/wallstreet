package com.yeepbank.android.model.business;

import com.yeepbank.android.base.BaseModel;

/**
 * Created by WW on 2015/10/29.
 */
public class InvestRecommend extends BaseModel {
    /*
    * 项目名
    * */
    public String title;
    /*
    * 是否新推荐项目
    * */
    public boolean isNewer;
    /*
    * 投资期限
    * */
    public String timeLimit;
    /*
    * 投资百分比
    * */
    public int percent;
    /*
    * 是否体验项目
    * */
    public boolean isExperience;
    /*
    * 是否满减
    * */
    public boolean isFullCut;
    /*
    * 余额
    * */
    public String residualAmount;
    /*
    * 加息
    * */
    public boolean isIncreaseInterest;

    }
