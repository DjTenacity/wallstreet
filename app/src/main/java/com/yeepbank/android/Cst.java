package com.yeepbank.android;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.view.View;
import com.yeepbank.android.model.Banner;
import com.yeepbank.android.model.user.Investor;
import com.yeepbank.android.widget.SwitchBtn;
import java.util.ArrayList;

/**
 * Created by WW on 2015/9/1.
 */
public class Cst {


    /*
    * clientId  个推生成的识别号
    * */
    public static String PUSH_CLIENT_ID = null;

    public static boolean WEB_SOCKET_IS_CONNECTED = false;

     public static String SIGN_METHOD_SECRET = null;
    /*
        * @params key 加密秘钥
        * */
    public static String KEY;
    /*
    * 下拉刷新回调接口
    * */
    public interface OnRefresh<T>{
        void refresh(T target);
        void loadMore();
    }
    /*
    * 切换按钮切换回调接口
    * */
    public interface OnSlideCompleteListener{
        void onComplete(View view);
    }
    /*
    * SwitchBtn切换回调按钮
    * */
    public interface OnStateChangeListener{
        void onOpened(SwitchBtn switchBtn);
        void onClosed(SwitchBtn switchBtn);
    }

    public interface URL{

         /*
        * SD卡根目录
        * */

        String SDCARD_ROOT_PATH = Environment.getExternalStorageDirectory()+"/";
        /*
      * banner文件夹目录
      * */
        String BANNER_PATH = SDCARD_ROOT_PATH+"banner/";
        /*
       * 服务器地址
       * */
      //String ROOT_URL = "http://app.yeepbank.xyz";
      //String ROOT_URL = "http://test.app.yeepbank.xyz";
      //String ROOT_URL = "http://172.19.40.11";
        String ROOT_URL = "http://app.yeepbank.com";
       /*
       * 检查版本更新
       *
       * */
       String CHECK_VERSION = ROOT_URL + "/version/queryVersionInfo.ajax";


        /*
        * apk存放目录
        * */
        String APK_PATH = SDCARD_ROOT_PATH+"download/";
        /*
        * 登录
        * */
        //String LOGIN_URL = ROOT_URL+"/ana/login.ajax";
        String LOGIN_URL = ROOT_URL+"/api/mobile/login/post.json";
        /*
        * 注册
        * */
        String REGISTER_URL = ROOT_URL+"/api/mobile/register/post.json";
        /*
        * 获取验证码
        * */
        String EXAM_CODE_URL = ROOT_URL+"/api/mobile/phone/sendCode.json";

        /*
        * 获取首页数据
        * */
        String INVEST_DATA_URL = ROOT_URL+"/api/mobile/home/project.json";

        /*
        * 获取推荐项目列表
        * */
        String PROJECT_LIST_URL = ROOT_URL+"/api/mobile/project/list.json";

        /*
       * 获取转债项目列表
       * */
        String PROJECT_TRANSFORM_URL = ROOT_URL+"/api/mobile/transfer/list.json";

        /*
        * 确认投资
        * */
        String DO_BIDDING_URL = ROOT_URL+"/api/mobile/bidding/doBidding.json";
        /*
        * 转债投资
        * */
        String TRANSFER_DO_BIDDING_URL = ROOT_URL+"/api/mobile/transfer/doBidding.json";
        /*
        * 投资列表
        * */

        String DO_BIDDING_LIST_URL = ROOT_URL+"/api/mobile/bidding/list.json";

        /*
        * 计划还款
        * */
        String REPAYMENT_PLAN_URL = ROOT_URL+"/api/mobile/project/repaymentPlan.json";

        /*
        * 账户信息
        * */

        String GET_ACCOUNT_MSG_URL = ROOT_URL+"/api/mobile/investor/account.json";

        /*
        * 天天盈接口
        * */
        String GET_EDM_OVERVIEW_MSG_URL = ROOT_URL+"/api/mobile/edm/info.json";

        /*
        * 天天盈买入
        * */

        String EDM_OVERVIEW_MSG_BUY_IN_URL = ROOT_URL+"/api/mobile/edm/project/list.json";

         /*
        * 天天盈转出
        * */

        String EDM_OVERVIEW_MSG_TURN_OUT_URL = ROOT_URL+"/api/mobile/edm/exit.json";
        /*
        * 获取项目详情
        * */
        String PROJECT_DETAIL_URL = ROOT_URL+"/api/mobile/project/info.json";

