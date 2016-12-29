package com.yeepbank.android.adapter;

import android.content.Context;
import android.view.View;
import android.widget.*;
import com.yeepbank.android.R;
import com.yeepbank.android.activity.business.PurchaseActivity;
import com.yeepbank.android.base.AbstractAdapter;
import com.yeepbank.android.model.business.Purchase;
import com.yeepbank.android.utils.Utils;

import java.util.List;

/**
 * Created by WW on 2015/11/16.
 */
public class PurchaseListAdapter extends AbstractAdapter<Purchase>{
    public PurchaseListAdapter(final List<Purchase> data, Context context) {
        super(data, context, new IViewHolder() {
            TextView radioText,rateText,rateUnit,explanText;
            TextView residualAmountText;
            @Override
            public void initView(View view) {
                radioText = (TextView) view.findViewById(R.id.project_name);//项目的名称
                residualAmountText = (TextView) view.findViewById(R.id.residual_amount);//剩余金额
                rateText = (TextView) view.findViewById(R.id.rate);//利率
                rateUnit = (TextView) view.findViewById(R.id.unit);
                explanText = (TextView) view.findViewById(R.id.explain);//项目名称的更近一步的介绍
            }

            @Override
            public void fillData(int position) {
                explanText.setText(data.get(position).projectTitle);
                radioText.setText(data.get(position).projectTypeName);
                residualAmountText.setText(Utils.getInstances().thousandFormat(data.get(position).requestAmount - data.get(position).biddingAmount));
                rateText.setText(Utils.getInstances().formatUp(data.get(position).interestRate * 100));
            }
        });
    }

    @Override
    public int getLayoutResources() {
        return R.layout.purcharge_list_item;
    }


}
