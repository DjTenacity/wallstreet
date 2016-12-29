package com.yeepbank.android.request.user;

import android.content.Context;
import com.yeepbank.android.Cst;
import com.yeepbank.android.http.BaseRequest;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.utils.DesUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by WW on 2016/4/25.
 */
public class ConfirmOrderRequest extends BaseRequest{

    private String userId,tradeMoney,orderCompleteTime,orderState,orderExplain,
            cardAfterFour,depositId,comId,ptdealerId,orderNo,orderTime,userNumber,location;

    public ConfirmOrderRequest(Context context, StringListener stringListener,String userId,String tradeMoney,
                               String orderCompleteTime,String orderState,String orderExplain,
                               String cardAfterFour,String depositId,String comId,String ptdealerId,
                               String orderNo,String orderTime,String userNumber,String location) {
        super(context, stringListener);
        this.userId = userId;this.tradeMoney = tradeMoney;
        this.orderCompleteTime = orderCompleteTime;
        this.orderState = orderState;
        this.orderExplain = orderExplain;this.cardAfterFour = cardAfterFour;
        this.depositId = depositId;this.comId = comId;this.ptdealerId = ptdealerId;
        this.orderNo = orderNo;this.orderTime = orderTime;this.userNumber = userNumber;
        this.location = location;
    }

    @Override
    protected String getUrl() {
        return Cst.URL.CONFIRM_ORDER_URL;
    }

    @Override
    protected int getMethod() {
        return METHOD_POST;
    }

    @Override
    protected Map<String, String> getParams() {
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("userId",userId);
        params.put("tradeMoney",tradeMoney);
        //params.put("orderCompleteTime",orderCompleteTime);
        params.put("orderState",orderState);
        params.put("orderExplain",orderExplain);
        params.put("cardAfterFour",cardAfterFour);
        params.put("depositId",depositId);
        params.put("comId", DesUtil.encrypt(comId));
        params.put("ptdealerId",ptdealerId);
        params.put("orderNo",orderNo);
        params.put("orderTime",orderTime);
        params.put("userNumber",userNumber);
        params.put("location",location);
        params.put("securetKey",Cst.currentUser.appSecuretKey);
        return params;
    }

    @Override
    public void stringRequest() {
        super.stringRequest();
    }
}
