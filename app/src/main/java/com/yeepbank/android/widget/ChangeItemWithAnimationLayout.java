package com.yeepbank.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.yeepbank.android.Cst;
import com.yeepbank.android.R;
import com.yeepbank.android.activity.business.ProjectDetailActivity;
import com.yeepbank.android.activity.business.SecKillActivity;
import com.yeepbank.android.activity.business.TransformActivity;
import com.yeepbank.android.activity.user.RunningAccountActivity;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by WW on 2015/10/8.
 */
public class ChangeItemWithAnimationLayout extends RelativeLayout implements View.OnClickListener {
    private Context mContext;
    private LinearLayout itemLayout;
    private ArrayList<TextModel> itemList;
    private int padding;
    private boolean isOnce = true;
    private TextModel checkedTextModel;
    private boolean isAnimation = false;
    private Cst.OnSlideCompleteListener onSlideCompleteListener;
    private ImageView img;
    private int dx;
    private RelativeLayout.LayoutParams lp;
    private String[] itemTexts = null;
    private String status = "",witchPage="";
    private int pageStyle;
    private int rawWidth,rawHeight;
    private int screenWidth;
    private int checkLayoutWidth;
    public static final int RUNNING_ACCOUNT = 0x111111;

    private int initChoose = 0;
    private boolean hasMeasured = false;
    private boolean isDurationInit = true;//如果在初始化阶段 loadInitChooseData 方法执行，否则执行onSlideCompleteListener接口方法



    public ChangeItemWithAnimationLayout(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init(context, attrs);
    }

    public ChangeItemWithAnimationLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.mContext = context;
        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.ChangeItemWithAnimationLayout);
        pageStyle = a.getInt(R.styleable.ChangeItemWithAnimationLayout_pageStyle,0);
        a.recycle();
        try {
            Log.e("Time","获取initChoose:"+System.currentTimeMillis());
            Field field = mContext.getClass().getDeclaredField("initChoose");
            initChoose = field.getInt(mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(pageStyle == 0){
            if(witchPage.equals("TRANS_PROJECT")){
                itemTexts = mContext.getResources().getStringArray(R.array.item_text_trans);
            }else {
                if(status.equals("IRP")||status.equals("NCL")){
                    itemTexts = mContext.getResources().getStringArray(R.array.item_text);
                }else {
                    itemTexts = mContext.getResources().getStringArray(R.array.item_text1);
                }
            }
        }else if(pageStyle == RUNNING_ACCOUNT){
            itemTexts = mContext.getResources().getStringArray(R.array.running_account);
        }else{
            //xiaogang.dong在此update
            itemTexts = mContext.getResources().getStringArray(R.array.investment_record_item_text);
        }

        img = new ImageView(mContext);
        img.setId(R.id.item_img);
        addView(img);

        itemLayout = new LinearLayout(mContext);
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);
        itemLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        itemLayout.setId(R.id.item_layout);
        addView(itemLayout);
    }

    private void init() {
        removeAllViews();
        if(pageStyle == 0){
            if(witchPage.equals("TRANS_PROJECT")){
                itemTexts = mContext.getResources().getStringArray(R.array.item_text_trans);
            }else {
                if(status.equals("IRP")||status.equals("NCL")){
                    itemTexts = mContext.getResources().getStringArray(R.array.item_text);
                }else {
                    itemTexts = mContext.getResources().getStringArray(R.array.item_text1);
                }
            }
        }else if(pageStyle == RUNNING_ACCOUNT){
            itemTexts = mContext.getResources().getStringArray(R.array.running_account);
        }else{
            //xiaogang.dong在此update
            itemTexts = mContext.getResources().getStringArray(R.array.investment_record_item_text);
        }

        img = new ImageView(mContext);
        img.setId(R.id.item_img);
        addView(img);

        itemLayout = new LinearLayout(mContext);
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);
        itemLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        itemLayout.setId(R.id.item_layout);
        addView(itemLayout);
    }

    private int min(int... spaces) {
        int minValue = spaces[0];
        for(int space:spaces){
            if(minValue > space){
                minValue = space;
            }
        }
        return minValue;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        createView();
    }

    private void createView(){
        if(isOnce){
            isOnce = false;
            padding = min(getPaddingLeft(),getPaddingTop(),getPaddingRight(),getPaddingBottom());
            itemList = new ArrayList<TextModel>();
            screenWidth = ((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
            checkLayoutWidth = (screenWidth - 2*padding)/itemTexts.length;
            for (int i = 0; i < itemTexts.length; i++) {
                LinearLayout layout = new LinearLayout(mContext);
                layout.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,1));
                layout.setGravity(Gravity.CENTER);
                layout.setOrientation(LinearLayout.HORIZONTAL);

                TextView text = new TextView(mContext);
                text.setText(itemTexts[i]);
                text.setTextColor(Color.parseColor("#666666"));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,32,
                                mContext.getResources().getDisplayMetrics()));
                text.setMinHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, mContext.getResources().getDisplayMetrics()));
                text.setTextColor(getResources().getColor(R.color.dark_hint_color));
                text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                text.setGravity(Gravity.CENTER);
                text.setClickable(false);
                if(i == 0){
                    if(itemTexts.length < 3){
                        img.setBackgroundResource(R.drawable.scroll_round_rect_bg_2);

                    }else {
                        img.setBackgroundResource(R.drawable.scroll_round_rect_bg);
                    }
                    lp = new RelativeLayout.LayoutParams(checkLayoutWidth,(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,32,
                            mContext.getResources().getDisplayMetrics()));
                    img.setLayoutParams(lp);
                }

                text.setLayoutParams(params);
                layout.addView(text);
                LinearLayout redDocLayout = new LinearLayout(mContext);
                redDocLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
                redDocLayout.setGravity(Gravity.TOP);
                redDocLayout.setPadding(2,10,0,0);
                ImageView redDoc = new ImageView(mContext);
                redDoc.setLayoutParams(new ViewGroup.LayoutParams(20, ViewGroup.LayoutParams.WRAP_CONTENT));
                redDoc.setImageResource(R.drawable.red_dot);
                redDocLayout.addView(redDoc);
                layout.addView(redDocLayout);
                layout.setId(getIdByText(itemTexts[i]));

                itemLayout.addView(layout);

                TextModel model = new TextModel();
                model.textLayout = layout;
                model.index = i;
                model.textLayout.setOnClickListener(this);
                itemList.add(model);
            }

            checkedTextModel = find(initChoose);
            if (checkedTextModel != null){
                ((TextView)checkedTextModel.textLayout.getChildAt(0)).setTextColor(Color.parseColor("#999999"));
                checkedTextModel.textLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        if (checkedTextModel.textLayout.getWidth() > 0){
                            checkedTextModel.textLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    drawImg();
                                    setIndexImgPosition(checkedTextModel.index);
                                }
                            },10);
                        }


                    }
                });
            }else {
                if (itemList.size() > 0) {
                    checkedTextModel = itemList.get(0);
                    ((TextView) checkedTextModel.textLayout.getChildAt(0)).setTextColor(Color.parseColor("#999999"));
                }
            }
            setRedDocVisible(GONE);

        }
    }


    public void setRedDocVisible(int visible){
        if (itemList != null && itemList.size() > 0){
            for (int i = 0; i < itemList.size(); i++) {
                TextModel textModel = itemList.get(i);
                textModel.textLayout.getChildAt(1).setVisibility(GONE);
            }
        }
        if (checkedTextModel != null){
            checkedTextModel.textLayout.getChildAt(1).setVisibility(visible);
        }
    }

    public void setRedDocVisible(int visible,int id){
        TextModel textModel = find(id);
        if (textModel != null){
            textModel.textLayout.getChildAt(1).setVisibility(visible);
        }
    }

    private int getIdByText(String text){
        int id = 0;
        if(text.trim().equals("项目详情")){
            id = R.id.project_detail;
        }else if(text.trim().equals("风险控制")){
            id = R.id.risk_control;
        }else if(text.trim().equals("投资列表")){
            id = R.id.invest_list;
        }else if(text.trim().equals("还款记录")||text.trim().equals("投资还款计划")){
            id = R.id.repayment_plan;
        }else if(text.trim().equals("待结标")){
            id=R.id.waitEnd;
        }else if(text.trim().equals("还款中")){
            id=R.id.repaying;
        }else if(text.trim().equals("已还款")){
            id=R.id.repayed;
        }else if(text.trim().equals("充值")){
            id=R.id.recharge;
        }else if(text.trim().equals("提现")){
            id=R.id.withdrawal;
        }

        return id;

    }


    @Override
    public void onClick(final View v) {
        if(isAnimation){
            return;
        }
        if(checkedTextModel == null){
            checkedTextModel = find(v);
        }else if(checkedTextModel.textLayout.getId() == v.getId()){
            return;
        }else {


            new Thread(){
                @Override
                public void run() {
                    Message msg = handler.obtainMessage();
                    msg.what = 0;
                    msg.obj = v;
                    handler.sendMessage(msg);
                }
            }.start();



        }
    }
    public void scroll(final int i) {
        if(isAnimation){
            return;
        }
        if(checkedTextModel==null){
            checkedTextModel=find(itemLayout.getChildAt(i));
        }else if(checkedTextModel.textLayout.getId()==itemLayout.getChildAt(i).getId()){
            return;
        }else{
            new Thread(){
                @Override
                public void run() {
                    Message msg=handler.obtainMessage();
                    msg.what=0;
                    msg.obj=itemLayout.getChildAt(i);
                    handler.sendMessage(msg);
                }
            }.start();
        }
    }
    public void moveImgWithoutLoadData(View v){
        if(isAnimation){
            return;
        }
        moveImg(v,false);
    }

    /*获取下一个tab*/
    public TextModel getNextTextModel(){
        for (int i = 0; i < itemList.size(); i++) {
            if (itemList.get(i).textLayout.getId() == checkedTextModel.textLayout.getId()){
                if (i < itemList.size() - 1){
                    return itemList.get(i+1);
                }
            }
        }
        return null;
    }

    /*获取上一个tab*/
    public TextModel getPreTextModel(){
        for (int i = 0; i < itemList.size(); i++) {
            if (itemList.get(i).textLayout.getId() == checkedTextModel.textLayout.getId()){
                if (i > 0){
                    return itemList.get(i - 1);
                }
            }
        }
        return null;
    }

    private TextModel find(View v) {
        for (TextModel model:itemList){
            if(model.textLayout.getId() == v.getId()){
                return model;
            }
        }
        return null;
    }

    private TextModel find(int id) {
        for (TextModel model:itemList){
            if(model.textLayout.getId() == id){
                return model;
            }
        }
        return null;
    }

    public class TextModel implements Serializable{
        public LinearLayout textLayout;
        public int index;
    }


    public void setOnSlideCompleteListener(Cst.OnSlideCompleteListener onSlideCompleteListener){
        this.onSlideCompleteListener = onSlideCompleteListener;
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    View v = (View) msg.obj;
                    moveImg(v,true);
                    break;

            }
        }
    };


    private void moveImg(final View v, final boolean withLoadCmd){
        dx = checkedTextModel.textLayout.getWidth()*(Math.abs(checkedTextModel.index - find(v).index));
        if(checkedTextModel.index > find(v).index){
            dx = -dx;
        }

        Animation tAnimation = new TranslateAnimation(0, dx, 0,0);
        tAnimation.setFillAfter(true);
        tAnimation.setDuration(300);
        tAnimation.setInterpolator(new AccelerateInterpolator());
        drawImg();
        tAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isAnimation = true;
                for (int i = 0; i < itemList.size(); i++) {
                    TextModel model = itemList.get(i);
                    ((TextView) model.textLayout.getChildAt(0)).setTextColor(Color.parseColor("#666666"));
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                checkedTextModel = find(v);
                ((TextView) checkedTextModel.textLayout.getChildAt(0)).setTextColor(Color.parseColor("#999999"));
                isAnimation = false;
                if (onSlideCompleteListener != null && withLoadCmd) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onSlideCompleteListener.onComplete(checkedTextModel.textLayout);
                        }
                    }, 100);

                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        img.startAnimation(tAnimation);
        setIndexImgPosition(dx);
    }

    /*
    * 设置下标图标位置
    * */
    private void setIndexImgPosition(int dx) {
        if(mContext instanceof ProjectDetailActivity){
            ((ProjectDetailActivity) mContext).moveTriangle(dx, lp.leftMargin, checkedTextModel.textLayout.getWidth());
        }else if(mContext instanceof SecKillActivity){
            ((SecKillActivity) mContext).moveTriangle(dx, lp.leftMargin, checkedTextModel.textLayout.getWidth());
        }else if (mContext instanceof TransformActivity){
            ((TransformActivity) mContext).moveTriangle(dx, lp.leftMargin, checkedTextModel.textLayout.getWidth());
        }else if(mContext instanceof RunningAccountActivity){
            ((RunningAccountActivity) mContext).moveTriangle(dx, lp.leftMargin, checkedTextModel.textLayout.getWidth());
        }
        if (initChoose != 0 && isDurationInit){
            try {
                Method method = mContext.getClass().getDeclaredMethod("loadInitChooseData",int.class);
                method.invoke(mContext,initChoose);
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                isDurationInit = false;
            }
        }
    }

    /*
    * 设置item背景框的位置
    * */
    private void drawImg() {


        if(itemTexts.length < 3){
            img.setBackgroundResource(R.drawable.scroll_round_rect_bg_2);
        }else {
            img.setBackgroundResource(R.drawable.scroll_round_rect_bg);
        }
        lp = new RelativeLayout.LayoutParams(checkedTextModel.textLayout.getWidth(),checkedTextModel.textLayout.getMeasuredHeight());
        lp.leftMargin = checkedTextModel.textLayout.getLeft() - padding;
        lp.topMargin = checkedTextModel.textLayout.getTop();
        img.setLayoutParams(lp);
    }

    public void setStatus(String status,String witchPage){
        this.status = status;
        this.witchPage = witchPage;
        init();
        isOnce = true;
        createView();
    }

    public int getCheckLayoutWidth(){
        return checkLayoutWidth;
    }

    public ArrayList<TextModel> getItemList() {
        return itemList;
    }
}