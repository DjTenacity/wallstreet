package com.yeepbank.android.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.gson.Gson;
import com.yeepbank.android.Cst;
import com.yeepbank.android.LaunchActivity;
import com.yeepbank.android.R;
import com.yeepbank.android.WebSocketService;
import com.yeepbank.android.activity.business.HomeActivity;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Created by WW on 2015/8/26.
 */
public class NotifyManagerUtil {

    private Context mContext;
    public static PushMessage message;

    public NotifyManagerUtil(Context mContext) {
        this.mContext = mContext;
    }

    public void notify(int id,String title,String content){
        message = new Gson().fromJson(content,PushMessage.class);
        message.currentTimes = new Date().getTime();
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification();
        notification.icon = R.drawable.logo_notify;
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.when = System.currentTimeMillis();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
       //notification.tickerText = title;
        Intent intent = null;

        if (message.activeCode.equals("1")){
            intent = new Intent(mContext, HomeActivity.class);
            intent.setAction("PUSH_MESSAGE");
            intent.putExtra("target","RECHARGE_NOTIFY");
        }else if (message.activeCode.equals("2")){
            intent = new Intent(mContext, HomeActivity.class);
            intent.setAction("PUSH_MESSAGE");
            intent.putExtra("projectId", message.infoId);
        }else if (message.activeCode.equals("3")){
            intent = new Intent(mContext, HomeActivity.class);
            intent.setAction("PUSH_MESSAGE");
            intent.putExtra("target","investTickActivity");
        }else if (message.activeCode.equals("4")){
            intent = new Intent(mContext, HomeActivity.class);
            intent.setAction("PUSH_MESSAGE");
            intent.putExtra("target","BIDDING_END");
        }else if (message.activeCode.equals("5")){
            intent = new Intent(mContext, HomeActivity.class);
            intent.setAction("PUSH_MESSAGE");
            intent.putExtra("target","BIDDING_END_REPAYMENTED");
        }else if (message.activeCode.equals("6")){
            intent = new Intent(mContext, HomeActivity.class);
            intent.setAction("PUSH_MESSAGE");
            intent.putExtra("target","WITHDRAW_NOTIFY");
        }else if(message.activeCode.equals("7")){
            intent = new Intent(mContext, HomeActivity.class);
            intent.setAction("PUSH_MESSAGE");
            intent.putExtra("target","BATTLE_COUPON_NOTIFY");
        }
        //sendBorad(message);

        if (intent != null){
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, UUID.randomUUID().hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            notification.setLatestEventInfo(mContext,title,message.msg,pendingIntent);
            notificationManager.notify(id,notification);
        }
    }

    private void sendBorad(PushMessage message) {
//        Intent intent = new Intent(WebSocketService.ACTION);
//        intent.putExtra("cid", Cst.PUSH_CLIENT_ID);
//        mContext.sendBroadcast(intent);

        Intent webSocketIntent = new Intent(WebSocketService.ACTION);
        Bundle bundle = new Bundle();
        message.currentTimes = new Date().getTime();
        bundle.putSerializable("push_message",message);
        webSocketIntent.putExtra("push_message", bundle);
        mContext.sendBroadcast(webSocketIntent);
    }

    public class PushMessage implements Serializable{
       public String activeCode;
       public String msg;
       public String infoId;
        public long currentTimes;
    }
}
