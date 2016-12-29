package com.yeepbank.android.activity.user;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.android.volley.VolleyError;
import com.yeepbank.android.Cst;
import com.yeepbank.android.R;
import com.yeepbank.android.activity.setting.RealNameAuthenticationActivity;
import com.yeepbank.android.adapter.BankCardAdapter;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.model.user.BankCard;
import com.yeepbank.android.request.user.BankListRequest;
import com.yeepbank.android.request.user.HasRealNameRequest;
import com.yeepbank.android.response.user.BankListResponse;
import com.yeepbank.android.response.user.HasRealNameResponse;
import com.yeepbank.android.utils.LoadDialog;
import com.yeepbank.android.utils.Utils;

import java.util.ArrayList;

/**
 * Created by WW on 2015/11/18.
 */
public class BankListActivity extends BaseActivity implements View.OnClickListener{

    private View navigationBar;
    private ListView bankCardList;
    private BankCardAdapter bankCardAdapter;
    private ArrayList<BankCard> bankCardArrayList;
    private LinearLayout addNewBankCardLayout;
    private LoadDialog msgDialog,alertDialog;
    private int WITCH_DO = 0;//0表示未登录，1表示未实名认证，2表示非新手投资新手项目
    @Override
    protected void initView() {
        navigationBar = findViewById(R.id.navigation_bar);
        bankCardList = (ListView) findViewById(R.id.bank_card_list);
        addNewBankCardLayout = (LinearLayout) findViewById(R.id.add_new_bank_card);
        addNewBankCardLayout.setOnClickListener(this);
        bankCardAdapter = new BankCardAdapter(new ArrayList<BankCard>(),mContext);
        bankCardList.setAdapter(bankCardAdapter);

    }


    private void createMsgDialog(){
        msgDialog = new LoadDialog(mContext, R.style.dialog, false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msgDialog.dismiss();
                if(WITCH_DO == 1){
                    gotoTarget(RealNameAuthenticationActivity.class, R.anim.activity_in_from_right, R.anim.activity_out_from_left, "");
                }

            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msgDialog.dismiss();
            }
        },0);
        msgDialog.setSureBtn("立即登录");
        msgDialog.setCancelBtn("再看看");
    }

    private void createAlertDialog(){
        alertDialog = new LoadDialog(mContext, R.style.dialog, false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        },0).setMessage("您已做过投资，请把机会留给新手吧").setSureBtn("我知道了");
    }

    @Override
    protected void fillData() {
        loadData();
    }

    @Override
    protected void onResume() {
        loadData();
        super.onResume();
    }

    /*
        * 获取银行卡列表
        * */
    private void loadData(){
        loadding.showAs();
        BankListRequest request = new BankListRequest(mContext, new StringListener() {
            @Override
            public void ResponseListener(String result) {
                dismiss();
                BankListResponse response = new BankListResponse();
                if(response.getStatus(result) == 200){
                    if(bankCardArrayList != null){
                        bankCardArrayList.clear();
                    }
                    bankCardArrayList = response.getObject(result);
                    if (bankCardArrayList.size() > 0){
                        for (int i = 0; i < bankCardArrayList.size(); i++){
                            BankCard bankCard = bankCardArrayList.get(i);
                            if (bankCard.useType.trim().equals("Q")){
                                addNewBankCardLayout.setVisibility(View.GONE);
                                break;
                            }else if (i == bankCardArrayList.size() - 1){
                                addNewBankCardLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    bankCardAdapter.getData().clear();
                    bankCardAdapter.getData().addAll(bankCardArrayList);
                    bankCardAdapter.notifyDataSetChanged();
                }else {
                    toast(response.getMessage(result));
                }
            }

            @Override
            public void ErrorListener(VolleyError volleyError) {
                dismiss();
                showErrorMsg(getString(R.string.net_error),null);
            }
        }, Cst.currentUser.investorId,"W,Q");
        request.stringRequest();
    }

    @Override
    protected int getLayoutResources() {
        return R.layout.bank_card_list;
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
            case R.id.add_new_bank_card:
                if(Cst.currentUser.idAuthFlag!= null && Cst.currentUser.idAuthFlag.equals("Y")){
                    gotoTarget(AddNewBankCardActivity.class, R.anim.activity_in_from_right,
                            R.anim.activity_out_from_left, "");
                }else {
                    hasRealName();
                }

                break;
        }
    }


    /*
    * 检查是否实名认证
    * */
    private void hasRealName() {
        HasRealNameRequest request = new HasRealNameRequest(mContext, new StringListener() {
            @Override
            public void ResponseListener(String result) {
                HasRealNameResponse response = new HasRealNameResponse();
                if(response.getStatus(result) == 200){
                    Cst.currentUser.idAuthFlag = response.getObject(result);
                    if(Cst.currentUser.idAuthFlag!= null && Cst.currentUser.idAuthFlag.equals("Y")){
                        Utils.getInstances().putInvestorToSharedPreference(mContext, Cst.currentUser);
                        gotoTarget(AddNewBankCardActivity.class, R.anim.activity_in_from_right,
                                R.anim.activity_out_from_left, "");
                    }else if(Cst.currentUser.idAuthFlag!= null && Cst.currentUser.idAuthFlag.equals("P")){
                        if(alertDialog == null){
                            createAlertDialog();
                        }
                        alertDialog.setMessage("您的实名认证信息正在审核,暂时无法投资/提现/充值,我们会加紧审核");
                        alertDialog.setSureBtn("我知道了");
                        alertDialog.showAs();
                    }else if(Cst.currentUser.idAuthFlag!= null && Cst.currentUser.idAuthFlag.equals("N")){
                        if(msgDialog == null){
                            createMsgDialog();
                        }
                        WITCH_DO = 1;
                        msgDialog.setMessage("您的实名认证信息审核未通过,是否重新认证");
                        msgDialog.setSureBtn("重新认证");
                        msgDialog.setCancelBtn("取消");
                        msgDialog.showAs();
                    }else {
                        if(msgDialog == null){
                            createMsgDialog();
                        }
                        WITCH_DO = 1;
                        msgDialog.setMessage("完成实名认证后可投资，是否立即实名认证");
                        msgDialog.setSureBtn("实名认证");
                        msgDialog.setCancelBtn("再看看");
                        msgDialog.showAs();
                    }
                }else {
                    toast(response.getMessage(result));
                }
            }

            @Override
            public void ErrorListener(VolleyError volleyError) {
                showErrorMsg(getString(R.string.net_error),navigationBar);
            }
        }, Cst.currentUser.investorId);
        request.stringRequest();
    }
    @Override
    protected void onDestroy() {
        if(loadding != null && loadding.isShowing()){
            loadding.dismiss();
        }
        if(msgDialog!=null&&msgDialog.isShowing()){
            msgDialog.dismiss();
        }
        if(alertDialog!=null&&alertDialog.isShowing()){
            alertDialog.dismiss();
        }
        super.onDestroy();
    }
}
