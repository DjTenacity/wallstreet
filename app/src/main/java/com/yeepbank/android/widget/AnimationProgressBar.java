package com.yeepbank.android.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import com.yeepbank.android.R;

import java.util.Timer;
import java.util.TimerTask;
/**
 * Created by WW on 2015/10/16.
 */
public class AnimationProgressBar extends ProgressBar {
    private Handler handler;
    private Timer timer;
    private Context mContext;
    private final static int UPDATE_PROGRESS = 0;
    public AnimationProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init(context);
    }

    public AnimationProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case UPDATE_PROGRESS:
                        int progress = (int) msg.obj;
                        setProgress(getProgress()+5 > progress ? progress :getProgress()+5);
                        break;
                }
            }
        };



    }

    public synchronized void updateProgress(final int progress){

        new Thread(){
            @Override
            public void run() {
                final Timer timer = new Timer();
                //setProgress(10);
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Message msg = handler.obtainMessage();
                        msg.obj = progress;
                        msg.what = 0;
                        handler.sendMessage(msg);
                        if(getProgress() >= progress){
                            timer.cancel();
                        }
                    }
                },0,50);
            }
        }.start();
    }

}
