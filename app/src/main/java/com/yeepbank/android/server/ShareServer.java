package com.yeepbank.android.server;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.yeepbank.android.R;

/**
 * Created by WW on 2015/11/27.
 */
public class ShareServer implements View.OnClickListener{

    private static ShareServer shareServer;
    private static PopupWindow popupWindow;
    private static Context mContext;
    private ImageButton shareToWeiXinBtn,shareToFriendsBtn;//分享到微信，分享到朋友圈
    private Button cancelBtn;//取消按钮
    private static String mTitle,mMessage;
    private static IWXAPI mIwxapi;


    private ShareServer(){}

    public static ShareServer getInstances(Context context,IWXAPI iwxapi,String title,String message){
        synchronized (ShareServer.class){
            popupWindow = null;
            shareServer = null;
            mContext = context;
            mTitle = title;
            mMessage = message;
            mIwxapi = iwxapi;
            shareServer = new ShareServer();
            return shareServer;
        }
    }

    private void createPopupWindow(){
        if(popupWindow == null){
            View view = LayoutInflater.from(mContext).inflate(R.layout.share,null);
            final View fnView = view.findViewById(R.id.fn_panel);
            view.setOnTouchListener(new View.OnTouchListener() {
                float fy;
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            fy = event.getY();
                            if(fy < fnView.getTop()){
                                popupWindow.dismiss();
                            }
                            return true;

                    }
                    return true;
                }
            });
            shareToWeiXinBtn = (ImageButton) view.findViewById(R.id.share_weixin_btn);
            shareToFriendsBtn = (ImageButton) view.findViewById(R.id.share_to_firends_btn);
            cancelBtn = (Button) view.findViewById(R.id.cancel_btn);
            shareToWeiXinBtn.setOnClickListener(this);
            shareToFriendsBtn.setOnClickListener(this);
            cancelBtn.setOnClickListener(this);
            popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
            popupWindow.setAnimationStyle(R.style.exist_style);
        }

    }

    public void show(View view){
        if(popupWindow == null){
            createPopupWindow();
        }
        popupWindow.showAsDropDown(view);
    }

    public void hide(){
        if(popupWindow != null && popupWindow.isShowing()){
            popupWindow.dismiss();

        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.share_weixin_btn:
                hide();
                wechatShare(0);
                break;
            case R.id.share_to_firends_btn:
                hide();
                wechatShare(1);
                break;
            case R.id.cancel_btn:
                hide();
                break;
        }
    }


    public void wechatShare(int flag){
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = "www.baidu.com";
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = mTitle;
        msg.description = mMessage;
        //这里替换一张自己工程里的图片资源
        Bitmap thumb = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.logo_icon);
        msg.setThumbImage(thumb);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = flag==0?SendMessageToWX.Req.WXSceneSession:SendMessageToWX.Req.WXSceneTimeline;
        mIwxapi.sendReq(req);
    }
}