        /*
        * 获取转债项目详情接口
        * */
        String TRANSFORM_PROJECT_DETAIL_URL = ROOT_URL+"/api/mobile/transfer/info.json";
         /*
        * 获取银行卡列表
        * @params userId
        * @params useType  W提现 Q快捷支付和提现
        * */
        String BANK_LIST_URL = ROOT_URL+"/api/mobile/bankcard/investor/list.json";
        /*
        * 充值
        * */
        //String RECHARGE_URL = ROOT_URL+"/api/mobile/fastpay/recharge.json";
        String RECHARGE_URL = ROOT_URL+"/api/mobile/fastpay/investor/recharge.json";

        /*
        * >=2.2.0 充值接口
        * */
        String MORE_VERSION_RECHARGE_URL = ROOT_URL+"/api/mobile/fastpay/investor/recharge.json";
         /*
        * 查询充值结果
        * */
        String RECHARGE_RESULT_URL = ROOT_URL+"/api/mobile/fastpay/investor/deposit/queryOne.json";
         /*
        *
        * 提现
        * */
        String WITHDRAWALS_URL = ROOT_URL+"/api/mobile/withdraw/request.json";
        /*
        * banner
        * */
        String BANNER_URL = ROOT_URL+"/api/mobile/home/banner/list.json";

        /*
        * 二维码
        * */

        String QR_CODE_URL = ROOT_URL+"/api/mobile/investor/qrCode.json";

        /*
        * 根据银行卡号查询信息
        * */

        String FIND_BANK_NAME_BY_NO = ROOT_URL+"/api/mobile/bankcard/findBankNameByNo.json";
        /*
        * 发送验证码
        * */

        String SEND_BANK_CARD_EXAM_CODE_URL = ROOT_URL+"/api/mobile/bankcard/investor/bindCardBank.json";

        /*
        * 银行卡绑定完成提交
        * */

        String BANK_CARD_BIND_OK_URL = ROOT_URL+"/api/mobile/bankcard/investor/bindCardBank/confirm.json";
        /*
        * 检查是否实名认证
        * */
        String HAS_REAL_NAME_URL = ROOT_URL+"/api/mobile/investor/checkIdAuthFlag.json";
         /*
        * 实名认证
        * */

        String REAL_NAME_URL = ROOT_URL+"/api/mobile/investor/idAuth.json";

        /*
        * 忘记密码之短信验证码
        * */

        String FORGET_PASSWORD_GET_MSG_CODE_URL = ROOT_URL+"/api/mobile/phone/sendCode.json";
        /*
        * 设置新的登录密码
        * */
        String FORGET_PASSWORD_SETTING_NEW_PASS_URL = ROOT_URL+"/api/mobile/investor/resetPasswd.json";

        /*
        * 投资券
        * ,@"userId",@"",@"status"
        * */
        String COUPONS_URL = ROOT_URL+"/api/mobile/coupon/investor/list.json";
        /*
        * 选择投资券
        * ticketType
        * userId
        * */
        String CHOOSE_COUPONS_URL = ROOT_URL+"/api/mobile/coupon/investor/project/list.json";
        /*
        * 优惠码兑换投资券
        * userId,@"userId",
                         codeText,@"couponCode", nil];
        * */
        String EXCHANGE_URL = ROOT_URL+"/api/mobile/coupon/investor/bindCoupon.json";

       /*

               *意见反馈

               * */
        String INVESTOR_ADVICEFEEDBACK_URL = ROOT_URL+"/api/mobile/feedback/post.json";
        /*

              *修改登录密码

              * */
        String INVESTOR_UPDATELOGINPASSWD_URL = ROOT_URL+"/api/mobile/investor/modifyLoginPasswd.json";
        /*

             *投资记录

             * */
        String INVESTOR_INVESTMENTRECORD_URL = ROOT_URL+"/api/mobile/investment/investor/list.json";
        /*
         *投资者的投资记录（待结标记录）
         * */
        String INVESTOR_INVESTMENTWAITENDRECORD_URL = ROOT_URL+"/api/mobile/bidding/investor/list.json";

        /*
                 *
                 * 验证登录密码是否正确
                 * */
        String INVESTOR_CHECKPASSWD_URL = ROOT_URL+"/api/mobile/investor/checkLogiPasswd.json";
        /*
                *
                * 验证登录密码和短信验证码是否正确
                * */
        String INVESTOR_CHECK_PASSWD_AND_SMSCODE_URL = ROOT_URL+"/api/mobile/investor/validateSmsAndPwd.json";
        /*
                 *
                 * 设置交易密码
                 * */
        String INVESTOR_UPDATETRADEASSWD_URL = ROOT_URL+"/api/mobile/investor/setTxnPwd.json";

