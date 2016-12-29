package com.yeepbank.android.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.*;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.*;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
//import com.nineoldandroids.animation.ObjectAnimator;
import com.igexin.sdk.PushManager;
import com.lib.signkey.SignKeys;
import com.android.volley.VolleyError;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yeepbank.android.*;

import com.yeepbank.android.activity.business.HomeActivity;
import com.yeepbank.android.activity.fragment.BattleCouponCenterFragment;
import com.yeepbank.android.activity.fragment.ErrorFragment;
import com.yeepbank.android.activity.fragment.MeFragment;
import com.yeepbank.android.activity.setting.AboutYeepActivity;
import com.yeepbank.android.activity.setting.CouponsActivity;
import com.yeepbank.android.activity.setting.RealNameAuthenticationActivity;
import com.yeepbank.android.activity.user.WebActivity;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.model.AppInfo;
import com.yeepbank.android.model.Web;
import com.yeepbank.android.request.user.LogOutRequest;
import com.yeepbank.android.response.user.LogOutResponse;
import com.yeepbank.android.server.AppUpdateServer;
import com.yeepbank.android.server.LoginAndRegisterServer;
import com.yeepbank.android.utils.*;

import java.io.Serializable;
import java.util.List;
import java.util.Timer;
import java.util.zip.Inflater;


/**
 * Created by WW on 2015/8/22.
 */
public abstract class BaseActivity extends Activity {

    protected  Activity mContext;
    private PopupWindow popupWindow;
    private long exitTimes = 0;
    private View navigationBar;
    private TextView labelText;
    private LayoutInflater inflater;
    private static boolean once = true;
    public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;
    protected Intent resultData;//startActivityForResult 返回结果
    private IWXAPI iwxapi;
    public LoadDialog loadding;
    private boolean isNetError = false;
    private static boolean PUSH_HAS_REGISTERED = false;


    private int updateCmd;
    public static boolean isNetErrorNow = false;//判断当前网络是否异常
    private  LoginAndRegisterServer server;
    private LoadDialog msgDialog;
    private  LoadDialog reLoginDialog;
    private AppUpdateServer appUpdateServer;
    public boolean LoginFrameShowing = false;//判断如果当前页面弹出登录框  不能注销该页面的登录超时监听

    private AppInfo appInfo = null;

