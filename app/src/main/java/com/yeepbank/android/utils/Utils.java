package com.yeepbank.android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.view.View;
import com.yeepbank.android.Cst;
import com.yeepbank.android.R;
import com.yeepbank.android.model.Banner;
import com.yeepbank.android.model.SocketMsg;
import com.yeepbank.android.model.user.Investor;
import com.yeepbank.android.widget.gridpasswordview.Util;

import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Utils {

    public SharedPreferences preferences;
    public  String logStringCache = "";
    public  DecimalFormat dFormat;
    public  DecimalFormat thousandFormat;
    public  SimpleDateFormat sdf;
    private View view;
    private static Utils utils;

    public static Utils getInstances(){
        synchronized (Utils.class){
            if(utils == null){
                utils = new Utils();
            }
            return utils;
        }
    }

    private Utils(){
        dFormat = new DecimalFormat("####0.00");
        thousandFormat = new DecimalFormat("#,##0.00");
        sdf = new SimpleDateFormat("yyyy-MM-dd");
    }
    // 获取ApiKey
    public static String getMetaValue(Context context, String metaKey) {
        Bundle metaData = null;
        String apiKey = null;
        if (context == null || metaKey == null) {
            return null;
        }
        try {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                apiKey = metaData.getString(metaKey);
            }
        } catch (NameNotFoundException e) {

        }
        return apiKey;
    }

    public static List<String> getTagsList(String originalText) {
        if (originalText == null || originalText.equals("")) {
            return null;
        }
        List<String> tags = new ArrayList<String>();
        int indexOfComma = originalText.indexOf(',');
        String tag;
        while (indexOfComma != -1) {
            tag = originalText.substring(0, indexOfComma);
            tags.add(tag);

            originalText = originalText.substring(indexOfComma + 1);
            indexOfComma = originalText.indexOf(',');
        }

        tags.add(originalText);
        return tags;
    }

    public static String getLogText(Context context) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sp.getString("log_text", "");
    }

    public static void setLogText(Context context, String text) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor editor = sp.edit();
        editor.putString("log_text", text);
        editor.commit();
    }

    public static Map<String, String> encodeParameters(Map<String, String> params, String paramsEncoding) {

        Map<String,String> newParams = new HashMap<String,String>();
        for (String key:params.keySet()){
            String value = params.get(key);
            try {
                newParams.put(URLEncoder.encode(key, paramsEncoding),URLEncoder.encode( value, paramsEncoding));
            } catch (UnsupportedEncodingException var6) {
                throw new RuntimeException("Encoding not supported: " + paramsEncoding, var6);
            }
        }
        params.clear();
        params = null;
        return newParams;
    }

    /*
    * 验证手机号
    * */
    public static boolean match(String phone){
        String reg = "^1+[3578]+\\d{9}";
        return phone.trim().length() == 11 && phone.matches(reg);
    }

    /*
    *
    * 是否用过引导页保存到缓存
    * */

    public boolean isUsed(Context context) {

        preferences = context.getSharedPreferences("isUsed",0);
        boolean isUsed = preferences.getBoolean("isUsed", false);

        return isUsed;

    }

    public void setUsed(Context context) {

        preferences = context.getSharedPreferences("isUsed",0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isUsed",true);
        editor.commit();
    }

    public void removeUsed(Context context){
        preferences = context.getSharedPreferences("isUsed",0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("isUsed");
        editor.commit();
    }

    /*
    * 保存升级提示到缓存
    * */

    public void putUpdateInfoToPreference(Context context,String key,boolean isUpdated){
        preferences = context.getSharedPreferences("isUpdated",0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, isUpdated);
        editor.commit();
    }
     /*
    * 保存评分提示到缓存
    * */

    public void putFirstInfoToPreference(Context context,String key,boolean defValue){
        preferences = context.getSharedPreferences("isSave",0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, defValue);
        editor.commit();
    }
    /*
    * 从缓存中获得保存的评分*/
    public boolean getFistInfoFromPreference(Context context,String key){
        preferences = context.getSharedPreferences("isSave",0);
        return preferences.getBoolean(key,false);
    }
    public void putSceondInfoToPreference(Context context,String key,boolean defValue){
        preferences = context.getSharedPreferences("isSave",0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, defValue);
        editor.commit();
    }
    /*
    * 从缓存中获得保存的评分*/
    public boolean getSceondInfoFromPreference(Context context,String key){
        preferences = context.getSharedPreferences("isSave",0);
        return preferences.getBoolean(key,false);
    }
     /*
    * 删除升级提示记录
    * */

    public void putUpdateInfoToPreference(Context context,String key){
        preferences = context.getSharedPreferences("isUpdated",0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, false);
        editor.commit();
    }

    /*
    * 升级提示到缓存
    * */

    public boolean getUpdateInfoToPreference(Context context,String key){
        preferences = context.getSharedPreferences("isUpdated",0);
        return preferences.getBoolean(key, false);
    }

    public void removeUpdateInfo(Context context){
        preferences = context.getSharedPreferences("isUpdated",0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("isUpdated");
        editor.commit();
    }
    /*
    * 下拉刷新事件保存到缓存
    * */

    public void putToSharedPreference(Context context,long times){
        preferences = context.getSharedPreferences("update_time",0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("update_time", times);
        editor.commit();
    }
    /*
    * 从缓存中获取下拉刷新时间
    * */

    public String getUpdateTimeFromSharedPreference(Context context){
        preferences = context.getSharedPreferences("update_time",0);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long times = preferences.getLong("update_time", 0);
        if(times == 0){
            return "";
        }
        return simpleDateFormat.format(new Date(times));
    }

    public void removeUpdateTime(Context context){
        preferences = context.getSharedPreferences("update_time",0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("update_time");
        editor.commit();
    }

    /*
    * 从缓存中获取在使用充值银行卡
    * */

    public String getBankCardOfRechargeFromSharedPreference(Context context){
        preferences = context.getSharedPreferences("CardIdOfRecharge",0);
        String cardId = preferences.getString("CardIdOfRecharge", "");
        return cardId;
    }

     /*
    * 用户使用充值银行卡保存到缓存
    * */

    public void putBankCardOfRechargeToSharedPreference(Context context,String cardId){
        if(context == null){
            return;
        }
        preferences = context.getSharedPreferences("CardIdOfRecharge",0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("CardIdOfRecharge", cardId);
        editor.commit();

    }

    public void removeBankCardOfRecharge(Context context){
        preferences = context.getSharedPreferences("CardIdOfRecharge",0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("CardIdOfRecharge");
        editor.commit();
    }


     /*
    * 从缓存中获取在使用提现银行卡
    * */

    public String getBankCardOfWithDrawalFromSharedPreference(Context context){
        preferences = context.getSharedPreferences("CardIdOfWithDrawal",0);
        String cardId = preferences.getString("CardIdOfWithDrawal", "");
        return cardId;
    }

     /*
    * 用户使用提现银行卡保存到缓存
    * */

    public void putBankCardOfWithDrawalToSharedPreference(Context context,String cardId){
        if(context == null){
            return;
        }
        preferences = context.getSharedPreferences("CardIdOfWithDrawal",0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("CardIdOfWithDrawal", cardId);
        editor.commit();

    }

    public void removeBankCardOfWithDrawal(Context context){
        preferences = context.getSharedPreferences("CardIdOfWithDrawal",0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("CardIdOfWithDrawal");
        editor.commit();
    }
      /*
    * 将上次登陆的用户名保存到缓存
    * */

    public void putUserNameToSharedPreference(Context context,String userName){
        if(context == null){
            return;
        }
        preferences = context.getSharedPreferences("UserName",0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("UserName", userName);
        editor.commit();

    }
    /*
    * 从缓存中获取上次登陆用户的用户名
    * */

    public String getUserNameFromSharedPreference(Context context){
        preferences = context.getSharedPreferences("UserName", 0);
        String userName = preferences.getString("UserName", "");
        return userName;
    }
     /*
    * 投资FragmentTab提醒项目保存到缓存
    * */

    public void putInvestFragmentTabRedDocToSharedPreference(Context context,boolean flag){
        if(context == null){
            return;
        }
        preferences = context.getSharedPreferences("investFragmentTab",0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("investFragmentTab", flag);
        editor.commit();

    }
    /*
    * 抢券FragmentTab提醒项目保存到缓存
    * */

    public void putBattleCouponFragmentTabRedDocToSharedPreference(Context context,boolean flag){
        if(context == null){
            return;
        }
        preferences = context.getSharedPreferences("battleCouponFragmentTab",0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("battleCouponFragmentTab", flag);
        editor.commit();

    }
    /*
    * 投资MeFragmentTab提醒项目保存到缓存
    * */
    public void putMeFragmentTabRedDocToSharedPreference(Context context,boolean flag){
        if(context == null){
            return;
        }
        preferences = context.getSharedPreferences("meFragmentTab",0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("meFragmentTab", flag);
        editor.commit();
    }
    /*
    * 投资FragmentTab提醒项目从缓存中读取
    * */

    public boolean getInvestFragmentTabRedDocToSharedPreference(Context context){
        preferences = context.getSharedPreferences("investFragmentTab",0);
        return preferences.getBoolean("investFragmentTab",false);
    }
     /*
    * 抢券FragmentTab提醒项目从缓存中读取
    * */

    public boolean getBattleCouponFragmentTabRedDocToSharedPreference(Context context){
        preferences = context.getSharedPreferences("battleCouponFragmentTab",0);
        return preferences.getBoolean("battleCouponFragmentTab",false);
    }
    /*
    * meFragmentTab提醒项目从缓存中读取
    * */

    public boolean getMeFragmentTabRedDocToSharedPreference(Context context){
        preferences = context.getSharedPreferences("meFragmentTab",0);
        return preferences.getBoolean("meFragmentTab",false);
    }


    /*
    * 我的账户Fragment提醒项目保存到缓存
    * */

    public void putMeFragmentRedDocToSharedPreference(Context context,HashMap<String,ArrayList<SocketMsg>> socketMsgMap){
        if(context == null || Cst.currentUser == null){
            return;
        }

        try {
            preferences = context.getSharedPreferences("meFragment",0);
            SharedPreferences.Editor editor = preferences.edit();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(socketMsgMap);
            String meFragmentRedStr = new String(Base64.encode(bos
                    .toByteArray(), 0));
            editor.putString("meFragment->"+Cst.currentUser.investorId,meFragmentRedStr);
            editor.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    /*
    * 我的账户Fragment提醒项目从缓存中读取
    * */

    public HashMap<String,ArrayList<SocketMsg>> getMeFragmentRedDocFromSharedPreference(Context context){

        if (Cst.currentUser == null){
            return null;
        }

        preferences = context.getSharedPreferences("meFragment",0);
        String meFragmentRedStr = preferences.getString("meFragment->"+Cst.currentUser.investorId,null);
        if(meFragmentRedStr != null){
            byte[] base64 = Base64.decode(meFragmentRedStr.getBytes(), 0);
            ByteArrayInputStream bais = new ByteArrayInputStream(base64);
            try {
                ObjectInputStream bis = new ObjectInputStream(bais);
                return (HashMap<String,ArrayList<SocketMsg>>) bis.readObject();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;


    }




     /*
    * 登录用户保存到缓存
    * */

    public void putInvestorToSharedPreference(Context context,Investor investor){
        if(context == null){
            return;
        }
        preferences = context.getSharedPreferences("investor",0);
        SharedPreferences.Editor editor = preferences.edit();
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(investor);
            String investorStr = new String(Base64.encode(bos
                    .toByteArray(), 0));

            editor.putString("investor",investorStr);
            editor.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

     /*
    * 从缓存中清除登录用户
    * */

    public void removeInvestorFromSharedPreference(Context context){
        preferences = context.getSharedPreferences("investor",0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("investor");
        editor.commit();

    }



    /*
    * 登录用户从缓存中获取
    * */

    public Investor getInvestorFromSharedPreference(Context context){
        preferences = context.getSharedPreferences("investor",0);
        String investorStr = preferences.getString("investor",null);
        if(investorStr != null){
            byte[] base64 = Base64.decode(investorStr.getBytes(), 0);
            ByteArrayInputStream bais = new ByteArrayInputStream(base64);
            try {
                ObjectInputStream bis = new ObjectInputStream(bais);
                return (Investor) bis.readObject();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;

    }


    /*
    * 获取是否接收通知
    * */
    public boolean getAcceptPushMsg(Context context,String key) {

        preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return preferences.getBoolean(key, true);
    }

    /*
    * 设置是否接收通知
    * */
    public void setAcceptPushMsg(Context context,String key, boolean CMD) {
        preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor editor = preferences.edit();
        editor.putBoolean(key, CMD);
        editor.commit();
    }
    /**
     * * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs) * * @param
     * context
     */
    public void cleanSharedPreference(Context context) {
        removeBankCardOfRecharge(context);
        removeBankCardOfWithDrawal(context);
        removeInvestorFromSharedPreference(context);
        removeUpdateInfo(context);
        removeUpdateTime(context);
        removeUsed(context);
    }


    /*
    * 转化日期格式至日
    * */
    public String formatDate(Date date){
        return sdf.format(date);
    }

    public String format(double value){
            BigDecimal bd = new BigDecimal(Double.toString(value));
            return String.valueOf(bd.setScale(2, BigDecimal.ROUND_DOWN).toPlainString());
    }
    public String formatUp(double value){
        BigDecimal bd = new BigDecimal(value);
        return String.valueOf(bd.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
    }

    /*
    * flag  作为转债标识，如果是转债一律使用元作单位
    * */
    public String formatWithUnit(double value,int flag){

        if(value < 10000 || flag == 0){
            return dFormat.format(value)+"元";

        }else {
            return dFormat.format(value/10000)+"万";
        }

    }

    public String thousandFormat(double value){
        return thousandFormat.format(value);
    }

    public String thousandFormatWithUnit(double value){

        return thousandFormat.format(value)+"元";
    }


    public synchronized  void loadImageResources(View imageView, final Banner banner) {
        view = imageView;
        new Thread(){
            @Override
            public void run() {
                loadData(banner.bannerImageUrl);
                super.run();
            }
        }.start();

    }

    private void loadData(final String url){
        new Thread(){
            @Override
            public void run() {
                try {
                    URL uri = new URL(url);
                    HttpURLConnection conn = null;
                    conn = (HttpURLConnection) uri.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setReadTimeout(5000);

                    InputStream input = conn.getInputStream();
                    byte[] data = readInputStream(input);
                    Message msg = msgHandler.obtainMessage();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    msg.obj = bitmap;
                    msg.what = 0;
                    msgHandler.sendMessage(msg);
                    input.close();


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    private byte[] readInputStream(InputStream input) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            int len = 0;
            while((len = input.read(buffer)) != -1) {
                output.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output.toByteArray();
    }

    private Handler msgHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    view.setBackground(new BitmapDrawable((Bitmap) msg.obj));
            }
        }
    };

    public boolean bannerIsExist(String fileName){
        File bannerFile = new File(Cst.URL.BANNER_PATH,fileName);
        return bannerFile.exists();

    }

    /*
    * 从SD卡中拿到banner
    * */
    public InputStream getBanner(String fileName){

        File bannerFile = new File(Cst.URL.BANNER_PATH,fileName);
        if(bannerFile.exists()){
            try {
                BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(bannerFile));
                return inputStream;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;

    }

    /*
    * 将banner保存到SD卡
    * */
    public void putBanner(InputStream inSream,String fileName){
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            try {

                if (inSream != null) {
                    File pathFile = new File(Cst.URL.BANNER_PATH);
                    if (!pathFile.exists()) {
                        pathFile.mkdirs();
                    }
                    File tempFile = new File(Cst.URL.BANNER_PATH + fileName);
                    if (tempFile != null && tempFile.exists()) {
                        tempFile.delete();
                    }
                    tempFile.createNewFile();
                    BufferedInputStream in = new BufferedInputStream(inSream);
                    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(tempFile));
                    int read = 0;
                    byte[] buffer = new byte[1024];
                    while ((read = in.read(buffer)) != -1) {
                        out.write(buffer, 0, read);

                    }
                    out.flush();
                    inSream.close();
                    in.close();
                    out.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {

            }
        }else{
            Message msg = msgHandler.obtainMessage();
            msg.what = Cst.CMD.ALERT_ERROR_MSG;
            msg.obj = "SD卡不可用";
            msgHandler.sendMessage(msg);
        }

    }




    public String ToDBC(String input) {
           char[] c = input.toCharArray();
           for (int i = 0; i< c.length; i++) {
                   if (c[i] == 12288) {
                         c[i] = (char) 32;
                         continue;
                       }if (c[i]> 65280&& c[i]< 65375)
                          c[i] = (char) (c[i] - 65248);
                   }
           return new String(c);
    }


    /*
    * 获取设备ID
    * */

    public String getDeviceId(Context context){
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }


    /*
  * 将Log保存到SD卡
  * */
    public void putLogToSD(String text){
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            try {
                File pathFile = new File(Cst.URL.LOG_PATH);
                if (!pathFile.exists()) {
                    pathFile.mkdirs();
                }
                File tempFile = new File(Cst.URL.LOG_PATH + "log.txt");
                if (tempFile == null || tempFile.exists()) {
                    tempFile.createNewFile();
                }
                BufferedWriter out = new BufferedWriter(new FileWriter
                        (Cst.URL.LOG_PATH + "log.txt",true));
                out.write(text);
                out.write("\r\n");
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }else{
            Message msg = msgHandler.obtainMessage();
            msg.what = Cst.CMD.ALERT_ERROR_MSG;
            msg.obj = "SD卡不可用";
            msgHandler.sendMessage(msg);
        }

    }

     /*
    * 精确计算加
    * */

    public double add(double... params){
        if(params == null || params.length < 1)return 0;

        BigDecimal[] bigDecimals = new BigDecimal[params.length];
        for (int i = 0; i < params.length; i++) {
            bigDecimals[i] = new BigDecimal(Double.toString(params[i]));
        }
        BigDecimal result = bigDecimals[0];
        for (int i = 1; i < bigDecimals.length; i++) {
            result = result.add(bigDecimals[i]);
        }
        return result.doubleValue();
    }

    /*
    * 精确计算减法
    * */

    public double sub(double... params){
        if(params == null || params.length < 1)return 0;

        BigDecimal[] bigDecimals = new BigDecimal[params.length];
        for (int i = 0; i < params.length; i++) {
            bigDecimals[i] = new BigDecimal(Double.toString(params[i]));
        }
        BigDecimal result = bigDecimals[0];
        for (int i = 1; i < bigDecimals.length; i++) {
            result = result.subtract(bigDecimals[i]);
        }
        return result.doubleValue();
    }

     /*
    * 精确计算乘法
    * */

    public double mul(double... params){
        if(params == null || params.length < 1)return 0;

        BigDecimal[] bigDecimals = new BigDecimal[params.length];
        for (int i = 0; i < params.length; i++) {
            bigDecimals[i] = new BigDecimal(Double.toString(params[i]));
        }
        BigDecimal result = bigDecimals[0];
        for (int i = 1; i < bigDecimals.length; i++) {
            result = result.multiply(bigDecimals[i]);
        }
        return result.doubleValue();
    }

     /*
    * 精确计算除法
    * */

    public double div(double... params){
        if(params == null || params.length < 1)return 0;

        BigDecimal[] bigDecimals = new BigDecimal[params.length];
        for (int i = 0; i < params.length; i++) {
            bigDecimals[i] = new BigDecimal(Double.toString(params[i]));
        }
        BigDecimal result = bigDecimals[0];
        for (int i = 1; i < bigDecimals.length; i++) {
            result = result.divide(bigDecimals[i], 5);
        }
        return result.doubleValue();
    }

    public void recycle(){
        if(utils != null)utils = null;
    }

    /*
    * 用户状态发生改变，保存各个推送状态
    * */
    public void updatePushState(Context mContext){
        setAcceptPushMsg(mContext, mContext.getString(R.string.ACCESS_ALL), Cst.currentUser.pushAllFlag);
        setAcceptPushMsg(mContext, mContext.getString(R.string.ACCESS_NEW_PROJECT), Cst.currentUser.pushNewProjectFlag);
        setAcceptPushMsg(mContext, mContext.getString(R.string.ACCESS_DEPOSIT), Cst.currentUser.pushDepositWithdrawFlag);
        setAcceptPushMsg(mContext, mContext.getString(R.string.ACCESS_BIDDING), Cst.currentUser.pushBiddingRelatedFlag);
        setAcceptPushMsg(mContext, mContext.getString(R.string.ACCESS_GRANT_COUPON), Cst.currentUser.pushGrantCouponFlag);
    }

}



