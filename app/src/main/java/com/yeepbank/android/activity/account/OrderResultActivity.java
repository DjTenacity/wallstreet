package com.yeepbank.android.activity.account;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import cn.com.kuaishua.sdk.entity.PayResult;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.kuaishua.tools.encrypt.StringUtil;
import com.yeepbank.android.Cst;
import com.yeepbank.android.R;
import com.yeepbank.android.activity.business.HomeActivity;
import com.yeepbank.android.activity.user.RunningAccountActivity;
import com.yeepbank.android.base.ActivityStacks;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.model.order.PdsDepositInfo;
import com.yeepbank.android.request.user.ConfirmOrderRequest;
import com.yeepbank.android.response.user.ConfirmOrderResponse;
import com.yeepbank.android.utils.LoadDialog;

public class OrderResultActivity extends BaseActivity implements OnClickListener{
	private TextView comid, money,time, cardno, orderExplain, state,recordTitleText;
	private ImageButton backProBtn,searchRechargeRecordBtn;
	private View navigationBar;
	private PayResult payResult;
	private PdsDepositInfo pdsDepositInfo;
	@Override
	protected void initView() {
		orderExplain = (TextView) findViewById(R.id.order_explain);
		recordTitleText = (TextView) findViewById(R.id.record_title);
		backProBtn = (ImageButton) findViewById(R.id.back_to_project_icon);backProBtn.setOnClickListener(this);
		searchRechargeRecordBtn = (ImageButton) findViewById(R.id.search_recharge_record);searchRechargeRecordBtn.setOnClickListener(this);
		comid = (TextView) findViewById(R.id.comid);
		money = (TextView) findViewById(R.id.recharge_money);
		time = (TextView) findViewById(R.id.time);
		cardno = (TextView) findViewById(R.id.cardno);
		navigationBar = findViewById(R.id.navigation_bar);
	}

	@Override
	protected void fillData() {
		Bundle bundle = getIntent().getExtras();
		payResult = (PayResult) bundle.get("payResult");
		Log.e("result",new Gson().toJson(payResult).toString());
		setData(payResult);
	}

	private void notifyServer(PayResult payResult) {
		if (payResult != null){
			loadding.showAs();
			ConfirmOrderRequest request = new ConfirmOrderRequest(mContext, new StringListener() {
				@Override
				public void ResponseListener(String result) {
					Log.e("LOG","返回："+result);
					loadding.dismiss();

					ConfirmOrderResponse response = new ConfirmOrderResponse();
					if (response.getStatus(result) == 200){
						pdsDepositInfo = response.getObject(result);
					}else {
						toast(response.getMessage(result));
					}
				}

				@Override
				public void ErrorListener(VolleyError volleyError) {
					loadding.dismiss();
					showErrorMsg(getString(R.string.net_error),navigationBar);
				}
			}, Cst.currentUser.investorId,payResult.getPayInfo().getTradeMoney().toPlainString(),payResult.getOrderCompleteTime(),
					payResult.getOrderState(),payResult.getOrderExplain(),payResult.getCardAfterFour() == null?"":payResult.getCardAfterFour(),
					PayByCardForRechargeActivity.payOrder.depositId,payResult.getPayInfo().getComId(),
					PayByCardForRechargeActivity.payOrder.ptdealerId,payResult.getPayInfo().getOrderNo(),
					payResult.getPayInfo().getOrderTime(),payResult.getPayInfo().getUserNumber(),payResult.getPayInfo().getLocation());
			request.stringRequest();
		}
	}

	private void setData(PayResult pdsDepositInfo) {
		if (pdsDepositInfo != null){
			if(pdsDepositInfo.getOrderState().equals("0000")){
				recordTitleText.setText("交易成功");
				recordTitleText.setTextColor(Color.parseColor("#3887BE"));
				orderExplain.setText("努力获取充值信息，请于一分钟后，到“资金流水”查看充值结果");

			}else {
				orderExplain.setText(pdsDepositInfo.getOrderExplain());
				recordTitleText.setText("交易失败");
				recordTitleText.setTextColor(Color.parseColor("#e96969"));

			}
			comid.setText(pdsDepositInfo.getPayInfo().getComId());
			money.setText(String.valueOf(pdsDepositInfo.getPayInfo().getTradeMoney()));
			time.setText(pdsDepositInfo.getOrderCompleteTime());
			if (!StringUtil.isBlank(pdsDepositInfo.getCardAfterFour())) {
				cardno.setText(pdsDepositInfo.getCardAfterFour());
			}
		}
	}


	@Override
	protected int getLayoutResources() {
		return R.layout.activity_orderresult;
	}

	@Override
	protected View getNavigationBar() {
		return navigationBar;
	}

	@Override
	public LoadDialog getLoadDialog() {
		return loadding;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.back_to_project_icon:
				ActivityStacks.getInstances().popToWitch(HomeActivity.class.getName());
				break;
			case R.id.search_recharge_record:
				gotoTargetRemovePre(RunningAccountActivity.class,R.anim.activity_in_from_right,
						R.anim.activity_out_from_left,"我的账户");
				break;
		}
	}

	@Override
	protected void onDestroy() {
		PayByCardForRechargeActivity.payOrder = null;
		super.onDestroy();
	}
}
