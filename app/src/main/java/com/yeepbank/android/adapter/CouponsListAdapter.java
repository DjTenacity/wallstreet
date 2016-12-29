package com.yeepbank.android.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.yeepbank.android.Cst;
import com.yeepbank.android.R;
import com.yeepbank.android.base.AbstractAdapter;
import com.yeepbank.android.model.user.CouponsVo;
import com.yeepbank.android.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WW on 2015/11/21.
 * 投资券列表
 */
public class CouponsListAdapter extends BaseAdapter{
    private Context mContext;
    private ArrayList<CouponsVo> data;

    public CouponsListAdapter(Context context,ArrayList<CouponsVo> couponsVoArrayList) {
        this.mContext = context;
        this.data = couponsVoArrayList;
    }

    public ArrayList<CouponsVo> getData(){
        return data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(data.get(position).status.trim().equals("SELF")){
            convertView = inflater.inflate(R.layout.exchange_coupons,null);
            convertView.setTag("EXCHANGE_COUPONS");
        }else {
            if(convertView == null || convertView.getTag().equals("EXCHANGE_COUPONS")){

                convertView = inflater.inflate(R.layout.coupons_list_item,null);
                convertView.setTag(holder);
            }else {
                if(!convertView.getTag().equals("EXCHANGE_COUPONS")){
                    holder = (AbstractAdapter.IViewHolder) convertView.getTag();
                }
            }
            if(holder != null && !convertView.getTag().equals("EXCHANGE_COUPONS")){
                holder.initView(convertView);
                holder.fillData(position);
            }
        }
        return convertView;
    }

