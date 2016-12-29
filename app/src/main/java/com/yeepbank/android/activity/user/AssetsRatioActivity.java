package com.yeepbank.android.activity.user;
import android.view.View;
import android.widget.TextView;
import com.android.volley.VolleyError;

import com.yeepbank.android.Cst;
import com.yeepbank.android.R;
import com.yeepbank.android.activity.business.HomeActivity;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.model.business.EdmOverview;
import com.yeepbank.android.request.business.DailyIncreaseRequest;
import com.yeepbank.android.response.business.DailyIncreaseResponse;
import com.yeepbank.android.utils.LoadDialog;
import com.yeepbank.android.utils.Utils;
import com.yeepbank.android.widget.PieChartView;
import java.util.ArrayList;

/**
 * Created by WW on 2015/11/7.
 */
public class AssetsRatioActivity extends BaseActivity {
    private PieChartView pieChartView;
    private EdmOverview edmOverview;
    private ArrayList<String> proNameList;
    private TextView freeBalancePercentText,freeBalanceAmountText,edmFreeBalancePercentText,edmFreeBalanceText,
            totalPIReceivablePercentText,totalPIReceivableText,waitingBiddingAmountPercentText,
            waitingBiddingAmountText,withDrawingAmountPercent,withDrawingAmountText,totalAssetsText;
    private View navigationBar;
    @Override
    protected void initView() {
        proNameList = new ArrayList<>();

        pieChartView = (PieChartView) findViewById(R.id.pie_chart);
        freeBalancePercentText = (TextView) findViewById(R.id.free_balance_percent);
        freeBalanceAmountText = (TextView) findViewById(R.id.free_balance);
        edmFreeBalancePercentText = (TextView) findViewById(R.id.edm_free_balance_percent);
        edmFreeBalanceText = (TextView) findViewById(R.id.edm_free_balance);
        totalPIReceivablePercentText = (TextView) findViewById(R.id.totalPIReceivable_percent);
        totalPIReceivableText = (TextView) findViewById(R.id.totalPIReceivable);
        waitingBiddingAmountPercentText = (TextView) findViewById(R.id.waitingBiddingAmount_percent);
        waitingBiddingAmountText = (TextView) findViewById(R.id.waitingBiddingAmount);
        withDrawingAmountPercent = (TextView) findViewById(R.id.withDrawingAmount_percent);
        withDrawingAmountText = (TextView) findViewById(R.id.withDrawingAmount);
        totalAssetsText = (TextView) findViewById(R.id.total_assets);
        navigationBar = findViewById(R.id.navigation_bar);
        loadData();



    }

    private void loadData() {

        if(Cst.currentUser != null){
            loadding.showAs();
            DailyIncreaseRequest request = new DailyIncreaseRequest(mContext, new StringListener() {
                @Override
                public void ResponseListener(String result) {
                    dismiss();
                    DailyIncreaseResponse response = new DailyIncreaseResponse();
                    if(response.getStatus(result) == 200){
                        edmOverview = response.getObject(result);
                        initChart();
                    }else {
                        toast(response.getMessage(result));
                    }
                }

                @Override
                public void ErrorListener(VolleyError volleyError) {
                    dismiss();
                    showErrorMsg(getString(R.string.net_error),navigationBar);
                }
            },Cst.currentUser.investorId);
            request.stringRequest();
        }
    }

    private void initChart() {

        freeBalancePercentText.setText(getPercent(HomeActivity.totalAssets.freeBalance));//剩余金额
        freeBalanceAmountText.setText(Utils.getInstances().thousandFormat(HomeActivity.totalAssets.freeBalance));
        totalPIReceivablePercentText.setText(getPercent(HomeActivity.totalAssets.totalPIReceivable));//定期待收本息
        totalPIReceivableText.setText(Utils.getInstances().thousandFormat(HomeActivity.totalAssets.totalPIReceivable));
        waitingBiddingAmountPercentText.setText(getPercent(HomeActivity.totalAssets.waitingBiddingAmountWithoutECcoupon));//定期待结标
        waitingBiddingAmountText.setText(Utils.getInstances().thousandFormat(HomeActivity.totalAssets.waitingBiddingAmountWithoutECcoupon));
        withDrawingAmountPercent.setText(getPercent(HomeActivity.totalAssets.withDrawingAmount));//提现中
        withDrawingAmountText.setText(Utils.getInstances().thousandFormat(HomeActivity.totalAssets.withDrawingAmount));
        totalAssetsText.setText(Utils.getInstances().thousandFormat(HomeActivity.totalAssets.totalAssets));//总资产
        edmFreeBalancePercentText.setText(getPercent(edmOverview.totalBiddingAmountToday));//天天盈持有金额
        edmFreeBalanceText.setText(Utils.getInstances().thousandFormat(edmOverview.totalBiddingAmountToday));

        getPieData();
    }

