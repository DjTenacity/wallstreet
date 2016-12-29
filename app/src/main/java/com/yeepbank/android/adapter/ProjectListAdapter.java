package com.yeepbank.android.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.yeepbank.android.R;
import com.yeepbank.android.activity.business.ProjectDetailActivity;
import com.yeepbank.android.activity.business.SecKillActivity;
import com.yeepbank.android.base.AbstractAdapter;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.base.BaseModel;
import com.yeepbank.android.model.business.NorProject;
import com.yeepbank.android.model.business.Project;
import com.yeepbank.android.utils.Utils;
import com.yeepbank.android.widget.AnimationProgressBar;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by WW on 2015/10/13.
 */
public class ProjectListAdapter extends AbstractAdapter<BaseModel[]> {


    public ProjectListAdapter(final List<BaseModel[]> data, final Context context) {
        super(data, context, new AbstractAdapter.IViewHolder() {
            private View leftProject,rightProject;
            private TextView titleTextLeft,monthTextLeft,progressPercentTextLeft,progressDecimalTextLeft,residualAmountTextLeft;
            private AnimationProgressBar progressBarLeft,progressBarRight;
            private TextView titleTextRight,monthTextRight,progressPercentTextRight,progressDecimalTextRight,residualAmountTextRight;
            private ImageView newImgLeft,newImgRight,experienceImgLeft,experienceImgRight,
                    fullCutImgLeft,fullCutImgRight,increaseInterestImgLeft,increaseInterestImgRight;
            @Override
            public void initView(View view) {
                leftProject = view.findViewById(R.id.left_project);
                rightProject = view.findViewById(R.id.right_project);

                titleTextLeft = (TextView) leftProject.findViewById(R.id.recommend_title);
                monthTextLeft = (TextView) leftProject.findViewById(R.id.time_limit);
                progressPercentTextLeft = (TextView) leftProject.findViewById(R.id.progress_percent_text);
                progressDecimalTextLeft = (TextView) leftProject.findViewById(R.id.progress_decimal_text);
                progressBarLeft = (AnimationProgressBar) leftProject.findViewById(R.id.progress);
                residualAmountTextLeft = (TextView) leftProject.findViewById(R.id.residual_amount);
                newImgLeft = (ImageView) leftProject.findViewById(R.id.new_flag);
                experienceImgLeft = (ImageView) leftProject.findViewById(R.id.experience);
                fullCutImgLeft = (ImageView) leftProject.findViewById(R.id.full_cut);
                increaseInterestImgLeft = (ImageView) leftProject.findViewById(R.id.increase_interest);

                titleTextRight = (TextView) rightProject.findViewById(R.id.recommend_title);
                monthTextRight = (TextView) rightProject.findViewById(R.id.time_limit);
                progressPercentTextRight = (TextView) rightProject.findViewById(R.id.progress_percent_text);
                progressDecimalTextRight = (TextView) rightProject.findViewById(R.id.progress_decimal_text);
                progressBarRight = (AnimationProgressBar) rightProject.findViewById(R.id.progress);
                residualAmountTextRight = (TextView) rightProject.findViewById(R.id.residual_amount);
                newImgRight = (ImageView) rightProject.findViewById(R.id.new_flag);
                experienceImgRight = (ImageView) rightProject.findViewById(R.id.experience);
                fullCutImgRight = (ImageView) rightProject.findViewById(R.id.full_cut);
                increaseInterestImgRight = (ImageView) rightProject.findViewById(R.id.increase_interest);
            }

            @Override
            public void fillData(int position) {
                leftProject.setTag(data.get(position)[0]);
                rightProject.setTag(data.get(position)[1]);
                leftProject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (((BaseModel) v.getTag()).projectType.equals("SEC")) {
                            ((BaseActivity) context).gotoTarget(SecKillActivity.class, R.anim.activity_in_from_right,
                                    R.anim.activity_out_from_left, "", v.getTag());
                        } else {
                            ((BaseActivity) context).gotoTarget(ProjectDetailActivity.class, R.anim.activity_in_from_right,
                                    R.anim.activity_out_from_left, "", v.getTag());
                        }
                    }
                });
                rightProject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (((BaseModel) v.getTag()).projectType.equals("SEC")) {
                            ((BaseActivity) context).gotoTarget(SecKillActivity.class, R.anim.activity_in_from_right,
                                    R.anim.activity_out_from_left, "", v.getTag());
                        } else {
                            ((BaseActivity) context).gotoTarget(ProjectDetailActivity.class, R.anim.activity_in_from_right,
                                    R.anim.activity_out_from_left, "", v.getTag());
                        }
                    }
                });
                titleTextLeft.setText(data.get(position)[0].projectTitle);
                SpannableString sb=new SpannableString(data.get(position)[0].duration+Project.parseUnit(data.get(position)[0].durationUnit));
                sb.setSpan(new AbsoluteSizeSpan(55), 0, data.get(position)[0].duration.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                monthTextLeft.setText(sb);
                progressPercentTextLeft.setText(Utils.getInstances().formatUp(data.get(position)[0].interestRate * 100).split("\\.")[0]);
                progressDecimalTextLeft.setText("."+Utils.getInstances().formatUp(data.get(position)[0].interestRate * 100).split("\\.")[1]);
                progressBarLeft.setProgress(0);
                progressBarLeft.updateProgress((int) (data.get(position)[0].biddingAmount * 100
                        / data.get(position)[0].requestAmount));

                residualAmountTextLeft.setText(Utils.getInstances().formatWithUnit(data.get(position)[0].requestAmount
                        - data.get(position)[0].biddingAmount, 1));

                if(data.get(position)[0].projectType.trim().equals("FFI")){//新手
                    newImgLeft.setVisibility(View.VISIBLE);
                    newImgLeft.setImageResource(R.drawable.recommendnewicon);
                }else if(data.get(position)[0].projectType.trim().equals("SEC")){//秒杀
                    newImgLeft.setVisibility(View.VISIBLE);
                    newImgLeft.setImageResource(R.drawable.sec_icon);
                }else {
                    newImgLeft.setVisibility(View.INVISIBLE);
                }


                if(data.get(position)[0].couponEcFlag != null && data.get(position)[0].couponEcFlag.equals("Y")){
                    experienceImgLeft.setVisibility(View.VISIBLE);
                }else {
                    experienceImgLeft.setVisibility(View.GONE);
                }

                if(data.get(position)[0].couponFcFlag != null && data.get(position)[0].couponFcFlag.equals("Y")){
                    fullCutImgLeft.setVisibility(View.VISIBLE);
                }else {
                    fullCutImgLeft.setVisibility(View.GONE);
                }

                if(data.get(position)[0].couponIaFlag != null && data.get(position)[0].couponIaFlag.equals("Y")){
                    increaseInterestImgLeft.setVisibility(View.VISIBLE);
                }else {
                    increaseInterestImgLeft.setVisibility(View.GONE);
                }

                if(data.get(position)[1] != null){
                    rightProject.setVisibility(View.VISIBLE);
                    titleTextRight.setText(data.get(position)[1].projectTitle);
                    SpannableString sbb=new SpannableString(data.get(position)[1].duration+Project.parseUnit(data.get(position)[1].durationUnit));
                    sbb.setSpan(new AbsoluteSizeSpan(55), 0, data.get(position)[1].duration.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    monthTextRight.setText(sbb);

                    progressPercentTextRight.setText(Utils.getInstances().format(data.get(position)[1].interestRate * 100).split("\\.")[0]);
                    progressDecimalTextRight.setText("."+Utils.getInstances().format(data.get(position)[1].interestRate * 100).split("\\.")[1]);
                    progressBarRight.setProgress(0);
                    progressBarRight.updateProgress((int) (data.get(position)[1].biddingAmount * 100
                            / data.get(position)[1].requestAmount));
                    residualAmountTextRight.setText(Utils.getInstances().formatWithUnit(data.get(position)[0].requestAmount
                            - data.get(position)[0].biddingAmount, 1));

                    if(data.get(position)[1].projectType.trim().equals("FFI")){//新手
                        newImgRight.setVisibility(View.VISIBLE);
                        newImgRight.setImageResource(R.drawable.recommendnewicon);
                    }else if(data.get(position)[1].projectType.trim().equals("SEC")){//秒杀
                        newImgRight.setVisibility(View.VISIBLE);
                        newImgRight.setImageResource(R.drawable.sec_icon);
                    }else {
                        newImgRight.setVisibility(View.INVISIBLE);
                    }

                    if(data.get(position)[1].couponEcFlag != null && data.get(position)[1].couponEcFlag.equals("Y")){
                        experienceImgRight.setVisibility(View.VISIBLE);
                    }else {
                        experienceImgRight.setVisibility(View.GONE);
                    }

                    if(data.get(position)[1].couponFcFlag != null && data.get(position)[1].couponFcFlag.equals("Y")){
                        fullCutImgRight.setVisibility(View.VISIBLE);
                    }else {
                        fullCutImgRight.setVisibility(View.GONE);
                    }

                    if(data.get(position)[1].couponIaFlag != null && data.get(position)[1].couponIaFlag.equals("Y")){
                        increaseInterestImgRight.setVisibility(View.VISIBLE);
                    }else {
                        increaseInterestImgRight.setVisibility(View.GONE);
                    }

                }else {
                    rightProject.setVisibility(View.INVISIBLE);
                }

            }
        });
    }

    @Override
    public int getLayoutResources() {
        return R.layout.project_list_item;
    }


}
