package com.yeepbank.android.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import com.android.volley.VolleyError;
import com.yeepbank.android.Cst;
import com.yeepbank.android.R;
import com.yeepbank.android.activity.user.WebActivity;
import com.yeepbank.android.adapter.BannerAdapter;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.http.BaseRequest;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.model.Banner;
import com.yeepbank.android.request.business.BannerRequest;
import com.yeepbank.android.response.business.BannerResponse;
import com.yeepbank.android.utils.ApiUtils;
import com.yeepbank.android.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by WW on 2015/8/21.
 */
public class GuidePage extends RelativeLayout{

    private Context mContext;
    private ViewPager viewPager;
    private List<ImageView> imageViewList;
    private int currentItem = 500;
    private Bitmap bitmap;


    public GuidePage(Context context, AttributeSet attrs) {
        super(context, attrs,0);
        init(context,attrs);
    }

    public GuidePage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        viewPager = new ViewPager(mContext);
        addView(viewPager);
        imageViewList = new ArrayList<ImageView>();
        loadData();
        try {
            Field field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            Scroller scroller = new Scroller(mContext){
                @Override
                public void startScroll(int startX, int startY, int dx, int dy) {
                    super.startScroll(startX, startY, dx, dy,1000);
                }

                @Override
                public void startScroll(int startX, int startY, int dx, int dy, int duration) {
                    super.startScroll(startX, startY, dx, dy, 1000);
                }
            };
            field.set(viewPager,scroller);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
//                if(imageViewList!= null && imageViewList.size() > 0){
//                    viewPager.setCurrentItem((viewPager.getCurrentItem()+1)%imageViewList.size());
//                }
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        viewPager.setCurrentItem(Integer.MAX_VALUE / 2);

        new Thread(){
            @Override
            public void run() {
                while (true){
                    SystemClock.sleep(3000);
                    cmdHandler.sendEmptyMessage(0);
                }
            }
        }.start();


    }

    private void createImageView() {
        if(Cst.bannerList != null && Cst.bannerList.size() > 0){

            for(int i = 0; i < Cst.bannerList.size(); i++){
                ImageView imageView = new ImageView(mContext);
                imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                imageView.setTag(Cst.bannerList.get(i));
                LoadImage(imageView, Cst.bannerList.get(i).bannerImageUrl);
                imageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((BaseActivity)mContext).gotoTarget(WebActivity.class,R.anim.activity_in_from_right,
                                R.anim.activity_out_from_left,"",v.getTag());
                    }
                });
                imageViewList.add(imageView);
            }
            viewPager.setAdapter(new BannerAdapter((ArrayList<ImageView>) imageViewList));
        }
    }

    private void LoadImage(ImageView img, String path){
       //异步加载图片资源
      AsyncTaskImageLoad async=new AsyncTaskImageLoad(img);
      //执行异步加载，并把图片的路径传送过去
      async.execute(path);
    }

    class AsyncTaskImageLoad extends AsyncTask<String, Integer, Bitmap> {

            private ImageView Image=null;

        public AsyncTaskImageLoad(ImageView img){
            Image=img;
        }
        //运行在子线程中
        protected Bitmap doInBackground(String... params) {
            try{
                String fileName = params[0].substring(params[0].lastIndexOf("?")+1,params[0].length())+".png";
               if(!Utils.getInstances().bannerIsExist(fileName)) {

                   URL url=new URL(params[0]);
                   HttpURLConnection conn=(HttpURLConnection) url.openConnection();
                   conn.setRequestMethod("GET");
                   conn.setConnectTimeout(5000);
                   if(conn.getResponseCode()==200){
                       InputStream input=conn.getInputStream();
                       //if(input.read() > -1){
                           Utils.getInstances().putBanner(input, fileName);
                       //}else {
                          // return null;
                       //}
                   }
               }
                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                bitmapOptions.inSampleSize = 2;
                bitmap = BitmapFactory.decodeStream(Utils.getInstances().getBanner(fileName),null,bitmapOptions);
                return bitmap;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Bitmap result){
            if(Image!=null && result!=null){
                Image.setBackground(new BitmapDrawable(result));
            }
            super.onPostExecute(result);
//            if (result != null && !result.isRecycled())
//                result.recycle();
        }
    }


    private Handler cmdHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
        }
    };


    public void loadData(){
        BannerRequest request = new BannerRequest(mContext, new StringListener() {
            @Override
            public void ResponseListener(String result) {
                BannerResponse response = new BannerResponse();
                if(response.getStatus(result) == 200){
                    Cst.bannerList = response.getObject(result);
                    createView();
                }else {
                    ((BaseActivity)mContext).toast(response.getMessage(result));
                }
            }

            @Override
            public void ErrorListener(VolleyError volleyError) {

            }
        });
        request.stringRequest();
    }

    private void createView(){
        createImageView();

    }


}
