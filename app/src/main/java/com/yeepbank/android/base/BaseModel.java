package com.yeepbank.android.base;

import com.yeepbank.android.model.business.DetailModel;
import com.yeepbank.android.model.user.CouponsVo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by WW on 2015/9/11.
 */
public class BaseModel implements Serializable {


    public String loginName;//投资人
    public double sellerRoi;//转让利率

    public CouponsVo couponList[];//可使用票据

    //data.requestAmount - data.biddingAmount 为剩余金额
    public String sellerId;//卖项目人的id

    public String projectId ;
    public String projectName ;
    public String duration ;
    public String durationUnit ;
    public double requestAmount ;
    public String publishTime ;
    public double interestRate ;//年化利率
    public double biddingAmount ;
    public String couponFcFlag ;
    public String couponIaFlag ;
    public String couponEcFlag ;
    public String status ;    //public static final String IPB = "IPB";//转让中
                                //public static final String SCS = "SCS";//转让成功
    public String statusName ;
    public String purpose ;
    public String description ;//借款说明
    public String guarantee ;
    public String mortgage ;
    public String guaranteeFlag ;
    public String mortgageFlag ;
    public String repaymentType ;
    public String repaymentTypeName ;

    public String projectType ;//FFI新手，
    public String transferId;//转债醒目编号
    public String projectTypeName ;//天天用买入条目中间字段

    /*
    * 预期收益
    * */
    public String expectedReturnForOht;

    public String projectTitle;//项目标题
    /*
    * 起投额
    * */
    public double biddingStartAmount;
    /*
    * 步长
    * */
    public double biddingStepAmount;
     /*
    * 最大投资额
    * */
    public double biddingEndAmount;

    public long longPublistTime;

    /*
* 债权出让人
* */
    public String sellerName;
    /*
    * 截止日期
    * */
    public String publishEndDate;
    /*
    * 到期本息
    * */
    public String sellerInvestmentPrice;
    /*
    * 到期收益
    * */
    public String buyerInvestmentIncome;
    /*
    * 到期日
    * */
    public String debtEndDate;
    /*
    * 剩余期限
    * */
    public String buyerHoldingDays;
    /*
    * 转让金额
    * */
    public double transferPrice;
    /*
    * 转让利率
    * */
    public double buyerRoi;

    public String hangingType;//挂标方式(T - transfer  转债,R - redirect　直接挂标)
    public String borrowerType;//借款类型(个人 - P,企业 - B)
    //借款人信息(直接挂标-个人)
    public String borrowName;//债权转让方或借款人
    public String borrowMobileMask;
    public String borrowIdNo;
    public String borrowIdNoMask;
    //借款企业信息(直接挂标/委托挂标-企业)
    public String businessName;//企业名称
    public String legalPersonNameMask;//法人姓名
    public String legalPersonMobileMask;//法人手机号
    public String legalPersonIdNoMask;//法人身份证号
    public String businessLicenseNoMask;//工商登记号
    public List<DetailModel> protocols;
    public String riskLevel;//项目的风险等级
    public String income;//年收入
    public String assets;//主要财产
    public String debt;//主要债务
    public String business;//财产主要来源
    public String defaults;//是否有银行还款违约记录
    public String location;//地理位置
    public String repaymentSource;//还款来源
    public String creditComment;//信用评价


    public static String parseUnit(String unit){
        if("M".equals(unit.trim())){
            return "个月";
        }else if("Y".equals(unit.trim())){
            return "年";
        }else if("D".equals(unit.trim())){
            return "天";
        }
        return unit;
    }
}
