package com.yeepbank.android.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by WW on 2015/11/23.
 */
public class BannerAdapter extends PagerAdapter {

    public BannerAdapter(ArrayList<ImageView> imageViews) {
        this.imageViews = imageViews;
    }

    private ArrayList<ImageView> imageViews;

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        position %= imageViews.size();
        if (position<0){
            position = imageViews.size()+position;
        }
        ImageView view = imageViews.get(position);
        //如果View已经在之前添加到了一个父组件，则必须先remove，否则会抛出IllegalStateException。
        ViewParent vp =view.getParent();
        if (vp!=null){
            ViewGroup parent = (ViewGroup)vp;
            parent.removeView(view);
        }
        container.addView(view);
        //add listeners here if necessary
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //container.removeView(imageViews.get(position % imageViews.size()));
    }
}
