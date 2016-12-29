package com.yeepbank.android.adapter;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.yeepbank.android.R;
import com.yeepbank.android.base.AbstractAdapter;
import com.yeepbank.android.base.BaseModel;
import com.yeepbank.android.utils.Utils;
import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by WW on 2015/11/23.
 */
public class ProjectDetailAdapter extends AbstractAdapter<BaseModel> {
    public ProjectDetailAdapter(final List<BaseModel> data, Context context) {
        super(data, context, new IViewHolder() {
            private LinearLayout personalBusinessLayout,companyBusinessTransferLayout,companyBusniessBiddingLayout,projectInfoLayout;

            /*
            * 项目信息
            * */
            private TextView projectNameText,borrowMoneyText,rateText,borrowDurationText,repayMethodText;

            /*
            * 个人业务
            * */
            private TextView nameText,phoneText,IDCardText;
            private TextView personBorrownIncome,personBorrownAssets,personBorrownDebt,personBorrownBusiness,personBorrownDefaults;//年收入，主要财产，主要债务,主要收入来源,是否有银行还款违约记录
            /*
            * 企业转债
            * */

            private TextView businessNameText,chiefNameTransText,chiefPhoneTransText,chiefIdCardTransText,commercialNoTransText,
                    businessNameTrans2Text;

            /*
            * 企业挂标
            * */

            private TextView businessNameBiddingText,chiefNameBiddingText,
                    chiefPhoneBiddingText,chiefIdCardBiddingText,commercialNoBiddingText;
            private TextView companyBorrownIncome,companyBorrownAssets,companyBorrownDebt,companyBorrownBusiness,companyBorrownDefaults;//年收入，主要财产，主要债务,主要收入来源,是否有银行还款违约记录
            private TextView guaranteeInformationText,mortgageInformationText,useWayText,explainText;
            private TextView productType,productLocation,repaymentResource,creditComment;//产品类型，产地，还款来源,信用评价
            @Override
            public void initView(View view) {

                personalBusinessLayout = (LinearLayout) view.findViewById(R.id.personal_layout);
                companyBusinessTransferLayout = (LinearLayout) view.findViewById(R.id.transfer_layout);
                companyBusniessBiddingLayout = (LinearLayout) view.findViewById(R.id.bidding_layout);
                projectInfoLayout = (LinearLayout) view.findViewById(R.id.project_info_layout);

                /*
                * 项目信息
                * */
                projectNameText = (TextView) projectInfoLayout.findViewById(R.id.project_name);
                borrowMoneyText = (TextView) projectInfoLayout.findViewById(R.id.borrow_money);
                rateText = (TextView) projectInfoLayout.findViewById(R.id.rate);
                borrowDurationText = (TextView) projectInfoLayout.findViewById(R.id.borrow_duration);
                repayMethodText = (TextView) projectInfoLayout.findViewById(R.id.repayment_method);

                 /*
                * 个人业务
                * */
                nameText = (TextView) personalBusinessLayout.findViewById(R.id.name);

                phoneText = (TextView) personalBusinessLayout.findViewById(R.id.phone);

                IDCardText = (TextView) personalBusinessLayout.findViewById(R.id.id_card);

                personBorrownIncome= (TextView) personalBusinessLayout.findViewById(R.id.person_borrown_income);
                personBorrownAssets= (TextView) personalBusinessLayout.findViewById(R.id.person_borrown_assets);
                personBorrownBusiness= (TextView) personalBusinessLayout.findViewById(R.id.person_borrown_business);
                personBorrownDebt= (TextView) personalBusinessLayout.findViewById(R.id.person_borrown_debt);
                personBorrownDefaults= (TextView) personalBusinessLayout.findViewById(R.id.person_borrown_defaults);
                /*
                * 企业转债
                * */

                businessNameText = (TextView) companyBusinessTransferLayout.findViewById(R.id.business_name_trans);

                chiefNameTransText = (TextView) companyBusinessTransferLayout.findViewById(R.id.chief_name_trans);

                chiefPhoneTransText = (TextView) companyBusinessTransferLayout.findViewById(R.id.chief_phone_trans);
                chiefIdCardTransText = (TextView) companyBusinessTransferLayout.findViewById(R.id.chief_id_card_trans);

                commercialNoTransText = (TextView) companyBusinessTransferLayout.findViewById(R.id.commercial_no_trans);
                businessNameTrans2Text = (TextView) companyBusinessTransferLayout.findViewById(R.id.business_name_trans_2);

                /*
                * 企业挂标
                * */
                businessNameBiddingText = (TextView) companyBusniessBiddingLayout.findViewById(R.id.business_name_bidding);
                chiefNameBiddingText = (TextView) companyBusniessBiddingLayout.findViewById(R.id.chief_name_bidding);

                chiefPhoneBiddingText = (TextView) companyBusniessBiddingLayout.findViewById(R.id.chief_phone_bidding);
                chiefIdCardBiddingText = (TextView) companyBusniessBiddingLayout.findViewById(R.id.chief_id_card_bidding);
                commercialNoBiddingText = (TextView) companyBusniessBiddingLayout.findViewById(R.id.commercial_no_bidding);

                companyBorrownIncome= (TextView) companyBusniessBiddingLayout.findViewById(R.id.company_borrown_income);
                companyBorrownAssets= (TextView) companyBusniessBiddingLayout.findViewById(R.id.company_borrown_assets);
                companyBorrownDebt= (TextView) companyBusniessBiddingLayout.findViewById(R.id.company_borrown_debt);
                companyBorrownBusiness=(TextView) companyBusniessBiddingLayout.findViewById(R.id.company_borrown_business);
                companyBorrownDefaults=(TextView) companyBusniessBiddingLayout.findViewById(R.id.company_borrown_defaults);


                useWayText = (TextView) view.findViewById(R.id.use_way);

                explainText = (TextView) view.findViewById(R.id.explain);

                guaranteeInformationText = (TextView) view.findViewById(R.id.guarantee_information);

                mortgageInformationText = (TextView) view.findViewById(R.id.mortgage_information);
                productType= (TextView) view.findViewById(R.id.product_type);
                productLocation= (TextView) view.findViewById(R.id.product_location);
                repaymentResource= (TextView) view.findViewById(R.id.repayment_resource);
                creditComment= (TextView) view.findViewById(R.id.credit_comment);
                /*
                * 企业转债
                * */

            }

            @Override
            public void fillData(int position) {
                if(data.get(position).borrowerType.equals("P")){
                    personalBusinessLayout.setVisibility(View.VISIBLE);
                    companyBusinessTransferLayout.setVisibility(View.GONE);
                    companyBusniessBiddingLayout.setVisibility(View.GONE);
                    productType.setText("个人挂标");
                }else if(data.get(position).borrowerType.equals("B")){
                    if (data.get(position).hangingType.equals("T")){
                        personalBusinessLayout.setVisibility(View.GONE);
                        companyBusinessTransferLayout.setVisibility(View.VISIBLE);
                        companyBusniessBiddingLayout.setVisibility(View.GONE);
                        productType.setText("企业委托挂标");
                    }else if(data.get(position).hangingType.equals("R")){
                        personalBusinessLayout.setVisibility(View.GONE);
                        companyBusinessTransferLayout.setVisibility(View.GONE);
                        companyBusniessBiddingLayout.setVisibility(View.VISIBLE);
                        productType.setText("企业直接挂标");
                    }
                }
                /*
                *
                * 项目信息
                * */
                projectNameText.setText(data.get(position).projectName);
                borrowMoneyText.setText(Utils.getInstances().thousandFormatWithUnit(data.get(position).requestAmount));
                rateText.setText(Utils.getInstances().formatUp(data.get(position).interestRate * 100)+"%");
                borrowDurationText.setText(data.get(position).duration+BaseModel.parseUnit(data.get(position).durationUnit));
                repayMethodText.setText(data.get(position).repaymentTypeName);

                 /*
                * 个人业务
                * */
                nameText.setText(data.get(position).borrowName);
                phoneText.setText(data.get(position).borrowMobileMask);
                IDCardText.setText(data.get(position).borrowIdNoMask);
                personBorrownIncome.setText(data.get(position).income);
                personBorrownAssets.setText(data.get(position).assets);
                personBorrownBusiness.setText(data.get(position).business);
                personBorrownDebt.setText(data.get(position).debt);
                personBorrownDefaults.setText(data.get(position).defaults);
                /*
                * 企业转债
                * */

                businessNameText.setText(data.get(position).businessName);
                chiefNameTransText.setText(data.get(position).legalPersonNameMask);
                chiefPhoneTransText.setText(data.get(position).legalPersonMobileMask);
                chiefIdCardTransText.setText(data.get(position).legalPersonIdNoMask);
                commercialNoTransText.setText(data.get(position).businessLicenseNoMask);
                businessNameTrans2Text.setText(data.get(position).borrowName);
                /*
                * 企业挂标
                * */

                businessNameBiddingText.setText(data.get(position).businessName);
                chiefNameBiddingText.setText(data.get(position).legalPersonNameMask);
                chiefPhoneBiddingText.setText(data.get(position).legalPersonMobileMask);
                chiefIdCardBiddingText.setText(data.get(position).legalPersonIdNoMask);
                commercialNoBiddingText.setText(data.get(position).businessLicenseNoMask);
                companyBorrownIncome.setText(data.get(position).income);
                companyBorrownAssets.setText(data.get(position).assets);
                companyBorrownBusiness.setText(data.get(position).business);
                companyBorrownDebt.setText(data.get(position).debt);
                companyBorrownDefaults.setText(data.get(position).defaults);


                useWayText.setText(data.get(position).purpose);
                explainText.setText(Utils.getInstances().ToDBC(data.get(position).description));
                guaranteeInformationText.setText(data.get(position).guarantee);
                mortgageInformationText.setText(data.get(position).mortgage);
                productLocation.setText(data.get(position).location);
                repaymentResource.setText(data.get(position).repaymentSource);
                creditComment.setText(data.get(position).creditComment);

                projectInfoLayout.setVisibility(View.VISIBLE);
                if(data.get(position).transferId!=null && !data.get(position).transferId.trim().equals("")){
                    projectInfoLayout.setVisibility(View.VISIBLE);
                }else {
                    projectInfoLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public int getLayoutResources() {
        return R.layout.more_detail_project_detail;
    }


}