    static {
        System.loadLibrary("signkey");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        //if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            //透明导航栏
//            (getActivity()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        //}

         /*
        * 注册推送，只需要注册一次
        * */
        if(once){
            once = !once;
            Cst.currentUser = Utils.getInstances().getInvestorFromSharedPreference(mContext);
            PushManager.getInstance().initialize(this.getApplicationContext());
            Cst.SIGN_METHOD_SECRET = SignKeys.getSignKeyFromC().substring(0, SignKeys.getSignKeyFromC().lastIndexOf("|"));
            Cst.KEY = SignKeys.getSignKeyFromC().substring(SignKeys.getSignKeyFromC().lastIndexOf("|")+1,SignKeys.getSignKeyFromC().length());
        }

        loadding = getLoadding();

        ActivityStacks.getInstances().push(mContext);
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(getLayoutResources(), null);
        setContentView(contentView);
        initView();
        fillData();
        navigationBar = getNavigationBar();
        initNavigationBar(navigationBar);

    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
//            View v = getCurrentFocus();
//            if (isShouldHideInput(v, ev)) {
//
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                if (imm != null) {
//                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//                }
//            }
//            return super.dispatchTouchEvent(ev);
//        }
//        // 必不可少，否则所有的组件都不会有TouchEvent了
//        if (getWindow().superDispatchTouchEvent(ev)) {
//            return true;
//        }
//        return onTouchEvent(ev);
//    }
//
//    public  boolean isShouldHideInput(View v, MotionEvent event) {
//        if (v != null && (v instanceof EditText)) {
//            int[] leftTop = { 0, 0 };
//            //获取输入框当前的location位置
//            v.getLocationInWindow(leftTop);
//            int left = leftTop[0];
//            int top = leftTop[1];
//            int bottom = top + v.getHeight();
//            int right = left + v.getWidth();
//            if (event.getX() > left && event.getX() < right
//                    && event.getY() > top && event.getY() < bottom) {
//                // 点击的是输入框区域，保留点击EditText的事件
//                return false;
//            } else {
//                return true;
//            }
//        }
//        return false;
//    }


    //    @Override
//    public void onTrimMemory(int level) {
//        switch (level){
//            case TRIM_MEMORY_RUNNING_CRITICAL:
//                Log.e("Memory","Memory:应用处于运行状态并且不会被杀掉, 设备使用的内存比较低, 系统级会杀掉一些其它的缓存应用");
//                break;
//            case TRIM_MEMORY_RUNNING_LOW:
//                Log.e("Memory","Memory:应用处于运行状态并且不会被杀掉, 设备可以使用的内存非常低, 可以把不用的资源释放一些提高性能(会直接影响程序的性能)");
//                break;
//            case TRIM_MEMORY_BACKGROUND:
//                Log.e("Memory","Memory:系统处于低内存的运行状态中并且你的应用处于缓存应用列表的初级阶段.  虽然你的应用不会处于被杀的高风险中, 但是系统已经开始清除缓存列表中的其它应用," +
//                        " 所以你必须释放资源使你的应用继续存留在列表中以便用户再次回到你的应用时能快速恢复进行使用.");
//                break;
//            case TRIM_MEMORY_MODERATE:
//                Log.e("Memory","Memory:系统处于低内存的运行状态中并且你的应用处于缓存应用列表的中级阶段. 如果系运行内存收到限制, 你的应用有被杀掉的风险.");
//                break;
//            case TRIM_MEMORY_COMPLETE:
//                Log.e("Memory","Memory:系统处于低内存的运行状态中如果系统现在没有内存回收你的应用将会第一个被杀掉. 你必须释放掉所有非关键的资源从而恢复应用的状态.");
//                break;
//        }
//        super.onTrimMemory(level);
//    }

    public View getContentView(){
        return inflater.inflate(getLayoutResources(), null);
    }

    protected abstract void initView();

    protected abstract void fillData();

    protected abstract int getLayoutResources();

    @Override
    public void onBackPressed() {


        if(PackageUtil.getInstances(mContext).activityIsRunning(HomeActivity.class)){
            if(System.currentTimeMillis() - exitTimes > 2000){
                Toast.makeText(mContext,"再按一次退出程序",Toast.LENGTH_LONG).show();
                exitTimes = System.currentTimeMillis();
            }else{
                Intent intent = new Intent(WebSocketService.ACTION);
                intent.setPackage(getPackageName());
                stopService(intent);
                exit();

            }
        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        if(Build.BRAND.equals("Meizu")){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);
        }

        if(!Cst.WEB_SOCKET_IS_CONNECTED){
            Intent intent = new Intent(WebSocketService.ACTION);
            intent.putExtra("cid", Cst.PUSH_CLIENT_ID);
            sendBroadcast(intent);
        }
        super.onResume();
        registerReceiver(netWorkBroadcast, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        if ( ! (mContext instanceof LaunchActivity)){
            registerReceiver(loginOutTimeReciver, new IntentFilter(Cst.CMD.LOGIN_OUT_TIME_ACTION));
        }
        if(mContext instanceof HomeActivity || mContext instanceof AboutYeepActivity)
            registerReceiver(updateMsgReceiver, new IntentFilter(UpdateService.UPDATE_MSG));

    }

    protected void showExitDialog(){
        cancelMsg();
       final View view = LayoutInflater.from(mContext).inflate(R.layout.exist_panel,null);
        View parentView = LayoutInflater.from(mContext).inflate(getLayoutResources(),null);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setAnimationStyle(R.style.exist_style);
        popupWindow.showAtLocation(parentView, Gravity.NO_GRAVITY, 0, 0);

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float y = event.getY();
                if (y < view.findViewById(R.id.exist_panel).getTop()) {
                    popupWindow.dismiss();
                }
                return true;
            }
        });
    }



    protected abstract View getNavigationBar();