    @Override
    protected void fillData() {

    }

    @Override
    protected int getLayoutResources() {
        return R.layout.assets_ratio;
    }

    @Override
    protected View getNavigationBar() {
        ((TextView)navigationBar.findViewById(R.id.label_text)).setText("");
        return navigationBar;
    }

    private void getPieData() {
        float totalValue = 0;
        totalValue+=(float) HomeActivity.totalAssets.freeBalance;
        totalValue+=(float) edmOverview.totalBiddingAmountToday;
        totalValue+=(float) HomeActivity.totalAssets.totalPIReceivable;
        totalValue+=(float) HomeActivity.totalAssets.waitingBiddingAmountWithoutECcoupon;
        totalValue+=(float) HomeActivity.totalAssets.withDrawingAmount;
        if(totalValue == 0){
            return;
        }
        float gap1 = totalValue*0.003f,gap2 = totalValue*0.003f,gap3 = totalValue*0.003f,gap4 = totalValue*0.003f,gap5 = totalValue*0.003f;
        if(HomeActivity.totalAssets.freeBalance == 0 || Utils.getInstances().format(HomeActivity.totalAssets.freeBalance).equals(Utils.getInstances().format(totalValue))){
            gap1 = 0;
        }
        if(edmOverview.totalBiddingAmountToday == 0 || Utils.getInstances().format(edmOverview.totalBiddingAmountToday).equals(Utils.getInstances().format(totalValue))){
            gap2 = 0;
        }
        if(HomeActivity.totalAssets.totalPIReceivable  == 0 || Utils.getInstances().format(HomeActivity.totalAssets.totalPIReceivable).equals(Utils.getInstances().format(totalValue))){
            gap3 = 0;
        }
        if(HomeActivity.totalAssets.waitingBiddingAmountWithoutECcoupon  == 0 || Utils.getInstances().format(HomeActivity.totalAssets.waitingBiddingAmountWithoutECcoupon).equals(Utils.getInstances().format(totalValue))){
            gap4 = 0;
        }
        if(HomeActivity.totalAssets.withDrawingAmount == 0 || Utils.getInstances().format(HomeActivity.totalAssets.withDrawingAmount).equals(Utils.getInstances().format(totalValue))){
            gap5 = 0;
        }

        PieChartView.PieItemBean[] items = new PieChartView.PieItemBean[]{
                new PieChartView.PieItemBean("可用余额：", (float) HomeActivity.totalAssets.freeBalance),
                new PieChartView.PieItemBean("空隙：", gap1),
                new PieChartView.PieItemBean("天天盈持有：", (float) edmOverview.totalBiddingAmountToday),
                new PieChartView.PieItemBean("空隙：", gap2),
                new PieChartView.PieItemBean("定期待收本息：", (float) HomeActivity.totalAssets.totalPIReceivable),
                new PieChartView.PieItemBean("空隙：", gap3),
                new PieChartView.PieItemBean("定期待结镖：", (float) HomeActivity.totalAssets.waitingBiddingAmountWithoutECcoupon),
                new PieChartView.PieItemBean("空隙：", gap4),
                new PieChartView.PieItemBean("提现中：", (float) HomeActivity.totalAssets.withDrawingAmount),
                new PieChartView.PieItemBean("空隙：", gap5),
        };
        pieChartView.setPieItems(items);

    };


    private String getPercent(double value){
        double totalMoney = HomeActivity.totalAssets.freeBalance+
                edmOverview.autoAmount+
                HomeActivity.totalAssets.totalPIReceivable+
                HomeActivity.totalAssets.waitingBiddingAmount+
                HomeActivity.totalAssets.withDrawingAmount;
        if(totalMoney == 0){
            return "0%";
        }
        return Utils.getInstances().format(value / (HomeActivity.totalAssets.freeBalance +
                edmOverview.autoAmount +
                HomeActivity.totalAssets.totalPIReceivable +
                HomeActivity.totalAssets.waitingBiddingAmount +
                HomeActivity.totalAssets.withDrawingAmount) * 100)+"%";
    }

    @Override
    protected void onDestroy() {
        if(loadding != null && loadding.isShowing()){
            loadding.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public LoadDialog getLoadDialog() {
        return loadding;
    }
}
