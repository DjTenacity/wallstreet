package com.yeepbank.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.yeepbank.android.R;
import com.yeepbank.android.model.business.InvestListItem;
import com.yeepbank.android.model.user.InvestmentVo;
import com.yeepbank.android.model.user.NeProjectVo;
import com.yeepbank.android.utils.Utils;
import com.yeepbank.android.base.AbstractAdapter;

import java.util.List;

/**
 * Created by xiaogang.dong on 2015/11/9.
 * Ͷ
 */
public class InvestmentRecordWaitEndAdapter extends AbstractAdapter<NeProjectVo> {
    public InvestmentRecordWaitEndAdapter(final List<NeProjectVo> data, Context context) {
        super(data, context, new AbstractAdapter.IViewHolder() {
            private  TextView investmentMonth,investmentDate,investmentAmount,investmentType,investmentTimelinmit,investmentRate,dateUnit;
            private LinearLayout investmentWaitendCouponLayout;
            private TextView couponUseMessage;
            @Override
            public void initView(View view) {

                investmentMonth=(TextView)view.findViewById(R.id.invest_monthne);
                investmentDate=(TextView)view.findViewById(R.id.invest_datene);
                investmentAmount=(TextView)view.findViewById(R.id.invest_amountne);
                investmentType=(TextView)view.findViewById(R.id.invest_typene);
                investmentTimelinmit=(TextView)view.findViewById(R.id.invest_timelimitne);
                investmentRate=(TextView)view.findViewById(R.id.invest_ratene);
                dateUnit=(TextView)view.findViewById(R.id.date_unitne);
                investmentWaitendCouponLayout= (LinearLayout) view.findViewById(R.id.investment_waitend_coupon_layout);
                couponUseMessage= (TextView) view.findViewById(R.id.coupon_use);

            }

            @Override
            public void fillData(int position) {
                if(data.get(position).couponUse){
                    investmentWaitendCouponLayout.setVisibility(View.VISIBLE);
                    couponUseMessage.setText("("+data.get(position).couponRule+")");
                }else{
                    investmentWaitendCouponLayout.setVisibility(View.GONE);
                    couponUseMessage.setText("");
                }


                investmentMonth.setText(data.get(position).biddingMonth);
                investmentDate.setText("."+data.get(position).biddingDay);
                investmentAmount.setText(Utils.getInstances().thousandFormat(data.get(position).biddingAmount));
                investmentType.setText(data.get(position).projectTitle);
                investmentTimelinmit.setText(data.get(position).duration+"");
                dateUnit.setText(data.get(position).durationUnitName+"期");
                investmentRate.setText(Utils.getInstances().formatUp(data.get(position).interestRate * 100));
            }
        });
    }


    @Override
    public int getLayoutResources() {
        return R.layout.investment_record_waitend_item;
    }

}
