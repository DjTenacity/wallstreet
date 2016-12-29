package com.yeepbank.android.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.yeepbank.android.R;
import com.yeepbank.android.base.AbstractAdapter;
import com.yeepbank.android.model.business.BattleCouponModel;
import com.yeepbank.android.utils.DateUtils;
import com.yeepbank.android.utils.Utils;

import java.util.Date;
import java.util.List;

/**
 * Created by xiaogang.dong 2016/9/1.
 */
public class BattleCouponCenterAdapter extends AbstractAdapter<BattleCouponModel>{
    public BattleCouponCenterAdapter(final List<BattleCouponModel> data, Context context) {
        super(data, context,new AbstractAdapter.IViewHolder(){
            private LinearLayout couponCenterItemLeftLayout;//左边的布局背景
            private TextView battleCouponType;//券的类型；是满减券，体验券，还是加息券
            private TextView battleCouponDate;//使用该券的投资期限
            private TextView battleCouponAccount;//投资券的面额，也就是面值
            private TextView battleCouponAccountDanwei;//单位
            private TextView battleCouponLimit;//使用该投资券的投资的最低限额
            private LinearLayout couponCenterItemRightLayout;//右边的布局背景
            private TextView battleCouponTitle;//方法该券的原因
            private LinearLayout battleCouponRiqiLayout;//该券的活动开始时间的布局
            private TextView battleCouponRiqi;//该券的活动开始时间
            private TextView battleCouponStatus;//该券的状态
            @Override
            public void initView(View view) {
                couponCenterItemLeftLayout= (LinearLayout) view.findViewById(R.id.coupon_center_item_left_layout);
                battleCouponType= (TextView) view.findViewById(R.id.battle_coupon_type);
                battleCouponDate= (TextView) view.findViewById(R.id.battle_coupon_date);
                battleCouponAccount= (TextView) view.findViewById(R.id.battle_coupon_account);
                battleCouponAccountDanwei= (TextView) view.findViewById(R.id.battle_coupon_account_danwei);
                battleCouponLimit= (TextView) view.findViewById(R.id.battle_coupon_limit);
                couponCenterItemRightLayout= (LinearLayout) view.findViewById(R.id.coupon_center_item_right_layout);
                battleCouponTitle= (TextView) view.findViewById(R.id.battle_coupon_title);
                battleCouponRiqiLayout= (LinearLayout) view.findViewById(R.id.battle_coupon_riqi_layout);
                battleCouponRiqi= (TextView) view.findViewById(R.id.battle_coupon_riqi);
                battleCouponStatus= (TextView) view.findViewById(R.id.battle_coupon_status);
            }

            @Override
            public void fillData(int position) {
                if(data.get(position).couponStatus.equals("0")||data.get(position).couponStatus.equals("1")||data.get(position).couponStatus.equals("2")){
                    couponCenterItemLeftLayout.setBackgroundResource(R.drawable.coupon_un_left);
                    couponCenterItemRightLayout.setBackgroundResource(R.drawable.coupon_un_right);
                    if(data.get(position).receiveStartTimeStamp>new Date().getTime()){
                        if(DateUtils.getDateToString(data.get(position).receiveStartTimeStamp).equals(DateUtils.getCurrentDate())){
                            String str=DateUtils.getDateToStringTwo(data.get(position).receiveStartTimeStamp);
                            str=str.substring(11,str.length());
                            battleCouponRiqiLayout.setVisibility(View.VISIBLE);
                            battleCouponRiqi.setText("今天 "+str);
                            battleCouponStatus.setText("准时开抢");
                        }else{
                            String str=DateUtils.getDateToString(data.get(position).receiveStartTimeStamp);
                            str=str.substring(6,str.length());
                            battleCouponRiqiLayout.setVisibility(View.VISIBLE);
                            battleCouponRiqi.setText(str);
                            battleCouponStatus.setText("即将开抢");

                        }
                    }else{
                        if(data.get(position).receiveEndTimeStamp-new Date().getTime()<3*60*60*1000&&data.get(position).receiveEndTimeStamp-new Date().getTime()>0){
                            long tim=data.get(position).receiveEndTimeStamp-new Date().getTime();
                            battleCouponRiqiLayout.setVisibility(View.VISIBLE);
                            if(tim>=60000) {
                                battleCouponRiqi.setText(DateUtils.formatTime(tim) + "后结束");
                            }else{
                                battleCouponRiqi.setText("1分后结束");
                            }

                            battleCouponStatus.setText("立即领取");
                        }else if(data.get(position).receiveEndTimeStamp-new Date().getTime()>=3*60*60*1000){
                            battleCouponRiqiLayout.setVisibility(View.GONE);
                            data.get(position).setCouponStatus("0");
                            battleCouponStatus.setText("立即领取");
                        }else if(data.get(position).receiveEndTimeStamp-new Date().getTime()<60000){
                            couponCenterItemLeftLayout.setBackgroundResource(R.drawable.coupon_pa);
                            couponCenterItemRightLayout.setBackgroundResource(R.drawable.coupon_pa_right);
                            battleCouponRiqiLayout.setVisibility(View.GONE);
                            data.get(position).setCouponStatus("5");
                            battleCouponStatus.setText("已过期");
                        }


                    }

                }else if(data.get(position).couponStatus.equals("3")){
                    battleCouponRiqiLayout.setVisibility(View.GONE);
                    battleCouponStatus.setText("已领取");
                    couponCenterItemLeftLayout.setBackgroundResource(R.drawable.coupon_al_left);
                    couponCenterItemRightLayout.setBackgroundResource(R.drawable.coupon_al_right);
                }else if(data.get(position).couponStatus.equals("4")){
                    battleCouponRiqiLayout.setVisibility(View.GONE);
                    battleCouponStatus.setText("已抢完");
                    couponCenterItemLeftLayout.setBackgroundResource(R.drawable.coupon_pa);
                    couponCenterItemRightLayout.setBackgroundResource(R.drawable.coupon_pa_right);
                }else if(data.get(position).couponStatus.equals("5")){
                    couponCenterItemLeftLayout.setBackgroundResource(R.drawable.coupon_pa);
                    couponCenterItemRightLayout.setBackgroundResource(R.drawable.coupon_pa_right);
                    battleCouponRiqiLayout.setVisibility(View.GONE);
                    battleCouponStatus.setText("已过期");
                }

                battleCouponType.setText(data.get(position).couponTypeName);
                battleCouponTitle.setText(data.get(position).couponDescription);
                if(data.get(position).projectDurationRange!=null&&data.get(position).projectDurationRange.equals("GE")){
                    battleCouponDate.setText("≥"+data.get(position).projectDuration+"个月");
                }else{
                    battleCouponDate.setText(data.get(position).projectDuration+"个月");
                }
                if(data.get(position).couponType.equals("FC")){
                    battleCouponType.setTextColor(Color.parseColor("#3887be"));
                    battleCouponAccount.setText("减"+data.get(position).subtractAmount);
                    battleCouponAccountDanwei.setVisibility(View.VISIBLE);
                    battleCouponLimit.setText("最低投资"+data.get(position).fullAmount+"元");
                }else if(data.get(position).couponType.equals("IA")){
                    battleCouponType.setTextColor(Color.parseColor("#6bc090"));
                    battleCouponAccount.setText("+" + Utils.getInstances().format(data.get(position).addingInterest * 100) + "%");
                    battleCouponAccountDanwei.setVisibility(View.GONE);
                    battleCouponLimit.setText("最低投资"+data.get(position).minAmount+"元");
                }else{
                    battleCouponType.setTextColor(Color.parseColor("#f0b442"));
                    battleCouponAccount.setText(data.get(position).experienceAmount);
                    battleCouponAccountDanwei.setVisibility(View.VISIBLE);
                    battleCouponLimit.setText("");
                }


            }
        });
    }

    @Override
    public int getLayoutResources() {
        return R.layout.battle_coupon_center_item;
    }
}