    AbstractAdapter.IViewHolder holder = new AbstractAdapter.IViewHolder() {
        private TextView couponDescriptionText, fullAmountText, subtractAmountText,
                expirationDateText, belongsIaText, numberAfterWords;//描述，满减金额-满,满减金额-减,截止日期,加息券文字描述,数字后面文字
        private LinearLayout tickLayoutRight, belongsFcLayout;//，优惠券右边布局，满减券左边文字
        private View splitLine;
        private RelativeLayout tickLayoutLeft;//优惠券左边布局
        private ImageView tickMarkImg, tickChooseImg;//券右边文字标识，选中券标识

        @Override
        public void initView(View view) {
            couponDescriptionText = (TextView) view.findViewById(R.id.coupon_description);
            fullAmountText = (TextView) view.findViewById(R.id.full_amount);
            subtractAmountText = (TextView) view.findViewById(R.id.subtract_amount);
            expirationDateText = (TextView) view.findViewById(R.id.expiration_date);
            tickLayoutLeft = (RelativeLayout) view.findViewById(R.id.tick_layout_left);
            tickLayoutRight = (LinearLayout) view.findViewById(R.id.tick_layout_right);
            belongsFcLayout = (LinearLayout) view.findViewById(R.id.belongs_fc);
            belongsIaText = (TextView) view.findViewById(R.id.belongs_ia);
            numberAfterWords = (TextView) view.findViewById(R.id.number_after_words);
            splitLine = view.findViewById(R.id.split_line);
            tickMarkImg = (ImageView) view.findViewById(R.id.tick_mark);
            tickChooseImg = (ImageView) view.findViewById(R.id.tick_choose);
            tickChooseImg.setVisibility(View.GONE);


        }

        @Override
        public void fillData(int position) {
            if (data.get(position).status.trim().equals("B")) {
                couponDescriptionText.setText(data.get(position).couponDescription);
                if (data.get(position).couponType.equals("FC")) {
                    belongsFcLayout.setVisibility(View.VISIBLE);
                    belongsIaText.setVisibility(View.GONE);
                    tickLayoutLeft.setBackgroundResource(R.drawable.full_coupon_left_icon);
                    tickLayoutRight.setBackgroundResource(R.drawable.full_coupon_right_icon);
                    fullAmountText.setText((int)data.get(position).fullAmount+"");
                    subtractAmountText.setText((int)data.get(position).subtractAmount+"");
                    subtractAmountText.setTextColor(Color.parseColor("#4da9de"));
                    numberAfterWords.setText("元");
                    numberAfterWords.setVisibility(View.VISIBLE);
                    splitLine.setBackgroundColor(Color.parseColor("#c3dff0"));
                    tickMarkImg.setImageResource(R.drawable.full_cut_text);
                    if(data.get(position).choose){
                        tickChooseImg.setVisibility(View.VISIBLE);
                    }else {
                        tickChooseImg.setVisibility(View.GONE);
                    }

                } else if (data.get(position).couponType.equals("IA")) {
                    belongsFcLayout.setVisibility(View.GONE);
                    belongsIaText.setVisibility(View.VISIBLE);
                    tickLayoutLeft.setBackgroundResource(R.drawable.interest_rate_coupon_left_icon);
                    tickLayoutRight.setBackgroundResource(R.drawable.interest_rate_coupon_right_icon);
                    subtractAmountText.setText(Utils.getInstances().format(data.get(position).addingInterest * 100) + "%");
                    subtractAmountText.setTextColor(Color.parseColor("#60c08b"));
                    numberAfterWords.setVisibility(View.GONE);
                    splitLine.setBackgroundColor(Color.parseColor("#e3f1dc"));
                    tickMarkImg.setImageResource(R.drawable.interest_rate_text);
                    if(data.get(position).choose){
                        tickChooseImg.setVisibility(View.VISIBLE);
                    }else {
                        tickChooseImg.setVisibility(View.GONE);
                    }
                } else if (data.get(position).couponType.equals("EC")) {
                    belongsFcLayout.setVisibility(View.GONE);
                    belongsIaText.setVisibility(View.GONE);
                    tickLayoutLeft.setBackgroundResource(R.drawable.experience_coupon_left_icon);
                    tickLayoutRight.setBackgroundResource(R.drawable.experience_coupon_right_icon);
                    subtractAmountText.setText((int)data.get(position).experienceAmount+"");
                    subtractAmountText.setTextColor(Color.parseColor("#fcb944"));
                    numberAfterWords.setVisibility(View.VISIBLE);
                    numberAfterWords.setText("元体验金");
                    splitLine.setBackgroundColor(Color.parseColor("#fbe9d5"));
                    tickMarkImg.setImageResource(R.drawable.experience_coupon_text);
                    if(data.get(position).choose){
                        tickChooseImg.setVisibility(View.VISIBLE);
                    }else {
                        tickChooseImg.setVisibility(View.GONE);
                    }
                }
                expirationDateText.setText(data.get(position).expirationDate);
            } else if (data.get(position).status.trim().equals("U") || data.get(position).status.trim().equals("E")) {
                if (data.get(position).status.trim().equals("U")) {
                    tickLayoutLeft.setBackgroundResource(R.drawable.used_coupons_left_icon);
                } else if (data.get(position).status.trim().equals("E")) {
                    tickLayoutLeft.setBackgroundResource(R.drawable.expired_coupons_left_icon);
                }
                tickLayoutRight.setBackgroundResource(R.drawable.expired_coupons_right_icon);
                couponDescriptionText.setText(data.get(position).couponDescription);
                couponDescriptionText.setTextColor(Color.parseColor("#999999"));
                numberAfterWords.setTextColor(Color.parseColor("#a3a3a3"));
                subtractAmountText.setTextColor(Color.parseColor("#a3a3a3"));
                splitLine.setBackgroundColor(Color.parseColor("#dbdcdb"));
                if (data.get(position).couponType.equals("FC")) {
                    belongsFcLayout.setVisibility(View.VISIBLE);
                    belongsIaText.setVisibility(View.GONE);
                    fullAmountText.setText((int)data.get(position).fullAmount+"");
                    subtractAmountText.setText((int)data.get(position).subtractAmount+"");
                    numberAfterWords.setText("元");
                    numberAfterWords.setVisibility(View.VISIBLE);
                    tickMarkImg.setImageResource(R.drawable.full_cut_text_gray);
                } else if (data.get(position).couponType.equals("IA")) {
                    belongsFcLayout.setVisibility(View.GONE);
                    belongsIaText.setVisibility(View.VISIBLE);
                    subtractAmountText.setText(Utils.getInstances().format(data.get(position).addingInterest * 100) + "%");
                    numberAfterWords.setVisibility(View.GONE);
                    tickMarkImg.setImageResource(R.drawable.interest_rate_text_gray);
                } else if (data.get(position).couponType.equals("EC")) {
                    belongsFcLayout.setVisibility(View.GONE);
                    belongsIaText.setVisibility(View.GONE);
                    subtractAmountText.setText((int)data.get(position).experienceAmount+"");
                    numberAfterWords.setVisibility(View.VISIBLE);
                    numberAfterWords.setText("元体验金");
                    tickMarkImg.setImageResource(R.drawable.experience_coupon_text_gray);
                }
                expirationDateText.setText(data.get(position).expirationDate);
            }
        }
    };
}

