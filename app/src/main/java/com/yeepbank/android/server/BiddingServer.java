package com.yeepbank.android.server;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import com.android.volley.VolleyError;
import com.yeepbank.android.R;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.model.business.InvestListItem;
import com.yeepbank.android.model.business.RepaymentListItem;
import com.yeepbank.android.request.business.BiddingListRequest;
import com.yeepbank.android.request.business.RepaymentListRequest;
import com.yeepbank.android.response.business.BiddingListResponse;
import com.yeepbank.android.response.business.RepaymentListResponse;
import com.yeepbank.android.utils.LoadDialog;

import java.util.ArrayList;

/**
 * Created by WW on 2015/11/12.
 */
public class BiddingServer {

    public static BiddingServer biddingServer;
    private static Context mContext;
    private ArrayList<InvestListItem> investListItemArrayList;
    private ArrayList<RepaymentListItem> repaymentListItemArrayList;
    private static Handler msgHandler;

    private BiddingServer(){}

    public static BiddingServer instancesOf(Context context,Handler handler){
        synchronized (BiddingServer.class){
            if(biddingServer == null){
                biddingServer = new BiddingServer();
                msgHandler = handler;
                mContext = context;
            }

            return biddingServer;
        }
    }





    public ArrayList<InvestListItem> getInvestListItemArrayList(){
        if(investListItemArrayList == null){
            investListItemArrayList = new ArrayList<InvestListItem>();
        }
        return investListItemArrayList;
    }

    public ArrayList<RepaymentListItem> getRepaymentListItemArrayList(){
        if(repaymentListItemArrayList == null){
            repaymentListItemArrayList = new ArrayList<RepaymentListItem>();
        }
        return repaymentListItemArrayList;
    }
}
