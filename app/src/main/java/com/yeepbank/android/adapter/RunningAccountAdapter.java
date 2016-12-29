package com.yeepbank.android.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.yeepbank.android.R;
import com.yeepbank.android.base.AbstractAdapter;
import com.yeepbank.android.model.user.RunningAccount;

import java.util.List;

/**
 * Created by WW on 2016/3/16.
 */
public class RunningAccountAdapter extends AbstractAdapter<RunningAccount> {

    public RunningAccountAdapter(final List<RunningAccount> data, Context context) {
        super(data, context, new IViewHolder() {
            private TextView applyText,happenDateTime,
                    happenTimeText,happenMoneyText,happenStateText,
                    giveText,finishDateText,finishTimeText,failReasonText,withdrawRequestDatetime,withdrawApproveDatetime;
            private ImageView successFlagView;
            private LinearLayout withdrawalsLayout;


            @Override
            public void initView(View view) {
                applyText = (TextView) view.findViewById(R.id.apply_time);
                happenDateTime = (TextView) view.findViewById(R.id.happen_date);
                happenTimeText = (TextView) view.findViewById(R.id.happen_time);
                happenMoneyText = (TextView) view.findViewById(R.id.happen_money);
                happenStateText = (TextView) view.findViewById(R.id.happen_state);

                successFlagView = (ImageView) view.findViewById(R.id.open_btn);
                giveText = (TextView) view.findViewById(R.id.give_text);
                withdrawRequestDatetime= (TextView) view.findViewById(R.id.withdraw_requestDatetime);
                withdrawApproveDatetime= (TextView) view.findViewById(R.id.withdraw_approveDatetime);
                failReasonText = (TextView) view.findViewById(R.id.fail_reason);
                withdrawalsLayout = (LinearLayout) view.findViewById(R.id.withdrawals_content);
            }

            @Override
            public void fillData(int position) {
                RunningAccount runningAccount = data.get(position);
                if(runningAccount.reasonIsOpen){
                    failReasonText.setVisibility(View.VISIBLE);
                    successFlagView.findViewById(R.id.open_btn).setRotationX(180f);
                }else {
                    failReasonText.setVisibility(View.GONE);
                    successFlagView.findViewById(R.id.open_btn).setRotationX(0f);
                }

                if(runningAccount.type.equals("recharge")){
                    withdrawalsLayout.setVisibility(View.GONE);
                    applyText.setVisibility(View.GONE);
                    happenDateTime.setText(runningAccount.happenDate);
                    happenTimeText.setText(runningAccount.happenTime);
                    happenMoneyText.setText(runningAccount.happenMoney);
                    //finishDateText.setText(runningAccount.finishDate);
                    //finishTimeText.setText(runningAccount.finishTime);
                    if(runningAccount.success.equals("F")){
                        failReasonText.setText(runningAccount.failReason);
                        happenStateText.setText(runningAccount.stateName);
                        happenStateText.setTextColor(Color.parseColor("#fa886d"));
                        successFlagView.setVisibility(View.VISIBLE);

                    }else if(runningAccount.success.equals("S")){
                        happenStateText.setText(runningAccount.stateName);
                        happenStateText.setTextColor(Color.parseColor("#3887be"));
                        successFlagView.setVisibility(View.INVISIBLE);
                    }else {
                        happenStateText.setText(runningAccount.stateName);
                        happenStateText.setTextColor(Color.parseColor("#999999"));
                        successFlagView.setVisibility(View.INVISIBLE);
                    }
                }else if(runningAccount.type.equals("withdrawals")){
                    if(runningAccount.success.equals("E")){
                        withdrawalsLayout.setVisibility(View.VISIBLE);
                        withdrawApproveDatetime.setText(runningAccount.approveDatetime);
                    }else{
                        withdrawalsLayout.setVisibility(View.GONE);
                    }
                    applyText.setVisibility(View.VISIBLE);

                    happenDateTime.setVisibility(View.GONE);
                    happenTimeText.setVisibility(View.GONE);
                    withdrawRequestDatetime.setVisibility(View.VISIBLE);
                    withdrawRequestDatetime.setText(runningAccount.requestDatetime);
                    happenMoneyText.setText(runningAccount.happenMoney);


                    successFlagView.setVisibility(View.INVISIBLE);
                    happenStateText.setText(runningAccount.stateName);
                    if(runningAccount.success.equals("E")){
                        happenStateText.setTextColor(Color.parseColor("#666666"));
                    }else {
                        happenStateText.setTextColor(Color.parseColor("#999999"));
                    }

                }
            }
        });
    }

    @Override
    public int getLayoutResources() {
        return R.layout.running_account_list_item;
    }
}
