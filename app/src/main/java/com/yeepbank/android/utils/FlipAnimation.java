package com.yeepbank.android.utils;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by WW on 2015/9/10.
 */
public class FlipAnimation extends Animation {

    private float mFromDegrees;// 开始角度
    private float mToDegrees;// 结束角度
    // 中心点
    private float mCenterX;
    private float mCenterY;
    //深度
    private float mDepthZ;
    //是否需要扭曲
    private boolean mReverse;
    //摄像头
    private Camera mCamera;

    public FlipAnimation(float mFromDegrees, float mToDegrees, float mCenterX, float mCenterY, float mDepthZ, boolean mReverse) {
        this.mFromDegrees = mFromDegrees;
        this.mToDegrees = mToDegrees;
        this.mCenterX = mCenterX;
        this.mCenterY = mCenterY;
        this.mDepthZ = mDepthZ;
        this.mReverse = mReverse;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        float degrees = mFromDegrees + (mToDegrees - mFromDegrees) * interpolatedTime;
        Matrix matrix = t.getMatrix();
        mCamera.save();
        if (mReverse) {
            mCamera.translate(0.0f, 0.0f, mDepthZ * interpolatedTime);
        } else {
            mCamera.translate(0.0f, 0.0f, mDepthZ * (1.0f - interpolatedTime));
        }
        mCamera.rotateY(degrees);//翻转
        mCamera.getMatrix(matrix);// 取得变换后的矩阵
        mCamera.restore();
        matrix.preTranslate(-mCenterX, -mCenterY);
        matrix.postTranslate(mCenterX, mCenterY);
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        mCamera = new Camera();
    }
}
