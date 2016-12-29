package com.yeepbank.android.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.yeepbank.android.R;
import com.yeepbank.android.base.AbstractAdapter;
import com.yeepbank.android.model.business.Purchase;
import com.yeepbank.android.utils.Utils;

import java.util.List;

/**
 * Created by WW on 2015/11/17.
 */
public class TurnoutAdapter extends AbstractAdapter<Purchase> {

    public TurnoutAdapter(final List<Purchase> data, Context context) {
        super(data, context, new IViewHolder() {
            TextView projectNameText,residualAmountText;
            @Override
            public void initView(View view) {
                projectNameText = (TextView) view.findViewById(R.id.project_name);
                residualAmountText = (TextView) view.findViewById(R.id.residual_amount);
            }

            @Override
            public void fillData(int position) {
                projectNameText.setText(data.get(position).projectTitle);
                residualAmountText.setText(Utils.getInstances().thousandFormat(data.get(position).biddingAmount));
            }
        });
    }

    @Override
    public int getLayoutResources() {
        return R.layout.trun_out_list_item;
    }
}
