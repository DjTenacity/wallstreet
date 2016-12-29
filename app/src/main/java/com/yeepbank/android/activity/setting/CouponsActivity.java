package com.yeepbank.android.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import com.android.volley.VolleyError;
import com.yeepbank.android.Cst;
import com.yeepbank.android.R;
import com.yeepbank.android.activity.business.HomeActivity;
import com.yeepbank.android.adapter.CouponsListAdapter;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.model.SocketMsg;
import com.yeepbank.android.model.Web;
import com.yeepbank.android.model.user.CouponsVo;
import com.yeepbank.android.request.user.ChooseCouponsRequest;
import com.yeepbank.android.request.user.CouponsRequest;
import com.yeepbank.android.response.user.CouponsResponse;
import com.yeepbank.android.utils.LoadDialog;
import com.yeepbank.android.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by WW on 2015/11/21.
 */
public class CouponsActivity extends BaseActivity implements View.OnClickListener{

    private ListView couponsList;
    private View navigationBar;
    private CouponsListAdapter couponsListAdapter;
    private ArrayList<CouponsVo> couponsVoArrayList;
    private ArrayList<CouponsVo> canUsedCouponsVoArrayList;
    private ArrayList<CouponsVo> canNotUsedCouponsVoArrayList;
    private LoadDialog alertDialog;
    private String filter = null;
    private ImageView raidersImg,notUsedImg;//攻略

    @Override
    protected void initView() {

        navigationBar = findViewById(R.id.navigation_bar);
        raidersImg = (ImageView) navigationBar.findViewById(R.id.raiders);
        raidersImg.setOnClickListener(this);
        notUsedImg = (ImageView) navigationBar.findViewById(R.id.not_used);
        notUsedImg.setOnClickListener(this);

        couponsList = (ListView) findViewById(R.id.tick_list);
        couponsListAdapter = new CouponsListAdapter(mContext,new ArrayList<CouponsVo>());
        couponsList.setAdapter(couponsListAdapter);
        canUsedCouponsVoArrayList = new ArrayList<CouponsVo>();
        canNotUsedCouponsVoArrayList = new ArrayList<CouponsVo>();

        Intent intent = getIntent();
        if(intent != null){
            Bundle bundle = intent.getBundleExtra("data");
            if(bundle != null){
                filter = (String) bundle.getSerializable("data");
            }
        }
    }

    private void loadData() {
        loadding.showAs();
        CouponsRequest request = new CouponsRequest(mContext, new StringListener() {
            @Override
            public void ResponseListener(String result) {
                dismiss();
                CouponsResponse response = new CouponsResponse();
                if(response.getStatus(result) == 200){

                    if(couponsVoArrayList!= null){
                        couponsVoArrayList.clear();
                    }
                    couponsVoArrayList = response.getObject(result);
                    setData();
                }else {
                    toast(response.getMessage(result));
                }
            }

            @Override
            public void ErrorListener(VolleyError volleyError) {
                dismiss();
                showErrorMsg(getString(R.string.net_error),navigationBar);
            }
        }, Cst.currentUser.investorId,"");
        request.stringRequest();
    }

    private void setData(){
        if(canUsedCouponsVoArrayList != null){
            canUsedCouponsVoArrayList.clear();
        }
        if(canNotUsedCouponsVoArrayList != null){
            canNotUsedCouponsVoArrayList.clear();
        }
        for (int i = 0; i < couponsVoArrayList.size(); i++) {
            if(couponsVoArrayList.get(i).status.trim().equals("B")){
//                couponsVoArrayList.get(i).choose = false;
//                if(Cst.couponId!= null && couponsVoArrayList.get(i).couponId.equals(Cst.couponId)){
//                    couponsVoArrayList.get(i).choose = true;
//                }
                canUsedCouponsVoArrayList.add(couponsVoArrayList.get(i));
            }else {
                canNotUsedCouponsVoArrayList.add(couponsVoArrayList.get(i));
            }
        }
        CouponsVo selfCoupon = new CouponsVo();
        selfCoupon.status = "SELF";
        couponsListAdapter.getData().clear();
        couponsListAdapter.getData().addAll(canUsedCouponsVoArrayList);
        couponsListAdapter.getData().add(selfCoupon);
        couponsListAdapter.getData().addAll(canNotUsedCouponsVoArrayList);
        couponsListAdapter.notifyDataSetChanged();

    }

