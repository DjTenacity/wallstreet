package com.yeepbank.android.utils;

/**
 * Created by WW on 2015/11/23.
 */

        import android.graphics.Bitmap;

        import android.graphics.BitmapFactory;

        import android.util.LruCache;

/*��ͼƬ���й���Ĺ����ࡣ
06
 * @author Tony
07
 */

public class ImageLoader {

    /*ͼƬ���漼���ĺ����࣬���ڻ����������غõ�ͼƬ���ڳ����ڴ�ﵽ�趨ֵʱ�Ὣ�������ʹ�õ�ͼƬ�Ƴ�����*/

    private static LruCache<String, Bitmap> mMemoryCache;



    /*ImageLoader��ʵ����*/

    private static ImageLoader mImageLoader;

    private ImageLoader() {

        // ��ȡӦ�ó����������ڴ�

        int maxMemory = (int) Runtime.getRuntime().maxMemory();

        int cacheSize = maxMemory / 8;

        // ����ͼƬ�����СΪ�����������ڴ��1/8

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {

            @Override

            protected int sizeOf(String key, Bitmap bitmap) {

                return bitmap.getByteCount();

            }

        };

    }

            /**
             28
             * ��ȡImageLoader��ʵ����
             29
             * @return ImageLoader��ʵ����
            30
             */

    public static ImageLoader getInstance() {

        if (mImageLoader == null) {

            mImageLoader = new ImageLoader();

        }

        return mImageLoader;

    }



            /**
             39
             * ��һ��ͼƬ�洢��LruCache�С�
             40
             * @param key
            41
             *            LruCache�ļ������ﴫ��ͼƬ��URL��ַ��
            42
             * @param bitmap
            43
             *            LruCache�ļ������ﴫ������������ص�Bitmap����
            44
             */

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {

        if (getBitmapFromMemoryCache(key) == null) {
            if(bitmap != null){
                mMemoryCache.put(key, bitmap);
            }


        }

    }


            /**
             52
             * ��LruCache�л�ȡһ��ͼƬ����������ھͷ���null��
             53
             * @param key
            54
             *            LruCache�ļ������ﴫ��ͼƬ��URL��ַ��
            55
             * @return ��Ӧ�������Bitmap���󣬻���null��
            56
             */

    public Bitmap getBitmapFromMemoryCache(String key) {

        return mMemoryCache.get(key);

    }



    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth) {

        // ԴͼƬ�Ŀ��

        final int width = options.outWidth;

        int inSampleSize = 1;

        if (width > reqWidth) {

            // �����ʵ�ʿ�Ⱥ�Ŀ���ȵı���

            final int widthRatio = Math.round((float) width / (float) reqWidth);

            inSampleSize = widthRatio;

        }

        return inSampleSize;

    }



    public static Bitmap decodeSampledBitmapFromResource(String pathName) {

        // ��һ�ν�����inJustDecodeBounds����Ϊtrue������ȡͼƬ��С

        final BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(pathName, options);

        // �������涨��ķ�������inSampleSizeֵ

        //options.inSampleSize = calculateInSampleSize(options, reqWidth);

        // ʹ�û�ȡ����inSampleSizeֵ�ٴν���ͼƬ

        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(pathName, options);
    }
}

