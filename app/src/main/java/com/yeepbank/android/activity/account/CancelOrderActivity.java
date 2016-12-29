package com.yeepbank.android.activity.account;

import android.app.Activity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import com.android.volley.VolleyError;
import com.kuaishua.tools.encrypt.ExitApplication;
import com.yeepbank.android.Cst;
import com.yeepbank.android.R;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.request.user.CancelOrderRequest;
import com.yeepbank.android.response.user.CancelOrderResponse;

public class CancelOrderActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ExitApplication.getInstance().addTradeActivity(this);
		setContentView(R.layout.activity_cancelorder);
		WindowManager m = getWindowManager();
		Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
		android.view.WindowManager.LayoutParams p = getWindow().getAttributes();

		p.width = (int) (d.getWidth() * 0.7); // 宽度设置为屏幕的0.7
		getWindow().setAttributes(p);

		Button button = (Button) findViewById(R.id.buttonexit);

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				notifyServer();
				finish();
			}
		});
	}

	/*
	* 通知server取消订单
	* */
	private void notifyServer() {
		if (PayByCardForRechargeActivity.payOrder == null){
			return;
		}
		CancelOrderRequest request = new CancelOrderRequest(this, new StringListener() {
			@Override
			public void ResponseListener(String result) {
				CancelOrderResponse response = new CancelOrderResponse();
				if (response.getStatus(result) == 200){

				}
			}

			@Override
			public void ErrorListener(VolleyError volleyError) {

			}
		}, Cst.currentUser.investorId,PayByCardForRechargeActivity.payOrder.tradeMoney,
				PayByCardForRechargeActivity.payOrder.depositId,
				PayByCardForRechargeActivity.payOrder.orderNo,
				PayByCardForRechargeActivity.payOrder.location);
		request.stringRequest();
	}

	@Override
	protected void onDestroy() {
		PayByCardForRechargeActivity.payOrder = null;
		super.onDestroy();
	}
}
