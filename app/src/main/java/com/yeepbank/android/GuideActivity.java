package com.yeepbank.android;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

//import com.fasterxml.jackson.databind.node.NodeCursor;
import com.yeepbank.android.activity.business.HomeActivity;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.utils.LoadDialog;
import com.yeepbank.android.utils.Utils;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by WW on 2015/9/11.
 */
public class GuideActivity extends BaseActivity implements ViewPager.OnPageChangeListener,View.OnClickListener{
    private ViewPager guidePage;
    private List<View> imageViewList;
    private LinearLayout docsLay;
    private int[] resources = new int[]{R.drawable.step1,R.drawable.step2,R.drawable.step3};
    @Override
    protected void initView() {
        guidePage = (ViewPager) findViewById(R.id.guide_page);
        imageViewList = new ArrayList<View>();
        docsLay = (LinearLayout) findViewById(R.id.docs);
    }

    @Override
    protected void fillData() {

        for(int i = 0; i < resources.length; i++){
            View view = LayoutInflater.from(mContext).inflate(R.layout.step_item,null);
            view.setBackgroundResource(resources[i]);
            imageViewList.add(view);
        }
        Log.e("guideActivity","+++++++guideActivity");
        initDoc();

        guidePage.setAdapter(new PagerAdapter() {


            @Override
            public int getCount() {
                return imageViewList.size();
            }

//            @Override
//            public boolean isViewFromObject(View view, NodeCursor.Object o) {
//                return view == o;
//            }
//
//            @Override
//            public void destroyItem(ViewGroup container, int position, NodeCursor.Object object) {
//                container.removeView(imageViewList.get(position));
//
//            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(imageViewList.get(position));
                return imageViewList.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(imageViewList.get(position));
//                super.destroyItem(container, position, object);
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return false;
            }
        });

        guidePage.setOnPageChangeListener(this);
        View last = imageViewList.get(imageViewList.size() - 1);
        Button entranceBtn = (Button) last.findViewById(R.id.entrance_btn);
        entranceBtn.setVisibility(View.VISIBLE);
        entranceBtn.setOnClickListener(this);
        Log.e("guideActivity","+++++++entranceButton");
    }

    private void initDoc() {

        for(int i = 0; i < imageViewList.size(); i++){
            ImageView imageView = new ImageView(mContext);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ViewGroup.MarginLayoutParams marginLayoutParams = params;
            marginLayoutParams.setMargins(0,0,30,0);
            imageView.setLayoutParams(marginLayoutParams);
            imageView.setImageResource(R.drawable.dot_normal);
            docsLay.addView(imageView);
        }
        if(docsLay.getChildAt(0)!=null){
            ((ImageView)docsLay.getChildAt(0)).setImageResource(R.drawable.dot_selected);
        }
    }

    @Override
    protected int getLayoutResources() {
        return R.layout.guid_page;
    }

    @Override
    protected View getNavigationBar() {
        return null;
    }

    @Override
    protected void initNavigationBar(View navigationBar) {

    }

    @Override
    public LoadDialog getLoadDialog() {
        return null;
    }


    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        for(int childIndex = 0; childIndex < docsLay.getChildCount(); childIndex++){
            ImageView child = (ImageView) docsLay.getChildAt(childIndex);
            child.setImageResource(R.drawable.dot_normal);
        }
        ((ImageView)docsLay.getChildAt(i)).setImageResource(R.drawable.dot_selected);
        Log.e("guideActivity","+++++++selected");
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public void onClick(View v) {
        Utils.getInstances().setUsed(mContext);
        gotoTargetRemovePre(HomeActivity.class, R.anim.activity_in_from_left, R.anim.activity_out_from_right, "");
    }
}