        /*
        * 获取用户信息
        * */
        String USER_INFO_URL = ROOT_URL+"/api/mobile/edm/info.json";
        /*
      * 修改用户的交易密码
      * */
        String UPDATE_USER_TRADEPASSWORD_URL = ROOT_URL+"/api/mobile/investor/findTxnPwd.json";
        /*
     * 修改用户的交易密码时的短信验证码的获取
     * */
        String UPDATE_USER_TRADEPASSWORD_GET_EXAM_CODE_URL = ROOT_URL+"/api/mobile/phone/investor/sendCode.json";
        /*
         * 在线升级
         * */
        String VERSION_UPDATE_URL = ROOT_URL+"/api/mobile/upgrade.json";

        /*
        * 服务协议：
        * */
        String SERVER_DETAIL = ROOT_URL+"/views/useragreement.html";
        /*
        * 隐私条款：
        * */
        String PRIVACY_DETAIL = ROOT_URL+"/views/useRemind.html";

        /*
        *天天盈常见问题
        * */

        String QUESTION_DETAIL_URL = ROOT_URL+"/views/question.html";
        /*
        *投资券攻略
        * */



        String RAIDERS_DETAIL_URL = ROOT_URL+"/views/raiders.html";

          /*
        *调查问卷
        * */

        String RISK_QUESTION_URL = ROOT_URL+"/views/riskQuestion.html";
        /*
        *转出说明
        * */

        String TURN_OUT_URL = ROOT_URL+"/views/turnout_description.html";

        /*
        * bug地址
        * */
        String BUG_PATH = ROOT_URL+"/api/mobile/debug/postlog.json";

        /*
        * 重置 securetKey(POST): /resetSecuretKey.json
        * */

        String RESET_SECURE_KEY_PATH = ROOT_URL+"/api/mobile/investor/resetSecuretKey.json";

        /*
        * log文件夹目录
        * */
        String LOG_PATH = SDCARD_ROOT_PATH+"Log/";

        /*
        投资者的充值记录
         */
        String INVESTOR_RECHARGE_RECORD=ROOT_URL+"/api/mobile/fastpay/investor/list.json";
        /*
        投资者的提现记录
         */
        String INVESTOR_WITHDRAW_RECORD=ROOT_URL+"/api/mobile/withdraw/investor/list.json";
        /*
        *安全退出
         */
        String SAFETY_LOGOUT=ROOT_URL+"/api/mobile/logout/post.json";

        /*
        * pos充值前信息验证
        * */

        String INVESTOR_PERMISSION_CHECK_URL = ROOT_URL+"/api/mobile/pdspay/check.json";

        /*
        * 获取商户调用的SDK参数
        * */
        String GET_BUSINESS_PARAMS_URL = ROOT_URL+"/api/mobile/pdspay/genOrderForm.json";
        /*
        * 取消订单
        * */
        String CANCEL_ORDER_URL = ROOT_URL+"/api/mobile/pdspay/cancleOrderForm.json";
        /*
        * 确认订单
        * */
        String CONFIRM_ORDER_URL = ROOT_URL+"/api/mobile/pdspay/confirmOrderForm.json";

        /*
        * 推送注册
        * */
        String PULL_REGISTER_URL = ROOT_URL+"/api/mobile/register/device/post.json";


        /*
        * 推送设置
        * */

        String PUSH_SETTING_URL = ROOT_URL+"/api/mobile/investor/pushSwitch.json";

        /*
        * websocket回调接口
        * */
        String WEB_SOCKET_CALLBACK = ROOT_URL+"/api/mobile/register/device/upReciveTime.json";

        /*
        * websocket正式环境的地址
        * */
        String WEB_SOCKET_URL = "ws://ypns.yeepbank.com/notice/";
        /*
        websocket测试环境的地址
        * */
        //String WEB_SOCKET_URL = "ws://ws.yeepbank.xyz/notice/";

        //抢券列表的地址
        String BATTLE_LIST_URL =ROOT_URL+ "/api/mobile/coupon/list.json";

        //抢券地址
        String BATTLE_URL =ROOT_URL+ "/api/mobile/coupon/investor/receiveCoupon.json";
    }