//    public CouponsListAdapter(List<CouponsVo> data, Context context) {
//        super(data, context, new IViewHolder() {
//            private TextView couponDescriptionText,fullAmountText,subtractAmountText,
//                    expirationDateText,belongsIaText,numberAfterWords;//描述，满减金额-满,满减金额-减,截止日期,加息券文字描述,数字后面文字
//            private LinearLayout tickLayoutRight,belongsFcLayout;//，优惠券右边布局，满减券左边文字
//            private View splitLine;
//            private RelativeLayout tickLayoutLeft;//优惠券左边布局
//            private ImageView tickMarkImg,tickChooseImg;//券右边文字标识，选中券标识
//            @Override
//            public void initView(View view) {
//                couponDescriptionText = (TextView) view.findViewById(R.id.coupon_description);
//                fullAmountText = (TextView) view.findViewById(R.id.full_amount);
//                subtractAmountText = (TextView) view.findViewById(R.id.subtract_amount);
//                expirationDateText = (TextView) view.findViewById(R.id.expiration_date);
//                tickLayoutLeft = (RelativeLayout) view.findViewById(R.id.tick_layout_left);
//                tickLayoutRight = (LinearLayout) view.findViewById(R.id.tick_layout_right);
//                belongsFcLayout = (LinearLayout) view.findViewById(R.id.belongs_fc);
//                belongsIaText = (TextView) view.findViewById(R.id.belongs_ia);
//                numberAfterWords = (TextView) view.findViewById(R.id.number_after_words);
//                splitLine = view.findViewById(R.id.split_line);
//                tickMarkImg = (ImageView) view.findViewById(R.id.tick_mark);
//                tickChooseImg = (ImageView) view.findViewById(R.id.tick_choose);
//                tickChooseImg.setVisibility(View.GONE);
//
//            }
//
//            @Override
//            public void fillData(int position) {
//                if(data.get(position).status.trim().equals("B")){
//                    couponDescriptionText.setText(data.get(position).couponDescription);
//                    if(data.get(position).couponType.equals("FC")){
//                        belongsFcLayout.setVisibility(View.VISIBLE);
//                        belongsIaText.setVisibility(View.GONE);
//                        tickLayoutLeft.setBackgroundResource(R.drawable.full_coupon_left_icon);
//                        tickLayoutRight.setBackgroundResource(R.drawable.full_coupon_right_icon);
//                        fullAmountText.setText(Utils.format(data.get(position).fullAmount));
//                        subtractAmountText.setText(Utils.format(data.get(position).subtractAmount));
//                        subtractAmountText.setTextColor(Color.parseColor("#4da9de"));
//                        numberAfterWords.setText("元");
//                        numberAfterWords.setVisibility(View.VISIBLE);
//                        splitLine.setBackgroundColor(Color.parseColor("#c3dff0"));
//                        tickMarkImg.setImageResource(R.drawable.full_cut_text);
//                    }else if(data.get(position).couponType.equals("IA")){
//                        belongsFcLayout.setVisibility(View.GONE);
//                        belongsIaText.setVisibility(View.VISIBLE);
//                        tickLayoutLeft.setBackgroundResource(R.drawable.interest_rate_coupon_left_icon);
//                        tickLayoutRight.setBackgroundResource(R.drawable.interest_rate_coupon_right_icon);
//                        subtractAmountText.setText(Utils.format(data.get(position).addingInterest * 100) + "%");
//                        subtractAmountText.setTextColor(Color.parseColor("#60c08b"));
//                        numberAfterWords.setVisibility(View.GONE);
//                        splitLine.setBackgroundColor(Color.parseColor("#e3f1dc"));
//                        tickMarkImg.setImageResource(R.drawable.interest_rate_text);
//                    }else if(data.get(position).couponType.equals("EC")){
//                        belongsFcLayout.setVisibility(View.GONE);
//                        belongsIaText.setVisibility(View.GONE);
//                        tickLayoutLeft.setBackgroundResource(R.drawable.experience_coupon_left_icon);
//                        tickLayoutRight.setBackgroundResource(R.drawable.experience_coupon_right_icon);
//                        subtractAmountText.setText(Utils.format(data.get(position).experienceAmount));
//                        subtractAmountText.setTextColor(Color.parseColor("#fcb944"));
//                        numberAfterWords.setVisibility(View.VISIBLE);
//                        numberAfterWords.setText("元体验金");
//                        splitLine.setBackgroundColor(Color.parseColor("#fbe9d5"));
//                        tickMarkImg.setImageResource(R.drawable.experience_coupon_text);
//                    }
//                    expirationDateText.setText(data.get(position).expirationDate);
//                }else if(data.get(position).status.trim().equals("U")||data.get(position).status.trim().equals("E")){
//                    if(data.get(position).status.trim().equals("U")){
//                        tickLayoutLeft.setBackgroundResource(R.drawable.used_coupons_left_icon);
//                    }else if(data.get(position).status.trim().equals("E")){
//                        tickLayoutLeft.setBackgroundResource(R.drawable.expired_coupons_left_icon);
//                    }
//                    tickLayoutRight.setBackgroundResource(R.drawable.expired_coupons_right_icon);
//                    couponDescriptionText.setText(data.get(position).couponDescription);
//                    couponDescriptionText.setTextColor(Color.parseColor("#999999"));
//                    numberAfterWords.setTextColor(Color.parseColor("#a3a3a3"));
//                    subtractAmountText.setTextColor(Color.parseColor("#a3a3a3"));
//                    splitLine.setBackgroundColor(Color.parseColor("#dbdcdb"));
//                    if(data.get(position).couponType.equals("FC")){
//                        belongsFcLayout.setVisibility(View.VISIBLE);
//                        belongsIaText.setVisibility(View.GONE);
//                        fullAmountText.setText(Utils.format(data.get(position).fullAmount));
//                        subtractAmountText.setText(Utils.format(data.get(position).subtractAmount));
//                        numberAfterWords.setText("元");
//                        numberAfterWords.setVisibility(View.VISIBLE);
//                        tickMarkImg.setImageResource(R.drawable.full_cut_text_gray);
//                    }else if(data.get(position).couponType.equals("IA")){
//                        belongsFcLayout.setVisibility(View.GONE);
//                        belongsIaText.setVisibility(View.VISIBLE);
//                        subtractAmountText.setText(Utils.format(data.get(position).addingInterest * 100) + "%");
//                        numberAfterWords.setVisibility(View.GONE);
//                        tickMarkImg.setImageResource(R.drawable.interest_rate_text_gray);
//                    }else if(data.get(position).couponType.equals("EC")){
//                        belongsFcLayout.setVisibility(View.GONE);
//                        belongsIaText.setVisibility(View.GONE);
//                        subtractAmountText.setText(Utils.format(data.get(position).experienceAmount));
//                        numberAfterWords.setVisibility(View.VISIBLE);
//                        numberAfterWords.setText("元体验金");
//                        tickMarkImg.setImageResource(R.drawable.experience_coupon_text_gray);
//                    }
//                    expirationDateText.setText(data.get(position).expirationDate);
//                }
//            }
//        });
//    }
//
//    @Override
//    public int getLayoutResources() {
//        return R.layout.coupons_list_item;
//    }


