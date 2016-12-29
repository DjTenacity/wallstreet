
package com.yeepbank.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.yeepbank.android.R;
import com.yeepbank.android.activity.business.ProjectDetailActivity;
import com.yeepbank.android.activity.business.TransformActivity;
import com.yeepbank.android.base.AbstractAdapter;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.model.business.NorProject;
import com.yeepbank.android.model.business.TranProject;
import com.yeepbank.android.utils.Utils;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by WW on 2015/11/10.
 */
public class TransProjectListAdapter extends AbstractAdapter<TranProject[]> {

    public TransProjectListAdapter(final List<TranProject[]> data, final Context context) {
        super(data, context, new IViewHolder() {
            View viewLeft,viewRight;
            TextView projectTitleLeft,durationTextLeft,percentIntegerTextLeft,percentDecimalTextLeft,
                    transferAmountTextLeft;
            TextView projectTitleRight,durationTextRight,percentIntegerTextRight,percentDecimalTextRight,
                    transferAmountTextRight;
            @Override
            public void initView(View view) {
                viewLeft = view.findViewById(R.id.left_project);
                viewRight = view.findViewById(R.id.right_project);


                projectTitleLeft = (TextView)viewLeft.findViewById(R.id.project_name);
                durationTextLeft = (TextView)viewLeft.findViewById(R.id.limit_day);
                percentIntegerTextLeft = (TextView) viewLeft.findViewById(R.id.percent_integer);
                percentDecimalTextLeft = (TextView)viewLeft.findViewById(R.id.percent_decimal);
                transferAmountTextLeft = (TextView)viewLeft.findViewById(R.id.transfer_amount);

                projectTitleRight = (TextView)viewRight.findViewById(R.id.project_name);
                durationTextRight = (TextView)viewRight.findViewById(R.id.limit_day);
                percentIntegerTextRight = (TextView) viewRight.findViewById(R.id.percent_integer);
                percentDecimalTextRight = (TextView)viewRight.findViewById(R.id.percent_decimal);
                transferAmountTextRight = (TextView)viewRight.findViewById(R.id.transfer_amount);
            }

            @Override
            public void fillData(int position) {
                viewLeft.setTag(data.get(position)[0]);
                viewRight.setTag(data.get(position)[1]);

                viewLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ((BaseActivity) context).gotoTarget(TransformActivity.class, R.anim.activity_in_from_right,
                                R.anim.activity_out_from_left, "", v.getTag());
                    }
                });
                viewRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ((BaseActivity) context).gotoTarget(TransformActivity.class, R.anim.activity_in_from_right,
                                R.anim.activity_out_from_left, "", v.getTag());
                    }
                });

                projectTitleLeft.setText(data.get(position)[0].projectTitle);

                durationTextLeft.setText(data.get(position)[0].buyerHoldingDays);
                percentIntegerTextLeft.setText(Utils.getInstances().formatUp(data.get(position)[0].buyerRoi * 100).split("\\.")[0]);
                percentDecimalTextLeft.setText("." + Utils.getInstances().formatUp(data.get(position)[0].buyerRoi * 100).split("\\.")[1]);
                transferAmountTextLeft.setText(Utils.getInstances().thousandFormatWithUnit(data.get(position)[0].transferPrice));

                if(data.get(position)[1] != null){
                    viewRight.setVisibility(View.VISIBLE);
                    projectTitleRight.setText(data.get(position)[1].projectTitle);
//                    durationTextRight.setText(data.get(position)[1].duration
//                            + NorProject.parseUnit(data.get(position)[1].durationUnit));
                    durationTextRight.setText(data.get(position)[1].buyerHoldingDays);
                    percentIntegerTextRight.setText(Utils.getInstances().formatUp(data.get(position)[1].buyerRoi * 100).split("\\.")[0]);
                    percentDecimalTextRight.setText("."+Utils.getInstances().formatUp(data.get(position)[1].buyerRoi * 100).split("\\.")[1]);
                    transferAmountTextRight.setText(Utils.getInstances().thousandFormatWithUnit(data.get(position)[1].transferPrice));
                }else {
                    viewRight.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public int getLayoutResources() {
        return R.layout.assignment_rights_layout;
    }
}