    public interface PARAMS{
        /*
        * 渠道号
        * */
        String FUND_CHANNEL_ID = "ANDROID-TEST";
        /*
        *签名秘钥
        * */
        String SIGN_KEY = "ENFQ9B7ZY2ZYSSS4KK5XMNQZDQNKB5N5";
 /*       *//*
        * 加密秘钥
        * *//*
        String ENCRYPT_KEY = "7IUPQC325AVYNXCM";*/

        /*
        * 版本号
        * */
        String VERSION_CODE = "";

        /*
        * 每页条目数
        * */
        int PAGE_SIZE = 10;


       }

    public interface CMD{

        /*
        * 检查APP版本
        * */
        int CHECK_APP_VERSION_CODE = 0;
        /*
        * 最新APP无需更新
        * */
        int IS_NEWEST = 1;
        /*
        * 发现新版本
        * */
        int IS_OLD = 2;

         /*
        * 下载APP
        * */
         int DOWNLOAD_APP = 3;
        /*
        * 检查APP版本显示的Loading
        * */
        int APP_CHECK_LOADING = 4;
        /*
        * 下载显示Loading
        * */
        int DOWN_LOADING = 5;

        /*
        * 等待Loading
        * */
        int NORMAL_LOADING = 6;
        /*
       * 改变下载进度
       * */
        int CHANGE_DOWNLOAD_PERCENT = 7;
        /*
      * 安装apk
      * */
        int INSTALL_APK = 8;
        /*
     * 提示错误信息
     * */
        int ALERT_ERROR_MSG = 9;

        /*
        * 提示对话框
        * */

        int ALERT_DIALOG_APP_UPDATE = 12;

        /*
        * WIFI连接成功后下载
        * */

        int DOWNLOAD_UNDER_WIFI = 13;

        /*
        * 登陆成功
        * */

        int LOGIN_SUCCESS = 14;
        /*
        * 注册成功
        * */

        int REGISTER_SUCCESS = 15;
        /*
        * 登录过期
        * */
        int LOGIN_OUT_TIME = 16;

        /*
        * 登录过期FILTER_ACTION
        * */
        String LOGIN_OUT_TIME_ACTION = "LOGIN_OUT_TIME_ACTION";

        /*
       * 关闭登录框
       * */
        int LOGIN_FRAME_CLOSED = 0X111113;
         /*
        * 加载fragment数据
        * */

        int SHOW_FRAGMENT = 0X111111;

         /*
        * 加载网络断开页面
        * */

        int NET_ERROR = 0X111112;

        /*
        * 版本提示框
        * */
        int NEW_VERSION = 0x111113;
         /*
        * 客服电话
        * */
        String SERVICE_TELNUMBER_ONE="4008436868";
        String SERVICE_TELNUMBER_TWO="400-843-6868";

    }

    public interface COMMON{
        int WIFI = 10;
        int MOBILE = 11;

        /*下拉刷新状态*/
        int NULL = 0;//正常状态
        int PULL_TO_REFRESH = 1;//下拉可刷新
        int RELEASE_TO_REFRESH = 2;//释放立即刷新
        int REFRESH = 3;//正在刷新
        int REFRESH_COMPLETE = 5;//刷新完成

        int NO_MORE = 6;//无最新数据

        String WX_APP_ID = "wxb6bcfb00d67cea38";


        String CHARSET_UTF8 = "UTF-8";

        String CHARSET_GBK = "GBK";

        String SIGN_METHOD_MD5 = "MD5";

        String SIGN_METHOD_HMAC = "HMAC";



        String DES_KEY = "$apr1$G7";

        int APP_VERSION = 200;

        String APK_NAME = "wallstreet_android_pro.apk";

        String RESULT_CALL_BACK_CLASS_NAME = "com.yeepbank.android.activity.account.OrderResultActivity";
        String CANCEL_CALL_BACK_CLASS_NAME = "com.yeepbank.android.activity.account.CancelOrderActivity";
    }

    public static Investor currentUser;

    public static ArrayList<Banner> bannerList;

    /**
     * 获取软件版本号
     *
     * @param context
     * @return
     */
    public static String getVersionCode(Context context) {
        String versionCode = "";
        try {
            // 获取软件版本号，对应AndroidManifest.xml下android:versionCode
            PackageInfo packInfo = context.getPackageManager().getPackageInfo("com.yeepbank.android", 0);
            versionCode = packInfo.versionName;
            String versionName = packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /*
    * 记录当前选中的投资券
    * */
    public static String couponId = null;
}