    protected void initNavigationBar(View navigationBar){
        if(navigationBar != null){
            Intent intent =getIntent();
            labelText = (TextView) navigationBar.findViewById(R.id.label_text);
            labelText.setText(getTitle());
            TextView preBtn = (TextView) navigationBar.findViewById(R.id.pre_text);
            if(intent != null){
                String backText = intent.getStringExtra("backText");
                preBtn.setText(backText);
            }

            navigationBar.findViewById(R.id.back_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    backBtnTap();
                }
            });
        }
    }

    protected void backBtnTap(){
        mContext.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(netWorkBroadcast);
        if ( ! (mContext instanceof LaunchActivity)){
            unregisterReceiver(loginOutTimeReciver);
        }
        if(mContext instanceof HomeActivity || mContext instanceof AboutYeepActivity){
            unregisterReceiver(updateMsgReceiver);
        }
    }



    /*
        * 监听当前Activity的网络连接状态
        * */
    private BroadcastReceiver netWorkBroadcast = new BroadcastReceiver() {
       @Override
       public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
           if(action.equals(ConnectivityManager.CONNECTIVITY_ACTION)){
               ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
               NetworkInfo networkInfo = manager.getActiveNetworkInfo();
               if(networkInfo == null|| !networkInfo.getState().equals(NetworkInfo.State.CONNECTED)
                &&(!(mContext instanceof LaunchActivity) && !(mContext instanceof GuideActivity) && !isNetError)){
                   isNetError = true;
                   if(!(mContext instanceof LaunchActivity) && !(mContext instanceof GuideActivity)){
                       View view = LayoutInflater.from(mContext).inflate(R.layout.net_work_off,null);
                        if(mContext instanceof HomeActivity){
                            ((HomeActivity)mContext).getMsgHandler().sendEmptyMessage(Cst.CMD.NET_ERROR);
                            return;
                        }

                       LinearLayout errorBar = (LinearLayout) view.findViewById(R.id.error_bar);
                       errorBar.removeAllViews();
                       if(getNavigationBar() != null){
                           if(getNavigationBar().getParent() instanceof FrameLayout){
                            ((FrameLayout)getNavigationBar().getParent()).removeView(getNavigationBar());
                           }else if(getNavigationBar().getParent() instanceof LinearLayout){
                               ((LinearLayout)getNavigationBar().getParent()).removeView(getNavigationBar());
                           }else if(getNavigationBar().getParent() instanceof RelativeLayout){
                               ((RelativeLayout)getNavigationBar().getParent()).removeView(getNavigationBar());
                           }

                           errorBar.addView(getNavigationBar());
                       }

                        setContentView(view);

                   }
               }else{
                   if(!(mContext instanceof LaunchActivity) && !(mContext instanceof GuideActivity) && isNetError){
                       isNetError = false;
                       if(mContext instanceof HomeActivity){

                           ((HomeActivity) mContext).showFragment(((HomeActivity)mContext).getKeyFromMap(HomeActivity.currentFragment));
                           ((HomeActivity) mContext).checkVersion();
                           return;
                       }
                       setContentView(getLayoutResources());
                       initView();
                       fillData();
                       navigationBar = getNavigationBar();
                       initNavigationBar(navigationBar);
                   }
                   cancelMsg();
                   if(NetUtil.getInstances(mContext).getNetType() == Cst.COMMON.WIFI && appUpdateServer!= null && appUpdateServer.isDownloading()){
                        AppUpdateServer.getInstances(mContext).onStartCommand(Cst.CMD.DOWNLOAD_UNDER_WIFI);
                   }
               }
           }
       }
    };



    public void cancelMsg(){

        if(popupWindow!=null && popupWindow.isShowing()&&!mContext.isFinishing()){
            Log.e("+++++22","我到这里啦");
            popupWindow.dismiss();
        }
    }



    private Rect getNotifyFrame(){
        //获取状态栏高度，即out.top
        Rect out = new Rect();
        mContext.getWindow().getDecorView().getWindowVisibleDisplayFrame(out);
        return out;
    }


    protected void showMsg(String msg, final View fragmentNavigationBar){

            //cancelMsg();
            if (popupWindow != null && popupWindow.isShowing()){
                return;
            }
            View view = LayoutInflater.from(mContext).inflate(R.layout.message,null);
            final View parentView = LayoutInflater.from(mContext).inflate(getLayoutResources(),null);
            TextView msgText = (TextView) view.findViewById(R.id.help_msg);
            msgText.setText(msg);
            popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setAnimationStyle(R.style.help_msg_style);
            navigationBar = getNavigationBar();
            if(navigationBar == null || navigationBar.getVisibility() == View.GONE||fragmentNavigationBar == null){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        popupWindow.showAtLocation(parentView, Gravity.NO_GRAVITY, 0, getNotifyFrame().top);
                    }
                },200);

            }else {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(fragmentNavigationBar == null){
                            popupWindow.showAsDropDown(navigationBar);
                        }else {
                            popupWindow.showAsDropDown(fragmentNavigationBar);
                        }
                    }
                }, 200);
            }


    }



    public void showErrorMsg(String msg,View navigationBar){

        showMsg(msg, navigationBar);
        new CountDownTimer(3000,1000){
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                cancelMsg();
            }
        }.start();
    }

    public void showErrorMsg(String msg){

        showMsg(msg,null);
        new CountDownTimer(3000,1000){
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                cancelMsg();
            }
        }.start();
    }

    public void toast(String msg) {
       /* Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();*/
        Toast toastOne=Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
        toastOne.setGravity(Gravity.CENTER, 0, 0);
        toastOne.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityStacks.getInstances().pop(this);
    }

    public void gotoTarget(Class activity,int animationIn,int animationOut,String fromTarget){
        Intent intent = new Intent(mContext, activity);
        //intent.putExtra("backText", fromTarget);
        startActivity(intent);
        overridePendingTransition(animationIn, animationOut);
    }
    public void gotoTargetForResult(Class activity,int animationIn,int animationOut,String fromTarget,int requestCode){
        Intent intent = new Intent(mContext, activity);
        //intent.putExtra("backText", fromTarget);
        startActivityForResult(intent, requestCode);
        overridePendingTransition(animationIn, animationOut);
    }

    public void gotoTarget(Class activity,int animationIn,int animationOut,String fromTarget,Object object){
        Intent intent = new Intent(mContext, activity);
        //intent.putExtra("backText", fromTarget);
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", (Serializable) object);
        intent.putExtra("data", bundle);
        startActivity(intent);
        overridePendingTransition(animationIn, animationOut);
    }

    public void gotoTarget(Class activity,String fromTarget,Object object){
        Intent intent = new Intent(mContext, activity);
        //intent.putExtra("backText", fromTarget);
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", (Serializable) object);
        intent.putExtra("data", bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_in_from_bottom, R.anim.activity_out_without_move);
    }

    public void gotoTargetForResult(Class activity,int animationIn,int animationOut,String fromTarget,Object object,int requestCode){
        Intent intent = new Intent(mContext, activity);
        //intent.putExtra("backText", fromTarget);
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", (Serializable) object);
        intent.putExtra("data", bundle);
        startActivityForResult(intent, requestCode);
        overridePendingTransition(animationIn, animationOut);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 0:
                if (data!=null && data.getAction()!= null && data.getAction().equals("ANSWER_OK")){
                    Cst.currentUser.answerFlag = true;
                    Utils.getInstances().putInvestorToSharedPreference(mContext,Cst.currentUser);
                }else if (data!=null && data.getAction()!= null && data.getAction().equals("ANSWER_ERROR")){
                    Cst.currentUser.answerFlag = false;
                    Utils.getInstances().putInvestorToSharedPreference(mContext,Cst.currentUser);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    public void gotoTargetRemovePre(Class activity,int animationIn,int animationOut,String fromTarget){
        Intent intent = new Intent(mContext,activity);
        intent.setAction("LANUCH");
        intent.putExtra("backText", fromTarget);
        startActivity(intent);
        overridePendingTransition(animationIn, animationOut);
        mContext.finish();
    }
    public void gotoTargetRemovePre(Class activity,int animationIn,int animationOut,String fromTarget,Object object){
        Intent intent = new Intent(mContext,activity);
        intent.putExtra("backText", fromTarget);
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", (Serializable) object);
        intent.putExtra("data", bundle);
        startActivity(intent);
        overridePendingTransition(animationIn, animationOut);
        mContext.finish();
    }
    /*跳转到我的投资券
    * */
    public void gotoMyCoupon(){
        Intent intent = new Intent(this,CouponsActivity.class);
        startActivity(intent);
    }
     /*
    * 跳转H5页面
    * */
    public void gotoWeb(Web web) {
        Intent intent = new Intent(this, WebActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("data",web);
        intent.putExtra("data", bundle);
        startActivity(intent);
    }

    public void showDialogWindow(int viewResource, int animation, OnShowListener onShowListener){
        View view = LayoutInflater.from(mContext).inflate(viewResource,null);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setFocusable(true);
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        Animation animation1 = AnimationUtils.loadAnimation(mContext, R.anim.activity_in_from_bottom);

        view.findViewById(R.id.contentView).setAnimation(animation1);

        //popupWindow.setAnimationStyle(animation);
        BitmapDrawable bitmap = new BitmapDrawable(mContext.getResources(), blur());
        //bitmap.setColorFilter(Color.parseColor("#aa000000"),PorterDuff.Mode.MULTIPLY);
        popupWindow.setBackgroundDrawable(bitmap);

        View parentView = LayoutInflater.from(mContext).inflate(getLayoutResources(), null);
        popupWindow.showAtLocation(parentView, Gravity.CENTER, 0, 200);
        if(onShowListener != null){
            onShowListener.show(view);
        }
        Intent intent = new Intent();
        intent.setAction("HAS_SHOW");
        mContext.sendBroadcast(intent);
    }


    private Bitmap overlay = null;
    private Bitmap mBitmap = null;

    private Bitmap blur() {
//        if (null != overlay) {
//            overlay.destoryDrawingCache();
//        }
        long startMs = System.currentTimeMillis();

        View view = mContext.getWindow().getDecorView();

        view.setDrawingCacheEnabled(true);
        view.destroyDrawingCache();
        view.buildDrawingCache(true);
        Bitmap mBitmap = view.getDrawingCache();


        float scaleFactor = 8;//图片缩放比例；
        float radius = 3;//模糊程度
        int width = mBitmap.getWidth();
        int height =  mBitmap.getHeight();

        overlay = Bitmap.createBitmap((int) (width / scaleFactor),(int) (height / scaleFactor),Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(overlay);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        //paint.setAlpha(150);
        canvas.drawBitmap(mBitmap, 0, 0, paint);
        overlay = FastBlur.doBlur(overlay, (int) radius, true);

        Log.i("TAG", "blur time is:" + (System.currentTimeMillis() - startMs));
        return overlay;
    }


    public interface OnShowListener{
        void show(View view);
    }

    protected void setTitle(String title){
        labelText.setText(title);
    }


    /*
    * 根据输入值改编按钮背景
    * */
    public void changeButtonStateWithValue(final EditText editText, final Button button, final int bgSources, final int unActivationBgSources){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean right = false;

                if (editText.getId() == R.id.register_phone || editText.getId() == R.id.phone_number || editText.getId() == R.id.forget_tradepassword_register_phone) {

                    right = s.toString().trim().matches("^[1][0-9]{10}$");
                } else if (editText.getId() == R.id.bank_card_number_text || editText.getId() == R.id.real_name) {
                    right = s.toString().trim().length() > 0;
                } else if (editText.getId() == R.id.discount_code) {
                    right = s.toString().trim().length() == 12;
                } else if (editText.getId() == R.id.purchase_money || editText.getId() == R.id.withdrawals_money) {
                    try {
                        Double.parseDouble(editText.getText().toString());
                        right = s.toString().length() > 0;
                    } catch (Exception e) {
                        //showErrorMsg(getString(R.string.format_error));
                        right = false;
                    }
                } else {
                    right = s.toString().trim().length() > 0;
                }
                if (right) {
                    button.setBackgroundResource(bgSources);
                    button.setEnabled(true);
                } else {
                    button.setBackgroundResource(unActivationBgSources);
                    button.setEnabled(false);
                }
            }
        });
    }

    public void changeButtonStateWithValue(final EditText editText, final ImageButton button, final int bgSources, final int unActivationBgSources){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean right = false;
                if (editText.getId() == R.id.register_phone || editText.getId() == R.id.phone_number || editText.getId() == R.id.forget_tradepassword_register_phone) {
                    right = s.toString().trim().matches("^[1][0-9]{10}$");
                } else if (editText.getId() == R.id.bank_card_number_text || editText.getId() == R.id.real_name) {
                    right = s.toString().trim().length() > 0;
                } else if (editText.getId() == R.id.discount_code) {
                    right = s.toString().trim().length() == 12;
                } else if (editText.getId() == R.id.purchase_money || editText.getId() == R.id.withdrawals_money) {
                    try {
                        Double.parseDouble(editText.getText().toString());
                        right = s.toString().length() > 0;
                    } catch (Exception e) {
                        //showErrorMsg(getString(R.string.format_error));
                        right = false;
                    }
                } else {
                    right = s.toString().trim().length() > 0;
                }
                if (right) {
                    button.setBackgroundResource(bgSources);
                    button.setEnabled(true);
                } else {
                    button.setBackgroundResource(unActivationBgSources);
                    button.setEnabled(false);
                }
            }
        });
    }


    private void getInfo() {
        TelephonyManager mTm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String imei = mTm.getDeviceId();
        String imsi = mTm.getSubscriberId();
        String mtype = android.os.Build.MODEL; // 手机型号
        String numer = mTm.getLine1Number(); // 手机号码，有的可得，有的不可得
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        if(keyCode == KeyEvent.KEYCODE_BACK){
//            backBtnTap();
//        }else
        if (keyCode == KeyEvent.KEYCODE_HOME){
            if( Build.BRAND.equals("Meizu")){
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意
                intent.addCategory(Intent.CATEGORY_HOME);
                this.startActivity(intent);
                return true;
            }
        }
        return super.onKeyUp(keyCode,event);
    }

    public void showAs(){
        if(loadding!= null && !loadding.isShowing()){
            loadding.show();
        }
    }

    public void dismiss(){
        if(loadding!= null && loadding.isShowing()){
            loadding.dismiss();
        }
    }

    public LoadDialog getLoadding(){
        return  new LoadDialog(mContext,R.style.dialog,false, Cst.CMD.NORMAL_LOADING);
    }


    BroadcastReceiver updateMsgReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(UpdateService.UPDATE_MSG)){

                Bundle bundle = intent.getBundleExtra("data");
                if (getLoadDialog() != null && getLoadDialog().isShowing()){
                    getLoadDialog().dismiss();
                }
                if(bundle != null){
                    UpdateService.Msg updateMsg = (UpdateService.Msg) bundle.getSerializable("data");
                    msgDialog = null;
                    updateCmd = updateMsg.cmd;
                    String message = updateMsg.message;
                    message = message.replaceAll(";", ";\n");
                    appInfo = updateMsg.appInfo;
                    appUpdateServer = AppUpdateServer.getInstances(mContext);
                    appUpdateServer.setAppInfo(appInfo);
                    switch (updateCmd){
                        case 1://非强制升级
                        case 2://强制升级
                            if(msgDialog == null){
                                createDialog(updateCmd);
                            }
                            if(msgDialog != null){
                                msgDialog.setMessage(message);
                                msgDialog.showAs();
                            }
                            break;
                    }
                }
            }
        }
    };



    private void createDialog(final int cmd){
        String leftBtnText = "不再提醒";
        if(cmd == 2){
            leftBtnText = "退出";
        }
        if(mContext != null){
            msgDialog = new LoadDialog(mContext, R.style.dialog, false, new View.OnClickListener() {
                //确定
                @Override
                public void onClick(View v) {
                    /*
                    * 如果是强制更新，则从服务端下载，否则跳转应用市场
                    *
                    * */

                    msgDialog.dismiss();
                    appUpdateServer.onStartCommand(Cst.CMD.DOWNLOAD_APP);
                }
            }, new View.OnClickListener() {
                //取消
                @Override
                public void onClick(View v) {
                    msgDialog.dismiss();

                    switch (cmd){
                        case 1:

            /*非强制升级只提示一次*/
                            if (appInfo != null)
                            Utils.getInstances().putUpdateInfoToPreference(mContext, appInfo.version, true);
                            break;
                        case 2:
                            ActivityStacks.getInstances().finishActivity();
                            break;
                    }

                }
            },Cst.CMD.NEW_VERSION).setSureBtn("下载更新").setCancelBtn(leftBtnText).selfGravity(Gravity.LEFT);
        }
    }

    protected  Intent getIntent(Context paramContext)
    {
        StringBuilder localStringBuilder = new StringBuilder().append("market://details?id=");
        String str = paramContext.getPackageName();
        localStringBuilder.append(str);
        Uri localUri = Uri.parse(localStringBuilder.toString());
        return new Intent("android.intent.action.VIEW", localUri);
    }

    //直接跳转不判断是否存在市场应用
    protected void start(Context paramContext, String paramString)
    {
        Uri localUri = Uri.parse(paramString);
        Intent localIntent = new Intent("android.intent.action.VIEW", localUri);
        localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        paramContext.startActivity(localIntent);
    }

