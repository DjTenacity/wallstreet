package com.yeepbank.android.adapter;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.yeepbank.android.R;
import com.yeepbank.android.base.AbstractAdapter;
import com.yeepbank.android.model.user.BankCard;

import java.util.List;

/**
 * Created by WW on 2015/11/17.
 */
public class BankListAdapter extends AbstractAdapter<BankCard> {

    public BankListAdapter(final List<BankCard> data, Context context) {
        super(data, context, new IViewHolder() {
            TextView bankText;
            @Override
            public void initView(View view) {
                bankText = (TextView) view.findViewById(R.id.bank_card_text);
            }

            @Override
            public void fillData(int position) {
                bankText.setText(data.get(position).bankInfo);
            }
        });
    }

    @Override
    public int getLayoutResources() {
        return R.layout.bank_list_item;
    }
}
