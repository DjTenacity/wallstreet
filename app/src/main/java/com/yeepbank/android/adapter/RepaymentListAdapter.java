package com.yeepbank.android.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.yeepbank.android.R;
import com.yeepbank.android.base.AbstractAdapter;
import com.yeepbank.android.model.business.Repayment;
import com.yeepbank.android.model.business.RepaymentListItem;
import com.yeepbank.android.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WW on 2015/10/13.
 */
public class RepaymentListAdapter extends AbstractAdapter<RepaymentListItem> {

    public RepaymentListAdapter(final List<RepaymentListItem> data, Context context) {
        super(data, context, new AbstractAdapter.IViewHolder() {
            private TextView repayDate,principal,interest,status;
            @Override
            public void initView(View view) {
                repayDate = (TextView) view.findViewById(R.id.repayment_date);
                principal = (TextView) view.findViewById(R.id.principal);
                interest = (TextView) view.findViewById(R.id.interest);
                status = (TextView) view.findViewById(R.id.status);
            }

            @Override
            public void fillData(int position) {
                repayDate.setText(data.get(position).paymentDueDate);
                principal.setText(data.get(position).principal);
                interest.setText(data.get(position).interest);
                status.setText(data.get(position).statusName);
            }
        });
    }

    @Override
    public int getLayoutResources() {
        return R.layout.repayment_list_in_project_more;
    }
}
