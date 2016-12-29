package com.yeepbank.android.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.yeepbank.android.R;
import com.yeepbank.android.base.AbstractAdapter;
import com.yeepbank.android.model.business.InvestListItem;
import com.yeepbank.android.model.user.Investor;
import com.yeepbank.android.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WW on 2015/10/13.
 */
public class InvestListAdapter extends AbstractAdapter<InvestListItem> {

    public InvestListAdapter(final List<InvestListItem> data, Context context) {
        super(data, context, new AbstractAdapter.IViewHolder() {
            private TextView investor,investMoney,investTime;
            @Override
            public void initView(View view) {
                investor = (TextView) view.findViewById(R.id.investor);
                investMoney = (TextView) view.findViewById(R.id.invest_money);
                investTime = (TextView) view.findViewById(R.id.invest_time);
            }

            @Override
            public void fillData(int position) {
                investor.setText(data.get(position).loginName);
                investMoney.setText(Utils.getInstances().format(data.get(position).biddingAmount));
                investTime.setText(data.get(position).biddingDatetime);
            }
        });
    }

    @Override
    public int getLayoutResources() {
        return R.layout.invest_list_item_in_project_more;
    }
}
