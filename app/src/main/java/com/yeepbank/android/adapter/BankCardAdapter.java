package com.yeepbank.android.adapter;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.yeepbank.android.R;
import com.yeepbank.android.base.AbstractAdapter;
import com.yeepbank.android.model.user.BankCard;

import java.util.List;

/**
 * Created by WW on 2015/11/18.
 * 显示银行卡列表适配器
 */
public class BankCardAdapter extends AbstractAdapter<BankCard>{

    public BankCardAdapter(final List<BankCard> data, final Context context) {
        super(data, context, new IViewHolder() {

            TextView bankNameText,bankCardMaskText;
   //         float sX;
 //           ImageButton clearBtn;
            private View view;
            @Override
            public void initView(View view) {
                this.view = view;
                bankNameText = (TextView) view.findViewById(R.id.bank_name);
                bankCardMaskText = (TextView) view.findViewById(R.id.bank_card_mask);
   //             clearBtn = (ImageButton) view.findViewById(R.id.delete_btn);

/*
                view.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()){
                            case MotionEvent.ACTION_DOWN:
                                sX = event.getX();
                                return true;
                            case MotionEvent.ACTION_UP:
                            case MotionEvent.ACTION_CANCEL:
                                float dX = event.getX();
                                if(dX - sX < -10){
                                    showClearBtn(view);
                                }else if(dX - sX > 10){
                                    hideClearBtn(view);
                                }
                                return true;
                        }
                        return true;
                    }
                });
*/
            }

            private void showClearBtn(View view) {
                ImageButton clearBtn = (ImageButton) view.findViewById(R.id.delete_btn);
                if(clearBtn.getVisibility() == View.VISIBLE){
                    return;
                }
                Animation animation = AnimationUtils.loadAnimation(context,R.anim.activity_from_xsmall_to_xbig);
                clearBtn.startAnimation(animation);
                clearBtn.setVisibility(View.VISIBLE);
            }

            private void hideClearBtn(View view) {
                final ImageButton clearBtn = (ImageButton) view.findViewById(R.id.delete_btn);
                if(clearBtn.getVisibility() == View.GONE){
                    return;
                }
                Animation animation = AnimationUtils.loadAnimation(context,R.anim.activity_from_xbig_to_xsmall);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        clearBtn.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                clearBtn.startAnimation(animation);

            }

            private int getBgResourcesByBankCode(String bankCode){
                String name = bankCode.toLowerCase();
                int value = R.drawable.bankcarddefaultbg;
                if (null != name) {
                    if (name.indexOf(".") != -1) {
                        name = name.substring(0, name.indexOf("."));
                    }
                    Class<com.yeepbank.android.R.drawable> cls = R.drawable.class;
                    try {
                        value = cls.getDeclaredField(name).getInt(null);
                    } catch (Exception e) {

                    }
                }
                return value;
            }

            @Override
            public void fillData(int position) {
                bankNameText.setText(data.get(position).bankName);
                bankCardMaskText.setText(data.get(position).bankCardNoMask);
                view.setBackgroundResource(getBgResourcesByBankCode(data.get(position).bankCode));
                /*
                clearBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, data.get(position).bankName+"卡", Toast.LENGTH_SHORT).show();
                    }
                });
                */
            }
        });
    }



    @Override
    public int getLayoutResources() {
        return R.layout.bank_card_list_item;
    }
}