//    protected  boolean judge(Context paramContext, Intent paramIntent)
//    {
//        List<ResolveInfo> localList = paramContext.getPackageManager().queryIntentActivities(paramIntent, PackageManager.GET_INTENT_FILTERS);
//        if ((localList != null) && (localList.size() > 0)){
//            return false;
//        }else{
//            return true;
//        }
//    }


    public void exit(){
        if(loadding != null && loadding.isShowing()){
            loadding.dismiss();
        }
        if(msgDialog != null && msgDialog.isShowing()){
            msgDialog.dismiss();
        }
        ActivityStacks.getInstances().finishActivity();
    }

    public IWXAPI getIwxapi(){
        iwxapi = WXAPIFactory.createWXAPI(mContext, Cst.COMMON.WX_APP_ID);
        Log.e("Context","Context："+mContext.getLocalClassName());
        iwxapi.registerApp(Cst.COMMON.WX_APP_ID);
        return iwxapi;
    }


    BroadcastReceiver loginOutTimeReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(Cst.CMD.LOGIN_OUT_TIME_ACTION.equals(action)){
                if(getLoadDialog() != null && getLoadDialog().isShowing()){
                    getLoadDialog().dismiss();
                }
                // HomeActivity.investBtn.setChecked(true);
                if(reLoginDialog != null && reLoginDialog.isShowing()){
                    reLoginDialog.dismiss();
                    reLoginDialog = null;
                }
                Cst.currentUser = null;
                Utils.getInstances().removeInvestorFromSharedPreference(mContext);
                HomeActivity.totalAssets = null;
                /*
                * 登录过期重新连接websocket
                * */
                Intent webSocketIntent = new Intent(WebSocketService.ACTION);
                context.sendBroadcast(webSocketIntent);

                /*
                * 去掉小红点
                * */
                HomeActivity.hideRedDot(HomeActivity.meBtn);

                reLoginDialog = new LoadDialog(mContext, R.style.dialog, false, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(reLoginDialog != null){
                                reLoginDialog.dismiss();
                            }

                            showDialogWindow(R.layout.login_and_register, R.style.exist_style, new OnShowListener() {
                                @Override
                                public void show(View view) {
                                    server = new LoginAndRegisterServer(mContext, view, loginOutTimeHandler);
                                }
                            });
                        }
                    },
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(reLoginDialog != null){
                                        reLoginDialog.dismiss();
                                    }

                                    ActivityStacks.getInstances().popToWitch(HomeActivity.class.getName());
