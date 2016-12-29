package com.yeepbank.android.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.yeepbank.android.R;
import com.yeepbank.android.base.AbstractAdapter;
import com.yeepbank.android.model.user.InvestmentVo;
import com.yeepbank.android.model.user.NeProjectVo;
import com.yeepbank.android.utils.Utils;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by xiaogang.dong on 2015/11/9.
 * Ͷ投资记录的还款中，已还款的适配器
 */
public class InvestmentRecordAdapter extends AbstractAdapter<InvestmentVo> {
    public InvestmentRecordAdapter(final List<InvestmentVo> data, Context context) {
        super(data, context, new AbstractAdapter.IViewHolder() {
            private TextView investmentMonth,investmentDate,investmentAmount,investmentType,investmentTimelinmit,investmentRate,dateLimit,dateUnit;
            private LinearLayout bondSignLayout;//转债标志的布局
            private LinearLayout investmentSignLayout;//有关投资券的信息布局
            private TextView investmentSignTextView;//有关的投资券的信息
            private TextView principalAndInterestText;//有关本息的情况
            private ImageView imageLayout;//item布局左边的图片
            @Override
            public void initView(View view) {
                investmentMonth=(TextView)view.findViewById(R.id.invest_month);
                investmentDate=(TextView)view.findViewById(R.id.invest_date);
                investmentAmount=(TextView)view.findViewById(R.id.invest_amount);
                investmentType=(TextView)view.findViewById(R.id.invest_type);
                investmentTimelinmit=(TextView)view.findViewById(R.id.invest_timelimit);
                dateUnit= (TextView) view.findViewById(R.id.date_unit);
                investmentRate=(TextView)view.findViewById(R.id.invest_rate);
                dateLimit= (TextView) view.findViewById(R.id.date_limit);
                bondSignLayout= (LinearLayout) view.findViewById(R.id.bond_sign_layout);
                investmentSignLayout= (LinearLayout) view.findViewById(R.id.investment_sign_layout);
                investmentSignTextView= (TextView) view.findViewById(R.id.investment_sign_text);
                principalAndInterestText= (TextView) view.findViewById(R.id.principal_and_interest_text);
                imageLayout= (ImageView) view.findViewById(R.id.image_layout);
            }
            @Override
            public void fillData(int position) {
                //如果此项目是转债项目
                if(data.get(position).hasTransfer) {
                    bondSignLayout.setVisibility(View.VISIBLE);
                    investmentMonth.setText(data.get(position).biddingMonth);
                    investmentDate.setText("." + data.get(position).biddingDay);

                    investmentAmount.setText(Utils.getInstances().thousandFormat(data.get(position).transferPrice));
                    if (data.get(position).couponRule!=null&&data.get(position).couponRule.equals("")) {
                        investmentSignLayout.setVisibility(View.GONE);
                        imageLayout.setImageResource(R.drawable.investment_line_two);
                    } else if(data.get(position).couponRule!=null&&!data.get(position).couponRule.equals("")){
                        investmentSignLayout.setVisibility(View.VISIBLE);
                        investmentSignTextView.setText("("+data.get(position).couponRule+")");
                        imageLayout.setImageResource(R.drawable.investment_line_three);
                    }

                    investmentType.setText(data.get(position).transferId+"");
                    investmentTimelinmit.setText(data.get(position).buyerHoldingDays + "");
                    dateUnit.setText("天");
                    investmentRate.setText(Utils.getInstances().formatUp(data.get(position).buyerRoi * 100));
                    if (data.get(position).investmentStatus.equals("IRP")) {
                        String strs = "待收本息" + Utils.getInstances().thousandFormat(data.get(position).receivable);
                        Spannable spans = new SpannableString(strs);
                        spans.setSpan(new ForegroundColorSpan(Color.parseColor("#666666")), 4, strs.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        principalAndInterestText.setText(spans);

                        String str = "预计还款日期" + data.get(position).debtEndDate;
                        Spannable span = new SpannableString(str);
                        span.setSpan(new ForegroundColorSpan(Color.rgb(56, 136, 191)), 6, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        dateLimit.setText(span);
                    } else if (data.get(position).investmentStatus.equals("NCL")) {
                        String strs = "已收本息" + Utils.getInstances().thousandFormat(data.get(position).receivable);
                        Spannable spans = new SpannableString(strs);
                        spans.setSpan(new ForegroundColorSpan(Color.parseColor("#666666")), 4, strs.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        principalAndInterestText.setText(spans);

                        dateLimit.setText("还款日期" + data.get(position).debtEndDate);
                    }
                }else{
                    //如果此项目是普通项目（非转债项目）
                    bondSignLayout.setVisibility(View.INVISIBLE);
                    investmentMonth.setText(data.get(position).biddingMonth);
                    investmentDate.setText("." + data.get(position).biddingDay);
                    investmentAmount.setText(Utils.getInstances().thousandFormat(data.get(position).investmentPrice));
                    if (data.get(position).couponRule!=null&&data.get(position).couponRule.equals("")) {
                        investmentSignLayout.setVisibility(View.GONE);
                        imageLayout.setImageResource(R.drawable.investment_line_two);
                    } else if(data.get(position).couponRule!=null&&!data.get(position).couponRule.equals("")){
                        investmentSignLayout.setVisibility(View.VISIBLE);
                        investmentSignTextView.setText("("+data.get(position).couponRule+")");
                        imageLayout.setImageResource(R.drawable.investment_line_three);
                    }
                    investmentType.setText(data.get(position).projectTitle);
                    investmentTimelinmit.setText(data.get(position).duration + "");
                    dateUnit.setText(data.get(position).durationUnitName + "期");
                    investmentRate.setText(Utils.getInstances().formatUp(data.get(position).interestRate * 100));
                    if (data.get(position).investmentStatus.equals("IRP")) {
                        String strs = "待收本息" + Utils.getInstances().thousandFormat(data.get(position).receivable);
                        Spannable spans = new SpannableString(strs);
                        spans.setSpan(new ForegroundColorSpan(Color.parseColor("#666666")), 4, strs.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        principalAndInterestText.setText(spans);

                        String str = "预计还款日期" + data.get(position).planPeriodEndDate;
                        Spannable span = new SpannableString(str);
                        span.setSpan(new ForegroundColorSpan(Color.rgb(56, 136, 191)), 6, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        dateLimit.setText(span);
                    } else if (data.get(position).investmentStatus.equals("NCL")) {
                        String strs = "已收本息" + Utils.getInstances().thousandFormat(data.get(position).receivable);
                        Spannable spans = new SpannableString(strs);
                        spans.setSpan(new ForegroundColorSpan(Color.parseColor("#666666")), 4, strs.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        principalAndInterestText.setText(spans);

                        dateLimit.setText("还款日期" + data.get(position).planPaymentDueDate);
                    }

                }

            }
        });
    }
    @Override
    public int getLayoutResources() {
        return R.layout.investmentrecord_item;
    }

}