    @Override
    protected void fillData() {

        /*
         * 获取socket信息 显示小红点
         * */
        HashMap<String,ArrayList<SocketMsg>> socketMsgHashMap = Utils.getInstances().getMeFragmentRedDocFromSharedPreference(mContext);

        /*
                * 判断如果投资券上有小红点，点击后小红点消失
                * */
        if (socketMsgHashMap!= null &&
                socketMsgHashMap.get("investTick") != null &&
                socketMsgHashMap.get("investTick").size() > 0){
            socketMsgHashMap.get("investTick").clear();
            Utils.getInstances().putMeFragmentRedDocToSharedPreference(mContext,socketMsgHashMap);
        }
        couponsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (couponsListAdapter.getData().get(position).status.trim().equals("B")) {
                    if (filter != null && filter.length() > 0) {

                        double needMoney = 0;
                        if (couponsListAdapter.getData().get(position).couponType.equals("FC")) {
                            needMoney = couponsListAdapter.getData().get(position).fullAmount;
                        } else if (couponsListAdapter.getData().get(position).couponType.equals("IA")) {
                            needMoney = couponsListAdapter.getData().get(position).minAmount;
                        }
//                        } else if (couponsListAdapter.getData().get(position).couponType.equals("EC")) {
//                            needMoney = couponsListAdapter.getData().get(position).experienceAmount;
//                        }

                        if (HomeActivity.totalAssets.freeBalance < needMoney
                                && !couponsListAdapter.getData().get(position).couponType.equals("EC")) {
                            if (alertDialog == null) {
                                alertDialog = new LoadDialog(mContext, R.style.dialog, false, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();
                                    }
                                }, 0).setTitle("提示").setMessage("可用余额不足所选投资券最低使用金额，无法使用此券，请先充值");
                                alertDialog.setSureBtn("我知道了");
                            }
                            alertDialog.showAs();
                        } else {
                            ImageView imageView = (ImageView) view.findViewById(R.id.tick_choose);
                            imageView.setVisibility(View.VISIBLE);
                            Intent intent = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("data", couponsListAdapter.getData().get(position));
                            Cst.couponId = couponsListAdapter.getData().get(position).couponId;
                            intent.putExtra("data", bundle);
                            setResult(1, intent);
                            finish();
                        }
//                        ask(imageView, couponsListAdapter.getData().get(position));
                    }
                } else if (couponsListAdapter.getData().get(position).status.trim().equals("SELF")) {
                    gotoTargetForResult(ExchangeInvestmentActivity.class, R.anim.activity_in_from_right,
                            R.anim.activity_out_from_left, "投资券", 1);
                }
            }
        });
        if(filter == null || filter.length() == 0){
            notUsedImg.setVisibility(View.GONE);
            raidersImg.setVisibility(View.VISIBLE);
            loadData();
        }else {
            notUsedImg.setVisibility(View.VISIBLE);
            raidersImg.setVisibility(View.GONE);
            loadCanUsedData();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
                if(filter == null || filter.length() == 0){
                    loadData();
                }else {
                    loadCanUsedData();
                }
                break;

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loadCanUsedData() {
        loadding.showAs();
        ChooseCouponsRequest request = new ChooseCouponsRequest(mContext, new StringListener() {
            @Override
            public void ResponseListener(String result) {
                dismiss();
                CouponsResponse response = new CouponsResponse();
                if(response.getStatus(result) == 200){
                    couponsVoArrayList = response.getObject(result);
                    setChooseData();
                }
            }

            @Override
            public void ErrorListener(VolleyError volleyError) {
                dismiss();
                showErrorMsg(getString(R.string.net_error),navigationBar);
            }
        },Cst.currentUser.investorId,filter);
        request.stringRequest();
    }

    private void setChooseData() {
        couponsListAdapter.getData().clear();
        for (int i = 0; i < couponsVoArrayList.size(); i++) {
            couponsVoArrayList.get(i).choose = false;
            if(Cst.couponId!= null && Cst.couponId.equals(couponsVoArrayList.get(i).couponId)){
                couponsVoArrayList.get(i).choose = true;
            }
        }
        couponsListAdapter.getData().addAll(couponsVoArrayList);
        couponsListAdapter.notifyDataSetChanged();
    }

    private void ask(final ImageView imageView, final CouponsVo couponsVo) {
        if(alertDialog == null){
            alertDialog = new LoadDialog(mContext, R.style.dialog, false, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageView.setVisibility(View.VISIBLE);
                    alertDialog.dismiss();
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("data",couponsVo);
                    intent.putExtra("data", bundle);
                    setResult(1, intent);
                    finish();
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageView.setVisibility(View.GONE);
                    alertDialog.dismiss();
                }
            },0).setTitle("提示").setMessage("确定使用该投资券");
            alertDialog.setSureBtn("确定");
            alertDialog.setCancelBtn("取消");
        }
        alertDialog.showAs();
    }

    @Override
    protected int getLayoutResources() {
        return R.layout.coupons;
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
            case R.id.not_used:
                Cst.couponId = null;
                notUsedImg.setVisibility(View.GONE);
                raidersImg.setVisibility(View.VISIBLE);
                refreshBankCardStyle();
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", null);
                intent.putExtra("data", bundle);
                setResult(1, intent);
                finish();
                break;
            case R.id.raiders:
                gotoWeb(new Web("投资券攻略", Cst.URL.RAIDERS_DETAIL_URL, "投资券"));
                break;
        }
    }



    private void refreshBankCardStyle(){
        for (int i = 0; i < couponsList.getChildCount(); i++) {
            View view = couponsList.getChildAt(i);
            if (couponsListAdapter.getData().get(i).status.trim().equals("B")) {
                ImageView imageView = (ImageView) view.findViewById(R.id.tick_choose);
                if(Cst.couponId!= null && couponsListAdapter.getData().get(i).couponId.equals(Cst.couponId)){
                    imageView.setVisibility(View.VISIBLE);
                }else {
                    imageView.setVisibility(View.GONE);
                }
            }
        }
    }
    @Override
    protected void onDestroy() {
        if(loadding != null && loadding.isShowing()){
            loadding.dismiss();
        }
        if(alertDialog!=null&&alertDialog.isShowing()){
            alertDialog.dismiss();
        }
        super.onDestroy();
    }
}