//                                    if (context instanceof HomeActivity && HomeActivity.currentFragment instanceof BattleCouponCenterFragment){
//                                        ((BattleCouponCenterFragment)HomeActivity.currentFragment).onShow(context);
//                                    }

                                }
                            }, 0).setMessage("登录失效，请重新登录后继续操作");
                reLoginDialog.setSureBtn("立即登录");
                reLoginDialog.setCancelBtn("再看看");
                reLoginDialog.show();

                /*
                * 当前的fragment是MeFragment时，登录过期时，需要隐藏有关用户的相关信息*/
                if (context instanceof HomeActivity && HomeActivity.currentFragment instanceof MeFragment){
                    ((MeFragment)HomeActivity.currentFragment).setHasLoginOutTime(true);
                }
            }

        }

    };


    public  Handler loginOutTimeHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Cst.CMD.LOGIN_SUCCESS:
                case Cst.CMD.REGISTER_SUCCESS:
                    Cst.currentUser = server.getInvestor();
                    cancelMsg();
                    if(mContext instanceof HomeActivity){
                        ((BaseFragment)HomeActivity.currentFragment).onShow(mContext);
                    }
                    break;
                case Cst.CMD.LOGIN_FRAME_CLOSED:
                    ActivityStacks.getInstances().popToWitch(HomeActivity.class.getName());
            }
            super.handleMessage(msg);
        }
    };

    public abstract LoadDialog getLoadDialog();

}
